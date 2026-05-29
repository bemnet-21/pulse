package com.example.pulse.ui.screens

import android.app.Application
import android.webkit.WebView
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.outlined.BookmarkBorder
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import com.example.pulse.database.ArticleEntity
import com.example.pulse.utils.DateUtils
import com.example.pulse.viewmodels.ArticleViewModel
import com.example.pulse.viewmodels.DetailUiState
import com.yourname.devpulse.viewmodels.BookmarkViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DetailScreen(
    articleId: Int,
    onBackClick: () -> Unit,
    viewModel: ArticleViewModel = viewModel(
        factory = ViewModelProvider.AndroidViewModelFactory.getInstance(LocalContext.current.applicationContext as Application)
    ),
    bookmarkViewModel: BookmarkViewModel = viewModel(
        factory = ViewModelProvider.AndroidViewModelFactory.getInstance(LocalContext.current.applicationContext as Application)
    )
) {
    val uiState by viewModel.uiState.collectAsState()
    val isBookmarked by bookmarkViewModel.checkIsBookmarked(articleId).collectAsState()

    LaunchedEffect(articleId) {
        viewModel.fetchArticleById(articleId)
    }

    Scaffold(
        containerColor = MaterialTheme.colorScheme.background,
        topBar = {
            TopAppBar(
                title = { Text("") },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back", tint = Color.White)
                    }
                },
                actions = {
                    if (uiState is DetailUiState.SuccessLocal || uiState is DetailUiState.SuccessRemote) {
                        IconButton(onClick = {
                            val entityToSave = when (val state = uiState) {
                                is DetailUiState.SuccessLocal -> state.article
                                is DetailUiState.SuccessRemote -> {
                                    val r = state.article
                                    ArticleEntity(
                                        id = r.id,
                                        title = r.title,
                                        description = r.description ?: "",
                                        coverImage = r.cover_image,
                                        bodyHtml = r.body_html,
                                        // Normalize string "android,kotlin" to List
                                        tags = r.tags,
                                        reading_time_minutes = r.reading_time_minutes,
                                        date = r.published_at

                                    )
                                }
                                else -> null
                            }
                            entityToSave?.let { bookmarkViewModel.toggleBookmark(it, isBookmarked) }
                        }) {
                            Icon(
                                imageVector = if (isBookmarked) Icons.Filled.Bookmark else Icons.Outlined.BookmarkBorder,
                                contentDescription = "Bookmark",
                                tint = if (isBookmarked) MaterialTheme.colorScheme.primary else Color.Gray
                            )
                        }
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.Transparent)
            )
        }
    ) { paddingValues ->
        Box(modifier = Modifier.fillMaxSize().padding(bottom = paddingValues.calculateBottomPadding())) {
            when (val state = uiState) {
                is DetailUiState.Loading -> {
                    CircularProgressIndicator(modifier = Modifier.align(Alignment.Center), color = MaterialTheme.colorScheme.primary)
                }
                is DetailUiState.Error -> {
                    Text(text = state.message, color = Color.Red, modifier = Modifier.align(Alignment.Center))
                }
                is DetailUiState.SuccessLocal, is DetailUiState.SuccessRemote -> {

                    // 1. DATA NORMALIZATION (Now including Tags)
                    val title: String
                    val bodyHtml: String
                    val coverImage: String?
                    val tags: List<String>
                    val date: String

                    if (state is DetailUiState.SuccessLocal) {
                        title = state.article.title
                        bodyHtml = state.article.bodyHtml ?: ""
                        coverImage = state.article.coverImage
                        tags = state.article.tags
                        date = state.article.date
                    } else {
                        val remote = (state as DetailUiState.SuccessRemote).article
                        title = remote.title
                        bodyHtml = remote.body_html
                        coverImage = remote.cover_image
                        tags = remote.tags
                        date = remote.published_at
                    }

                    Column(modifier = Modifier.fillMaxSize().verticalScroll(rememberScrollState())) {
                        AsyncImage(
                            model = coverImage ?: "https://picsum.photos/600/300",
                            contentDescription = null,
                            modifier = Modifier.fillMaxWidth().height(260.dp),
                            contentScale = ContentScale.Crop
                        )

                        Spacer(modifier = Modifier.height(16.dp))

                        Text(
                            text = title,
                            color = MaterialTheme.colorScheme.onSurface,
                            fontSize = 24.sp,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(horizontal = 16.dp)
                        )

                        Spacer(modifier = Modifier.height(12.dp))

                        // 2. THE TAGS ROW
                        LazyRow(
                            modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            items(tags) { tag ->
                                Box(
                                    modifier = Modifier
                                        .border(1.dp, MaterialTheme.colorScheme.secondary.copy(alpha = 0.5f), RoundedCornerShape(4.dp))
                                        .padding(horizontal = 8.dp, vertical = 4.dp)
                                ) {
                                    Text(
                                        text = tag.uppercase(),
                                        color = MaterialTheme.colorScheme.secondary,
                                        fontSize = 12.sp,
                                        fontFamily = FontFamily.Monospace,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                            }
                        }

                        Text(
                            text = DateUtils.formatPublishedDate(date),
                            color = Color.Gray,
                            fontSize = 12.sp,
                            modifier = Modifier.padding(horizontal = 16.dp)
                        )

                        Spacer(modifier = Modifier.height(16.dp))

                        // 3. THE ARTICLE BODY
                        AndroidView(
                            factory = { context -> WebView(context).apply { setBackgroundColor(0x0A0A0E) } },
                            update = { webView ->
                                val styledHtml = """
                                    <html>
                                    <head>
                                        <style>
                                            body { color: #F8FAFC; font-family: sans-serif; padding: 16px; line-height: 1.7; background-color: #0A0A0E; }
                                            img { max-width: 100%; border-radius: 8px; margin: 16px 0; }
                                            pre { background-color: #15151C; padding: 12px; border-radius: 8px; overflow-x: auto; color: #14B8A6; border: 1px solid #2A2A35;}
                                        </style>
                                    </head>
                                    <body>$bodyHtml</body>
                                    </html>
                                """.trimIndent()
                                webView.loadDataWithBaseURL(null, styledHtml, "text/html", "UTF-8", null)
                            },
                            modifier = Modifier.fillMaxWidth().heightIn(min = 500.dp)
                        )
                        Spacer(modifier = Modifier.height(40.dp))
                    }
                }
            }
        }
    }
}
