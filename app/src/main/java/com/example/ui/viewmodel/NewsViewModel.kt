package com.example.ui.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.local.AppDatabase
import com.example.data.model.AdminMetrics
import com.example.data.model.AppNotification
import com.example.data.model.DailyBriefing
import com.example.data.model.NewsArticle
import com.example.data.model.NewsSource
import com.example.data.model.UserPreferences
import com.example.data.repository.NewsRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import org.json.JSONArray

enum class AppScreen {
    HOME,
    DAILY_BRIEFING,
    AI_NEWS,
    CATEGORIES,
    COUNTRY_SELECTION,
    ARTICLE_DETAIL,
    SEARCH,
    SAVED,
    NOTIFICATIONS,
    SETTINGS,
    ADMIN_DASHBOARD
}

data class PipelineState(
    val isRunning: Boolean = false,
    val currentStep: String = "",
    val progress: Float = 0f,
    val completedMessage: String? = null
)

class NewsViewModel(application: Application) : AndroidViewModel(application) {

    private val repository: NewsRepository

    init {
        val db = AppDatabase.getDatabase(application)
        repository = NewsRepository(db.newsDao())
        viewModelScope.launch {
            repository.initializeIfEmpty()
        }
    }

    // --- Navigation & Active State ---
    private val _currentScreen = MutableStateFlow(AppScreen.HOME)
    val currentScreen: StateFlow<AppScreen> = _currentScreen.asStateFlow()

    private val _selectedArticle = MutableStateFlow<NewsArticle?>(null)
    val selectedArticle: StateFlow<NewsArticle?> = _selectedArticle.asStateFlow()

    private val _selectedCategory = MutableStateFlow("Top News")
    val selectedCategory: StateFlow<String> = _selectedCategory.asStateFlow()

    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    private val _searchCategoryFilter = MutableStateFlow<String?>(null)
    val searchCategoryFilter: StateFlow<String?> = _searchCategoryFilter.asStateFlow()

    private val _searchCountryFilter = MutableStateFlow<String?>(null)
    val searchCountryFilter: StateFlow<String?> = _searchCountryFilter.asStateFlow()

    private val _pipelineState = MutableStateFlow(PipelineState())
    val pipelineState: StateFlow<PipelineState> = _pipelineState.asStateFlow()

    private val _isAiSummarizing = MutableStateFlow(false)
    val isAiSummarizing: StateFlow<Boolean> = _isAiSummarizing.asStateFlow()

