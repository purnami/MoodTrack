package com.example.moodtrack.data.local.dao

import androidx.room.*
import com.example.moodtrack.data.local.entity.MoodEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface MoodDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMood(mood: MoodEntity)

    @Query("SELECT * FROM moods ORDER BY timestamp DESC")
    fun getAllMoods(): Flow<List<MoodEntity>>

    @Query("SELECT * FROM moods WHERE timestamp BETWEEN :startTime AND :endTime ORDER BY timestamp ASC")
    fun getMoodsInRange(startTime: Long, endTime: Long): Flow<List<MoodEntity>>

    @Query("SELECT * FROM moods WHERE userId = :userId ORDER BY timestamp DESC")
    fun getMoodsByUser(userId: String): Flow<List<MoodEntity>>

    @Delete
    suspend fun deleteMood(mood: MoodEntity)

    @Query("DELETE FROM moods")
    suspend fun clearAllMoods()

    @Query("SELECT * FROM moods WHERE userId = :userId ORDER BY timestamp DESC LIMIT 1")
    suspend fun getLastMoodByUser(userId: String): MoodEntity?

}
