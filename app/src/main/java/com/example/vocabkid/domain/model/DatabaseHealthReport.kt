package com.example.vocabkid.domain.model

data class DatabaseHealthReport(
    val studentCount: Int,
    val wordCount: Int,
    val progressRowCount: Int,
    val reviewHistoryCount: Int,
    val conversationLineCount: Int,
    val conversationScenarioProgressCount: Int,
    val conversationChoiceHistoryCount: Int
)

data class DatabaseMaintenanceResult(
    val deletedOrphanProgressRows: Int,
    val deletedOrphanHistoryRows: Int
)
