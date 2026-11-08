package com.example.vocabkid.presentation.quiz

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.vocabkid.data.repository.VocabKidRepository
import com.example.vocabkid.domain.model.QuizQuestion
import com.example.vocabkid.domain.model.ReviewMode
import kotlinx.coroutines.launch

data class QuizUiState(
    val questions: List<QuizQuestion> = emptyList(),
    val currentIndex: Int = 0,
    val selectedAnswer: String? = null,
    val isAnswerCorrect: Boolean? = null,
    val score: Int = 0,
    val selectedCategory: String? = null,
    val selectedLevel: Int? = null,
    val categories: List<String> = emptyList(),
    val levels: List<Int> = emptyList(),
    val unlockedLevel2Categories: Set<String> = emptySet(),
    val isFinished: Boolean = false,
    val isLoading: Boolean = true
) {
    val currentQuestion: QuizQuestion?
        get() = questions.getOrNull(currentIndex)
}

class QuizViewModel(
    private val repository: VocabKidRepository
) : ViewModel() {
    var uiState by mutableStateOf(QuizUiState())
        private set

    init {
        loadQuestions()
    }

    fun loadQuestions() {
        viewModelScope.launch {
            val requestedCategory = uiState.selectedCategory
            val requestedLevel = uiState.selectedLevel
            val unlockedCategories = uiState.unlockedLevel2Categories + repository.getUnlockedQuizLevel2Categories()
            uiState = uiState.copy(isLoading = true)
            val allWords = repository.getAllWords()
            val category = requestedCategory ?: allWords.firstOrNull()?.category
            val level2Unlocked = category in unlockedCategories
            val level = if (requestedLevel == 2 && !level2Unlocked) 1 else requestedLevel ?: 1
            val questions = repository.createQuizQuestions(questionCount = 10, category = category, level = level)
            uiState = QuizUiState(
                questions = questions,
                isLoading = false,
                isFinished = questions.isEmpty(),
                selectedCategory = category,
                selectedLevel = level,
                categories = allWords.map { it.category }.distinct().sorted(),
                levels = allWords.filter { it.category == category }.map { it.level }.distinct().filter { it == 1 || level2Unlocked }.sorted(),
                unlockedLevel2Categories = unlockedCategories
            )
        }
    }

    fun answer(choice: String) {
        val current = uiState.currentQuestion ?: return
        if (uiState.selectedAnswer != null) return

        val isCorrect = choice == current.correctAnswer
        uiState = uiState.copy(
            selectedAnswer = choice,
            isAnswerCorrect = isCorrect,
            score = uiState.score + if (isCorrect) 10 else 0
        )

        viewModelScope.launch {
            repository.reviewWord(
                wordId = current.word.id,
                quality = if (isCorrect) 4 else 0,
                mode = ReviewMode.QUIZ,
                isCorrectOverride = isCorrect
            )
        }
    }

    fun nextQuestion() {
        val nextIndex = uiState.currentIndex + 1
        uiState = if (nextIndex >= uiState.questions.size) {
            val completedLevel1Perfectly =
                uiState.selectedLevel == 1 &&
                    uiState.selectedCategory != null &&
                    uiState.questions.size == 10 &&
                    uiState.score == uiState.questions.size * 10
            val category = uiState.selectedCategory
            if (completedLevel1Perfectly) {
                repository.unlockQuizLevel2(category!!)
            }
            val unlockedCategories = if (completedLevel1Perfectly) {
                uiState.unlockedLevel2Categories + category!!
            } else {
                uiState.unlockedLevel2Categories
            }
            uiState.copy(
                isFinished = true,
                unlockedLevel2Categories = unlockedCategories,
                levels = if (category in unlockedCategories) uiState.levels.distinct().plus(2).sorted() else uiState.levels
            )
        } else {
            uiState.copy(
                currentIndex = nextIndex,
                selectedAnswer = null,
                isAnswerCorrect = null
            )
        }
    }

    fun restartQuiz() {
        loadQuestions()
    }

    fun selectCategory(category: String) {
        uiState = uiState.copy(selectedCategory = category)
        loadQuestions()
    }

    fun selectLevel(level: Int) {
        if (level == 2 && uiState.selectedCategory !in uiState.unlockedLevel2Categories) return
        uiState = uiState.copy(selectedLevel = level)
        loadQuestions()
    }
}
