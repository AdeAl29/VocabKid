package com.example.vocabkid.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.example.vocabkid.data.local.entity.ReviewHistoryEntity
import com.example.vocabkid.data.local.model.ReviewModeCount
import kotlinx.coroutines.flow.Flow

@Dao
interface ReviewHistoryDao {
    @Insert
    suspend fun insertHistory(history: ReviewHistoryEntity): Long

    @Query("SELECT COUNT(*) FROM review_history")
    fun observeTotalReviewCount(): Flow<Int>

    @Query("SELECT COUNT(*) FROM review_history WHERE reviewDate >= :startOfDay")
    fun observeReviewCountSince(startOfDay: Long): Flow<Int>

    @Query("SELECT COUNT(*) FROM review_history WHERE isCorrect = 1")
    fun observeCorrectAnswerCount(): Flow<Int>

    @Query("SELECT COUNT(*) FROM review_history")
    fun observeAnswerCount(): Flow<Int>

    @Query("SELECT COUNT(*) FROM review_history")
    suspend fun countReviewHistory(): Int

    @Query(
        """
        SELECT * FROM review_history
        WHERE wordId = :wordId
        ORDER BY reviewDate DESC
        LIMIT :limit
        """
    )
    suspend fun getRecentHistoryForWord(
        wordId: Long,
        limit: Int = 20
    ): List<ReviewHistoryEntity>

    @Query(
        """
        SELECT mode, COUNT(*) AS reviewCount
        FROM review_history
        GROUP BY mode
        ORDER BY reviewCount DESC, mode ASC
        """
    )
    fun observeReviewModeCounts(): Flow<List<ReviewModeCount>>

    @Query("DELETE FROM review_history WHERE reviewDate < :beforeDate")
    suspend fun deleteHistoryBefore(beforeDate: Long): Int

    @Query(
        """
        DELETE FROM review_history
        WHERE wordId NOT IN (SELECT id FROM words)
        """
    )
    suspend fun deleteOrphanHistory(): Int
}
