package com.example.crunchyroll_pemvis_5

data class Episode(
    val episodeId: String = "",
    val episodeNumber: Int = 1,
    val title: String = "",
    val durationText: String = "23m",
    val synopsis: String = "",
    val thumbnailUrl: String = "",
    val downloadSizeText: String = "",
    val likesCount: Int = 0,
    val dislikesCount: Int = 0
)
