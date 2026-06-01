package com.example.pulse.viewmodels

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.pulse.database.ArticleEntity
import com.example.pulse.network.DevToArticleDetail
import com.example.pulse.network.RetrofitInstance
import com.yourname.devpulse.database.AppDatabase
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

sealed class DetailUiState {
    object Loading : DetailUiState()
    data class SuccessRemote(val article: DevToArticleDetail) : DetailUiState()
    data class SuccessLocal(val article: ArticleEntity) : DetailUiState()
    data class Error(val message: String) : DetailUiState()
}

class ArticleViewModel(application: Application): AndroidViewModel(application) {

    // ── Article detail state ──────────────────────────────────────────────────
    private val _uiState = MutableStateFlow<DetailUiState>(DetailUiState.Loading)
    val uiState: StateFlow<DetailUiState> = _uiState.asStateFlow()

    private val dao = AppDatabase.getDatabase(application).articleDao()

    fun fetchArticleById(articleId: Int) {
        viewModelScope.launch {
            _uiState.value = DetailUiState.Loading

            // Check local database first
            val localArticle = dao.getAllBookmarks().first().find { it.id == articleId }
            if (localArticle != null && localArticle.bodyHtml != null) {
                _uiState.value = DetailUiState.SuccessLocal(localArticle)
            } else {
                try {
                    val article: DevToArticleDetail = RetrofitInstance.api.getArticleById(articleId).data
                    _uiState.value = DetailUiState.SuccessRemote(article)
                } catch (e: Exception) {
                    _uiState.value = DetailUiState.Error("Failed to fetch article detail: ${e.message}")
                }
            }
        }
    }

    // ── On-demand AI Insights ─────────────────────────────────────────────────

    /** True while a Gemini request is in-flight. */
    private val _isSummarizing = MutableStateFlow(false)
    val isSummarizing: StateFlow<Boolean> = _isSummarizing.asStateFlow()

    /** The generated (or cached) bullet-point summary. Null until first request. */
    private val _aiSummary = MutableStateFlow<String?>(null)
    val aiSummary: StateFlow<String?> = _aiSummary.asStateFlow()

    private val _aiError = MutableSharedFlow<String>()
    val aiError: SharedFlow<String> = _aiError.asSharedFlow()

    /**
     * Calls POST /articles/{id}/summarize.
     * The backend returns a cached result if one already exists,
     * otherwise it calls Gemini, persists it, and returns the new summary.
     */
    fun fetchAiSummary(id: Int) {
        viewModelScope.launch {
            _isSummarizing.value = true
            try {
                val response = RetrofitInstance.api.summarizeArticle(id)
                _aiSummary.value = response.data.ai_summary
            } catch (e: Exception) {
                if (com.example.pulse.BuildConfig.DEBUG) {
                    Log.e("ArticleViewModel", "AI Summarization Error: ${e.message}", e)
                }
                _aiError.emit("AI Insights currently unavailable. Please try again later.")
            } finally {
                _isSummarizing.value = false
            }
        }
    }

    /**
     * Pre-seeds the aiSummary state from a cached value already embedded in the
     * article response (the `ai_summary` field on DevToArticleDetail).
     * Called once from the UI after the detail loads — a no-op if a summary is
     * already present so it never overwrites a freshly generated result.
     */
    fun seedAiSummary(cached: String) {
        if (_aiSummary.value == null) {
            _aiSummary.value = cached
        }
    }
}
