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
fun LoginScreen(
    authViewModel: AuthViewModel = hiltViewModel(),
    onLoginSuccess: () -> Unit,
    onNavigateToRegister: () -> Unit
) {
    val context = LocalContext.current
    val loginState by authViewModel.loginState.collectAsState()
    val errors by authViewModel.errors.collectAsState()

    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }

    LaunchedEffect(loginState) {
        when (loginState) {
            is UiState.Success -> {
                Toast.makeText(context, "Login Berhasil", Toast.LENGTH_SHORT).show()
                onLoginSuccess()
            }
            is UiState.Error -> {
                Toast.makeText(context, (loginState as UiState.Error).errorMessage, Toast.LENGTH_SHORT).show()
            }
            else -> Unit
        }
    }

    Column(
        modifier = Modifier.fillMaxSize().padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        InputField(
            value = email,
            label = "Email",
            error = errors["email"],
            onValueChange = { email = it }
        )

        InputField(
            value = password,
            label = "Password",
            isPassword = true,
            error = errors["password"],
            onValueChange = { password = it }
        )

        Spacer(modifier = Modifier.height(16.dp))

        Button(
            onClick = { authViewModel.loginWithEmail(email, password) },
            modifier = Modifier.fillMaxWidth(),
            enabled = loginState !is UiState.Loading
        ) {
            if (loginState is UiState.Loading) {
                CircularProgressIndicator(modifier = Modifier.size(24.dp))
            } else {
                Text("Login")
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        TextButton(onClick = onNavigateToRegister) {
            Text("Belum punya akun? Daftar")
        }
    }
}
