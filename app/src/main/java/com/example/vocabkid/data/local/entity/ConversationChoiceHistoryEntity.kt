package com.example.vocabkid.data.local.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "conversation_choice_history",
    foreignKeys = [
        ForeignKey(
            entity = ConversationScenarioProgressEntity::class,
            parentColumns = ["scenarioId"],
            childColumns = ["scenarioId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [
        Index(value = ["scenarioId"]),
        Index(value = ["sceneId"]),
        Index(value = ["selectedAt"])
    ]
)
data class ConversationChoiceHistoryEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val scenarioId: String,
    val sceneId: String,
    val stepIndex: Int,
    val choiceIndex: Int,
    val choiceEnglish: String,
    val choiceIndonesian: String,
    val responseEnglish: String,
    val selectedAt: Long = System.currentTimeMillis(),
    val createdAt: Long = System.currentTimeMillis()
)
