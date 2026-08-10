package com.example.vocabkid.presentation.navigation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.vocabkid.data.repository.VocabKidRepository
import com.example.vocabkid.presentation.auth.AuthViewModel
import com.example.vocabkid.presentation.conversation.ConversationViewModel
import com.example.vocabkid.presentation.home.HomeViewModel
import com.example.vocabkid.presentation.onboarding.OnboardingViewModel
import com.example.vocabkid.presentation.profile.ProfileViewModel
import com.example.vocabkid.presentation.progress.ProgressViewModel
import com.example.vocabkid.presentation.quiz.QuizViewModel
import com.example.vocabkid.presentation.study.StudyViewModel
import com.example.vocabkid.presentation.chatassistant.ChatAssistantViewModel
import com.example.vocabkid.presentation.pronunciation.PronunciationViewModel
import com.example.vocabkid.presentation.vocabulary.VocabularyViewModel
import com.google.firebase.auth.FirebaseAuth

class VocabKidViewModelFactory(
    private val repository: VocabKidRepository,
    private val firebaseAuth: FirebaseAuth = FirebaseAuth.getInstance()
) : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return when {
            modelClass.isAssignableFrom(OnboardingViewModel::class.java) -> {
                OnboardingViewModel(repository) as T
            }
            modelClass.isAssignableFrom(AuthViewModel::class.java) -> {
                AuthViewModel(repository, firebaseAuth) as T
            }
            modelClass.isAssignableFrom(ConversationViewModel::class.java) -> {
                ConversationViewModel(repository) as T
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
            modelClass.isAssignableFrom(ProfileViewModel::class.java) -> {
                ProfileViewModel(repository) as T
            }
            modelClass.isAssignableFrom(PronunciationViewModel::class.java) -> {
                PronunciationViewModel(repository) as T
            }
            modelClass.isAssignableFrom(com.example.vocabkid.presentation.story.StoryViewModel::class.java) -> {
                com.example.vocabkid.presentation.story.StoryViewModel() as T
            }
            modelClass.isAssignableFrom(ChatAssistantViewModel::class.java) -> {
                ChatAssistantViewModel() as T
            }
            else -> throw IllegalArgumentException("Unknown ViewModel class: ${modelClass.name}")
        }
    }
}
