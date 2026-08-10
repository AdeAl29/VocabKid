package com.example.vocabkid.data.local.entity

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "conversation_scenario_progress",
    indices = [
        Index(value = ["sceneId"]),
        Index(value = ["lastPlayedAt"]),
        Index(value = ["completedCount"])
    ]
)
data class ConversationScenarioProgressEntity(
    @PrimaryKey
    val scenarioId: String,
    val sceneId: String,
    val sceneTitle: String,
    val scenarioTitle: String,
    val partnerName: String,
    val totalSteps: Int,
    val completedSteps: Int = 0,
    val choiceCount: Int = 0,
    val completedCount: Int = 0,
    val isCompleted: Boolean = false,
    val firstPlayedAt: Long = System.currentTimeMillis(),
    val lastPlayedAt: Long = System.currentTimeMillis(),
    val createdAt: Long = System.currentTimeMillis(),
    val updatedAt: Long = System.currentTimeMillis()
)
