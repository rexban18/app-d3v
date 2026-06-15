package com.example.ui.screens

import android.widget.Toast
import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.components.GlassCard
import com.example.ui.components.GradientButton
import com.example.ui.theme.*
import com.example.viewmodel.MainViewModel
import com.example.viewmodel.UiState

@Composable
fun PremiumScreen(
    viewModel: MainViewModel,
    onNavigateToRedeemUrl: () -> Unit
) {
    val context = LocalContext.current
    val plans by viewModel.premiumPlans.collectAsState()
    val paymentState by viewModel.paymentState.collectAsState()
    val currentUser by viewModel.currentUser.collectAsState()

    var selectedPlanId by remember { mutableStateOf("") }

    LaunchedEffect(paymentState) {
        if (paymentState is UiState.Success) {
            Toast.makeText(context, "Welcome to SahidAnime Premium!", Toast.LENGTH_LONG).show()
            viewModel.clearPaymentState()
        } else if (paymentState is UiState.Error) {
            Toast.makeText(context, (paymentState as UiState.Error).message, Toast.LENGTH_SHORT).show()
            viewModel.clearPaymentState()
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(BackgroundPrimary)
            .verticalScroll(rememberScrollState())
            .statusBarsPadding()
            .padding(bottom = 90.dp) // bottom nav clearance
    ) {
        // Top Premium Glow Banner Header
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(24.dp),
            contentAlignment = Alignment.Center
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Box(
                    modifier = Modifier
                        .size(48.dp)
                        .clip(CircleShape)
                        .background(PremiumGold.copy(alpha = 0.15f))
                        .border(1.dp, PremiumGold, CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Star,
                        contentDescription = null,
                        tint = PremiumGold,
                        modifier = Modifier.size(24.dp)
                    )
                }

                Spacer(modifier = Modifier.height(14.dp))

                Text(
                    text = "SAHIDANIME PREMIUM",
                    color = PremiumGold,
                    fontFamily = OrbitronFamily,
                    fontWeight = FontWeight.Bold,
                    fontSize = 20.sp,
                    letterSpacing = 1.5.sp
                )

                Spacer(modifier = Modifier.height(4.dp))

                val subLabel = if (currentUser?.isPremium == true) "YOUR PLAN INTENSITY ACTIVATED" else "UNLOCK EXCLUSIVE ANIME ACCESS"
                Text(
                    text = subLabel,
                    color = TextPrimary,
                    fontFamily = OrbitronFamily,
                    fontWeight = FontWeight.Normal,
                    fontSize = 10.sp,
                    letterSpacing = 1.sp
                )
            }
        }

        // Active premium user banner if already upgraded
        if (currentUser?.isPremium == true) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 24.dp, vertical = 6.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(PremiumGold.copy(alpha = 0.12f))
                    .border(1.dp, PremiumGold.copy(alpha = 0.4f), RoundedCornerShape(12.dp))
                    .padding(16.dp),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = "PREMIUM MEMBERSHIP STATUS ACTIVE",
                        color = PremiumGold,
                        fontWeight = FontWeight.Bold,
                        fontSize = 12.sp,
                        fontFamily = SpaceMonoFamily
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "All catalog restrictions have been suspended.",
                        color = TextPrimary,
                        fontSize = 11.sp,
                        textAlign = TextAlign.Center
                    )
                }
            }
        }

        // Features comparative list
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp, vertical = 12.dp)
        ) {
            Text(
                text = "ACCESS ADVANTAGES",
                color = TextMuted,
                fontSize = 11.sp,
                fontFamily = SpaceMonoFamily,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(bottom = 12.dp)
            )

            val benefits = listOf(
                "Stream latest releases immediately (No delay timers)",
                "Full high-definition 1080p and 4K ultra quality player",
                "Completely blank ads (Uninterrupted session content)",
                "Support dual multi-audio formats and subtitle tracks"
            )

            benefits.forEach { benefit ->
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.padding(vertical = 5.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(16.dp)
                            .clip(CircleShape)
                            .background(AccentPurple.copy(alpha = 0.15f))
                            .border(1.dp, AccentGlow.copy(alpha = 0.5f), CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Check,
                            contentDescription = null,
                            tint = AccentGlow,
                            modifier = Modifier.size(10.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(10.dp))
                    Text(
                        text = benefit,
                        color = TextPrimary,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Medium
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(18.dp))

        // Subscription Plans select block
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp)
        ) {
            Text(
                text = "CHOOSE YOUR TIERS PLAN",
                color = TextMuted,
                fontSize = 11.sp,
                fontFamily = SpaceMonoFamily,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(bottom = 12.dp)
            )

            // Dynamic grid layout
            if (plans.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(110.dp),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator(color = AccentPurple)
                }
            } else {
                plans.forEach { plan ->
                    val isSelected = selectedPlanId == plan.planId
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 8.dp)
                            .clip(RoundedCornerShape(16.dp))
                            .background(if (isSelected) BackgroundSurface else BackgroundCard)
                            .border(
                                width = 1.5.dp,
                                color = if (isSelected) PremiumGold else BorderColor,
                                shape = RoundedCornerShape(16.dp)
                            )
                            .clickable { selectedPlanId = plan.planId }
                            .padding(20.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column {
                                Box(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(6.dp))
                                        .background(if (isSelected) PremiumGold.copy(alpha = 0.15f) else BorderColor)
                                        .padding(horizontal = 8.dp, vertical = 2.dp)
                                ) {
                                    Text(
                                        text = "${plan.durationDays} DAYS",
                                        color = if (isSelected) PremiumGold else TextMuted,
                                        fontSize = 9.sp,
                                        fontFamily = SpaceMonoFamily,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                                Spacer(modifier = Modifier.height(6.dp))
                                Text(
                                    text = plan.name.uppercase(),
                                    color = TextPrimary,
                                    fontSize = 16.sp,
                                    fontWeight = FontWeight.Bold,
                                    fontFamily = OrbitronFamily
                                )
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = plan.features.firstOrNull() ?: "Full Access Bundle Included",
                                    color = TextMuted,
                                    fontSize = 11.sp
                                )
                            }

                            Column(
                                horizontalAlignment = Alignment.End
                            ) {
                                Text(
                                    text = "INR ${plan.price}",
                                    color = if (isSelected) PremiumGold else TextPrimary,
                                    fontSize = 18.sp,
                                    fontFamily = SpaceMonoFamily,
                                    fontWeight = FontWeight.Bold
                                )
                                Text(
                                    text = "SECURE PAY",
                                    color = TextMuted,
                                    fontSize = 9.sp,
                                    fontFamily = SpaceMonoFamily
                                )
                            }
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        // CTA Payment buttons
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            if (paymentState is UiState.Loading) {
                CircularProgressIndicator(color = AccentPurple)
            } else {
                GradientButton(
                    text = "PURCHASE SELECTED PLAN",
                    modifier = Modifier.fillMaxWidth(),
                    onClick = {
                        if (selectedPlanId.isBlank()) {
                            Toast.makeText(context, "Please click to select a pricing plan", Toast.LENGTH_SHORT).show()
                        } else {
                            viewModel.purchasePlan(selectedPlanId)
                        }
                    }
                )
            }

            Spacer(modifier = Modifier.height(18.dp))

            // Non-paying route Link to Redeem code tab directly
            Text(
                text = "HAVE A COUPON REDEEM CODE?",
                color = AccentGlow,
                fontSize = 11.sp,
                fontFamily = SpaceMonoFamily,
                fontWeight = FontWeight.Bold,
                modifier = Modifier
                    .clickable { onNavigateToRedeemUrl() }
                    .padding(8.dp)
            )
        }
    }
}
