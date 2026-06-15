package com.example.repository

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.example.database.*
import com.example.model.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.util.UUID

val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "sahid_anime_preferences")

class AnimeRepositoryImpl(private val context: Context) : AnimeRepository {

    private val scope = CoroutineScope(Dispatchers.IO)
    private val database = AppDatabase.getDatabase(context)
    private val dao = database.dao()

    // Keys
    private val onboardingKey = booleanPreferencesKey("onboarding_completed")
    private val localUserKey = stringPreferencesKey("local_user_data")

    // Flows
    private val _currentUserState = MutableStateFlow<SahidUser?>(null)
    override val currentUserState: Flow<SahidUser?> = _currentUserState.asStateFlow()

    override val onboardingCompleted: Flow<Boolean> = context.dataStore.data
        .map { preferences -> preferences[onboardingKey] ?: false }

    // Redeem codes mapping in-memory
    private val redeemCodes = mapOf(
        "SAHID50" to 30,
        "ANIME100" to 90,
        "OTAKUFREE" to 7,
        "PREM30" to 30
    )

    // Plans mapping in-memory
    private val plans = listOf(
        PremiumPlan("monthly_premium", "Weekly Otaku", 99.0, 7, listOf("HD streaming quality available", "No-ads complete browsing", "Watch all exclusive premium anime episodes", "Instant support assistance")),
        PremiumPlan("quarterly_premium", "Cosmic Monthly", 249.0, 30, listOf("Ultra HD streaming quality available", "No-ads complete streaming with lockscreens", "Watch all exclusive premium anime episodes", "Priority customer query resolution", "Redeem premium points")),
        PremiumPlan("annual_premium", "Ultimate Shogun", 999.0, 365, listOf("4K extreme resolution streaming", "No ads completely", "Access all premium episodes", "Support creator directly", "Exclusive premium avatar frames"))
    )

    init {
        scope.launch {
            // Ensure precompiled user is instantly logged in as direct-entry Premium/VIP
            val defaultUid = "guest_vip_user"
            var localUser = dao.getUserById(defaultUid)
            if (localUser == null) {
                val newUser = SahidUser(
                    uid = defaultUid,
                    name = "Premium Otaku",
                    email = "premium@sahidanime.com",
                    avatar = "S",
                    isPremium = true,
                    premiumExpiry = System.currentTimeMillis() + (365L * 24 * 60 * 60 * 1000), // 1-year Premium VIP
                    joinedAt = System.currentTimeMillis()
                )
                dao.insertUser(newUser.toEntity("no_password"))
                localUser = dao.getUserById(defaultUid)
            }
            _currentUserState.value = localUser?.toDomain()

            // Automatically complete onboarding
            context.dataStore.edit { preferences ->
                preferences[onboardingKey] = true
            }

            // Repopulate using JSON local database
            prepopulateNewAnimeDataset()
        }
    }

    override suspend fun completeOnboarding() {
        context.dataStore.edit { preferences ->
            preferences[onboardingKey] = true
        }
    }

    override suspend fun login(email: String, password: String): Result<SahidUser> {
        val userEntity = dao.getUserByEmail(email.trim().lowercase())
            ?: return Result.failure(Exception("Incorrect Email or Password! User not found."))

        if (userEntity.passwordHash != password) {
            return Result.failure(Exception("Incorrect Password! Please try again."))
        }

        val domainUser = userEntity.toDomain()
        _currentUserState.value = domainUser
        
        context.dataStore.edit { preferences ->
            preferences[localUserKey] = domainUser.uid
        }
        return Result.success(domainUser)
    }

    override suspend fun register(name: String, email: String, password: String): Result<SahidUser> {
        val sanitizedEmail = email.trim().lowercase()
        val existing = dao.getUserByEmail(sanitizedEmail)
        if (existing != null) {
            return Result.failure(Exception("Email is already registered! Please log in instead."))
        }

        val newUser = SahidUser(
            uid = UUID.randomUUID().toString(),
            name = name,
            email = sanitizedEmail,
            avatar = name.take(1).uppercase(),
            isPremium = false,
            joinedAt = System.currentTimeMillis()
        )

        // Save directly to our secure offline Room DB
        dao.insertUser(newUser.toEntity(password))
        
        _currentUserState.value = newUser
        context.dataStore.edit { preferences ->
            preferences[localUserKey] = newUser.uid
        }

        return Result.success(newUser)
    }

