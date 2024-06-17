package com.loc.newsapp.data.remote.dto

import com.loc.newsapp.domain.model.Article
import kotlinx.serialization.Serializable

@Serializable
data class NewsResponse(
    val articles: List<Article>,
    val status: String,
    val totalResults: Int
)