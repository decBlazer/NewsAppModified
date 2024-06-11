package com.loc.newsapp.presentation.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.loc.newsapp.domain.model.Article
import com.loc.newsapp.domain.usecases.news.GetNews
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val getNewsUseCase: GetNews
) : ViewModel() {

    private val _articles: MutableStateFlow<PagingData<Article>> =
        MutableStateFlow(PagingData.empty())
    val articles: StateFlow<PagingData<Article>> get() = _articles

    private var defaultSources = listOf("bbc-news", "abc-news", "al-jazeera-english")
    private var sportsSources = listOf("espn", "bleacher-report")

    private var count = 1

    init {
        switchNews()
    }

    fun switchNews(): List<String> {
        val sourcesToUse = if (count % 2 == 0) defaultSources else sportsSources
        viewModelScope.launch {
            getNewsUseCase(sources = sourcesToUse)
                .cachedIn(viewModelScope)
                .collect {
                    _articles.value = it
                }
        }
        count++
        return sourcesToUse
    }

    fun getCount(): Int {
        return count
    }
}