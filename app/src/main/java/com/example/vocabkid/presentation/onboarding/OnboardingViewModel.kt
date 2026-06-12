package com.example.vocabkid.presentation.onboarding

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.vocabkid.data.repository.VocabKidRepository
import kotlinx.coroutines.launch

class OnboardingViewModel(
    private val repository: VocabKidRepository
) : ViewModel() {
    var name by mutableStateOf("")
        private set

    var grade by mutableStateOf(3)
        private set

    var errorMessage by mutableStateOf<String?>(null)
        private set

    var isSaving by mutableStateOf(false)
        private set

    fun updateName(value: String) {
        name = value
        errorMessage = null
    }

    fun updateGrade(value: Int) {
        grade = value
    }

    fun saveStudent(onSaved: () -> Unit) {
        val cleanName = name.trim()
        if (cleanName.isBlank()) {
            errorMessage = "Nama siswa wajib diisi."
            return
        }

        viewModelScope.launch {
            isSaving = true
            repository.saveStudent(cleanName, grade)
            isSaving = false
            onSaved()
        }
    }
}
