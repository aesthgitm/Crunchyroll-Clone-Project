package com.example.crunchyroll_pemvis_5

import java.io.Serializable

data class EpisodeModel(
    val id: String = "",
    val number: Int = 1,
    val name: String = "",
    val durationRemaining: String = "23m",
    val thumbnailColor: Int = 0,
    val videoUrl: String = "",
    val youtubeVideoId: String = "",
    val imageUrl: String = ""
) : Serializable

data class AnimeModel(
    val id: String,
    val title: String,
    val subtitle: String,
    val ageRating: String,
    val dubSubText: String,
    val ratingStars: Float,
    val ratingReviews: String,
    val description: String,
    val placeholderColor: Int,
    var episodes: List<EpisodeModel> = emptyList(), // Diubah menjadi var agar list episode bisa diisi dinamis dari database
    val imageUrl: String = ""
) : Serializable

data class CrunchylistModel(
    val id: String,
    var name: String,
    val animeIds: MutableList<String> = mutableListOf()
) : Serializable