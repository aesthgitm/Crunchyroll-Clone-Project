package com.example.crunchyroll_pemvis_5

data class User(
    val uid: String = "",
    val username: String = "",
    val email: String = "",
    val profileName: String = "Moon",
    val membershipType: String = "Free",
    val contentRatingRestriction: String = "16+",
    val audioLanguage: String = "Bahasa Indonesia",
    val subtitleLanguage: String = "English",
    val audioDescriptionEnabled: Boolean = false,
    val downloadQuality: Int = 0,
    val streamCellularEnabled: Boolean = false,
    val downloadCellularEnabled: Boolean = false
)
