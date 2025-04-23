package com.example.moodtrack.data.remote.request

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class OpenAIRequest(
    @SerialName("model") val model: String,
    val store : Boolean = true,
    val messages: List<MessageRequest>
)


@Serializable
data class MessageRequest(
    val role: String,
    val content: String
)