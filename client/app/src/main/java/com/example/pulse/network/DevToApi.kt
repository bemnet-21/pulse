package com.example.pulse.network

import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path
import retrofit2.http.Query


data class ApiResponse<T>(
    val data: T,
    val message: String
)

data class DevToArticle(
    val id: Int,
    val title: String,
    val description: String,
    val tags: String,
    val reading_time_minutes: Int,
    val cover_image: String?
)

data class DevToUser(
    val name: String,
    val profile_image: String
)

data class DevToArticleDetail(
    val id: Int,
    val title: String,
    val body_html: String,
    val cover_image: String?,
    val social_image: String?,
    val reading_time_minutes: Int,
    val published_at: String,
    val user: DevToUser,
    val tags: String,
    val description: String,
    val ai_summary: String? = null
)

/** Returned by POST articles/{id}/summarize */
data class SummarizeResponse(
    val ai_summary: String
)

interface DevToApi {
    @GET("articles")
    suspend fun getArticles(
        @Query("per_page") perPage: Int = 15,
        @Query("top") top: Int = 1
    ): ApiResponse<List<DevToArticle>>

    @GET("articles/{id}")
    suspend fun getArticleById(
        @Path("id") articleId: Int
    ): ApiResponse<DevToArticleDetail>

    /** Triggers lazy AI summary generation (or returns cached result). */
    @POST("articles/{id}/summarize")
    suspend fun summarizeArticle(
        @Path("id") articleId: Int
    ): ApiResponse<SummarizeResponse>
}