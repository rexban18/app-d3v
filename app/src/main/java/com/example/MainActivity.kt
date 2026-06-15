package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.repository.AnimeRepositoryImpl
import com.example.ui.screens.*
import com.example.ui.theme.*
import com.example.viewmodel.MainViewModel
import com.example.viewmodel.MainViewModelFactory

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            MyApplicationTheme {
                // Initialize clean falling-back repository instance
                val repository = remember { AnimeRepositoryImpl(applicationContext) }
                val factory = remember { MainViewModelFactory(repository) }
                val mainViewModel: MainViewModel = viewModel(factory = factory)

                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    Box(modifier = Modifier.padding(innerPadding)) {
                        MainAppNavigation(viewModel = mainViewModel)
                    }
                }
            }
        }
    }
}

@Composable
fun MainAppNavigation(viewModel: MainViewModel) {
    val navController = rememberNavController()
    val onboardingCompleted by viewModel.onboardingCompleted.collectAsState()
    val currentUser by viewModel.currentUser.collectAsState()

    NavHost(
        navController = navController,
        startDestination = "splash"
    ) {
        // Splash route entry
        composable("splash") {
            SplashScreen(
                onboardingCompleted = onboardingCompleted,
                isLoggedIn = currentUser != null,
                onNavigateNext = { dest ->
                    navController.navigate(dest) {
                        popUpTo("splash") { inclusive = true }
                    }
                }
            )
        }

        // Onboarding swipe wizard
        composable("onboarding") {
            OnboardingScreen(
                onComplete = {
                    viewModel.completeOnboarding()
                },
                onNavigateToLogin = {
                    navController.navigate("login") {
                        popUpTo("onboarding") { inclusive = true }
                    }
                },
                onNavigateToRegister = {
                    navController.navigate("register") {
                        popUpTo("onboarding") { inclusive = true }
                    }
                }
            )
        }

        // Standard Login
        composable("login") {
            LoginScreen(
                viewModel = viewModel,
                onNavigateToRegister = {
                    navController.navigate("register")
                },
                onLoginSuccess = {
                    navController.navigate("main") {
                        popUpTo("login") { inclusive = true }
                    }
                }
            )
        }

        // Standard Register
        composable("register") {
            RegisterScreen(
                viewModel = viewModel,
                onNavigateToLogin = {
                    navController.navigate("login") {
                        popUpTo("register") { inclusive = true }
                    }
                },
                onRegisterSuccess = {
                    navController.navigate("main") {
                        popUpTo("register") { inclusive = true }
                    }
                }
            )
        }

        // Core Bottom Nav Scaffold Screen
        composable("main") {
            MainScaffoldScreen(
                viewModel = viewModel,
                onNavigateToDetail = { animeId ->
                    navController.navigate("animeDetail/$animeId")
                },
                onNavigateToRedeem = {
                    navController.navigate("redeem")
                },
                onNavigateBackToAuth = {
                    navController.navigate("login") {
                        popUpTo("main") { inclusive = true }
                    }
                }
            )
        }

        // Detail pages
        composable("animeDetail/{animeId}") { backStackEntry ->
            val animeId = backStackEntry.arguments?.getString("animeId") ?: ""
            AnimeDetailScreen(
                animeId = animeId,
                viewModel = viewModel,
                onNavigateBack = { navController.popBackStack() },
                onNavigateToWatch = { aId, epId ->
                    navController.navigate("watch/$aId/$epId")
                },
                onNavigateToPremium = {
                    navController.navigate("main") {
                        // Keeps backstack cleanly focused on main, switching tabs to Premium
                    }
                },
                onNavigateToRedeem = {
                    navController.navigate("redeem")
                }
            )
        }

        // Streaming and player pages
        composable("watch/{animeId}/{epId}") { backStackEntry ->
            val animeId = backStackEntry.arguments?.getString("animeId") ?: ""
            val epId = backStackEntry.arguments?.getString("epId") ?: ""
            WatchScreen(
                animeId = animeId,
                epId = epId,
                viewModel = viewModel,
                onNavigateBack = { navController.popBackStack() },
                onNavigateToPremium = {
                    navController.navigate("main")
                },
                onNavigateToAlternateEpisode = { altEpId ->
                    navController.navigate("watch/$animeId/$altEpId") {
                        popUpTo("watch/$animeId/$epId") { inclusive = true }
                    }
                }
            )
        }

        // Dedicated promo screen
        composable("redeem") {
            RedeemScreen(
                viewModel = viewModel,
                onNavigateBack = { navController.popBackStack() }
            )
        }
    }
}

