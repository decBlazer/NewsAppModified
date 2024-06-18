package com.loc.newsapp.data.remote

import android.nfc.tech.MifareUltralight.PAGE_SIZE
import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.loc.newsapp.data.remote.dto.NewsResponse
import com.loc.newsapp.domain.model.Article
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get
import io.ktor.client.statement.bodyAsText
import io.ktor.http.isSuccess
import io.ktor.utils.io.errors.IOException

class NewsPagingSourceKtor(
    private val httpClient: HttpClient,
    private val sources: List<String>
) : PagingSource<Int, Article>() {

    private var totalNewsCount = 0

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, Article> {
        return try {
            val currentPage = params.key ?: 1
            val response =
                httpClient.get("https://newsapi.org/v2/top-headlines?country=us&apiKey=c5154edadbe64a0da23b8035c6aef5e9") {
                    // Add other necessary parameters like API key, page size, etc.
                }

            if (response.status.isSuccess()) {
                System.out.println("Success")
                val newsResponse: NewsResponse = response.body()
                val articles = newsResponse.articles.distinctBy { it.title }
                val totalResults = newsResponse.totalResults

                LoadResult.Page(
                    data = articles,
                    prevKey = if (currentPage == 1) null else currentPage - 1,
                    nextKey = if (articles.isEmpty() || totalResults <= currentPage * PAGE_SIZE) null else currentPage + 1
                )
            } else {
                System.out.println("Failure")
                LoadResult.Error(IOException("Error response ${response.status}: ${response.bodyAsText()}"))
            }
        } catch (e: Exception) {
            e.printStackTrace()
            LoadResult.Error(e)
        }
    }

    override fun getRefreshKey(state: PagingState<Int, Article>): Int? {
        // You can implement a more sophisticated refresh key logic if needed
        return state.anchorPosition?.let { anchorPosition ->
            state.closestPageToPosition(anchorPosition)?.prevKey?.plus(1)
                ?: state.closestPageToPosition(anchorPosition)?.nextKey?.minus(1)
        }
    }
}

