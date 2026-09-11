package com.example.vocabkid.presentation.auth

import android.util.Patterns
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.vocabkid.data.repository.VocabKidRepository
import com.google.android.gms.tasks.Task
import com.google.firebase.FirebaseNetworkException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.FirebaseAuthInvalidUserException
import com.google.firebase.auth.FirebaseAuthUserCollisionException
import com.google.firebase.auth.FirebaseAuthWeakPasswordException
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.UserProfileChangeRequest
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException

class AuthViewModel(
    private val repository: VocabKidRepository,
    private val firebaseAuth: FirebaseAuth
) : ViewModel() {
    var isLoading by mutableStateOf(false)
        private set

    var errorMessage by mutableStateOf<String?>(null)
        private set

    var successMessage by mutableStateOf<String?>(null)
        private set

    fun login(
        email: String,
        password: String,
        onSuccess: () -> Unit
    ) {
        val cleanEmail = email.trim()
        successMessage = null
        if (!validateEmailPassword(cleanEmail, password)) return

        viewModelScope.launch {
            isLoading = true
            errorMessage = null
            successMessage = null
            try {
                val result = firebaseAuth.signInWithEmailAndPassword(
                    cleanEmail,
                    password
                ).awaitResult()
                ensureLocalStudent(result.user)
                onSuccess()
            } catch (exception: Exception) {
                errorMessage = friendlyAuthMessage(exception)
            } finally {
                isLoading = false
            }
        }
    }

    fun register(
        name: String,
        nis: String,
        email: String,
        password: String,
        grade: Int,
        onSuccess: () -> Unit
    ) {
        val cleanName = name.trim()
        val cleanEmail = email.trim()
        val cleanNis = nis.trim()
        successMessage = null

        if (cleanName.isBlank()) {
            errorMessage = "Nama siswa wajib diisi."
            return
        }
        if (!validateEmailPassword(cleanEmail, password)) return

        viewModelScope.launch {
            isLoading = true
            errorMessage = null
            successMessage = null
            try {
                val result = firebaseAuth.createUserWithEmailAndPassword(
                    cleanEmail,
                    password
                ).awaitResult()

                val profileRequest = UserProfileChangeRequest.Builder()
                    .setDisplayName(cleanName)
                    .build()
                result.user?.updateProfile(profileRequest)?.awaitCompletion()

                repository.saveStudent(
                    name = cleanName,
                    grade = grade,
                    nis = cleanNis
                )
                onSuccess()
            } catch (exception: Exception) {
                errorMessage = friendlyAuthMessage(exception)
            } finally {
                isLoading = false
            }
        }
    }

    fun resetPassword(email: String) {
        val cleanEmail = email.trim()
        successMessage = null
        if (!validateEmail(cleanEmail)) return

        viewModelScope.launch {
            isLoading = true
            errorMessage = null
            try {
                firebaseAuth.setLanguageCode("id")
                firebaseAuth.sendPasswordResetEmail(cleanEmail).awaitCompletion()
                successMessage = "Link reset password sudah dikirim ke $cleanEmail. Cek inbox atau folder spam."
            } catch (exception: Exception) {
                errorMessage = friendlyPasswordResetMessage(exception)
            } finally {
                isLoading = false
            }
        }
    }

    private fun validateEmailPassword(
        email: String,
        password: String
    ): Boolean {
        if (!validateEmail(email)) return false
        if (password.length < 6) {
            errorMessage = "Password minimal 6 karakter."
            return false
        }
        errorMessage = null
        return true
    }

    private fun validateEmail(email: String): Boolean {
        if (email.isBlank()) {
            errorMessage = "Email wajib diisi."
            return false
        }
        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            errorMessage = "Format email belum benar."
            return false
        }
        errorMessage = null
        return true
    }

    private suspend fun ensureLocalStudent(user: FirebaseUser?) {
        if (repository.observeStudent().first() != null) return

        repository.saveStudent(
            name = defaultStudentName(user),
            grade = 3
        )
    }

    private fun defaultStudentName(user: FirebaseUser?): String {
        return user?.displayName
            ?.trim()
            ?.takeIf { it.isNotBlank() }
            ?: user?.email
                ?.substringBefore("@")
                ?.replaceFirstChar { char ->
                    if (char.isLowerCase()) char.titlecase() else char.toString()
                }
                ?.takeIf { it.isNotBlank() }
            ?: "Siswa VocabKid"
    }

    private fun friendlyAuthMessage(exception: Throwable): String {
        return when (exception) {
            is FirebaseNetworkException -> "Koneksi internet belum stabil. Coba lagi sebentar."
            is FirebaseAuthUserCollisionException -> "Email ini sudah terdaftar. Silakan masuk."
            is FirebaseAuthWeakPasswordException -> "Password terlalu lemah. Gunakan minimal 6 karakter."
            is FirebaseAuthInvalidUserException -> "Akun dengan email ini belum ditemukan."
            is FirebaseAuthInvalidCredentialsException -> "Email atau password belum cocok."
            else -> "Autentikasi belum berhasil. Coba lagi."
        }
    }

    private fun friendlyPasswordResetMessage(exception: Throwable): String {
        return when (exception) {
            is FirebaseNetworkException -> "Koneksi internet belum stabil. Coba lagi sebentar."
            is FirebaseAuthInvalidUserException -> "Email ini belum terdaftar."
            is FirebaseAuthInvalidCredentialsException -> "Format email belum benar."
            else -> "Email reset belum bisa dikirim. Coba lagi."
        }
    }
}

private suspend fun <T> Task<T>.awaitResult(): T {
    return suspendCancellableCoroutine { continuation ->
        addOnCompleteListener { task ->
            if (task.isSuccessful) {
                continuation.resume(task.result)
            } else {
                continuation.resumeWithException(
                    task.exception ?: IllegalStateException("Firebase task failed.")
                )
            }
        }
    }
}

private suspend fun Task<*>.awaitCompletion() {
    suspendCancellableCoroutine<Unit> { continuation ->
        addOnCompleteListener { task ->
            if (task.isSuccessful) {
                continuation.resume(Unit)
            } else {
                continuation.resumeWithException(
                    task.exception ?: IllegalStateException("Firebase task failed.")
                )
            }
        }
    }
}
