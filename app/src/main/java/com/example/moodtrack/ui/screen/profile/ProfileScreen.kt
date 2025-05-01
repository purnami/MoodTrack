package com.example.moodtrack.ui.screen.profile

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.provider.MediaStore
import androidx.compose.runtime.Composable

import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
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
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.painter.BitmapPainter
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.moodtrack.R
import com.example.moodtrack.ui.viewmodel.AuthViewModel
import com.example.moodtrack.ui.viewmodel.ProfileViewModel

@Composable
fun ProfileScreen(
    authViewModel: AuthViewModel = hiltViewModel(),
    profileViewModel: ProfileViewModel = hiltViewModel(),
    onLogoutSuccess: () -> Unit,
    onNavigateToRecommendation: (Int, String) -> Unit
) {
    val context = LocalContext.current
    val isUserLoggedIn by authViewModel.isUserLoggedIn.collectAsState()
    val user by authViewModel.currentUser.collectAsState()
    val mood by profileViewModel.mood.collectAsState()

    val nameInput by profileViewModel.nameInput.collectAsState()
    val savedPhotoBase64 by profileViewModel.photoBase64.collectAsState()
    val saveResult by profileViewModel.isProfileSaved.collectAsState()

    val photoPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        uri?.let {
            val bitmap = MediaStore.Images.Media.getBitmap(context.contentResolver, it)
            val base64 = profileViewModel.bitmapToBase64(bitmap)
            profileViewModel.setPhotoBase64(base64)
            profileViewModel.saveProfile(nameInput, base64)
        }
    }

    LaunchedEffect(Unit) {
        profileViewModel.loadLatestMoodFromFirestore()
        profileViewModel.loadProfile()
    }

    if (saveResult != null) {
        if (saveResult as Boolean) {
            Toast.makeText(context, "Profil berhasil disimpan", Toast.LENGTH_SHORT).show()
        } else {
            Toast.makeText(context, "Gagal menyimpan profil", Toast.LENGTH_SHORT).show()
        }

        profileViewModel.resetProfileSavedStatus()
    }

    if (isUserLoggedIn && user != null) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 16.dp),
                shape = MaterialTheme.shapes.medium,
                elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
            ) {
                Column(
                    modifier = Modifier
                        .padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Box(modifier = Modifier.size(120.dp)) {
                        val imageBitmap: Bitmap? = savedPhotoBase64?.let {
                            val decodedBytes = android.util.Base64.decode(it, android.util.Base64.DEFAULT)
                            BitmapFactory.decodeByteArray(decodedBytes, 0, decodedBytes.size)
                        }

                        Image(
                            painter = if (imageBitmap != null)
                                BitmapPainter(imageBitmap.asImageBitmap())
                            else
                                painterResource(R.drawable.placeholder_profile),
                            contentDescription = "Foto Profil",
                            modifier = Modifier
                                .size(120.dp)
                                .clip(CircleShape)
                                .border(2.dp, MaterialTheme.colorScheme.primary, CircleShape)
                        )
                        IconButton(
                            onClick = { photoPickerLauncher.launch("image/*") },
                            modifier = Modifier
                                .align(Alignment.BottomEnd)
                                .background(Color.White, shape = CircleShape)
                                .border(1.dp, MaterialTheme.colorScheme.primary, CircleShape)
                                .size(36.dp)
                        ) {
                            Icon(Icons.Default.Edit, contentDescription = "Edit Foto", tint = MaterialTheme.colorScheme.primary)
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    var isEditing by remember { mutableStateOf(false) }

//                    if (nameInput.isBlank() || isEditing) {

                    if (isEditing) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 8.dp)
                        ) {
                            OutlinedTextField(
                                value = nameInput,
                                onValueChange = { profileViewModel.setNameInput(it) },
                                label = { Text("Nama Pengguna") },
                                singleLine = true,
                                modifier = Modifier.weight(1f)
                            )

                            Spacer(modifier = Modifier.width(8.dp))

                            Button(
                                onClick = {
                                    profileViewModel.saveProfile(nameInput, savedPhotoBase64 ?: "")
                                    isEditing = false
                                },
                                modifier = Modifier.height(56.dp)
                            ) {
                                Text("Simpan")
                            }
                        }
                    } else {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.Center,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text(
                                text = nameInput.ifBlank { "Nama Pengguna" },
                                style = MaterialTheme.typography.titleMedium,
                                modifier = Modifier.padding(end = 4.dp),
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )

                            IconButton(
                                onClick = { isEditing = true }, // Masuk ke mode edit
                                modifier = Modifier.size(24.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Edit,
                                    contentDescription = "Edit Nama",
                                    tint = MaterialTheme.colorScheme.primary
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    Text(
                        text = user?.email ?: "Email tidak tersedia",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        textAlign = TextAlign.Center
                    )
                }
            }

            Spacer(modifier = Modifier.height(32.dp))

            Column(modifier = Modifier.fillMaxWidth()) {
                Button(
                    onClick = {
                        mood?.let {
                            onNavigateToRecommendation(it.mood, it.note.toString())
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp)
                ) {
                    Text("Rekomendasi")
                }

                Button(
                    onClick = {
                        authViewModel.logout()
                        Toast.makeText(context, "Logout Berhasil", Toast.LENGTH_SHORT).show()
                        onLogoutSuccess()
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp)
                ) {
                    Text("Logout", color = MaterialTheme.colorScheme.onError)
                }
            }
        }
    } else {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "Pengguna belum login.",
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                textAlign = TextAlign.Center
            )
        }
    }
}