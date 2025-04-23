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
//import kotlin.coroutines.jvm.internal.CompletedContinuation.context

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

//    fun getMoodsByUserIdFromFirestore(period: MoodPeriod): Flow<List<MoodEntity>> = flow {
//        val userId = userPreferences.getUserId()
//        Log.d("Firestore", "User ID: $userId")
//
//        if (userId != null) {
////            val calendar = Calendar.getInstance()
//
//            val startOfDayMillis = Calendar.getInstance().apply {
//                set(Calendar.HOUR_OF_DAY, 0)
//                set(Calendar.MINUTE, 0)
//                set(Calendar.SECOND, 0)
//                set(Calendar.MILLISECOND, 0)
//            }.timeInMillis
//
//            val endOfDayMillis = Calendar.getInstance().apply {
//                set(Calendar.HOUR_OF_DAY, 23)
//                set(Calendar.MINUTE, 59)
//                set(Calendar.SECOND, 59)
//                set(Calendar.MILLISECOND, 999)
//            }.timeInMillis
//
//            try {
//                val querySnapshot = firestore.collection("moods")
//                    .whereEqualTo("userId", userId)
//                    .whereGreaterThanOrEqualTo("timestamp", startOfDayMillis)
//                    .whereLessThan("timestamp", endOfDayMillis)
//                    .orderBy("timestamp", Query.Direction.ASCENDING)
//                    .get()
//                    .await()
//
//                Log.d("Firestore", "Query snapshot: $querySnapshot")
//
//                val moods = querySnapshot.documents.mapNotNull { document ->
//                    document.toObject(MoodEntity::class.java)
//                }
//
//                Log.d("Firestore", "Fetched moods: $moods")
//
//                emit(moods)
//            } catch (e: Exception) {
//                Log.e("Firestore", "Gagal mengambil mood dari Firestore", e)
//                emit(emptyList<MoodEntity>())
//            }
//        } else {
//            emit(emptyList<MoodEntity>())
//        }
//    }

    fun getMoodsByUserIdFromFirestore(period: MoodPeriod): Flow<List<MoodEntity>> = flow {
        val userId = userPreferences.getUserId()
        Log.d("Firestore", "User ID: $userId")

        if (userId != null) {
            try {
                val now = Calendar.getInstance()

                // Hitung timestamp range berdasarkan periode
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
                        now.set(Calendar.DAY_OF_WEEK, now.firstDayOfWeek) // Awal minggu (biasanya Minggu/Senin)
                        now.set(Calendar.HOUR_OF_DAY, 0)
                        now.set(Calendar.MINUTE, 0)
                        now.set(Calendar.SECOND, 0)
                        now.set(Calendar.MILLISECOND, 0)
                        startMillis = now.timeInMillis
                    }

                    MoodPeriod.MONTHLY -> {
                        now.set(Calendar.DAY_OF_MONTH, 1) // Awal bulan
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

        // Jam 9 Pagi
        calendar.set(Calendar.HOUR_OF_DAY, 9)
        calendar.set(Calendar.MINUTE, 0)
        val morningReminder = OneTimeWorkRequestBuilder<MoodNotificationWorker>()
            .setInitialDelay(getInitialDelay(calendar), TimeUnit.MILLISECONDS)
            .build()

        // Jam 3 Sore
        calendar.set(Calendar.HOUR_OF_DAY, 16)
        calendar.set(Calendar.MINUTE, 18)
        val afternoonReminder = OneTimeWorkRequestBuilder<MoodNotificationWorker>()
            .setInitialDelay(getInitialDelay(calendar), TimeUnit.MILLISECONDS)
            .build()

        // Jam 9 Malam
        calendar.set(Calendar.HOUR_OF_DAY, 16)
        calendar.set(Calendar.MINUTE, 19)
        val eveningReminder = OneTimeWorkRequestBuilder<MoodNotificationWorker>()
            .setInitialDelay(getInitialDelay(calendar), TimeUnit.MILLISECONDS)
            .build()

        // Menjadwalkan WorkManager
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
            targetTime - currentTime // Waktu yang tersisa hingga jadwal berikutnya
        } else {
            targetTime + TimeUnit.DAYS.toMillis(1) - currentTime // Jika waktu sudah lewat, set untuk hari berikutnya
        }
    }
}
