package com.example.vocabkid.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.vocabkid.data.local.entity.WordProgressEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface WordProgressDao {
    @Query("SELECT * FROM word_progress WHERE wordId = :wordId LIMIT 1")
    suspend fun getProgressByWordId(wordId: Long): WordProgressEntity?

    @Query("SELECT wordId FROM word_progress")
    suspend fun getProgressWordIds(): List<Long>

    @Query("SELECT COUNT(*) FROM word_progress")
    suspend fun countProgressRows(): Int

    @Query("SELECT COUNT(*) FROM word_progress WHERE dueDate <= :today")
    fun observeDueCount(today: Long): Flow<Int>

    @Query("SELECT COUNT(*) FROM word_progress WHERE status = 'Dipelajari'")
    fun observeLearningCount(): Flow<Int>

    @Query("SELECT COUNT(*) FROM word_progress WHERE status = 'Dikuasai'")
    fun observeMasteredCount(): Flow<Int>

    @Query("SELECT COUNT(*) FROM word_progress WHERE status = 'Sering Salah'")
    fun observeFrequentlyWrongCount(): Flow<Int>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertProgress(progress: WordProgressEntity): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertProgressList(progressList: List<WordProgressEntity>)

    @Query(
        """
        UPDATE word_progress
        SET repetition = 0,
            intervalDays = 0,
            easeFactor = 2.5,
            dueDate = :today,
            lastReviewedDate = NULL,
            correctCount = 0,
            wrongCount = 0,
            status = 'Baru',
            updatedAt = :updatedAt
        WHERE wordId = :wordId
        """
    )
    suspend fun resetProgressForWord(
        wordId: Long,
        today: Long,
        updatedAt: Long = System.currentTimeMillis()
    ): Int

    @Query(
        """
        DELETE FROM word_progress
        WHERE wordId NOT IN (SELECT id FROM words)
        """
    )
    suspend fun deleteOrphanProgress(): Int
}
