package com.example.moodtrack.data.repository

import android.content.Context
import android.util.Log
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import com.example.moodtrack.core.utils.MoodPeriod
import com.example.moodtrack.core.workers.MoodNotificationWorker
import com.example.moodtrack.data.local.dao.MoodDao
import com.example.moodtrack.data.local.entity.MoodEntity
import com.example.moodtrack.data.local.preferences.UserPreferences
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.emitAll
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.tasks.await
import java.util.Calendar
import java.util.UUID
import java.util.concurrent.TimeUnit
import javax.inject.Inject

class MoodRepository @Inject constructor(
    private val moodDao: MoodDao,
    private val firestore: FirebaseFirestore,
    private val userPreferences: UserPreferences
) {

    suspend fun insertMood(mood: Int, note: String?) {
        val userId = userPreferences.getUserId() ?: return
        val moodEntity = MoodEntity(
            id = UUID.randomUUID().toString(),
            userId = userId,
            mood = mood,
            note = note,
            timestamp = System.currentTimeMillis()
        )
        moodDao.insertMood(moodEntity)
        saveMoodToFirestore(moodEntity)
    }

    fun getAllMoods(): Flow<List<MoodEntity>> = moodDao.getAllMoods()

    fun getMoodsByCurrentUser(): Flow<List<MoodEntity>> = flow {
        val userId = userPreferences.getUserId()
        if (userId != null) {
            emitAll(moodDao.getMoodsByUser(userId))
        }
    }

    fun getMoodsInRange(startTime: Long, endTime: Long): Flow<List<MoodEntity>> =
        moodDao.getMoodsInRange(startTime, endTime)

    suspend fun deleteMood(mood: MoodEntity) {
        moodDao.deleteMood(mood)
    }

    suspend fun clearAllMoods() {
        moodDao.clearAllMoods()
    }

    private suspend fun saveMoodToFirestore(mood: MoodEntity) {
        try {
            firestore.collection("moods")
                .document(mood.id)
                .set(mood)
                .await()
            Log.d("Firestore", "Mood berhasil disimpan")
        } catch (e: Exception) {
            Log.e("Firestore", "Gagal menyimpan mood", e)
        }
    }

    suspend fun getLastMoodByCurrentUser(): MoodEntity? {
        val userId = userPreferences.getUserId() ?: return null
        return moodDao.getLastMoodByUser(userId)
    }

    fun getMoodsByUserIdFromFirestore(period: MoodPeriod): Flow<List<MoodEntity>> = flow {
        val userId = userPreferences.getUserId()
        Log.d("Firestore", "User ID: $userId")

        if (userId != null) {
            try {
                val now = Calendar.getInstance()

                val startMillis: Long
                val endMillis: Long = now.timeInMillis

                when (period) {
                    MoodPeriod.DAILY -> {
                        now.set(Calendar.HOUR_OF_DAY, 0)
                        now.set(Calendar.MINUTE, 0)
                        now.set(Calendar.SECOND, 0)
                        now.set(Calendar.MILLISECOND, 0)
                        startMillis = now.timeInMillis
                    }

                    MoodPeriod.WEEKLY -> {
                        now.set(Calendar.DAY_OF_WEEK, now.firstDayOfWeek)
                        now.set(Calendar.HOUR_OF_DAY, 0)
                        now.set(Calendar.MINUTE, 0)
                        now.set(Calendar.SECOND, 0)
                        now.set(Calendar.MILLISECOND, 0)
                        startMillis = now.timeInMillis
                    }

                    MoodPeriod.MONTHLY -> {
                        now.set(Calendar.DAY_OF_MONTH, 1)
                        now.set(Calendar.HOUR_OF_DAY, 0)
                        now.set(Calendar.MINUTE, 0)
                        now.set(Calendar.SECOND, 0)
                        now.set(Calendar.MILLISECOND, 0)
                        startMillis = now.timeInMillis
                    }
                }

                val querySnapshot = firestore.collection("moods")
                    .whereEqualTo("userId", userId)
                    .whereGreaterThanOrEqualTo("timestamp", startMillis)
                    .whereLessThan("timestamp", endMillis)
                    .orderBy("timestamp", Query.Direction.ASCENDING)
                    .get()
                    .await()

                val moods = querySnapshot.documents.mapNotNull { document ->
                    document.toObject(MoodEntity::class.java)
                }

                emit(moods)

            } catch (e: Exception) {
                Log.e("Firestore", "Gagal mengambil mood dari Firestore", e)
                emit(emptyList())
            }
        } else {
            emit(emptyList())
        }
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
}
