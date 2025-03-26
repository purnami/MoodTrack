package com.example.moodtrack.ui.screen.auth

import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.moodtrack.ui.viewmodel.AuthViewModel
import com.example.moodtrack.ui.viewmodel.LoginState
import com.example.moodtrack.ui.viewmodel.RegisterState

@Composable
fun RegisScreen(
    authViewModel: AuthViewModel = hiltViewModel(),
    onRegisSuccess: () -> Unit
) {
    val context = LocalContext.current
    val registerState by authViewModel.registerState.collectAsState()

    LaunchedEffect(registerState) {
        when (registerState) {
            is RegisterState.Success -> {
                Toast.makeText(context, "Registrasi Berhasil", Toast.LENGTH_SHORT).show()
                onRegisSuccess() // Navigasi ke halaman berikutnya
            }
            is RegisterState.Error -> {
                Toast.makeText(context, (registerState as RegisterState.Error).message, Toast.LENGTH_SHORT).show()
            }
            else -> {}
        }
    }

    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        var email by remember { mutableStateOf("") }
        var password by remember { mutableStateOf("") }

        OutlinedTextField(value = email, onValueChange = { email = it }, label = { Text("Email") })
        OutlinedTextField(value = password, onValueChange = { password = it }, label = { Text("Password") }, visualTransformation = PasswordVisualTransformation())

        Spacer(modifier = Modifier.height(16.dp))

        Button(onClick = {
            authViewModel.registerUser(email, password) { success, errorMessage ->
                if (success) {
                    Toast.makeText(context, "Registrasi berhasil!", Toast.LENGTH_SHORT).show()
                    onRegisSuccess()
                } else {
                    Toast.makeText(context, "Gagal registrasi: $errorMessage", Toast.LENGTH_SHORT).show()
                }
            }
        }) {
            Text("Register")
        }

    }
}
