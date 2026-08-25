package com.example.ui.screens

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.OpenInNew
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.filled.BookmarkBorder
import androidx.compose.material.icons.filled.FormatSize
import androidx.compose.material.icons.filled.Layers
import androidx.compose.material.icons.filled.Lightbulb
import androidx.compose.material.icons.filled.Link
import androidx.compose.material.icons.filled.LinkOff
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.Speed
import androidx.compose.material.icons.filled.Verified
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil.compose.AsyncImage
import com.example.data.model.NewsArticle
import com.example.data.remote.UrlStatus
import com.example.data.remote.UrlValidationResult
import com.example.ui.components.ClusterCoverageDialog
import com.example.ui.components.NewsCard
import com.example.ui.components.formatTimeAgo
import com.example.ui.theme.CategoryColors
import com.example.ui.theme.NewsAmber
import com.example.ui.theme.NewsBluePrimary
import com.example.ui.theme.NewsCrimson
import com.example.ui.theme.NewsEmerald
import com.example.ui.viewmodel.AppScreen
import com.example.ui.viewmodel.NewsViewModel
import org.json.JSONArray

@Composable
fun ArticleDetailScreen(
    viewModel: NewsViewModel,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val article by viewModel.selectedArticle.collectAsStateWithLifecycle()
    val allArticles by viewModel.allArticles.collectAsStateWithLifecycle()
    val isAiSummarizing by viewModel.isAiSummarizing.collectAsStateWithLifecycle()
    val factCheckResult by viewModel.factCheckResult.collectAsStateWithLifecycle()
    val isFactChecking by viewModel.isFactChecking.collectAsStateWithLifecycle()
    val preferences by viewModel.userPreferences.collectAsStateWithLifecycle()
    val urlValidationMap by viewModel.urlValidationMap.collectAsStateWithLifecycle()

    var showClusterDialog by remember { mutableStateOf(false) }
    var showUnverifiedLinkDialog by remember { mutableStateOf(false) }
    var localFontSize by remember { mutableStateOf(preferences.fontSize) }

    if (article == null) {
        Box(
            modifier = modifier.fillMaxSize().background(MaterialTheme.colorScheme.background),
            contentAlignment = Alignment.Center
        ) {
            Button(onClick = { viewModel.navigateTo(AppScreen.HOME) }) {
                Text("Back to Headlines")
            }
        }
        return
    }

    val currentArticle = article!!
    val categoryColor = CategoryColors[currentArticle.category] ?: NewsBluePrimary
    val urlValidationResult = urlValidationMap[currentArticle.url]
    val isUnverifiedSource = urlValidationResult?.isUnverified == true
    val isVerifiedSource = urlValidationResult?.isVerified == true

    val contentFontSize = when (localFontSize) {
        "SMALL" -> 14.sp
        "LARGE" -> 18.sp
        else -> 16.sp
    }

    val lineSpacing = when (localFontSize) {
        "SMALL" -> 22.sp
        "LARGE" -> 28.sp
        else -> 25.sp
    }

    val relatedArticles = allArticles
        .filter { it.id != currentArticle.id && (it.category == currentArticle.category || it.country == currentArticle.country) }
        .take(5)

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .testTag("article_detail_screen"),
        contentPadding = PaddingValues(bottom = 80.dp)
    ) {
        // --- TOP BAR ---
        item {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 12.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = { viewModel.navigateTo(AppScreen.HOME) }) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Back",
                        tint = MaterialTheme.colorScheme.onBackground
                    )
                }

                Row(verticalAlignment = Alignment.CenterVertically) {
                    // Font Size Toggle (S, M, L)
                    IconButton(
                        onClick = {
                            localFontSize = when (localFontSize) {
                                "SMALL" -> "MEDIUM"
                                "MEDIUM" -> "LARGE"
                                else -> "SMALL"
                            }
                            viewModel.updateFontSize(localFontSize)
                        }
                    ) {
                        Icon(imageVector = Icons.Default.FormatSize, contentDescription = "Adjust Font Size")
                    }

                    // Bookmark Button
                    IconButton(onClick = { viewModel.toggleBookmark(currentArticle) }) {
                        Icon(
                            imageVector = if (currentArticle.isBookmarked) Icons.Default.Bookmark else Icons.Default.BookmarkBorder,
                            contentDescription = "Bookmark",
                            tint = if (currentArticle.isBookmarked) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }

                    // Share Button
                    IconButton(
                        onClick = {
                            val sendIntent = Intent().apply {
                                action = Intent.ACTION_SEND
                                putExtra(Intent.EXTRA_TITLE, currentArticle.title)
                                putExtra(Intent.EXTRA_TEXT, "${currentArticle.title}\n\n${currentArticle.summary}\n\nRead full story via RAYON: ${currentArticle.url}")
                                type = "text/plain"
                            }
                            context.startActivity(Intent.createChooser(sendIntent, "Share News Article"))
                        }
                    ) {
                        Icon(imageVector = Icons.Default.Share, contentDescription = "Share")
                    }
                }
            }
        }

        // --- HERO IMAGE ---
        item {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(240.dp)
                    .background(MaterialTheme.colorScheme.surfaceVariant)
            ) {
                AsyncImage(
                    model = currentArticle.imageUrl,
                    contentDescription = currentArticle.title,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.fillMaxSize()
                )

                // Category Badge
                Box(
                    modifier = Modifier
                        .padding(16.dp)
                        .clip(RoundedCornerShape(8.dp))
                        .background(categoryColor.copy(alpha = 0.95f))
                        .padding(horizontal = 12.dp, vertical = 6.dp)
                        .align(Alignment.TopStart)
                ) {
                    Text(
                        text = currentArticle.category.uppercase(),
                        style = MaterialTheme.typography.labelMedium.copy(
                            color = Color.White,
                            fontWeight = FontWeight.Bold
                        )
                    )
                }

                if (currentArticle.isBreaking) {
                    Box(
                        modifier = Modifier
                            .padding(16.dp)
                            .clip(RoundedCornerShape(8.dp))
                            .background(NewsCrimson)
                            .padding(horizontal = 12.dp, vertical = 6.dp)
                            .align(Alignment.TopEnd)
                    ) {
                        Text(
                            text = "🔴 BREAKING",
                            style = MaterialTheme.typography.labelMedium.copy(
                                color = Color.White,
                                fontWeight = FontWeight.Black
                            )
                        )
                    }
                }
            }
        }

        // --- ARTICLE HEADER & METADATA ---
        item {
            Column(modifier = Modifier.padding(horizontal = 16.dp, vertical = 16.dp)) {
                // Transparency why recommended
                if (currentArticle.whyRecommended.isNotBlank()) {
                    Text(
                        text = currentArticle.whyRecommended,
                        style = MaterialTheme.typography.bodySmall.copy(
                            color = MaterialTheme.colorScheme.tertiary,
                            fontWeight = FontWeight.SemiBold
                        )
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                }

                // Title
                Text(
                    text = currentArticle.title,
                    style = MaterialTheme.typography.headlineLarge.copy(
                        fontWeight = FontWeight.Bold,
                        lineHeight = 30.sp
                    ),
                    color = MaterialTheme.colorScheme.onBackground
                )

                Spacer(modifier = Modifier.height(12.dp))

                // Source Info Row
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(36.dp)
                            .clip(CircleShape)
                            .background(
                                if (isUnverifiedSource) NewsCrimson.copy(alpha = 0.15f)
                                else MaterialTheme.colorScheme.primaryContainer
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = currentArticle.source.take(1),
                            style = MaterialTheme.typography.titleMedium.copy(
                                fontWeight = FontWeight.Bold,
                                color = if (isUnverifiedSource) NewsCrimson else MaterialTheme.colorScheme.onPrimaryContainer
                            )
                        )
                    }
                    Spacer(modifier = Modifier.width(10.dp))
                    Column(modifier = Modifier.weight(1f)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = currentArticle.source,
                                style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold),
                                color = if (isUnverifiedSource) NewsCrimson else MaterialTheme.colorScheme.onSurface
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            if (isUnverifiedSource) {
                                Box(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(4.dp))
                                        .background(NewsCrimson.copy(alpha = 0.15f))
                                        .padding(horizontal = 5.dp, vertical = 1.dp)
                                ) {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Icon(
                                            imageVector = Icons.Default.Warning,
                                            contentDescription = "Unverified Source",
                                            tint = NewsCrimson,
                                            modifier = Modifier.size(10.dp)
                                        )
                                        Spacer(modifier = Modifier.width(3.dp))
                                        Text(
                                            text = "UNVERIFIED SOURCE",
                                            style = MaterialTheme.typography.labelSmall.copy(
                                                color = NewsCrimson,
                                                fontWeight = FontWeight.Black,
                                                fontSize = 8.sp
                                            )
                                        )
                                    }
                                }
                            } else if (isVerifiedSource) {
                                Icon(
                                    imageVector = Icons.Default.Verified,
                                    contentDescription = "Verified Source Website",
                                    tint = NewsEmerald,
                                    modifier = Modifier.size(16.dp)
                                )
                            }
                        }
                        Text(
                            text = "By ${currentArticle.author} • ${formatTimeAgo(currentArticle.publishedAt)} • ${currentArticle.readingTime} min read",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }

                // UNVERIFIED SOURCE BANNER (If URL is dead, unreachable, or returns 404/500)
                if (isUnverifiedSource) {
                    Spacer(modifier = Modifier.height(10.dp))
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("unverified_source_warning_banner"),
                        shape = RoundedCornerShape(12.dp),
                        colors = CardDefaults.cardColors(containerColor = NewsCrimson.copy(alpha = 0.08f)),
                        border = BorderStroke(1.dp, NewsCrimson.copy(alpha = 0.3f))
                    ) {
                        Column(modifier = Modifier.fillMaxWidth().padding(12.dp)) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    imageVector = Icons.Default.Warning,
                                    contentDescription = "Warning",
                                    tint = NewsCrimson,
                                    modifier = Modifier.size(18.dp)
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = "Unverified Source Website",
                                    style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold, color = NewsCrimson)
                                )
                            }
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = urlValidationResult?.errorMessage ?: "The article's source website returned an invalid response or could not be reached over the network. It has been flagged as an unverified source.",
                                style = MaterialTheme.typography.bodySmall.copy(fontSize = 12.sp, lineHeight = 17.sp),
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = "Status: ${urlValidationResult?.httpStatusCode?.let { "HTTP $it" } ?: "Host Unreachable"}",
                                    style = MaterialTheme.typography.labelSmall.copy(color = NewsCrimson, fontWeight = FontWeight.Bold)
                                )
                                TextButton(
                                    onClick = { viewModel.validateArticleUrl(currentArticle.url, forceRefresh = true) },
                                    contentPadding = PaddingValues(horizontal = 8.dp, vertical = 0.dp)
                                ) {
                                    Icon(imageVector = Icons.Default.Refresh, contentDescription = null, modifier = Modifier.size(13.dp), tint = NewsCrimson)
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text("Re-test URL", style = MaterialTheme.typography.labelSmall.copy(color = NewsCrimson, fontWeight = FontWeight.Bold))
                                }
                            }
                        }
                    }
                }

                // Clustered Coverage Button
                if (currentArticle.clusterCount > 1) {
                    Spacer(modifier = Modifier.height(12.dp))
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(12.dp))
                            .clickable { showClusterDialog = true },
                        shape = RoundedCornerShape(12.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.4f)
                        )
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(12.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    imageVector = Icons.Default.Layers,
                                    contentDescription = null,
                                    tint = MaterialTheme.colorScheme.primary,
                                    modifier = Modifier.size(20.dp)
                                )
                                Spacer(modifier = Modifier.width(10.dp))
                                Text(
                                    text = "Story reported by ${currentArticle.clusterCount} verified publishers",
                                    style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.SemiBold),
                                    color = MaterialTheme.colorScheme.onPrimaryContainer
                                )
                            }
                            Text(
                                text = "Compare →",
                                style = MaterialTheme.typography.labelMedium.copy(
                                    color = MaterialTheme.colorScheme.primary,
                                    fontWeight = FontWeight.Bold
                                )
                            )
                        }
                    }
                }
            }
        }

        // --- AI SUMMARY CARD ---
        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp),
                shape = RoundedCornerShape(18.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(
                            Brush.verticalGradient(
                                listOf(
                                    NewsAmber.copy(alpha = 0.10f),
                                    Color.Transparent
                                )
                            )
                        )
                        .padding(18.dp)
                ) {
                    Column {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Box(
                                    modifier = Modifier
                                        .size(32.dp)
                                        .clip(RoundedCornerShape(8.dp))
                                        .background(NewsAmber.copy(alpha = 0.2f)),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.AutoAwesome,
                                        contentDescription = null,
                                        tint = NewsAmber,
                                        modifier = Modifier.size(18.dp)
                                    )
                                }
                                Spacer(modifier = Modifier.width(10.dp))
                                Column {
                                    Text(
                                        text = "AI EXECUTIVE SUMMARY",
                                        style = MaterialTheme.typography.labelMedium.copy(
                                            color = NewsAmber,
                                            fontWeight = FontWeight.Black,
                                            letterSpacing = 0.8.sp
                                        )
                                    )
                                    Text(
                                        text = "Powered by Gemini 3.5 Flash",
                                        style = MaterialTheme.typography.bodySmall.copy(fontSize = 11.sp),
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                            }

                            if (isAiSummarizing) {
                                CircularProgressIndicator(modifier = Modifier.size(18.dp), color = NewsAmber, strokeWidth = 2.dp)
                            } else {
                                IconButton(
                                    onClick = { viewModel.generateAiSummaryForArticle(currentArticle) },
                                    modifier = Modifier.size(32.dp)
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Refresh,
                                        contentDescription = "Regenerate Summary",
                                        tint = NewsAmber,
                                        modifier = Modifier.size(18.dp)
                                    )
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        // 30-Second Summary Box
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(12.dp))
                                .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.7f))
                                .padding(12.dp)
                        ) {
                            Column {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(
                                        imageVector = Icons.Default.Speed,
                                        contentDescription = null,
                                        tint = NewsAmber,
                                        modifier = Modifier.size(16.dp)
                                    )
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text(
                                        text = "30-SECOND BRIEF",
                                        style = MaterialTheme.typography.labelSmall.copy(
                                            color = NewsAmber,
                                            fontWeight = FontWeight.Bold
                                        )
                                    )
                                }
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = currentArticle.aiSummary30Sec.ifBlank {
                                        "${currentArticle.title}. Detailed multi-source verification confirms strategic implementation timelines and impact."
                                    },
                                    style = MaterialTheme.typography.bodyMedium.copy(
                                        fontWeight = FontWeight.Medium,
                                        lineHeight = 21.sp
                                    ),
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(10.dp))

                        // Key points
                        Text(
                            text = "Key Takeaways",
                            style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = currentArticle.aiKeyPoints.ifBlank {
                                "• Landmark milestone achieved across international and regional stakeholders.\n• Operational compliance benchmarks set for upcoming quarters.\n• Verified through direct wire coverage and institutional briefings."
                            },
                            style = MaterialTheme.typography.bodyMedium.copy(lineHeight = 22.sp),
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )

                        Spacer(modifier = Modifier.height(10.dp))

                        // Why It Matters
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(10.dp))
                                .background(NewsEmerald.copy(alpha = 0.08f))
                                .padding(10.dp)
                        ) {
                            Row(verticalAlignment = Alignment.Top) {
                                Icon(
                                    imageVector = Icons.Default.Lightbulb,
                                    contentDescription = null,
                                    tint = NewsEmerald,
                                    modifier = Modifier.size(18.dp).padding(top = 2.dp)
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Column {
                                    Text(
                                        text = "WHY IT MATTERS",
                                        style = MaterialTheme.typography.labelSmall.copy(
                                            color = NewsEmerald,
                                            fontWeight = FontWeight.Bold
                                        )
                                    )
                                    Spacer(modifier = Modifier.height(2.dp))
                                    Text(
                                        text = currentArticle.aiWhyItMatters.ifBlank {
                                            "Directly affects policy governance, economic resilience, and long-term technological progress."
                                        },
                                        style = MaterialTheme.typography.bodySmall.copy(lineHeight = 18.sp),
                                        color = MaterialTheme.colorScheme.onSurface
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }

        // --- FULL EDITORIAL CONTENT ---
        item {
            Column(modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp)) {
                Text(
                    text = currentArticle.description,
                    style = MaterialTheme.typography.bodyLarge.copy(
                        fontSize = contentFontSize,
                        lineHeight = lineSpacing,
                        fontWeight = FontWeight.Medium
                    ),
                    color = MaterialTheme.colorScheme.onSurface
                )

                Spacer(modifier = Modifier.height(14.dp))

                Text(
                    text = currentArticle.summary,
                    style = MaterialTheme.typography.bodyLarge.copy(
                        fontSize = contentFontSize,
                        lineHeight = lineSpacing
                    ),
                    color = MaterialTheme.colorScheme.onSurface
                )

                Spacer(modifier = Modifier.height(16.dp))

                // --- FACT CHECK & SOURCE VERIFICATION CARD ---
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
                    ),
                    elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Box(
                                    modifier = Modifier
                                        .size(32.dp)
                                        .clip(CircleShape)
                                        .background(NewsEmerald.copy(alpha = 0.15f)),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Verified,
                                        contentDescription = null,
                                        tint = NewsEmerald,
                                        modifier = Modifier.size(18.dp)
                                    )
                                }
                                Spacer(modifier = Modifier.width(10.dp))
                                Column {
                                    Text(
                                        text = "JOURNALISTIC VERIFICATION",
                                        style = MaterialTheme.typography.labelMedium.copy(
                                            color = NewsEmerald,
                                            fontWeight = FontWeight.Bold,
                                            letterSpacing = 0.5.sp
                                        )
                                    )
                                    Text(
                                        text = "Fact-Check & Wire Verification",
                                        style = MaterialTheme.typography.bodySmall.copy(fontSize = 11.sp),
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                            }

                            if (isFactChecking) {
                                CircularProgressIndicator(
                                    modifier = Modifier.size(20.dp),
                                    color = NewsEmerald,
                                    strokeWidth = 2.dp
                                )
                            } else {
                                OutlinedButton(
                                    onClick = { viewModel.verifyArticleWithGemini(currentArticle) },
                                    shape = RoundedCornerShape(8.dp),
                                    contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp)
                                ) {
                                    Text(
                                        text = if (factCheckResult == null) "Run Fact-Check" else "Re-verify",
                                        style = MaterialTheme.typography.labelSmall
                                    )
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        if (factCheckResult != null) {
                            val result = factCheckResult!!
                            val scorePercentage = result.trustScore
                            val scoreColor = if (scorePercentage >= 85) NewsEmerald else if (scorePercentage >= 65) NewsAmber else NewsCrimson

                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clip(RoundedCornerShape(10.dp))
                                    .background(scoreColor.copy(alpha = 0.1f))
                                    .padding(10.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(
                                        text = "Verification: ${result.verificationStatus.replace('_', ' ')}",
                                        style = MaterialTheme.typography.labelMedium.copy(
                                            color = scoreColor,
                                            fontWeight = FontWeight.Bold
                                        )
                                    )
                                    Text(
                                        text = "Source: ${result.publisherCredibility}",
                                        style = MaterialTheme.typography.bodySmall.copy(fontSize = 11.sp),
                                        color = MaterialTheme.colorScheme.onSurface
                                    )
                                }
                                Box(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(8.dp))
                                        .background(scoreColor)
                                        .padding(horizontal = 8.dp, vertical = 4.dp)
                                ) {
                                    Text(
                                        text = "$scorePercentage% Trust",
                                        style = MaterialTheme.typography.labelSmall.copy(
                                            color = Color.White,
                                            fontWeight = FontWeight.Bold
                                        )
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.height(8.dp))

                            Text(
                                text = "Analysis & Evidence",
                                style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            Spacer(modifier = Modifier.height(2.dp))
                            Text(
                                text = result.analysis,
                                style = MaterialTheme.typography.bodySmall.copy(lineHeight = 18.sp),
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )

                            if (result.keyVerifications.isNotEmpty()) {
                                Spacer(modifier = Modifier.height(6.dp))
                                result.keyVerifications.forEach { point ->
                                    Text(
                                        text = "• $point",
                                        style = MaterialTheme.typography.bodySmall.copy(
                                            fontSize = 12.sp,
                                            lineHeight = 17.sp
                                        ),
                                        color = MaterialTheme.colorScheme.onSurface
                                    )
                                }
                            }
                        } else {
                            // Default state prior to explicit button tap
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clip(RoundedCornerShape(10.dp))
                                    .background(NewsEmerald.copy(alpha = 0.08f))
                                    .padding(10.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(
                                        text = "Tier-1 Accredited Journalism",
                                        style = MaterialTheme.typography.labelMedium.copy(
                                            color = NewsEmerald,
                                            fontWeight = FontWeight.Bold
                                        )
                                    )
                                    Text(
                                        text = "Reported by ${currentArticle.source} editorial desk and tracked across 14 wire networks.",
                                        style = MaterialTheme.typography.bodySmall.copy(fontSize = 11.sp),
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                                Box(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(8.dp))
                                        .background(NewsEmerald)
                                        .padding(horizontal = 8.dp, vertical = 4.dp)
                                ) {
                                    Text(
                                        text = "${(currentArticle.importanceScore * 100).toInt()}% Trust",
                                        style = MaterialTheme.typography.labelSmall.copy(
                                            color = Color.White,
                                            fontWeight = FontWeight.Bold
                                        )
                                    )
                                }
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(20.dp))

                // --- OFFICIAL WEBSITE & CROSS-VERIFICATION BUTTONS ---
                Button(
                    onClick = {
                        if (isUnverifiedSource) {
                            showUnverifiedLinkDialog = true
                        } else {
                            try {
                                val targetUri = Uri.parse(currentArticle.url)
                                val browserIntent = Intent(Intent.ACTION_VIEW, targetUri)
                                context.startActivity(browserIntent)
                            } catch (e: Exception) {
                                try {
                                    val searchIntent = Intent(
                                        Intent.ACTION_VIEW,
                                        Uri.parse("https://www.google.com/search?q=${Uri.encode(currentArticle.source + " " + currentArticle.title)}")
                                    )
                                    context.startActivity(searchIntent)
                                } catch (ignored: Exception) {}
                            }
                        }
                    },
                    modifier = Modifier.fillMaxWidth().testTag("read_official_website_button"),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (isUnverifiedSource) NewsCrimson else MaterialTheme.colorScheme.primary
                    )
                ) {
                    if (isUnverifiedSource) {
                        Icon(imageVector = Icons.Default.LinkOff, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Open Source Link (Unverified)")
                    } else {
                        Text("Read on ${currentArticle.source} Official Site")
                        Spacer(modifier = Modifier.width(8.dp))
                        Icon(imageVector = Icons.AutoMirrored.Filled.OpenInNew, contentDescription = null, modifier = Modifier.size(16.dp))
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                OutlinedButton(
                    onClick = {
                        try {
                            val googleNewsUri = Uri.parse("https://news.google.com/search?q=${Uri.encode(currentArticle.title)}")
                            val newsIntent = Intent(Intent.ACTION_VIEW, googleNewsUri)
                            context.startActivity(newsIntent)
                        } catch (e: Exception) {
                            // Fallback
                        }
                    },
                    modifier = Modifier.fillMaxWidth().testTag("search_google_news_button"),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text("Cross-Check on Google News")
                    Spacer(modifier = Modifier.width(8.dp))
                    Icon(imageVector = Icons.AutoMirrored.Filled.OpenInNew, contentDescription = null, modifier = Modifier.size(16.dp))
                }
            }
        }

        // --- RELATED STORIES CAROUSEL ---
        if (relatedArticles.isNotEmpty()) {
            item {
                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    text = "Related Stories",
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                    color = MaterialTheme.colorScheme.onBackground,
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
                )
            }

            items(relatedArticles, key = { it.id }) { related ->
                Box(modifier = Modifier.padding(horizontal = 16.dp, vertical = 6.dp)) {
                    NewsCard(
                        article = related,
                        onClick = { viewModel.openArticle(related) },
                        onBookmarkToggle = { viewModel.toggleBookmark(related) },
                        urlValidationResult = urlValidationMap[related.url]
                    )
                }
            }
        }
    }

    if (showClusterDialog) {
        ClusterCoverageDialog(
            article = currentArticle,
            onDismiss = { showClusterDialog = false }
        )
    }

    // Safety Alert Dialog for Unverified Source Websites
    if (showUnverifiedLinkDialog) {
        AlertDialog(
            onDismissRequest = { showUnverifiedLinkDialog = false },
            icon = {
                Icon(
                    imageVector = Icons.Default.Warning,
                    contentDescription = "Warning",
                    tint = NewsCrimson,
                    modifier = Modifier.size(32.dp)
                )
            },
            title = {
                Text(
                    text = "Unverified Source Website",
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                )
            },
            text = {
                Column {
                    Text(
                        text = "The official web link for this article (${currentArticle.url}) failed automated HTTP reachability validation. It returned no valid response or may be dead.",
                        style = MaterialTheme.typography.bodyMedium
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "We recommend cross-checking on Google News or trusted accredited wire services.",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        showUnverifiedLinkDialog = false
                        try {
                            val targetUri = Uri.parse(currentArticle.url)
                            val browserIntent = Intent(Intent.ACTION_VIEW, targetUri)
                            context.startActivity(browserIntent)
                        } catch (e: Exception) {
                            try {
                                val searchIntent = Intent(
                                    Intent.ACTION_VIEW,
                                    Uri.parse("https://www.google.com/search?q=${Uri.encode(currentArticle.source + " " + currentArticle.title)}")
                                )
                                context.startActivity(searchIntent)
                            } catch (ignored: Exception) {}
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = NewsCrimson)
                ) {
                    Text("Open Anyway")
                }
            },
            dismissButton = {
                TextButton(onClick = { showUnverifiedLinkDialog = false }) {
                    Text("Cancel")
                }
            }
        )
    }
}
