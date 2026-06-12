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

    @Query("SELECT COUNT(*) FROM word_progress WHERE dueDate <= :today")
    fun observeDueCount(today: Long): Flow<Int>

    @Query("SELECT COUNT(*) FROM word_progress WHERE status = 'Dikuasai'")
    fun observeMasteredCount(): Flow<Int>

    @Query("SELECT COUNT(*) FROM word_progress WHERE status = 'Sering Salah'")
    fun observeFrequentlyWrongCount(): Flow<Int>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertProgress(progress: WordProgressEntity): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertProgressList(progressList: List<WordProgressEntity>)
}
