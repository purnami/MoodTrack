package com.example.moodtrack.data.repository

import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class ProfileRepository @Inject constructor(
    private val firestore: FirebaseFirestore
) {

    suspend fun loadProfile(): Pair<String?, String?> {
        val doc = firestore.collection("users").document("user_id").get().await()
        val name = doc.getString("name")
        val photoBase64 = doc.getString("photoBase64")
        return Pair(name, photoBase64)
    }

    suspend fun saveProfile(name: String, photoBase64: String?): Boolean {
        val data = hashMapOf(
            "name" to name,
            "photoBase64" to photoBase64
        )
        return try {
            firestore.collection("users").document("user_id").set(data).await()
            true
        } catch (e: Exception) {
            false
        }
    }
}