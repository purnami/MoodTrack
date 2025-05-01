package com.example.moodtrack.core.workers

import android.content.Context
import android.util.Log
import androidx.work.*
import java.util.Calendar
import java.util.concurrent.TimeUnit

fun scheduleWeeklyMoodInsightWorker(context: Context) {
    val workRequest = PeriodicWorkRequestBuilder<WeeklyMoodInsightWorker>(
        7, TimeUnit.DAYS
    )
        .setInitialDelay(1, TimeUnit.MINUTES)
        .addTag("weekly_mood_insight_worker")
        .build()

    WorkManager.getInstance(context)
        .enqueueUniquePeriodicWork(
            "WeeklyMoodInsightWork",
            ExistingPeriodicWorkPolicy.KEEP,
            workRequest
        )
}

fun scheduleMoodNotifications(context: Context) {
    Log.d("MoodNotification", "Scheduling mood notifications")
    val calendar = Calendar.getInstance()

    calendar.set(Calendar.HOUR_OF_DAY, 9)
    calendar.set(Calendar.MINUTE, 0)
    val morningReminder = OneTimeWorkRequestBuilder<MoodNotificationWorker>()
        .setInitialDelay(getInitialDelay(calendar), TimeUnit.MILLISECONDS)
        .build()

    calendar.set(Calendar.HOUR_OF_DAY, 15)
    calendar.set(Calendar.MINUTE, 0)
    val afternoonReminder = OneTimeWorkRequestBuilder<MoodNotificationWorker>()
        .setInitialDelay(getInitialDelay(calendar), TimeUnit.MILLISECONDS)
        .build()

    calendar.set(Calendar.HOUR_OF_DAY, 21)
    calendar.set(Calendar.MINUTE, 0)
    val eveningReminder = OneTimeWorkRequestBuilder<MoodNotificationWorker>()
        .setInitialDelay(getInitialDelay(calendar), TimeUnit.MILLISECONDS)
        .build()

    WorkManager.getInstance(context.applicationContext).apply {
        enqueue(morningReminder)
        enqueue(afternoonReminder)
        enqueue(eveningReminder)
    }

    Log.d("MoodNotification", "Scheduled notifications at 9 AM, 3 PM, and 9 PM.")
}

private fun getInitialDelay(calendar: Calendar): Long {
    val currentTime = System.currentTimeMillis()
    val targetTime = calendar.timeInMillis

    Log.d("MoodNotification", "Current time: $currentTime, Target time: $targetTime")
    return if (targetTime > currentTime) {
        targetTime - currentTime
    } else {
        targetTime + TimeUnit.DAYS.toMillis(1) - currentTime
    }
}