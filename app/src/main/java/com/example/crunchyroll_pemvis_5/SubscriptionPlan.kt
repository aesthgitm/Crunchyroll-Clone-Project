package com.example.crunchyroll_pemvis_5

data class SubscriptionPlan(
    val title: String,
    val promoTag: String?,
    val oldPrice: String?,
    val newPrice: String,
    val mascotResId: Int,
    val streamingPerangkat: String,
    val hasGameVault: Boolean
)
