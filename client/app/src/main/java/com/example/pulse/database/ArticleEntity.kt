package com.example.pulse.database

import androidx.room.Entity
import androidx.room.PrimaryKey


@Entity(tableName = "bookmarked_articles")
data class ArticleEntity(
    @PrimaryKey val id: Int,
    val title: String,
    val description: String,
    val coverImage: String?,
    val bodyHtml: String?,
    val tags: List<String>,
    val reading_time_minutes: Int,
    val date: String

    )
