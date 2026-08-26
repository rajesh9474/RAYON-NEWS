package com.example.ui.screens

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.SizeTransform
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.slideOutVertically
import androidx.compose.animation.togetherWith
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
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.Category
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.DarkMode
import androidx.compose.material.icons.filled.FilterList
import androidx.compose.material.icons.filled.LightMode
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.ScrollableTabRow
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRowDefaults
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.material3.Text
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.material3.pulltorefresh.PullToRefreshDefaults
import androidx.compose.material3.pulltorefresh.rememberPullToRefreshState
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
import com.example.ui.theme.NewsEmerald
import com.example.ui.viewmodel.AppScreen
import com.example.ui.viewmodel.NewsViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    viewModel: NewsViewModel,
    modifier: Modifier = Modifier
) {
    val preferences by viewModel.userPreferences.collectAsStateWithLifecycle()
    val latestBriefing by viewModel.latestDailyBriefing.collectAsStateWithLifecycle()
    val breakingNews by viewModel.breakingNews.collectAsStateWithLifecycle()
    val personalizedFeed by viewModel.personalizedFeed.collectAsStateWithLifecycle()
    val allArticles by viewModel.allArticles.collectAsStateWithLifecycle()
    val pipelineState by viewModel.pipelineState.collectAsStateWithLifecycle()
    val notifications by viewModel.notifications.collectAsStateWithLifecycle()
    val selectedCategory by viewModel.selectedCategory.collectAsStateWithLifecycle()
    val isAiSummarizing by viewModel.isAiSummarizing.collectAsStateWithLifecycle()
    val urlValidationMap by viewModel.urlValidationMap.collectAsStateWithLifecycle()
    val isRefreshingFeeds by viewModel.isRefreshingFeeds.collectAsStateWithLifecycle()
    val liveFeedStatusMessage by viewModel.liveFeedStatusMessage.collectAsStateWithLifecycle()

    val pullRefreshState = rememberPullToRefreshState()

    var clusterArticleToShow by remember { mutableStateOf<NewsArticle?>(null) }
    var aiSummaryArticleToShow by remember { mutableStateOf<NewsArticle?>(null) }

    val unreadCount = notifications.count { !it.isRead }
    val countryInfo = CountryDatabase.getByName(preferences.primaryCountry)

    // Category definition list for the tabbed navigation interface
    data class FeedTabCategory(
        val id: String,
        val displayName: String,
        val emoji: String,
        val filterQuery: String
    )

    val feedCategories = remember {
        listOf(
            FeedTabCategory("top", "Top Stories", "🔥", "Top News"),
            FeedTabCategory("tech", "Tech", "💻", "Tech"),
            FeedTabCategory("politics", "Politics", "🏛️", "Politics"),
            FeedTabCategory("health", "Health", "🏥", "Health"),
            FeedTabCategory("business", "Business", "💼", "Business"),
            FeedTabCategory("ai", "AI", "🤖", "Artificial Intelligence"),
            FeedTabCategory("world", "World", "🌍", "World"),
            FeedTabCategory("sports", "Sports", "🏆", "Sports"),
            FeedTabCategory("science", "Science", "🧪", "Science"),
            FeedTabCategory("entertainment", "Entertainment", "🎬", "Entertainment")
        )
    }

    val selectedTabIndex = remember(selectedCategory, feedCategories) {
        val idx = feedCategories.indexOfFirst { category ->
            selectedCategory.equals(category.filterQuery, ignoreCase = true) ||
            (category.id == "top" && (selectedCategory.equals("Top News", ignoreCase = true) || selectedCategory.isBlank()))
        }
        if (idx >= 0) idx else 0
    }

    PullToRefreshBox(
        isRefreshing = isRefreshingFeeds,
        onRefresh = { viewModel.refreshLiveFeeds() },
        state = pullRefreshState,
        modifier = modifier
            .fillMaxSize()
            .testTag("home_pull_to_refresh_box"),
        indicator = {
            PullToRefreshDefaults.Indicator(
                state = pullRefreshState,
                isRefreshing = isRefreshingFeeds,
                containerColor = MaterialTheme.colorScheme.surfaceVariant,
                color = NewsBluePrimary,
                modifier = Modifier.align(Alignment.TopCenter)
            )
        }
    ) {
        LazyColumn(
            modifier = Modifier
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
                    Column {
                        Text(
                            text = "RAYON",
                            style = MaterialTheme.typography.titleLarge.copy(
                                fontWeight = FontWeight.Bold,
                                fontSize = 18.sp,
                                letterSpacing = (-0.5).sp
                            ),
                            color = MaterialTheme.colorScheme.onBackground
                        )
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.clickable { viewModel.refreshLiveFeeds() }
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(6.dp)
                                    .clip(CircleShape)
                                    .background(if (isRefreshingFeeds) NewsAmber else NewsEmerald)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = if (isRefreshingFeeds) "Syncing Wire..." else "Live Global Wire",
                                fontSize = 10.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = if (isRefreshingFeeds) NewsAmber else NewsEmerald
                            )
                        }
                    }
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

        // --- TABBED NAVIGATION INTERFACE FOR NEWS CATEGORIES ---
        item(key = "category_tabs_navigation") {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(MaterialTheme.colorScheme.surface)
                    .testTag("category_navigation_section")
            ) {
                ScrollableTabRow(
                    selectedTabIndex = selectedTabIndex,
                    edgePadding = 16.dp,
                    containerColor = MaterialTheme.colorScheme.surface,
                    contentColor = MaterialTheme.colorScheme.primary,
                    indicator = { tabPositions ->
                        if (selectedTabIndex < tabPositions.size) {
                            TabRowDefaults.SecondaryIndicator(
                                modifier = Modifier.tabIndicatorOffset(tabPositions[selectedTabIndex]),
                                color = NewsBluePrimary,
                                height = 3.dp
                            )
                        }
                    },
                    divider = {
                        HorizontalDivider(
                            color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.35f),
                            thickness = 1.dp
                        )
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("category_tab_row")
                ) {
                    feedCategories.forEachIndexed { index, category ->
                        val isSelected = (index == selectedTabIndex)
                        Tab(
                            selected = isSelected,
                            onClick = {
                                viewModel.selectCategory(category.filterQuery)
                            },
                            text = {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                                    modifier = Modifier.padding(vertical = 6.dp)
                                ) {
                                    Text(
                                        text = category.emoji,
                                        fontSize = 15.sp
                                    )
                                    Text(
                                        text = category.displayName,
                                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                                        fontSize = 14.sp,
                                        color = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                            },
                            modifier = Modifier.testTag("category_tab_${category.id}")
                        )
                    }
                }
            }
        }

        // --- HORIZONTAL SLIDING ANIMATED CATEGORY FEED CONTENT ---
        item(key = "animated_category_feed") {
            AnimatedContent(
                targetState = selectedTabIndex,
                transitionSpec = {
                    val direction = if (targetState > initialState) 1 else -1
                    (slideInHorizontally(
                        animationSpec = spring(
                            dampingRatio = Spring.DampingRatioLowBouncy,
                            stiffness = Spring.StiffnessMediumLow
                        ),
                        initialOffsetX = { fullWidth -> direction * fullWidth }
                    ) + fadeIn(
                        animationSpec = spring(stiffness = Spring.StiffnessMediumLow)
                    )).togetherWith(
                        slideOutHorizontally(
                            animationSpec = spring(
                                dampingRatio = Spring.DampingRatioNoBouncy,
                                stiffness = Spring.StiffnessMediumLow
                            ),
                            targetOffsetX = { fullWidth -> -direction * fullWidth }
                        ) + fadeOut(
                            animationSpec = spring(stiffness = Spring.StiffnessMedium)
                        )
                    ).using(
                        SizeTransform(clip = false)
                    )
                },
                label = "CategoryHorizontalSlideTransition",
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("category_animated_content")
            ) { tabIndex ->
                val currentCategory = feedCategories.getOrNull(tabIndex) ?: feedCategories[0]
                val articlesForTab: List<NewsArticle> = remember(currentCategory, personalizedFeed, allArticles) {
                    filterArticlesByCategory(currentCategory.filterQuery, allArticles, personalizedFeed)
                }

                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 16.dp)
                ) {
                    // --- HIGH DENSITY LIVE BREAKING NEWS TICKER ---
                    if (breakingNews.isNotEmpty() && (currentCategory.id == "top" || currentCategory.filterQuery == "Top News")) {
                        BreakingNewsTicker(
                            breakingArticles = breakingNews,
                            onArticleClick = { viewModel.openArticle(it) }
                        )
                    }

                    // --- HIGH DENSITY MORNING BRIEF HERO CARD ---
                    if (currentCategory.id == "top" || currentCategory.filterQuery == "Top News") {
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

                    // --- FEED HEADER WITH ACTIVE FILTER STATUS ---
                    if (currentCategory.id != "top" && currentCategory.filterQuery != "Top News") {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 16.dp, vertical = 8.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(
                                    text = "Showing ${currentCategory.displayName} News",
                                    style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                                    color = MaterialTheme.colorScheme.primary
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = "(${articlesForTab.size})",
                                    style = MaterialTheme.typography.labelMedium,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }

                            OutlinedButton(
                                onClick = { viewModel.selectCategory("Top News") },
                                contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp),
                                shape = RoundedCornerShape(12.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Clear,
                                    contentDescription = "Clear filter",
                                    modifier = Modifier.size(14.dp)
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Text("Reset", style = MaterialTheme.typography.labelSmall)
                            }
                        }
                    }

                    // --- EMPTY STATE IF NO ARTICLES MATCH ---
                    if (articlesForTab.isEmpty()) {
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 16.dp, vertical = 24.dp)
                                .testTag("empty_category_card"),
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)),
                            shape = RoundedCornerShape(16.dp)
                        ) {
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(24.dp),
                                horizontalAlignment = Alignment.CenterHorizontally,
                                verticalArrangement = Arrangement.Center
                            ) {
                                Text(
                                    text = "🔍",
                                    fontSize = 32.sp
                                )
                                Spacer(modifier = Modifier.height(10.dp))
                                Text(
                                    text = "No articles found in ${currentCategory.displayName}",
                                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                                Spacer(modifier = Modifier.height(6.dp))
                                Text(
                                    text = "Try switching to another category or browse Top Stories.",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                                Spacer(modifier = Modifier.height(14.dp))
                                Button(
                                    onClick = { viewModel.selectCategory("Top News") },
                                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
                                ) {
                                    Text("Browse All Top Stories")
                                }
                            }
                        }
                    } else {
                        // --- COMPACT NEWS FEED ITEMS ---
                        for (article in articlesForTab) {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(horizontal = 16.dp, vertical = 5.dp)
                            ) {
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
                }
            }
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

