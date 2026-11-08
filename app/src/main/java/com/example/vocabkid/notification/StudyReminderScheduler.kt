package com.example.vocabkid.notification

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import androidx.work.Constraints
import androidx.work.Data
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.ExistingWorkPolicy
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import java.util.Calendar
import java.util.concurrent.TimeUnit

/**
 * Manages scheduling and preferences for the daily Spaced Repetition study reminder.
 */
object StudyReminderScheduler {
    const val CHANNEL_ID = "vocabkid_daily_reminder"
    private const val CHANNEL_NAME = "Pengingat Belajar Harian"
    private const val CHANNEL_DESCRIPTION =
        "Mengingatkan siswa ketika ada kosakata yang jatuh tempo untuk diulang (Spaced Repetition)."

    private const val UNIQUE_PERIODIC_WORK_NAME = "vocabkid_daily_study_reminder_work"
    private const val UNIQUE_IMMEDIATE_WORK_NAME = "vocabkid_immediate_study_reminder_work"

    private const val PREFS_NAME = "vocabkid_reminder_prefs"
    private const val KEY_REMINDER_ENABLED = "reminder_enabled"
    private const val KEY_REMINDER_HOUR = "reminder_hour"
    private const val DEFAULT_REMINDER_HOUR = 16 // 16:00 (4 PM after school)

    fun ensureNotificationChannel(context: Context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val manager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            val existing = manager.getNotificationChannel(CHANNEL_ID)
            if (existing == null) {
                val channel = NotificationChannel(
                    CHANNEL_ID,
                    CHANNEL_NAME,
                    NotificationManager.IMPORTANCE_DEFAULT
                ).apply {
                    description = CHANNEL_DESCRIPTION
                }
                manager.createNotificationChannel(channel)
            }
        }
    }

    fun isReminderEnabled(context: Context): Boolean {
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        return prefs.getBoolean(KEY_REMINDER_ENABLED, true)
    }

    fun getReminderHour(context: Context): Int {
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        return prefs.getInt(KEY_REMINDER_HOUR, DEFAULT_REMINDER_HOUR).coerceIn(0, 23)
    }

    fun setReminderEnabled(context: Context, enabled: Boolean) {
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        prefs.edit().putBoolean(KEY_REMINDER_ENABLED, enabled).apply()
        if (enabled) {
            scheduleDailyReminder(context, replaceExisting = true)
        } else {
            cancelDailyReminder(context)
        }
    }

    fun scheduleDailyReminder(
        context: Context,
        replaceExisting: Boolean = false
    ) {
        ensureNotificationChannel(context)
        if (!isReminderEnabled(context)) {
            cancelDailyReminder(context)
            return
        }

        val targetHour = getReminderHour(context)
        val initialDelayMillis = computeInitialDelayMillis(
            nowMillis = System.currentTimeMillis(),
            targetHourOfDay = targetHour
        )

        val constraints = Constraints.Builder()
            .setRequiresBatteryNotLow(true)
            .build()

        val periodicRequest = PeriodicWorkRequestBuilder<DailyStudyReminderWorker>(
            24,
            TimeUnit.HOURS
        )
            .setInitialDelay(initialDelayMillis, TimeUnit.MILLISECONDS)
            .setConstraints(constraints)
            .build()

        val policy = if (replaceExisting) {
            ExistingPeriodicWorkPolicy.UPDATE
        } else {
            ExistingPeriodicWorkPolicy.KEEP
        }

        WorkManager.getInstance(context).enqueueUniquePeriodicWork(
            UNIQUE_PERIODIC_WORK_NAME,
            policy,
            periodicRequest
        )
    }

    fun triggerImmediateCheck(context: Context, forceNotify: Boolean = true) {
        ensureNotificationChannel(context)
        val inputData = Data.Builder()
            .putBoolean(DailyStudyReminderWorker.KEY_FORCE_NOTIFY, forceNotify)
            .build()

        val request = OneTimeWorkRequestBuilder<DailyStudyReminderWorker>()
            .setInputData(inputData)
            .build()

        WorkManager.getInstance(context).enqueueUniqueWork(
            UNIQUE_IMMEDIATE_WORK_NAME,
            ExistingWorkPolicy.REPLACE,
            request
        )
    }

    fun cancelDailyReminder(context: Context) {
        WorkManager.getInstance(context).cancelUniqueWork(UNIQUE_PERIODIC_WORK_NAME)
    }

    internal fun computeInitialDelayMillis(
        nowMillis: Long,
        targetHourOfDay: Int,
        targetMinute: Int = 0
    ): Long {
        val now = Calendar.getInstance().apply { timeInMillis = nowMillis }
        val target = Calendar.getInstance().apply {
            timeInMillis = nowMillis
            set(Calendar.HOUR_OF_DAY, targetHourOfDay.coerceIn(0, 23))
            set(Calendar.MINUTE, targetMinute.coerceIn(0, 59))
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
        }
        if (!target.after(now)) {
            target.add(Calendar.DAY_OF_YEAR, 1)
        }
        return (target.timeInMillis - now.timeInMillis).coerceAtLeast(60_000L)
    }
}

