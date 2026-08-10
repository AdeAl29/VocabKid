package com.example.vocabkid.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.vocabkid.data.local.entity.ConversationChoiceHistoryEntity
import com.example.vocabkid.data.local.entity.ConversationScenarioProgressEntity
import com.example.vocabkid.domain.model.ConversationPracticeStats
import kotlinx.coroutines.flow.Flow

@Dao
interface ConversationProgressDao {
    @Query(
        """
        SELECT * FROM conversation_scenario_progress
        ORDER BY lastPlayedAt DESC, scenarioTitle ASC
        """
    )
    fun observeScenarioProgress(): Flow<List<ConversationScenarioProgressEntity>>

    @Query(
        """
        SELECT * FROM conversation_scenario_progress
        WHERE sceneId = :sceneId
        ORDER BY lastPlayedAt DESC, scenarioTitle ASC
        """
    )
    fun observeScenarioProgressForScene(sceneId: String): Flow<List<ConversationScenarioProgressEntity>>

    @Query("SELECT * FROM conversation_scenario_progress WHERE scenarioId = :scenarioId LIMIT 1")
    suspend fun getScenarioProgress(scenarioId: String): ConversationScenarioProgressEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertScenarioProgress(progress: ConversationScenarioProgressEntity)

    @Insert
    suspend fun insertChoiceHistory(history: ConversationChoiceHistoryEntity): Long

    @Query(
        """
        SELECT * FROM conversation_choice_history
        WHERE scenarioId = :scenarioId
        ORDER BY selectedAt DESC
        LIMIT :limit
        """
    )
    suspend fun getRecentChoicesForScenario(
        scenarioId: String,
        limit: Int = 20
    ): List<ConversationChoiceHistoryEntity>

    @Query(
        """
        SELECT
            COUNT(*) AS startedScenarios,
            COALESCE(SUM(CASE WHEN isCompleted = 1 THEN 1 ELSE 0 END), 0) AS completedScenarios,
            COALESCE(SUM(completedCount), 0) AS totalCompletions,
            COALESCE(SUM(choiceCount), 0) AS totalChoices,
            MAX(lastPlayedAt) AS lastPlayedAt
        FROM conversation_scenario_progress
        """
    )
    fun observePracticeStats(): Flow<ConversationPracticeStats>

    @Query("SELECT COUNT(*) FROM conversation_scenario_progress")
    suspend fun countScenarioProgressRows(): Int

    @Query("SELECT COUNT(*) FROM conversation_choice_history")
    suspend fun countChoiceHistoryRows(): Int

    @Query("DELETE FROM conversation_choice_history")
    suspend fun deleteAllChoiceHistory(): Int
}
