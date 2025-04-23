package com.example.moodtrack.ui.screen.profile

import androidx.compose.runtime.Composable

import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.rememberAsyncImagePainter
import com.example.moodtrack.R
import com.example.moodtrack.ui.viewmodel.AuthViewModel
@Composable
fun ProfileScreen(
    authViewModel: AuthViewModel = hiltViewModel(),
    onLogoutSuccess: () -> Unit,
    onNavigateToSettings: () -> Unit,
    onNavigateToRecommendation: () -> Unit,
) {
    val context = LocalContext.current
    val isUserLoggedIn by authViewModel.isUserLoggedIn.collectAsState()
    val user by authViewModel.currentUser.collectAsState()

    if (isUserLoggedIn && user != null) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Foto Profil + Edit
            Box(modifier = Modifier.size(120.dp)) {
                Image(
                    painter = rememberAsyncImagePainter(user!!.photoUrl ?: R.drawable.placeholder_profile),
                    contentDescription = "Foto Profil",
                    modifier = Modifier
                        .size(120.dp)
                        .clip(CircleShape)
                        .border(2.dp, Color.Gray, CircleShape)
                )
                IconButton(
                    onClick = { /* buka galeri atau dialog ganti foto */ },
                    modifier = Modifier
                        .align(Alignment.BottomEnd)
                        .background(Color.White, shape = CircleShape)
                        .size(32.dp)
                ) {
                    Icon(Icons.Default.Edit, contentDescription = "Edit Foto", tint = MaterialTheme.colorScheme.primary)
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Nama Pengguna
            Text(
                text = user?.displayName ?: "Nama tidak tersedia",
                style = MaterialTheme.typography.titleMedium
            )

            // Email
            Text(
                text = user?.email ?: "Email tidak tersedia",
                style = MaterialTheme.typography.bodySmall,
                color = Color.Gray
            )

            Spacer(modifier = Modifier.height(32.dp))

            // Tombol Navigasi
            Button(
                onClick = onNavigateToSettings,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Pengaturan")
            }

            Spacer(modifier = Modifier.height(12.dp))

            Button(
                onClick = onNavigateToRecommendation,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Rekomendasi")
            }

            Spacer(modifier = Modifier.height(12.dp))

            Button(
                onClick = {
                    authViewModel.logout()
                    Toast.makeText(context, "Logout Berhasil", Toast.LENGTH_SHORT).show()
                    onLogoutSuccess()
                },
                colors = ButtonDefaults.buttonColors(containerColor = Color.Red),
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Logout", color = Color.White)
            }
        }
    } else {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "Pengguna belum login.",
                style = MaterialTheme.typography.bodyMedium,
                textAlign = TextAlign.Center
            )
        }
    }
}

//@Composable
//fun ProfileScreen(
//    authViewModel: AuthViewModel = hiltViewModel(),
//    onLogoutSuccess: () -> Unit
//) {
//    val context = LocalContext.current
//    val isUserLoggedIn by authViewModel.isUserLoggedIn.collectAsState()
//
//    if (isUserLoggedIn) {
//        Column(
//            modifier = Modifier
//                .fillMaxSize()
//                .padding(16.dp),
//            horizontalAlignment = Alignment.CenterHorizontally,
//            verticalArrangement = Arrangement.Center
//        ) {
//            // Menampilkan informasi akun pengguna jika sudah login
//            Text(
//                text = "Pengguna sudah login.",
//                style = MaterialTheme.typography.bodyMedium
//            )
//
//            Spacer(modifier = Modifier.height(32.dp))
//
//            // Tombol Logout
//            Button(
//                onClick = {
//                    authViewModel.logout()
//                    Toast.makeText(context, "Logout Berhasil", Toast.LENGTH_SHORT).show()
//                    onLogoutSuccess() // Panggil callback setelah logout berhasil
//                },
//                modifier = Modifier.fillMaxWidth()
//            ) {
//                Text("Logout")
//            }
//        }
//    } else {
//        // Jika pengguna belum login, tampilkan pesan
//        Text(
//            text = "Pengguna belum login.",
//            style = MaterialTheme.typography.bodyMedium,
//            modifier = Modifier.fillMaxSize(),
//            textAlign = TextAlign.Center
//        )
//    }
//}
