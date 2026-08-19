package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.filled.Category
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Public
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.WbSunny
import androidx.compose.material.icons.outlined.AutoAwesome
import androidx.compose.material.icons.outlined.BookmarkBorder
import androidx.compose.material.icons.outlined.Category
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.Public
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.material.icons.outlined.WbSunny
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.NavigationRail
import androidx.compose.material3.NavigationRailItem
import androidx.compose.material3.NavigationRailItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.ui.screens.AdminDashboardScreen
import com.example.ui.screens.AiNewsScreen
import com.example.ui.screens.ArticleDetailScreen
import com.example.ui.screens.CategoriesScreen
import com.example.ui.screens.CountrySelectionScreen
import com.example.ui.screens.DailyBriefingScreen
import com.example.ui.screens.HomeScreen
import com.example.ui.screens.NotificationsScreen
import com.example.ui.screens.SavedNewsScreen
import com.example.ui.screens.SearchScreen
import com.example.ui.screens.SettingsScreen
import com.example.ui.theme.HighDensityTextSecondary
import com.example.ui.theme.HighDensityTextTertiary
import com.example.ui.theme.NewsBluePrimary
import com.example.ui.viewmodel.AppScreen
import com.example.ui.viewmodel.NewsViewModel

data class NavItem(
    val screen: AppScreen,
    val label: String,
    val selectedIcon: ImageVector,
    val unselectedIcon: ImageVector
)

