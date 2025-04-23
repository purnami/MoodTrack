package com.example.moodtrack.ui.screen.statistics

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.moodtrack.core.utils.MoodPeriod
import com.example.moodtrack.ui.viewmodel.MoodStatisticsViewModel
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.material3.pulltorefresh.rememberPullToRefreshState
import androidx.compose.ui.text.font.FontWeight
import com.example.moodtrack.ui.common.UiState
import com.example.moodtrack.ui.components.MoodChart
import com.example.moodtrack.ui.components.RecommendationList
import com.example.moodtrack.ui.components.parseBulletPoints
import com.example.moodtrack.ui.components.parseInsightText

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MoodStatisticsScreen(
    viewModel: MoodStatisticsViewModel = hiltViewModel()
) {
    val moodData by viewModel.moodData.collectAsState()
    val selectedPeriod by viewModel.selectedPeriod.collectAsState()
    val isRefreshing by viewModel.isRefreshing.collectAsState()
    val moodInsight by viewModel.moodInsight.collectAsState()

    val pullRefreshState = rememberPullToRefreshState()

    PullToRefreshBox(
        modifier = Modifier,
        state = pullRefreshState,
        isRefreshing = isRefreshing,
        onRefresh = { viewModel.refreshMoodData() },
    ) {
        val scrollState = rememberScrollState()

        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(scrollState)
                .padding(16.dp)
        ) {
            Text(text = "Grafik Perubahan Mood", style = MaterialTheme.typography.titleLarge)
            Spacer(modifier = Modifier.height(8.dp))

            var expanded by remember { mutableStateOf(false) }

            Box {
                OutlinedButton(onClick = { expanded = true }) {
                    Text(selectedPeriod.label)
                }

                DropdownMenu(
                    expanded = expanded,
                    onDismissRequest = { expanded = false }
                ) {
                    MoodPeriod.entries.forEach { period ->
                        DropdownMenuItem(
                            text = { Text(period.label) },
                            onClick = {
                                expanded = false
                                viewModel.setPeriod(period)
                            }
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            if (moodData.isNotEmpty()) {
                MoodChart(moodData)
            } else {
                Text("Tidak ada data mood tersedia.")
            }

            Spacer(modifier = Modifier.height(24.dp))

            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                elevation = CardDefaults.cardElevation(4.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "Insight dari Mood Kamu",
                        style = MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.primary
                    )
                    Spacer(modifier = Modifier.height(8.dp))

                    when (moodInsight) {
                        is UiState.Success -> {
                            val parsedInsight = parseInsightText((moodInsight as UiState.Success).data)

                            Column {
                                parsedInsight.forEach { (title, content) ->
                                    Text(
                                        text = title,
                                        style = MaterialTheme.typography.titleSmall.copy(
                                            fontWeight = FontWeight.Bold
                                        ),
                                        color = MaterialTheme.colorScheme.primary
                                    )
                                    Spacer(modifier = Modifier.height(4.dp))

                                    if (title.contains("Rekomendasi", ignoreCase = true)) {
                                        RecommendationList(parseBulletPoints(content))
                                    } else {
                                        Text(
                                            text = content,
                                            style = MaterialTheme.typography.bodyMedium
                                        )
                                    }
                                    Spacer(modifier = Modifier.height(16.dp))
                                }
                            }
                        }

                        is UiState.Error -> {
                            Text(
                                text = (moodInsight as UiState.Error).errorMessage,
                                color = MaterialTheme.colorScheme.error,
                                style = MaterialTheme.typography.bodyMedium
                            )
                        }

                        is UiState.Loading -> {
                            CircularProgressIndicator(modifier = Modifier.size(24.dp))
                        }

                        UiState.Idle -> {
                            Text(
                                text = "Belum ada insight tersedia.",
                                style = MaterialTheme.typography.bodyMedium
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(200.dp))
        }
    }
}