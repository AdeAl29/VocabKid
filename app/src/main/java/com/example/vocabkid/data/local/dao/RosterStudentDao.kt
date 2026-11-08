package com.example.vocabkid.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.vocabkid.data.local.entity.RosterStudentEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface RosterStudentDao {
    @Query("SELECT * FROM roster_students ORDER BY grade ASC, name ASC")
    fun observeAll(): Flow<List<RosterStudentEntity>>

    @Query("SELECT COUNT(*) FROM roster_students")
    suspend fun count(): Int

    @Query("DELETE FROM roster_students")
    suspend fun deleteAll()

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertAll(students: List<RosterStudentEntity>)
}
