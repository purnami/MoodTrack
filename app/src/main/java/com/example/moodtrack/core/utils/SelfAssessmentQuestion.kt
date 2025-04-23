package com.example.moodtrack.core.utils

data class SelfAssessmentQuestion(
    val question: String,
    val options: List<String> = emptyList()
)
