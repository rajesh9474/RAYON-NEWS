package com.example.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "news_articles")
data class NewsArticle(
    @PrimaryKey val id: String,
    val title: String,
    val description: String,
    val summary: String,
    val url: String,
    val imageUrl: String,
    val source: String,
    val sourceLogoUrl: String = "",
    val author: String = "Newsroom Staff",
    val publishedAt: Long = System.currentTimeMillis(),
    val country: String = "India",
    val region: String = "Tamil Nadu",
    val category: String = "Top News",
    val tags: String = "", // Comma-separated or JSON
    val language: String = "en",
    val readingTime: Int = 3, // in minutes
    val importanceScore: Double = 0.85,
    val trendingScore: Double = 0.75,
    val clusterId: String? = null,
    val clusterCount: Int = 1,
    val relatedSources: String = "[]", // JSON array of source names e.g. ["BBC", "The Hindu", "Reuters"]
    val isBreaking: Boolean = false,
    val isBookmarked: Boolean = false,
    val whyRecommended: String = "Top story for your region and interests",
    val aiSummary30Sec: String = "",
    val aiKeyPoints: String = "", // Multi-line or JSON
    val aiWhyItMatters: String = "",
    val createdAt: Long = System.currentTimeMillis(),
    val updatedAt: Long = System.currentTimeMillis()
)
