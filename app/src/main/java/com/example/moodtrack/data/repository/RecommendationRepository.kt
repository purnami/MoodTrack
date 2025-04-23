package com.example.moodtrack.data.repository

import com.example.moodtrack.data.remote.services.YoutubeApiService
import com.example.moodtrack.data.remote.response.VideoItem
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class RecommendationRepository @Inject constructor(
    private val apiService: YoutubeApiService
) {
    private fun <T> safeCall(block: suspend () -> T): Flow<Result<T>> = flow {
        try {
            emit(Result.success(block()))
        } catch (e: Exception) {
            emit(Result.failure(e))
        }
    }

    fun getVideosByMood(apiKey: String, mood: Int): Flow<Result<List<VideoItem>>> = safeCall {
        val query = when (mood) {
            0 -> "meditation for stress"
            1 -> "meditation for sadness"
            2 -> "neutral mood meditation"
            3 -> "energetic meditation"
            4 -> "happy meditation"
            else -> "guided meditation"
        }
        apiService.getYouTubeVideos(apiKey, query)
    }
}
