package com.example.vocabkid.data.local.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "words",
    indices = [
        Index(value = ["normalizedEnglishWord"]),
        Index(value = ["category", "englishWord"])
    ]
)
data class WordEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val englishWord: String,
    val indonesianMeaning: String,
    val category: String,
    val exampleSentence: String,
    @ColumnInfo(defaultValue = "''")
    val normalizedEnglishWord: String = englishWord.trim().lowercase(),
    @ColumnInfo(defaultValue = "0")
    val createdAt: Long = System.currentTimeMillis(),
    @ColumnInfo(defaultValue = "0")
    val updatedAt: Long = System.currentTimeMillis()
)
