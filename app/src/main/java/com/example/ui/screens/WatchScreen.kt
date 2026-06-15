package com.example.ui.screens

import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.Toast
import kotlin.OptIn
import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.blur
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.media3.common.MediaItem
import androidx.media3.common.util.UnstableApi
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.ui.PlayerView
import com.example.model.Anime
import com.example.model.Episode
import com.example.ui.components.EpisodeCard
import com.example.ui.components.GlassCard
import com.example.ui.components.GradientButton
import com.example.ui.theme.*
import com.example.viewmodel.MainViewModel
import kotlinx.coroutines.delay

@OptIn(UnstableApi::class, ExperimentalMaterial3Api::class)
@Composable
fun WatchScreen(
    animeId: String,
    epId: String,
    viewModel: MainViewModel,
    onNavigateBack: () -> Unit,
    onNavigateToPremium: () -> Unit,
    onNavigateToAlternateEpisode: (episodeId: String) -> Unit
) {
    val context = LocalContext.current
    val animeList by viewModel.latestAnimes.collectAsState()
    val episodesList by viewModel.getEpisodes(animeId).collectAsState(initial = emptyList())
    val currentUser by viewModel.currentUser.collectAsState()

    val anime = remember(animeList, animeId) { animeList.find { it.animeId == animeId } }
    val episode = remember(episodesList, epId) { episodesList.find { it.epId == epId } }

    var isLiked by remember(currentUser, animeId) {
        mutableStateOf(currentUser?.likedAnimes?.contains(animeId) ?: false)
    }

    var isEpisodesExpanded by remember { mutableStateOf(false) }

    if (anime == null || episode == null) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(BackgroundPrimary),
            contentAlignment = Alignment.Center
        ) {
            CircularProgressIndicator(color = AccentPurple)
        }
        return
    }

    val isUserPremium = currentUser?.isPremium ?: false
    val isEpisodeLocked = !episode.isFree && !isUserPremium

    // Keep track of current next episode
    val nextEpisode = remember(episodesList, episode) {
        episodesList.find { it.epNumber == episode.epNumber + 1 }
    }

    Scaffold(
        containerColor = BackgroundPrimary,
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text(
                        text = "WATCHING EPISODE",
                        color = TextPrimary,
                        fontFamily = OrbitronFamily,
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = "Return",
                            tint = TextPrimary
                        )
                    }
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = Color.Transparent
                )
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            // 1. Video Player HUD / Gating Blockers
            if (isEpisodeLocked) {
                // Locked Premium Gate layout container
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(230.dp)
                        .padding(horizontal = 16.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .background(BackgroundCard)
                        .border(1.dp, PremiumGold.copy(alpha = 0.2f), RoundedCornerShape(12.dp)),
                    contentAlignment = Alignment.Center
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .blur(14.dp)
                    )

                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier.padding(16.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Lock,
                            contentDescription = null,
                            tint = PremiumGold,
                            modifier = Modifier.size(42.dp)
                        )
                        Spacer(modifier = Modifier.height(10.dp))
                        Text(
                            text = "PREMIUM SUITE EXCLUSIVE",
                            color = PremiumGold,
                            fontFamily = OrbitronFamily,
                            fontWeight = FontWeight.Bold,
                            fontSize = 13.sp,
                            letterSpacing = 1.sp
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "Upgrade your catalog tier to continue stream access.",
                            color = TextMuted,
                            fontSize = 11.sp
                        )
                        Spacer(modifier = Modifier.height(14.dp))
                        GradientButton(
                            text = "UPGRADE TO PREMIUM",
                            onClick = onNavigateToPremium
                        )
                    }
                }
            } else {
                // Interactive Media3 Player Frame
                VideoPlayerFrame(
                    videoUrl = episode.videoUrl,
                    onProgressUpdate = { progress ->
                        // Automatically record progress with the ViewModel
                        viewModel.recordWatchProgress(animeId, epId, episode.epNumber, progress)
                    }
                )
            }

            // Description / Header Details
            LazyColumn(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                contentPadding = PaddingValues(top = 18.dp, bottom = 24.dp)
            ) {
                // Info block
                item {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 24.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = "EPISODE ${episode.epNumber}: " + episode.title.uppercase(),
                                    color = TextPrimary,
                                    fontFamily = OrbitronFamily,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 15.sp,
                                    maxLines = 2
                                )
                                Spacer(modifier = Modifier.height(3.dp))
                                Text(
                                    text = anime.title + " | " + episode.duration,
                                    color = TextMuted,
                                    fontSize = 11.sp,
                                    fontFamily = SpaceMonoFamily
                                )
                            }

                            // Interactive Like icon
                            IconButton(
                                onClick = {
                                    isLiked = !isLiked
                                    viewModel.toggleLikeAnime(anime.animeId)
                                },
                                modifier = Modifier
                                    .size(46.dp)
                                    .clip(CircleShape)
                                    .background(BackgroundSurface)
                                    .border(1.dp, BorderColor, CircleShape)
                            ) {
                                Icon(
                                    imageVector = if (isLiked) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                                    contentDescription = "Like stream anime",
                                    tint = if (isLiked) DangerRed else TextPrimary,
                                    modifier = Modifier.size(18.dp)
                                )
                            }
                        }

                        // Play/Download Action row
                        if (episode.downloadUrl.isNotBlank()) {
                            Spacer(modifier = Modifier.height(14.dp))
                            val uriHandler = androidx.compose.ui.platform.LocalUriHandler.current
                            GradientButton(
                                text = "DOWNLOAD FULL EPISODE (HD)",
                                modifier = Modifier.fillMaxWidth(),
                                onClick = {
                                    Toast.makeText(context, "Initiating secure download sequence...", Toast.LENGTH_SHORT).show()
                                    try {
                                        uriHandler.openUri(episode.downloadUrl)
                                    } catch (e: Exception) {
                                        Toast.makeText(context, "Failed to launch web viewer: ${e.localizedMessage}", Toast.LENGTH_LONG).show()
                                    }
                                }
                            )
                        }

                        Spacer(modifier = Modifier.height(20.dp))
                    }
                }

                // Next Episode preview button card (if exists)
                if (nextEpisode != null) {
                    val isNextLocked = !nextEpisode.isFree && !isUserPremium
                    item {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 24.dp)
                        ) {
                            Text(
                                text = "NEXT STREAMING ELEMENT",
                                color = TextMuted,
                                fontSize = 11.sp,
                                fontFamily = SpaceMonoFamily,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.padding(bottom = 8.dp)
                            )

                            EpisodeCard(
                                episode = nextEpisode,
                                isLocked = isNextLocked,
                                onClick = {
                                    if (isNextLocked) {
                                        onNavigateToPremium()
                                    } else {
                                        onNavigateToAlternateEpisode(nextEpisode.epId)
                                    }
                                }
                            )

                            Spacer(modifier = Modifier.height(20.dp))
                        }
                    }
                }

                // Expandable more episodes catalog block
                item {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 24.dp)
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(12.dp))
                                .background(BackgroundSurface)
                                .border(1.dp, BorderColor, RoundedCornerShape(12.dp))
                                .clickable { isEpisodesExpanded = !isEpisodesExpanded }
                                .padding(14.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(
                                text = "EXPAND MORE EPISODES (" + episodesList.size + ")",
                                color = TextPrimary,
                                fontSize = 12.sp,
                                fontFamily = SpaceMonoFamily,
                                fontWeight = FontWeight.Bold
                            )
                            val metaLabel = if (isEpisodesExpanded) "COLLAPSE" else "EXPAND"
                            Text(
                                text = metaLabel,
                                color = AccentGlow,
                                fontSize = 11.sp,
                                fontFamily = SpaceMonoFamily,
                                fontWeight = FontWeight.Bold
                            )
                        }

                        // Collapsing / Expanding list logic
                        AnimatedVisibility(
                            visible = isEpisodesExpanded,
                            enter = expandVertically() + fadeIn(),
                            exit = shrinkVertically() + fadeOut()
                        ) {
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(top = 10.dp)
                            ) {
                                episodesList.forEach { ep ->
                                    val isEpLocked = !ep.isFree && !isUserPremium
                                    EpisodeCard(
                                        episode = ep,
                                        isLocked = isEpLocked,
                                        onClick = {
                                            if (isEpLocked) {
                                                onNavigateToPremium()
                                            } else {
                                                onNavigateToAlternateEpisode(ep.epId)
                                            }
                                        }
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@OptIn(UnstableApi::class)
@Composable
fun VideoPlayerFrame(
    videoUrl: String,
    onProgressUpdate: (Int) -> Unit
) {
    val context = LocalContext.current

    val exoPlayer = remember {
        ExoPlayer.Builder(context).build().apply {
            setMediaItem(MediaItem.fromUri(videoUrl))
            prepare()
            playWhenReady = true
        }
    }

    // Capture ticking current progress loops
    LaunchedEffect(exoPlayer) {
        while (true) {
            delay(3000)
            val currentPos = exoPlayer.currentPosition
            val totalDuration = exoPlayer.duration
            if (totalDuration > 0) {
                val percentage = ((currentPos * 100) / totalDuration).toInt()
                onProgressUpdate(percentage.coerceIn(0, 100))
            }
        }
    }

    DisposableEffect(videoUrl) {
        exoPlayer.setMediaItem(MediaItem.fromUri(videoUrl))
        exoPlayer.prepare()
        exoPlayer.playWhenReady = true
        onDispose {
            // release exoPlayer instance
        }
    }

    DisposableEffect(Unit) {
        onDispose {
            exoPlayer.release()
        }
    }

    AndroidView(
        factory = { ctx ->
            PlayerView(ctx).apply {
                player = exoPlayer
                useController = true
                layoutParams = FrameLayout.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    200
                )
            }
        },
        modifier = Modifier
            .fillMaxWidth()
            .height(230.dp)
            .padding(horizontal = 16.dp)
            .clip(RoundedCornerShape(12.dp))
            .background(Color.Black)
            .border(1.dp, BorderColor, RoundedCornerShape(12.dp))
    )
}
