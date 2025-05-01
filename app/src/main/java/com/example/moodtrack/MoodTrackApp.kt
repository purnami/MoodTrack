package com.example.moodtrack

import android.app.Application
import com.example.moodtrack.core.workers.scheduleMoodNotifications
import com.example.moodtrack.core.workers.scheduleWeeklyMoodInsightWorker
import com.google.firebase.FirebaseApp
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class MoodTrackApp : Application() {
    override fun onCreate() {
        super.onCreate()
        FirebaseApp.initializeApp(this)
        scheduleWeeklyMoodInsightWorker(this)
        scheduleMoodNotifications(this)
    }
}
