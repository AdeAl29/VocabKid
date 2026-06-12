package com.example.vocabkid.data.local.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.vocabkid.data.local.dao.ReviewHistoryDao
import com.example.vocabkid.data.local.dao.StudentDao
import com.example.vocabkid.data.local.dao.WordDao
import com.example.vocabkid.data.local.dao.WordProgressDao
import com.example.vocabkid.data.local.entity.ReviewHistoryEntity
import com.example.vocabkid.data.local.entity.StudentEntity
import com.example.vocabkid.data.local.entity.WordEntity
import com.example.vocabkid.data.local.entity.WordProgressEntity

@Database(
    entities = [
        StudentEntity::class,
        WordEntity::class,
        WordProgressEntity::class,
        ReviewHistoryEntity::class
    ],
    version = 1,
    exportSchema = false
)
abstract class VocabKidDatabase : RoomDatabase() {
    abstract fun studentDao(): StudentDao
    abstract fun wordDao(): WordDao
    abstract fun wordProgressDao(): WordProgressDao
    abstract fun reviewHistoryDao(): ReviewHistoryDao

    companion object {
        @Volatile
        private var INSTANCE: VocabKidDatabase? = null

        fun getDatabase(context: Context): VocabKidDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    VocabKidDatabase::class.java,
                    "vocabkid.db"
                ).build()
                INSTANCE = instance
                instance
            }
        }
    }
}
