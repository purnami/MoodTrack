package com.example.moodtrack.data.remote.services

import android.util.Log
import com.example.moodtrack.data.local.entity.MoodEntity
import com.example.moodtrack.data.remote.request.MessageRequest
import com.example.moodtrack.data.remote.request.OpenAIRequest
import com.example.moodtrack.data.remote.response.OpenAIErrorResponse
import com.example.moodtrack.data.remote.response.OpenAIResponse
import io.ktor.client.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*
import kotlinx.serialization.json.Json
import java.text.SimpleDateFormat
import java.util.*

class OpenAIService(private val client: HttpClient) {

   private val json = Json { ignoreUnknownKeys = true }

    suspend fun analyzeMood(mood: String, note: String): String {
        val prompt = "Mood: $mood. Note: $note"
        return requestToOpenAI(
            systemPrompt = "Tolong berikan rekomendasi kegiatan",
            userPrompt = prompt
        )
    }

    suspend fun analyzeAssessment(userAnswers: String): String {
        val prompt = """
            Saya ingin kamu bertindak sebagai konselor non-medis. Berikut ini adalah hasil self-assessment pengguna:

            $userAnswers

            Berikan:
            - Ringkasan kondisi emosional
            - Tanda-tanda positif
            - Hal yang perlu diperhatikan
            - Saran ringan sehari-hari
            - Ajakan mencari bantuan profesional (jika perlu)
        """.trimIndent()

        return requestToOpenAI(
            systemPrompt = "Kamu adalah konselor empatik",
            userPrompt = prompt
        )
    }

    suspend fun getMoodInsightFromList(moodList: List<MoodEntity>): String {
        if (moodList.isEmpty()) return "Tidak ada data mood untuk dianalisis."

        val dateFormat = SimpleDateFormat("dd MMM yyyy", Locale("id", "ID"))

        val moodDataString = moodList.joinToString("\n") { mood ->
            val date = dateFormat.format(Date(mood.timestamp))
            val moodLevel = mood.mood
            val note = mood.note?.ifBlank { "-" } ?: "-"
            "Tanggal: $date, Mood: $moodLevel, Catatan: $note"
        }

        val prompt = """
        Saya ingin kamu bertindak sebagai analis suasana hati. Berdasarkan data mood berikut ini:

        $moodDataString

        Tolong berikan insight:
        1. Ringkasan tren suasana hati pengguna.
        2. Apakah ada pola penurunan atau peningkatan emosi?
        3. Rekomendasi ringan berdasarkan data tersebut.
        4. Motivasi singkat atau ajakan reflektif.
    """.trimIndent()

        return requestToOpenAI(
            systemPrompt = "Kamu adalah asisten psikologis empatik yang mampu menganalisis mood dengan bijak.",
            userPrompt = prompt
        )
    }

    private suspend fun requestToOpenAI(systemPrompt: String, userPrompt: String): String {
        val request = OpenAIRequest(
            model = "gpt-4o-mini",
            messages = listOf(
                MessageRequest("system", systemPrompt),
                MessageRequest("user", userPrompt)
            )
        )

        val jsonBody = json.encodeToString(OpenAIRequest.serializer(), request)

        return try {
            val response: HttpResponse = client.post("https://api.openai.com/v1/chat/completions") {
                header(HttpHeaders.Authorization, apiKey)
                contentType(ContentType.Application.Json)
                setBody(jsonBody)
            }

            val responseBody = response.bodyAsText()
            Log.d("OpenAIResponse", responseBody)

            try {
                val successResponse = json.decodeFromString<OpenAIResponse>(responseBody)
                successResponse.choices.firstOrNull()?.message?.content ?: "Tidak ada tanggapan dari model."
            } catch (e: Exception) {
                val errorResponse = json.decodeFromString<OpenAIErrorResponse>(responseBody)
                "Terjadi kesalahan dari OpenAI: ${errorResponse.error.message}"
            }
        } catch (e: Exception) {
            "Terjadi kesalahan: ${e.message}"
        }
    }
}
