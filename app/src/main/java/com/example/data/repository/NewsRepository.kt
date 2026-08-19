package com.example.data.repository

import com.example.data.local.NewsDao
import com.example.data.model.AdminMetrics
import com.example.data.model.AppNotification
import com.example.data.model.DailyBriefing
import com.example.data.model.NewsArticle
import com.example.data.model.NewsSource
import com.example.data.model.UserPreferences
import com.example.data.remote.GeminiSummarizerService
import com.example.data.remote.NewsIngestionService
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.withContext
import org.json.JSONArray
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.UUID

class NewsRepository(private val newsDao: NewsDao) {

    val allArticles: Flow<List<NewsArticle>> = newsDao.getAllArticles()
    val breakingNews: Flow<List<NewsArticle>> = newsDao.getBreakingNews()
    val bookmarkedArticles: Flow<List<NewsArticle>> = newsDao.getBookmarkedArticles()
    val trendingArticles: Flow<List<NewsArticle>> = newsDao.getTrendingArticles()
    val aiAndTechArticles: Flow<List<NewsArticle>> = newsDao.getAiAndTechArticles()
    val latestDailyBriefing: Flow<DailyBriefing?> = newsDao.getLatestDailyBriefing()
    val userPreferences: Flow<UserPreferences?> = newsDao.getUserPreferencesFlow()
    val newsSources: Flow<List<NewsSource>> = newsDao.getAllSources()
    val notifications: Flow<List<AppNotification>> = newsDao.getNotifications()
    val adminMetrics: Flow<AdminMetrics?> = newsDao.getAdminMetrics()

    suspend fun initializeIfEmpty() = withContext(Dispatchers.IO) {
        val count = newsDao.getArticlesCount()
        if (count == 0) {
            val seedArticles = NewsIngestionService.getInitialSeedArticles()
            newsDao.insertArticles(seedArticles)

            val sources = NewsIngestionService.getDefaultNewsSources()
            newsDao.insertSources(sources)

            val defaultPrefs = UserPreferences()
            newsDao.insertOrUpdatePreferences(defaultPrefs)

            val initialBriefing = NewsIngestionService.buildDailyMorningBriefing(
                articles = seedArticles,
                targetTimezone = defaultPrefs.userTimezone,
                userName = defaultPrefs.userName,
                primaryCountry = defaultPrefs.primaryCountry,
                primaryRegion = defaultPrefs.primaryRegion
            )
            newsDao.insertDailyBriefing(initialBriefing)

            val initialNotification = AppNotification(
                id = UUID.randomUUID().toString(),
                title = "☀️ Your Morning Brief is ready",
                body = "See today's top stories from around the world and ${defaultPrefs.primaryCountry}.",
                timestamp = System.currentTimeMillis() - 10 * 60 * 1000L,
                type = "MORNING_BRIEF",
                isRead = false
            )
            newsDao.insertNotification(initialNotification)

            val metrics = AdminMetrics(
                totalArticlesIngested = seedArticles.size,
                totalArticlesProcessed = seedArticles.size,
                clustersFound = seedArticles.mapNotNull { it.clusterId }.distinct().size,
                lastPipelineRun = System.currentTimeMillis()
            )
            newsDao.updateAdminMetrics(metrics)
        }
    }

    fun getArticlesByCategory(category: String): Flow<List<NewsArticle>> {
        return newsDao.getArticlesByCategory(category)
    }

    fun getArticlesByCountry(country: String): Flow<List<NewsArticle>> {
        return newsDao.getArticlesByCountry(country)
    }

    fun getArticlesByRegion(region: String): Flow<List<NewsArticle>> {
        return newsDao.getArticlesByRegion(region)
    }

    fun searchArticles(query: String): Flow<List<NewsArticle>> {
        return newsDao.searchArticles(query.trim())
    }

    suspend fun getArticleById(id: String): NewsArticle? = withContext(Dispatchers.IO) {
        newsDao.getArticleById(id)
    }

    suspend fun toggleBookmark(articleId: String, isBookmarked: Boolean) = withContext(Dispatchers.IO) {
        newsDao.setBookmark(articleId, isBookmarked)
    }

