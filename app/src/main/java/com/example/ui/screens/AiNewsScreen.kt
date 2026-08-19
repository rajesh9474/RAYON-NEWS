package com.example.ui.screens

import androidx.compose.foundation.background
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Memory
import androidx.compose.material.icons.filled.Psychology
import androidx.compose.material.icons.filled.SmartToy
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
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
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.data.model.NewsArticle
import com.example.ui.components.AiSummaryDialog
import com.example.ui.components.ClusterCoverageDialog
import com.example.ui.components.NewsCard
import com.example.ui.theme.NewsAmber
import com.example.ui.theme.NewsBluePrimary
import com.example.ui.viewmodel.AppScreen
import com.example.ui.viewmodel.NewsViewModel

@Composable
fun AiNewsScreen(
    viewModel: NewsViewModel,
    modifier: Modifier = Modifier
) {
    val aiAndTechArticles by viewModel.aiAndTechArticles.collectAsStateWithLifecycle()
    val isAiSummarizing by viewModel.isAiSummarizing.collectAsStateWithLifecycle()

    var selectedSubtopic by remember { mutableStateOf("All AI") }
    var clusterArticleToShow by remember { mutableStateOf<NewsArticle?>(null) }
    var aiSummaryArticleToShow by remember { mutableStateOf<NewsArticle?>(null) }

    val subtopics = listOf("All AI", "Agents & LLMs", "On-Device & Mobile", "OpenAI & Gemini", "Robotics", "AI Safety & Policy", "Startups")

    val filteredArticles = when (selectedSubtopic) {
        "Agents & LLMs" -> aiAndTechArticles.filter { it.title.contains("Agent", ignoreCase = true) || it.tags.contains("LLM", ignoreCase = true) }
        "On-Device & Mobile" -> aiAndTechArticles.filter { it.title.contains("Mobile", ignoreCase = true) || it.tags.contains("Device", ignoreCase = true) }
        "OpenAI & Gemini" -> aiAndTechArticles.filter { it.tags.contains("Gemini", ignoreCase = true) || it.tags.contains("OpenAI", ignoreCase = true) }
        "AI Safety & Policy" -> aiAndTechArticles.filter { it.category.contains("Politics", ignoreCase = true) || it.tags.contains("Safety", ignoreCase = true) || it.tags.contains("Governance", ignoreCase = true) }
        else -> aiAndTechArticles
    }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .testTag("ai_news_screen"),
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
                    text = "AI & Frontier Tech",
                    style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
                    color = MaterialTheme.colorScheme.onBackground
                )

                IconButton(onClick = { viewModel.navigateTo(AppScreen.SEARCH) }) {
                    Icon(
                        imageVector = Icons.Default.AutoAwesome,
                        contentDescription = "Search AI",
                        tint = NewsAmber
                    )
                }
            }
        }

        // --- HERO BANNER ---
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
                            Brush.linearGradient(
                                listOf(
                                    Color(0xFF8B5CF6).copy(alpha = 0.18f),
                                    Color(0xFF3B82F6).copy(alpha = 0.08f)
                                )
                            )
                        )
                        .padding(20.dp)
                ) {
                    Column {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .size(40.dp)
                                    .clip(RoundedCornerShape(12.dp))
                                    .background(Color(0xFF8B5CF6).copy(alpha = 0.25f)),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.SmartToy,
                                    contentDescription = null,
                                    tint = Color(0xFF7C3AED),
                                    modifier = Modifier.size(24.dp)
                                )
                            }
                            Spacer(modifier = Modifier.width(12.dp))
                            Column {
                                Text(
                                    text = "ARTIFICIAL INTELLIGENCE",
                                    style = MaterialTheme.typography.labelMedium.copy(
                                        fontWeight = FontWeight.Black,
                                        color = Color(0xFF7C3AED),
                                        letterSpacing = 1.sp
                                    )
                                )
                                Text(
                                    text = "Frontier models, chips, agents, robotics & regulation",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }
                    }
                }
            }
        }

        // --- SUBTOPIC FILTER PILLS ---
        item {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(rememberScrollState())
                    .padding(horizontal = 16.dp, vertical = 10.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                subtopics.forEach { subtopic ->
                    val isSelected = selectedSubtopic == subtopic
                    FilterChip(
                        selected = isSelected,
                        onClick = { selectedSubtopic = subtopic },
                        label = { Text(subtopic) },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = Color(0xFF7C3AED),
                            selectedLabelColor = Color.White
                        )
                    )
                }
            }
        }

        // --- ARTICLE FEED ---
        items(filteredArticles, key = { it.id }) { article ->
            Box(modifier = Modifier.padding(horizontal = 16.dp, vertical = 6.dp)) {
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
