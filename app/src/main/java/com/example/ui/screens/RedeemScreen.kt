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
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RedeemScreen(
    viewModel: MainViewModel,
    onNavigateBack: () -> Unit
) {
    val context = LocalContext.current
    var codeText by remember { mutableStateOf("") }
    val redeemState by viewModel.codeRedeemState.collectAsState()

    var showSuccessDialog by remember { mutableStateOf(false) }
    var grantedPromoDays by remember { mutableStateOf(0) }

    LaunchedEffect(redeemState) {
        if (redeemState is UiState.Success) {
            grantedPromoDays = (redeemState as UiState.Success<Int>).data
            showSuccessDialog = true
            viewModel.clearRedeemState()
        } else if (redeemState is UiState.Error) {
            Toast.makeText(context, (redeemState as UiState.Error).message, Toast.LENGTH_LONG).show()
            viewModel.clearRedeemState()
        }
    }

    Scaffold(
        containerColor = BackgroundPrimary,
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text(
                        text = "REDEEM VAULT",
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
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .background(BackgroundPrimary)
                .verticalScroll(rememberScrollState())
                .padding(24.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            GlassCard(
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Box(
                        modifier = Modifier
                            .size(54.dp)
                            .clip(CircleShape)
                            .background(AccentPurple.copy(alpha = 0.15f))
                            .border(1.dp, AccentGlow.copy(alpha = 0.6f), CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Star,
                            contentDescription = null,
                            tint = AccentGlow,
                            modifier = Modifier.size(24.dp)
                        )
                    }

                    Spacer(modifier = Modifier.height(18.dp))

                    Text(
                        text = "COUPON UNLOCK CORE",
                        color = TextPrimary,
                        fontFamily = OrbitronFamily,
                        fontWeight = FontWeight.Bold,
                        fontSize = 18.sp,
                        letterSpacing = 1.5.sp
                    )

                    Spacer(modifier = Modifier.height(4.dp))

                    Text(
                        text = "ENTER SECURE CODE FOR BONUS HOURS",
                        color = TextMuted,
                        fontSize = 9.sp,
                        fontFamily = SpaceMonoFamily,
                        textAlign = TextAlign.Center
                    )

                    Spacer(modifier = Modifier.height(28.dp))

                    // Capitialization helper input field
                    OutlinedTextField(
                        value = codeText,
                        onValueChange = { codeText = it.uppercase() },
                        placeholder = {
                            Text(
                                "E.G. SAHID100",
                                color = TextMuted,
                                fontSize = 12.sp,
                                fontFamily = SpaceMonoFamily
                            )
                        },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = AccentPurple,
                            unfocusedBorderColor = BorderColor,
                            focusedTextColor = TextPrimary,
                            unfocusedTextColor = TextPrimary,
                            focusedContainerColor = BackgroundSurface.copy(alpha = 0.5f),
                            unfocusedContainerColor = BackgroundSurface.copy(alpha = 0.5f)
                        ),
                        singleLine = true
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    Text(
                        text = "All codes are case-sensitive. Redeemed codes grant instant, non-reversible premium periods.",
                        color = TextMuted,
                        fontSize = 11.sp,
                        textAlign = TextAlign.Center,
                        lineHeight = 15.sp,
                        modifier = Modifier.padding(horizontal = 10.dp)
                    )

                    Spacer(modifier = Modifier.height(28.dp))

                    if (redeemState is UiState.Loading) {
                        CircularProgressIndicator(color = AccentPurple)
                    } else {
                        GradientButton(
                            text = "APPLY CODE",
                            modifier = Modifier.fillMaxWidth(),
                            onClick = {
                                if (codeText.isBlank()) {
                                    Toast.makeText(context, "Please enter a valid code format", Toast.LENGTH_SHORT).show()
                                } else {
                                    viewModel.redeemCode(codeText.trim())
                                }
                            }
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(30.dp))

            // Info guide card
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = BackgroundCard),
                border = borderStroke(1.dp, BorderColor)
            ) {
                Column(
                    modifier = Modifier.padding(18.dp)
                ) {
                    Text(
                        text = "PROMOTIONAL DEALS",
                        color = AccentPurple,
                        fontFamily = OrbitronFamily,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 1.sp
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "Redeem codes are distributed exclusively during seasonal launches, user surveys, or through sponsor groups. Active premium users can still stack bonus days.",
                        color = TextMuted,
                        fontSize = 11.sp,
                        lineHeight = 16.sp
                    )
                }
            }

            // Success celebration AlertDialog
            if (showSuccessDialog) {
                AlertDialog(
                    onDismissRequest = { showSuccessDialog = false },
                    containerColor = BackgroundCard,
                    title = {
                        Text(
                            text = "VAULT COMMITTED",
                            fontFamily = OrbitronFamily,
                            fontWeight = FontWeight.Bold,
                            color = FreeGreen,
                            fontSize = 16.sp,
                            textAlign = TextAlign.Center,
                            modifier = Modifier.fillMaxWidth()
                        )
                    },
                    text = {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(50.dp)
                                    .clip(CircleShape)
                                    .background(FreeGreen.copy(alpha = 0.15f)),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Check,
                                    contentDescription = null,
                                    tint = FreeGreen,
                                    modifier = Modifier.size(24.dp)
                                )
                            }
                            Spacer(modifier = Modifier.height(14.dp))
                            Text(
                                text = "SUCCESSFULLY UNLOCKED!",
                                color = TextPrimary,
                                fontWeight = FontWeight.Bold,
                                fontSize = 14.sp
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = "$grantedPromoDays Premium Membership days have been successfully loaded onto your profile.",
                                color = TextMuted,
                                fontSize = 12.sp,
                                textAlign = TextAlign.Center
                            )
                        }
                    },
                    confirmButton = {
                        TextButton(
                            onClick = {
                                showSuccessDialog = false
                                codeText = ""
                                onNavigateBack()
                            }
                        ) {
                            Text(
                                text = "PROCEED NOW",
                                color = AccentPurple,
                                fontFamily = SpaceMonoFamily,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                )
            }
        }
    }
}
