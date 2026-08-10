package com.example.vocabkid.data.local.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "conversation_lines",
    indices = [Index(value = ["displayOrder", "createdAt"])]
)
data class ConversationLineEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val speaker: String,
    val englishSentence: String,
    val indonesianMeaning: String,
    val displayOrder: Int,
    val createdAt: Long = System.currentTimeMillis(),
    @ColumnInfo(defaultValue = "0")
    val updatedAt: Long = System.currentTimeMillis()
)
