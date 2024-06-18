package com.loc.newsapp.data.remote

import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.loc.newsapp.data.remote.dto.NewsResponse
import com.loc.newsapp.domain.model.Article
import io.ktor.client.HttpClient
import io.ktor.client.request.get
import io.ktor.client.statement.HttpResponse
import io.ktor.client.statement.bodyAsText
import io.ktor.http.isSuccess
import io.ktor.util.InternalAPI
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
    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, Article> {
        val currentPage = params.key ?: 1

        return try {
            val response: HttpResponse = httpClient.get("https://newsapi.org/v2/top-headlines?q=$searchQuery&apiKey=$apiKey")

            if (response.status.isSuccess()) {
                val jsonString = response.bodyAsText()
                val newsResponse = Json.decodeFromString<NewsResponse>(jsonString)
                val articles = newsResponse.articles

                val nextPage = if (articles.isNotEmpty()) currentPage + 1 else null

                LoadResult.Page(
                    data = articles,
                    prevKey = if (currentPage == 1) null else currentPage - 1,
                    nextKey = nextPage
                )
            } else {
                LoadResult.Error(Exception("HTTP Error: ${response.status}"))
            }
        } catch (e: Exception) {
            LoadResult.Error(e)
        }
    }
}