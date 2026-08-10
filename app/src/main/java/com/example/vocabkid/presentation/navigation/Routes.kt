package com.example.vocabkid.presentation.navigation

object Routes {
    const val AUTH = "auth"
    const val ONBOARDING = "onboarding"
    const val MAIN_TABS = "main_tabs"
    const val HOME = "home"
    const val STUDY = "study"
    const val QUIZ = "quiz"
    const val VOCABULARY = "vocabulary"
    const val VOCABULARY_DETAIL = "vocabulary_detail/{wordId}"
    const val PROGRESS = "progress"
    const val PROFILE = "profile"
    const val CONVERSATION = "conversation"
    const val PRONUNCIATION = "pronunciation"
    const val STORY = "story"

    fun vocabularyDetail(wordId: Long): String = "vocabulary_detail/$wordId"
}
