package com.example.vocabkid.domain.algorithm

import com.example.vocabkid.data.local.entity.WordProgressEntity
import com.example.vocabkid.domain.model.DateUtils
import com.example.vocabkid.domain.model.WordStatus
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class SpacedRepetitionAlgorithmTest {

    private val baseToday = DateUtils.startOfDayMillis(1_700_000_000_000L)

    private fun createInitialProgress(wordId: Long = 1L) = WordProgressEntity(
        id = 1L,
        wordId = wordId,
        repetition = 0,
        intervalDays = 0,
        easeFactor = 2.5,
        dueDate = baseToday,
        lastReviewedDate = null,
        correctCount = 0,
        wrongCount = 0,
        status = WordStatus.NEW
    )

    @Test
    fun `first correct review sets repetition to 1, interval to 1 day, and status to LEARNING`() {
        val initial = createInitialProgress()
        val updated = SpacedRepetitionAlgorithm.calculateNextReview(
            current = initial,
            quality = 4,
            todayMillis = baseToday
        )

        assertEquals(1, updated.repetition)
        assertEquals(1, updated.intervalDays)
        assertEquals(1, updated.correctCount)
        assertEquals(0, updated.wrongCount)
        assertEquals(WordStatus.LEARNING, updated.status)
        assertEquals(DateUtils.addDays(baseToday, 1), updated.dueDate)
    }

    @Test
    fun `second consecutive correct review sets interval to 3 days`() {
        val first = SpacedRepetitionAlgorithm.calculateNextReview(
            current = createInitialProgress(),
            quality = 5,
            todayMillis = baseToday
        )
        val day2 = DateUtils.addDays(baseToday, 1)
        val second = SpacedRepetitionAlgorithm.calculateNextReview(
            current = first,
            quality = 5,
            todayMillis = day2
        )

        assertEquals(2, second.repetition)
        assertEquals(3, second.intervalDays)
        assertEquals(2, second.correctCount)
        assertEquals(WordStatus.LEARNING, second.status)
        assertEquals(DateUtils.addDays(day2, 3), second.dueDate)
    }

    @Test
    fun `four consecutive correct reviews transition status to MASTERED`() {
        var progress = createInitialProgress()
        var currentDay = baseToday

        repeat(4) {
            progress = SpacedRepetitionAlgorithm.calculateNextReview(
                current = progress,
                quality = 5,
                todayMillis = currentDay
            )
            currentDay = progress.dueDate
        }

        assertEquals(4, progress.repetition)
        assertEquals(4, progress.correctCount)
        assertEquals(WordStatus.MASTERED, progress.status)
        assertTrue(progress.intervalDays > 3)
    }

    @Test
    fun `wrong review resets repetition to 0, sets interval to 1 day, and marks FREQUENTLY_WRONG`() {
        val learnedProgress = createInitialProgress().copy(
            repetition = 3,
            intervalDays = 8,
            correctCount = 3,
            status = WordStatus.LEARNING
        )

        val afterWrong = SpacedRepetitionAlgorithm.calculateNextReview(
            current = learnedProgress,
            quality = 1,
            todayMillis = baseToday
        )

        assertEquals(0, afterWrong.repetition)
        assertEquals(1, afterWrong.intervalDays)
        assertEquals(1, afterWrong.wrongCount)
        assertEquals(3, afterWrong.correctCount)
        assertEquals(WordStatus.FREQUENTLY_WRONG, afterWrong.status)
        assertEquals(DateUtils.addDays(baseToday, 1), afterWrong.dueDate)
    }

    @Test
    fun `ease factor never drops below 1_3 even after repeated low quality reviews`() {
        var progress = createInitialProgress()
        repeat(15) {
            progress = SpacedRepetitionAlgorithm.calculateNextReview(
                current = progress,
                quality = 0,
                todayMillis = baseToday
            )
        }

        assertTrue("Ease factor should be at least 1.3", progress.easeFactor >= 1.3)
        assertEquals(1.3, progress.easeFactor, 0.0001)
    }

    @Test
    fun `quality input out of bounds is safely clamped between 0 and 5`() {
        val initial = createInitialProgress()
        val highClamped = SpacedRepetitionAlgorithm.calculateNextReview(
            current = initial,
            quality = 99,
            todayMillis = baseToday
        )
        val maxNormal = SpacedRepetitionAlgorithm.calculateNextReview(
            current = initial,
            quality = 5,
            todayMillis = baseToday
        )

        assertEquals(maxNormal.easeFactor, highClamped.easeFactor, 0.0001)
        assertEquals(maxNormal.intervalDays, highClamped.intervalDays)

        val lowClamped = SpacedRepetitionAlgorithm.calculateNextReview(
            current = initial,
            quality = -10,
            todayMillis = baseToday
        )
        val minNormal = SpacedRepetitionAlgorithm.calculateNextReview(
            current = initial,
            quality = 0,
            todayMillis = baseToday
        )

        assertEquals(minNormal.easeFactor, lowClamped.easeFactor, 0.0001)
        assertEquals(WordStatus.FREQUENTLY_WRONG, lowClamped.status)
    }

    @Test
    fun `updateProgressAfterReview records lastReviewedDate`() {
        val initial = createInitialProgress()
        val updated = SpacedRepetitionAlgorithm.updateProgressAfterReview(
            current = initial,
            quality = 4,
            todayMillis = baseToday
        )

        assertEquals(baseToday, updated.lastReviewedDate)
    }
}

