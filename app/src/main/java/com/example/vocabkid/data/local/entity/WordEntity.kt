package com.example.vocabkid.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "words")
data class WordEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val englishWord: String,
    val indonesianMeaning: String,
    val category: String,
    val exampleSentence: String
)
