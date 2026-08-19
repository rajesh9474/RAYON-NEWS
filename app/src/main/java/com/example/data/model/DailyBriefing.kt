package com.example.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "daily_briefings")
data class DailyBriefing(
    @PrimaryKey val id: String, // e.g. "brief_2026-08-18_Asia/Kolkata"
    val dateStr: String, // e.g. "August 19, 2026"
    val targetTimezone: String, // e.g. "Asia/Kolkata"
    val generatedAt: Long = System.currentTimeMillis(),
    val worldArticleIds: String = "[]", // JSON list of article IDs
    val countryArticleIds: String = "[]",
    val stateArticleIds: String = "[]",
    val aiTechArticleIds: String = "[]",
    val sportsArticleIds: String = "[]",
    val trendingArticleIds: String = "[]",
    val greetingMessage: String = "Good Morning, Rajesh 👋 Here is today's 5-minute brief.",
    val keyTakeaways: String = "",
    val readMinutesEstimate: Int = 5
)
