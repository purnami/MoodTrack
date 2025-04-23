package com.example.moodtrack.core.utils

val moodEmojis = listOf("😡", "😢", "😐", "😊", "😄")
val moodDescriptions = listOf("Stress", "Sad", "Neutral", "Energetic", "Happy")
fun Int.toMoodLabel(): String = moodDescriptions.getOrElse(this) { "Unknown" }

//enum class MoodPeriod { DAILY, WEEKLY, MONTHLY }

enum class MoodPeriod(val label: String) {
    DAILY("Harian"),
    WEEKLY("Mingguan"),
    MONTHLY("Bulanan")
}
