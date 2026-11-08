package com.example.vocabkid.notification

import android.Manifest
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.example.vocabkid.MainActivity
import com.example.vocabkid.R
import com.example.vocabkid.data.local.database.VocabKidDatabase
import com.example.vocabkid.data.repository.VocabKidRepository
import com.example.vocabkid.domain.model.DateUtils
import kotlinx.coroutines.flow.firstOrNull

/**
 * Background worker that checks if the student has vocabulary words due for review today
 * according to the SM-2 Spaced Repetition schedule and posts a kid-friendly notification.
 */
class DailyStudyReminderWorker(
    appContext: Context,
    workerParams: WorkerParameters
) : CoroutineWorker(appContext, workerParams) {

    override suspend fun doWork(): Result {
        return try {
            val forceNotify = inputData.getBoolean(KEY_FORCE_NOTIFY, false)
            if (!forceNotify && !StudyReminderScheduler.isReminderEnabled(applicationContext)) {
                return Result.success()
            }

            val database = VocabKidDatabase.getDatabase(applicationContext)
            val repository = VocabKidRepository(database, applicationContext)

            val dueToday = repository.getDueWordCount(DateUtils.todayMillis())
            if (dueToday <= 0 && !forceNotify) {
                return Result.success()
            }

            val student = repository.observeStudent().firstOrNull()
            val studentName = student?.name?.takeIf { it.isNotBlank() } ?: "Teman Belajar"

            showReminderNotification(
                context = applicationContext,
                studentName = studentName,
                dueCount = dueToday
            )

            Result.success()
        } catch (e: Exception) {
            Result.retry()
        }
    }

    companion object {
        const val KEY_FORCE_NOTIFY = "force_notify"
        private const val NOTIFICATION_ID = 1001

        fun showReminderNotification(
            context: Context,
            studentName: String,
            dueCount: Int
        ): Boolean {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                val granted = ContextCompat.checkSelfPermission(
                    context,
                    Manifest.permission.POST_NOTIFICATIONS
                ) == PackageManager.PERMISSION_GRANTED
                if (!granted) return false
            }

            StudyReminderScheduler.ensureNotificationChannel(context)

            val openAppIntent = Intent(context, MainActivity::class.java).apply {
                flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            }
            val pendingIntent = PendingIntent.getActivity(
                context,
                0,
                openAppIntent,
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
            )

            val title = if (dueCount > 0) {
                "Waktunya Belajar, $studentName! 🌟"
            } else {
                "Hebat, $studentName! 🎉"
            }

            val body = if (dueCount > 0) {
                "Ada $dueCount kosakata yang siap diulang hari ini. Yuk latihan sebentar agar makin pintar!"
            } else {
                "Semua jadwal kosakata hari ini sudah tuntas! Yuk main kuis atau baca cerita seru di VocabKid."
            }

            val notification = NotificationCompat.Builder(
                context,
                StudyReminderScheduler.CHANNEL_ID
            )
                .setSmallIcon(R.drawable.ic_launcher)
                .setContentTitle(title)
                .setContentText(body)
                .setStyle(NotificationCompat.BigTextStyle().bigText(body))
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .setContentIntent(pendingIntent)
                .setAutoCancel(true)
                .build()

            NotificationManagerCompat.from(context).notify(NOTIFICATION_ID, notification)
            return true
        }
    }
}

