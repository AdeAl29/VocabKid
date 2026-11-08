package com.example.vocabkid.presentation.story

import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test

class StoryViewModelTest {

    @Test
    fun `initial state shows chapter list with default chapters`() {
        val viewModel = StoryViewModel()
        val state = viewModel.uiState.value

        assertEquals(StoryPhase.CHAPTER_LIST, state.phase)
        assertTrue(state.chapters.isNotEmpty())
        assertNull(state.activeChapter)
    }

    @Test
    fun `startChapter initializes reading phase and counts quizzes`() {
        val viewModel = StoryViewModel()
        val chapter = StoryData.chapters.first()
        val expectedQuizzes = chapter.scenes.count { it.quiz != null }

        viewModel.startChapter(chapter)
        val state = viewModel.uiState.value

        assertEquals(StoryPhase.READING, state.phase)
        assertEquals(chapter, state.activeChapter)
        assertEquals(0, state.sceneIndex)
        assertEquals(expectedQuizzes, state.totalQuizzes)
        assertEquals(0, state.correctCount)
        assertFalse(state.isAnswerRevealed)
    }

    @Test
    fun `completing all scenes with correct quiz answers awards 3 stars and marks chapter completed`() {
        val viewModel = StoryViewModel()
        val chapter = StoryData.chapters.first()
        viewModel.startChapter(chapter)

        while (viewModel.uiState.value.phase != StoryPhase.SUMMARY) {
            val currentState = viewModel.uiState.value
            val currentScene = currentState.currentScene
            assertNotNull(currentScene)

            val quiz = currentScene?.quiz
            if (currentState.phase == StoryPhase.QUIZ && quiz != null) {
                viewModel.selectOption(quiz.correctIndex)
                assertTrue(viewModel.uiState.value.isAnswerRevealed)
            }
            viewModel.advanceScene()
        }

        val summaryState = viewModel.uiState.value
        assertEquals(StoryPhase.SUMMARY, summaryState.phase)
        assertEquals(summaryState.totalQuizzes, summaryState.correctCount)
        assertEquals(3, summaryState.starCount)
        assertTrue(chapter.id in summaryState.completedChapterIds)
    }

    @Test
    fun `selectOption ignores subsequent clicks after answer is already revealed`() {
        val viewModel = StoryViewModel()
        val chapter = StoryData.chapters.first()
        viewModel.startChapter(chapter)

        // Advance until first quiz scene
        while (viewModel.uiState.value.phase != StoryPhase.QUIZ && !viewModel.uiState.value.isLastScene) {
            viewModel.advanceScene()
        }

        val quiz = viewModel.uiState.value.currentScene?.quiz
        assertNotNull(quiz)
        val wrongIndex = (quiz!!.correctIndex + 1) % quiz.options.size

        viewModel.selectOption(wrongIndex)
        assertEquals(0, viewModel.uiState.value.correctCount)

        // Attempting to change to the correct option after reveal should be ignored
        viewModel.selectOption(quiz.correctIndex)
        assertEquals(wrongIndex, viewModel.uiState.value.selectedOptionIndex)
        assertEquals(0, viewModel.uiState.value.correctCount)
    }
}

