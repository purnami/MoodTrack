package com.example.moodtrack.ui.viewmodel

import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.moodtrack.core.utils.MoodPeriod
import com.example.moodtrack.data.local.entity.MoodEntity
import com.example.moodtrack.data.repository.MoodRepository
import com.example.moodtrack.data.repository.OpenAIRepository
import com.example.moodtrack.ui.common.UiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import java.time.LocalDate
import javax.inject.Inject

@HiltViewModel
class MoodStatisticsViewModel @Inject constructor(
    private val moodRepository: MoodRepository,
    private val openAIRepository: OpenAIRepository
) : ViewModel() {

    private val _selectedPeriod = MutableStateFlow(MoodPeriod.DAILY)
    val selectedPeriod: StateFlow<MoodPeriod> = _selectedPeriod

    private val _moodData = MutableStateFlow<List<MoodEntity>>(emptyList())
    val moodData: StateFlow<List<MoodEntity>> = _moodData

    private val _isRefreshing = MutableStateFlow(false)
    val isRefreshing: StateFlow<Boolean> = _isRefreshing

    private val _moodInsight = MutableStateFlow<UiState<String>>(UiState.Idle)
    val moodInsight: StateFlow<UiState<String>> = _moodInsight.asStateFlow()
//    init {
//        fetchMoodData()
//    }

    init {
        viewModelScope.launch {
            _selectedPeriod.collectLatest { period ->
                fetchMoodData(period)
            }
        }
    }

    fun refreshMoodData() {
        _isRefreshing.value = true
        viewModelScope.launch {
            fetchMoodData(_selectedPeriod.value)
            _isRefreshing.value = false
        }
    }

//    fun setPeriod(period: MoodPeriod) {
//        _selectedPeriod.value = period
//        fetchMoodData()
//    }

    fun setPeriod(period: MoodPeriod) {
        _selectedPeriod.value = period
    }

    private suspend fun fetchMoodData(period: MoodPeriod) {
        moodRepository.getMoodsByUserIdFromFirestore(period).collect { moods ->
            Log.d("MoodStatisticsViewModel", "Fetched moods: $moods")
            _moodData.value = moods
            fetchMoodInsight()
        }
    }

    private fun fetchMoodInsight() {
        viewModelScope.launch {
            _isRefreshing.value = true
            openAIRepository.getMoodInsightFromList(_moodData.value).collect { result ->
                result
                    .onSuccess { data ->
                        Log.d("MoodStatisticsViewModel", "Insight: $data")
                        _moodInsight.value = UiState.Success(data)
                    }
                    .onFailure { throwable ->
                        Log.d("MoodStatisticsViewModel", "Error analyzing mood: ${throwable.message}")
                        _moodInsight.value = UiState.Error(throwable.message ?: "Terjadi kesalahan")
                    }
            }
            _isRefreshing.value = false
        }
    }


//    fun fetchMoodData() {
//        viewModelScope.launch {
////            moodRepository.getMoodsByCurrentUser().collect { moods ->
////                _moodData.value = moods
////            }
////            moodRepository.getMoodsByUserIdFromFirestore().collect { moods ->
////                Log.d("MoodStatisticsViewModel", "Fetched moods: $moods")
////                _moodData.value = moods
////            }
//            moodRepository.getMoodsByUserIdFromFirestore(_selectedPeriod.value).collect { moods ->
//                Log.d("MoodStatisticsViewModel", "Fetched moods: $moods")
//                _moodData.value = moods
//            }
//        }
//    }

//    fun fetchMoodData() {
//        viewModelScope.launch {
//            moodRepository.getMoodsByUserIdFromFirestore().collect { moods ->
//                val filteredMoods = filterMoodsByPeriod(moods, _selectedPeriod.value)
//                _moodData.value = filteredMoods
//            }
//        }
//    }
//
//    @RequiresApi(Build.VERSION_CODES.O)
//    private fun filterMoodsByPeriod(moods: List<MoodEntity>, period: MoodPeriod): List<MoodEntity> {
//        val now = LocalDate.now()
//        return when (period) {
//            MoodPeriod.DAILY -> moods.filter { it.date.toLocalDate() == now }
//            MoodPeriod.WEEKLY -> moods.filter { it.date.toLocalDate().isAfter(now.minusDays(7)) }
//            MoodPeriod.MONTHLY -> moods.filter { it.date.toLocalDate().month == now.month }
//        }
//    }

}