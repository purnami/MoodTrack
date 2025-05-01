package com.example.moodtrack.data.repository

import android.util.Log
import com.example.moodtrack.core.utils.MoodPeriod
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

    fun getMoodsByCurrentUser(): Flow<List<MoodEntity>> = flow {
        val userId = userPreferences.getUserId()
        if (userId != null) {
            emitAll(moodDao.getMoodsByUser(userId))
        }
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
                        now.add(Calendar.DAY_OF_YEAR, -7)
                        now.set(Calendar.HOUR_OF_DAY, 0)
                        now.set(Calendar.MINUTE, 0)
                        now.set(Calendar.SECOND, 0)
                        now.set(Calendar.MILLISECOND, 0)
                        startMillis = now.timeInMillis
                    }

                    MoodPeriod.MONTHLY -> {
                        now.set(Calendar.DAY_OF_MONTH, 1)
                        now.add(Calendar.MONTH, -1)
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

    suspend fun getLatestMoodFromFirestore(): MoodEntity? {
        val userId = userPreferences.getUserId() ?: return null
        Log.d("Firestore", "Mencari mood untuk userId: $userId")
        return try {
            val querySnapshot = firestore.collection("moods")
                .whereEqualTo("userId", userId)
                .orderBy("timestamp", Query.Direction.DESCENDING)
                .limit(1)
                .get()
                .await()

            val document = querySnapshot.documents.firstOrNull()
            document?.toObject(MoodEntity::class.java)

        } catch (e: Exception) {
            Log.e("Firestore", "Gagal mengambil mood terakhir dari Firestore", e)
            null
        }
    }
}
