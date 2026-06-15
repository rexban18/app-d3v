package com.example.database

import android.content.Context
import androidx.room.*
import com.example.model.*
import kotlinx.coroutines.flow.Flow

@Entity(tableName = "users")
data class UserEntity(
    @PrimaryKey val uid: String,
    val name: String,
    val email: String,
    val passwordHash: String,
    val avatar: String,
    val isPremium: Boolean,
    val premiumExpiry: Long,
    val joinedAt: Long,
    val likedAnimesCsv: String,
    val watchHistorySerialized: String
)

@Entity(tableName = "animes")
data class AnimeEntity(
    @PrimaryKey val animeId: String,
    val title: String,
    val poster: String,
    val bannerImage: String,
    val description: String,
    val genresCsv: String,
    val rating: Double,
    val totalEpisodes: Int,
    val isTrending: Boolean,
    val isPremium: Boolean
)

@Entity(tableName = "episodes")
data class EpisodeEntity(
    @PrimaryKey val epId: String,
    val animeId: String,
    val epNumber: Int,
    val title: String,
    val duration: String,
    val videoUrl: String,
    val isFree: Boolean,
    val thumbnailUrl: String,
    val downloadUrl: String
)

@Dao
interface AppDao {
    @Query("SELECT * FROM users WHERE email = :email LIMIT 1")
    suspend fun getUserByEmail(email: String): UserEntity?

    @Query("SELECT * FROM users WHERE uid = :uid LIMIT 1")
    suspend fun getUserById(uid: String): UserEntity?

    @Query("SELECT * FROM users WHERE uid = :uid LIMIT 1")
    fun getUserByIdFlow(uid: String): Flow<UserEntity?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertUser(user: UserEntity)

    @Query("DELETE FROM users WHERE uid = :uid")
    suspend fun deleteUser(uid: String)

    @Query("SELECT * FROM animes")
    fun getAllAnimesFlow(): Flow<List<AnimeEntity>>

    @Query("SELECT * FROM animes WHERE animeId = :animeId LIMIT 1")
    fun getAnimeByIdFlow(animeId: String): Flow<AnimeEntity?>

    @Query("SELECT * FROM animes WHERE animeId = :animeId LIMIT 1")
    suspend fun getAnimeById(animeId: String): AnimeEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAnimes(animes: List<AnimeEntity>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAnime(anime: AnimeEntity)

    @Query("DELETE FROM animes")
    suspend fun clearAnimes()

    @Query("SELECT * FROM episodes WHERE animeId = :animeId")
    fun getEpisodesForAnimeFlow(animeId: String): Flow<List<EpisodeEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertEpisodes(episodes: List<EpisodeEntity>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertEpisode(episode: EpisodeEntity)

    @Query("DELETE FROM episodes")
    suspend fun clearEpisodes()
}

@Database(entities = [UserEntity::class, AnimeEntity::class, EpisodeEntity::class], version = 2, exportSchema = false)
abstract class AppDatabase : RoomDatabase() {
    abstract fun dao(): AppDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "sahid_anime_local_db"
                )
                .fallbackToDestructiveMigration()
                .build()
                INSTANCE = instance
                instance
            }
        }
    }
}

// Data Mappers to encapsulate DB layers from Domain models cleanly
fun UserEntity.toDomain(): SahidUser {
    val liked = likedAnimesCsv.split(",").filter { it.isNotBlank() }
    val history = deserializeWatchHistory(watchHistorySerialized)
    return SahidUser(
        uid = this.uid,
        name = this.name,
        email = this.email,
        avatar = this.avatar,
        isPremium = this.isPremium,
        premiumExpiry = this.premiumExpiry,
        joinedAt = this.joinedAt,
        likedAnimes = liked,
        watchHistory = history
    )
}

fun SahidUser.toEntity(passwordHash: String): UserEntity {
    val likedCsv = likedAnimes.joinToString(",")
    val historySerialized = serializeWatchHistory(watchHistory)
    return UserEntity(
        uid = this.uid,
        name = this.name,
        email = this.email,
        passwordHash = passwordHash,
        avatar = this.avatar,
        isPremium = this.isPremium,
        premiumExpiry = this.premiumExpiry,
        joinedAt = this.joinedAt,
        likedAnimesCsv = likedCsv,
        watchHistorySerialized = historySerialized
    )
}

fun AnimeEntity.toDomain(): Anime {
    val genres = genresCsv.split(",").filter { it.isNotBlank() }
    return Anime(
        animeId = this.animeId,
        title = this.title,
        poster = this.poster,
        bannerImage = this.bannerImage,
        description = this.description,
        genres = genres,
        rating = this.rating,
        totalEpisodes = this.totalEpisodes,
        isTrending = this.isTrending,
        isPremium = this.isPremium
    )
}

fun Anime.toEntity(): AnimeEntity {
    val genresCsv = genres.joinToString(",")
    return AnimeEntity(
        animeId = this.animeId,
        title = this.title,
        poster = this.poster,
        bannerImage = this.bannerImage,
        description = this.description,
        genresCsv = genresCsv,
        rating = this.rating,
        totalEpisodes = this.totalEpisodes,
        isTrending = this.isTrending,
        isPremium = this.isPremium
    )
}

fun EpisodeEntity.toDomain(): Episode {
    return Episode(
        epId = this.epId,
        animeId = this.animeId,
        epNumber = this.epNumber,
        title = this.title,
        duration = this.duration,
        videoUrl = this.videoUrl,
        isFree = this.isFree,
        thumbnailUrl = this.thumbnailUrl,
        downloadUrl = this.downloadUrl
    )
}

fun Episode.toEntity(): EpisodeEntity {
    return EpisodeEntity(
        epId = this.epId,
        animeId = this.animeId,
        epNumber = this.epNumber,
        title = this.title,
        duration = this.duration,
        videoUrl = this.videoUrl,
        isFree = this.isFree,
        thumbnailUrl = this.thumbnailUrl,
        downloadUrl = this.downloadUrl
    )
}

// High performance robust custom string serialization helpers
fun serializeWatchHistory(history: List<WatchHistoryItem>): String {
    return history.joinToString(";") {
        "${it.animeId}|${it.epId}|${it.epNumber}|${it.progressPercentage}|${it.timestamp}"
    }
}

fun deserializeWatchHistory(data: String): List<WatchHistoryItem> {
    if (data.isBlank()) return emptyList()
    return data.split(";").mapNotNull { item ->
        val parts = item.split("|")
        if (parts.size == 5) {
            WatchHistoryItem(
                animeId = parts[0],
                epId = parts[1],
                epNumber = parts[2].toIntOrNull() ?: 1,
                progressPercentage = parts[3].toIntOrNull() ?: 0,
                timestamp = parts[4].toLongOrNull() ?: 0L
            )
        } else null
    }
}
