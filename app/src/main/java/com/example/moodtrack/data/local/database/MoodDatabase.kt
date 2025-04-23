package com.example.moodtrack.data.local.database

import androidx.room.Database
import androidx.room.RoomDatabase
import com.example.moodtrack.data.local.dao.MoodDao
import com.example.moodtrack.data.local.entity.MoodEntity

@Database(entities = [MoodEntity::class], version = 1, exportSchema = false)
abstract class MoodDatabase : RoomDatabase() {
    abstract fun moodDao(): MoodDao
}
