package com.example.vocabkid

import android.app.Application
import com.example.vocabkid.data.local.database.VocabKidDatabase
import com.example.vocabkid.data.repository.VocabKidRepository

class VocabKidApplication : Application() {
    val database: VocabKidDatabase by lazy {
        VocabKidDatabase.getDatabase(this)
    }

    val repository: VocabKidRepository by lazy {
        VocabKidRepository(database)
    }
}
