package com.example.moodtrack.ui.viewmodel

import android.graphics.Bitmap
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.moodtrack.data.local.entity.MoodEntity
import com.example.moodtrack.data.repository.MoodRepository
import com.example.moodtrack.data.repository.ProfileRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject
import android.util.Base64
import java.io.ByteArrayOutputStream

@HiltViewModel
class ProfileViewModel @Inject constructor(
    private val moodRepository: MoodRepository,
    private val profileRepository: ProfileRepository
) : ViewModel() {

    private val _mood = MutableStateFlow<MoodEntity?>(null)
    val mood: StateFlow<MoodEntity?> = _mood.asStateFlow()

    private val _nameInput = MutableStateFlow("")
    val nameInput: StateFlow<String> = _nameInput.asStateFlow()

    private val _photoBase64 = MutableStateFlow<String?>(null)
    val photoBase64: StateFlow<String?> = _photoBase64.asStateFlow()

    private val _isProfileSaved = MutableStateFlow<Boolean?>(null)
    val isProfileSaved: StateFlow<Boolean?> = _isProfileSaved.asStateFlow()

    fun loadLatestMoodFromFirestore() {
        viewModelScope.launch {
            val latestMood = moodRepository.getLatestMoodFromFirestore()
            Log.d("ProfileViewModel", "Latest Mood: $latestMood")
            _mood.value = latestMood
        }
    }

    fun loadProfile() {
        viewModelScope.launch {
            val (name, photoBase64) = profileRepository.loadProfile()
            _nameInput.value = name ?: ""
            _photoBase64.value = photoBase64
        }
    }

    fun saveProfile(name: String, selectedPhotoBase64: String?) {
        viewModelScope.launch {
            val success = profileRepository.saveProfile(name, selectedPhotoBase64)
            _isProfileSaved.value = success
        }
    }

    fun bitmapToBase64(bitmap: Bitmap): String {
        val byteArrayOutputStream = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, byteArrayOutputStream)
        val byteArray = byteArrayOutputStream.toByteArray()
        return Base64.encodeToString(byteArray, Base64.DEFAULT)
    }

    fun setNameInput(value: String) {
        _nameInput.value = value
    }

    fun resetProfileSavedStatus() {
        _isProfileSaved.value = null
    }

    fun setPhotoBase64(base64: String) {
        _photoBase64.value = base64
    }

}