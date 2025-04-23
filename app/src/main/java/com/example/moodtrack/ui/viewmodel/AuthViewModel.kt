package com.example.moodtrack.ui.viewmodel

import android.util.Log
import android.util.Patterns
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.moodtrack.data.repository.AuthRepository
import com.example.moodtrack.ui.common.UiState
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.FirebaseAuthInvalidUserException
import com.google.firebase.auth.FirebaseUser
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AuthViewModel @Inject constructor(private val authRepository: AuthRepository) : ViewModel() {

    private val _loginState = MutableStateFlow<UiState<Unit>>(UiState.Idle)
    val loginState = _loginState.asStateFlow()

    private val _registerState = MutableStateFlow<UiState<FirebaseUser?>>(UiState.Idle)
    val registerState: StateFlow<UiState<FirebaseUser?>> = _registerState.asStateFlow()

    private val _errors = MutableStateFlow<Map<String, String>>(emptyMap())
    val errors: StateFlow<Map<String, String>> = _errors.asStateFlow()

    private val _email = MutableStateFlow("")
    val email: StateFlow<String> = _email.asStateFlow()

    private val _password = MutableStateFlow("")
    val password: StateFlow<String> = _password.asStateFlow()

    private val _confirmPassword = MutableStateFlow("")
    val confirmPassword: StateFlow<String> = _confirmPassword.asStateFlow()

    private val _passwordVisible = MutableStateFlow(false)
    val passwordVisible: StateFlow<Boolean> = _passwordVisible.asStateFlow()

    private val _confirmPasswordVisible = MutableStateFlow(false)
    val confirmPasswordVisible: StateFlow<Boolean> = _confirmPasswordVisible.asStateFlow()

    fun updateEmail(newEmail: String) {
        _email.value = newEmail
    }

    fun updatePassword(newPassword: String) {
        _password.value = newPassword
    }

    fun updateConfirmPassword(newConfirmPassword: String) {
        _confirmPassword.value = newConfirmPassword
    }

    fun togglePasswordVisibility() {
        _passwordVisible.value = !_passwordVisible.value
    }

    fun toggleConfirmPasswordVisibility() {
        _confirmPasswordVisible.value = !_confirmPasswordVisible.value
    }

    val isUserLoggedIn: StateFlow<Boolean> = authRepository.getCurrentUserFlow()
        .map { it != null }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), false)

    val currentUser = MutableStateFlow(FirebaseAuth.getInstance().currentUser)

    fun loginWithEmail() {
        val validationErrors = inputValidation(_email.value, _password.value, isRegis = false)
        _errors.value = validationErrors
        if (validationErrors.isNotEmpty()) return
        _loginState.value = UiState.Loading
        viewModelScope.launch {
            authRepository.login(_email.value, _password.value).collect { result ->
                _loginState.value = result.fold(
                    onSuccess = { UiState.Success(Unit) },
                    onFailure = { exception ->
                        val errorMessage = when (exception) {
                            is FirebaseAuthInvalidUserException,
                            is FirebaseAuthInvalidCredentialsException ->
                                "Email atau password salah, silakan coba lagi"
                            else -> exception.message ?: "Login gagal"
                        }
                        UiState.Error(errorMessage)
                    }
                )
            }
        }
    }

    fun registerWithEmail(email: String, password: String, confirmPassword: String) {
        Log.d("AuthViewModel", "Registering with email: $email");
        val validationErrors = inputValidation(email, password, confirmPassword, true)
        _errors.value = validationErrors

        if (validationErrors.isNotEmpty()) return

        _registerState.value = UiState.Loading

        viewModelScope.launch {
            val isRegistered = checkIfEmailExists(email)
            if (isRegistered) {
                _registerState.value = UiState.Error("Email sudah terdaftar")
                return@launch
            }

            authRepository.register(email, password).collect { result ->
                _registerState.value = result.fold(
                    onSuccess = { UiState.Success(it) },
                    onFailure = { UiState.Error(it.message ?: "Registrasi gagal") }
                )
            }
        }
    }

    private fun inputValidation(email: String, password: String, confirmPassword: String? = null, isRegis: Boolean): Map<String, String> {
        val errors = mutableMapOf<String, String>()

        if (email.isBlank()) {
            errors["email"] = "Email tidak boleh kosong"
        } else if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            errors["email"] = "Format email tidak valid"
        }

        if (password.isBlank()) {
            errors["password"] = "Password tidak boleh kosong"
        } else if (password.length < 8) {
            errors["password"] = "Password minimal 8 karakter"
        }

        if (isRegis) {
            if (confirmPassword.isNullOrBlank()) {
                errors["confirmPassword"] = "Konfirmasi password tidak boleh kosong"
            } else if (password != confirmPassword) {
                errors["confirmPassword"] = "Password dan konfirmasi tidak cocok"
            }
        }

        return errors
    }

    private suspend fun checkIfEmailExists(email: String): Boolean {
        return authRepository.checkEmailExists(email)
    }

    fun logout() {
        viewModelScope.launch {
            authRepository.logout()
        }
    }

}