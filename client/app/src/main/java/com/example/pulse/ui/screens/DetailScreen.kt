package com.example.pulse.ui.screens

import android.webkit.WebView
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DetailScreen (
    articleId: Int,
    onBackClick: () -> Unit,
    viewModel: ArticleViewModel = viewModel()
    ) {
    LaunchedEffect(articleId) {
        viewModel.fetchArticleById(articleId)
    }

    val uiState by viewModel.uiState.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("") },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back", tint = MaterialTheme.colorScheme.onPrimary)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.Transparent)
            )
        },
        containerColor = MaterialTheme.colorScheme.background
    ) { paddingValues ->
        Box(modifier = Modifier.fillMaxSize().padding(paddingValues)) {
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
                            .verticalScroll(rememberScrollState())
                    ) {
                        AsyncImage(
                            model = article.cover_image ?: "https://picsum.photos/600/300",
                            contentDescription = "Cover",
                            modifier = Modifier.fillMaxWidth().height(250.dp),
                            contentScale = ContentScale.Crop
                        )

                        Text(
                            text = article.title,
                            color = MaterialTheme.colorScheme.onPrimary,
                            fontSize = 24.sp,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(16.dp)
                        )

                        AndroidView(
                            factory = { context ->
                                WebView(context).apply {
                                    setBackgroundColor(android.graphics.Color.TRANSPARENT)
                                }
                            },
                            update = { webView ->
                                val styledHtml = """
                                    <html>
                                    <head>
                                        <style>
                                            body { color: #F8FAFC; 
                                                font-family: sans-serif;
                                                padding: 16px;
                                                line-height: 1.6;
                                                background-color: #0A0A0E;
                                            }
                                            a { color: #6366F1; text-decoration: none; }
                                            img { max-width: 100%;
                                                border-radius: 8px;
                                                margin-top: 16px;
                                                margin-bottom: 16px;
                                            }
                                            pre { background-color: #15151C;
                                                padding: 12px;
                                                border-radius: 8px;
                                                overflow-x: auto;
                                                color: #14B8A6;
                                                font-family: monospace;
                                                font-size: 14px;
                                                border: 1px solid #2A2A35;
                                            }
                                            code { font-family: monospace;
                                                background-color: #15151C;
                                                padding: 2px 4px;
                                                border-radius: 4px;
                                                color: #14B8A6;
                                            }
                                            h1, h2, h3 { color: #F8FAFC;
                                                margin-top: 24px; 
                                            }
                                        </style>
                                    <head>
                                    <body>${article.body_html}</body>
                                    </html>
                                    """.trimIndent()

                                webView.loadDataWithBaseURL(null, styledHtml, "text/html", "UTF-8", null)

                            },
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                }
            }
        }
    }
}