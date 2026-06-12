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
            uiState = QuizUiState(isLoading = true)
            val questions = repository.createQuizQuestions(questionCount = 10)
            uiState = QuizUiState(
                questions = questions,
                isLoading = false,
                isFinished = questions.isEmpty()
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
            score = uiState.score + if (isCorrect) 1 else 0
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
            uiState.copy(isFinished = true)
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
}
