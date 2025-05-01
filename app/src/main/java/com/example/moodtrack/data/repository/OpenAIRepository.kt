package com.example.moodtrack.data.repository

import com.example.moodtrack.data.local.entity.MoodEntity
import com.example.moodtrack.data.remote.services.OpenAIService
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class OpenAIRepository @Inject constructor(
    private val openAIService: OpenAIService
) {
    private fun <T> safeCall(block: suspend () -> T): Flow<Result<T>> = flow {
        try {
            emit(Result.success(block()))
        } catch (e: Exception) {
            emit(Result.failure(e))
        }
    }

    fun analyzeMood(mood: String, note: String): Flow<Result<String>> = safeCall {
        openAIService.analyzeMood(mood, note)
    }

    fun analyzeAssessment(userAnswers: String): Flow<Result<String>> = safeCall {
        openAIService.analyzeAssessment(userAnswers)
    }

    fun getMoodInsightFromList(moodList: List<MoodEntity>): Flow<Result<String>> = safeCall {
        openAIService.getMoodInsightFromList(moodList)
    }

    fun getMoodInsightWeekly(moodList: List<MoodEntity>): Flow<Result<String>> = safeCall {
        openAIService.generatePromptForWeeklyInsight(moodList)
    }

}