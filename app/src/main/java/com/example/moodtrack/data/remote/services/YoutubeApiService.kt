package com.example.moodtrack.data.remote.services

import com.example.moodtrack.data.remote.response.VideoId
import com.example.moodtrack.data.remote.response.VideoItem
import com.example.moodtrack.data.remote.response.VideoSnippet
import com.example.moodtrack.data.remote.response.YouTubeApiResponse
import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.request.*
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.json.Json

class YoutubeApiService(
    private val apiKey: String
) {

    private val client = HttpClient {
        install(ContentNegotiation) {
            json(Json { ignoreUnknownKeys = true })
        }
    }

    suspend fun getYouTubeVideos(query: String): List<VideoItem> {
        val response: YouTubeApiResponse = client.get("https://www.googleapis.com/youtube/v3/search") {
            parameter("part", "snippet")
            parameter("q", query)
            parameter("type", "video")
            parameter("maxResults", 5)
            parameter("key", apiKey)
        }.body()

        return response.items.map {
            val videoId = it.id.videoId
            val snippet = it.snippet

            VideoItem(
                id = VideoId(
                    kind = it.id.kind,
                    videoId = videoId
                ),
                snippet = VideoSnippet(
                    publishedAt = snippet.publishedAt,
                    title = snippet.title,
                    description = snippet.description,
                    thumbnails = snippet.thumbnails,
                    channelTitle = snippet.channelTitle,
                    publishTime = snippet.publishTime
                )
            )
        }
    }
}
