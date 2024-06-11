package com.loc.newsapp.presentation.home

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.cachedIn
import com.loc.newsapp.domain.usecases.news.GetNews
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val getNewsUseCase: GetNews
): ViewModel() {

    private var defaultSources = listOf("bbc-news","abc-news","al-jazeera-english")
    private var sportsSources = listOf("espn", "bleacher-report")
    var state = mutableStateOf(HomeState())
        private set

    var news = getNewsUseCase(
        sources = switchNews()
    ).cachedIn(viewModelScope)
        private set

    private var count = 0
    fun switchNews(): List<String> {
        val sourcesToUse = if (count % 2 == 0) defaultSources else sportsSources
        news = getNewsUseCase(sources = sourcesToUse).cachedIn(viewModelScope)
        count++
        return sourcesToUse
    }
}