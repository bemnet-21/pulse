package com.example.pulse.ui.screens

import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.outlined.BookmarkBorder
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import com.example.pulse.models.dummyCategories
import com.example.pulse.network.DevToArticle
import com.example.pulse.viewmodels.FeedUiState
import com.example.pulse.viewmodels.FeedViewModel
import com.yourname.devpulse.viewmodels.BookmarkViewModel

enum class NavRoute {
    Home, Bookmarks, Profile
}

@Composable
fun PulseBottomBar(
    currentRoute: NavRoute,
    onNavigate: (NavRoute) -> Unit
) {
    NavigationBar(
        containerColor = MaterialTheme.colorScheme.background,
        contentColor = Color.Gray,
        tonalElevation = 8.dp
    ) {
        NavigationBarItem(
            selected = currentRoute == NavRoute.Home,
            onClick = { onNavigate(NavRoute.Home) },
            icon = {
                Icon(
                    imageVector = if (currentRoute == NavRoute.Home) Icons.Filled.Home else Icons.Outlined.Home,
                    contentDescription = "Home"
                )
            },
            label = { Text("Home") },
            colors = NavigationBarItemDefaults.colors(
                selectedIconColor = MaterialTheme.colorScheme.primary,
                selectedTextColor = MaterialTheme.colorScheme.primary,
                indicatorColor = MaterialTheme.colorScheme.surface
            )
        )

        NavigationBarItem(
            selected = currentRoute == NavRoute.Bookmarks,
            onClick = { onNavigate(NavRoute.Bookmarks) },
            icon = {
                Icon(
                    imageVector = if (currentRoute == NavRoute.Bookmarks) Icons.Filled.Bookmark else Icons.Outlined.BookmarkBorder,
                    contentDescription = "Bookmarks"
                )
            },
            label = { Text("Offline") },
            colors = NavigationBarItemDefaults.colors(
                selectedIconColor = MaterialTheme.colorScheme.primary,
                selectedTextColor = MaterialTheme.colorScheme.primary,
                indicatorColor = MaterialTheme.colorScheme.surface
            )
        )

        NavigationBarItem(
            selected = currentRoute == NavRoute.Profile,
            onClick = { onNavigate(NavRoute.Profile) },
            icon = {
                Icon(
                    imageVector = if (currentRoute == NavRoute.Profile) Icons.Filled.Person else Icons.Outlined.Person,
                    contentDescription = "Profile"
                )
            },
            label = { Text("Profile") },
            colors = NavigationBarItemDefaults.colors(
                selectedIconColor = MaterialTheme.colorScheme.primary,
                selectedTextColor = MaterialTheme.colorScheme.primary,
                indicatorColor = MaterialTheme.colorScheme.surface
            )
        )
    }
}

// 3. Article Card Component
@Composable
fun ArticleCard(article: DevToArticle, isBookmarked: Boolean) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp)
            .border(1.dp, Color.White.copy(alpha = 0.06f), RoundedCornerShape(12.dp)),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        shape = RoundedCornerShape(12.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column(modifier = Modifier.weight(1f).padding(end = 16.dp)) {
                Text(
                    text = article.tags.split(",").firstOrNull()?.uppercase() ?: "DEV",
                    color = MaterialTheme.colorScheme.secondary,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    fontFamily = FontFamily.Monospace
                )

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = article.title,
                    color = MaterialTheme.colorScheme.onSurface,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = article.description,
                    color = Color.Gray,
                    fontSize = 14.sp,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis,
                    lineHeight = 20.sp
                )

                Spacer(modifier = Modifier.height(12.dp))

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = "${article.reading_time_minutes} min read",
                        color = Color.Gray,
                        fontSize = 12.sp
                    )
                    Spacer(modifier = Modifier.weight(1f))

                    Icon(
                        imageVector = if(isBookmarked) Icons.Filled.Bookmark else Icons.Outlined.BookmarkBorder,
                        contentDescription = "Save",
                        // Make the icon primary color if bookmarked!
                        tint = if(isBookmarked) MaterialTheme.colorScheme.primary else Color.Gray,
                        modifier = Modifier.size(20.dp)
                    )
                }
            }

            AsyncImage(
                model = article.cover_image ?: "https://picsum.photos/200",
                contentDescription = "Cover Image",
                modifier = Modifier
                    .size(80.dp)
                    .clip(RoundedCornerShape(8.dp)),
                contentScale = ContentScale.Crop
            )
        }
    }
}

// 4. Main Feed Screen Component
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FeedScreen(
    onArticleClick: (Int) -> Unit,
    onNavigateBottomBar: (NavRoute) -> Unit,
    viewModel: FeedViewModel = viewModel(),
    bookmarkViewModel: BookmarkViewModel = viewModel()
) {
    val uiState by viewModel.uiState.collectAsState()

    Scaffold(
        containerColor = MaterialTheme.colorScheme.background,
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "<DevPulse />",
                        fontFamily = FontFamily.Monospace,
                        color = MaterialTheme.colorScheme.onPrimary,
                        fontWeight = FontWeight.Bold
                    )
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.background
                )
            )
        },
        // Plug in the bottom bar here
        bottomBar = {
            PulseBottomBar(
                currentRoute = NavRoute.Home,
                onNavigate = { route -> onNavigateBottomBar(route) }
            )
        }
    ) { paddingValues ->
        // Use paddingValues here so the list doesn't get hidden behind the top/bottom bars
        Box(modifier = Modifier.fillMaxSize().padding(paddingValues)) {
            when (uiState) {
                is FeedUiState.Loading -> {
                    CircularProgressIndicator(
                        modifier = Modifier.align(Alignment.Center),
                        color = MaterialTheme.colorScheme.primary
                    )
                }
                is FeedUiState.Error -> {
                    Text(
                        text = (uiState as FeedUiState.Error).message,
                        color = Color.Red,
                        modifier = Modifier.align(Alignment.Center).padding(16.dp)
                    )
                }
                is FeedUiState.Success -> {
                    val articles = (uiState as FeedUiState.Success).articles
                    LazyColumn(
                        modifier = Modifier.fillMaxSize()
                    ) {
                        // Horizontal Discovery Chips
                        item {
                            LazyRow(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(horizontal = 16.dp, vertical = 8.dp),
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                items(dummyCategories) { category ->
                                    FilterChip(
                                        selected = category == "All",
                                        onClick = { /* TODO */ },
                                        label = { Text(category, color = MaterialTheme.colorScheme.onPrimary) },
                                        colors = FilterChipDefaults.filterChipColors(
                                            selectedContainerColor = MaterialTheme.colorScheme.primary,
                                            containerColor = MaterialTheme.colorScheme.surface
                                        ),
                                        border = FilterChipDefaults.filterChipBorder(
                                            borderColor = Color.White.copy(alpha = 0.06f),
                                            enabled = true,
                                            selected = false
                                        )
                                    )
                                }
                            }
                            Spacer(modifier = Modifier.height(8.dp))
                        }

                        // Article Feed
                        items(articles) { article ->
                            Box(
                                modifier = Modifier.clickable {
                                    onArticleClick(article.id)
                                }
                            ) {
                                val isBookmarked by bookmarkViewModel.checkIsBookmarked(article.id).collectAsState()
                                ArticleCard(article, isBookmarked)
                            }
                        }

                        // Space at bottom for scrolling
                        item { Spacer(modifier = Modifier.height(24.dp)) }
                    }
                }
            }
        }
    }
}