package com.example.crunchyroll_pemvis_5

import android.graphics.Color

object MockData {
    val createdCrunchylists = mutableListOf<CrunchylistModel>()
    var activeSubscriptionPlan: String = "Gratis"

    // Profile preferences state
    var profileName: String = "Advan"
    var profileUsername: String = "advan_p"
    var activeContentRestriction: String = "Semua Konten"
    var isPinProfileEnabled: Boolean = false
    var isClosedCaptionsEnabled: Boolean = false
    var streamCellularEnabled: Boolean = false
    var downloadCellularEnabled: Boolean = false
    var audioLanguage: String = "Bahasa Indonesia"
    var subtitleLanguage: String = "English"
    var audioDescriptionEnabled: Boolean = false
    var downloadQuality: Int = 0 // 0=Tinggi, 1=Sedang, 2=Rendah

    // Added dynamic state for page integration
    var loggedInEmail: String = "advan_p@gmail.com"
    var userPassword: String = "password123"
    var selectedSortOption: String = "Terbaru"
    var selectedGenreFilter: String = "Semua"
    var selectedSimulcastSeason: String = "Musim Dingin 2026"
    val watchlist = mutableListOf<AnimeModel>()
    val watchHistory = mutableListOf<AnimeModel>()
    val downloads = mutableListOf<AnimeModel>()
    val searchHistory = mutableListOf<String>()

    val animeByGenre: Map<String, List<AnimeModel>> by lazy {
        mapOf(
            "Aksi" to listOf(trendingAnime[1], exclusiveAnime[2], exclusiveAnime[1]),
            "Fantasi" to listOf(trendingAnime[0], exclusiveAnime[0], trendingAnime[2]),
            "Komedi" to listOf(trendingAnime[2], exclusiveAnime[0]),
            "Fiksi Ilmiah" to listOf(bannerAnime[0], trendingAnime[0]),
            "Romantis" to listOf(trendingAnime[0]),
            "Drama" to listOf(trendingAnime[0], exclusiveAnime[2])
        )
    }


    val trendingAnime = listOf(
        AnimeModel(
            id = "tye",
            title = "To Your Eternity",
            subtitle = "Season 3",
            ageRating = "13+",
            dubSubText = "Sulih Suara English | T...",
            ratingStars = 4.9f,
            ratingReviews = "785R",
            description = "In the beginning, an 'Orb' is cast unto Earth. 'It' can do two things: take the form of any thing that reflects it, and cause things to regenerate after death.",
            placeholderColor = Color.parseColor("#2E153B"),
            episodes = listOf(
                EpisodeModel("tye_e1", 1, "The Last Festival", "23m", Color.parseColor("#154C8F")),
                EpisodeModel("tye_e2", 2, "A New Journey", "23m", Color.parseColor("#113D75"))
            ),
            imageResId = R.drawable.to_your_eternity
        ),
        AnimeModel(
            id = "dsr",
            title = "Daemons of the Shadow Realm",
            subtitle = "Supernatural Adventure",
            ageRating = "16+",
            dubSubText = "Sulih Suara English | T...",
            ratingStars = 4.8f,
            ratingReviews = "310R",
            description = "Deep in the remote mountains lies a quiet village. However, mysterious occurrences begin to disrupt the peace as the supernatural starts taking over.",
            placeholderColor = Color.parseColor("#0C2E5C"),
            episodes = listOf(
                EpisodeModel("dsr_e1", 1, "The Shadow Appears", "24m", Color.parseColor("#0C2D5C"))
            ),
            imageResId = R.drawable.daemons_shadow
        ),
        AnimeModel(
            id = "strongest",
            title = "The strongest job is a...",
            subtitle = "Fantasy World Adventure",
            ageRating = "13+",
            dubSubText = "Sulih Suara العربية | Ta...",
            ratingStars = 4.5f,
            ratingReviews = "45R",
            description = "Reincarnated into another world with an seemingly weak apprentice job, our protagonist unlocks secret abilities that make him the ultimate warrior.",
            placeholderColor = Color.parseColor("#0F3D1C"),
            episodes = listOf(
                EpisodeModel("str_e1", 1, "Apprentice Beginnings", "23m", Color.parseColor("#196930"))
            ),
            imageResId = R.drawable.strongest_job
        )
    )

    val exclusiveAnime = listOf(
        AnimeModel(
            id = "shy",
            title = "SHY",
            subtitle = "Superhero",
            ageRating = "13+",
            dubSubText = "Sulih Suara English | T...",
            ratingStars = 4.6f,
            ratingReviews = "98R",
            description = "Earth was on the brink of a third World War when super-powered individuals appeared from each country, ending the conflict and bringing peace.",
            placeholderColor = Color.parseColor("#540E0E"),
            episodes = listOf(
                EpisodeModel("shy_e1", 1, "I am Shy", "24m", Color.parseColor("#871D1D"))
            ),
            imageResId = R.drawable.shy
        ),
        AnimeModel(
            id = "mha",
            title = "My Hero Academia: Heroes Rising",
            subtitle = "Movie",
            ageRating = "13+",
            dubSubText = "Sulih Suara English | T...",
            ratingStars = 4.7f,
            ratingReviews = "1.2M",
            description = "Class 1-A visits Nabu Island where they finally get to do some real hero work. The place is so peaceful that it’s more like a vacation—until they’re attacked.",
            placeholderColor = Color.parseColor("#0F3D1C"),
            episodes = listOf(
                EpisodeModel("mha_e1", 1, "Heroes Rising Movie", "1h 44m", Color.parseColor("#135225"))
            ),
            imageResId = R.drawable.naruto // fallback card graphic
        ),
        AnimeModel(
            id = "aot",
            title = "Attack on Titan",
            subtitle = "Action Thriller",
            ageRating = "18+",
            dubSubText = "Dengan Takarir",
            ratingStars = 4.9f,
            ratingReviews = "2.1M",
            description = "Centuries ago, mankind was slaughtered to near extinction by monstrous humanoid creatures called Titans, forcing humans to hide behind enormous concentric walls.",
            placeholderColor = Color.parseColor("#2E1C16"),
            episodes = listOf(
                EpisodeModel("aot_e1", 1, "To You, 2000 Years in the Future", "23m", Color.parseColor("#5C392E"))
            ),
            imageResId = R.drawable.sl // fallback card graphic
        )
    )

    val bannerAnime = listOf(
        AnimeModel(
            id = "drstone",
            title = "Dr.STONE",
            subtitle = "SCIENCE FUTURE",
            ageRating = "14+",
            dubSubText = "• Sulih Suara English | Takarir",
            ratingStars = 4.9f,
            ratingReviews = "480R",
            description = "Modern society is lost when a mysterious light turns humanity to stone. Thousands of years pass and high schoolers Senku and Taiju awaken in an overgrown version of...",
            placeholderColor = Color.parseColor("#154C8F"),
            episodes = listOf(
                EpisodeModel("ds_e1", 1, "Stone World", "24m", Color.parseColor("#154C8F"))
            ),
            imageResId = R.drawable.dr_stone_banner
        ),
        trendingAnime[0], // To Your Eternity
        trendingAnime[1], // Daemons of the Shadow Realm
        trendingAnime[2], // The strongest job is a...
        exclusiveAnime[0] // SHY
    )

    val allAnime = trendingAnime + exclusiveAnime + listOf(bannerAnime[0])

    fun searchAnime(query: String): List<AnimeModel> {
        if (query.trim().isEmpty()) return emptyList()
        return allAnime.filter { 
            it.title.contains(query, ignoreCase = true) || 
            it.subtitle.contains(query, ignoreCase = true) 
        }
    }
}
