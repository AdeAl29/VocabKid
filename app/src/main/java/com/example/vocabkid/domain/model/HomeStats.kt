package com.example.vocabkid.domain.model

data class HomeStats(
    val dueToday: Int = 0,
    val masteredWords: Int = 0,
    val reviewsToday: Int = 0,
    val totalWords: Int = 0
)
