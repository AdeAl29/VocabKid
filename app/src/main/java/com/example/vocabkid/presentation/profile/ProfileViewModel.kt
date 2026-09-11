package com.example.vocabkid.presentation.profile

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.vocabkid.data.repository.VocabKidRepository
import com.example.vocabkid.domain.model.StudentAvatar
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class ProfileViewModel(
    private val repository: VocabKidRepository
) : ViewModel() {
    val student = repository.observeStudent()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), null)

    var name by mutableStateOf("")
        private set

    var grade by mutableStateOf(3)
        private set

    var nis by mutableStateOf("")
        private set

    var avatar by mutableStateOf(StudentAvatar.SISWA)
        private set

    var errorMessage by mutableStateOf<String?>(null)
        private set

    var successMessage by mutableStateOf<String?>(null)
        private set

    var isSaving by mutableStateOf(false)
        private set

    private var loadedStudentId: Long? = null

    init {
        viewModelScope.launch {
            student.collect { currentStudent ->
                if (currentStudent != null && loadedStudentId != currentStudent.id) {
                    loadedStudentId = currentStudent.id
                    name = currentStudent.name
                    nis = currentStudent.nis
                    grade = currentStudent.grade
                    avatar = StudentAvatar.fromId(currentStudent.avatar)
                    errorMessage = null
                }
            }
        }
    }

    fun updateName(value: String) {
        name = value
        errorMessage = null
        successMessage = null
    }

    fun updateGrade(value: Int) {
        grade = value
        errorMessage = null
        successMessage = null
    }

    fun updateNis(value: String) {
        nis = value
        errorMessage = null
        successMessage = null
    }

    fun updateAvatar(value: StudentAvatar) {
        avatar = value
        errorMessage = null
        successMessage = null
    }

    fun saveProfile() {
        val cleanName = name.trim()
        if (cleanName.isBlank()) {
            errorMessage = "Nama siswa wajib diisi."
            successMessage = null
            return
        }

        viewModelScope.launch {
            isSaving = true
            try {
                repository.saveStudent(cleanName, grade, avatar, nis = nis.trim())
                errorMessage = null
                successMessage = "Profil berhasil disimpan."
            } catch (exception: Exception) {
                errorMessage = "Profil belum bisa disimpan. Coba lagi."
                successMessage = null
            } finally {
                isSaving = false
            }
        }
    }
}
