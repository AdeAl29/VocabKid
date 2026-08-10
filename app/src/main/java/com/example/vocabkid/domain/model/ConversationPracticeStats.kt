package com.example.vocabkid.domain.model

data class ConversationPracticeStats(
    val startedScenarios: Int = 0,
    val completedScenarios: Int = 0,
    val totalCompletions: Int = 0,
    val totalChoices: Int = 0,
    val lastPlayedAt: Long? = null
)
