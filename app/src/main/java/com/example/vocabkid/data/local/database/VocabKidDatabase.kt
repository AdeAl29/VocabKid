package com.example.vocabkid.data.local.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import com.example.vocabkid.data.local.dao.ConversationLineDao
import com.example.vocabkid.data.local.dao.ConversationProgressDao
import com.example.vocabkid.data.local.dao.ReviewHistoryDao
import com.example.vocabkid.data.local.dao.StudentDao
import com.example.vocabkid.data.local.dao.WordDao
import com.example.vocabkid.data.local.dao.WordProgressDao
import com.example.vocabkid.data.local.entity.ConversationChoiceHistoryEntity
import com.example.vocabkid.data.local.entity.ConversationLineEntity
import com.example.vocabkid.data.local.entity.ConversationScenarioProgressEntity
import com.example.vocabkid.data.local.entity.ReviewHistoryEntity
import com.example.vocabkid.data.local.entity.StudentEntity
import com.example.vocabkid.data.local.entity.WordEntity
import com.example.vocabkid.data.local.entity.WordProgressEntity

@Database(
    entities = [
        StudentEntity::class,
        WordEntity::class,
        WordProgressEntity::class,
        ReviewHistoryEntity::class,
        ConversationLineEntity::class,
        ConversationScenarioProgressEntity::class,
        ConversationChoiceHistoryEntity::class
    ],
    version = 5,
    exportSchema = true
)
abstract class VocabKidDatabase : RoomDatabase() {
    abstract fun studentDao(): StudentDao
    abstract fun wordDao(): WordDao
    abstract fun wordProgressDao(): WordProgressDao
    abstract fun reviewHistoryDao(): ReviewHistoryDao
    abstract fun conversationLineDao(): ConversationLineDao
    abstract fun conversationProgressDao(): ConversationProgressDao

