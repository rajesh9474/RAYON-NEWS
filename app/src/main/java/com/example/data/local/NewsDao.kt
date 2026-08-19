package com.example.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.data.model.AdminMetrics
import com.example.data.model.AppNotification
import com.example.data.model.DailyBriefing
import com.example.data.model.NewsArticle
import com.example.data.model.NewsSource
import com.example.data.model.UserPreferences
import kotlinx.coroutines.flow.Flow

@Dao
interface NewsDao {

    // --- News Articles ---
    @Query("SELECT * FROM news_articles ORDER BY publishedAt DESC")
    fun getAllArticles(): Flow<List<NewsArticle>>

    @Query("SELECT * FROM news_articles WHERE id = :id LIMIT 1")
    suspend fun getArticleById(id: String): NewsArticle?

    @Query("SELECT * FROM news_articles WHERE isBreaking = 1 ORDER BY publishedAt DESC")
    fun getBreakingNews(): Flow<List<NewsArticle>>

    @Query("SELECT * FROM news_articles WHERE isBookmarked = 1 ORDER BY publishedAt DESC")
    fun getBookmarkedArticles(): Flow<List<NewsArticle>>

    @Query("SELECT * FROM news_articles WHERE category = :category ORDER BY importanceScore DESC, publishedAt DESC")
    fun getArticlesByCategory(category: String): Flow<List<NewsArticle>>

    @Query("SELECT * FROM news_articles WHERE category IN ('Artificial Intelligence', 'Technology') ORDER BY importanceScore DESC, publishedAt DESC")
    fun getAiAndTechArticles(): Flow<List<NewsArticle>>

    @Query("SELECT * FROM news_articles WHERE country = :country ORDER BY importanceScore DESC, publishedAt DESC")
    fun getArticlesByCountry(country: String): Flow<List<NewsArticle>>

    @Query("SELECT * FROM news_articles WHERE region = :region ORDER BY importanceScore DESC, publishedAt DESC")
    fun getArticlesByRegion(region: String): Flow<List<NewsArticle>>

    @Query("SELECT * FROM news_articles ORDER BY trendingScore DESC, publishedAt DESC LIMIT :limit")
    fun getTrendingArticles(limit: Int = 20): Flow<List<NewsArticle>>

    @Query("""
        SELECT * FROM news_articles 
        WHERE title LIKE '%' || :query || '%' 
           OR description LIKE '%' || :query || '%' 
           OR source LIKE '%' || :query || '%' 
           OR category LIKE '%' || :query || '%'
           OR country LIKE '%' || :query || '%'
           OR region LIKE '%' || :query || '%'
           OR tags LIKE '%' || :query || '%'
        ORDER BY importanceScore DESC, publishedAt DESC
    """)
    fun searchArticles(query: String): Flow<List<NewsArticle>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertArticles(articles: List<NewsArticle>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertArticle(article: NewsArticle)

    @Update
    suspend fun updateArticle(article: NewsArticle)

    @Query("UPDATE news_articles SET isBookmarked = :isBookmarked WHERE id = :id")
    suspend fun setBookmark(id: String, isBookmarked: Boolean)

    @Query("UPDATE news_articles SET aiSummary30Sec = :summary30Sec, aiKeyPoints = :keyPoints, aiWhyItMatters = :whyItMatters WHERE id = :id")
    suspend fun updateAiSummary(id: String, summary30Sec: String, keyPoints: String, whyItMatters: String)

    @Query("SELECT COUNT(*) FROM news_articles")
    suspend fun getArticlesCount(): Int

    // --- Daily Briefing ---
    @Query("SELECT * FROM daily_briefings ORDER BY generatedAt DESC LIMIT 1")
    fun getLatestDailyBriefing(): Flow<DailyBriefing?>

    @Query("SELECT * FROM daily_briefings WHERE id = :id LIMIT 1")
    suspend fun getBriefingById(id: String): DailyBriefing?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertDailyBriefing(briefing: DailyBriefing)

    // --- User Preferences ---
    @Query("SELECT * FROM user_preferences WHERE id = 1 LIMIT 1")
    fun getUserPreferencesFlow(): Flow<UserPreferences?>

    @Query("SELECT * FROM user_preferences WHERE id = 1 LIMIT 1")
    suspend fun getUserPreferences(): UserPreferences?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrUpdatePreferences(preferences: UserPreferences)

    // --- News Sources ---
    @Query("SELECT * FROM news_sources ORDER BY reliabilityScore DESC")
    fun getAllSources(): Flow<List<NewsSource>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSources(sources: List<NewsSource>)

    @Query("UPDATE news_sources SET isEnabled = :isEnabled WHERE id = :id")
    suspend fun toggleSourceEnabled(id: String, isEnabled: Boolean)

    // --- Notifications ---
    @Query("SELECT * FROM app_notifications ORDER BY timestamp DESC")
    fun getNotifications(): Flow<List<AppNotification>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertNotification(notification: AppNotification)

    @Query("UPDATE app_notifications SET isRead = 1 WHERE id = :id")
    suspend fun markNotificationAsRead(id: String)

    @Query("UPDATE app_notifications SET isRead = 1")
    suspend fun markAllNotificationsAsRead()

    // --- Admin Metrics ---
    @Query("SELECT * FROM admin_metrics WHERE id = 1 LIMIT 1")
    fun getAdminMetrics(): Flow<AdminMetrics?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun updateAdminMetrics(metrics: AdminMetrics)
}