@Composable
fun GlobalNewsNavigation(
    viewModel: NewsViewModel
) {
    val currentScreen by viewModel.currentScreen.collectAsStateWithLifecycle()

    val navItems = listOf(
        NavItem(AppScreen.HOME, "Home", Icons.Filled.Home, Icons.Outlined.Home),
        NavItem(AppScreen.DAILY_BRIEFING, "Brief", Icons.Filled.WbSunny, Icons.Outlined.WbSunny),
        NavItem(AppScreen.AI_NEWS, "AI News", Icons.Filled.AutoAwesome, Icons.Outlined.AutoAwesome),
        NavItem(AppScreen.CATEGORIES, "Categories", Icons.Filled.Category, Icons.Outlined.Category),
        NavItem(AppScreen.SAVED, "Saved", Icons.Filled.Bookmark, Icons.Outlined.BookmarkBorder)
    )

    BoxWithConstraints(modifier = Modifier.fillMaxSize()) {
        val isExpandedScreen = maxWidth >= 700.dp

        if (isExpandedScreen) {
            // Large screen layout: NavigationRail on left + Screen on right
            Row(modifier = Modifier.fillMaxSize()) {
                NavigationRail(
                    modifier = Modifier.fillMaxHeight(),
                    containerColor = MaterialTheme.colorScheme.surface,
                    header = {
                        Box(
                            modifier = Modifier
                                .padding(vertical = 16.dp)
                                .size(40.dp)
                                .clip(RoundedCornerShape(10.dp))
                                .background(NewsBluePrimary),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "R",
                                style = MaterialTheme.typography.titleLarge.copy(
                                    color = Color.White,
                                    fontWeight = FontWeight.Bold
                                )
                            )
                        }
                    }
                ) {
                    Spacer(modifier = Modifier.height(8.dp))
                    navItems.forEach { item ->
                        val isSelected = currentScreen == item.screen
                        NavigationRailItem(
                            selected = isSelected,
                            onClick = { viewModel.navigateTo(item.screen) },
                            icon = {
                                Icon(
                                    imageVector = if (isSelected) item.selectedIcon else item.unselectedIcon,
                                    contentDescription = item.label,
                                    tint = if (isSelected) NewsBluePrimary else HighDensityTextTertiary
                                )
                            },
                            label = {
                                Text(
                                    text = item.label,
                                    style = MaterialTheme.typography.labelSmall.copy(
                                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                                        fontSize = 11.sp
                                    )
                                )
                            },
                            colors = NavigationRailItemDefaults.colors(
                                indicatorColor = Color(0xFFEFF6FF)
                            )
                        )
                    }

                    Spacer(modifier = Modifier.weight(1f))

                    NavigationRailItem(
                        selected = currentScreen == AppScreen.COUNTRY_SELECTION,
                        onClick = { viewModel.navigateTo(AppScreen.COUNTRY_SELECTION) },
                        icon = { Icon(Icons.Outlined.Public, contentDescription = "Country") },
                        label = { Text("Country") }
                    )

                    NavigationRailItem(
                        selected = currentScreen == AppScreen.SETTINGS,
                        onClick = { viewModel.navigateTo(AppScreen.SETTINGS) },
                        icon = { Icon(Icons.Outlined.Settings, contentDescription = "Settings") },
                        label = { Text("Settings") }
                    )
                }

                Box(modifier = Modifier.weight(1f).fillMaxHeight()) {
                    ScreenContent(currentScreen, viewModel)
                }
            }
        } else {
            // Mobile layout: Screen + Bottom NavigationBar
            Scaffold(
                bottomBar = {
                    // Hide bottom bar on Article Detail and Admin Dashboard for focus
                    if (currentScreen != AppScreen.ARTICLE_DETAIL && currentScreen != AppScreen.ADMIN_DASHBOARD) {
                        NavigationBar(
                            containerColor = MaterialTheme.colorScheme.surface,
                            tonalElevation = 2.dp,
                            modifier = Modifier.testTag("bottom_nav_bar")
                        ) {
                            navItems.forEach { item ->
                                val isSelected = currentScreen == item.screen
                                NavigationBarItem(
                                    selected = isSelected,
                                    onClick = { viewModel.navigateTo(item.screen) },
                                    icon = {
                                        Icon(
                                            imageVector = if (isSelected) item.selectedIcon else item.unselectedIcon,
                                            contentDescription = item.label,
                                            tint = if (isSelected) NewsBluePrimary else HighDensityTextTertiary,
                                            modifier = Modifier.size(22.dp)
                                        )
                                    },
                                    label = {
                                        Text(
                                            text = item.label,
                                            style = MaterialTheme.typography.labelSmall.copy(
                                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                                                fontSize = 10.sp
                                            ),
                                            color = if (isSelected) NewsBluePrimary else HighDensityTextTertiary
                                        )
                                    },
                                    colors = NavigationBarItemDefaults.colors(
                                        indicatorColor = Color(0xFFEFF6FF),
                                        selectedIconColor = NewsBluePrimary,
                                        selectedTextColor = NewsBluePrimary,
                                        unselectedIconColor = HighDensityTextTertiary,
                                        unselectedTextColor = HighDensityTextTertiary
                                    )
                                )
                            }
                        }
                    }
                }
            ) { paddingValues ->
                Box(modifier = Modifier.fillMaxSize().padding(paddingValues)) {
                    ScreenContent(currentScreen, viewModel)
                }
            }
        }
    }
}

@Composable
fun ScreenContent(
    screen: AppScreen,
    viewModel: NewsViewModel
) {
    when (screen) {
        AppScreen.HOME -> HomeScreen(viewModel)
        AppScreen.DAILY_BRIEFING -> DailyBriefingScreen(viewModel)
        AppScreen.AI_NEWS -> AiNewsScreen(viewModel)
        AppScreen.CATEGORIES -> CategoriesScreen(viewModel)
        AppScreen.COUNTRY_SELECTION -> CountrySelectionScreen(viewModel)
        AppScreen.ARTICLE_DETAIL -> ArticleDetailScreen(viewModel)
        AppScreen.SEARCH -> SearchScreen(viewModel)
        AppScreen.SAVED -> SavedNewsScreen(viewModel)
        AppScreen.NOTIFICATIONS -> NotificationsScreen(viewModel)
        AppScreen.SETTINGS -> SettingsScreen(viewModel)
        AppScreen.ADMIN_DASHBOARD -> AdminDashboardScreen(viewModel)
    }
}
