package com.example.vocabkid.domain.model

import com.example.vocabkid.data.local.entity.WordEntity

data class QuizQuestion(
    val word: WordEntity,
    val choices: List<String>
) {
    val correctAnswer: String = word.indonesianMeaning
}
