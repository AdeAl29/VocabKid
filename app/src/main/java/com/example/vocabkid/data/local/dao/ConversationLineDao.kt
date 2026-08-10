package com.example.vocabkid.data.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.vocabkid.data.local.entity.ConversationLineEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface ConversationLineDao {
    @Query("SELECT * FROM conversation_lines ORDER BY displayOrder ASC, createdAt ASC")
    fun observeConversationLines(): Flow<List<ConversationLineEntity>>

    @Query("SELECT COALESCE(MAX(displayOrder), -1) + 1 FROM conversation_lines")
    suspend fun nextDisplayOrder(): Int

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertConversationLine(line: ConversationLineEntity): Long

    @Update
    suspend fun updateConversationLine(line: ConversationLineEntity)

    @Delete
    suspend fun deleteConversationLine(line: ConversationLineEntity)

    @Query("DELETE FROM conversation_lines")
    suspend fun deleteAllConversationLines(): Int

    @Query("SELECT COUNT(*) FROM conversation_lines")
    suspend fun countConversationLines(): Int
}
