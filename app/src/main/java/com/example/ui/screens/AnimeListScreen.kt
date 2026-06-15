package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.Anime
import com.example.ui.components.AnimeCard
import com.example.ui.theme.*
import com.example.viewmodel.MainViewModel

@Composable
fun AnimeListScreen(
    viewModel: MainViewModel,
    onNavigateToDetail: (animeId: String) -> Unit,
    onTriggerSearchTab: () -> Unit
) {
    val animeList by viewModel.latestAnimes.collectAsState()
    val selectedFilter by viewModel.selectedFilter.collectAsState()

    val categories = remember {
        listOf("All", "Action", "Adventure", "Fantasy", "Romance", "Sci-Fi", "Free", "Premium")
    }

    // Apply Filter Logic
    val filteredAnimes = remember(animeList, selectedFilter) {
        when (selectedFilter) {
            "All" -> animeList
            "Free" -> animeList.filter { !it.isPremium }
            "Premium" -> animeList.filter { it.isPremium }
            else -> animeList.filter { anime ->
                anime.genres.any { it.equals(selectedFilter, ignoreCase = true) }
            }
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(BackgroundPrimary)
            .statusBarsPadding()
            .padding(bottom = 80.dp) // bottom nav clearance
    ) {
        // Sticky Header / Search Bar HUD
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .background(BackgroundPrimary)
                .padding(horizontal = 20.dp, vertical = 10.dp)
        ) {
            Text(
                text = "EXPLORE CATALOGUE",
                color = TextPrimary,
                fontFamily = OrbitronFamily,
                fontWeight = FontWeight.Bold,
                fontSize = 20.sp,
                letterSpacing = 1.sp
            )

            Spacer(modifier = Modifier.height(12.dp))

            // Non-interactive search bar that clicks into the Search Tab
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp)
                    .clip(RoundedCornerShape(24.dp))
                    .background(BackgroundSurface)
                    .border(1.dp, BorderColor, RoundedCornerShape(24.dp))
                    .clickable { onTriggerSearchTab() }
                    .padding(horizontal = 16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Default.Search,
                    contentDescription = "Search catalog",
                    tint = TextMuted,
                    modifier = Modifier.size(20.dp)
                )
                Spacer(modifier = Modifier.width(10.dp))
                Text(
                    text = "SEARCH DISCOVER TITLES...",
                    color = TextMuted,
                    fontSize = 12.sp,
                    fontFamily = SpaceMonoFamily
                )
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Dynamic filter chips scrolling horizontally
            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(categories) { category ->
                    val isSelected = selectedFilter == category
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(18.dp))
                            .background(if (isSelected) AccentPurple else BackgroundSurface)
                            .border(
                                width = 1.dp,
                                color = if (isSelected) AccentGlow else BorderColor,
                                shape = RoundedCornerShape(18.dp)
                            )
                            .clickable { viewModel.setFilter(category) }
                            .padding(horizontal = 14.dp, vertical = 6.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = category.uppercase(),
                            color = if (isSelected) TextPrimary else TextMuted,
                            fontSize = 11.sp,
                            fontFamily = SpaceMonoFamily,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        }

        // Catalogue content grid (2 columns standard, spacing adjusted)
        if (filteredAnimes.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                contentAlignment = Alignment.Center
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "NO MATCHES FOUND",
                        color = TextMuted,
                        fontFamily = OrbitronFamily,
                        fontSize = 14.sp
                    )
                }
            }
        } else {
            LazyVerticalGrid(
                columns = GridCells.Fixed(2),
                contentPadding = PaddingValues(horizontal = 20.dp, vertical = 8.dp),
                horizontalArrangement = Arrangement.spacedBy(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp),
                modifier = Modifier.weight(1f)
            ) {
                items(filteredAnimes) { anime ->
                    AnimeCard(
                        anime = anime,
                        modifier = Modifier.fillMaxWidth(),
                        onClick = { onNavigateToDetail(anime.animeId) }
                    )
                }
            }
        }
    }
}