    // --- Core Repository Flows ---
    val allArticles: StateFlow<List<NewsArticle>> = repository.allArticles
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val breakingNews: StateFlow<List<NewsArticle>> = repository.breakingNews
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val bookmarkedArticles: StateFlow<List<NewsArticle>> = repository.bookmarkedArticles
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val trendingArticles: StateFlow<List<NewsArticle>> = repository.trendingArticles
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val aiAndTechArticles: StateFlow<List<NewsArticle>> = repository.aiAndTechArticles
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val latestDailyBriefing: StateFlow<DailyBriefing?> = repository.latestDailyBriefing
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)

    val userPreferences: StateFlow<UserPreferences> = repository.userPreferences
        .combine(MutableStateFlow(UserPreferences())) { pref, def -> pref ?: def }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), UserPreferences())

    val newsSources: StateFlow<List<NewsSource>> = repository.newsSources
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val notifications: StateFlow<List<AppNotification>> = repository.notifications
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val adminMetrics: StateFlow<AdminMetrics?> = repository.adminMetrics
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)

    // Personalized feed combining user's country, state, and followed topics
    val personalizedFeed: StateFlow<List<NewsArticle>> = combine(
        allArticles,
        userPreferences
    ) { articles, prefs ->
        val followedCategories = try {
            val arr = JSONArray(prefs.followedCategories)
            (0 until arr.length()).map { arr.getString(it) }
        } catch (e: Exception) {
            listOf("Top News", "Artificial Intelligence", "Technology", "Cricket")
        }

        articles.filter { article ->
            article.country.equals(prefs.primaryCountry, ignoreCase = true) ||
            article.region.equals(prefs.primaryRegion, ignoreCase = true) ||
            followedCategories.any { it.equals(article.category, ignoreCase = true) } ||
            article.isBreaking ||
            article.importanceScore >= 0.90
        }.sortedByDescending { it.importanceScore }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Filtered search results
    val searchResults: StateFlow<List<NewsArticle>> = combine(
        allArticles,
        searchQuery,
        searchCategoryFilter,
        searchCountryFilter
    ) { articles, query, catFilter, countryFilter ->
        if (query.isBlank() && catFilter == null && countryFilter == null) {
            return@combine emptyList()
        }
        articles.filter { article ->
            val matchesQuery = query.isBlank() ||
                article.title.contains(query, ignoreCase = true) ||
                article.description.contains(query, ignoreCase = true) ||
                article.source.contains(query, ignoreCase = true) ||
                article.category.contains(query, ignoreCase = true) ||
                article.tags.contains(query, ignoreCase = true) ||
                article.country.contains(query, ignoreCase = true) ||
                article.region.contains(query, ignoreCase = true)

            val matchesCategory = catFilter == null || article.category.equals(catFilter, ignoreCase = true)
            val matchesCountry = countryFilter == null || article.country.equals(countryFilter, ignoreCase = true)

            matchesQuery && matchesCategory && matchesCountry
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // --- Actions & Intents ---

    fun navigateTo(screen: AppScreen) {
        _currentScreen.value = screen
    }

    fun openArticle(article: NewsArticle) {
        _selectedArticle.value = article
        _currentScreen.value = AppScreen.ARTICLE_DETAIL
    }

    fun selectCategory(category: String) {
        _selectedCategory.value = category
    }

    fun updateSearchQuery(query: String) {
        _searchQuery.value = query
    }

    fun setSearchCategoryFilter(category: String?) {
        _searchCategoryFilter.value = category
    }

    fun setSearchCountryFilter(country: String?) {
        _searchCountryFilter.value = country
    }

    fun toggleBookmark(article: NewsArticle) {
        viewModelScope.launch {
            val newStatus = !article.isBookmarked
            repository.toggleBookmark(article.id, newStatus)
            if (_selectedArticle.value?.id == article.id) {
                _selectedArticle.value = _selectedArticle.value?.copy(isBookmarked = newStatus)
            }
        }
    }

    fun generateAiSummaryForArticle(article: NewsArticle) {
        viewModelScope.launch {
            _isAiSummarizing.value = true
            try {
                val updated = repository.generateOrFetchAiSummary(article)
                _selectedArticle.value = updated
            } finally {
                _isAiSummarizing.value = false
            }
        }
    }

    fun trigger630AmBriefingUpdate() {
        viewModelScope.launch {
            _pipelineState.value = PipelineState(isRunning = true, currentStep = "Starting 6:30 AM Pipeline...", progress = 0.05f)
            repository.trigger630AmPipeline { step, progress ->
                _pipelineState.value = PipelineState(
                    isRunning = progress < 1.0f,
                    currentStep = step,
                    progress = progress,
                    completedMessage = if (progress >= 1.0f) "6:30 AM Morning Brief successfully refreshed!" else null
                )
            }
        }
    }

    fun clearPipelineMessage() {
        _pipelineState.value = _pipelineState.value.copy(completedMessage = null)
    }

    fun setPrimaryCountryAndRegion(country: String, region: String) {
        viewModelScope.launch {
            val current = userPreferences.value
            val currentSaved = try {
                val arr = JSONArray(current.savedCountries)
                val list = (0 until arr.length()).map { arr.getString(it) }.toMutableList()
                if (!list.contains(country)) list.add(0, country)
                JSONArray(list).toString()
            } catch (e: Exception) {
                JSONArray(listOf(country, "India", "United States")).toString()
            }

            val updated = current.copy(
                primaryCountry = country,
                primaryRegion = region,
                savedCountries = currentSaved
            )
            repository.updateUserPreferences(updated)
            // Refresh morning briefing for the new country/region
            repository.trigger630AmPipeline()
        }
    }

    fun toggleFollowedCategory(category: String) {
        viewModelScope.launch {
            val current = userPreferences.value
            val list = try {
                val arr = JSONArray(current.followedCategories)
                (0 until arr.length()).map { arr.getString(it) }.toMutableList()
            } catch (e: Exception) {
                mutableListOf("Top News", "Artificial Intelligence", "Technology")
            }

            if (list.contains(category)) {
                if (list.size > 1) list.remove(category)
            } else {
                list.add(category)
            }

            val updated = current.copy(followedCategories = JSONArray(list).toString())
            repository.updateUserPreferences(updated)
        }
    }

    fun updateBriefingTime(time: String) {
        viewModelScope.launch {
            val current = userPreferences.value
            val updated = current.copy(morningBriefTime = time)
            repository.updateUserPreferences(updated)
        }
    }

    fun updateTimezone(timezone: String) {
        viewModelScope.launch {
            val current = userPreferences.value
            val updated = current.copy(userTimezone = timezone)
            repository.updateUserPreferences(updated)
            repository.trigger630AmPipeline()
        }
    }

    fun updateThemeMode(theme: String) {
        viewModelScope.launch {
            val current = userPreferences.value
            val updated = current.copy(themeMode = theme)
            repository.updateUserPreferences(updated)
        }
    }

    fun updateFontSize(size: String) {
        viewModelScope.launch {
            val current = userPreferences.value
            val updated = current.copy(fontSize = size)
            repository.updateUserPreferences(updated)
        }
    }

    fun updateNotificationToggles(
        enabled: Boolean,
        breaking: Boolean,
        morning: Boolean,
        ai: Boolean
    ) {
        viewModelScope.launch {
            val current = userPreferences.value
            val updated = current.copy(
                enableNotifications = enabled,
                enableBreakingNewsAlerts = breaking,
                enableMorningBriefAlerts = morning,
                enableAiAlerts = ai
            )
            repository.updateUserPreferences(updated)
        }
    }

    fun markNotificationRead(id: String) {
        viewModelScope.launch {
            repository.markNotificationAsRead(id)
        }
    }

    fun markAllNotificationsRead() {
        viewModelScope.launch {
            repository.markAllNotificationsAsRead()
        }
    }

    fun toggleNewsSource(id: String, isEnabled: Boolean) {
        viewModelScope.launch {
            repository.toggleNewsSource(id, isEnabled)
        }
    }
}
