package com.example.pulse.models

data class Article(
    val id: Int,
    val title: String,
    val excerpt: String,
    val category: String,
    val readTime: String,
    val isSaved: Boolean = false
)

val dummyArticles = listOf(
    Article(
        id = 1,
        title = "Why we migrated from Node.js to Go",
        excerpt = "A deep dive into our microservices rewrite, memory safety, and how we cut cloud costs by 40%",
        category = "Backend",
        readTime = "6 min read"
    ),
    Article(
        id = 2,
        title = "Mastering Jetpack Compose Modifiers",
        excerpt = "Understanding the order of operations in Modifiers can save you hours of UI debugging.",
        category = "Android",
        readTime = "4 min read"
    ),
    Article(
        id = 3,
        title = "The System Design behind Postgres",
        excerpt = "How the world's most advanced open-source relational database handles concurrency.",
        category = "Database",
        readTime = "12 min read"
    )

)

val dummyCategories = listOf("All", "Backend", "Android", "Database", "AI/ML", "System Design")