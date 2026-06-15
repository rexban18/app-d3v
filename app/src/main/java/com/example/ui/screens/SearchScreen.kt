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
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.components.AnimeCard
import com.example.ui.theme.*
import com.example.viewmodel.MainViewModel

@Composable
fun SearchScreen(
    viewModel: MainViewModel,
    onNavigateToDetail: (animeId: String) -> Unit
) {
    val searchQuery by viewModel.searchQuery.collectAsState()
    val recentSearches by viewModel.recentSearches.collectAsState()
    val searchResults by viewModel.searchResults.collectAsState()

    val focusRequester = remember { FocusRequester() }

    // Auto focus search field on screen open
    LaunchedEffect(Unit) {
        focusRequester.requestFocus()
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(BackgroundPrimary)
            .statusBarsPadding()
            .padding(bottom = 80.dp) // bottom nav clearance
    ) {
        // Sticky Top Search Header
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp, vertical = 10.dp)
        ) {
            Text(
                text = "SEARCH DATABASE",
                color = TextPrimary,
                fontFamily = OrbitronFamily,
                fontWeight = FontWeight.Bold,
                fontSize = 20.sp,
                letterSpacing = 1.sp
            )

            Spacer(modifier = Modifier.height(12.dp))

            // Text Input Field
            TextField(
                value = searchQuery,
                onValueChange = { query ->
                    viewModel.setSearchQuery(query)
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp)
                    .focusRequester(focusRequester),
                placeholder = {
                    Text(
                        "ENTER ANIME TITLE OR GENRE...",
                        color = TextMuted,
                        fontSize = 12.sp,
                        fontFamily = SpaceMonoFamily
                    )
                },
                leadingIcon = {
                    Icon(
                        imageVector = Icons.Default.Search,
                        contentDescription = "Search input",
                        tint = AccentPurple,
                        modifier = Modifier.size(20.dp)
                    )
                },
                trailingIcon = {
                    if (searchQuery.isNotEmpty()) {
                        IconButton(onClick = { viewModel.clearQuery() }) {
                            Icon(
                                imageVector = Icons.Default.Close,
                                contentDescription = "Clear query",
                                tint = TextMuted,
                                modifier = Modifier.size(18.dp)
                            )
                        }
                    }
                },
                shape = RoundedCornerShape(26.dp),
                colors = TextFieldDefaults.colors(
                    focusedTextColor = TextPrimary,
                    unfocusedTextColor = TextPrimary,
                    disabledTextColor = Color.Transparent,
                    focusedContainerColor = BackgroundSurface,
                    unfocusedContainerColor = BackgroundSurface,
                    focusedIndicatorColor = Color.Transparent,
                    unfocusedIndicatorColor = Color.Transparent,
                    disabledIndicatorColor = Color.Transparent
                ),
                singleLine = true
            )

            // Recent searches chips (visible if query is blank)
            if (searchQuery.isBlank() && recentSearches.isNotEmpty()) {
                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    text = "RECENT SEARCHES",
                    color = TextMuted,
                    fontSize = 11.sp,
                    fontFamily = SpaceMonoFamily,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(8.dp))
                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(recentSearches) { tag ->
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier
                                .clip(RoundedCornerShape(16.dp))
                                .background(BackgroundSurface)
                                .border(1.dp, BorderColor, RoundedCornerShape(16.dp))
                                .clickable {
                                    viewModel.setSearchQuery(tag)
                                }
                                .padding(horizontal = 12.dp, vertical = 6.dp)
                        ) {
                            Text(
                                text = tag.uppercase(),
                                color = TextPrimary,
                                fontSize = 10.sp,
                                fontFamily = SpaceMonoFamily,
                                fontWeight = FontWeight.Bold
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Icon(
                                imageVector = Icons.Default.Close,
                                contentDescription = "Remove search reference",
                                tint = TextMuted,
                                modifier = Modifier
                                    .size(12.dp)
                                    .clickable {
                                        viewModel.removeRecentSearch(tag)
                                    }
                            )
                        }
                    }
                }
            }
        }

        // Live grid outputs
        if (searchQuery.isBlank()) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Icon(
                        imageVector = Icons.Default.Search,
                        contentDescription = null,
                        tint = BorderColor,
                        modifier = Modifier.size(64.dp)
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    Text(
                        text = "FIND YOUR NEXT SCI-FI ADVENTURE",
                        color = TextMuted,
                        fontSize = 11.sp,
                        fontFamily = SpaceMonoFamily
                    )
                }
            }
        } else if (searchResults.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = "NO ANIME FOUND",
                        color = AccentGlow,
                        fontFamily = OrbitronFamily,
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "TRY ADJUSTING KEYWORDS",
                        color = TextMuted,
                        fontSize = 11.sp,
                        fontFamily = SpaceMonoFamily
                    )
                }
            }
        } else {
            // Trigger saving search queries upon successful typing
            LaunchedEffect(searchResults) {
                if (searchResults.isNotEmpty()) {
                    viewModel.addRecentSearch(searchQuery)
                }
            }

            LazyVerticalGrid(
                columns = GridCells.Fixed(2),
                contentPadding = PaddingValues(horizontal = 20.dp, vertical = 8.dp),
                horizontalArrangement = Arrangement.spacedBy(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp),
                modifier = Modifier.weight(1f)
            ) {
                items(searchResults) { anime ->
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
