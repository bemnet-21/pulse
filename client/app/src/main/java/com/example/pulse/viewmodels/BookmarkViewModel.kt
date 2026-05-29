package com.yourname.devpulse.viewmodels

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.pulse.database.ArticleEntity
import com.yourname.devpulse.database.AppDatabase
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class BookmarkViewModel(application: Application) : AndroidViewModel(application) {

    private val dao = AppDatabase.getDatabase(application).articleDao()

    val bookmarkedArticles: StateFlow<List<ArticleEntity>> = dao.getAllBookmarks()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    fun checkIsBookmarked(id: Int): StateFlow<Boolean> {
        return dao.isBookmarked(id).stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), false)
    }

    fun toggleBookmark(article: ArticleEntity, isCurrentlyBookmarked: Boolean) {
        viewModelScope.launch {
            if (isCurrentlyBookmarked) {
                dao.deleteBookmark(article)
            } else {
                dao.insertBookmark(article)
            }
        }
    }
}