package com.example.ui.screens

import androidx.compose.animation.core.*
import androidx.compose.foundation.*
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.example.model.Anime
import com.example.ui.components.AnimeCard
import com.example.ui.components.PremiumBadge
import com.example.ui.components.ShimmerCard
import com.example.ui.theme.*
import com.example.viewmodel.MainViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun HomeScreen(
    viewModel: MainViewModel,
    onNavigateToDetail: (animeId: String) -> Unit,
    onNavigateToProfile: () -> Unit,
    onNavigateToPremium: () -> Unit
) {
    val trendingList by viewModel.trendingAnimes.collectAsState()
    val latestList by viewModel.latestAnimes.collectAsState()
    val premiumPicksList by viewModel.premiumPicks.collectAsState()
    val currentUser by viewModel.currentUser.collectAsState()

    val scope = rememberCoroutineScope()

    // 5-second automatic sliding pager for our featured / trending heroes
    val bannerList = remember(trendingList) {
        trendingList.take(4)
    }
    val pagerState = rememberPagerState(pageCount = { bannerList.size.coerceAtLeast(1) })

    if (bannerList.isNotEmpty()) {
        LaunchedEffect(pagerState.currentPage) {
            delay(5000)
            val nextPage = (pagerState.currentPage + 1) % bannerList.size
            scope.launch {
                pagerState.animateScrollToPage(
                    page = nextPage,
                    animationSpec = spring(dampingRatio = 0.8f, stiffness = Spring.StiffnessLow)
                )
            }
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(BackgroundPrimary)
            .verticalScroll(rememberScrollState())
            .statusBarsPadding()
            .padding(bottom = 80.dp) // bottom nav clearance
    ) {
        // 1. Top Custom Appbar with Logo and Profile avatar
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp, vertical = 14.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column {
                Text(
                    text = "SAHIDANIME",
                    color = AccentPurple,
                    fontFamily = OrbitronFamily,
                    fontWeight = FontWeight.Bold,
                    fontSize = 24.sp,
                    letterSpacing = 2.sp,
                    modifier = Modifier.graphicsLayer {
                        shadowElevation = 8f
                        spotShadowColor = AccentGlow
                    }
                )
                Text(
                    text = "THE NEW ANIME UNIVERSE",
                    color = TextMuted,
                    fontSize = 8.sp,
                    fontFamily = SpaceMonoFamily,
                    letterSpacing = 1.sp
                )
            }

            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                IconButton(
                    onClick = { /* trigger notifications */ },
                    modifier = Modifier
                        .size(42.dp)
                        .clip(CircleShape)
                        .background(BackgroundSurface)
                        .border(1.dp, Color.White.copy(alpha = 0.05f), CircleShape)
                ) {
                    Icon(
                        imageVector = Icons.Default.Notifications,
                        contentDescription = "Alert notifications",
                        tint = TextPrimary,
                        modifier = Modifier.size(18.dp)
                    )
                }

                // Profile Avatar Circle
                Box(
                    modifier = Modifier
                        .size(42.dp)
                        .clip(CircleShape)
                        .background(
                            Brush.linearGradient(
                                colors = listOf(AccentPurple, AccentGlow)
                            )
                        )
                        .border(1.5.dp, Color.White.copy(alpha = 0.2f), CircleShape)
                        .clickable { onNavigateToProfile() },
                    contentAlignment = Alignment.Center
                ) {
                    val initial = currentUser?.name?.take(1)?.uppercase() ?: "F"
                    Text(
                        text = initial,
                        color = TextPrimary,
                        fontWeight = FontWeight.Bold,
                        fontFamily = OrbitronFamily,
                        fontSize = 15.sp
                    )
                }
            }
        }

        // 2. Hero banner slider with auto animation
        if (bannerList.isEmpty()) {
            ShimmerCard(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(240.dp)
                    .padding(20.dp)
            )
        } else {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(300.dp)
            ) {
                HorizontalPager(
                    state = pagerState,
                    modifier = Modifier.fillMaxSize()
                ) { page ->
                    val anime = bannerList[page]
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .clickable { onNavigateToDetail(anime.animeId) }
                    ) {
                        AsyncImage(
                            model = ImageRequest.Builder(LocalContext.current)
                                .data(anime.bannerImage)
                                .crossfade(true)
                                .build(),
                            contentDescription = anime.title,
                            modifier = Modifier.fillMaxSize(),
                            contentScale = ContentScale.Crop
                        )

                        // Outer gradient shadow masking the poster borders
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .background(
                                    Brush.verticalGradient(
                                        colors = listOf(
                                            Color.Transparent,
                                            Color.Black.copy(alpha = 0.5f),
                                            BackgroundPrimary
                                        ),
                                        startY = 100f
                                    )
                                )
                        )

                        // Left Overlay Details
                        Column(
                            modifier = Modifier
                                .fillAlignBottomStart()
                                .padding(20.dp)
                        ) {
                            // High contrast glowing status indicators instead of emojis
                            Row(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(6.dp))
                                    .background(AccentPurple.copy(alpha = 0.15f))
                                    .border(1.dp, AccentGlow.copy(alpha = 0.5f), RoundedCornerShape(6.dp))
                                    .padding(horizontal = 8.dp, vertical = 4.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(6.dp)
                                        .clip(CircleShape)
                                        .background(AccentGlow)
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = "FIRE SYSTEM TRENDING",
                                    color = AccentGlow,
                                    fontSize = 8.sp,
                                    fontWeight = FontWeight.Bold,
                                    fontFamily = SpaceMonoFamily
                                )
                            }

                            Spacer(modifier = Modifier.height(8.dp))

                            Text(
                                text = anime.title.uppercase(),
                                color = TextPrimary,
                                fontFamily = OrbitronFamily,
                                fontWeight = FontWeight.Bold,
                                fontSize = 23.sp,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )

                            Spacer(modifier = Modifier.height(4.dp))

                            Text(
                                text = anime.description,
                                color = TextPrimary.copy(alpha = 0.8f),
                                fontSize = 11.sp,
                                maxLines = 2,
                                overflow = TextOverflow.Ellipsis,
                                modifier = Modifier.fillMaxWidth(0.85f)
                            )

                            Spacer(modifier = Modifier.height(12.dp))

                            // Watch button
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier
                                    .clip(RoundedCornerShape(18.dp))
                                    .background(AccentPurple)
                                    .clickable { onNavigateToDetail(anime.animeId) }
                                    .padding(horizontal = 14.dp, vertical = 6.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.PlayArrow,
                                    contentDescription = "Play watch episode",
                                    tint = TextPrimary,
                                    modifier = Modifier.size(16.dp)
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(
                                    text = "WATCH NOW",
                                    color = TextPrimary,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 11.sp,
                                    fontFamily = SpaceMonoFamily
                                )
                            }
                        }
                    }
                }

                // Banner indicator dots
                Row(
                    modifier = Modifier
                        .align(Alignment.BottomEnd)
                        .padding(20.dp),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    bannerList.forEachIndexed { index, _ ->
                        Box(
                            modifier = Modifier
                                .size(if (pagerState.currentPage == index) 16.dp else 6.dp, 6.dp)
                                .clip(CircleShape)
                                .background(if (pagerState.currentPage == index) AccentPurple else TextMuted.copy(alpha = 0.5f))
                        )
                    }
                }
            }
        }

        // 3. Continue Watching Section (mock history values)
        val watchHistory = currentUser?.watchHistory ?: emptyList()
        if (watchHistory.isNotEmpty()) {
            Spacer(modifier = Modifier.height(18.dp))
            SectionHeader(title = "CONTINUE WATCHING", showAll = false)
            LazyRow(
                contentPadding = PaddingValues(horizontal = 20.dp),
                horizontalArrangement = Arrangement.spacedBy(14.dp),
                modifier = Modifier.padding(top = 10.dp)
            ) {
                items(watchHistory) { history ->
                    // Find anime details
                    val matchingAnime = latestList.find { it.animeId == history.animeId }
                    if (matchingAnime != null) {
                        ContinueWatchingCard(
                            anime = matchingAnime,
                            progress = history.progressPercentage,
                            epNum = history.epNumber,
                            onClick = { onNavigateToDetail(history.animeId) }
                        )
                    }
                }
            }
        }

        // 4. Latest Anime List Section
        Spacer(modifier = Modifier.height(24.dp))
        SectionHeader(title = "LATEST ANIME", showAll = true, onShowAllClick = { onNavigateToDetail("") })
        LazyRow(
            contentPadding = PaddingValues(horizontal = 20.dp),
            horizontalArrangement = Arrangement.spacedBy(14.dp),
            modifier = Modifier.padding(top = 10.dp)
        ) {
            items(latestList) { anime ->
                AnimeCard(anime = anime, onClick = { onNavigateToDetail(anime.animeId) })
            }
        }

        // 5. Trending Section with custom Rank Indicators!
        Spacer(modifier = Modifier.height(28.dp))
        SectionHeader(title = "TOP TRENDING", showAll = false)
        LazyRow(
            contentPadding = PaddingValues(horizontal = 20.dp),
            horizontalArrangement = Arrangement.spacedBy(20.dp),
            modifier = Modifier.padding(top = 12.dp)
        ) {
            itemsIndexed(trendingList) { index, anime ->
                Box(
                    modifier = Modifier.width(180.dp)
                ) {
                    Row(
                        verticalAlignment = Alignment.Bottom
                    ) {
                        // Massive tech ranking numbers instead of icons
                        Text(
                            text = (index + 1).toString(),
                            color = BorderColor.copy(alpha = 0.6f),
                            fontSize = 72.sp,
                            fontFamily = OrbitronFamily,
                            fontWeight = FontWeight.Black,
                            modifier = Modifier
                                .offset(y = 12.dp)
                                .graphicsLayer {
                                    shadowElevation = 4f
                                }
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        AnimeCard(
                            anime = anime,
                            modifier = Modifier.weight(1f),
                            onClick = { onNavigateToDetail(anime.animeId) }
                        )
                    }
                }
            }
        }

        // 6. Premium Picks Section
        Spacer(modifier = Modifier.height(28.dp))
        SectionHeader(
            title = "PREMIUM PICKS",
            showAll = true,
            titleColor = PremiumGold,
            onShowAllClick = { onNavigateToPremium() }
        )
        LazyRow(
            contentPadding = PaddingValues(horizontal = 20.dp),
            horizontalArrangement = Arrangement.spacedBy(14.dp),
            modifier = Modifier.padding(top = 10.dp)
        ) {
            items(premiumPicksList) { anime ->
                AnimeCard(anime = anime, onClick = { onNavigateToDetail(anime.animeId) })
            }
        }

        Spacer(modifier = Modifier.height(40.dp))
    }
}

