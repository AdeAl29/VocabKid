package com.example.vocabkid.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.vocabkid.data.local.entity.StudentEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface StudentDao {
    @Query("SELECT * FROM students ORDER BY id DESC LIMIT 1")
    fun observeStudent(): Flow<StudentEntity?>

    @Query("SELECT * FROM students ORDER BY id DESC LIMIT 1")
    suspend fun getStudent(): StudentEntity?

    @Query("DELETE FROM students")
    suspend fun clearStudents()

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertStudent(student: StudentEntity): Long
}
