package com.example.vocabkid.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.example.vocabkid.data.local.entity.ReviewHistoryEntity
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
}
