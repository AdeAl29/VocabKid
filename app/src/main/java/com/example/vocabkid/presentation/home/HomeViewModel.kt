package com.example.vocabkid.presentation.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.vocabkid.data.local.entity.StudentEntity
import com.example.vocabkid.data.repository.VocabKidRepository
import com.example.vocabkid.domain.model.HomeStats
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn

class HomeViewModel(
    repository: VocabKidRepository
) : ViewModel() {
    val student: StateFlow<StudentEntity?> = repository.observeStudent()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), null)

    val stats: StateFlow<HomeStats> = repository.observeHomeStats()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), HomeStats())
}
