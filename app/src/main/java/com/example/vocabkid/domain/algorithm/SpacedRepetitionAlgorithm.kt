package com.example.vocabkid.domain.algorithm

import com.example.vocabkid.data.local.entity.WordProgressEntity
import com.example.vocabkid.domain.model.DateUtils
import com.example.vocabkid.domain.model.WordStatus
import kotlin.math.max
import kotlin.math.roundToInt

object SpacedRepetitionAlgorithm {
    /**
     * Menghitung jadwal pengulangan berikutnya dengan SM-2 sederhana.
     * Input quality berasal dari tombol flashcard atau hasil kuis, lalu digunakan
     * untuk memperbarui repetition, intervalDays, easeFactor, dueDate, dan status.
     */
    fun calculateNextReview(
        current: WordProgressEntity,
        quality: Int,
        todayMillis: Long = DateUtils.todayMillis()
    ): WordProgressEntity {
        val safeQuality = quality.coerceIn(0, 5)
        val newEaseFactor = calculateEaseFactor(current.easeFactor, safeQuality)

        val reviewedProgress = if (safeQuality < 3) {
            current.copy(
                repetition = 0,
                intervalDays = 1,
                easeFactor = newEaseFactor,
                dueDate = DateUtils.addDays(todayMillis, 1),
                wrongCount = current.wrongCount + 1,
                status = WordStatus.FREQUENTLY_WRONG
            )
        } else {
            val newRepetition = current.repetition + 1
            val newCorrectCount = current.correctCount + 1
            val newInterval = when (newRepetition) {
                1 -> 1
                2 -> 3
                else -> max(1, (current.intervalDays * newEaseFactor).roundToInt())
            }
            val newStatus = when {
                newRepetition > 3 && newCorrectCount > current.wrongCount -> WordStatus.MASTERED
                newRepetition >= 1 -> WordStatus.LEARNING
                else -> WordStatus.NEW
            }

            current.copy(
                repetition = newRepetition,
                intervalDays = newInterval,
                easeFactor = newEaseFactor,
                dueDate = DateUtils.addDays(todayMillis, newInterval),
                correctCount = newCorrectCount,
                status = newStatus
            )
        }

        return reviewedProgress
    }

    /**
     * Fungsi utama yang dipanggil repository setelah siswa melakukan review.
     * Fungsi ini menambahkan lastReviewedDate agar dosen/penguji dapat melihat
     * kapan progress terakhir diperbarui, sementara perhitungan jadwal tetap
     * dikerjakan oleh calculateNextReview().
     */
    fun updateProgressAfterReview(
        current: WordProgressEntity,
        quality: Int,
        todayMillis: Long = DateUtils.todayMillis()
    ): WordProgressEntity {
        return calculateNextReview(
            current = current,
            quality = quality,
            todayMillis = todayMillis
        ).copy(lastReviewedDate = todayMillis)
    }

    private fun calculateEaseFactor(currentEaseFactor: Double, quality: Int): Double {
        val difference = 5 - quality
        val updatedEaseFactor = currentEaseFactor + (
            0.1 - difference * (0.08 + difference * 0.02)
            )
        return max(1.3, updatedEaseFactor)
    }
}
