package com.example.vocabkid.presentation.navigation

object Routes {
    const val ONBOARDING = "onboarding"
    const val HOME = "home"
    const val STUDY = "study"
    const val QUIZ = "quiz"
    const val VOCABULARY = "vocabulary"
    const val VOCABULARY_DETAIL = "vocabulary_detail/{wordId}"
    const val PROGRESS = "progress"

    fun vocabularyDetail(wordId: Long): String = "vocabulary_detail/$wordId"
}
