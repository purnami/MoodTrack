package com.example.moodtrack.ui.screen.recomendation

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.example.moodtrack.ui.common.UiState
import com.example.moodtrack.ui.components.RecommendationList
import com.example.moodtrack.ui.components.YouTubePlayer
import com.example.moodtrack.ui.components.parseNumberedPoints
import com.example.moodtrack.ui.viewmodel.RecommendationViewModel
import kotlinx.coroutines.launch


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RecommendationScreen(
    navController: NavController,
    mood: Int,
    note: String,
    viewModel: RecommendationViewModel = hiltViewModel()
) {
    val videoState by viewModel.videoState.collectAsState()
    val musicState by viewModel.musicState.collectAsState()
    val insightState by viewModel.insightState.collectAsState()

    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    val coroutineScope = rememberCoroutineScope()
    var showBottomSheet by remember { mutableStateOf(false) }

    var videoIdToPlay by remember { mutableStateOf<String?>(null) }

    LaunchedEffect(Unit) {
        viewModel.setMood(mood)
        viewModel.setNote(note)
        viewModel.fetchVideosByMood(mood)
        viewModel.fetchMusicsByMood(mood)
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Rekomendasi Kegiatan") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp)
                .verticalScroll(rememberScrollState())
        ) {
            Text(
                "Berdasarkan mood Anda, kami merekomendasikan:",
                style = MaterialTheme.typography.headlineSmall.copy(fontSize = 20.sp)
            )

            Spacer(modifier = Modifier.height(16.dp))

            when (val state = insightState) {
                is UiState.Loading -> {
                    CircularProgressIndicator()
                }
                is UiState.Success -> {
                    RecommendationList(parseNumberedPoints(state.data))
                }
                is UiState.Error -> {
                    Text(
                        text = "Terjadi kesalahan: ${state.errorMessage}",
                        color = Color.Red,
                        style = MaterialTheme.typography.bodySmall
                    )
                }
                else -> {
                    Text("Tidak ada data tersedia", color = Color.Gray)
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            Text("Rekomendasi video meditasi berdasarkan mood Anda:",style = MaterialTheme.typography.headlineSmall.copy(fontSize = 20.sp))

            Spacer(modifier = Modifier.height(16.dp))

            when (val state = videoState) {
                is UiState.Loading -> {
                    CircularProgressIndicator()
                }
                is UiState.Success -> {
                    val videoList = state.data
                    videoList.forEach { video ->
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 6.dp)
                                .clickable {
                                    showBottomSheet = true
                                    videoIdToPlay = video.id.videoId
                                    coroutineScope.launch { sheetState.show() }
                                },
                            elevation = CardDefaults.cardElevation(4.dp),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Row(modifier = Modifier.padding(8.dp)) {
                                AsyncImage(
                                    model = video.snippet.thumbnails.medium.url,
                                    contentDescription = "Thumbnail",
                                    modifier = Modifier
                                        .size(120.dp)
                                        .clip(RoundedCornerShape(8.dp)),
                                    contentScale = ContentScale.Crop
                                )
                                Spacer(modifier = Modifier.width(12.dp))
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(
                                        text = video.snippet.title,
                                        style = MaterialTheme.typography.titleMedium,
                                        maxLines = 2,
                                        overflow = TextOverflow.Ellipsis
                                    )
                                    Spacer(modifier = Modifier.height(4.dp))
                                    Text(
                                        text = video.snippet.channelTitle,
                                        style = MaterialTheme.typography.bodySmall,
                                        color = Color.Gray
                                    )
                                }
                            }
                        }
                    }
                }
                is UiState.Error -> {
                    Text(text = state.errorMessage, color = Color.Red)
                }
                else -> {
                    Text(text = "Tidak ada data tersedia", color = Color.Gray)
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            Text("Rekomendasi musik relaksasi berdasarkan mood Anda:",style = MaterialTheme.typography.headlineSmall.copy(fontSize = 20.sp))

            Spacer(modifier = Modifier.height(16.dp))

            when (val state = musicState) {
                is UiState.Loading -> {
                    CircularProgressIndicator()
                }
                is UiState.Success -> {
                    val musicList = state.data
                    musicList.forEach { music ->
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 6.dp)
                                .clickable {
                                    showBottomSheet = true
                                    videoIdToPlay = music.id.videoId
                                    coroutineScope.launch { sheetState.show() }
                                },
                            elevation = CardDefaults.cardElevation(4.dp),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Row(modifier = Modifier.padding(8.dp)) {
                                AsyncImage(
                                    model = music.snippet.thumbnails.medium.url,
                                    contentDescription = "Thumbnail",
                                    modifier = Modifier
                                        .size(120.dp)
                                        .clip(RoundedCornerShape(8.dp)),
                                    contentScale = ContentScale.Crop
                                )
                                Spacer(modifier = Modifier.width(12.dp))
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(
                                        text = music.snippet.title,
                                        style = MaterialTheme.typography.titleMedium,
                                        maxLines = 2,
                                        overflow = TextOverflow.Ellipsis
                                    )
                                    Spacer(modifier = Modifier.height(4.dp))
                                    Text(
                                        text = music.snippet.channelTitle,
                                        style = MaterialTheme.typography.bodySmall,
                                        color = Color.Gray
                                    )
                                }
                            }
                        }
                    }
                }
                is UiState.Error -> {
                    Text(text = state.errorMessage, color = Color.Red)
                }
                else -> {
                    Text(text = "Tidak ada data tersedia", color = Color.Gray)
                }
            }
        }

        if (showBottomSheet) {
            ModalBottomSheet(
                onDismissRequest = {
                    showBottomSheet = false
                },
                sheetState = sheetState
            ) {
                Column(modifier = Modifier.padding(8.dp)) {
                    Spacer(modifier = Modifier)
                    videoIdToPlay?.let { videoId ->
                        YouTubePlayer(
                            youtubeId = videoId,
                            lifecycleOwner = androidx.compose.ui.platform.LocalLifecycleOwner.current
                        )
                    }
                }
            }
        }
    }
}
