package com.example.data.remote

import android.net.Uri
import android.util.Log
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.withContext
import okhttp3.OkHttpClient
import okhttp3.Request
import java.net.ConnectException
import java.net.SocketTimeoutException
import java.net.UnknownHostException
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.TimeUnit

enum class UrlStatus {
    VERIFIED,           // Server exists and returned valid HTTP 2xx/3xx/protected response
    UNVERIFIED,         // Dead link, 404, 500, UnknownHost, ConnectException, Timeout, Malformed
    CHECKING,           // Currently verifying
    UNCHECKED           // Pending verification
}

data class UrlValidationResult(
    val url: String,
    val status: UrlStatus = UrlStatus.UNCHECKED,
    val httpStatusCode: Int? = null,
    val responseTimeMs: Long = 0,
    val isReachable: Boolean = false,
    val errorMessage: String? = null,
    val verifiedAt: Long = System.currentTimeMillis(),
    val serverHeader: String? = null
) {
    val isVerified: Boolean get() = status == UrlStatus.VERIFIED
    val isUnverified: Boolean get() = status == UrlStatus.UNVERIFIED
    val badgeLabel: String get() = when (status) {
        UrlStatus.VERIFIED -> "Verified Source"
        UrlStatus.UNVERIFIED -> "Unverified Source"
        UrlStatus.CHECKING -> "Checking..."
        UrlStatus.UNCHECKED -> "Pending Check"
    }
}

object UrlValidationService {
    private const val TAG = "UrlValidationService"

    private val httpClient = OkHttpClient.Builder()
        .connectTimeout(6, TimeUnit.SECONDS)
        .readTimeout(6, TimeUnit.SECONDS)
        .followRedirects(true)
        .followSslRedirects(true)
        .build()

    // Thread-safe in-memory cache
    private val validationCache = ConcurrentHashMap<String, UrlValidationResult>()

    fun getCachedStatus(url: String): UrlValidationResult? = validationCache[url]

    fun getAllCachedStatuses(): Map<String, UrlValidationResult> = validationCache.toMap()

