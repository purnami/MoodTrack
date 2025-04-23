package com.example.moodtrack.ui.viewmodel

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.moodtrack.data.local.entity.MoodEntity
import com.example.moodtrack.data.repository.MoodRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.util.UUID
import javax.inject.Inject

@HiltViewModel
class MoodViewModel @Inject constructor(private val moodRepository: MoodRepository) : ViewModel() {

    private val _selectedMood = MutableStateFlow(3)
    val selectedMood: StateFlow<Int> = _selectedMood.asStateFlow()

    private val _note = MutableStateFlow("")
    val note: StateFlow<String> = _note.asStateFlow()

    private val _showDialog = MutableStateFlow(false)
    val showDialog: StateFlow<Boolean> = _showDialog.asStateFlow()

    private val _moodState = MutableStateFlow<List<MoodEntity>>(emptyList())
    val moodState: StateFlow<List<MoodEntity>> = _moodState.asStateFlow()

    fun updateMood(mood: Int) {
        _selectedMood.value = mood
    }

    fun updateNote(text: String) {
        _note.value = text
    }

    fun setShowDialog(show: Boolean) {
        _showDialog.value = show
    }

    fun insertMood() {
        viewModelScope.launch {
            moodRepository.insertMood(
                mood = _selectedMood.value,
                note = _note.value
            )
            _showDialog.value = true
        }
    }

    fun fetchAllMoods() {
        moodRepository.getMoodsByCurrentUser().onEach { moods ->
            _moodState.value = moods
        }.launchIn(viewModelScope)
    }

    fun scheduleMoodNotifications(context: Context) {
        viewModelScope.launch {
            moodRepository.scheduleMoodNotifications(context)
        }
    }

}
