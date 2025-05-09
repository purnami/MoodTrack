package com.example.moodtrack.ui.screen.auth

import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.moodtrack.ui.common.UiState
import com.example.moodtrack.ui.components.InputField
import com.example.moodtrack.ui.viewmodel.AuthViewModel

@Composable
fun RegisScreen(
    authViewModel: AuthViewModel = hiltViewModel(),
    onRegisSuccess: () -> Unit,
    onNavigateToLogin: () -> Unit
) {
    val context = LocalContext.current
    val registerState by authViewModel.registerState.collectAsState()
    val errors by authViewModel.errors.collectAsState()

    val email by authViewModel.email.collectAsState()
    val password by authViewModel.password.collectAsState()
    val confirmPassword by authViewModel.confirmPassword.collectAsState()

    val passwordVisible by authViewModel.passwordVisible.collectAsState()
    val confirmPasswordVisible by authViewModel.confirmPasswordVisible.collectAsState()

    LaunchedEffect(registerState) {
        when (registerState) {
            is UiState.Success -> {
                Toast.makeText(context, "Registrasi Berhasil", Toast.LENGTH_SHORT).show()
                onRegisSuccess()
            }
            is UiState.Error -> {
                Toast.makeText(context, (registerState as UiState.Error).errorMessage, Toast.LENGTH_SHORT).show()
            }
            else -> Unit
        }
    }

    Column(
        modifier = Modifier.fillMaxSize().padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Spacer(modifier = Modifier.height(32.dp))

        InputField(
            value = email,
            label = "Email",
            error = errors["email"],
            onValueChange = { authViewModel.updateEmail(it) }
        )

        InputField(
            value = password,
            label = "Password",
            isPassword = true,
            isPasswordVisible = passwordVisible,
            onVisibilityChange = { authViewModel.togglePasswordVisibility() },
            error = errors["password"],
            onValueChange = { authViewModel.updatePassword(it) }
        )

        InputField(
            value = confirmPassword,
            label = "Konfirmasi Password",
            isPassword = true,
            isPasswordVisible = confirmPasswordVisible,
            onVisibilityChange = { authViewModel.toggleConfirmPasswordVisibility() },
            error = errors["confirmPassword"],
            onValueChange = { authViewModel.updateConfirmPassword(it) }
        )

        Spacer(modifier = Modifier.height(16.dp))

        Button(
            onClick = { authViewModel.registerWithEmail(email, password, confirmPassword) },
            modifier = Modifier.fillMaxWidth(),
            enabled = registerState !is UiState.Loading
        ) {
            if (registerState is UiState.Loading) {
                CircularProgressIndicator(modifier = Modifier.size(24.dp))
            } else {
                Text("Daftar")
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        TextButton(onClick = onNavigateToLogin) {
            Text("Sudah punya akun? Login")
        }
    }
}
