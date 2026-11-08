package com.example.vocabkid.data.local.entity

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "roster_students",
    indices = [Index(value = ["nis"], unique = true), Index(value = ["grade"])]
)
data class RosterStudentEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val name: String,
    val nis: String,
    val grade: Int,
    val gender: String = "L",
    val email: String = "",
    val createdAt: Long = System.currentTimeMillis()
)
