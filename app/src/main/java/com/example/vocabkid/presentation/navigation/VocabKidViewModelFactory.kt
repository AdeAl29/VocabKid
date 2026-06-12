package com.example.vocabkid.presentation.navigation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.vocabkid.data.repository.VocabKidRepository
import com.example.vocabkid.presentation.home.HomeViewModel
import com.example.vocabkid.presentation.onboarding.OnboardingViewModel
import com.example.vocabkid.presentation.progress.ProgressViewModel
import com.example.vocabkid.presentation.quiz.QuizViewModel
import com.example.vocabkid.presentation.study.StudyViewModel
import com.example.vocabkid.presentation.vocabulary.VocabularyViewModel

class VocabKidViewModelFactory(
    private val repository: VocabKidRepository
) : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return when {
            modelClass.isAssignableFrom(OnboardingViewModel::class.java) -> {
                OnboardingViewModel(repository) as T
            }
            modelClass.isAssignableFrom(HomeViewModel::class.java) -> {
                HomeViewModel(repository) as T
            }
            modelClass.isAssignableFrom(StudyViewModel::class.java) -> {
                StudyViewModel(repository) as T
            }
            modelClass.isAssignableFrom(QuizViewModel::class.java) -> {
                QuizViewModel(repository) as T
            }
            modelClass.isAssignableFrom(VocabularyViewModel::class.java) -> {
                VocabularyViewModel(repository) as T
            }
            modelClass.isAssignableFrom(ProgressViewModel::class.java) -> {
                ProgressViewModel(repository) as T
            }
            else -> throw IllegalArgumentException("Unknown ViewModel class: ${modelClass.name}")
        }
    }
}
