package com.example.vocabkid.presentation.vocabulary

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.vocabkid.data.local.entity.WordEntity
import com.example.vocabkid.data.local.entity.WordWithProgressEntity
import com.example.vocabkid.data.repository.VocabKidRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class VocabularyViewModel(
    private val repository: VocabKidRepository
) : ViewModel() {
    val words: StateFlow<List<WordWithProgressEntity>> = repository.observeWordsWithProgress()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptyList())

    fun observeWord(wordId: Long): Flow<WordWithProgressEntity?> {
        return repository.observeWordWithProgress(wordId)
    }

    fun addWord(
        englishWord: String,
        indonesianMeaning: String,
        category: String,
        exampleSentence: String
    ) {
        viewModelScope.launch {
            repository.addWord(
                englishWord = englishWord,
                indonesianMeaning = indonesianMeaning,
                category = category,
                exampleSentence = exampleSentence
            )
        }
    }

    fun updateWord(word: WordEntity) {
        viewModelScope.launch {
            repository.updateWord(word)
        }
    }

    fun deleteWord(word: WordEntity) {
        viewModelScope.launch {
            repository.deleteWord(word)
        }
    }
}