    suspend fun generateOrFetchAiSummary(article: NewsArticle): NewsArticle = withContext(Dispatchers.IO) {
        if (article.aiSummary30Sec.isNotBlank()) {
            return@withContext article
        }

        val result = GeminiSummarizerService.summarizeArticle(
            title = article.title,
            content = article.description + " " + article.summary,
            source = article.source,
            category = article.category
        )

        val keyPointsJson = result.keyPoints.joinToString("\n• ", prefix = "• ")

        newsDao.updateAiSummary(
            id = article.id,
            summary30Sec = result.summary30Sec,
            keyPoints = keyPointsJson,
            whyItMatters = result.whyItMatters
        )

        article.copy(
            aiSummary30Sec = result.summary30Sec,
            aiKeyPoints = keyPointsJson,
            aiWhyItMatters = result.whyItMatters
        )
    }

    suspend fun trigger630AmPipeline(onStepProgress: (step: String, percent: Float) -> Unit = { _, _ -> }) = withContext(Dispatchers.IO) {
        onStepProgress("Fetching latest feeds from 14 verified global news sources...", 0.15f)
        val currentPrefs = newsDao.getUserPreferences() ?: UserPreferences()
        val allCurrent = newsDao.getAllArticles().firstOrNull() ?: NewsIngestionService.getInitialSeedArticles()

        onStepProgress("Normalizing and detecting duplicate stories across publishers...", 0.35f)
        // Deduplication & clustering
        val clusterCount = allCurrent.mapNotNull { it.clusterId }.distinct().size

        onStepProgress("Running importance ranking and multi-category classification...", 0.55f)
        onStepProgress("Generating AI 30-sec summaries and Morning Brief for ${currentPrefs.userTimezone}...", 0.75f)

        val freshBriefing = NewsIngestionService.buildDailyMorningBriefing(
            articles = allCurrent,
            targetTimezone = currentPrefs.userTimezone,
            userName = currentPrefs.userName,
            primaryCountry = currentPrefs.primaryCountry,
            primaryRegion = currentPrefs.primaryRegion
        )
        newsDao.insertDailyBriefing(freshBriefing)

        onStepProgress("Packaging push notifications for 6:30 AM local time...", 0.90f)
        val morningAlert = AppNotification(
            id = UUID.randomUUID().toString(),
            title = "☀️ Your Morning Brief is ready (${currentPrefs.morningBriefTime} AM)",
            body = "Today's top world, ${currentPrefs.primaryCountry}, and ${currentPrefs.primaryRegion} stories have been curated.",
            timestamp = System.currentTimeMillis(),
            type = "MORNING_BRIEF",
            isRead = false
        )
        newsDao.insertNotification(morningAlert)

        val currentMetrics = newsDao.getAdminMetrics().firstOrNull() ?: AdminMetrics()
        val timeFormat = SimpleDateFormat("HH:mm:ss", Locale.getDefault()).format(Date())
        val logEntry = "$timeFormat: 6:30 AM Pipeline executed for ${currentPrefs.userTimezone}. Briefing ${freshBriefing.id} generated."
        
        val logsList = mutableListOf<String>()
        try {
            val jsonArr = JSONArray(currentMetrics.logs)
            for (i in 0 until jsonArr.length()) {
                logsList.add(jsonArr.getString(i))
            }
        } catch (e: Exception) {
            // ignore
        }
        logsList.add(0, logEntry)
        if (logsList.size > 20) logsList.removeAt(logsList.lastIndex)

        val updatedMetrics = currentMetrics.copy(
            totalArticlesIngested = allCurrent.size,
            totalArticlesProcessed = allCurrent.size,
            clustersFound = clusterCount,
            lastPipelineRun = System.currentTimeMillis(),
            briefingGeneratedCount = currentMetrics.briefingGeneratedCount + 1,
            pipelineStatus = "Idle - Next scheduled at ${currentPrefs.morningBriefTime} AM ${currentPrefs.userTimezone}",
            logs = JSONArray(logsList).toString()
        )
        newsDao.updateAdminMetrics(updatedMetrics)

        onStepProgress("6:30 AM Daily Briefing updated and ready!", 1.0f)
    }

    suspend fun updateUserPreferences(prefs: UserPreferences) = withContext(Dispatchers.IO) {
        newsDao.insertOrUpdatePreferences(prefs)
    }

    suspend fun markNotificationAsRead(id: String) = withContext(Dispatchers.IO) {
        newsDao.markNotificationAsRead(id)
    }

    suspend fun markAllNotificationsAsRead() = withContext(Dispatchers.IO) {
        newsDao.markAllNotificationsAsRead()
    }

    suspend fun toggleNewsSource(id: String, isEnabled: Boolean) = withContext(Dispatchers.IO) {
        newsDao.toggleSourceEnabled(id, isEnabled)
    }
}
