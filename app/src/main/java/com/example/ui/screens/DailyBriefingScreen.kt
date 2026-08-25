package com.example.ui.screens

import android.content.Intent
import androidx.compose.animation.AnimatedVisibility
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
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.WbSunny
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Divider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ScrollableTabRow
import androidx.compose.material3.Tab
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.data.model.CountryDatabase
import com.example.data.model.NewsArticle
import com.example.ui.components.AiSummaryDialog
import com.example.ui.components.ClusterCoverageDialog
import com.example.ui.components.NewsCard
import com.example.ui.theme.NewsAmber
import com.example.ui.theme.NewsBluePrimary
import com.example.ui.viewmodel.AppScreen
import com.example.ui.viewmodel.NewsViewModel

@Composable
fun DailyBriefingScreen(
    viewModel: NewsViewModel,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val preferences by viewModel.userPreferences.collectAsStateWithLifecycle()
    val briefing by viewModel.latestDailyBriefing.collectAsStateWithLifecycle()
    val allArticles by viewModel.allArticles.collectAsStateWithLifecycle()
    val pipelineState by viewModel.pipelineState.collectAsStateWithLifecycle()
    val isAiSummarizing by viewModel.isAiSummarizing.collectAsStateWithLifecycle()
    val urlValidationMap by viewModel.urlValidationMap.collectAsStateWithLifecycle()

    var selectedTabIndex by remember { mutableIntStateOf(0) }
    var clusterArticleToShow by remember { mutableStateOf<NewsArticle?>(null) }
    var aiSummaryArticleToShow by remember { mutableStateOf<NewsArticle?>(null) }

    val countryInfo = CountryDatabase.getByName(preferences.primaryCountry)

    // Sections
    val tabs = listOf(
        "Overview" to "☀️",
        "World" to "🌍",
        preferences.primaryCountry to countryInfo.flagEmoji,
        preferences.primaryRegion to "📍",
        "AI & Tech" to "🤖",
        "Sports" to "🏆",
        "Trending" to "🔥"
    )

    // Filtered articles for the active section
    val currentArticles: List<NewsArticle> = when (selectedTabIndex) {
        1 -> allArticles.filter { it.country.equals("Global", ignoreCase = true) || it.category.equals("World", ignoreCase = true) }
        2 -> allArticles.filter { it.country.equals(preferences.primaryCountry, ignoreCase = true) || it.category.equals(preferences.primaryCountry, ignoreCase = true) }
        3 -> allArticles.filter { it.region.equals(preferences.primaryRegion, ignoreCase = true) || it.category.equals("State/Regional", ignoreCase = true) }
        4 -> allArticles.filter { it.category.equals("Artificial Intelligence", ignoreCase = true) || it.category.equals("Technology", ignoreCase = true) }
        5 -> allArticles.filter { it.category.equals("Sports", ignoreCase = true) || it.category.equals("Cricket", ignoreCase = true) || it.category.equals("Football", ignoreCase = true) }
        6 -> allArticles.sortedByDescending { it.trendingScore }
        else -> allArticles.sortedByDescending { it.importanceScore }.take(8)
    }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .testTag("daily_briefing_screen"),
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

                Text(
                    text = "Morning Briefing",
                    style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
                    color = MaterialTheme.colorScheme.onBackground
                )

                Row {
                    IconButton(
                        onClick = {
                            val shareText = "☀️ Daily Morning Brief (${briefing?.dateStr ?: "Today"})\n\n" +
                                    "${briefing?.greetingMessage}\n\n" +
                                    "${briefing?.keyTakeaways}\n\n" +
                                    "Read full coverage via RAYON News"
                            val sendIntent = Intent().apply {
                                action = Intent.ACTION_SEND
                                putExtra(Intent.EXTRA_TEXT, shareText)
                                type = "text/plain"
                            }
                            context.startActivity(Intent.createChooser(sendIntent, "Share Morning Brief"))
                        }
                    ) {
                        Icon(imageVector = Icons.Default.Share, contentDescription = "Share Briefing")
                    }

                    IconButton(
                        onClick = { viewModel.trigger630AmBriefingUpdate() }
                    ) {
                        Icon(imageVector = Icons.Default.Refresh, contentDescription = "Refresh Briefing")
                    }
                }
            }
        }

        // --- HERO GREETING BANNER ---
        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 6.dp),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(
                            Brush.verticalGradient(
                                listOf(
                                    NewsAmber.copy(alpha = 0.15f),
                                    NewsBluePrimary.copy(alpha = 0.05f)
                                )
                            )
                        )
                        .padding(20.dp)
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
                                        .size(40.dp)
                                        .clip(RoundedCornerShape(12.dp))
                                        .background(NewsAmber.copy(alpha = 0.2f)),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.WbSunny,
                                        contentDescription = null,
                                        tint = NewsAmber,
                                        modifier = Modifier.size(24.dp)
                                    )
                                }
                                Spacer(modifier = Modifier.width(10.dp))
                                Column {
                                    Text(
                                        text = "6:30 AM DAILY EDITION",
                                        style = MaterialTheme.typography.labelSmall.copy(
                                            fontWeight = FontWeight.ExtraBold,
                                            color = NewsAmber,
                                            letterSpacing = 1.sp
                                        )
                                    )
                                    Text(
                                        text = "${briefing?.dateStr ?: "Today"} • ${preferences.userTimezone}",
                                        style = MaterialTheme.typography.bodySmall,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                            }

                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(12.dp))
                                    .background(MaterialTheme.colorScheme.primaryContainer)
                                    .padding(horizontal = 8.dp, vertical = 4.dp)
                            ) {
                                Text(
                                    text = "5-MIN READ",
                                    style = MaterialTheme.typography.labelSmall.copy(
                                        fontWeight = FontWeight.Bold,
                                        color = MaterialTheme.colorScheme.onPrimaryContainer
                                    )
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(14.dp))

                        Text(
                            text = briefing?.greetingMessage ?: "Good Morning, ${preferences.userName} 👋",
                            style = MaterialTheme.typography.headlineMedium.copy(fontWeight = FontWeight.Bold),
                            color = MaterialTheme.colorScheme.onSurface
                        )

                        Spacer(modifier = Modifier.height(10.dp))

                        // Takeaways
                        if (!briefing?.keyTakeaways.isNullOrBlank()) {
                            Text(
                                text = "Today's Core Briefing:",
                                style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
                                color = MaterialTheme.colorScheme.primary
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = briefing!!.keyTakeaways,
                                style = MaterialTheme.typography.bodyMedium.copy(lineHeight = 22.sp),
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }

                        // Pipeline progress indicator if running
                        AnimatedVisibility(visible = pipelineState.isRunning) {
                            Column(modifier = Modifier.padding(top = 12.dp)) {
                                Text(
                                    text = pipelineState.currentStep,
                                    style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Bold),
                                    color = NewsBluePrimary
                                )
                                Spacer(modifier = Modifier.height(6.dp))
                                LinearProgressIndicator(
                                    progress = { pipelineState.progress },
                                    modifier = Modifier.fillMaxWidth().height(6.dp).clip(RoundedCornerShape(3.dp))
                                )
                            }
                        }
                    }
                }
            }
        }

        // --- 6:30 AM PIPELINE SIMULATION ACTION BAR ---
        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp),
                shape = RoundedCornerShape(14.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f))
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(14.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "Automated Morning Pipeline",
                            style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold)
                        )
                        Text(
                            text = "Next automated run: 6:30 AM ${preferences.userTimezone}",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }

                    Button(
                        onClick = { viewModel.trigger630AmBriefingUpdate() },
                        enabled = !pipelineState.isRunning,
                        colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary),
                        shape = RoundedCornerShape(10.dp)
                    ) {
                        if (pipelineState.isRunning) {
                            CircularProgressIndicator(modifier = Modifier.size(16.dp), color = Color.White, strokeWidth = 2.dp)
                        } else {
                            Icon(imageVector = Icons.Default.PlayArrow, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Run Pipeline")
                        }
                    }
                }
            }
        }

        // --- PILLAR SECTION TABS ---
        item {
            ScrollableTabRow(
                selectedTabIndex = selectedTabIndex,
                edgePadding = 16.dp,
                divider = {},
                modifier = Modifier.padding(vertical = 8.dp)
            ) {
                tabs.forEachIndexed { index, (title, emoji) ->
                    Tab(
                        selected = selectedTabIndex == index,
                        onClick = { selectedTabIndex = index },
                        text = {
                            Text(
                                text = "$emoji $title",
                                style = MaterialTheme.typography.labelLarge.copy(
                                    fontWeight = if (selectedTabIndex == index) FontWeight.Bold else FontWeight.Normal
                                )
                            )
                        }
                    )
                }
            }
        }

        // --- SECTION SUBTITLE ---
        item {
            val (title, emoji) = tabs[selectedTabIndex]
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 6.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "$emoji $title Stories (${currentArticles.size})",
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                    color = MaterialTheme.colorScheme.onBackground
                )
            }
        }

        // --- ARTICLES LIST ---
        items(currentArticles, key = { it.id }) { article ->
            Box(modifier = Modifier.padding(horizontal = 16.dp, vertical = 6.dp)) {
                NewsCard(
                    article = article,
                    onClick = { viewModel.openArticle(article) },
                    onBookmarkToggle = { viewModel.toggleBookmark(article) },
                    onClusterClick = { clusterArticleToShow = article },
                    onAiSummaryClick = {
                        aiSummaryArticleToShow = article
                        viewModel.generateAiSummaryForArticle(article)
                    },
                    urlValidationResult = urlValidationMap[article.url]
                )
            }
        }
    }

    // Cluster Coverage Dialog
    clusterArticleToShow?.let { article ->
        ClusterCoverageDialog(
            article = article,
            onDismiss = { clusterArticleToShow = null }
        )
    }

    // AI Summary Dialog
    aiSummaryArticleToShow?.let { article ->
        AiSummaryDialog(
            article = article,
            isLoading = isAiSummarizing,
            onRegenerate = { viewModel.generateAiSummaryForArticle(article) },
            onDismiss = { aiSummaryArticleToShow = null }
        )
    }
}
