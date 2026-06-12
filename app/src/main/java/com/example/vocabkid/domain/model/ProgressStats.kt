package com.example.vocabkid.domain.model

data class ProgressStats(
    val totalWords: Int = 0,
    val masteredWords: Int = 0,
    val dueWords: Int = 0,
    val frequentlyWrongWords: Int = 0,
    val accuracyPercent: Int = 0,
    val totalReviews: Int = 0
)
