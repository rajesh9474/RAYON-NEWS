package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.DarkMode
import androidx.compose.material.icons.filled.LightMode
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.data.model.CountryDatabase
import com.example.data.model.NewsArticle
import com.example.data.model.NewsCategories
import com.example.ui.components.AiSummaryDialog
import com.example.ui.components.BreakingNewsTicker
import com.example.ui.components.ClusterCoverageDialog
import com.example.ui.components.MorningBriefCard
import com.example.ui.components.NewsCard
import com.example.ui.theme.HighDensityBorder
import com.example.ui.theme.HighDensityTextPrimary
import com.example.ui.theme.HighDensityTextSecondary
import com.example.ui.theme.NewsAmber
import com.example.ui.theme.NewsBluePrimary
import com.example.ui.theme.NewsCrimson
import com.example.ui.viewmodel.AppScreen
import com.example.ui.viewmodel.NewsViewModel

@Composable
fun HomeScreen(
    viewModel: NewsViewModel,
    modifier: Modifier = Modifier
) {
    val preferences by viewModel.userPreferences.collectAsStateWithLifecycle()
    val latestBriefing by viewModel.latestDailyBriefing.collectAsStateWithLifecycle()
    val breakingNews by viewModel.breakingNews.collectAsStateWithLifecycle()
    val personalizedFeed by viewModel.personalizedFeed.collectAsStateWithLifecycle()
    val pipelineState by viewModel.pipelineState.collectAsStateWithLifecycle()
    val notifications by viewModel.notifications.collectAsStateWithLifecycle()
    val selectedCategory by viewModel.selectedCategory.collectAsStateWithLifecycle()
    val isAiSummarizing by viewModel.isAiSummarizing.collectAsStateWithLifecycle()

    var clusterArticleToShow by remember { mutableStateOf<NewsArticle?>(null) }
    var aiSummaryArticleToShow by remember { mutableStateOf<NewsArticle?>(null) }

    val unreadCount = notifications.count { !it.isRead }
    val countryInfo = CountryDatabase.getByName(preferences.primaryCountry)

    // Filter feed by category if a pill is chosen
    val displayedArticles = if (selectedCategory == "Top News") {
        personalizedFeed
    } else {
        personalizedFeed.filter { it.category.equals(selectedCategory, ignoreCase = true) }
    }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .testTag("home_screen"),
        contentPadding = PaddingValues(bottom = 80.dp)
    ) {
        // --- HIGH DENSITY TOP HEADER ---
        item {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(MaterialTheme.colorScheme.surface.copy(alpha = 0.95f))
                    .padding(horizontal = 16.dp, vertical = 12.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Logo & App Name
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.clickable { viewModel.navigateTo(AppScreen.HOME) }
                ) {
                    Box(
                        modifier = Modifier
                            .size(34.dp)
                            .clip(RoundedCornerShape(8.dp))
                            .background(NewsBluePrimary),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "R",
                            style = MaterialTheme.typography.titleMedium.copy(
                                color = Color.White,
                                fontWeight = FontWeight.Bold,
                                fontSize = 18.sp
                            )
                        )
                    }
                    Spacer(modifier = Modifier.width(10.dp))
                    Text(
                        text = "RAYON",
                        style = MaterialTheme.typography.titleLarge.copy(
                            fontWeight = FontWeight.Bold,
                            fontSize = 20.sp,
                            letterSpacing = (-0.5).sp
                        ),
                        color = MaterialTheme.colorScheme.onBackground
                    )
                }

                // High Density Action Buttons (Search, Notification Bell with Red Live Dot, Profile)
                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Search Action Button
                    Box(
                        modifier = Modifier
                            .size(38.dp)
                            .clip(CircleShape)
                            .background(MaterialTheme.colorScheme.surfaceVariant)
                            .clickable { viewModel.navigateTo(AppScreen.SEARCH) }
                            .testTag("search_top_btn"),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Search,
                            contentDescription = "Search",
                            tint = MaterialTheme.colorScheme.onSurface,
                            modifier = Modifier.size(19.dp)
                        )
                    }

                    // Notification Button with Live Alert Badge
                    Box(
                        modifier = Modifier
                            .size(38.dp)
                            .clip(CircleShape)
                            .background(MaterialTheme.colorScheme.surfaceVariant)
                            .clickable { viewModel.navigateTo(AppScreen.NOTIFICATIONS) }
                            .testTag("notifications_top_btn"),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Notifications,
                            contentDescription = "Notifications",
                            tint = MaterialTheme.colorScheme.onSurface,
                            modifier = Modifier.size(19.dp)
                        )
                        if (unreadCount > 0) {
                            Box(
                                modifier = Modifier
                                    .size(8.dp)
                                    .clip(CircleShape)
                                    .background(NewsCrimson)
                                    .align(Alignment.TopEnd)
                            )
                        }
                    }

                    // User Profile Avatar
                    Box(
                        modifier = Modifier
                            .size(38.dp)
                            .clip(CircleShape)
                            .background(MaterialTheme.colorScheme.primaryContainer)
                            .clickable { viewModel.navigateTo(AppScreen.SETTINGS) }
                            .testTag("profile_top_btn"),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = preferences.userName.take(1).uppercase(),
                            style = MaterialTheme.typography.labelLarge.copy(
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.primary
                            )
                        )
                    }
                }
            }
        }

        // --- HIGH DENSITY LIVE BREAKING NEWS TICKER ---
        if (breakingNews.isNotEmpty()) {
            item {
                BreakingNewsTicker(
                    breakingArticles = breakingNews,
                    onArticleClick = { viewModel.openArticle(it) }
                )
            }
        }

        // --- HIGH DENSITY MORNING BRIEF HERO CARD ---
        item {
            Box(modifier = Modifier.padding(horizontal = 16.dp, vertical = 6.dp)) {
                MorningBriefCard(
                    briefing = latestBriefing,
                    preferences = preferences,
                    pipelineState = pipelineState,
                    onOpenFullBriefing = { viewModel.navigateTo(AppScreen.DAILY_BRIEFING) },
                    onTriggerPipeline = { viewModel.trigger630AmBriefingUpdate() }
                )
            }
        }

        // --- HIGH DENSITY CATEGORY PILL FILTER CHIPS ---
        item {
            Column(modifier = Modifier.padding(vertical = 8.dp)) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .horizontalScroll(rememberScrollState())
                        .padding(horizontal = 16.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    NewsCategories.ALL.take(10).forEach { category ->
                        val isSelected = selectedCategory.equals(category.name, ignoreCase = true)

                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(24.dp))
                                .background(
                                    if (isSelected) Color(0xFF0F172A) // slate-900
                                    else MaterialTheme.colorScheme.surface
                                )
                                .then(
                                    if (!isSelected) {
                                        Modifier.background(
                                            color = MaterialTheme.colorScheme.surface,
                                            shape = RoundedCornerShape(24.dp)
                                        )
                                    } else Modifier
                                )
                                .clickable { viewModel.selectCategory(category.name) }
                                .padding(horizontal = 14.dp, vertical = 8.dp)
                                .testTag("category_pill_${category.id}")
                        ) {
                            Text(
                                text = category.name,
                                style = MaterialTheme.typography.labelMedium.copy(
                                    fontWeight = if (isSelected) FontWeight.SemiBold else FontWeight.Medium,
                                    fontSize = 13.sp
                                ),
                                color = if (isSelected) Color.White else HighDensityTextSecondary
                            )
                        }
                    }
                }
            }
        }

        // --- COMPACT NEWS FEED ITEMS ---
        items(displayedArticles, key = { it.id }) { article ->
            Box(modifier = Modifier.padding(horizontal = 16.dp, vertical = 5.dp)) {
                NewsCard(
                    article = article,
                    onClick = { viewModel.openArticle(article) },
                    onBookmarkToggle = { viewModel.toggleBookmark(article) },
                    onClusterClick = { clusterArticleToShow = article },
                    onAiSummaryClick = {
                        aiSummaryArticleToShow = article
                        viewModel.generateAiSummaryForArticle(article)
                    }
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
