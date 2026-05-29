package com.example.pulse.viewmodels

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.pulse.database.ArticleEntity
import com.example.pulse.network.DevToArticleDetail
import com.example.pulse.network.RetrofitInstance
import com.yourname.devpulse.database.AppDatabase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
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
                    val article: DevToArticleDetail = RetrofitInstance.api.getArticleById(articleId)
                    _uiState.value = DetailUiState.SuccessRemote(article)
                } catch (e: Exception) {
                    _uiState.value = DetailUiState.Error("Failed to fetch article detail: ${e.message}")
                }
            }
        }
    }
}
