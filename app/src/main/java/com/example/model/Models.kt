package com.example.model

data class Anime(
    val animeId: String = "",
    val title: String = "",
    val poster: String = "",
    val bannerImage: String = "",
    val description: String = "",
    val genres: List<String> = emptyList(),
    val rating: Double = 0.0,
    val totalEpisodes: Int = 0,
    val isTrending: Boolean = false,
    val isPremium: Boolean = false
)

data class Episode(
    val epId: String = "",
    val animeId: String = "",
    val epNumber: Int = 0,
    val title: String = "",
    val duration: String = "",
    val videoUrl: String = "",
    val isFree: Boolean = true,
    val thumbnailUrl: String = "",
    val downloadUrl: String = ""
)

data class WatchHistoryItem(
    val animeId: String = "",
    val epId: String = "",
    val epNumber: Int = 0,
    val progressPercentage: Int = 0,
    val timestamp: Long = 0
)

data class SahidUser(
    val uid: String = "",
    val name: String = "",
    val email: String = "",
    val avatar: String = "",
    val isPremium: Boolean = false,
    val premiumExpiry: Long = 0,
    val joinedAt: Long = 0,
    val likedAnimes: List<String> = emptyList(),
    val watchHistory: List<WatchHistoryItem> = emptyList()
)

data class RedeemCode(
    val code: String = "",
    val durationDays: Int = 0,
    val isUsed: Boolean = false,
    val usedBy: String = "",
    val usedAt: Long = 0
)

data class PremiumPlan(
    val planId: String = "",
    val name: String = "",
    val price: Double = 0.0,
    val durationDays: Int = 0,
    val features: List<String> = emptyList()
)
