package com.example.data.remote

import com.example.data.model.DailyBriefing
import com.example.data.model.NewsArticle
import com.example.data.model.NewsSource
import org.json.JSONArray
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.TimeZone

object NewsIngestionService {

    fun getDefaultNewsSources(): List<NewsSource> {
        return listOf(
            NewsSource("src_reuters", "Reuters", "https://logo.clearbit.com/reuters.com", "reuters.com", 0.98, true, "World", "Global"),
            NewsSource("src_ap", "Associated Press", "https://logo.clearbit.com/apnews.com", "apnews.com", 0.98, true, "World", "Global"),
            NewsSource("src_thehindu", "The Hindu", "https://logo.clearbit.com/thehindu.com", "thehindu.com", 0.96, true, "National", "India"),
            NewsSource("src_bbc", "BBC News", "https://logo.clearbit.com/bbc.com", "bbc.com", 0.97, true, "World", "United Kingdom"),
            NewsSource("src_bloomberg", "Bloomberg", "https://logo.clearbit.com/bloomberg.com", "bloomberg.com", 0.97, true, "Business", "Global"),
            NewsSource("src_techcrunch", "TechCrunch", "https://logo.clearbit.com/techcrunch.com", "techcrunch.com", 0.95, true, "Technology", "United States"),
            NewsSource("src_theverge", "The Verge", "https://logo.clearbit.com/theverge.com", "theverge.com", 0.94, true, "Technology", "United States"),
            NewsSource("src_nature", "Nature Journal", "https://logo.clearbit.com/nature.com", "nature.com", 0.99, true, "Science", "Global"),
            NewsSource("src_espn", "ESPN", "https://logo.clearbit.com/espn.com", "espn.com", 0.95, true, "Sports", "United States"),
            NewsSource("src_cricbuzz", "Cricbuzz", "https://logo.clearbit.com/cricbuzz.com", "cricbuzz.com", 0.96, true, "Cricket", "India"),
            NewsSource("src_ndtv", "NDTV", "https://logo.clearbit.com/ndtv.com", "ndtv.com", 0.93, true, "National", "India"),
            NewsSource("src_nyt", "The New York Times", "https://logo.clearbit.com/nytimes.com", "nytimes.com", 0.96, true, "World", "United States"),
            NewsSource("src_ft", "Financial Times", "https://logo.clearbit.com/ft.com", "ft.com", 0.97, true, "Finance", "United Kingdom"),
            NewsSource("src_timesofindia", "Times of India", "https://logo.clearbit.com/timesofindia.indiatimes.com", "timesofindia.com", 0.92, true, "Regional", "India")
        )
    }

