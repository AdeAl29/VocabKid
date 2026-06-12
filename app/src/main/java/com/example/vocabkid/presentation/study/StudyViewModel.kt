package com.example.vocabkid.presentation.study

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.vocabkid.data.local.entity.WordWithProgressEntity
import com.example.vocabkid.data.repository.VocabKidRepository
import com.example.vocabkid.domain.model.ReviewMode
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class StudyViewModel(
    private val repository: VocabKidRepository
) : ViewModel() {
    val dueCards: StateFlow<List<WordWithProgressEntity>> = repository.observeDueWords()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptyList())

    var showMeaning by mutableStateOf(false)
        private set

    var isReviewing by mutableStateOf(false)
        private set

    fun revealMeaning() {
        showMeaning = true
    }

    fun review(wordId: Long, quality: Int) {
        if (isReviewing) return
        viewModelScope.launch {
            isReviewing = true
            try {
                repository.reviewWord(
                    wordId = wordId,
                    quality = quality,
                    mode = ReviewMode.FLASHCARD
                )
                showMeaning = false
            } finally {
                isReviewing = false
            }
        }
    }
}