@Composable
fun SectionHeader(
    title: String,
    showAll: Boolean,
    titleColor: Color = TextPrimary,
    onShowAllClick: () -> Unit = {}
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = title,
            color = titleColor,
            fontFamily = OrbitronFamily,
            fontWeight = FontWeight.Bold,
            fontSize = 15.sp,
            letterSpacing = 1.sp
        )
        if (showAll) {
            Text(
                text = "VIEW ALL",
                color = AccentGlow,
                fontSize = 10.sp,
                fontFamily = SpaceMonoFamily,
                fontWeight = FontWeight.Bold,
                modifier = Modifier
                    .clickable { onShowAllClick() }
                    .padding(4.dp)
            )
        }
    }
}

@Composable
fun ContinueWatchingCard(
    anime: Anime,
    progress: Int,
    epNum: Int,
    onClick: () -> Unit
) {
    var isPressed by remember { mutableStateOf(false) }
    val scale by animateFloatAsState(
        targetValue = if (isPressed) 0.97f else 1.0f,
        animationSpec = spring(dampingRatio = 0.8f, stiffness = 300f),
        label = "cw_scale"
    )

    Box(
        modifier = Modifier
            .width(220.dp)
            .graphicsLayer(scaleX = scale, scaleY = scale)
            .clip(RoundedCornerShape(12.dp))
            .background(BackgroundCard)
            .border(1.dp, BorderColor, RoundedCornerShape(12.dp))
            .clickable { onClick() }
            .pointerInput(Unit) {
                detectTapGestures(
                    onPress = {
                        isPressed = true
                        tryAwaitRelease()
                        isPressed = false
                    }
                )
            }
    ) {
        Column {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(110.dp)
            ) {
                AsyncImage(
                    model = ImageRequest.Builder(LocalContext.current)
                        .data(anime.bannerImage)
                        .crossfade(true)
                        .build(),
                    contentDescription = anime.title,
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Crop
                )

                // Dark semi-overlay
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(Color.Black.copy(alpha = 0.3f))
                )

                // Play icon centered overlay
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Box(
                        modifier = Modifier
                            .size(32.dp)
                            .clip(CircleShape)
                            .background(Color.Black.copy(alpha = 0.6f))
                            .border(1.dp, Color.White.copy(alpha = 0.3f), CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.PlayArrow,
                            contentDescription = "Resume play",
                            tint = TextPrimary,
                            modifier = Modifier.size(16.dp)
                        )
                    }
                }
            }

            // Progress Bar HUD
            LinearProgressIndicator(
                progress = { progress / 100f },
                color = AccentPurple,
                trackColor = BorderColor,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(3.dp)
            )

            Column(modifier = Modifier.padding(10.dp)) {
                Text(
                    text = anime.title,
                    color = TextPrimary,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "EPISODE $epNum",
                        color = AccentGlow,
                        fontSize = 9.sp,
                        fontFamily = SpaceMonoFamily,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "$progress% COMPLETE",
                        color = TextMuted,
                        fontSize = 8.sp,
                        fontFamily = SpaceMonoFamily
                    )
                }
            }
        }
    }
}

// Custom Alignment helper
private fun Modifier.fillAlignBottomStart() = this.fillMaxSize().wrapContentSize(Alignment.BottomStart)
