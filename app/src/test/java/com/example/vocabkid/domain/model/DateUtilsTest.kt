package com.example.vocabkid.domain.model

import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import java.util.Calendar

class DateUtilsTest {

    @Test
    fun `startOfDayMillis zeroes out hour, minute, second, and millisecond`() {
        val cal = Calendar.getInstance().apply {
            set(2026, Calendar.NOVEMBER, 8, 15, 42, 19)
            set(Calendar.MILLISECOND, 789)
        }
        val startOfDay = DateUtils.startOfDayMillis(cal.timeInMillis)
        val resultCal = Calendar.getInstance().apply { timeInMillis = startOfDay }

        assertEquals(2026, resultCal.get(Calendar.YEAR))
        assertEquals(Calendar.NOVEMBER, resultCal.get(Calendar.MONTH))
        assertEquals(8, resultCal.get(Calendar.DAY_OF_MONTH))
        assertEquals(0, resultCal.get(Calendar.HOUR_OF_DAY))
        assertEquals(0, resultCal.get(Calendar.MINUTE))
        assertEquals(0, resultCal.get(Calendar.SECOND))
        assertEquals(0, resultCal.get(Calendar.MILLISECOND))
    }

    @Test
    fun `addDays advances calendar days and normalizes to start of day`() {
        val cal = Calendar.getInstance().apply {
            set(2026, Calendar.JANUARY, 30, 18, 30, 0)
        }
        val base = DateUtils.startOfDayMillis(cal.timeInMillis)
        val plusThreeDays = DateUtils.addDays(base, 3)

        val resultCal = Calendar.getInstance().apply { timeInMillis = plusThreeDays }
        assertEquals(2026, resultCal.get(Calendar.YEAR))
        assertEquals(Calendar.FEBRUARY, resultCal.get(Calendar.MONTH))
        assertEquals(2, resultCal.get(Calendar.DAY_OF_MONTH))
        assertEquals(0, resultCal.get(Calendar.HOUR_OF_DAY))
    }

    @Test
    fun `formatDate returns dash for null and formatted date for valid timestamp`() {
        assertEquals("-", DateUtils.formatDate(null))

        val cal = Calendar.getInstance().apply {
            set(2026, Calendar.AUGUST, 17, 10, 0, 0)
        }
        val formatted = DateUtils.formatDate(cal.timeInMillis)
        assertTrue(formatted.contains("17"))
        assertTrue(formatted.contains("2026"))
    }
}

