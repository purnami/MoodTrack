package com.example.moodtrack.data.repository

import android.util.Log
import com.example.moodtrack.data.local.preferences.UserPreferences
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthUserCollisionException
import com.google.firebase.auth.FirebaseUser
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AuthRepository @Inject constructor(
    private val auth: FirebaseAuth,
    private val userPreferences: UserPreferences
) {

    fun login(email: String, password: String): Flow<Result<FirebaseUser?>> = callbackFlow {
        auth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val user = auth.currentUser
                    user?.uid?.let { uid ->
                        userPreferences.saveUserId(uid)
                    }
                    trySend(Result.success(auth.currentUser))
                } else {
                    trySend(Result.failure(task.exception ?: Exception("Login gagal")))
                }
                close()
            }
        awaitClose()
    }

    fun register(email: String, password: String): Flow<Result<FirebaseUser?>> = callbackFlow {
        auth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val user = auth.currentUser
                    user?.uid?.let { uid ->
                        userPreferences.saveUserId(uid)
                    }
                    Log.d("AuthRepository", "Registrasi berhasil: ${auth.currentUser}")
                    trySend(Result.success(auth.currentUser))
                } else {
                    Log.d("AuthRepository", "Registrasi gagal: ${task.exception}")
                    trySend(Result.failure(task.exception ?: Exception("Registrasi gagal")))
                }
                close()
            }
        awaitClose()
    }

    suspend fun checkEmailExists(email: String): Boolean {
        return try {
            auth.createUserWithEmailAndPassword(email, "temporaryPassword123").await()
                .user?.delete()
            false
        } catch (e: FirebaseAuthUserCollisionException) {
            true
        } catch (e: Exception) {
            false
        }
    }

    fun logout() {
        auth.signOut()
    }

    fun getCurrentUserFlow(): Flow<FirebaseUser?> = callbackFlow {
        val listener = FirebaseAuth.AuthStateListener { auth ->
            trySend(auth.currentUser)
        }
        auth.addAuthStateListener(listener)
        awaitClose { auth.removeAuthStateListener(listener) }
    }

}