/**
 * Filters articles based on a given category query against full article content and metadata.
 */
private fun filterArticlesByCategory(
    categoryQuery: String,
    allArticles: List<NewsArticle>,
    personalizedFeed: List<NewsArticle>
): List<NewsArticle> {
    if (categoryQuery == "Top News" || categoryQuery == "top" || categoryQuery.isBlank()) {
        return if (personalizedFeed.isNotEmpty()) personalizedFeed else allArticles
    }
    val query = categoryQuery.trim()
    val filtered = allArticles.filter { article ->
        when {
            article.category.equals(query, ignoreCase = true) -> true
            query.equals("Tech", ignoreCase = true) && (
                article.category.contains("Tech", ignoreCase = true) ||
                article.category.contains("Artificial Intelligence", ignoreCase = true) ||
                article.tags.contains("Tech", ignoreCase = true) ||
                article.tags.contains("Silicon", ignoreCase = true)
            ) -> true
            query.equals("Politics", ignoreCase = true) && (
                article.category.contains("Politic", ignoreCase = true) ||
                article.tags.contains("Politics", ignoreCase = true) ||
                article.tags.contains("Governance", ignoreCase = true) ||
                article.tags.contains("Privacy", ignoreCase = true) ||
                article.tags.contains("Parliament", ignoreCase = true)
            ) -> true
            query.equals("Sports", ignoreCase = true) && (
                article.category.contains("Sport", ignoreCase = true) ||
                article.category.equals("Cricket", ignoreCase = true) ||
                article.category.equals("Football", ignoreCase = true) ||
                article.tags.contains("Sports", ignoreCase = true) ||
                article.tags.contains("Cricket", ignoreCase = true)
            ) -> true
            query.equals("Business", ignoreCase = true) && (
                article.category.contains("Business", ignoreCase = true) ||
                article.category.contains("Finance", ignoreCase = true) ||
                article.category.contains("Stock", ignoreCase = true) ||
                article.category.contains("Startup", ignoreCase = true)
            ) -> true
            query.equals("AI", ignoreCase = true) || query.equals("Artificial Intelligence", ignoreCase = true) -> {
                article.category.contains("AI", ignoreCase = true) ||
                article.category.contains("Artificial Intelligence", ignoreCase = true) ||
                article.tags.contains("AI", ignoreCase = true) ||
                article.tags.contains("LLM", ignoreCase = true)
            }
            query.equals("World", ignoreCase = true) -> {
                article.category.contains("World", ignoreCase = true) ||
                article.country.equals("Global", ignoreCase = true) ||
                article.region.equals("Worldwide", ignoreCase = true)
            }
            query.equals("Science", ignoreCase = true) -> {
                article.category.contains("Science", ignoreCase = true) ||
                article.category.contains("Space", ignoreCase = true) ||
                article.tags.contains("Science", ignoreCase = true)
            }
            query.equals("Entertainment", ignoreCase = true) -> {
                article.category.contains("Entertainment", ignoreCase = true) ||
                article.category.contains("Gaming", ignoreCase = true) ||
                article.category.contains("Movie", ignoreCase = true)
            }
            query.equals("Health", ignoreCase = true) -> {
                article.category.contains("Health", ignoreCase = true) ||
                article.tags.contains("Health", ignoreCase = true) ||
                article.tags.contains("Medicine", ignoreCase = true)
            }
            else -> {
                article.category.contains(query, ignoreCase = true) ||
                article.tags.contains(query, ignoreCase = true)
            }
        }
    }
    return if (filtered.isNotEmpty()) filtered else personalizedFeed.filter { it.category.contains(query, ignoreCase = true) }
}
