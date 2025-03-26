package com.example.moodtrack.ui.screen

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.example.moodtrack.ui.viewmodel.AuthViewModel

@Composable
fun SplashScreen(navController: NavController, authViewModel: AuthViewModel = hiltViewModel()) {
    val context = LocalContext.current

    LaunchedEffect(Unit) {
        if (authViewModel.isUserLoggedIn()) {
            // Jika user sudah login, langsung ke HomeScreen
            navController.navigate("home") {
                popUpTo("splash") { inclusive = true } // Hapus SplashScreen dari backstack
            }
        } else {
            // Jika belum login, ke LoginScreen
            navController.navigate("login") {
                popUpTo("splash") { inclusive = true }
            }
        }
    }

    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        CircularProgressIndicator() // Tampilkan loading saat cek login
    }
}
