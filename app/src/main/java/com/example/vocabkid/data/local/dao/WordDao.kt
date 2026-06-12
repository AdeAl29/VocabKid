package com.example.vocabkid.data.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Update
import com.example.vocabkid.data.local.entity.WordEntity
import com.example.vocabkid.data.local.entity.WordWithProgressEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface WordDao {
    @Transaction
    @Query("SELECT * FROM words ORDER BY category ASC, englishWord ASC")
    fun observeWordsWithProgress(): Flow<List<WordWithProgressEntity>>

    @Transaction
    @Query("SELECT * FROM words WHERE id = :wordId LIMIT 1")
    fun observeWordWithProgress(wordId: Long): Flow<WordWithProgressEntity?>

    @Transaction
    @Query(
        """
        SELECT words.* FROM words
        INNER JOIN word_progress ON words.id = word_progress.wordId
        WHERE word_progress.dueDate <= :today
        ORDER BY word_progress.dueDate ASC, words.englishWord ASC
        """
    )
    fun observeDueWords(today: Long): Flow<List<WordWithProgressEntity>>

    @Query("SELECT * FROM words ORDER BY englishWord ASC")
    suspend fun getAllWords(): List<WordEntity>

    @Query("SELECT COUNT(*) FROM words")
    fun observeWordCount(): Flow<Int>

    @Query("SELECT COUNT(*) FROM words")
    suspend fun countWords(): Int

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertWord(word: WordEntity): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertWords(words: List<WordEntity>): List<Long>

    @Update
    suspend fun updateWord(word: WordEntity)

    @Delete
    suspend fun deleteWord(word: WordEntity)
}
