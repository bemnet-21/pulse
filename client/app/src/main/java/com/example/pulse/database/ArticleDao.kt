package com.example.pulse.database

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow


@Dao
interface ArticleDao {
    @Query("SELECT * FROM bookmarked_articles")
    fun getAllBookmarks(): Flow<List<ArticleEntity>>

    @Query("SELECT EXISTS(SELECT * FROM bookmarked_articles WHERE id = :id)")
    fun isBookmarked(id: Int): Flow<Boolean>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertBookmark(article: ArticleEntity)

    @Delete
    suspend fun deleteBookmark(article: ArticleEntity)
}