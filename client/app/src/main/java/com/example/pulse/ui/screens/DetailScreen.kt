package com.example.pulse.ui.screens

import android.app.Application
import android.webkit.WebView
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import com.example.pulse.viewmodels.ArticleViewModel
import com.example.pulse.viewmodels.DetailUiState
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.ViewModelProvider
import com.example.pulse.database.ArticleEntity
import com.yourname.devpulse.viewmodels.BookmarkViewModel
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.outlined.BookmarkBorder


@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun DetailScreen(
    articleId: Int,
    onBackClick: () -> Unit,
    viewModel: ArticleViewModel = viewModel(),
    bookmarkViewModel: BookmarkViewModel = viewModel(
        factory = ViewModelProvider.AndroidViewModelFactory.getInstance(LocalContext.current.applicationContext as Application)
    )
) {
    LaunchedEffect(articleId) {
        viewModel.fetchArticleById(articleId)
    }

    val uiState by viewModel.uiState.collectAsState()
    val scrollState = rememberScrollState()

    val isBookmarked by bookmarkViewModel.checkIsBookmarked(articleId).collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { },
                navigationIcon = {
                    IconButton(
                        onClick = onBackClick,
                        modifier = Modifier
                            .padding(8.dp)
                            // Surface background ensures button is visible on white images
                            .background(MaterialTheme.colorScheme.surface.copy(alpha = 0.7f), CircleShape)
                    ) {
                        Icon(
                            Icons.Default.ArrowBack,
                            contentDescription = "Back",
                            tint = MaterialTheme.colorScheme.onSurface
                        )
                    }
                },
                actions = {
                    if (uiState is DetailUiState.Success) {
                        val article = (uiState as DetailUiState.Success).article
                        IconButton(
                            onClick = { /* Share Action */ },
                            modifier = Modifier
                                .padding(end = 8.dp)
                                .background(MaterialTheme.colorScheme.surface.copy(alpha = 0.7f), CircleShape)
                        ) {
                            Icon(
                                Icons.Default.Share,
                                contentDescription = "Share",
                                tint = MaterialTheme.colorScheme.onSurface
                            )
                        }
                        IconButton(
                            onClick = {
                                val entity = ArticleEntity(
                                    id = article.id,
                                    title = article.title,
                                    description = "",
                                    coverImage = article.cover_image,
                                    bodyHtml = article.body_html,
                                    tags = "",
                                    reading_time_minutes = 0
                                )
                                bookmarkViewModel.toggleBookmark(entity, isBookmarked)
                            }
                        ) {
                            Icon(
                                imageVector = if(isBookmarked) Icons.Filled.Bookmark else Icons.Outlined.BookmarkBorder,
                                contentDescription = "Bookmark",
                                tint = if (isBookmarked) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onPrimary
                            )
                        }
                    }


                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.Transparent)
            )
        },
        containerColor = MaterialTheme.colorScheme.background
    ) { paddingValues ->

        Box(modifier = Modifier.fillMaxSize()) {
            when (uiState) {
                is DetailUiState.Loading -> {
                    CircularProgressIndicator(
                        modifier = Modifier.align(Alignment.Center),
                        color = MaterialTheme.colorScheme.primary
                    )
                }

                is DetailUiState.Error -> {
                    Text(
                        text = (uiState as DetailUiState.Error).message,
                        color = Color.Red,
                        modifier = Modifier.align(Alignment.Center)
                    )
                }

                is DetailUiState.Success -> {
                    val article = (uiState as DetailUiState.Success).article

                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .verticalScroll(scrollState)
                    ) {
                        // 1. Hero Image (Clean, no overlay)
                        AsyncImage(
                            model = article.cover_image ?: article.social_image ?: "https://picsum.photos/600/400",
                            contentDescription = "Cover",
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(350.dp),
                            contentScale = ContentScale.Crop
                        )

                        // 2. Info Section (Below Image)
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp)
                        ) {
                            // FlowRow automatically wraps tags and date to next line if space is tight
                            FlowRow(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(8.dp),
                                verticalArrangement = Arrangement.spacedBy(10.dp)
                            ) {
                                article.tags.forEach { tag ->
                                    Text(
                                        text = tag.uppercase(),
                                        color = MaterialTheme.colorScheme.primary,
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Bold,
                                        fontFamily = androidx.compose.ui.text.font.FontFamily.Monospace,
                                        modifier = Modifier
                                            .border(
                                                1.dp,
                                                MaterialTheme.colorScheme.primary.copy(alpha = 0.3f),
                                                RoundedCornerShape(4.dp)
                                            )
                                            .background(
                                                MaterialTheme.colorScheme.primary.copy(alpha = 0.1f),
                                                RoundedCornerShape(4.dp)
                                            )
                                            .padding(horizontal = 8.dp, vertical = 2.dp)
                                    )
                                }

                                // Separator & Date
                                Text(
                                    text = "• ${article.published_at.take(10)}",
                                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f),
                                    fontSize = 12.sp,
                                    modifier = Modifier.align(Alignment.CenterVertically)
                                )
                            }

                            Spacer(modifier = Modifier.height(16.dp))

                            // High Visibility Title
                            Text(
                                text = article.title,
                                color = MaterialTheme.colorScheme.onSurface,
                                fontSize = 28.sp,
                                lineHeight = 34.sp,
                                fontWeight = FontWeight.ExtraBold
                            )
                        }

                        // 3. Article Content (WebView)
                        AndroidView(
                            factory = { context ->
                                WebView(context).apply {
                                    setBackgroundColor(android.graphics.Color.TRANSPARENT)
                                    settings.javaScriptEnabled = true
                                }
                            },
                            update = { webView ->
                                val styledHtml = """
                                    <!DOCTYPE html>
                                    <html>
                                    <head>
                                        <meta name="viewport" content="width=device-width, initial-scale=1.0">
                                        <style>
                                            body { 
                                                color: #c7c4d7; 
                                                font-family: -apple-system, sans-serif;
                                                padding: 0 16px;
                                                line-height: 1.7;
                                                font-size: 17px;
                                                background-color: transparent;
                                                margin: 0;
                                            }
                                            a { color: #c0c1ff; text-decoration: none; font-weight: bold; }
                                            img { max-width: 100%; border-radius: 12px; margin: 24px 0; }
                                            pre { 
                                                background-color: #050508; 
                                                padding: 16px; 
                                                border-radius: 12px; 
                                                overflow-x: auto; 
                                                border: 1px solid #2A2A35;
                                            }
                                            code { 
                                                color: #4fdbc8; 
                                                font-family: monospace; 
                                                background: #15151C; 
                                                padding: 2px 5px; 
                                                border-radius: 4px;
                                            }
                                            blockquote {
                                                border-left: 4px solid #c0c1ff;
                                                margin: 24px 0;
                                                padding-left: 16px;
                                                font-style: italic;
                                                color: #c0c1ff;
                                            }
                                            h1, h2, h3 { color: #ffffff; margin-top: 32px; font-weight: 800; }
                                        </style>
                                    </head>
                                    <body>${article.body_html}</body>
                                    </html>
                                """.trimIndent()
                                webView.loadDataWithBaseURL(null, styledHtml, "text/html", "UTF-8", null)
                            },
                            modifier = Modifier.fillMaxWidth().wrapContentHeight()
                        )

                        // 4. Footer section
                        HorizontalDivider(
                            modifier = Modifier.padding(16.dp),
                            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.1f)
                        )

                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(start = 16.dp, end = 16.dp, bottom = 48.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                AsyncImage(
                                    model = article.user.profile_image,
                                    contentDescription = "Author",
                                    modifier = Modifier
                                        .size(44.dp)
                                        .clip(CircleShape),
                                    contentScale = ContentScale.Crop
                                )
                                Spacer(modifier = Modifier.width(12.dp))
                                Column {
                                    Text(
                                        text = article.user.name,
                                        color = MaterialTheme.colorScheme.onSurface,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 15.sp
                                    )
                                    Text(
                                        text = "${article.reading_time_minutes} min read",
                                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f),
                                        fontSize = 13.sp
                                    )
                                }
                            }

                            IconButton(
                                onClick = { /* Bookmark */ },
                                modifier = Modifier.border(1.dp, MaterialTheme.colorScheme.onSurface.copy(alpha = 0.2f), CircleShape)
                            ) {
                                Icon(Icons.Default.Favorite, contentDescription = "Favorite", tint = MaterialTheme.colorScheme.onSurface)
                            }
                        }

                        // Safety spacer for navigation bars
                        Spacer(modifier = Modifier.height(paddingValues.calculateBottomPadding()))
                    }
                }
            }

            // 5. Scroll Progress Bar (Sticky at top)
            if (uiState is DetailUiState.Success) {
                val progress = if (scrollState.maxValue > 0) {
                    scrollState.value.toFloat() / scrollState.maxValue.toFloat()
                } else 0f

                LinearProgressIndicator(
                    progress = { progress },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(3.dp)
                        .align(Alignment.TopCenter),
                    color = MaterialTheme.colorScheme.primary,
                    trackColor = Color.Transparent
                )
            }
        }
    }
}