package com.example.crunchyroll_pemvis_5

import java.io.Serializable

data class EpisodeModel(
    val id: String,
    val number: Int,
    val name: String,
    val durationRemaining: String,
    val thumbnailColor: Int,
    val videoUrl: String = ""
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
    val episodes: List<EpisodeModel>,
    val imageResId: Int? = null
) : Serializable

data class CrunchylistModel(
    val id: String,
    var name: String,
    val animeIds: MutableList<String> = mutableListOf()
) : Serializable
