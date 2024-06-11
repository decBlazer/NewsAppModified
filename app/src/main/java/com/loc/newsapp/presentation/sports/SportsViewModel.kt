package com.loc.newsapp.presentation.home

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.cachedIn
import com.loc.newsapp.domain.usecases.news.GetNews
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class SportsViewModel @Inject constructor(
    private val getNewsUseCase: GetNews
): ViewModel() {

    var state = mutableStateOf(HomeState())
        private set

    val news = getNewsUseCase(
        sources = listOf("espn", "bleacher-report")
    ).cachedIn(viewModelScope)

}