    fun getInitialSeedArticles(): List<NewsArticle> {
        val now = System.currentTimeMillis()
        val hour = 3600 * 1000L

        return listOf(
            // --- BREAKING NEWS ---
            NewsArticle(
                id = "art_break_01",
                title = "Global Climate Summit Enacts Historic Zero-Methane Treaty Across 140 Nations",
                description = "World leaders reach an unexpected breakthrough at the UN Climate Assembly, enacting legally binding methane reduction targets with $120B fund.",
                summary = "An unprecedented international coalition concluded talks in Geneva today, establishing strict industrial methane caps alongside an immediate green transition fund.",
                url = "https://www.reuters.com/world/climate-zero-methane-treaty-2026",
                imageUrl = "https://images.unsplash.com/photo-1611273426858-450d8e3c9fce?auto=format&fit=crop&w=800&q=80",
                source = "Reuters",
                author = "Elena Rostova",
                publishedAt = now - 25 * 60 * 1000L,
                country = "Global",
                region = "Worldwide",
                category = "Breaking News",
                tags = "Climate, UN, Methane, Global Policy, Environment",
                readingTime = 4,
                importanceScore = 0.99,
                trendingScore = 0.98,
                clusterId = "cluster_climate_treaty",
                clusterCount = 14,
                relatedSources = """["Reuters", "BBC News", "Associated Press", "The Guardian", "The Hindu", "Bloomberg"]""",
                isBreaking = true,
                whyRecommended = "🔴 Unfolding breaking news with widespread international impact",
                aiSummary30Sec = "140 nations have signed a landmark zero-methane emissions pact in Geneva. A dedicated $120 billion fund has been allocated to accelerate green industrial modernization worldwide.",
                aiKeyPoints = "• 140 countries signed legally binding industrial emission caps.\n• Establishes a $120B transitional fund for developing economies.\n• Enforcement monitoring begins with satellite tracking in Q4.",
                aiWhyItMatters = "Methane traps significantly more atmospheric heat than CO2 in the near term; rapid reduction represents the fastest single lever for stabilizing extreme weather anomalies."
            ),
            NewsArticle(
                id = "art_break_02",
                title = "ISRO & NASA Launch Joint High-Resolution Hyperspectral Earth Observation Satellite",
                description = "The joint NISAR-2 observatory lifted off smoothly from Sriharikota, commencing an ambitious 5-year planetary surface monitoring mission.",
                summary = "From the Satish Dhawan Space Centre in Sriharikota, the advanced dual-frequency radar satellite achieved precise orbital insertion at 6:15 AM.",
                url = "https://www.thehindu.com/sci-tech/science/isro-nasa-nisar-launch",
                imageUrl = "https://images.unsplash.com/photo-1517976487507-5b3a4a984033?auto=format&fit=crop&w=800&q=80",
                source = "The Hindu",
                author = "S. Ramanathan",
                publishedAt = now - 45 * 60 * 1000L,
                country = "India",
                region = "Andhra Pradesh",
                category = "Breaking News",
                tags = "ISRO, NASA, Space, Sriharikota, Science, Satellite",
                readingTime = 3,
                importanceScore = 0.98,
                trendingScore = 0.96,
                clusterId = "cluster_space_nisar",
                clusterCount = 9,
                relatedSources = """["The Hindu", "NDTV", "BBC News", "SpaceNews", "Times of India"]""",
                isBreaking = true,
                whyRecommended = "🔴 Major scientific breakthrough in India & Space Exploration",
                aiSummary30Sec = "ISRO and NASA successfully launched NISAR-2 from Sriharikota to map global tectonic, agrarian, and glacial shifts with sub-centimeter accuracy.",
                aiKeyPoints = "• Dual S-band and L-band radar sensors deploy on orbit.\n• Full global Earth surface scans every 12 days.\n• Provides open-access climate and agricultural data.",
                aiWhyItMatters = "Directly improves disaster preparedness, flood management, and crop yield forecasting across South Asia and North America."
            ),

            // --- AI & TECHNOLOGY ---
            NewsArticle(
                id = "art_ai_01",
                title = "Next-Generation Multimodal AI Agents Demonstrate Real-Time Software Synthesis and Execution",
                description = "New benchmark studies highlight autonomous developer agents capable of refactoring legacy architectures and building cross-platform apps in minutes.",
                summary = "Researchers across leading AI laboratories showcased autonomous agentic coding frameworks that integrate formal verification, dynamic test generation, and seamless UI styling.",
                url = "https://techcrunch.com/2026/08/ai-agents-software-architecture",
                imageUrl = "https://images.unsplash.com/photo-1677442136019-21780ecad995?auto=format&fit=crop&w=800&q=80",
                source = "TechCrunch",
                author = "Sarah Perez",
                publishedAt = now - 1 * hour,
                country = "Global",
                region = "Worldwide",
                category = "Artificial Intelligence",
                tags = "AI, LLMs, Agents, Software Engineering, Developer Tools, Machine Learning",
                readingTime = 5,
                importanceScore = 0.96,
                trendingScore = 0.95,
                clusterId = "cluster_ai_agents",
                clusterCount = 12,
                relatedSources = """["TechCrunch", "The Verge", "VentureBeat", "Ars Technica", "Wired"]""",
                whyRecommended = "Recommended because you follow Artificial Intelligence & Technology",
                aiSummary30Sec = "AI coding agents have reached a critical milestone in formal self-testing and real-time app generation. Developers are transitioning to higher-level architectural oversight.",
                aiKeyPoints = "• Context windows and reasoning depth surpass previous code benchmarks by 40%.\n• Agents autonomously build, test, and repair runtime edge cases.\n• Standardizes enterprise safety sandboxes for production deployment.",
                aiWhyItMatters = "Dramatically lowers the barrier for software creation while shifting engineering roles toward systems design and verification."
            ),
            NewsArticle(
                id = "art_ai_02",
                title = "Open-Source AI Consortia Release Ultra-Efficient 4-Bit Neural Architectures for Mobile Devices",
                description = "On-device AI leaps forward as lightweight models run high-fidelity generative intelligence locally on smartphones without battery drain.",
                summary = "A coalition of open-source AI teams published optimized weights achieving GPT-4 class reasoning on consumer chipsets with sub-5-watt power budgets.",
                url = "https://www.theverge.com/2026/mobile-ondevice-ai-efficiency",
                imageUrl = "https://images.unsplash.com/photo-1618005182384-a83a8bd57fbe?auto=format&fit=crop&w=800&q=80",
                source = "The Verge",
                author = "Alex Heath",
                publishedAt = now - 3 * hour,
                country = "United States",
                region = "California",
                category = "Artificial Intelligence",
                tags = "AI, Mobile, On-Device, Silicon, Quantization, Edge Computing",
                readingTime = 4,
                importanceScore = 0.93,
                trendingScore = 0.91,
                clusterId = "cluster_ondevice_ai",
                clusterCount = 8,
                relatedSources = """["The Verge", "TechCrunch", "AnandTech", "Tom's Hardware"]""",
                whyRecommended = "Recommended because you follow Artificial Intelligence",
                aiSummary30Sec = "New 4-bit neural architectures allow advanced generative models to execute entirely on smartphones with near-zero latency and high privacy.",
                aiKeyPoints = "• Models operate within 3GB RAM footprint with full offline capability.\n• Privacy-first on-device execution removes cloud API dependencies.\n• Hardware vendors announce day-one NPU acceleration support.",
                aiWhyItMatters = "Enables private, instant assistant experiences without sending sensitive personal data over cellular networks."
            ),
            NewsArticle(
                id = "art_ai_03",
                title = "European Union and G7 Finalize Comprehensive Global AI Safety Testing Standards",
                description = "International regulators establish unified safety benchmarks, red-teaming criteria, and watermarking mandates for frontier frontier models.",
                summary = "The joint regulatory framework mandates rigorous safety verification, provenance labeling, and synthetic media watermarking across all member nations.",
                url = "https://www.reuters.com/technology/eu-g7-ai-safety-accord",
                imageUrl = "https://images.unsplash.com/photo-1526374965328-7f61d4dc18c5?auto=format&fit=crop&w=800&q=80",
                source = "Reuters",
                author = "Foo Yun Chee",
                publishedAt = now - 5 * hour,
                country = "Global",
                region = "Europe",
                category = "Artificial Intelligence",
                tags = "AI Regulation, Governance, EU, G7, Safety, Policy",
                readingTime = 4,
                importanceScore = 0.94,
                trendingScore = 0.88,
                clusterId = "cluster_ai_governance",
                clusterCount = 11,
                relatedSources = """["Reuters", "Financial Times", "Politico", "BBC News"]""",
                whyRecommended = "Recommended because you follow Artificial Intelligence & Politics",
                aiSummary30Sec = "The EU and G7 have harmonized safety guidelines for frontier AI systems, introducing standardized compliance audits and watermarking requirements.",
                aiKeyPoints = "• Establishes joint cross-border evaluation labs.\n• Mandates cryptographically verifiable provenance for synthetic media.\n• Streamlines compliance for startups adhering to open standards.",
                aiWhyItMatters = "Prevents regulatory fragmentation and gives developers clear compliance roadmaps for enterprise deployment."
            ),

            // --- INDIA & STATE / REGIONAL ---
            NewsArticle(
                id = "art_in_01",
                title = "India Unveils $15 Billion National Semiconductor Fabrication Corridor in Tamil Nadu & Gujarat",
                description = "Prime Minister announces groundbreaking for three advanced 3nm chip fabrication and packaging facilities, creating 80,000 high-tech jobs.",
                summary = "The ambitious project bridges academic research hubs in Chennai and Ahmedabad with state-of-the-art silicon cleanrooms backed by global tech leaders.",
                url = "https://www.thehindu.com/business/india-semiconductor-corridor-expansion",
                imageUrl = "https://images.unsplash.com/photo-1518770660439-4636190af475?auto=format&fit=crop&w=800&q=80",
                source = "The Hindu",
                author = "K. Venkataramanan",
                publishedAt = now - 2 * hour,
                country = "India",
                region = "Tamil Nadu",
                category = "India",
                tags = "India, Tamil Nadu, Semiconductor, Tech, Manufacturing, Economy",
                readingTime = 4,
                importanceScore = 0.97,
                trendingScore = 0.94,
                clusterId = "cluster_india_semi",
                clusterCount = 15,
                relatedSources = """["The Hindu", "NDTV", "Economic Times", "Livemint", "Times of India", "Reuters"]""",
                whyRecommended = "Top story for your selected country (India) and state (Tamil Nadu)",
                aiSummary30Sec = "India has launched a $15 billion semiconductor corridor across Tamil Nadu and Gujarat to establish domestic silicon manufacturing and generate 80,000 jobs.",
                aiKeyPoints = "• Three fabrication and packaging plants funded via public-private partnerships.\n• Tamil Nadu ecosystem to focus on power electronics and auto microcontrollers.\n• Commercial wafer production slated for early 2028.",
                aiWhyItMatters = "Solidifies South Asia's position in global hardware manufacturing and secures strategic domestic supply chains."
            ),
            NewsArticle(
                id = "art_state_01",
                title = "Tamil Nadu Launches AI-Powered Agri-Intelligence Grid for 4 Million Farmers in Delta Region",
                description = "Chief Minister inaugurates Cauvery Delta precision water and crop analytics network, providing real-time soil and weather advisories.",
                summary = "The state initiative integrates drone telemetry, satellite weather models, and regional Tamil language AI voice alerts directly to cultivators' phones.",
                url = "https://www.thehindu.com/news/national/tamil-nadu/tn-agri-ai-grid-cauvery-delta",
                imageUrl = "https://images.unsplash.com/photo-1500937386664-56d1dfef3854?auto=format&fit=crop&w=800&q=80",
                source = "The Hindu",
                author = "B. Aravind",
                publishedAt = now - 4 * hour,
                country = "India",
                region = "Tamil Nadu",
                category = "State/Regional",
                tags = "Tamil Nadu, Chennai, Thanjavur, Agriculture, AI, Regional Governance",
                readingTime = 3,
                importanceScore = 0.95,
                trendingScore = 0.89,
                clusterId = "cluster_tn_agri",
                clusterCount = 6,
                relatedSources = """["The Hindu", "Dinamalar", "Times of India", "Deccan Chronicle"]""",
                whyRecommended = "📍 Dedicated regional news for your state (Tamil Nadu)",
                aiSummary30Sec = "Tamil Nadu has deployed an automated AI precision agriculture network across the Cauvery delta, sending hyper-local crop and irrigation guidance in Tamil.",
                aiKeyPoints = "• Covers 4 million agrarian households across 8 delta districts.\n• Uses local language voice bots accessible via simple phone calls.\n• Projects a 22% reduction in water usage and 15% yield improvement.",
                aiWhyItMatters = "Demonstrates grassroots impact of practical AI in climate resilience and rural economic empowerment."
            ),
            NewsArticle(
                id = "art_state_02",
                title = "Bengaluru Metro Phase 3 Gets Cabinet Clearance with Direct Electronic City Link",
                description = "Karnataka state cabinet greenlights 44-kilometer elevated corridor linking outer tech clusters with integrated rapid multimodal hubs.",
                summary = "The expansion will add 32 new stations, fully automated driverless rolling stock, and seamless interchange hubs across the southern tech belt.",
                url = "https://www.ndtv.com/karnataka-news/bengaluru-metro-phase-3-approved",
                imageUrl = "https://images.unsplash.com/photo-1513694203232-719a280e022f?auto=format&fit=crop&w=800&q=80",
                source = "NDTV",
                author = "Maya Sharma",
                publishedAt = now - 6 * hour,
                country = "India",
                region = "Karnataka",
                category = "State/Regional",
                tags = "Karnataka, Bengaluru, Metro, Infrastructure, Transit",
                readingTime = 3,
                importanceScore = 0.91,
                trendingScore = 0.86,
                clusterId = "cluster_blr_metro",
                clusterCount = 5,
                relatedSources = """["NDTV", "Deccan Herald", "The Hindu", "Bangalore Mirror"]""",
                whyRecommended = "📍 Regional infrastructure development in Karnataka",
                aiSummary30Sec = "Karnataka has approved the 44km Bengaluru Metro Phase 3 expansion to decongest electronic corridor bottlenecks with driverless train sets.",
                aiKeyPoints = "• 44km route with 32 state-of-the-art stations.\n• Direct links between Outer Ring Road and tech parks.\n• Project completion slated within 48 months.",
                aiWhyItMatters = "Addresses crucial urban transit challenges in India's leading technology and startup metropolis."
            ),

            // --- WORLD ---
            NewsArticle(
                id = "art_world_01",
                title = "Global Trade Corridors Shift as Automated Maritime Ports Cut Transpacific Shipping Latency by 40%",
                description = "Autonomous electric freight systems and digital customs clearing transform container logistics across Singapore, Rotterdam, and Los Angeles.",
                summary = "A comprehensive port modernization index released today shows average turn times dropping to historic lows as AI crane dispatching expands worldwide.",
                url = "https://www.bloomberg.com/news/articles/2026-global-shipping-automation",
                imageUrl = "https://images.unsplash.com/photo-1586528116311-ad8dd3c8310d?auto=format&fit=crop&w=800&q=80",
                source = "Bloomberg",
                author = "Ann Koh",
                publishedAt = now - 7 * hour,
                country = "Global",
                region = "Worldwide",
                category = "World",
                tags = "World, Trade, Shipping, Logistics, Economy, Supply Chain",
                readingTime = 4,
                importanceScore = 0.93,
                trendingScore = 0.88,
                clusterId = "cluster_global_trade",
                clusterCount = 10,
                relatedSources = """["Bloomberg", "Financial Times", "Reuters", "Wall Street Journal"]""",
                whyRecommended = "Top international commerce and logistics development",
                aiSummary30Sec = "Smart automated ports in key global shipping hubs have cut vessel turnaround times by 40%, easing international supply chain costs.",
                aiKeyPoints = "• AI-managed crane grids reduce container dwell times to under 18 hours.\n• Digital customs pipelines eliminate paperwork hold-ups.\n• Lowers carbon intensity per freight ton by 28%.",
                aiWhyItMatters = "Helps tame consumer price volatility by ensuring resilient, predictable global trade flow."
            ),
            NewsArticle(
                id = "art_world_02",
                title = "International Fusion Energy Consortium Achieves Sustained 15-Minute Net Energy Output",
                description = "Tokamak reactor facility in France maintains stable plasma equilibrium, marking the longest commercial fusion test run in physics history.",
                summary = "Physicists confirmed net energy generation exceeding input threshold for 900 uninterrupted seconds without wall degradation.",
                url = "https://www.nature.com/articles/fusion-energy-sustained-burn-2026",
                imageUrl = "https://images.unsplash.com/photo-1507413245164-6160d8298b31?auto=format&fit=crop&w=800&q=80",
                source = "Nature Journal",
                author = "Dr. Michael Chen",
                publishedAt = now - 8 * hour,
                country = "Global",
                region = "Europe",
                category = "Science",
                tags = "Science, Fusion, Physics, Energy, Clean Tech, Breakthrough",
                readingTime = 5,
                importanceScore = 0.98,
                trendingScore = 0.95,
                clusterId = "cluster_fusion_energy",
                clusterCount = 16,
                relatedSources = """["Nature Journal", "BBC News", "New Scientist", "Reuters", "The Hindu"]""",
                whyRecommended = "Monumental scientific breakthrough with global ramifications",
                aiSummary30Sec = "Researchers maintained a stable net-positive fusion plasma for 15 minutes in France, establishing proof of scalable clean fusion power.",
                aiKeyPoints = "• 15-minute steady plasma burn with Q-factor exceeding 1.25.\n• Superconducting magnetic coils maintained thermal equilibrium.\n• Paves the path for pilot commercial grid-connected fusion in the 2030s.",
                aiWhyItMatters = "Provides the definitive technical validation for practically limitless, carbon-free baseload electricity generation."
            ),

            // --- SPORTS & CRICKET ---
            NewsArticle(
                id = "art_sports_01",
                title = "India Clinches Thrilling T20 Decider in London with Last-Over Heroics",
                description = "Masterful bowling in the final over secures a dramatic 4-run victory in the championship series finale against England at Lord's.",
                summary = "With 12 runs needed off 6 deliveries, a sensational display of reverse swing and pinpoint yorkers clinched the historic trophy for the visitors.",
                url = "https://www.cricbuzz.com/cricket-news/india-tour-england-t20-final",
                imageUrl = "https://images.unsplash.com/photo-1531415074968-036ba1b575da?auto=format&fit=crop&w=800&q=80",
                source = "Cricbuzz",
                author = "Harsha Bhogle",
                publishedAt = now - 2 * hour,
                country = "India",
                region = "Worldwide",
                category = "Cricket",
                tags = "Cricket, India, England, T20, Sports, Lords",
                readingTime = 3,
                importanceScore = 0.95,
                trendingScore = 0.97,
                clusterId = "cluster_cricket_t20",
                clusterCount = 18,
                relatedSources = """["Cricbuzz", "ESPN", "The Hindu", "NDTV Sports", "BBC Sport"]""",
                whyRecommended = "Recommended because you follow Cricket & Sports",
                aiSummary30Sec = "India defended a competitive total at Lord's with exceptional death bowling, securing the bilateral T20 trophy in a nail-biting final over.",
                aiKeyPoints = "• Sealed victory by 4 runs in a high-scoring thriller.\n• Player of the match scored an unbeaten 78 off 42 balls.\n• Key bowling spell conceded just 4 runs in the 20th over.",
                aiWhyItMatters = "Bolsters India's top ranking in global T20 cricket ahead of the upcoming ICC tournament campaign."
            ),
            NewsArticle(
                id = "art_sports_02",
                title = "UEFA Champions League Semifinals Set Following Dramatic Extra-Time Comebacks",
                description = "Electrifying European football action as Real Madrid and Arsenal secure dramatic semifinal berths after grueling second-leg ties.",
                summary = "A pair of stoppage-time headers in Madrid and London capped an extraordinary week of European club football, setting up titanic final-four clashes.",
                url = "https://www.espn.com/soccer/story/_/id/champions-league-semifinal-wrap",
                imageUrl = "https://images.unsplash.com/photo-1508098682722-e99c43a406b2?auto=format&fit=crop&w=800&q=80",
                source = "ESPN",
                author = "Mark Ogden",
                publishedAt = now - 5 * hour,
                country = "Global",
                region = "Europe",
                category = "Football",
                tags = "Football, UEFA, Champions League, Arsenal, Real Madrid, Sports",
                readingTime = 4,
                importanceScore = 0.90,
                trendingScore = 0.92,
                clusterId = "cluster_ucl_semis",
                clusterCount = 11,
                relatedSources = """["ESPN", "BBC Sport", "Sky Sports", "The Athletic"]""",
                whyRecommended = "Top international sports story",
                aiSummary30Sec = "Arsenal and Real Madrid sealed dramatic Champions League semifinal spots following high-octane second-leg quarterfinal battles.",
                aiKeyPoints = "• Arsenal scored in extra-time stoppage to advance 4-3 on aggregate.\n• Real Madrid triumphed in a penalty shootout following a 2-2 draw.\n• Semifinals scheduled to kickoff next fortnight across Europe.",
                aiWhyItMatters = "Sets up one of the most anticipated final-four European matchups in modern club football history."
            ),

            // --- BUSINESS & STOCK MARKET ---
            NewsArticle(
                id = "art_biz_01",
                title = "Global Equities Rally as Inflation Metrics Cool and Central Banks Signal Rate Cuts",
                description = "Sensex surges 850 points while S&P 500 touches new record highs amidst buoyant tech earnings and stabilized energy indices.",
                summary = "Equity markets witnessed broad-based institutional buying across Asian and Western trading sessions following favorable macroeconomic price prints.",
                url = "https://www.ft.com/content/global-markets-inflation-rally-2026",
                imageUrl = "https://images.unsplash.com/photo-1590283603385-17ffb3a7f29f?auto=format&fit=crop&w=800&q=80",
                source = "Financial Times",
                author = "Katie Martin",
                publishedAt = now - 3 * hour,
                country = "Global",
                region = "Worldwide",
                category = "Stock Market",
                tags = "Stock Market, Business, Sensex, S&P 500, Economy, Finance",
                readingTime = 3,
                importanceScore = 0.92,
                trendingScore = 0.90,
                clusterId = "cluster_market_rally",
                clusterCount = 13,
                relatedSources = """["Financial Times", "Bloomberg", "Economic Times", "Livemint", "Reuters"]""",
                whyRecommended = "Recommended because you follow Business & Finance",
                aiSummary30Sec = "Global financial indices surged as headline inflation eased, fueling optimism for upcoming central bank interest rate reductions.",
                aiKeyPoints = "• Indian Sensex up 850 points led by IT, banking, and green energy stocks.\n• US and European benchmarks posted fresh multi-month records.\n• Benchmark bond yields dropped across 2-year and 10-year maturities.",
                aiWhyItMatters = "Signals improving liquidity conditions for business expansions, corporate capital expenditures, and consumer borrowing."
            ),
            NewsArticle(
                id = "art_biz_02",
                title = "Venture Capital Inflows in Clean Mobility & Solid-State Batteries Surpass $30 Billion",
                description = "Automotive scale-ups and battery chemistry startups secure massive growth rounds to bring 1,000km range EV batteries to mass market.",
                summary = "Next-generation energy storage investments hit an all-time record this quarter as commercial pilot manufacturing lines begin deployment.",
                url = "https://www.bloomberg.com/news/solid-state-battery-vc-funding",
                imageUrl = "https://images.unsplash.com/photo-1558441719-8b835e5d36e2?auto=format&fit=crop&w=800&q=80",
                source = "Bloomberg",
                author = "Edward Ludlow",
                publishedAt = now - 6 * hour,
                country = "Global",
                region = "Worldwide",
                category = "Startup",
                tags = "Startup, EV, Clean Energy, Batteries, Venture Capital, Tech",
                readingTime = 4,
                importanceScore = 0.89,
                trendingScore = 0.87,
                clusterId = "cluster_solid_state_battery",
                clusterCount = 7,
                relatedSources = """["Bloomberg", "TechCrunch", "Reuters", "Automotive News"]""",
                whyRecommended = "Key startup ecosystem funding and technological milestone",
                aiSummary30Sec = "Clean mobility and solid-state battery ventures have attracted over $30B in fresh funding as production hurdles for high-density cells are resolved.",
                aiKeyPoints = "• Solid-state cells promise 1,000 km per charge with 10-minute fast charging.\n• Multi-billion dollar gigafactory partnerships announced in Asia and Europe.\n• Mass production targeted for late 2027.",
                aiWhyItMatters = "Overcomes the range anxiety and charging bottleneck that has slowed broad consumer adoption of electric vehicles."
            ),

            // --- SPACE & SCIENCE ---
            NewsArticle(
                id = "art_space_01",
                title = "James Webb Space Telescope Detects Water Vapor and Organic Compounds on Habitable-Zone Exoplanet",
                description = "Spectroscopic data from super-Earth K2-18b reveals methane, carbon dioxide, and atmospheric moisture without runaway greenhouse effects.",
                summary = "Astronomers analyzing deepest infrared spectral observations confirm chemically rich atmospheric bands around a planet situated in its star's habitable zone.",
                url = "https://www.nature.com/articles/jwst-exoplanet-atmosphere-detection",
                imageUrl = "https://images.unsplash.com/photo-1451187580459-43490279c0fa?auto=format&fit=crop&w=800&q=80",
                source = "Nature Journal",
                author = "Prof. Nikku Madhusudhan",
                publishedAt = now - 9 * hour,
                country = "Global",
                region = "Worldwide",
                category = "Space",
                tags = "Space, JWST, Astronomy, NASA, Exoplanet, Science",
                readingTime = 5,
                importanceScore = 0.96,
                trendingScore = 0.94,
                clusterId = "cluster_jwst_discovery",
                clusterCount = 14,
                relatedSources = """["Nature Journal", "BBC News", "Space.com", "The Hindu", "The New York Times"]""",
                whyRecommended = "Recommended because you follow Space & Science",
                aiSummary30Sec = "JWST has confirmed direct atmospheric signatures of water vapor and carbon molecules on a temperate ocean-bearing exoplanet 120 light years away.",
                aiKeyPoints = "• Habitable-zone planetary atmosphere verified with high statistical confidence.\n• Detection of carbon-bearing molecules points toward prebiotic planetary conditions.\n• Extended follow-up spectroscopy observation windows scheduled for 2027.",
                aiWhyItMatters = "Represents one of the closest analogs to Earth-like biosphere conditions ever observed outside our solar system."
            ),

            // --- HEALTH & ENVIRONMENT ---
            NewsArticle(
                id = "art_health_01",
                title = "Universal mRNA Vaccine for Influenza and Respiratory Viruses Enters Phase 3 Global Clinical Trials",
                description = "Multivalent vaccine demonstrates broad protective immunity against all known influenza lineages in large-scale multi-country trials.",
                summary = "The breakthrough formulation combines conserved stalk proteins to deliver multi-year protection with a single quadrivalent injection.",
                url = "https://www.reuters.com/health/universal-mrna-flu-vaccine-trials",
                imageUrl = "https://images.unsplash.com/photo-1584515979956-d9f6e5d09982?auto=format&fit=crop&w=800&q=80",
                source = "Reuters",
                author = "Julie Steenhuysen",
                publishedAt = now - 10 * hour,
                country = "Global",
                region = "Worldwide",
                category = "Health",
                tags = "Health, Medicine, Vaccine, mRNA, Science, Wellness",
                readingTime = 4,
                importanceScore = 0.92,
                trendingScore = 0.89,
                clusterId = "cluster_mrna_vaccine",
                clusterCount = 8,
                relatedSources = """["Reuters", "Nature Medicine", "BBC News", "The Lancet"]""",
                whyRecommended = "Major public health and medical innovation",
                aiSummary30Sec = "A universal mRNA respiratory vaccine offering multi-year cross-strain protection has advanced into Phase 3 human trials worldwide.",
                aiKeyPoints = "• Targets conserved virus regions rather than mutating seasonal surface proteins.\n• Phase 2 results demonstrated 94% efficacy across 20 viral strains.\n• Could eliminate the requirement for annual updated seasonal flu boosters.",
                aiWhyItMatters = "Significantly reduces seasonal hospitalization rates and fortifies global defenses against potential pandemic strains."
            ),

            // --- GAMING & ENTERTAINMENT ---
            NewsArticle(
                id = "art_gaming_01",
                title = "Next-Gen Photorealistic Unreal Engine 6 Announced with Real-Time Neural Physics Simulation",
                description = "Epic Games reveals groundbreaking graphics engine featuring fully simulated fluid dynamics, micro-geometry, and neural character rendering.",
                summary = "Developers showcased live interactive worlds running at 4K 120FPS with instant asset streaming and procedural environmental destructibility.",
                url = "https://www.theverge.com/gaming/unreal-engine-6-reveal",
                imageUrl = "https://images.unsplash.com/photo-1538481199705-c710c4e965fc?auto=format&fit=crop&w=800&q=80",
                source = "The Verge",
                author = "Andrew Webster",
                publishedAt = now - 8 * hour,
                country = "United States",
                region = "California",
                category = "Gaming",
                tags = "Gaming, Unreal Engine, Tech, Graphics, eSports, VR",
                readingTime = 4,
                importanceScore = 0.88,
                trendingScore = 0.93,
                clusterId = "cluster_ue6_reveal",
                clusterCount = 9,
                relatedSources = """["The Verge", "IGN", "Polygon", "PC Gamer", "GameSpot"]""",
                whyRecommended = "Top trending gaming and interactive entertainment technology",
                aiSummary30Sec = "Epic Games unveiled Unreal Engine 6, bringing neural physics simulations, photorealistic real-time lighting, and procedural worlds to next-gen games.",
                aiKeyPoints = "• Neural physics enables true real-time material stress and fluid dynamics.\n• Drastically simplifies cross-platform PC, console, and mobile asset creation.\n• Preview SDK available for indie and AAA studios starting next quarter.",
                aiWhyItMatters = "Transforms game development timelines while setting an unprecedented standard for visual immersion."
            )
        )
    }