    companion object {
        @Volatile
        private var INSTANCE: VocabKidDatabase? = null

        fun getDatabase(context: Context): VocabKidDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    VocabKidDatabase::class.java,
                    "vocabkid.db"
                )
                    .addMigrations(MIGRATION_1_2, MIGRATION_2_3, MIGRATION_3_4, MIGRATION_4_5)
                    .build()
                INSTANCE = instance
                instance
            }
        }

        private val MIGRATION_1_2 = object : Migration(1, 2) {
            override fun migrate(db: SupportSQLiteDatabase) {
                db.execSQL(
                    "ALTER TABLE students ADD COLUMN avatar TEXT NOT NULL DEFAULT 'siswa'"
                )
            }
        }

        private val MIGRATION_2_3 = object : Migration(2, 3) {
            override fun migrate(db: SupportSQLiteDatabase) {
                db.execSQL(
                    """
                    CREATE TABLE IF NOT EXISTS conversation_lines (
                        id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
                        speaker TEXT NOT NULL,
                        englishSentence TEXT NOT NULL,
                        indonesianMeaning TEXT NOT NULL,
                        displayOrder INTEGER NOT NULL,
                        createdAt INTEGER NOT NULL
                    )
                    """.trimIndent()
                )
            }
        }

        private val MIGRATION_3_4 = object : Migration(3, 4) {
            override fun migrate(db: SupportSQLiteDatabase) {
                val nowExpression = "CAST(strftime('%s','now') AS INTEGER) * 1000"

                db.execSQL("ALTER TABLE words ADD COLUMN normalizedEnglishWord TEXT NOT NULL DEFAULT ''")
                db.execSQL("ALTER TABLE words ADD COLUMN createdAt INTEGER NOT NULL DEFAULT 0")
                db.execSQL("ALTER TABLE words ADD COLUMN updatedAt INTEGER NOT NULL DEFAULT 0")
                db.execSQL(
                    """
                    UPDATE words
                    SET normalizedEnglishWord = LOWER(TRIM(englishWord)),
                        createdAt = $nowExpression,
                        updatedAt = $nowExpression
                    WHERE normalizedEnglishWord = ''
                    """.trimIndent()
                )

                db.execSQL("ALTER TABLE students ADD COLUMN createdAt INTEGER NOT NULL DEFAULT 0")
                db.execSQL("ALTER TABLE students ADD COLUMN updatedAt INTEGER NOT NULL DEFAULT 0")
                db.execSQL(
                    """
                    UPDATE students
                    SET createdAt = $nowExpression,
                        updatedAt = $nowExpression
                    WHERE createdAt = 0
                    """.trimIndent()
                )

                db.execSQL("ALTER TABLE word_progress ADD COLUMN updatedAt INTEGER NOT NULL DEFAULT 0")
                db.execSQL(
                    """
                    UPDATE word_progress
                    SET updatedAt = COALESCE(lastReviewedDate, dueDate, $nowExpression)
                    WHERE updatedAt = 0
                    """.trimIndent()
                )

                db.execSQL("ALTER TABLE conversation_lines ADD COLUMN updatedAt INTEGER NOT NULL DEFAULT 0")
                db.execSQL(
                    """
                    UPDATE conversation_lines
                    SET updatedAt = createdAt
                    WHERE updatedAt = 0
                    """.trimIndent()
                )

                db.execSQL("CREATE INDEX IF NOT EXISTS index_words_normalizedEnglishWord ON words(normalizedEnglishWord)")
                db.execSQL("CREATE INDEX IF NOT EXISTS index_words_category_englishWord ON words(category, englishWord)")
                db.execSQL("CREATE INDEX IF NOT EXISTS index_students_name ON students(name)")
                db.execSQL("CREATE INDEX IF NOT EXISTS index_word_progress_dueDate ON word_progress(dueDate)")
                db.execSQL("CREATE INDEX IF NOT EXISTS index_word_progress_status ON word_progress(status)")
                db.execSQL("CREATE INDEX IF NOT EXISTS index_word_progress_lastReviewedDate ON word_progress(lastReviewedDate)")
                db.execSQL("CREATE INDEX IF NOT EXISTS index_review_history_reviewDate ON review_history(reviewDate)")
                db.execSQL("CREATE INDEX IF NOT EXISTS index_review_history_mode ON review_history(mode)")
                db.execSQL(
                    """
                    CREATE INDEX IF NOT EXISTS index_conversation_lines_displayOrder_createdAt
                    ON conversation_lines(displayOrder, createdAt)
                    """.trimIndent()
                )
            }
        }

        private val MIGRATION_4_5 = object : Migration(4, 5) {
            override fun migrate(db: SupportSQLiteDatabase) {
                db.execSQL(
                    """
                    CREATE TABLE IF NOT EXISTS conversation_scenario_progress (
                        scenarioId TEXT NOT NULL,
                        sceneId TEXT NOT NULL,
                        sceneTitle TEXT NOT NULL,
                        scenarioTitle TEXT NOT NULL,
                        partnerName TEXT NOT NULL,
                        totalSteps INTEGER NOT NULL,
                        completedSteps INTEGER NOT NULL,
                        choiceCount INTEGER NOT NULL,
                        completedCount INTEGER NOT NULL,
                        isCompleted INTEGER NOT NULL,
                        firstPlayedAt INTEGER NOT NULL,
                        lastPlayedAt INTEGER NOT NULL,
                        createdAt INTEGER NOT NULL,
                        updatedAt INTEGER NOT NULL,
                        PRIMARY KEY(scenarioId)
                    )
                    """.trimIndent()
                )
                db.execSQL("CREATE INDEX IF NOT EXISTS index_conversation_scenario_progress_sceneId ON conversation_scenario_progress(sceneId)")
                db.execSQL("CREATE INDEX IF NOT EXISTS index_conversation_scenario_progress_lastPlayedAt ON conversation_scenario_progress(lastPlayedAt)")
                db.execSQL("CREATE INDEX IF NOT EXISTS index_conversation_scenario_progress_completedCount ON conversation_scenario_progress(completedCount)")

                db.execSQL(
                    """
                    CREATE TABLE IF NOT EXISTS conversation_choice_history (
                        id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
                        scenarioId TEXT NOT NULL,
                        sceneId TEXT NOT NULL,
                        stepIndex INTEGER NOT NULL,
                        choiceIndex INTEGER NOT NULL,
                        choiceEnglish TEXT NOT NULL,
                        choiceIndonesian TEXT NOT NULL,
                        responseEnglish TEXT NOT NULL,
                        selectedAt INTEGER NOT NULL,
                        createdAt INTEGER NOT NULL,
                        FOREIGN KEY(scenarioId)
                            REFERENCES conversation_scenario_progress(scenarioId)
                            ON UPDATE NO ACTION
                            ON DELETE CASCADE
                    )
                    """.trimIndent()
                )
                db.execSQL("CREATE INDEX IF NOT EXISTS index_conversation_choice_history_scenarioId ON conversation_choice_history(scenarioId)")
                db.execSQL("CREATE INDEX IF NOT EXISTS index_conversation_choice_history_sceneId ON conversation_choice_history(sceneId)")
                db.execSQL("CREATE INDEX IF NOT EXISTS index_conversation_choice_history_selectedAt ON conversation_choice_history(selectedAt)")
            }
        }
    }
}
