package com.example.pulse.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.pulse.network.DevToArticleDetail
import com.example.pulse.network.RetrofitInstance
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

sealed class DetailUiState {
    object Loading : DetailUiState()
    data class Success(val article: DevToArticleDetail) : DetailUiState()
    data class Error(val message: String) : DetailUiState()
}

class ArticleViewModel: ViewModel() {
    private val _uiState = MutableStateFlow<DetailUiState>(DetailUiState.Loading)
    val uiState: StateFlow<DetailUiState> = _uiState.asStateFlow()

    fun fetchArticleById(articleId: Int) {
        viewModelScope.launch {
            _uiState.value = DetailUiState.Loading
            try {
                val article: DevToArticleDetail = RetrofitInstance.api.getArticleById(articleId)
                _uiState.value = DetailUiState.Success(article)

            } catch(e: Exception) {
                _uiState.value = DetailUiState.Error("Failed to fetch article detail ${e.message}")
            }
        }
    }
}