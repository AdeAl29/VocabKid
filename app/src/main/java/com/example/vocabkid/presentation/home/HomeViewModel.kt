package com.example.vocabkid.presentation.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.vocabkid.data.local.entity.StudentEntity
import com.example.vocabkid.data.local.entity.WordWithProgressEntity
import com.example.vocabkid.data.repository.VocabKidRepository
import com.example.vocabkid.domain.model.HomeStats
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class HomeViewModel(
    private val repository: VocabKidRepository
) : ViewModel() {
    val student: StateFlow<StudentEntity?> = repository.observeStudent()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), null)

    val stats: StateFlow<HomeStats> = repository.observeHomeStats()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), HomeStats())

    val difficultWords: StateFlow<List<WordWithProgressEntity>> = repository.observeDifficultWords()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptyList())

    fun logout(onReady: () -> Unit) {
        viewModelScope.launch {
            repository.clearStudent()
            onReady()
        }
    }
}
