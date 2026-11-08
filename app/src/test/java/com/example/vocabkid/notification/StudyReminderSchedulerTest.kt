package com.example.vocabkid.notification

import org.junit.Assert.assertTrue
import org.junit.Test
import java.util.Calendar
import java.util.concurrent.TimeUnit

class StudyReminderSchedulerTest {

    @Test
    fun `computeInitialDelayMillis schedules for today when target hour is in the future`() {
        val nowCal = Calendar.getInstance().apply {
            set(2026, Calendar.NOVEMBER, 8, 10, 0, 0)
            set(Calendar.MILLISECOND, 0)
        }
        val delayMillis = StudyReminderScheduler.computeInitialDelayMillis(
            nowMillis = nowCal.timeInMillis,
            targetHourOfDay = 16,
            targetMinute = 0
        )

        val expectedMillis = TimeUnit.HOURS.toMillis(6)
        assertTrue(delayMillis in (expectedMillis - 1000)..(expectedMillis + 1000))
    }

    @Test
    fun `computeInitialDelayMillis schedules for next day when target hour has already passed`() {
        val nowCal = Calendar.getInstance().apply {
            set(2026, Calendar.NOVEMBER, 8, 18, 0, 0)
            set(Calendar.MILLISECOND, 0)
        }
        val delayMillis = StudyReminderScheduler.computeInitialDelayMillis(
            nowMillis = nowCal.timeInMillis,
            targetHourOfDay = 16,
            targetMinute = 0
        )

        val expectedMillis = TimeUnit.HOURS.toMillis(22)
        assertTrue(delayMillis in (expectedMillis - 1000)..(expectedMillis + 1000))
    }
}

