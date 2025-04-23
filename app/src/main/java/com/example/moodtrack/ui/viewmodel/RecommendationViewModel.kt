package com.example.moodtrack.ui.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.moodtrack.core.utils.toMoodLabel
import com.example.moodtrack.data.remote.response.VideoItem
import com.example.moodtrack.data.repository.OpenAIRepository
import com.example.moodtrack.data.repository.RecommendationRepository
import com.example.moodtrack.ui.common.UiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject
import com.example.moodtrack.core.utils.toMoodLabel


@HiltViewModel
class RecommendationViewModel @Inject constructor(
    private val recommendationRepository: RecommendationRepository,
    private val openAIRepository: OpenAIRepository
) : ViewModel() {

//    private val _musicState = MutableStateFlow<UiState<List<MusicItem>>>(UiState.Idle)
//    val musicState: StateFlow<UiState<List<MusicItem>>> = _musicState.asStateFlow()

    private val _videoState = MutableStateFlow<UiState<List<VideoItem>>>(UiState.Idle)
    val videoState: StateFlow<UiState<List<VideoItem>>> = _videoState.asStateFlow()

    private val _insightState = MutableStateFlow<UiState<String>>(UiState.Idle)
    val insightState: StateFlow<UiState<String>> = _insightState.asStateFlow()

    private val _mood = MutableStateFlow<Int>(0)
    val mood: StateFlow<Int> = _mood

    private val _note = MutableStateFlow<String>("")
    val note: StateFlow<String> = _note

    fun setMood(mood: Int) {
        _mood.value = mood
    }

    fun setNote(note: String) {
        _note.value = note
    }

    init {
        analyzeMood(mood.value.toMoodLabel(), note.value)
    }

//    fun fetchMusicByMood(mood: Int) {
//        viewModelScope.launch {
//            _musicState.value = UiState.Loading
//            recommendationRepository.getMusicByMood(mood).collect { result ->
//                _musicState.value = result.fold(
//                    onSuccess = { UiState.Success(it) },
//                    onFailure = { UiState.Error(it.message ?: "Terjadi kesalahan") }
//                )
//            }
//        }
//    }

    fun fetchVideosByMood(apiKey: String, mood: Int) {
        Log.d("RecommendationViewModel", "Fetching videos for mood: $mood")
        viewModelScope.launch {
            _videoState.value = UiState.Loading
            recommendationRepository.getVideosByMood(apiKey, mood).collect { result ->
                _videoState.value = result.fold(
                    onSuccess = {
                        Log.d("RecommendationViewModel", "Fetched videos: $it")
                        UiState.Success(it)
                    },
                    onFailure = {
                        Log.d("RecommendationViewModel", "Error fetching videos: ${it.message}")
                        UiState.Error(it.message ?: "Terjadi kesalahan")
                    }
                )
            }
        }
    }

    fun analyzeMood(mood: String, note: String) {
        Log.d("RecommendationViewModel", "Mood: $mood, Note: $note")
        viewModelScope.launch {
            _insightState.value = UiState.Loading
            openAIRepository.analyzeMood(mood, note).collect { result ->
                result
                    .onSuccess { data ->
                        Log.d("RecommendationViewModel", "Insight: $data")
                        _insightState.value = UiState.Success(data)
                    }
                    .onFailure { throwable ->
                        Log.d("RecommendationViewModel", "Error analyzing mood: ${throwable.message}")
                        _insightState.value = UiState.Error(throwable.message ?: "Terjadi kesalahan")
                    }
            }
        }
    }
}