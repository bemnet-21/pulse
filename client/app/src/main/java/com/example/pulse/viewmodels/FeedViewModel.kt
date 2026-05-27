package com.example.pulse.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.pulse.network.DevToArticle
import com.example.pulse.network.RetrofitInstance
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

sealed class FeedUiState {
    object Loading: FeedUiState()
    data class Success(val articles: List<DevToArticle>) : FeedUiState()
    data class Error(val message: String) : FeedUiState()
}

class FeedViewModel: ViewModel() {
    private val _uiState = MutableStateFlow<FeedUiState>(FeedUiState.Loading)
    val uiState: StateFlow<FeedUiState> = _uiState.asStateFlow()

    init {
        fetchArticles()
    }

    private fun fetchArticles() {
        viewModelScope.launch {
            _uiState.value = FeedUiState.Loading
            try {
                val articles = RetrofitInstance.api.getArticles()
                _uiState.value = FeedUiState.Success(articles)
            } catch (e: Exception) {
                _uiState.value = FeedUiState.Error("Failed to fetch articles: ${e.message}")
            }
        }
    }
}