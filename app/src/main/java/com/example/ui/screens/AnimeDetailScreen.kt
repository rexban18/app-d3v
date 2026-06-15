package com.example.ui.screens

import android.widget.Toast
import androidx.compose.animation.*
import androidx.compose.animation.core.spring
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.blur
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.example.model.Anime
import com.example.model.Episode
import com.example.ui.components.EpisodeCard
import com.example.ui.components.GlassCard
import com.example.ui.components.GradientButton
import com.example.ui.theme.*
import com.example.viewmodel.MainViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AnimeDetailScreen(
    animeId: String,
    viewModel: MainViewModel,
    onNavigateBack: () -> Unit,
    onNavigateToWatch: (animeId: String, epId: String) -> Unit,
    onNavigateToPremium: () -> Unit,
    onNavigateToRedeem: () -> Unit
) {
    val context = LocalContext.current
    val animeList by viewModel.latestAnimes.collectAsState()
    val episodesByAnimeList by viewModel.getEpisodes(animeId).collectAsState(initial = emptyList())
    val currentUser by viewModel.currentUser.collectAsState()

    val anime = remember(animeList, animeId) {
        animeList.find { it.animeId == animeId }
    }

    var isLiked by remember(currentUser, animeId) {
        mutableStateOf(currentUser?.likedAnimes?.contains(animeId) ?: false)
    }

    var isDescExpanded by remember { mutableStateOf(false) }

    // Bottom sheet state for Premium upsell
    var showUpsellSheet by remember { mutableStateOf(false) }
    var selectedLockedEpisode by remember { mutableStateOf<Episode?>(null) }

    if (anime == null) {
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

    Scaffold(
        containerColor = BackgroundPrimary,
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text(
                        text = "ANIME PROFILE",
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
                            contentDescription = "Return back",
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
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(bottom = 30.dp)
            ) {
                // 1. Hero Graphics / Blurred header backdrop
                item {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(280.dp)
                    ) {
                        // Blurred Backdrop Banner
                        AsyncImage(
                            model = ImageRequest.Builder(LocalContext.current)
                                .data(anime.bannerImage)
                                .crossfade(true)
                                .build(),
                            contentDescription = null,
                            modifier = Modifier
                                .fillMaxSize()
                                .blur(12.dp),
                            contentScale = ContentScale.Crop
                        )

                        // Solid dark underlay overlay
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .background(Color.Black.copy(alpha = 0.6f))
                        )

                        // Center Content
                        Column(
                            modifier = Modifier.fillMaxSize(),
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.Center
                        ) {
                            // High contrast circular Glass card centering the front poster
                            Box(
                                modifier = Modifier
                                    .size(width = 110.dp, height = 150.dp)
                                    .clip(RoundedCornerShape(12.dp))
                                    .border(2.dp, Color.White.copy(alpha = 0.25f), RoundedCornerShape(12.dp))
                            ) {
                                AsyncImage(
                                    model = ImageRequest.Builder(LocalContext.current)
                                        .data(anime.poster)
                                        .crossfade(true)
                                        .build(),
                                    contentDescription = anime.title,
                                    modifier = Modifier.fillMaxSize(),
                                    contentScale = ContentScale.Crop
                                )
                            }

                            Spacer(modifier = Modifier.height(10.dp))

                            Text(
                                text = anime.title.uppercase(),
                                color = TextPrimary,
                                fontFamily = OrbitronFamily,
                                fontWeight = FontWeight.Bold,
                                fontSize = 20.sp,
                                textAlign = TextAlign.Center,
                                modifier = Modifier.padding(horizontal = 24.dp)
                            )
                        }

                        // Bottom gradient fading into background body
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .background(
                                    Brush.verticalGradient(
                                        colors = listOf(Color.Transparent, BackgroundPrimary),
                                        startY = 200f
                                    )
                                )
                        )
                    }
                }

                // Metadata items & Action Buttons
                item {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 20.dp)
                    ) {
                        // Metadata HUD Row (Rating and Episode totals)
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.Center,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            // Star rating
                            Row(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(BackgroundSurface)
                                    .padding(horizontal = 10.dp, vertical = 4.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Star,
                                    contentDescription = "Rating value",
                                    tint = PremiumGold,
                                    modifier = Modifier.size(14.dp)
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(
                                    text = anime.rating.toString(),
                                    color = TextPrimary,
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold,
                                    fontFamily = SpaceMonoFamily
                                )
                            }

                            Spacer(modifier = Modifier.width(12.dp))

                            // Fan Dub tag
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(BackgroundSurface)
                                    .padding(horizontal = 10.dp, vertical = 4.dp)
                            ) {
                                Text(
                                    text = "FAN DUB",
                                    color = TextPrimary,
                                    fontSize = 10.sp,
                                    fontFamily = SpaceMonoFamily,
                                    fontWeight = FontWeight.Bold
                                )
                            }

                            Spacer(modifier = Modifier.width(12.dp))

                            // Total episodes count
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(BackgroundSurface)
                                    .padding(horizontal = 10.dp, vertical = 4.dp)
                            ) {
                                Text(
                                    text = "${episodesByAnimeList.size} EPISODES",
                                    color = AccentGlow,
                                    fontSize = 10.sp,
                                    fontFamily = SpaceMonoFamily,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(18.dp))

                        // Category chips row
                        LazyRow(
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            items(anime.genres) { genre ->
                                Box(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(12.dp))
                                        .background(BackgroundSurface.copy(alpha = 0.5f))
                                        .border(1.dp, BorderColor, RoundedCornerShape(12.dp))
                                        .padding(horizontal = 10.dp, vertical = 4.dp)
                                ) {
                                    Text(
                                        text = genre.uppercase(),
                                        color = TextMuted,
                                        fontSize = 9.sp,
                                        fontFamily = SpaceMonoFamily,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(20.dp))

                        // Action buttons Row
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            // Large Watch Button
                            GradientButton(
                                text = "START WATCHING",
                                modifier = Modifier.weight(1f),
                                icon = Icons.Default.PlayArrow,
                                onClick = {
                                    val firstEp = episodesByAnimeList.firstOrNull()
                                    if (firstEp != null) {
                                        if (firstEp.isFree || currentUser?.isPremium == true) {
                                            onNavigateToWatch(anime.animeId, firstEp.epId)
                                        } else {
                                            selectedLockedEpisode = firstEp
                                            showUpsellSheet = true
                                        }
                                    } else {
                                        Toast.makeText(context, "No episodes available yet", Toast.LENGTH_SHORT).show()
                                    }
                                }
                            )

                            Spacer(modifier = Modifier.width(12.dp))

                            // Like Button
                            IconButton(
                                onClick = {
                                    isLiked = !isLiked
                                    viewModel.toggleLikeAnime(anime.animeId)
                                },
                                modifier = Modifier
                                    .size(50.dp)
                                    .clip(CircleShape)
                                    .background(BackgroundSurface)
                                    .border(1.dp, BorderColor, CircleShape)
                            ) {
                                Icon(
                                    imageVector = if (isLiked) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                                    contentDescription = "Like button",
                                    tint = if (isLiked) DangerRed else TextPrimary,
                                    modifier = Modifier.size(20.dp)
                                )
                            }

                            Spacer(modifier = Modifier.width(12.dp))

                            // Share Button
                            IconButton(
                                onClick = {
                                    Toast.makeText(context, "Anime profile link copied!", Toast.LENGTH_SHORT).show()
                                },
                                modifier = Modifier
                                    .size(50.dp)
                                    .clip(CircleShape)
                                    .background(BackgroundSurface)
                                    .border(1.dp, BorderColor, CircleShape)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Share,
                                    contentDescription = "Share anime",
                                    tint = TextPrimary,
                                    modifier = Modifier.size(20.dp)
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(20.dp))

                        // Glass card containing Description section
                        GlassCard(
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Column {
                                Text(
                                    text = "SYNOPSIS",
                                    color = TextPrimary,
                                    fontFamily = OrbitronFamily,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 12.sp,
                                    letterSpacing = 1.sp
                                )

                                Spacer(modifier = Modifier.height(6.dp))

                                // Description collapsing text
                                Column(
                                    modifier = Modifier.animateContentSize(animationSpec = spring())
                                ) {
                                    Text(
                                        text = anime.description,
                                        color = TextPrimary.copy(alpha = 0.8f),
                                        fontSize = 13.sp,
                                        lineHeight = 18.sp,
                                        maxLines = if (isDescExpanded) Int.MAX_VALUE else 3,
                                        overflow = TextOverflow.Ellipsis
                                    )

                                    Spacer(modifier = Modifier.height(4.dp))

                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.End
                                    ) {
                                        Text(
                                            text = if (isDescExpanded) "READ LESS" else "READ MORE",
                                            color = AccentGlow,
                                            fontSize = 9.sp,
                                            fontFamily = SpaceMonoFamily,
                                            fontWeight = FontWeight.Bold,
                                            modifier = Modifier
                                                .clickable { isDescExpanded = !isDescExpanded }
                                                .padding(4.dp)
                                        )
                                    }
                                }
                            }
                        }
                    }
                }

                // Episodes Section header
                item {
                    Spacer(modifier = Modifier.height(24.dp))
                    Text(
                        text = "EPISODES (${episodesByAnimeList.size})",
                        color = TextPrimary,
                        fontFamily = OrbitronFamily,
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp,
                        letterSpacing = 1.sp,
                        modifier = Modifier.padding(horizontal = 20.dp, vertical = 10.dp)
                    )
                }

                // Episode instances list
                if (episodesByAnimeList.isEmpty()) {
                    item {
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 20.dp),
                            colors = CardDefaults.cardColors(containerColor = BackgroundCard)
                        ) {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(24.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Text("No episodes recorded.", color = TextMuted, fontSize = 12.sp, fontFamily = SpaceMonoFamily)
                            }
                        }
                    }
                } else {
                    items(episodesByAnimeList) { episode ->
                        val isLocked = !episode.isFree && currentUser?.isPremium == false
                        Box(modifier = Modifier.padding(horizontal = 20.dp)) {
                            EpisodeCard(
                                episode = episode,
                                isLocked = isLocked,
                                onClick = {
                                    if (isLocked) {
                                        selectedLockedEpisode = episode
                                        showUpsellSheet = true
                                    } else {
                                        onNavigateToWatch(anime.animeId, episode.epId)
                                    }
                                }
                            )
                        }
                    }
                }
            }

            // M3 Modal Bottom Sheet for Premium Upsell
            if (showUpsellSheet) {
                ModalBottomSheet(
                    onDismissRequest = { showUpsellSheet = false },
                    containerColor = BackgroundSurface,
                    dragHandle = { BottomSheetDefaults.DragHandle(color = BorderColor) }
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 24.dp, vertical = 10.dp)
                            .padding(bottom = 36.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Box(
                            modifier = Modifier
                                .size(60.dp)
                                .clip(CircleShape)
                                .background(PremiumGold.copy(alpha = 0.15f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.Lock,
                                contentDescription = null,
                                tint = PremiumGold,
                                modifier = Modifier.size(28.dp)
                            )
                        }

                        Spacer(modifier = Modifier.height(18.dp))

                        Text(
                            text = "PREMIUM CONTENT LOCKED",
                            color = PremiumGold,
                            fontFamily = OrbitronFamily,
                            fontWeight = FontWeight.Bold,
                            fontSize = 18.sp,
                            letterSpacing = 1.sp
                        )

                        Spacer(modifier = Modifier.height(6.dp))

                        Text(
                            text = "Unlocks Episode ${selectedLockedEpisode?.epNumber ?: ""}: ${selectedLockedEpisode?.title ?: "Exclusive Access"}",
                            color = TextPrimary,
                            fontWeight = FontWeight.SemiBold,
                            fontSize = 13.sp,
                            textAlign = TextAlign.Center
                        )

                        Spacer(modifier = Modifier.height(6.dp))

                        Text(
                            text = "Upgrade to SahidAnime Premium to stream all exclusive episodes with high-speed ultra-servers and no-ads.",
                            color = TextMuted,
                            fontSize = 12.sp,
                            textAlign = TextAlign.Center,
                            modifier = Modifier.padding(horizontal = 14.dp)
                        )

                        Spacer(modifier = Modifier.height(28.dp))

                        // Subscribe option
                        GradientButton(
                            text = "UPGRADE TO PREMIUM NOW",
                            modifier = Modifier.fillMaxWidth(),
                            onClick = {
                                showUpsellSheet = false
                                onNavigateToPremium()
                            }
                        )

                        Spacer(modifier = Modifier.height(10.dp))

                        // Promo code option
                        OutlinedButton(
                            onClick = {
                                showUpsellSheet = false
                                onNavigateToRedeem()
                            },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(50.dp),
                            shape = RoundedCornerShape(25.dp),
                            border = borderStroke(1.dp, Color.White.copy(alpha = 0.15f)),
                            colors = ButtonDefaults.outlinedButtonColors(contentColor = TextPrimary)
                        ) {
                            Text(
                                text = "REDEEM PROMO CODE",
                                fontWeight = FontWeight.Bold,
                                fontSize = 12.sp,
                                fontFamily = SpaceMonoFamily
                            )
                        }
                    }
                }
            }
        }
    }
}
