package com.loc.newsapp.data.remote

import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.loc.newsapp.data.remote.dto.NewsResponse
import com.loc.newsapp.domain.model.Article
import io.ktor.client.*
import io.ktor.client.request.*
import io.ktor.client.statement.HttpResponse
import io.ktor.client.statement.bodyAsText
import io.ktor.http.*
import io.ktor.http.ContentType.Application.Json
import io.ktor.util.InternalAPI
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json


class SearchNewsPagingSourceKtor(
    private val httpClient: HttpClient,
    private val searchQuery: String,
    private val sources: String,
    private val apiKey: String
) : PagingSource<Int, Article>() {

    override fun getRefreshKey(state: PagingState<Int, Article>): Int? {
        return state.anchorPosition?.let { anchorPosition ->
            state.closestPageToPosition(anchorPosition)?.prevKey?.plus(1)
                ?: state.closestPageToPosition(anchorPosition)?.nextKey?.minus(1)
        }
    }

    @OptIn(InternalAPI::class)
    suspend fun fetchNews(pageNumber: Int): LoadResult<Int, Article>? {
        val url = "https://newsapi.org/v2/everything?" +
                "apiKey=c5154edadbe64a0da23b8035c6aef5e9" +
                searchQuery + // Add search query if needed
                "sources=your-sources&" + // Add sources if needed
                "page=$pageNumber"

        return try {
            val response: HttpResponse = httpClient.get(url)
            if (response.status.isSuccess()) {
                val jsonString = response.bodyAsText()
                val newsResponse = Json.decodeFromString<NewsResponse>(jsonString)
                LoadResult.Page(
                    data = newsResponse.articles,
                    prevKey = if (pageNumber == 1) null else pageNumber - 1,
                    nextKey = if (newsResponse.articles.isEmpty()) null else pageNumber + 1
                )
            } else {
                LoadResult.Error(Exception("HTTP Error: ${response.status}"))
            }
        } catch (e: Exception) {
            LoadResult.Error(e)
        }
    }

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, Article> {
        TODO("Not yet implemented")
    }
}