    override suspend fun loginWithGoogle(idToken: String): Result<SahidUser> {
        val googleEmail = "google_user_${UUID.randomUUID().toString().take(5)}@gmail.com"
        val googleName = "Google Otaku"
        
        val existing = dao.getUserByEmail(googleEmail)
        if (existing != null) {
            val domainUser = existing.toDomain()
            _currentUserState.value = domainUser
            context.dataStore.edit { preferences ->
                preferences[localUserKey] = domainUser.uid
            }
            return Result.success(domainUser)
        }

        val newUser = SahidUser(
            uid = "google_" + UUID.randomUUID().toString(),
            name = googleName,
            email = googleEmail,
            avatar = "G",
            isPremium = false,
            joinedAt = System.currentTimeMillis()
        )

        dao.insertUser(newUser.toEntity("google_login_token"))
        _currentUserState.value = newUser
        context.dataStore.edit { preferences ->
            preferences[localUserKey] = newUser.uid
        }

        return Result.success(newUser)
    }

    override suspend fun logout() {
        context.dataStore.edit { preferences ->
            preferences.remove(localUserKey)
        }
        _currentUserState.value = null
    }

    override fun getTrendingAnimes(): Flow<List<Anime>> {
        return dao.getAllAnimesFlow().map { list ->
            list.filter { it.isTrending }.map { it.toDomain() }
        }
    }

    override fun getLatestAnimes(): Flow<List<Anime>> {
        return dao.getAllAnimesFlow().map { list ->
            list.map { it.toDomain() }
        }
    }

    override fun getPremiumPicks(): Flow<List<Anime>> {
        return dao.getAllAnimesFlow().map { list ->
            list.filter { it.isPremium }.map { it.toDomain() }
        }
    }

    override fun getAnimeById(animeId: String): Flow<Anime?> {
        return dao.getAnimeByIdFlow(animeId).map { it?.toDomain() }
    }

    override fun getEpisodes(animeId: String): Flow<List<Episode>> {
        return dao.getEpisodesForAnimeFlow(animeId).map { list ->
            list.map { it.toDomain() }
        }
    }

    override fun getSearchResults(query: String): Flow<List<Anime>> {
        return dao.getAllAnimesFlow().map { list ->
            if (query.isBlank()) {
                emptyList()
            } else {
                list.map { it.toDomain() }.filter {
                    it.title.contains(query, ignoreCase = true) || 
                    it.genres.any { g -> g.contains(query, ignoreCase = true) }
                }
            }
        }
    }

    override suspend fun toggleLikeAnime(animeId: String): Result<Boolean> {
        val user = _currentUserState.value ?: return Result.failure(Exception("Please login to like anime"))
        val updatedLikedList = user.likedAnimes.toMutableList()
        val isLiked: Boolean
        if (updatedLikedList.contains(animeId)) {
            updatedLikedList.remove(animeId)
            isLiked = false
        } else {
            updatedLikedList.add(animeId)
            isLiked = true
        }

        val updatedUser = user.copy(likedAnimes = updatedLikedList)
        _currentUserState.value = updatedUser

        // Persist back into User Table in Room
        val dbUser = dao.getUserById(user.uid)
        if (dbUser != null) {
            dao.insertUser(updatedUser.toEntity(dbUser.passwordHash))
        }

        return Result.success(isLiked)
    }

    override suspend fun updateWatchProgress(
        animeId: String,
        epId: String,
        epNumber: Int,
        progressPercentage: Int
    ) {
        val user = _currentUserState.value ?: return
        val currentHistory = user.watchHistory.toMutableList()
        val existingIndex = currentHistory.indexOfFirst { it.animeId == animeId && it.epId == epId }

        val newItem = WatchHistoryItem(
            animeId = animeId,
            epId = epId,
            epNumber = epNumber,
            progressPercentage = progressPercentage,
            timestamp = System.currentTimeMillis()
        )

        if (existingIndex >= 0) {
            currentHistory[existingIndex] = newItem
        } else {
            currentHistory.add(0, newItem)
        }

        val updatedUser = user.copy(watchHistory = currentHistory)
        _currentUserState.value = updatedUser

        val dbUser = dao.getUserById(user.uid)
        if (dbUser != null) {
            dao.insertUser(updatedUser.toEntity(dbUser.passwordHash))
        }
    }

    override suspend fun redeemCode(code: String): Result<Int> {
        val formattedCode = code.uppercase().trim()
        val durationDays = redeemCodes[formattedCode]
            ?: return Result.failure(Exception("Invalid custom promo code! Please check code characters"))

        val user = _currentUserState.value ?: return Result.failure(Exception("Log in to redeem code"))

        val daysInMillis = durationDays * 24L * 60 * 60 * 1000
        val currentExpiry = if (user.isPremium) user.premiumExpiry else System.currentTimeMillis()
        val newExpiry = currentExpiry + daysInMillis

        val updatedUser = user.copy(
            isPremium = true,
            premiumExpiry = newExpiry
        )
        _currentUserState.value = updatedUser

        val dbUser = dao.getUserById(user.uid)
        if (dbUser != null) {
            dao.insertUser(updatedUser.toEntity(dbUser.passwordHash))
        }

        return Result.success(durationDays)
    }

