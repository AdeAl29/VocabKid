package com.example.vocabkid.data.local.database

import androidx.room.testing.MigrationTestHelper
import androidx.sqlite.db.framework.FrameworkSQLiteOpenHelperFactory
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import java.io.IOException

@RunWith(AndroidJUnit4::class)
class VocabKidDatabaseMigrationTest {

    private val testDbName = "vocabkid-migration-test"

    @get:Rule
    val helper: MigrationTestHelper = MigrationTestHelper(
        InstrumentationRegistry.getInstrumentation(),
        VocabKidDatabase::class.java,
        emptyList(),
        FrameworkSQLiteOpenHelperFactory()
    )

    @Test
    @Throws(IOException::class)
    fun migrateAllFrom1To10() {
        // Create earliest schema version (1) and close it
        helper.createDatabase(testDbName, 1).apply {
            execSQL(
                """
                INSERT INTO students (id, name, grade)
                VALUES (1, 'Budi', 4)
                """.trimIndent()
            )
            close()
        }

        // Run all migrations up to version 10 and validate schema
        helper.runMigrationsAndValidate(
            testDbName,
            10,
            true,
            *VocabKidDatabase.ALL_MIGRATIONS
        )
    }
}

