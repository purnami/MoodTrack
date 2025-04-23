package com.example.moodtrack.core.workers

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.core.content.ContextCompat.getSystemService
import androidx.work.Worker
import androidx.work.WorkerParameters
import com.example.moodtrack.MainActivity
import com.example.moodtrack.R
import io.ktor.network.selector.SelectInterest.Companion.flags

class MoodNotificationWorker(appContext: Context, workerParams: WorkerParameters) : Worker(appContext, workerParams) {
    override fun doWork(): Result {
        // Logika untuk menampilkan notifikasi
        sendNotification("Jangan lupa catat moodmu hari ini!")
        return Result.success()
    }

    private fun sendNotification(message: String) {
        Log.d("MoodNotification", "Sending notification: $message")
        val notificationManager = applicationContext.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        val channelId = "mood_reminder_channel"
        val channelName = "Mood Reminders"

        // Intent ke MainActivity (atau yang kamu mau tuju)
        val intent = Intent(applicationContext, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }
        val pendingIntent = PendingIntent.getActivity(
            applicationContext,
            0,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE // penting untuk Android 12+
        )

        val builder = NotificationCompat.Builder(applicationContext, channelId)
            .setContentTitle("Pengingat Mood")
            .setSmallIcon(R.drawable.ic_notification)
            .setContentText(message)
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setContentIntent(pendingIntent) // <- ini yang bikin notifikasi bisa di-tap
            .setAutoCancel(true) // supaya notifikasi hilang setelah ditekan

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                channelId,
                channelName,
                NotificationManager.IMPORTANCE_DEFAULT
            )
            builder.setChannelId(channelId)
            notificationManager.createNotificationChannel(channel)
        }

        val notification = builder.build()
        notificationManager.notify(0, notification)
    }


//    private fun sendNotification(message: String) {
//        Log.d("MoodNotification", "Sending notification: $message")
//        val notificationManager = applicationContext.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
//        val channelId = "mood_reminder_channel"
//        val channelName = "Mood Reminders"
//        val builder = NotificationCompat.Builder(applicationContext, channelId)
//            .setContentTitle("Pengingat Mood")
//            .setSmallIcon(R.drawable.ic_notification)
//            .setContentText(message)
//            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
////            .setSubText(getString(R.string.notification_subtext))
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
//            val channel = NotificationChannel(
//                channelId,
//                channelName,
//                NotificationManager.IMPORTANCE_DEFAULT
//            )
//            builder.setChannelId(channelId)
//            notificationManager.createNotificationChannel(channel)
//        }
//
//        val notification = builder.build()
//        notificationManager.notify(0, notification)
//    }

//    private fun sendNotification(message: String) {
//        Log.d("MoodNotification", "Sending notification: $message")
//        val notificationManager = applicationContext.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
//
//        // Membuat channel notifikasi jika belum ada
//        val channelId = "mood_reminder_channel"
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
//            val channel = NotificationChannel(
//                channelId,
//                "Mood Reminders",
//                NotificationManager.IMPORTANCE_DEFAULT
//            )
//            notificationManager.createNotificationChannel(channel)
//        }
//
//        // Membuat notifikasi
//        val notification = NotificationCompat.Builder(applicationContext, channelId)
//            .setSmallIcon(R.drawable.ic_notification) // Sesuaikan dengan ikon notifikasi
//            .setContentTitle("Pengingat Mood")
//            .setContentText(message)
//            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
//            .build()
//
//        notificationManager.notify(0, notification) // Menampilkan notifikasi
//    }
}
