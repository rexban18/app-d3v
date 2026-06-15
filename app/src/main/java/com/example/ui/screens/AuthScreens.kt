package com.example.ui.screens

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.blur
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.example.ui.components.GlassCard
import com.example.ui.components.GradientButton
import com.example.ui.theme.*
import com.example.viewmodel.MainViewModel
import com.example.viewmodel.UiState

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LoginScreen(
    viewModel: MainViewModel,
    onNavigateToRegister: () -> Unit,
    onLoginSuccess: () -> Unit
) {
    val context = LocalContext.current
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var isPasswordVisible by remember { mutableStateOf(false) }

    val authState by viewModel.authState.collectAsState()

    LaunchedEffect(authState) {
        if (authState is UiState.Success) {
            onLoginSuccess()
            viewModel.clearAuthState()
        } else if (authState is UiState.Error) {
            Toast.makeText(context, (authState as UiState.Error).message, Toast.LENGTH_LONG).show()
            viewModel.clearAuthState()
        }
    }

    Box(
        modifier = Modifier.fillMaxSize()
    ) {
        // Blurred background artwork representing anime style
        AsyncImage(
            model = ImageRequest.Builder(LocalContext.current)
                .data("https://images.unsplash.com/photo-1607604276583-eef5d076aa5f?w=1000&auto=format&fit=crop")
                .crossfade(true)
                .build(),
            contentDescription = null,
            modifier = Modifier
                .fillMaxSize()
                .blur(8.dp),
            contentScale = ContentScale.Crop
        )

        // Dark overlay
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.Black.copy(alpha = 0.85f))
        )

        // Scrollable glass card content
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(24.dp)
                .statusBarsPadding()
                .navigationBarsPadding(),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(30.dp))

            GlassCard(
                modifier = Modifier.fillMaxWidth(),
                backgroundAlpha = 0.6f,
                borderAlpha = 0.2f
            ) {
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "SAHIDANIME",
                        color = AccentPurple,
                        fontFamily = OrbitronFamily,
                        fontWeight = FontWeight.Bold,
                        fontSize = 32.sp,
                        letterSpacing = 4.sp
                    )

                    Spacer(modifier = Modifier.height(4.dp))

                    Text(
                        text = "COSMIC STREAMING ARCHIVE",
                        color = TextMuted,
                        fontSize = 10.sp,
                        fontFamily = SpaceMonoFamily,
                        fontWeight = FontWeight.Medium,
                        letterSpacing = 1.5.sp
                    )

                    Spacer(modifier = Modifier.height(28.dp))

                    // Email input
                    OutlinedTextField(
                        value = email,
                        onValueChange = { email = it },
                        label = { Text("Email Address", color = TextMuted, fontSize = 12.sp, fontFamily = SpaceMonoFamily) },
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
                        singleLine = true,
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email)
                    )

                    Spacer(modifier = Modifier.height(14.dp))

                    // Password input
                    OutlinedTextField(
                        value = password,
                        onValueChange = { password = it },
                        label = { Text("Password", color = TextMuted, fontSize = 12.sp, fontFamily = SpaceMonoFamily) },
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
                        singleLine = true,
                        visualTransformation = if (isPasswordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                        trailingIcon = {
                            val iconLabel = if (isPasswordVisible) "Hide" else "Show"
                            Text(
                                text = iconLabel,
                                color = AccentGlow,
                                fontSize = 11.sp,
                                fontFamily = SpaceMonoFamily,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier
                                    .clip(CircleShape)
                                    .clickable { isPasswordVisible = !isPasswordVisible }
                                    .padding(8.dp)
                            )
                        },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password)
                    )

                    Spacer(modifier = Modifier.height(18.dp))

                    // Forgot Password link
                    Box(
                        modifier = Modifier.fillMaxWidth(),
                        contentAlignment = Alignment.CenterEnd
                    ) {
                        Text(
                            text = "Forgot Password?",
                            color = TextMuted,
                            fontSize = 11.sp,
                            fontFamily = SpaceMonoFamily,
                            textDecoration = TextDecoration.Underline,
                            modifier = Modifier
                                .clickable {
                                    Toast.makeText(context, "Password reset link sent to $email", Toast.LENGTH_SHORT).show()
                                }
                        )
                    }

                    Spacer(modifier = Modifier.height(24.dp))

                    // Login Button with Loading indicator integrated
                    if (authState is UiState.Loading) {
                        CircularProgressIndicator(
                            color = AccentPurple,
                            modifier = Modifier.size(36.dp)
                        )
                    } else {
                        GradientButton(
                            text = "LOGIN",
                            modifier = Modifier.fillMaxWidth(),
                            onClick = {
                                if (email.isBlank() || password.isBlank()) {
                                    Toast.makeText(context, "Please write email and password credentials", Toast.LENGTH_SHORT).show()
                                } else {
                                    viewModel.login(email.trim(), password.trim())
                                }
                            }
                        )
                    }

                    Spacer(modifier = Modifier.height(20.dp))

                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.Center
                    ) {
                        Box(modifier = Modifier.weight(1f).height(1.dp).background(BorderColor))
                        Text(
                            text = "OR",
                            color = TextMuted,
                            fontSize = 11.sp,
                            fontFamily = SpaceMonoFamily,
                            modifier = Modifier.padding(horizontal = 14.dp)
                        )
                        Box(modifier = Modifier.weight(1f).height(1.dp).background(BorderColor))
                    }

                    Spacer(modifier = Modifier.height(20.dp))

                    // Google Login Option
                    OutlinedButton(
                        onClick = {
                            viewModel.login("google_user@gmail.com", "google")
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(50.dp),
                        shape = RoundedCornerShape(25.dp),
                        colors = ButtonDefaults.outlinedButtonColors(
                            contentColor = TextPrimary
                        ),
                        border = borderStroke(1.dp, Color.White.copy(alpha = 0.2f))
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            // High contrast custom indicator instead of emoji
                            Box(
                                modifier = Modifier
                                    .size(14.dp)
                                    .clip(CircleShape)
                                    .background(AccentGlow)
                            )
                            Spacer(modifier = Modifier.width(10.dp))
                            Text(
                                text = "LOGIN WITH GOOGLE",
                                fontWeight = FontWeight.Bold,
                                fontSize = 12.sp,
                                fontFamily = SpaceMonoFamily
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(28.dp))

                    // Tab switch
                    Row(
                        horizontalArrangement = Arrangement.Center,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "New here? ",
                            color = TextMuted,
                            fontSize = 13.sp
                        )
                        Text(
                            text = "Register",
                            color = AccentGlow,
                            fontSize = 13.sp,
                            fontFamily = SpaceMonoFamily,
                            fontWeight = FontWeight.Bold,
                            textDecoration = TextDecoration.Underline,
                            modifier = Modifier
                                .clickable { onNavigateToRegister() }
                        )
                    }
                }
            }
            Spacer(modifier = Modifier.height(40.dp))
        }
    }
}

