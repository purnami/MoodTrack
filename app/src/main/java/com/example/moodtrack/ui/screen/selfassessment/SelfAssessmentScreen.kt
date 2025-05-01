package com.example.moodtrack.ui.screen.selfassessment

import android.widget.Toast
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.moodtrack.ui.common.UiState
import com.example.moodtrack.ui.viewmodel.SelfAssessmentViewModel

@Composable
fun SelfAssessmentScreen(
    viewModel: SelfAssessmentViewModel = hiltViewModel()
) {
    val context = LocalContext.current
    val questions = viewModel.questions
    val answers by viewModel.answers.collectAsState()
    val result by viewModel.result.collectAsState()

    val scrollState = rememberScrollState()

    LaunchedEffect(result) {
        if (result is UiState.Loading || result is UiState.Success || result is UiState.Error) {
            scrollState.animateScrollTo(scrollState.maxValue)
        }
    }

    Box(modifier = Modifier.fillMaxSize()) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
                .verticalScroll(scrollState)
        ) {
            Text(
                text = "Self-Assessment",
                style = MaterialTheme.typography.headlineSmall,
                modifier = Modifier.padding(bottom = 12.dp)
            )

            questions.forEachIndexed { index, question ->
                Text(
                    text = "${index + 1}. ${question.question}",
                    style = MaterialTheme.typography.bodyLarge,
                    modifier = Modifier.padding(vertical = 4.dp)
                )

                var expanded by remember { mutableStateOf(false) }

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 8.dp)
                        .border(1.dp, Color.Gray, RoundedCornerShape(8.dp))
                        .clickable { expanded = true }
                        .padding(12.dp)
                ) {
                    Text(text = if (answers[index].isNotEmpty()) answers[index] else "Pilih jawaban")

                    DropdownMenu(
                        expanded = expanded,
                        onDismissRequest = { expanded = false }
                    ) {
                        question.options.forEach { option ->
                            DropdownMenuItem(
                                text = { Text(option) },
                                onClick = {
                                    viewModel.updateAnswer(index, option)
                                    expanded = false
                                }
                            )
                        }
                    }
                }
            }

            Button(
                onClick = {
                    if (answers.any { it.isBlank() }) {
                        Toast.makeText(
                            context,
                            "Silakan jawab semua pertanyaan terlebih dahulu.",
                            Toast.LENGTH_SHORT
                        ).show()
                    } else {
                        viewModel.submitAssessment()
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 12.dp)
            ) {
                Text("Lihat Hasil")
            }

            when (result) {
                is UiState.Success -> {
                    Text(
                        text = "Hasil Analisis:",
                        style = MaterialTheme.typography.titleMedium,
                        modifier = Modifier.padding(top = 16.dp, bottom = 8.dp)
                    )
                    Text((result as UiState.Success).data)
                }
                is UiState.Error -> {
                    Text(
                        text = "Terjadi kesalahan: ${(result as UiState.Error).errorMessage}",
                        color = Color.Red,
                        modifier = Modifier.padding(top = 16.dp)
                    )
                }
                else -> Unit
            }
        }

        if (result is UiState.Loading) {
            Box(
                modifier = Modifier
                    .fillMaxSize(),
                contentAlignment = androidx.compose.ui.Alignment.Center
            ) {
                CircularProgressIndicator()
            }
        }
    }
}