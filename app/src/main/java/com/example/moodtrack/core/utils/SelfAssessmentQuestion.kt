package com.example.moodtrack.core.utils

data class SelfAssessmentQuestion(
    val question: String,
//    val type: QuestionType,
    val options: List<String> = emptyList()
)

//enum class QuestionType {
//    MULTIPLE_CHOICE, SCALE, YES_NO, TEXT
//}