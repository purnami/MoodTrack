package com.example.moodtrack.core.workers

import android.content.Context
import androidx.work.Worker
import androidx.work.WorkerParameters
import com.example.moodtrack.core.utils.NotificationHelper

class MoodNotificationWorker(
    appContext: Context,
    workerParams: WorkerParameters
) : Worker(appContext, workerParams) {
    override fun doWork(): Result {
        NotificationHelper.showNotification(
            context = applicationContext,
            title = "Pengingat Mood",
            message = "Yuk catat suasana hatimu hari ini!",
            notificationId = 100
        )
        return Result.success()
    }
}
