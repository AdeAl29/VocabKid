package com.example.vocabkid.presentation.progress

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.vocabkid.data.repository.VocabKidRepository
import com.example.vocabkid.domain.model.ProgressStats
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn

class ProgressViewModel(
    repository: VocabKidRepository
) : ViewModel() {
    val stats: StateFlow<ProgressStats> = repository.observeProgressStats()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), ProgressStats())
}
