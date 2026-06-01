package com.example.pulse.ui.screens

import android.app.Application
import android.webkit.WebView
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
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
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.outlined.BookmarkBorder
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import com.example.pulse.database.ArticleEntity
import com.example.pulse.ui.theme.ElectricIndigo
import com.example.pulse.ui.theme.Inter
import com.example.pulse.ui.theme.JetBrain
import com.example.pulse.ui.theme.MatteCharcoal
import com.example.pulse.ui.theme.NeonTeal
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
    val isSummarizing by viewModel.isSummarizing.collectAsState()
    val aiSummary by viewModel.aiSummary.collectAsState()

    // Track whether the summary box should animate in
    var summaryVisible by remember { mutableStateOf(false) }
    LaunchedEffect(aiSummary) {
        if (aiSummary != null) summaryVisible = true
    }

    LaunchedEffect(articleId) {
        viewModel.fetchArticleById(articleId)
    }

    val scrollState = rememberScrollState()
    val appBarColor by animateColorAsState(
        targetValue = if (scrollState.value > 100) Color(0xFF0A0A0E) else Color.Transparent,
        label = "appBarColor"
    )

    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(Unit) {
        viewModel.aiError.collect { errorMsg ->
            snackbarHostState.showSnackbar(errorMsg)
        }
    }

    Scaffold(
        containerColor = MaterialTheme.colorScheme.background,
        snackbarHost = { SnackbarHost(snackbarHostState) },
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
                                        tags = r.tags.split(","),
                                        reading_time_minutes = r.reading_time_minutes,
                                        date = r.published_at ?: "2026-05-31"
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
                colors = TopAppBarDefaults.topAppBarColors(containerColor = appBarColor)
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

                    // 1. DATA NORMALIZATION
                    val title: String
                    val bodyHtml: String
                    val coverImage: String?
                    val tags: List<String>
                    val date: String
                    val readingTime: Int
                    // Pre-populate summary from the article if the backend already has one cached
                    val cachedSummary: String?

                    if (state is DetailUiState.SuccessLocal) {
                        title = state.article.title
                        bodyHtml = state.article.bodyHtml ?: ""
                        coverImage = state.article.coverImage
                        tags = state.article.tags
                        date = state.article.date
                        readingTime = state.article.reading_time_minutes
                        cachedSummary = null
                    } else {
                        val remote = (state as DetailUiState.SuccessRemote).article
                        title = remote.title
                        bodyHtml = remote.body_html
                        coverImage = remote.cover_image
                        tags = remote.tags.split(",")
                        date = remote.published_at ?: "2026-05-31"
                        readingTime = remote.reading_time_minutes
                        cachedSummary = remote.ai_summary
                    }

                    // Seed the aiSummary flow once with any server-cached value
                    LaunchedEffect(cachedSummary) {
                        if (cachedSummary != null && aiSummary == null) {
                            viewModel.seedAiSummary(cachedSummary)
                        }
                    }

                    Column(modifier = Modifier.fillMaxSize().verticalScroll(scrollState)) {
                        Box(modifier = Modifier.fillMaxWidth().height(260.dp)) {
                            AsyncImage(
                                model = coverImage ?: "https://picsum.photos/600/300",
                                contentDescription = null,
                                modifier = Modifier.fillMaxSize(),
                                contentScale = ContentScale.Crop
                            )
                            // Bottom Scrim/Gradient
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(100.dp)
                                    .align(Alignment.BottomCenter)
                                    .background(
                                        Brush.verticalGradient(
                                            colors = listOf(Color.Transparent, Color(0xFF0A0A0E))
                                        )
                                    )
                            )
                        }

                        Spacer(modifier = Modifier.height(16.dp))

                        Text(
                            text = title,
                            color = MaterialTheme.colorScheme.onSurface,
                            fontSize = 24.sp,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(horizontal = 16.dp)
                        )

                        Spacer(modifier = Modifier.height(12.dp))

                        // METADATA CHIPS
                        Row(
                            modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            SuggestionChip(
                                onClick = {},
                                label = { Text("${readingTime} min read", color = Color(0xFF94A3B8), fontSize = 12.sp) },
                                colors = SuggestionChipDefaults.suggestionChipColors(containerColor = MatteCharcoal),
                                border = null,
                                shape = RoundedCornerShape(16.dp)
                            )
                            SuggestionChip(
                                onClick = {},
                                label = { Text(DateUtils.formatPublishedDate(date), color = Color(0xFF94A3B8), fontSize = 12.sp) },
                                colors = SuggestionChipDefaults.suggestionChipColors(containerColor = MatteCharcoal),
                                border = null,
                                shape = RoundedCornerShape(16.dp)
                            )
                        }

                        Spacer(modifier = Modifier.height(16.dp))

                        // 2. TAGS ROW
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
                                        fontFamily = JetBrain,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(16.dp))

                        // ── 3. AI KEY INSIGHTS SECTION ───────────────────────────────
                        AiInsightsSection(
                            aiSummary = aiSummary,
                            isSummarizing = isSummarizing,
                            summaryVisible = summaryVisible,
                            onGenerateClick = { viewModel.fetchAiSummary(articleId) }
                        )

                        Spacer(modifier = Modifier.height(16.dp))

                        // 4. ARTICLE BODY
                        AndroidView(
                            factory = { context -> WebView(context).apply { setBackgroundColor(0x0A0A0E) } },
                            update = { webView ->
                                val styledHtml = """
                                    <html>
                                    <head>
                                        <meta name="viewport" content="width=device-width, initial-scale=1.0">
                                        <style>
                                            body { color: #F8FAFC; font-family: sans-serif; padding: 16px; line-height: 1.8; background-color: #0A0A0E; }
                                            p, h1, h2, ul { margin-bottom: 1.5em; }
                                            img { max-width: 100%; border-radius: 12px; border: 1px solid #2A2A35; box-shadow: 0 4px 6px rgba(0,0,0,0.5); margin: 2em auto; display: block; }
                                            a { color: #14B8A6; text-decoration: underline; text-underline-offset: 4px; font-weight: 600; }
                                            pre, code { background-color: #050508; font-family: 'JetBrains Mono', monospace; }
                                            pre { padding: 12px; border-radius: 8px; overflow-x: auto; color: #14B8A6; border: 1px solid #2A2A35; border-left: 3px solid #14B8A6; }
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

// ─────────────────────────────────────────────────────────────────────────────
// AI Insights composable — handles all 3 UI states
// ─────────────────────────────────────────────────────────────────────────────
@Composable
private fun AiInsightsSection(
    aiSummary: String?,
    isSummarizing: Boolean,
    summaryVisible: Boolean,
    onGenerateClick: () -> Unit
) {
    val cornerShape = RoundedCornerShape(12.dp)

    when {
        // ── State 2: Loading ─────────────────────────────────────────────────
        isSummarizing -> {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp)
                    .clip(cornerShape)
                    .background(MatteCharcoal)
                    .border(1.dp, ElectricIndigo.copy(alpha = 0.4f), cornerShape)
                    .padding(16.dp)
            ) {
                Text(
                    text = "GENERATING INSIGHTS...",
                    fontFamily = JetBrain,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    color = NeonTeal,
                    letterSpacing = 1.5.sp
                )
                Spacer(modifier = Modifier.height(12.dp))
                LinearProgressIndicator(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(2.dp)
                        .clip(RoundedCornerShape(1.dp)),
                    color = ElectricIndigo,
                    trackColor = ElectricIndigo.copy(alpha = 0.15f)
                )
            }
        }

        // ── State 3: Summary available ───────────────────────────────────────
        aiSummary != null -> {
            AnimatedVisibility(
                visible = summaryVisible,
                enter = fadeIn(animationSpec = tween(durationMillis = 600))
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp)
                        .clip(cornerShape)
                        .background(ElectricIndigo.copy(alpha = 0.10f))
                        .border(1.dp, ElectricIndigo.copy(alpha = 0.30f), cornerShape)
                        .padding(16.dp)
                ) {
                    // Header
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Filled.AutoAwesome,
                            contentDescription = null,
                            tint = ElectricIndigo,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "AI KEY INSIGHTS",
                            fontFamily = JetBrain,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = ElectricIndigo,
                            letterSpacing = 1.5.sp
                        )
                    }

                    Spacer(modifier = Modifier.height(12.dp))
                    HorizontalDivider(color = ElectricIndigo.copy(alpha = 0.20f), thickness = 1.dp)
                    Spacer(modifier = Modifier.height(12.dp))

                    // Body — split on newlines so each bullet is its own Text
                    aiSummary.lines()
                        .filter { it.isNotBlank() }
                        .forEach { line ->
                            Text(
                                text = line.trim(),
                                fontFamily = Inter,
                                fontSize = 14.sp,
                                lineHeight = 22.sp,
                                color = Color(0xFFF8FAFC),
                                modifier = Modifier.padding(bottom = 6.dp)
                            )
                        }
                }
            }
        }

        // ── State 1: No summary yet — show Generate button ───────────────────
        else -> {
            Button(
                onClick = onGenerateClick,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp)
                    .height(52.dp)
                    .border(1.dp, ElectricIndigo, cornerShape),
                shape = cornerShape,
                colors = ButtonDefaults.buttonColors(
                    containerColor = MatteCharcoal,
                    contentColor = NeonTeal
                ),
                elevation = ButtonDefaults.buttonElevation(defaultElevation = 0.dp)
            ) {
                Icon(
                    imageVector = Icons.Filled.AutoAwesome,
                    contentDescription = null,
                    tint = NeonTeal,
                    modifier = Modifier.size(18.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "✨ Generate AI Key Insights",
                    fontFamily = JetBrain,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Medium,
                    color = NeonTeal
                )
            }
        }
    }
}
