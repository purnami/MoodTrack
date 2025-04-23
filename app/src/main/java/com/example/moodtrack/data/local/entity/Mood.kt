package com.example.moodtrack.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "moods")
data class MoodEntity(
    @PrimaryKey
    val id: String = "",
    val userId: String = "",
    val mood: Int = 0,
    val note: String? = "",
    val timestamp: Long = 0L
)