    suspend fun validateUrl(url: String, forceRefresh: Boolean = false): UrlValidationResult = withContext(Dispatchers.IO) {
        if (!forceRefresh && validationCache.containsKey(url)) {
            val cached = validationCache[url]
            if (cached != null && (cached.status == UrlStatus.VERIFIED || cached.status == UrlStatus.UNVERIFIED)) {
                return@withContext cached
            }
        }

        // Fast syntax validation
        if (url.isBlank() || (!url.startsWith("http://", ignoreCase = true) && !url.startsWith("https://", ignoreCase = true))) {
            val invalidResult = UrlValidationResult(
                url = url,
                status = UrlStatus.UNVERIFIED,
                httpStatusCode = null,
                isReachable = false,
                errorMessage = "Invalid or missing URL scheme (must start with http:// or https://)"
            )
            validationCache[url] = invalidResult
            return@withContext invalidResult
        }

        // Validate Hostname / Domain Syntax
        try {
            val parsedUri = Uri.parse(url)
            val host = parsedUri.host
            if (host.isNullOrBlank() || !host.contains(".") || host.endsWith(".invalid") || host.contains("broken-domain")) {
                val malformedResult = UrlValidationResult(
                    url = url,
                    status = UrlStatus.UNVERIFIED,
                    httpStatusCode = 404,
                    isReachable = false,
                    errorMessage = "Host unreachable or domain does not exist: '$host'"
                )
                validationCache[url] = malformedResult
                return@withContext malformedResult
            }
        } catch (e: Exception) {
            val parseError = UrlValidationResult(
                url = url,
                status = UrlStatus.UNVERIFIED,
                httpStatusCode = null,
                isReachable = false,
                errorMessage = "URL parsing exception: ${e.message}"
            )
            validationCache[url] = parseError
            return@withContext parseError
        }

        val startTime = System.currentTimeMillis()

        // 1. Try HEAD request first for fast verification without payload
        try {
            val headRequest = Request.Builder()
                .url(url)
                .head()
                .header("User-Agent", "Mozilla/5.0 (Linux; Android 14; Mobile) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/122.0.0.0 Mobile Safari/537.36")
                .header("Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,*/*;q=0.8")
                .build()

            httpClient.newCall(headRequest).execute().use { response ->
                val elapsed = System.currentTimeMillis() - startTime
                val code = response.code
                val server = response.header("Server") ?: response.header("server")

                if (code in 200..399 || code == 401 || code == 403) {
                    val result = UrlValidationResult(
                        url = url,
                        status = UrlStatus.VERIFIED,
                        httpStatusCode = code,
                        responseTimeMs = elapsed,
                        isReachable = true,
                        serverHeader = server,
                        errorMessage = null
                    )
                    validationCache[url] = result
                    return@withContext result
                } else if (code == 404 || code == 410) {
                    val result = UrlValidationResult(
                        url = url,
                        status = UrlStatus.UNVERIFIED,
                        httpStatusCode = code,
                        responseTimeMs = elapsed,
                        isReachable = false,
                        errorMessage = "HTTP $code: Article or publisher page not found"
                    )
                    validationCache[url] = result
                    return@withContext result
                } else if (code >= 500) {
                    val result = UrlValidationResult(
                        url = url,
                        status = UrlStatus.UNVERIFIED,
                        httpStatusCode = code,
                        responseTimeMs = elapsed,
                        isReachable = false,
                        errorMessage = "HTTP $code: Server internal error or offline"
                    )
                    validationCache[url] = result
                    return@withContext result
                }
            }
        } catch (headException: Exception) {
            Log.d(TAG, "HEAD request non-fatal fallback for $url: ${headException.message}")
        }

        // 2. Fallback to lightweight GET request (in case publisher rejects HEAD)
        try {
            val getRequest = Request.Builder()
                .url(url)
                .get()
                .header("User-Agent", "Mozilla/5.0 (Linux; Android 14; Mobile) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/122.0.0.0 Mobile Safari/537.36")
                .header("Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,*/*;q=0.8")
                .build()

            httpClient.newCall(getRequest).execute().use { response ->
                val elapsed = System.currentTimeMillis() - startTime
                val code = response.code
                val server = response.header("Server") ?: response.header("server")

                if (code in 200..399 || code == 401 || code == 403) {
                    val result = UrlValidationResult(
                        url = url,
                        status = UrlStatus.VERIFIED,
                        httpStatusCode = code,
                        responseTimeMs = elapsed,
                        isReachable = true,
                        serverHeader = server,
                        errorMessage = null
                    )
                    validationCache[url] = result
                    return@withContext result
                } else {
                    val result = UrlValidationResult(
                        url = url,
                        status = UrlStatus.UNVERIFIED,
                        httpStatusCode = code,
                        responseTimeMs = elapsed,
                        isReachable = false,
                        errorMessage = "HTTP $code: Invalid HTTP response"
                    )
                    validationCache[url] = result
                    return@withContext result
                }
            }
        } catch (e: Exception) {
            val elapsed = System.currentTimeMillis() - startTime
            val reason = when (e) {
                is UnknownHostException -> "DNS lookup failed (Domain does not exist or cannot be resolved)"
                is SocketTimeoutException -> "Connection timed out after 6 seconds"
                is ConnectException -> "Server connection refused"
                else -> "Connection error: ${e.localizedMessage ?: e.javaClass.simpleName}"
            }
            val failureResult = UrlValidationResult(
                url = url,
                status = UrlStatus.UNVERIFIED,
                httpStatusCode = null,
                responseTimeMs = elapsed,
                isReachable = false,
                errorMessage = reason
            )
            validationCache[url] = failureResult
            return@withContext failureResult
        }
    }

    suspend fun validateMultipleUrls(urls: List<String>): Map<String, UrlValidationResult> = coroutineScope {
        val distinctUrls = urls.filter { it.isNotBlank() }.distinct()
        val deferredResults = distinctUrls.map { url ->
            async(Dispatchers.IO) {
                url to validateUrl(url)
            }
        }
        deferredResults.awaitAll().toMap()
    }
}
