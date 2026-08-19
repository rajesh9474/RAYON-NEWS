package com.example.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "user_preferences")
data class UserPreferences(
    @PrimaryKey val id: Int = 1,
    val userName: String = "Rajesh",
    val userEmail: String = "rrajeshsk555@gmail.com",
    val primaryCountry: String = "India",
    val savedCountries: String = "[\"India\",\"United States\",\"United Kingdom\"]", // JSON list
    val primaryRegion: String = "Tamil Nadu",
    val followedCategories: String = "[\"Top News\",\"Artificial Intelligence\",\"Technology\",\"Cricket\",\"Business\",\"Space\"]", // JSON list
    val morningBriefTime: String = "06:30",
    val userTimezone: String = "Asia/Kolkata",
    val enableNotifications: Boolean = true,
    val enableBreakingNewsAlerts: Boolean = true,
    val enableMorningBriefAlerts: Boolean = true,
    val enableAiAlerts: Boolean = true,
    val themeMode: String = "SYSTEM", // SYSTEM, LIGHT, DARK
    val fontSize: String = "MEDIUM", // SMALL, MEDIUM, LARGE
    val preferredLanguage: String = "English",
    val isGuest: Boolean = false
)

@Entity(tableName = "news_sources")
data class NewsSource(
    @PrimaryKey val id: String,
    val name: String,
    val logoUrl: String = "",
    val domain: String = "",
    val reliabilityScore: Double = 0.95,
    val isEnabled: Boolean = true,
    val category: String = "General",
    val country: String = "Global"
)

@Entity(tableName = "app_notifications")
data class AppNotification(
    @PrimaryKey val id: String,
    val title: String,
    val body: String,
    val timestamp: Long = System.currentTimeMillis(),
    val type: String = "MORNING_BRIEF", // MORNING_BRIEF, BREAKING, AI, TOPIC
    val articleId: String? = null,
    val isRead: Boolean = false
)

@Entity(tableName = "admin_metrics")
data class AdminMetrics(
    @PrimaryKey val id: Int = 1,
    val totalArticlesIngested: Int = 128,
    val totalArticlesProcessed: Int = 128,
    val clustersFound: Int = 24,
    val lastPipelineRun: Long = System.currentTimeMillis(),
    val activeUsers: Int = 1420,
    val apiRequestsToday: Int = 384,
    val briefingGeneratedCount: Int = 18,
    val pipelineStatus: String = "Idle - Next scheduled at 06:30 AM",
    val logs: String = "[\"6:30 AM Pipeline completed successfully for Asia/Kolkata\",\"Deduplication algorithm grouped 38 duplicate stories into 12 clusters\",\"Gemini 3.5 Flash summarized 15 top articles\",\"Delivered morning briefing push notifications to active subscribers\"]"
)
