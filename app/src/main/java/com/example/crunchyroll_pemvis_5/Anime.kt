package com.example.crunchyroll_pemvis_5

data class Anime(
    val animeId: String = "",
    val title: String = "",
    val description: String = "",
    val imageUrl: String = "",
    val bannerUrl: String = "",
    val genres: List<String> = emptyList(),
    val rating: Double = 0.0,
    val ratingCount: String = "0R",
    val status: String = "Ongoing",
    val releaseYear: Int = 2026,
    val simulcastSeason: String = "",
    val isSimulcast: Boolean = false
)
