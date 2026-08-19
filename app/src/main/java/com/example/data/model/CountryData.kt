package com.example.data.model

data class CountryInfo(
    val code: String,
    val name: String,
    val flagEmoji: String,
    val defaultTimezone: String,
    val subdivisions: List<String>
)

object CountryDatabase {
    val countries = listOf(
        CountryInfo(
            code = "IN",
            name = "India",
            flagEmoji = "🇮🇳",
            defaultTimezone = "Asia/Kolkata",
            subdivisions = listOf(
                "Tamil Nadu", "Maharashtra", "Karnataka", "Delhi", "Kerala",
                "Telangana", "Andhra Pradesh", "Gujarat", "West Bengal", "Rajasthan",
                "Uttar Pradesh", "Madhya Pradesh", "Punjab", "Haryana", "Bihar",
                "Odisha", "Assam", "Goa", "Jammu & Kashmir", "Jharkhand",
                "Uttarakhand", "Himachal Pradesh", "Tripura", "Meghalaya", "Manipur",
                "Nagaland", "Puducherry", "Chandigarh"
            )
        ),
        CountryInfo(
            code = "US",
            name = "United States",
            flagEmoji = "🇺🇸",
            defaultTimezone = "America/New_York",
            subdivisions = listOf(
                "California", "New York", "Texas", "Washington", "Florida",
                "Massachusetts", "Illinois", "Georgia", "Pennsylvania", "Ohio",
                "North Carolina", "Michigan", "Colorado", "Virginia", "Arizona"
            )
        ),
        CountryInfo(
            code = "GB",
            name = "United Kingdom",
            flagEmoji = "🇬🇧",
            defaultTimezone = "Europe/London",
            subdivisions = listOf(
                "England", "Scotland", "Wales", "Northern Ireland",
                "Greater London", "Greater Manchester", "West Midlands", "West Yorkshire"
            )
        ),
        CountryInfo(
            code = "CA",
            name = "Canada",
            flagEmoji = "🇨🇦",
            defaultTimezone = "America/Toronto",
            subdivisions = listOf(
                "Ontario", "Quebec", "British Columbia", "Alberta",
                "Manitoba", "Saskatchewan", "Nova Scotia", "New Brunswick"
            )
        ),
        CountryInfo(
            code = "AU",
            name = "Australia",
            flagEmoji = "🇦🇺",
            defaultTimezone = "Australia/Sydney",
            subdivisions = listOf(
                "New South Wales", "Victoria", "Queensland", "Western Australia",
                "South Australia", "Tasmania", "Australian Capital Territory"
            )
        ),
        CountryInfo(
            code = "DE",
            name = "Germany",
            flagEmoji = "🇩🇪",
            defaultTimezone = "Europe/Berlin",
            subdivisions = listOf(
                "Bavaria", "Berlin", "North Rhine-Westphalia", "Baden-Württemberg",
                "Hesse", "Lower Saxony", "Hamburg", "Saxony"
            )
        ),
        CountryInfo(
            code = "FR",
            name = "France",
            flagEmoji = "🇫🇷",
            defaultTimezone = "Europe/Paris",
            subdivisions = listOf(
                "Île-de-France", "Auvergne-Rhône-Alpes", "Provence-Alpes-Côte d'Azur",
                "Occitanie", "Nouvelle-Aquitaine", "Hauts-de-France"
            )
        ),
        CountryInfo(
            code = "JP",
            name = "Japan",
            flagEmoji = "🇯🇵",
            defaultTimezone = "Asia/Tokyo",
            subdivisions = listOf(
                "Tokyo", "Osaka", "Kanagawa", "Kyoto", "Hokkaido", "Aichi", "Fukuoka", "Hyogo"
            )
        ),
        CountryInfo(
            code = "SG",
            name = "Singapore",
            flagEmoji = "🇸🇬",
            defaultTimezone = "Asia/Singapore",
            subdivisions = listOf(
                "Central Region", "East Region", "North Region", "North-East Region", "West Region"
            )
        ),
        CountryInfo(
            code = "AE",
            name = "United Arab Emirates",
            flagEmoji = "🇦🇪",
            defaultTimezone = "Asia/Dubai",
            subdivisions = listOf(
                "Dubai", "Abu Dhabi", "Sharjah", "Ajman", "Ras Al Khaimah", "Fujairah", "Umm Al Quwain"
            )
        ),
        CountryInfo(
            code = "BR",
            name = "Brazil",
            flagEmoji = "🇧🇷",
            defaultTimezone = "America/Sao_Paulo",
            subdivisions = listOf(
                "São Paulo", "Rio de Janeiro", "Minas Gerais", "Bahia", "Paraná", "Rio Grande do Sul"
            )
        ),
        CountryInfo(
            code = "GLOBAL",
            name = "Global / International",
            flagEmoji = "🌍",
            defaultTimezone = "UTC",
            subdivisions = listOf("Worldwide", "Americas", "Asia-Pacific", "Europe", "Middle East", "Africa")
        )
    )

    val ALL get() = countries

    fun getByCode(code: String): CountryInfo {
        return countries.firstOrNull { it.code.equals(code, ignoreCase = true) }
            ?: countries.first { it.code == "IN" }
    }

    fun getByName(name: String): CountryInfo {
        return countries.firstOrNull { it.name.equals(name, ignoreCase = true) }
            ?: countries.first { it.code == "IN" }
    }
}
