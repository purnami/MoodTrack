package com.example.moodtrack.data.remote.response

import kotlinx.serialization.Serializable

@Serializable
data class YouTubeApiResponse(
    val items: List<VideoItem>
)

@Serializable
data class VideoItem(
    val id: VideoId,
    val snippet: VideoSnippet
)

@Serializable
data class VideoId(
    val kind: String,
    val videoId: String
)

@Serializable
data class VideoSnippet(
    val publishedAt: String,
    val title: String,
    val description: String,
    val thumbnails: VideoThumbnails,
    val channelTitle: String,
    val publishTime: String
)

@Serializable
data class VideoThumbnails(
    val default: ThumbnailDetails,
    val medium: ThumbnailDetails,
    val high: ThumbnailDetails
)

@Serializable
data class ThumbnailDetails(
    val url: String,
    val width: Int,
    val height: Int
)
