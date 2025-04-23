package com.example.moodtrack.data.remote.response

import kotlinx.serialization.Serializable
import kotlinx.serialization.json.JsonElement

@Serializable
data class OpenAIResponse(
    val choices: List<Choice>
)

@Serializable
data class Choice(
    val message: MessageResponse
)

@Serializable
data class MessageResponse(
    val role: String,
    val content: String
)

@Serializable
data class OpenAIErrorResponse(
    val error: OpenAIError
)

@Serializable
data class OpenAIError(
    val message: String,
    val type: String? = null,
    val param: JsonElement? = null,
    val code: JsonElement? = null
)