    override fun getPlans(): Flow<List<PremiumPlan>> = flowOf(plans)

    override suspend fun purchasePlan(planId: String): Result<Boolean> {
        val plan = plans.find { it.planId == planId } ?: return Result.failure(Exception("Plan not found"))
        val user = _currentUserState.value ?: return Result.failure(Exception("Please log in first"))

        val daysInMillis = plan.durationDays * 24L * 60 * 60 * 1000
        val currentExpiry = if (user.isPremium) user.premiumExpiry else System.currentTimeMillis()
        val newExpiry = currentExpiry + daysInMillis

        val updatedUser = user.copy(
            isPremium = true,
            premiumExpiry = newExpiry
        )
        _currentUserState.value = updatedUser

        val dbUser = dao.getUserById(user.uid)
        if (dbUser != null) {
            dao.insertUser(updatedUser.toEntity(dbUser.passwordHash))
        }

        return Result.success(true)
    }

    override suspend fun updateAvatar(avatarUrl: String): Result<Boolean> {
        val user = _currentUserState.value ?: return Result.failure(Exception("Please login"))
        val updatedUser = user.copy(avatar = avatarUrl)
        _currentUserState.value = updatedUser

        val dbUser = dao.getUserById(user.uid)
        if (dbUser != null) {
            dao.insertUser(updatedUser.toEntity(dbUser.passwordHash))
        }
        return Result.success(true)
    }

    override suspend fun deleteAccount(): Result<Boolean> {
        val uid = _currentUserState.value?.uid ?: return Result.failure(Exception("Please login"))
        dao.deleteUser(uid)
        _currentUserState.value = null
        return Result.success(true)
    }

    // Dynamic Database access helper methods for User insertion
    override suspend fun insertAnime(anime: Anime) {
        dao.insertAnime(anime.toEntity())
    }

    override suspend fun insertEpisode(episode: Episode) {
        dao.insertEpisode(episode.toEntity())
    }

    override suspend fun clearAllAnime() {
        dao.clearAnimes()
        dao.clearEpisodes()
    }

    private suspend fun prepopulateNewAnimeDataset() {
        try {
            // First clear all existing items to ensure only the user's detailed list is used
            dao.clearAnimes()
            dao.clearEpisodes()

            // Read JSON from assets
            val jsonString = context.assets.open("anime_data.json").bufferedReader().use { it.readText() }
            val rootArray = org.json.JSONArray(jsonString)

            for (i in 0 until rootArray.length()) {
                val animeObj = rootArray.getJSONObject(i)
                val animeId = animeObj.getString("animeId")
                val title = animeObj.getString("title")
                val poster = animeObj.getString("poster")
                val banner = animeObj.getString("bannerImage")
                val description = animeObj.getString("description")
                
                val genresArray = animeObj.getJSONArray("genres")
                val genresList = mutableListOf<String>()
                for (g in 0 until genresArray.length()) {
                    genresList.add(genresArray.getString(g))
                }
                
                val rating = animeObj.getDouble("rating")
                val totalEpisodes = animeObj.getInt("totalEpisodes")
                val isTrending = animeObj.getBoolean("isTrending")
                val isPremium = animeObj.getBoolean("isPremium")

                // Map to Entity and Save Anime
                val animeEntity = AnimeEntity(
                    animeId = animeId,
                    title = title,
                    poster = poster,
                    bannerImage = banner,
                    description = description,
                    genresCsv = genresList.joinToString(","),
                    rating = rating,
                    totalEpisodes = totalEpisodes,
                    isTrending = isTrending,
                    isPremium = isPremium
                )
                dao.insertAnime(animeEntity)

                // Save associated episodes
                val episodesArray = animeObj.getJSONArray("episodes")
                for (e in 0 until episodesArray.length()) {
                    val epObj = episodesArray.getJSONObject(e)
                    val epNumber = epObj.getInt("epNumber")
                    val epTitle = epObj.getString("title")
                    val videoUrl = epObj.getString("videoUrl")
                    val downloadUrl = epObj.optString("downloadUrl", "")
                    val isFree = epObj.getBoolean("isFree")

                    val epEntity = EpisodeEntity(
                        epId = "${animeId}_ep_${epNumber}",
                        animeId = animeId,
                        epNumber = epNumber,
                        title = epTitle,
                        duration = "24m", // default estimated length
                        videoUrl = videoUrl,
                        isFree = isFree,
                        thumbnailUrl = poster, // fallback
                        downloadUrl = downloadUrl
                    )
                    dao.insertEpisode(epEntity)
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}
