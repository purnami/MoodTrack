package com.example.moodtrack.ui.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.moodtrack.core.utils.SelfAssessmentQuestion
import com.example.moodtrack.data.repository.OpenAIRepository
import com.example.moodtrack.ui.common.UiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SelfAssessmentViewModel @Inject constructor(
    private val openAIRepository: OpenAIRepository
) : ViewModel() {

    private val _questions = listOf(
        SelfAssessmentQuestion("Bagaimana suasana hatimu minggu ini?", listOf("Sangat bahagia", "Bahagia", "Netral", "Sedih", "Sangat sedih")),
        SelfAssessmentQuestion("Seberapa sering kamu merasa cemas?", listOf("Sangat jarang", "Jarang", "Kadang-kadang", "Sering", "Sangat sering")),
        SelfAssessmentQuestion("Bagaimana kualitas tidurmu?", listOf("Sangat baik", "Cukup", "Kurang", "Tidak bisa tidur")),
        SelfAssessmentQuestion("Apakah kamu merasa kelelahan secara emosional?", listOf("Ya", "Kadang-kadang", "Tidak")),
        SelfAssessmentQuestion("Apakah kamu merasa kehilangan minat dalam hal yang kamu sukai?", listOf("Ya", "Kadang-kadang", "Tidak")),
        SelfAssessmentQuestion("Seberapa sering kamu merasa kesepian?", listOf("Sangat jarang", "Jarang", "Kadang-kadang", "Sering", "Sangat sering")),
        SelfAssessmentQuestion("Apakah kamu bisa fokus saat bekerja atau belajar?", listOf("Ya", "Kadang-kadang", "Tidak")),
        SelfAssessmentQuestion("Apakah kamu merasa mudah tersinggung atau marah?", listOf("Ya", "Kadang-kadang", "Tidak")),
        SelfAssessmentQuestion("Seberapa sering kamu merasa berharga atau percaya diri?",listOf("Sangat jarang", "Jarang", "Kadang-kadang", "Sering", "Sangat sering")),
        SelfAssessmentQuestion("Apakah kamu merasa memiliki dukungan dari orang lain?", listOf("Ya", "Kadang-kadang", "Tidak"))
    )

    val questions: List<SelfAssessmentQuestion> = _questions

    private val _answers = MutableStateFlow(List(questions.size) { "" })
    val answers: StateFlow<List<String>> = _answers

    private val _result = MutableStateFlow<UiState<String>>(UiState.Idle)
    val result: StateFlow<UiState<String>> = _result.asStateFlow()

    fun updateAnswer(index: Int, answer: String) {
        _answers.value = _answers.value.toMutableList().apply {
            this[index] = answer
        }
    }

    fun submitAssessment() {
        viewModelScope.launch {
            _result.value = UiState.Loading

            val formattedAnswers = _answers.value.mapIndexed { i, answer ->
                "${i + 1}. ${questions[i].question}: $answer"
            }.joinToString("\n")

            Log.d("SelfAssessmentViewModel", "Formatted Answers: $formattedAnswers")
            openAIRepository.analyzeAssessment(formattedAnswers).collect { result ->
                result
                    .onSuccess { data ->
                        Log.d("SelfAssessmentViewModel", "Insight: $data")
                        _result.value = UiState.Success(data)
                    }
                    .onFailure { throwable ->
                        Log.d("SelfAssessmentViewModel", "Error analyzing mood: ${throwable.message}")
                        _result.value = UiState.Error(throwable.message ?: "Terjadi kesalahan")
                    }
            }
        }
    }
}