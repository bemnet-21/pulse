package com.example.pulse.network

import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

data class DevToArticle(
    val id: Int,
    val title: String,
    val description: String,
    val tags: String,
    val reading_time_minutes: Int,
    val cover_image: String?
)

data class DevToArticleDetail(
    val id: Int,
    val title: String,
    val body_html: String,
    val cover_image: String?,
    val published_at: String
)
interface DevToApi {
    @GET("articles")
    suspend fun getArticles(
        @Query("per_page") perPage: Int = 15,
        @Query("top") top: Int = 1
    ): List<DevToArticle>

    @GET("articles/{id}")
    suspend fun getArticleById(
        @Path("id") articleId: Int
    ): DevToArticleDetail
}