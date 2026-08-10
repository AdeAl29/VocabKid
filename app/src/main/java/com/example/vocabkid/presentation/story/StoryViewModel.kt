package com.example.vocabkid.presentation.story

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

// ─── UI State ─────────────────────────────────────────────────────────────────

enum class StoryPhase {
    CHAPTER_LIST,
    READING,
    QUIZ,
    SUMMARY
}

data class StoryUiState(
    val phase: StoryPhase = StoryPhase.CHAPTER_LIST,
    val chapters: List<StoryChapter> = StoryData.chapters,
    val activeChapter: StoryChapter? = null,
    val sceneIndex: Int = 0,
    val selectedOptionIndex: Int = -1,
    val isAnswerRevealed: Boolean = false,
    val correctCount: Int = 0,
    val totalQuizzes: Int = 0,
    val narrationDone: Boolean = false,
    val completedChapterIds: Set<Int> = emptySet()
) {
    val currentScene: StoryScene?
        get() = activeChapter?.scenes?.getOrNull(sceneIndex)

    val isLastScene: Boolean
        get() = activeChapter?.let { sceneIndex >= it.scenes.size - 1 } ?: false

    val progressFraction: Float
        get() = activeChapter?.let {
            if (it.scenes.isEmpty()) 0f
            else (sceneIndex + 1).toFloat() / it.scenes.size.toFloat()
        } ?: 0f

    val starCount: Int
        get() = when {
            totalQuizzes == 0 -> 3
            correctCount == totalQuizzes -> 3
            correctCount >= totalQuizzes * 2 / 3 -> 2
            correctCount >= 1 -> 1
            else -> 0
        }
}

// ─── ViewModel ───────────────────────────────────────────────────────────────

class StoryViewModel : ViewModel() {

    private val _uiState = MutableStateFlow(StoryUiState())
    val uiState: StateFlow<StoryUiState> = _uiState.asStateFlow()

    fun startChapter(chapter: StoryChapter) {
        val totalQuizzes = chapter.scenes.count { it.quiz != null }
        _uiState.update {
            it.copy(
                phase = StoryPhase.READING,
                activeChapter = chapter,
                sceneIndex = 0,
                selectedOptionIndex = -1,
                isAnswerRevealed = false,
                correctCount = 0,
                totalQuizzes = totalQuizzes,
                narrationDone = false
            )
        }
    }

    fun onNarrationDone() {
        _uiState.update { it.copy(narrationDone = true) }
    }

    fun advanceScene() {
        val state = _uiState.value
        val chapter = state.activeChapter ?: return

        if (state.isLastScene) {
            _uiState.update { s ->
                s.copy(
                    phase = StoryPhase.SUMMARY,
                    completedChapterIds = s.completedChapterIds + chapter.id
                )
            }
            return
        }

        val nextIndex = state.sceneIndex + 1
        val nextScene = chapter.scenes[nextIndex]

        if (nextScene.quiz != null) {
            _uiState.update {
                it.copy(
                    sceneIndex = nextIndex,
                    phase = StoryPhase.QUIZ,
                    selectedOptionIndex = -1,
                    isAnswerRevealed = false,
                    narrationDone = false
                )
            }
        } else {
            _uiState.update {
                it.copy(
                    sceneIndex = nextIndex,
                    phase = StoryPhase.READING,
                    selectedOptionIndex = -1,
                    isAnswerRevealed = false,
                    narrationDone = false
                )
            }
        }
    }

    fun selectOption(index: Int) {
        val state = _uiState.value
        if (state.isAnswerRevealed) return
        val quiz = state.currentScene?.quiz ?: return
        val isCorrect = index == quiz.correctIndex
        _uiState.update {
            it.copy(
                selectedOptionIndex = index,
                isAnswerRevealed = true,
                correctCount = if (isCorrect) it.correctCount + 1 else it.correctCount
            )
        }
    }

    fun goToChapterList() {
        _uiState.update {
            it.copy(
                phase = StoryPhase.CHAPTER_LIST,
                activeChapter = null,
                sceneIndex = 0,
                selectedOptionIndex = -1,
                isAnswerRevealed = false,
                narrationDone = false
            )
        }
    }

    fun restartChapter() {
        val chapter = _uiState.value.activeChapter ?: return
        startChapter(chapter)
    }
}
