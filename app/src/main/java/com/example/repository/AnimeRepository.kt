package com.example.repository

import com.example.model.*
import kotlinx.coroutines.flow.Flow

interface AnimeRepository {
    val currentUserState: Flow<SahidUser?>
    val onboardingCompleted: Flow<Boolean>
    
    suspend fun completeOnboarding()
    suspend fun login(email: String, password: String): Result<SahidUser>
    suspend fun register(name: String, email: String, password: String): Result<SahidUser>
    suspend fun loginWithGoogle(idToken: String): Result<SahidUser>
    suspend fun logout()
    
    fun getTrendingAnimes(): Flow<List<Anime>>
    fun getLatestAnimes(): Flow<List<Anime>>
    fun getPremiumPicks(): Flow<List<Anime>>
    fun getAnimeById(animeId: String): Flow<Anime?>
    fun getEpisodes(animeId: String): Flow<List<Episode>>
    fun getSearchResults(query: String): Flow<List<Anime>>
    
    suspend fun toggleLikeAnime(animeId: String): Result<Boolean>
    suspend fun updateWatchProgress(animeId: String, epId: String, epNumber: Int, progressPercentage: Int)
    suspend fun redeemCode(code: String): Result<Int>
    
    fun getPlans(): Flow<List<PremiumPlan>>
    suspend fun purchasePlan(planId: String): Result<Boolean>
    
    suspend fun updateAvatar(avatarUrl: String): Result<Boolean>
    suspend fun deleteAccount(): Result<Boolean>

    // Room Database local administration helpers for user's custom lists
    suspend fun insertAnime(anime: Anime)
    suspend fun insertEpisode(episode: Episode)
    suspend fun clearAllAnime()
}
