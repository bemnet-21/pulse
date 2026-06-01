package com.example.pulse.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.filled.CloudOff
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.outlined.BookmarkBorder
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.filled.SearchOff
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.material3.pulltorefresh.PullToRefreshDefaults
import androidx.compose.material3.pulltorefresh.rememberPullToRefreshState
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
import com.example.pulse.network.DevToArticle
import com.example.pulse.viewmodels.FeedUiState
import com.example.pulse.viewmodels.FeedViewModel
import com.yourname.devpulse.ui.components.ShimmerArticleItem
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

//        NavigationBarItem(
//            selected = currentRoute == NavRoute.Profile,
//            onClick = { onNavigate(NavRoute.Profile) },
//            icon = {
//                Icon(
//                    imageVector = if (currentRoute == NavRoute.Profile) Icons.Filled.Person else Icons.Outlined.Person,
//                    contentDescription = "Profile"
//                )
//            },
//            label = { Text("Profile") },
//            colors = NavigationBarItemDefaults.colors(
//                selectedIconColor = MaterialTheme.colorScheme.primary,
//                selectedTextColor = MaterialTheme.colorScheme.primary,
//                indicatorColor = MaterialTheme.colorScheme.surface
//            )
//        )
    }
}

@Composable
fun ArticleCard(article: DevToArticle, isBookmarked: Boolean) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp)
            .border(1.dp, Color.White.copy(alpha = 0.06f), RoundedCornerShape(12.dp)),
        color = MaterialTheme.colorScheme.surface,
        shape = RoundedCornerShape(12.dp),
        border = BorderStroke(1.dp, Color(0xFF2A2A35))
    ) {
        Row(
            modifier = Modifier
                .padding(16.dp),
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = article.tags.split(",").firstOrNull()?.uppercase() ?: "DEV",
                    style = MaterialTheme.typography.bodyMedium,
                )

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = article.title,
                    style = MaterialTheme.typography.titleLarge,
                    maxLines = 2,
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
                        tint = if(isBookmarked) MaterialTheme.colorScheme.primary else Color.Gray,
                        modifier = Modifier.size(20.dp)
                    )
                }
            }

            AsyncImage(
                model = article.cover_image,
                contentDescription = null,
                modifier = Modifier
                    .size(88.dp)
                    .clip(RoundedCornerShape(10.dp)),
                contentScale = ContentScale.Crop
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FeedScreen(
    onArticleClick: (Int) -> Unit,
    onNavigateBottomBar: (NavRoute) -> Unit,
    viewModel: FeedViewModel = viewModel(),
    bookmarkViewModel: BookmarkViewModel = viewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val isRefreshing by viewModel.isRefreshing.collectAsState()
    val pullRefreshState = rememberPullToRefreshState()

    Scaffold(
        containerColor = MaterialTheme.colorScheme.background,
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "<Pulse />",
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
        bottomBar = {
            PulseBottomBar(
                currentRoute = NavRoute.Home,
                onNavigate = { route -> onNavigateBottomBar(route) }
            )
        }
    ) { paddingValues ->
        
        PullToRefreshBox(
            isRefreshing = isRefreshing,
            onRefresh = { viewModel.fetchArticles() },
            state = pullRefreshState,
            indicator = {
                PullToRefreshDefaults.Indicator(
                    state = pullRefreshState,
                    isRefreshing = isRefreshing,
                    containerColor = MaterialTheme.colorScheme.surface,
                    color = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.align(Alignment.TopCenter)
                )
            },
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues),
            contentAlignment = Alignment.TopCenter
        ) {
            Box(modifier = Modifier.fillMaxSize()) {
                when (uiState) {
                    is FeedUiState.Loading -> {
                        LazyColumn(modifier = Modifier.fillMaxSize()) {
                            items(6) {
                                ShimmerArticleItem()
                            }
                        }
                    }
                    is FeedUiState.Error -> {
                        val cachedArticles by bookmarkViewModel.bookmarkedArticles.collectAsState()
                        if (cachedArticles.isEmpty()) {
                            Column(
                                modifier = Modifier.fillMaxSize(),
                                verticalArrangement = androidx.compose.foundation.layout.Arrangement.Center,
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Icon(
                                    imageVector = androidx.compose.material.icons.Icons.Default.CloudOff,
                                    contentDescription = "Offline",
                                    tint = Color(0xFF94A3B8), // SlateGray
                                    modifier = Modifier.size(64.dp)
                                )
                                Spacer(modifier = Modifier.height(16.dp))
                                Text(
                                    text = "Offline Mode",
                                    color = Color(0xFFF8FAFC), // CloudWhite
                                    fontSize = 18.sp,
                                    fontWeight = FontWeight.Bold
                                )
                                Spacer(modifier = Modifier.height(8.dp))
                                Text(
                                    text = "No bookmarks available to read offline.",
                                    color = Color(0xFF94A3B8),
                                    fontSize = 14.sp
                                )
                            }
                        } else {
                            // Show Offline Cache
                            val articles = cachedArticles.map { 
                                DevToArticle(
                                    id = it.id,
                                    title = it.title,
                                    description = it.description,
                                    tags = it.tags.joinToString(","),
                                    reading_time_minutes = it.reading_time_minutes,
                                    cover_image = it.coverImage
                                )
                            }
                            
                            Box(modifier = Modifier.fillMaxSize()) {
                                LazyColumn(modifier = Modifier.fillMaxSize()) {
                                    items(articles) { article ->
                                        Box(modifier = Modifier.clickable { onArticleClick(article.id) }) {
                                            ArticleCard(article, isBookmarked = true)
                                        }
                                    }
                                    item { Spacer(modifier = Modifier.height(80.dp)) }
                                }
                                
                                // Offline Chip Overlay
                                Surface(
                                    modifier = Modifier
                                        .align(Alignment.BottomCenter)
                                        .padding(bottom = 16.dp),
                                    shape = RoundedCornerShape(16.dp),
                                    color = Color(0xFF15151C), // MatteCharcoal
                                    border = BorderStroke(1.dp, Color(0xFF2A2A35))
                                ) {
                                    Text(
                                        text = "Offline Mode: Reading from Cache",
                                        color = Color(0xFF94A3B8), // SlateGray
                                        fontSize = 12.sp,
                                        modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
                                    )
                                }
                            }
                        }
                    }
                    is FeedUiState.Success -> {
                        val articlesResponse = (uiState as FeedUiState.Success).articles
                        val filteredArticles = articlesResponse.data
                        
                        if (filteredArticles.isEmpty()) {
                            Column(
                                modifier = Modifier.fillMaxSize(),
                                verticalArrangement = androidx.compose.foundation.layout.Arrangement.Center,
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Icon(
                                    imageVector = Icons.Default.SearchOff,
                                    contentDescription = "No articles",
                                    tint = Color(0xFF94A3B8), // SlateGray
                                    modifier = Modifier.size(64.dp)
                                )
                                Spacer(modifier = Modifier.height(16.dp))
                                Text(
                                    text = "No articles found. Try a different keyword.",
                                    color = Color(0xFF94A3B8),
                                    fontSize = 16.sp
                                )
                            }
                        } else {
                            LazyColumn(
                                modifier = Modifier.fillMaxSize()
                            ) {
                                items(filteredArticles) { article ->
                                    Box(
                                        modifier = Modifier.clickable {
                                            onArticleClick(article.id)
                                        }
                                    ) {
                                        val isBookmarked by bookmarkViewModel.checkIsBookmarked(article.id).collectAsState()
                                        ArticleCard(article, isBookmarked)
                                    }
                                }

                                item { Spacer(modifier = Modifier.height(24.dp)) }
                            }
                        }
                    }
                }
            }
        }
    }
}
