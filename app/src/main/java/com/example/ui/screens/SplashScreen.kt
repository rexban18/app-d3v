package com.example.ui.screens

import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.BackgroundPrimary
import com.example.ui.theme.OrbitronFamily
import kotlinx.coroutines.delay
import kotlin.random.Random

@Composable
fun SplashScreen(
    onboardingCompleted: Boolean,
    isLoggedIn: Boolean,
    onNavigateNext: (route: String) -> Unit
) {
    // Falling Sakura Petals particle system
    val petals = remember {
        List(25) {
            SakuraPetal(
                xOffset = Random.nextFloat(),
                yOffset = Random.nextFloat() * -1f,
                speed = 0.05f + Random.nextFloat() * 0.1f,
                size = 10f + Random.nextFloat() * 15f,
                wobbleSpeed = 2f + Random.nextFloat() * 4f,
                wobbleWidth = 10f + Random.nextFloat() * 20f
            )
        }
    }

    val infiniteTransition = rememberInfiniteTransition(label = "pulse")
    val alphaGlow by infiniteTransition.animateFloat(
        initialValue = 0.3f,
        targetValue = 0.9f,
        animationSpec = infiniteRepeatable(
            animation = tween(1200, easing = LinearOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "logo_glow"
    )

    // Safe ticking frame update flow using standard Jetpack Compose rules
    var frameTick by remember { mutableStateOf(0) }

    LaunchedEffect(Unit) {
        // Background thread safe particle modifier loop
        while (true) {
            delay(16) // ~60fps
            petals.forEach { petal ->
                petal.yOffset += petal.speed * 0.05f
                if (petal.yOffset > 1.2f) {
                    petal.yOffset = -0.1f
                    petal.xOffset = Random.nextFloat()
                }
            }
            frameTick++
        }
    }

    LaunchedEffect(Unit) {
        delay(1500)
        onNavigateNext("main")
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(BackgroundPrimary),
        contentAlignment = Alignment.Center
    ) {
        // Draw falling sakura petals
        Canvas(modifier = Modifier.fillMaxSize()) {
            val width = size.width
            val height = size.height

            // Read the frame tick to trigger Canvas redraw properly
            val tick = frameTick

            petals.forEach { petal ->
                val currentX = (petal.xOffset * width) + 
                        (kotlin.math.sin(petal.yOffset * petal.wobbleSpeed) * petal.wobbleWidth)
                val currentY = petal.yOffset * height

                drawCircle(
                    color = Color(0xFFFFB7C5).copy(alpha = 0.5f),
                    radius = petal.size,
                    center = Offset(currentX, currentY)
                )
            }
        }

        // Center Logo Group
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = "SAHIDANIME",
                color = com.example.ui.theme.AccentPurple,
                fontFamily = OrbitronFamily,
                fontWeight = FontWeight.Bold,
                fontSize = 38.sp,
                letterSpacing = 4.sp,
                modifier = Modifier.graphicsLayer {
                    shadowElevation = 15f
                    ambientShadowColor = com.example.ui.theme.AccentGlow.copy(alpha = alphaGlow)
                    spotShadowColor = com.example.ui.theme.AccentGlow.copy(alpha = alphaGlow)
                }
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "YOUR GATEWAY TO COSMIC STREAMING",
                color = com.example.ui.theme.TextMuted,
                fontFamily = OrbitronFamily,
                fontWeight = FontWeight.Normal,
                fontSize = 10.sp,
                letterSpacing = 2.sp
            )
        }
    }
}

private class SakuraPetal(
    var xOffset: Float,
    var yOffset: Float,
    val speed: Float,
    val size: Float,
    val wobbleSpeed: Float,
    val wobbleWidth: Float
)