// Complete Tab Scaffolder using Liquid Glass elements
@Composable
fun MainScaffoldScreen(
    viewModel: MainViewModel,
    onNavigateToDetail: (animeId: String) -> Unit,
    onNavigateToRedeem: () -> Unit,
    onNavigateBackToAuth: () -> Unit
) {
    var selectedTab by remember { mutableStateOf("home") }

    Scaffold(
        containerColor = BackgroundPrimary,
        bottomBar = {
            // Elegant Translucent Liquid Glass Floating Navigation Bar!
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 12.dp)
                    .navigationBarsPadding(),
                contentAlignment = Alignment.BottomCenter
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(65.dp)
                        .clip(RoundedCornerShape(30.dp))
                        .background(BackgroundCard.copy(alpha = 0.85f))
                        .border(1.dp, Color.White.copy(alpha = 0.12f), RoundedCornerShape(30.dp))
                        .padding(horizontal = 10.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    BottomNavTabItem(
                        icon = Icons.Default.Home,
                        label = "HOME",
                        isSelected = selectedTab == "home",
                        onClick = { selectedTab = "home" }
                    )
                    BottomNavTabItem(
                        icon = Icons.Default.PlayArrow,
                        label = "ANIME",
                        isSelected = selectedTab == "animeList",
                        onClick = { selectedTab = "animeList" }
                    )
                    BottomNavTabItem(
                        icon = Icons.Default.Search,
                        label = "SEARCH",
                        isSelected = selectedTab == "search",
                        onClick = { selectedTab = "search" }
                    )
                    BottomNavTabItem(
                        icon = Icons.Default.Star,
                        label = "PREM",
                        isSelected = selectedTab == "premium",
                        onClick = { selectedTab = "premium" }
                    )
                    BottomNavTabItem(
                        icon = Icons.Default.Person,
                        label = "PROFIL",
                        isSelected = selectedTab == "profile",
                        onClick = { selectedTab = "profile" }
                    )
                }
            }
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            // Render Selected Tab screen
            when (selectedTab) {
                "home" -> HomeScreen(
                    viewModel = viewModel,
                    onNavigateToDetail = onNavigateToDetail,
                    onNavigateToProfile = { selectedTab = "profile" },
                    onNavigateToPremium = { selectedTab = "premium" }
                )
                "animeList" -> AnimeListScreen(
                    viewModel = viewModel,
                    onNavigateToDetail = onNavigateToDetail,
                    onTriggerSearchTab = { selectedTab = "search" }
                )
                "search" -> SearchScreen(
                    viewModel = viewModel,
                    onNavigateToDetail = onNavigateToDetail
                )
                "premium" -> PremiumScreen(
                    viewModel = viewModel,
                    onNavigateToRedeemUrl = onNavigateToRedeem
                )
                "profile" -> ProfileScreen(
                    viewModel = viewModel,
                    onNavigateToRedeemUrl = onNavigateToRedeem,
                    onNavigateToPremiumUrl = { selectedTab = "premium" },
                    onNavigateBackToAuth = onNavigateBackToAuth
                )
            }
        }
    }
}

@Composable
fun BottomNavTabItem(
    icon: ImageVector,
    label: String,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    val colorAccent = if (isSelected) AccentGlow else TextMuted
    val scaleFactor by animateFloatAsState(
        targetValue = if (isSelected) 1.05f else 1.0f,
        animationSpec = spring(stiffness = 200f, dampingRatio = 0.7f),
        label = "nav_scale"
    )

    Column(
        modifier = Modifier
            .width(55.dp)
            .graphicsLayer(scaleX = scaleFactor, scaleY = scaleFactor)
            .clip(CircleShape)
            .clickable { onClick() }
            .padding(vertical = 4.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Icon(
            imageVector = icon,
            contentDescription = label,
            tint = colorAccent,
            modifier = Modifier.size(20.dp)
        )
        Spacer(modifier = Modifier.height(2.dp))
        Text(
            text = label,
            color = colorAccent,
            fontSize = 9.sp,
            fontFamily = SpaceMonoFamily,
            fontWeight = FontWeight.Bold
        )
    }
}
