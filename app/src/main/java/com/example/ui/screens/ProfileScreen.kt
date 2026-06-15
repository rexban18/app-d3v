package com.example.ui.screens

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.horizontalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.ExitToApp
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.components.GlassCard
import com.example.ui.components.GradientButton
import com.example.ui.theme.*
import com.example.viewmodel.MainViewModel

@Composable
fun ProfileScreen(
    viewModel: MainViewModel,
    onNavigateToRedeemUrl: () -> Unit,
    onNavigateToPremiumUrl: () -> Unit,
    onNavigateBackToAuth: () -> Unit
) {
    val context = LocalContext.current
    val currentUser by viewModel.currentUser.collectAsState()
    val allAnimes by viewModel.latestAnimes.collectAsState()

    var showAddAnimeDialog by remember { mutableStateOf(false) }
    var showAddEpisodeDialog by remember { mutableStateOf(false) }
    var showClearDatabaseDialog by remember { mutableStateOf(false) }

    // Interative avatar labels
    val avatarChoices = remember {
        listOf("SAMURAI", "NINJA", "CYBORG", "WIZARD", "PIRATE", "MECH")
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(BackgroundPrimary)
            .verticalScroll(rememberScrollState())
            .statusBarsPadding()
            .padding(bottom = 90.dp) // bottom nav clearance
    ) {
        // 1. Top Graphic HUD
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(24.dp),
            contentAlignment = Alignment.Center
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                // Huge dynamic current avatar circle badge
                Box(
                    modifier = Modifier
                        .size(90.dp)
                        .clip(CircleShape)
                        .background(AccentPurple)
                        .border(2.dp, AccentGlow, CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    val initial = currentUser?.avatar?.take(1)?.uppercase() ?: "N"
                    Text(
                        text = initial,
                        color = TextPrimary,
                        fontWeight = FontWeight.Bold,
                        fontFamily = OrbitronFamily,
                        fontSize = 32.sp
                    )
                }

                Spacer(modifier = Modifier.height(14.dp))

                Text(
                    text = currentUser?.name?.uppercase() ?: "USER PROFILE",
                    color = TextPrimary,
                    fontFamily = OrbitronFamily,
                    fontWeight = FontWeight.Bold,
                    fontSize = 18.sp,
                    letterSpacing = 1.sp
                )

                Text(
                    text = currentUser?.email ?: "",
                    color = TextMuted,
                    fontSize = 11.sp,
                    fontFamily = SpaceMonoFamily
                )
            }
        }

        // 2. Interactive Avatar picker choices row
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp, vertical = 6.dp)
        ) {
            Text(
                text = "SWAP COSMIC CLASS PROFILE AVATAR",
                color = TextMuted,
                fontSize = 10.sp,
                fontFamily = SpaceMonoFamily,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(bottom = 8.dp)
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                avatarChoices.forEach { role ->
                    val isSelected = currentUser?.avatar == role
                    Box(
                        modifier = Modifier
                            .size(width = 50.dp, height = 32.dp)
                            .clip(RoundedCornerShape(8.dp))
                            .background(if (isSelected) AccentPurple else BackgroundCard)
                            .border(
                                1.dp,
                                if (isSelected) AccentGlow else BorderColor,
                                RoundedCornerShape(8.dp)
                            )
                            .clickable {
                                viewModel.updateProfileAvatar(role)
                                Toast.makeText(context, "Avatar loaded to $role", Toast.LENGTH_SHORT).show()
                            },
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = role.take(3),
                            color = if (isSelected) TextPrimary else TextMuted,
                            fontSize = 10.sp,
                            fontFamily = SpaceMonoFamily,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(14.dp))

        // Membership info status card (Unlocks / Premium billing statuses)
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp, vertical = 6.dp)
        ) {
            GlassCard(
                modifier = Modifier.fillMaxWidth()
            ) {
                Column {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "CATALOG MEMBERSHIP TIER",
                            color = TextPrimary,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            fontFamily = SpaceMonoFamily
                        )

                        if (currentUser?.isPremium == true) {
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(6.dp))
                                    .background(PremiumGold.copy(alpha = 0.15f))
                                    .padding(horizontal = 8.dp, vertical = 3.dp)
                            ) {
                                Text(
                                    text = "PREMIUM",
                                    color = PremiumGold,
                                    fontSize = 9.sp,
                                    fontWeight = FontWeight.Bold,
                                    fontFamily = SpaceMonoFamily
                                )
                            }
                        } else {
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(6.dp))
                                    .background(BorderColor)
                                    .padding(horizontal = 8.dp, vertical = 3.dp)
                            ) {
                                Text(
                                    text = "FREE ACCOUNT",
                                    color = TextMuted,
                                    fontSize = 9.sp,
                                    fontWeight = FontWeight.Bold,
                                    fontFamily = SpaceMonoFamily
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    val subText = if (currentUser?.isPremium == true) {
                        "Congratulations, you have full access to our global servers, exclusive seasonal and fan dub episodes."
                    } else {
                        "Get standard streaming options. Unlocked premium picks or episodes require tier upgrades."
                    }
                    Text(
                        text = subText,
                        color = TextMuted,
                        fontSize = 11.sp,
                        lineHeight = 15.sp
                    )

                    if (currentUser?.isPremium == false) {
                        Spacer(modifier = Modifier.height(14.dp))
                        GradientButton(
                            text = "UPGRADE TO PREMIUM",
                            modifier = Modifier.fillMaxWidth(),
                            onClick = onNavigateToPremiumUrl
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(14.dp))

        // Security / Settings Section List
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp)
        ) {
            Text(
                text = "COSMIC GUEST SETTINGS",
                color = TextMuted,
                fontSize = 10.sp,
                fontFamily = SpaceMonoFamily,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(bottom = 8.dp)
            )

            ProfileItemAction(
                label = "REDEEM VIP PROMO CODES",
                subLabel = "Unlock bonus premium streaming periods",
                icon = Icons.Default.Check,
                onClick = onNavigateToRedeemUrl
            )

            ProfileItemAction(
                label = "ACTIVE GUEST SESSION",
                subLabel = "All pro, custom content & download features unlocked",
                icon = Icons.Default.Check,
                tint = AccentGlow,
                onClick = {
                    Toast.makeText(context, "Premium Dynamic Guest session is active!", Toast.LENGTH_SHORT).show()
                }
            )
        }

        Spacer(modifier = Modifier.height(20.dp))

        // Room Database Admin HUD Controls
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp)
        ) {
            Text(
                text = "LOCAL DATABASE (ROOM) CONTROLS",
                color = TextMuted,
                fontSize = 10.sp,
                fontFamily = SpaceMonoFamily,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(bottom = 8.dp)
            )

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 4.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(AccentPurple.copy(alpha = 0.12f))
                    .border(1.dp, AccentPurple.copy(alpha = 0.3f), RoundedCornerShape(12.dp))
                    .padding(horizontal = 14.dp, vertical = 10.dp)
            ) {
                Text(
                    text = "STATUS: ${allAnimes.size} ANIME ENTITIES FOUND IN DB",
                    color = TextPrimary,
                    fontSize = 11.sp,
                    fontFamily = SpaceMonoFamily,
                    fontWeight = FontWeight.Bold
                )
            }

            ProfileItemAction(
                label = "ADD NEW ANIME ENTRY",
                subLabel = "Insert a brand new custom anime to local database",
                icon = Icons.Default.Add,
                tint = AccentGlow,
                onClick = { showAddAnimeDialog = true }
            )

            ProfileItemAction(
                label = "ADD ANIME STREAM EPISODE",
                subLabel = "Attach custom video stream URL (HLS m3u8 / MP4) to anime ID",
                icon = Icons.Default.PlayArrow,
                tint = AccentPurple,
                onClick = { showAddEpisodeDialog = true }
            )

            ProfileItemAction(
                label = "CLEAR ROOM ANIME DATABASE",
                subLabel = "Clear all custom anime, stream videos, and episode rows",
                icon = Icons.Default.Delete,
                tint = DangerRed,
                onClick = { showClearDatabaseDialog = true }
            )
        }

        Spacer(modifier = Modifier.height(20.dp))

        // CONFIRMATION POPUPS / MODAL FLOATS

        // 1. ADD NEW ANIME ENTRY Dialog
        if (showAddAnimeDialog) {
            var animeId by remember { mutableStateOf("") }
            var title by remember { mutableStateOf("") }
            var poster by remember { mutableStateOf("") }
            var bannerImage by remember { mutableStateOf("") }
            var description by remember { mutableStateOf("") }
            var genres by remember { mutableStateOf("") }
            var rating by remember { mutableStateOf("9.0") }
            var totalEpisodes by remember { mutableStateOf("12") }
            var isTrending by remember { mutableStateOf(true) }
            var isPremium by remember { mutableStateOf(false) }

            AlertDialog(
                onDismissRequest = { showAddAnimeDialog = false },
                containerColor = BackgroundSurface,
                title = {
                    Text(
                        "ADD NEW ANIME TO ROOM DB",
                        color = TextPrimary,
                        fontFamily = OrbitronFamily,
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold
                    )
                },
                text = {
                    Column(
                        modifier = Modifier.verticalScroll(rememberScrollState()),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        OutlinedTextField(
                            value = animeId,
                            onValueChange = { animeId = it },
                            label = { Text("Unique Anime ID (e.g. naruto)", color = TextMuted, fontSize = 11.sp) },
                            singleLine = true,
                            colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = AccentPurple, unfocusedBorderColor = BorderColor)
                        )
                        OutlinedTextField(
                            value = title,
                            onValueChange = { title = it },
                            label = { Text("Anime Title", color = TextMuted, fontSize = 11.sp) },
                            singleLine = true,
                            colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = AccentPurple, unfocusedBorderColor = BorderColor)
                        )
                        OutlinedTextField(
                            value = poster,
                            onValueChange = { poster = it },
                            label = { Text("Poster Image URL", color = TextMuted, fontSize = 11.sp) },
                            singleLine = true,
                            colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = AccentPurple, unfocusedBorderColor = BorderColor)
                        )
                        OutlinedTextField(
                            value = bannerImage,
                            onValueChange = { bannerImage = it },
                            label = { Text("Banner Image URL", color = TextMuted, fontSize = 11.sp) },
                            singleLine = true,
                            colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = AccentPurple, unfocusedBorderColor = BorderColor)
                        )
                        OutlinedTextField(
                            value = description,
                            onValueChange = { description = it },
                            label = { Text("Description", color = TextMuted, fontSize = 11.sp) },
                            colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = AccentPurple, unfocusedBorderColor = BorderColor)
                        )
                        OutlinedTextField(
                            value = genres,
                            onValueChange = { genres = it },
                            label = { Text("Genres (Comma separated)", color = TextMuted, fontSize = 11.sp) },
                            singleLine = true,
                            colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = AccentPurple, unfocusedBorderColor = BorderColor)
                        )
                        OutlinedTextField(
                            value = rating,
                            onValueChange = { rating = it },
                            label = { Text("Rating (Double, e.g. 9.5)", color = TextMuted, fontSize = 11.sp) },
                            singleLine = true,
                            colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = AccentPurple, unfocusedBorderColor = BorderColor)
                        )
                        OutlinedTextField(
                            value = totalEpisodes,
                            onValueChange = { totalEpisodes = it },
                            label = { Text("Total Episodes Count", color = TextMuted, fontSize = 11.sp) },
                            singleLine = true,
                            colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = AccentPurple, unfocusedBorderColor = BorderColor)
                        )
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Checkbox(checked = isTrending, onCheckedChange = { isTrending = it }, colors = CheckboxDefaults.colors(checkedColor = AccentPurple))
                            Text("Trending (Home Slider)", color = TextPrimary, fontSize = 11.sp, fontFamily = SpaceMonoFamily)
                        }
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Checkbox(checked = isPremium, onCheckedChange = { isPremium = it }, colors = CheckboxDefaults.colors(checkedColor = AccentPurple))
                            Text("Premium (VIP Streaming)", color = TextPrimary, fontSize = 11.sp, fontFamily = SpaceMonoFamily)
                        }
                    }
                },
                confirmButton = {
                    TextButton(
                        onClick = {
                            if (animeId.isBlank() || title.isBlank()) {
                                Toast.makeText(context, "Anime ID and Title are mandatory fields", Toast.LENGTH_SHORT).show()
                            } else {
                                val cleanPoster = poster.ifBlank { "https://images.unsplash.com/photo-1578632767115-351597cf2477?w=500" }
                                val cleanBanner = bannerImage.ifBlank { "https://images.unsplash.com/photo-1578632767115-351597cf2477?w=1000" }
                                val newAnime = com.example.model.Anime(
                                    animeId = animeId.trim().lowercase(),
                                    title = title.trim(),
                                    poster = cleanPoster.trim(),
                                    bannerImage = cleanBanner.trim(),
                                    description = description.trim(),
                                    genres = genres.split(",").map { it.trim() }.filter { it.isNotBlank() },
                                    rating = rating.toDoubleOrNull() ?: 9.0,
                                    totalEpisodes = totalEpisodes.toIntOrNull() ?: 12,
                                    isTrending = isTrending,
                                    isPremium = isPremium
                                )
                                viewModel.insertAnime(newAnime)
                                showAddAnimeDialog = false
                                Toast.makeText(context, "Anime: '${title}' inserted into Room!", Toast.LENGTH_SHORT).show()
                            }
                        }
                    ) {
                        Text("INSERT", color = AccentPurple, fontFamily = SpaceMonoFamily, fontWeight = FontWeight.Bold)
                    }
                },
                dismissButton = {
                    TextButton(onClick = { showAddAnimeDialog = false }) {
                        Text("CANCEL", color = TextPrimary, fontFamily = SpaceMonoFamily)
                    }
                }
            )
        }

        // 2. ADD ANIME STREAM EPISODE Dialog
        if (showAddEpisodeDialog) {
            var selectedAnimeId by remember { mutableStateOf("") }
            var epNumber by remember { mutableStateOf("1") }
            var epTitle by remember { mutableStateOf("") }
            var duration by remember { mutableStateOf("24:00") }
            var videoUrl by remember { mutableStateOf("") }
            var isFree by remember { mutableStateOf(true) }

            AlertDialog(
                onDismissRequest = { showAddEpisodeDialog = false },
                containerColor = BackgroundSurface,
                title = {
                    Text(
                        "ADD STREAM EPISODE TO ANIME",
                        color = TextPrimary,
                        fontFamily = OrbitronFamily,
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold
                    )
                },
                text = {
                    Column(
                        modifier = Modifier.verticalScroll(rememberScrollState()),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Text("Existing Anime IDs in database:", color = TextMuted, fontSize = 10.sp, fontFamily = SpaceMonoFamily)
                        if (allAnimes.isEmpty()) {
                            Text("No Anime found. Add Anime first!", color = DangerRed, fontSize = 11.sp)
                        } else {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .horizontalScroll(rememberScrollState())
                                    .padding(vertical = 4.dp),
                                horizontalArrangement = Arrangement.spacedBy(6.dp)
                            ) {
                                allAnimes.forEach { item ->
                                    Box(
                                        modifier = Modifier
                                            .clip(RoundedCornerShape(6.dp))
                                            .background(if (selectedAnimeId == item.animeId) AccentPurple else BackgroundCard)
                                            .border(1.dp, if (selectedAnimeId == item.animeId) AccentGlow else BorderColor, RoundedCornerShape(6.dp))
                                            .clickable { selectedAnimeId = item.animeId }
                                            .padding(horizontal = 8.dp, vertical = 4.dp)
                                    ) {
                                        Text(item.animeId, color = TextPrimary, fontSize = 10.sp, fontFamily = SpaceMonoFamily)
                                    }
                                }
                            }
                        }

                        OutlinedTextField(
                            value = selectedAnimeId,
                            onValueChange = { selectedAnimeId = it },
                            label = { Text("Target Anime ID", color = TextMuted, fontSize = 11.sp) },
                            singleLine = true,
                            colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = AccentPurple, unfocusedBorderColor = BorderColor)
                        )
                        OutlinedTextField(
                            value = epNumber,
                            onValueChange = { epNumber = it },
                            label = { Text("Episode Number (Int, e.g. 1)", color = TextMuted, fontSize = 11.sp) },
                            singleLine = true,
                            colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = AccentPurple, unfocusedBorderColor = BorderColor)
                        )
                        OutlinedTextField(
                            value = epTitle,
                            onValueChange = { epTitle = it },
                            label = { Text("Episode Title (Optional)", color = TextMuted, fontSize = 11.sp) },
                            singleLine = true,
                            colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = AccentPurple, unfocusedBorderColor = BorderColor)
                        )
                        OutlinedTextField(
                            value = duration,
                            onValueChange = { duration = it },
                            label = { Text("Duration (e.g. 24:15)", color = TextMuted, fontSize = 11.sp) },
                            singleLine = true,
                            colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = AccentPurple, unfocusedBorderColor = BorderColor)
                        )
                        OutlinedTextField(
                            value = videoUrl,
                            onValueChange = { videoUrl = it },
                            label = { Text("Video Stream Link (MP4 or HLS m3u8 URL)", color = TextMuted, fontSize = 11.sp) },
                            singleLine = true,
                            colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = AccentPurple, unfocusedBorderColor = BorderColor)
                        )
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Checkbox(checked = isFree, onCheckedChange = { isFree = it }, colors = CheckboxDefaults.colors(checkedColor = AccentPurple))
                            Text("Free (Non-premium tier users can watch)", color = TextPrimary, fontSize = 11.sp, fontFamily = SpaceMonoFamily)
                        }
                    }
                },
                confirmButton = {
                    TextButton(
                        onClick = {
                            if (selectedAnimeId.isBlank() || videoUrl.isBlank()) {
                                Toast.makeText(context, "Anime ID and Video URL are mandatory fields", Toast.LENGTH_SHORT).show()
                            } else {
                                val cleanEpTitle = epTitle.ifBlank { "Episode #$epNumber" }
                                val epNumInt = epNumber.toIntOrNull() ?: 1
                                val currentAnime = allAnimes.find { it.animeId == selectedAnimeId }
                                val newEpisode = com.example.model.Episode(
                                    epId = "${selectedAnimeId}_ep_$epNumInt",
                                    animeId = selectedAnimeId,
                                    epNumber = epNumInt,
                                    title = cleanEpTitle,
                                    duration = duration,
                                    videoUrl = videoUrl.trim(),
                                    isFree = isFree,
                                    thumbnailUrl = currentAnime?.poster ?: "https://images.unsplash.com/photo-1578632767115-351597cf2477?w=500"
                                )
                                viewModel.insertEpisode(newEpisode)
                                showAddEpisodeDialog = false
                                Toast.makeText(context, "Episode inserted securely in Room!", Toast.LENGTH_SHORT).show()
                            }
                        }
                    ) {
                        Text("INSERT EP", color = AccentPurple, fontFamily = SpaceMonoFamily, fontWeight = FontWeight.Bold)
                    }
                },
                dismissButton = {
                    TextButton(onClick = { showAddEpisodeDialog = false }) {
                        Text("CANCEL", color = TextPrimary, fontFamily = SpaceMonoFamily)
                    }
                }
            )
        }

        // 3. CLEAR ENTIRE DATABASE Dialog
        if (showClearDatabaseDialog) {
            AlertDialog(
                onDismissRequest = { showClearDatabaseDialog = false },
                containerColor = BackgroundSurface,
                title = {
                    Text(
                        "CLEAR ROOM ANIME DATABASE",
                        color = DangerRed,
                        fontFamily = OrbitronFamily,
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold
                    )
                },
                text = {
                    Text(
                        "Are you absolutely sure you want to clear all custom anime listings and stream video databases inside Room? This action cannot be undone.",
                        color = TextMuted,
                        fontSize = 12.sp
                    )
                },
                confirmButton = {
                    TextButton(
                        onClick = {
                            showClearDatabaseDialog = false
                            viewModel.clearAllAnime()
                            Toast.makeText(context, "Anime local database cleared!", Toast.LENGTH_SHORT).show()
                        }
                    ) {
                        Text("CLEAR DATA", color = DangerRed, fontFamily = SpaceMonoFamily, fontWeight = FontWeight.Bold)
                    }
                },
                dismissButton = {
                    TextButton(onClick = { showClearDatabaseDialog = false }) {
                        Text("ABORT", color = TextPrimary, fontFamily = SpaceMonoFamily)
                    }
                }
            )
        }
    }
}

@Composable
fun ProfileItemAction(
    label: String,
    subLabel: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    tint: Color = AccentGlow,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 6.dp)
            .clip(RoundedCornerShape(12.dp))
            .background(BackgroundCard)
            .border(1.dp, BorderColor, RoundedCornerShape(12.dp))
            .clickable { onClick() }
            .padding(14.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(36.dp)
                .clip(CircleShape)
                .background(tint.copy(alpha = 0.1f)),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = tint,
                modifier = Modifier.size(16.dp)
            )
        }

        Spacer(modifier = Modifier.width(14.dp))

        Column(
            modifier = Modifier.weight(1f)
        ) {
            Text(
                text = label,
                color = TextPrimary,
                fontSize = 11.sp,
                fontFamily = SpaceMonoFamily,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = subLabel,
                color = TextMuted,
                fontSize = 10.sp,
                lineHeight = 13.sp
            )
        }
    }
}
