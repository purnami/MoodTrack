package com.example.moodtrack.core.workers

import android.content.Context
import android.util.Log
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.example.moodtrack.core.utils.MoodPeriod
import com.example.moodtrack.core.utils.NotificationHelper
import com.example.moodtrack.data.repository.MoodRepository
import com.example.moodtrack.data.repository.OpenAIRepository
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import kotlinx.coroutines.flow.first

@HiltWorker
class WeeklyMoodInsightWorker @AssistedInject constructor(
    @Assisted context: Context,
    @Assisted workerParams: WorkerParameters,
    private val moodRepository: MoodRepository,
    private val openAIRepository: OpenAIRepository
) : CoroutineWorker(context, workerParams) {

    override suspend fun doWork(): Result {
        return try {
            val moods = moodRepository.getMoodsByUserIdFromFirestore(MoodPeriod.WEEKLY).first()

            val insightResult = openAIRepository.getMoodInsightWeekly(moods).first()

            insightResult.onSuccess { insight ->
                NotificationHelper.showNotification(
                    context = applicationContext,
                    title = "Insight Mingguan Mood Kamu",
                    message = "Cek insight emosional kamu minggu ini!",
                    notificationId = 101
                )
            }.onFailure { throwable ->
                Log.e("WeeklyMoodInsightWorker", "Gagal membuat insight: ${throwable.message}")
            }

            Result.success()
        } catch (e: Exception) {
            Log.e("WeeklyMoodInsightWorker", "Error di Worker: ${e.message}")
            Result.retry()
        }
    }
}
