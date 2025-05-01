package com.example.moodtrack.ui.screen.mood

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.moodtrack.core.utils.moodDescriptions
import com.example.moodtrack.core.utils.moodEmojis
import com.example.moodtrack.ui.viewmodel.MoodViewModel

@Composable
fun MoodInputScreen(
    moodViewModel: MoodViewModel = hiltViewModel(),
    onNavigateToRecommendation: (Int, String) -> Unit
) {

    val selectedMood by moodViewModel.selectedMood.collectAsState()
    val note by moodViewModel.note.collectAsState()
    val showDialog by moodViewModel.showDialog.collectAsState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(
                "Bagaimana Perasaan Anda",
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold
            )
            Text(
                "Hari Ini?",
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold,
            )
        }

        Spacer(modifier = Modifier.height(24.dp))

        Text(
            text = moodEmojis[selectedMood],
            fontSize = 80.sp,
            modifier = Modifier.padding(16.dp)
        )

        Spacer(modifier = Modifier.height(16.dp))

        Row(
            horizontalArrangement = Arrangement.SpaceEvenly,
            modifier = Modifier.fillMaxWidth()
        ) {
            moodEmojis.forEachIndexed { index, emoji ->
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = emoji,
                        fontSize = 30.sp,
                        modifier = Modifier
                            .clickable { moodViewModel.updateMood(index) }
                            .padding(8.dp)
                    )
                    Text(
                        text = moodDescriptions[index],
                        style = MaterialTheme.typography.bodySmall,
                        color = if (index == selectedMood) MaterialTheme.colorScheme.primary else Color.Gray
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        OutlinedTextField(
            value = note,
            onValueChange = { moodViewModel.updateNote(it) },
            label = { Text("Tulis catatan...") },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(16.dp))

        Button(
            onClick = {
                moodViewModel.insertMood()
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Catat Mood")
        }
    }

    if (showDialog) {
        AlertDialog(
            onDismissRequest = { moodViewModel.setShowDialog(false) },
            title = { Text("Mood Berhasil Diperbarui!") },
            text = { Text("Ingin melihat rekomendasi kegiatan berdasarkan mood Anda?") },
            confirmButton = {
                Button(onClick = {
                    moodViewModel.setShowDialog(false)
                    onNavigateToRecommendation(selectedMood, note)
                }) {
                    Text("Lihat Rekomendasi")
                }
            },
            dismissButton = {
                TextButton(onClick = { moodViewModel.setShowDialog(false) }) {
                    Text("Tutup")
                }
            }
        )
    }
}
