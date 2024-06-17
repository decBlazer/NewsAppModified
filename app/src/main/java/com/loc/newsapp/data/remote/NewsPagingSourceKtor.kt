import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.loc.newsapp.data.remote.dto.NewsResponse
import com.loc.newsapp.domain.model.Article
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get
import io.ktor.client.request.parameter

class   NewsPagingSourceKtor(
    private val httpClient: HttpClient,
    private val sources: List<String>
) : PagingSource<Int, Article>() {

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, Article> {
        return try {
            val currentPage = params.key ?: 1 // Start with page 1 if no key is provided
            val response =
                httpClient.get("https://newsapi.org/v2/everything?apiKey=c5154edadbe64a0da23b8035c6aef5e9") {
                    parameter("sources", sources.joinToString(","))
                    parameter("page", currentPage)
                    // Add other necessary parameters like API key, page size, etc.
                }

            val newsResponse: NewsResponse = response.body()
            val articles = newsResponse.articles

            val nextPage = if (articles.isNotEmpty()) currentPage + 1 else null
            LoadResult.Page(
                data = articles,
                prevKey = if (currentPage == 1) null else currentPage - 1,
                nextKey = nextPage
            )
        } catch (e: Exception) {
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