// Utility for border stroke compilation safety
fun borderStroke(width: androidx.compose.ui.unit.Dp, color: Color) = androidx.compose.foundation.BorderStroke(width, color)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RegisterScreen(
    viewModel: MainViewModel,
    onNavigateToLogin: () -> Unit,
    onRegisterSuccess: () -> Unit
) {
    val context = LocalContext.current
    var fullName by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }
    var termsAccepted by remember { mutableStateOf(false) }
    var isPasswordVisible by remember { mutableStateOf(false) }

    val authState by viewModel.authState.collectAsState()

    LaunchedEffect(authState) {
        if (authState is UiState.Success) {
            onRegisterSuccess()
            viewModel.clearAuthState()
        } else if (authState is UiState.Error) {
            Toast.makeText(context, (authState as UiState.Error).message, Toast.LENGTH_LONG).show()
            viewModel.clearAuthState()
        }
    }

    Box(
        modifier = Modifier.fillMaxSize()
    ) {
        // Blurred background artwork representing anime style
        AsyncImage(
            model = ImageRequest.Builder(LocalContext.current)
                .data("https://images.unsplash.com/photo-1607604276583-eef5d076aa5f?w=1000&auto=format&fit=crop")
                .crossfade(true)
                .build(),
            contentDescription = null,
            modifier = Modifier
                .fillMaxSize()
                .blur(8.dp),
            contentScale = ContentScale.Crop
        )

        // Dark overlay
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.Black.copy(alpha = 0.85f))
        )

        // Scrollable glass card content
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(24.dp)
                .statusBarsPadding()
                .navigationBarsPadding(),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(20.dp))

            GlassCard(
                modifier = Modifier.fillMaxWidth(),
                backgroundAlpha = 0.6f,
                borderAlpha = 0.2f
            ) {
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "CREATE ACCOUNT",
                        color = HighlightColorText(),
                        fontFamily = OrbitronFamily,
                        fontWeight = FontWeight.Bold,
                        fontSize = 24.sp,
                        letterSpacing = 2.sp
                    )

                    Spacer(modifier = Modifier.height(4.dp))

                    Text(
                        text = "JOIN THE STREAMING EMPIRE",
                        color = TextMuted,
                        fontSize = 10.sp,
                        fontFamily = SpaceMonoFamily,
                        letterSpacing = 1.sp
                    )

                    Spacer(modifier = Modifier.height(24.dp))

                    // Full Name input
                    OutlinedTextField(
                        value = fullName,
                        onValueChange = { fullName = it },
                        label = { Text("Full Name", color = TextMuted, fontSize = 12.sp, fontFamily = SpaceMonoFamily) },
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

                    Spacer(modifier = Modifier.height(12.dp))

                    // Email input
                    OutlinedTextField(
                        value = email,
                        onValueChange = { email = it },
                        label = { Text("Email Address", color = TextMuted, fontSize = 12.sp, fontFamily = SpaceMonoFamily) },
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
                        singleLine = true,
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email)
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    // Password input
                    OutlinedTextField(
                        value = password,
                        onValueChange = { password = it },
                        label = { Text("Password", color = TextMuted, fontSize = 12.sp, fontFamily = SpaceMonoFamily) },
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
                        singleLine = true,
                        visualTransformation = if (isPasswordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                        trailingIcon = {
                            val iconLabel = if (isPasswordVisible) "Hide" else "Show"
                            Text(
                                text = iconLabel,
                                color = AccentGlow,
                                fontSize = 11.sp,
                                fontFamily = SpaceMonoFamily,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier
                                    .clip(CircleShape)
                                    .clickable { isPasswordVisible = !isPasswordVisible }
                                    .padding(8.dp)
                            )
                        },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password)
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    // Confirm Password input
                    OutlinedTextField(
                        value = confirmPassword,
                        onValueChange = { confirmPassword = it },
                        label = { Text("Confirm Password", color = TextMuted, fontSize = 12.sp, fontFamily = SpaceMonoFamily) },
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
                        singleLine = true,
                        visualTransformation = PasswordVisualTransformation(),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password)
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    // Terms check box
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Checkbox(
                            checked = termsAccepted,
                            onCheckedChange = { termsAccepted = it },
                            colors = CheckboxDefaults.colors(
                                checkedColor = AccentPurple,
                                uncheckedColor = TextMuted
                            )
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "Accept terms and conditions",
                            color = TextPrimary,
                            fontSize = 11.sp,
                            fontFamily = SpaceMonoFamily,
                            modifier = Modifier.clickable { termsAccepted = !termsAccepted }
                        )
                    }

                    Spacer(modifier = Modifier.height(24.dp))

                    // Create account button
                    if (authState is UiState.Loading) {
                        CircularProgressIndicator(
                            color = AccentPurple,
                            modifier = Modifier.size(36.dp)
                        )
                    } else {
                        GradientButton(
                            text = "CREATE ACCOUNT",
                            modifier = Modifier.fillMaxWidth(),
                            onClick = {
                                if (fullName.isBlank() || email.isBlank() || password.isBlank()) {
                                    Toast.makeText(context, "Please complete fields", Toast.LENGTH_SHORT).show()
                                } else if (password != confirmPassword) {
                                    Toast.makeText(context, "Passwords do not match", Toast.LENGTH_SHORT).show()
                                } else if (!termsAccepted) {
                                    Toast.makeText(context, "Please accept conditions to proceed", Toast.LENGTH_SHORT).show()
                                } else {
                                    viewModel.registerUser(fullName.trim(), email.trim(), password.trim())
                                }
                            }
                        )
                    }

                    Spacer(modifier = Modifier.height(24.dp))

                    // Tab switch
                    Row(
                        horizontalArrangement = Arrangement.Center,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Already registered? ",
                            color = TextMuted,
                            fontSize = 13.sp
                        )
                        Text(
                            text = "Login",
                            color = AccentGlow,
                            fontSize = 13.sp,
                            fontFamily = SpaceMonoFamily,
                            fontWeight = FontWeight.Bold,
                            textDecoration = TextDecoration.Underline,
                            modifier = Modifier
                                .clickable { onNavigateToLogin() }
                        )
                    }
                }
            }
            Spacer(modifier = Modifier.height(40.dp))
        }
    }
}

@Composable
fun HighlightColorText(): Color = AccentPurple