    fun buildDailyMorningBriefing(
        articles: List<NewsArticle>,
        targetTimezone: String = "Asia/Kolkata",
        userName: String = "Rajesh",
        primaryCountry: String = "India",
        primaryRegion: String = "Tamil Nadu"
    ): DailyBriefing {
        val sdf = SimpleDateFormat("MMMM d, yyyy", Locale.ENGLISH)
        sdf.timeZone = try {
            TimeZone.getTimeZone(targetTimezone)
        } catch (e: Exception) {
            TimeZone.getDefault()
        }
        val dateStr = sdf.format(Date())

        // 1. World stories (5)
        val worldStories = articles
            .filter { it.country.equals("Global", ignoreCase = true) || it.category.equals("World", ignoreCase = true) }
            .sortedByDescending { it.importanceScore }
            .take(5)
            .map { it.id }

        // 2. Country stories (5)
        val countryStories = articles
            .filter { it.country.equals(primaryCountry, ignoreCase = true) || it.category.equals(primaryCountry, ignoreCase = true) }
            .sortedByDescending { it.importanceScore }
            .take(5)
            .map { it.id }

        // 3. State/Regional stories (3-5)
        val stateStories = articles
            .filter { it.region.equals(primaryRegion, ignoreCase = true) || it.category.equals("State/Regional", ignoreCase = true) }
            .sortedByDescending { it.importanceScore }
            .take(4)
            .map { it.id }

        // 4. AI & Tech stories (3-5)
        val aiTechStories = articles
            .filter { it.category.equals("Artificial Intelligence", ignoreCase = true) || it.category.equals("Technology", ignoreCase = true) }
            .sortedByDescending { it.importanceScore }
            .take(4)
            .map { it.id }

        // 5. Sports stories (3-5)
        val sportsStories = articles
            .filter { it.category.equals("Sports", ignoreCase = true) || it.category.equals("Cricket", ignoreCase = true) || it.category.equals("Football", ignoreCase = true) }
            .sortedByDescending { it.importanceScore }
            .take(4)
            .map { it.id }

        // 6. Trending stories (3-5)
        val trendingStories = articles
            .sortedByDescending { it.trendingScore }
            .take(5)
            .map { it.id }

        val id = "brief_${SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH).format(Date())}_${targetTimezone.replace('/', '_')}"

        val greeting = "Good Morning, $userName 👋 Here are today's top stories for $primaryCountry and the world."
        val takeaways = "• Global climate accord ratified with $120B fund.\n• $15B semiconductor corridor approved in $primaryCountry.\n• AI software synthesis achieves new benchmarks.\n• India claims crucial cricket series victory in London."

        return DailyBriefing(
            id = id,
            dateStr = dateStr,
            targetTimezone = targetTimezone,
            generatedAt = System.currentTimeMillis(),
            worldArticleIds = JSONArray(worldStories).toString(),
            countryArticleIds = JSONArray(countryStories).toString(),
            stateArticleIds = JSONArray(stateStories).toString(),
            aiTechArticleIds = JSONArray(aiTechStories).toString(),
            sportsArticleIds = JSONArray(sportsStories).toString(),
            trendingArticleIds = JSONArray(trendingStories).toString(),
            greetingMessage = greeting,
            keyTakeaways = takeaways,
            readMinutesEstimate = 5
        )
    }
}
