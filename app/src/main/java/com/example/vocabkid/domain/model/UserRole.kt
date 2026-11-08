package com.example.vocabkid.domain.model

enum class UserRole(val label: String) {
    MURID("Murid"),
    GURU("Guru");

    companion object {
        fun fromStorage(value: String?): UserRole =
            entries.firstOrNull { it.name == value } ?: MURID
    }
}
