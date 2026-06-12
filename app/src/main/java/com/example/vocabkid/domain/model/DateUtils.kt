package com.example.vocabkid.domain.model

import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

object DateUtils {
    fun todayMillis(): Long = startOfDayMillis(System.currentTimeMillis())

    fun startOfDayMillis(timeMillis: Long): Long {
        val calendar = Calendar.getInstance()
        calendar.timeInMillis = timeMillis
        calendar.set(Calendar.HOUR_OF_DAY, 0)
        calendar.set(Calendar.MINUTE, 0)
        calendar.set(Calendar.SECOND, 0)
        calendar.set(Calendar.MILLISECOND, 0)
        return calendar.timeInMillis
    }

    fun addDays(timeMillis: Long, days: Int): Long {
        val calendar = Calendar.getInstance()
        calendar.timeInMillis = timeMillis
        calendar.add(Calendar.DAY_OF_YEAR, days)
        return startOfDayMillis(calendar.timeInMillis)
    }

    fun formatDate(timeMillis: Long?): String {
        if (timeMillis == null) return "-"
        val formatter = SimpleDateFormat("dd MMM yyyy", Locale("id", "ID"))
        return formatter.format(Date(timeMillis))
    }
}
