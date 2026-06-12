package com.example.vocabkid.data.local.entity

import androidx.room.Embedded
import androidx.room.Relation

data class WordWithProgressEntity(
    @Embedded
    val word: WordEntity,
    @Relation(
        parentColumn = "id",
        entityColumn = "wordId"
    )
    val progress: WordProgressEntity?
)
