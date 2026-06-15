package com.example.ui.screens

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.components.GlassCard
import com.example.ui.components.GradientButton
import com.example.ui.theme.*
import kotlinx.coroutines.launch

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun OnboardingScreen(
    onComplete: () -> Unit,
    onNavigateToLogin: () -> Unit,
    onNavigateToRegister: () -> Unit
) {
    val pagerState = rememberPagerState(pageCount = { 3 })
    val scope = rememberCoroutineScope()

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(BackgroundPrimary)
            .statusBarsPadding()
            .navigationBarsPadding()
    ) {
        // Skip Button (top-right, only on page 0 and 1)
        if (pagerState.currentPage < 2) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                contentAlignment = Alignment.TopEnd
            ) {
                Text(
                    text = "SKIP",
                    color = TextMuted,
                    fontSize = 13.sp,
                    fontFamily = SpaceMonoFamily,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier
                        .clip(RoundedCornerShape(8.dp))
                        .clickable {
                            onComplete()
                            onNavigateToLogin()
                        }
                        .padding(horizontal = 12.dp, vertical = 6.dp)
                )
            }
        }

        // Horizontal Pager Layout
        HorizontalPager(
            state = pagerState,
            modifier = Modifier.fillMaxSize()
        ) { page ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(24.dp),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                when (page) {
                    0 -> {
                        // Page 1: Watch Anime Anywhere
                        GlassCard(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(260.dp),
                            backgroundAlpha = 0.25f
                        ) {
                            Box(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .background(
                                        Brush.radialGradient(
                                            colors = listOf(AccentPurple.copy(alpha = 0.15f), Color.Transparent),
                                        )
                                    ),
                                contentAlignment = Alignment.Center
                            ) {
                                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                    Box(
                                        modifier = Modifier
                                            .size(width = 160.dp, height = 90.dp)
                                            .clip(RoundedCornerShape(8.dp))
                                            .background(BackgroundSurface)
                                            .border(1.dp, Color.White.copy(alpha = 0.2f), RoundedCornerShape(8.dp)),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Text("< VIDEO PLAYER >", color = AccentGlow, fontSize = 10.sp, fontFamily = SpaceMonoFamily)
                                    }
                                    Spacer(modifier = Modifier.height(16.dp))
                                    Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                                        Box(modifier = Modifier.size(24.dp).clip(CircleShape).background(AccentPurple))
                                        Box(modifier = Modifier.size(width = 80.dp, height = 24.dp).clip(RoundedCornerShape(12.dp)).background(BackgroundSurface))
                                    }
                                }
                            }
                        }
                        Spacer(modifier = Modifier.height(36.dp))
                        Text(
                            text = "WATCH ANYWHERE",
                            color = TextPrimary,
                            style = MaterialTheme.typography.displayMedium,
                            fontWeight = FontWeight.Bold,
                            textAlign = TextAlign.Center
                        )
                        Spacer(modifier = Modifier.height(12.dp))
                        Text(
                            text = "Stream your favorite legendary releases and exclusive seasonal broadcasts on any portable device instantly, without disruptions.",
                            color = TextMuted,
                            style = MaterialTheme.typography.bodyMedium,
                            textAlign = TextAlign.Center,
                            modifier = Modifier.padding(horizontal = 16.dp)
                        )
                    }
                    1 -> {
                        // Page 2: Premium HD Episodes
                        GlassCard(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(260.dp),
                            backgroundAlpha = 0.25f
                        ) {
                            Column(
                                modifier = Modifier.fillMaxSize(),
                                verticalArrangement = Arrangement.Center
                            ) {
                                Text(
                                    text = "PREMIUM FEATURES",
                                    color = PremiumGold,
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold,
                                    fontFamily = SpaceMonoFamily,
                                    modifier = Modifier.padding(bottom = 12.dp)
                                )
                                val pFeatures = listOf(
                                    "Ultra High Definition Quality Stream",
                                    "Dual Premium Multi-audio and Fan dubs",
                                    "Full offline playback caching support",
                                    "Instant code unlock for bonus points"
                                )
                                pFeatures.forEach { feature ->
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        modifier = Modifier.padding(vertical = 4.dp)
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.Check,
                                            contentDescription = "Feature Unlocked",
                                            tint = FreeGreen,
                                            modifier = Modifier.size(16.dp)
                                        )
                                        Spacer(modifier = Modifier.width(8.dp))
                                        Text(
                                            text = feature,
                                            color = TextPrimary,
                                            fontSize = 13.sp,
                                            fontWeight = FontWeight.Medium
                                        )
                                    }
                                }
                            }
                        }
                        Spacer(modifier = Modifier.height(36.dp))
                        Text(
                            text = "PREMIUM HIGH DEF",
                            color = TextPrimary,
                            style = MaterialTheme.typography.displayMedium,
                            fontWeight = FontWeight.Bold,
                            textAlign = TextAlign.Center
                        )
                        Spacer(modifier = Modifier.height(12.dp))
                        Text(
                            text = "Unlock access to premium episodes with unparalleled visual sharpness and dedicated server speeds.",
                            color = TextMuted,
                            style = MaterialTheme.typography.bodyMedium,
                            textAlign = TextAlign.Center,
                            modifier = Modifier.padding(horizontal = 16.dp)
                        )
                    }
                    2 -> {
                        // Page 3: Join SahidAnime
                        GlassCard(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(260.dp),
                            backgroundAlpha = 0.25f
                        ) {
                            Box(
                                modifier = Modifier.fillMaxSize(),
                                contentAlignment = Alignment.Center
                            ) {
                                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                    Text(
                                        text = "SAHIDANIME",
                                        color = AccentPurple,
                                        fontFamily = OrbitronFamily,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 28.sp,
                                        letterSpacing = 3.sp
                                    )
                                    Spacer(modifier = Modifier.height(12.dp))
                                    Text(
                                        text = "CREATING THE NEW STANDARD",
                                        color = TextMuted,
                                        fontSize = 11.sp,
                                        fontFamily = SpaceMonoFamily
                                    )
                                }
                            }
                        }
                        Spacer(modifier = Modifier.height(36.dp))
                        Text(
                            text = "JOIN SAHIDANIME",
                            color = TextPrimary,
                            style = MaterialTheme.typography.displayMedium,
                            fontWeight = FontWeight.Bold,
                            textAlign = TextAlign.Center
                        )
                        Spacer(modifier = Modifier.height(12.dp))
                        Text(
                            text = "Explore user forums, create custom watch playlists, and experience next-generation immersive anime streaming with a fluid hub.",
                            color = TextMuted,
                            style = MaterialTheme.typography.bodyMedium,
                            textAlign = TextAlign.Center,
                            modifier = Modifier.padding(horizontal = 16.dp)
                        )
                    }
                }
            }
        }

        // Footer Section: Indicator dots & CTA Buttons
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .align(Alignment.BottomCenter)
                .padding(24.dp)
        ) {
            if (pagerState.currentPage < 2) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    // Dot Indicators
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        for (i in 0..2) {
                            Box(
                                modifier = Modifier
                                    .size(8.dp)
                                    .clip(CircleShape)
                                    .background(if (pagerState.currentPage == i) AccentPurple else TextMuted.copy(alpha = 0.4f))
                            )
                        }
                    }

                    // Next Button
                    Button(
                        onClick = {
                            scope.launch {
                                pagerState.animateScrollToPage(pagerState.currentPage + 1)
                            }
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = AccentPurple),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Text("NEXT", color = TextPrimary, fontWeight = FontWeight.Bold, fontFamily = SpaceMonoFamily)
                    }
                }
            } else {
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    GradientButton(
                        text = "GET STARTED",
                        modifier = Modifier.fillMaxWidth(),
                        onClick = {
                            onComplete()
                            onNavigateToLogin()
                        }
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    Text(
                        text = "CREATE AN ACCOUNT",
                        color = AccentGlow,
                        fontSize = 11.sp,
                        fontFamily = SpaceMonoFamily,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier
                            .clickable {
                                onComplete()
                                onNavigateToRegister()
                            }
                            .padding(8.dp)
                    )
                }
            }
        }
    }
}
