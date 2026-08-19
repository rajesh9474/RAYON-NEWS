package com.example.data.model

data class NewsCategory(
    val id: String,
    val name: String,
    val iconEmoji: String,
    val description: String,
    val colorHex: Long = 0xFF2563EB
)

object NewsCategories {
    val ALL = listOf(
        NewsCategory("top", "Top News", "🔥", "Top headlines and essential coverage from around the world"),
        NewsCategory("world", "World", "🌍", "International geopolitics, global events, and diplomatic affairs"),
        NewsCategory("india", "India", "🇮🇳", "National headlines, policy decisions, and events across India"),
        NewsCategory("state", "State/Regional", "📍", "Local reporting tailored to your selected state or province"),
        NewsCategory("politics", "Politics", "📰", "Government policies, elections, legislative debates, and governance"),
        NewsCategory("business", "Business", "💼", "Corporate updates, mergers, commerce, and enterprise trends"),
        NewsCategory("finance", "Finance", "💰", "Personal finance, banking, interest rates, and macroeconomics"),
        NewsCategory("stock_market", "Stock Market", "📈", "Indices, earnings reports, trading updates, and equity markets"),
        NewsCategory("tech", "Technology", "💻", "Hardware, software, consumer tech, internet culture, and cybersecurity"),
        NewsCategory("ai", "Artificial Intelligence", "🤖", "LLMs, generative models, OpenAI, Anthropic, Gemini, and AI ethics"),
        NewsCategory("science", "Science", "🧪", "Physics, biology, scientific discoveries, and breakthrough research"),
        NewsCategory("space", "Space", "🚀", "ISRO, NASA, astronomy, rocket launches, and cosmological exploration"),
        NewsCategory("sports", "Sports", "🏆", "Athletics, tournaments, championship matches, and sports news"),
        NewsCategory("cricket", "Cricket", "🏏", "IPL, ICC World Cup, test matches, series updates, and analysis"),
        NewsCategory("football", "Football", "⚽", "Champions League, Premier League, FIFA, transfers, and match reports"),
        NewsCategory("entertainment", "Entertainment", "🎬", "Pop culture, music releases, celebrity updates, and awards"),
        NewsCategory("movies", "Movies", "🍿", "Cinema, box office figures, reviews, and upcoming theatrical releases"),
        NewsCategory("gaming", "Gaming", "🎮", "Console, PC, eSports, studio updates, and indie game releases"),
        NewsCategory("health", "Health", "🏥", "Medicine, public wellness, nutrition, clinical research, and longevity"),
        NewsCategory("education", "Education", "🎓", "Universities, research funding, edtech, and student opportunities"),
        NewsCategory("environment", "Environment", "🌱", "Climate action, renewable energy, conservation, and sustainability"),
        NewsCategory("startup", "Startup", "🦄", "Venture capital, funding rounds, founders, and unicorn scale-ups"),
        NewsCategory("travel", "Travel", "✈️", "Destinations, tourism trends, hospitality, and aviation updates"),
        NewsCategory("lifestyle", "Lifestyle", "✨", "Design, culture, productivity, living well, and cuisine"),
        NewsCategory("trending", "Trending", "⚡", "Rapidly viral stories, multi-source conversations, and discussions"),
        NewsCategory("breaking", "Breaking News", "🚨", "Urgent unfolding events with real-time continuous updates")
    )

    fun getById(id: String): NewsCategory {
        return ALL.firstOrNull { it.id.equals(id, ignoreCase = true) }
            ?: ALL.first()
    }

    fun getByName(name: String): NewsCategory {
        return ALL.firstOrNull { it.name.equals(name, ignoreCase = true) }
            ?: ALL.first()
    }
}
