package com.example.moodtrack.ui.viewmodel

import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import javax.inject.Inject
import com.example.moodtrack.data.repository.AuthRepository
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.flow.asStateFlow

@HiltViewModel
class AuthViewModel @Inject constructor(
    private val auth: FirebaseAuth
) : ViewModel() {

    private val _loginState = MutableStateFlow<LoginState>(LoginState.Idle)
    val loginState: StateFlow<LoginState> = _loginState.asStateFlow()

    private val _registerState = MutableStateFlow<RegisterState>(RegisterState.Idle)
    val registerState: StateFlow<RegisterState> = _registerState.asStateFlow()

    /** 🔹 Cek apakah user sudah login */
    fun isUserLoggedIn(): Boolean {
        return auth.currentUser != null
    }

    /** 🔹 Login dengan email & password */
    fun loginWithEmail(email: String, password: String) {
        _loginState.value = LoginState.Loading
        auth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                _loginState.value = if (task.isSuccessful) {
                    LoginState.Success
                } else {
                    LoginState.Error(task.exception?.message ?: "Login gagal")
                }
            }
    }

    /** 🔹 Registrasi user baru */
    fun registerUser(email: String, password: String) {
        _registerState.value = RegisterState.Loading
        auth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                _registerState.value = if (task.isSuccessful) {
                    RegisterState.Success
                } else {
                    RegisterState.Error(task.exception?.message ?: "Registrasi gagal")
                }
            }
    }

    /** 🔹 Logout */
    fun logout() {
        auth.signOut()
        _loginState.value = LoginState.Idle
    }
}

sealed class LoginState {
    object Idle : LoginState()
    object Loading : LoginState()
    object Success : LoginState()
    data class Error(val message: String) : LoginState()
}

sealed class RegisterState {
    object Idle : RegisterState()
    object Loading : RegisterState()
    object Success : RegisterState()
    data class Error(val message: String) : RegisterState()
}

