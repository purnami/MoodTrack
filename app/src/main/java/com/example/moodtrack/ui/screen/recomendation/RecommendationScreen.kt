package com.example.moodtrack.ui.screen.recomendation

import android.util.Log
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
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
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Bathtub
import androidx.compose.material.icons.filled.Create
import androidx.compose.material.icons.filled.FitnessCenter
import androidx.compose.material.icons.filled.Group
import androidx.compose.material.icons.filled.MusicNote
import androidx.compose.material.icons.filled.Nature
import androidx.compose.material.icons.filled.Palette
import androidx.compose.material.icons.filled.RunCircle
import androidx.compose.material3.Card
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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.example.moodtrack.core.utils.toMoodLabel
import com.example.moodtrack.ui.common.UiState
import com.example.moodtrack.ui.components.RecommendationList
import com.example.moodtrack.ui.components.YouTubePlayer
import com.example.moodtrack.ui.components.parseBulletPoints
import com.example.moodtrack.ui.components.parseInsightText
import com.example.moodtrack.ui.components.parseNumberedPoints
import com.example.moodtrack.ui.viewmodel.RecommendationViewModel
import kotlinx.coroutines.launch
import kotlinx.serialization.json.JsonNull.content

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RecommendationScreen(
    navController: NavController,
    mood: Int,
    note: String,
    viewModel: RecommendationViewModel = hiltViewModel()
) {
    val videoState by viewModel.videoState.collectAsState()
    val insightState by viewModel.insightState.collectAsState()

    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    val coroutineScope = rememberCoroutineScope()
    var showBottomSheet by remember { mutableStateOf(false) }

    var videoIdToPlay by remember { mutableStateOf<String?>(null) }

    LaunchedEffect(Unit) {
        viewModel.setMood(mood)
        viewModel.setNote(note)
//        viewModel.analyzeMood(mood.toMoodLabel(), note)
        viewModel.fetchVideosByMood("AIzaSyAUnKlATFOkY_QO6Y0sACMNfTvuJIqYTuI", mood)
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
            Text("Berdasarkan mood Anda, kami merekomendasikan:", style = MaterialTheme.typography.headlineSmall)

            Spacer(modifier = Modifier.height(16.dp))

            when (val state = insightState) {
                is UiState.Loading -> {
                    CircularProgressIndicator()
                }
                is UiState.Success -> {
//                    Text(
//                        text = state.data,
//                        style = MaterialTheme.typography.bodyLarge,
//                        modifier = Modifier.padding(8.dp)
//                    )
//                    val parsedInsight = parseInsightText(state.data)
//                    RecommendationList(parseBulletPoints2(content))
                    val insights = parseNumberedPoints(state.data)

                    RecommendationList(parseNumberedPoints(state.data))
//                    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
//                        insights.forEach { annotated ->
//                            Text(text = annotated)
//                        }
//                    }
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

//            Text("Rekomendasi kegiatan untuk meredakan stres:", style = MaterialTheme.typography.headlineSmall)
//
//            Spacer(modifier = Modifier.height(16.dp))
//
//            // Rekomendasi kegiatan
//            val activities = listOf(
//                Pair("Berjalan di Alam", Icons.Default.Nature),
//                Pair("Meditasi atau Yoga", Icons.Default.FitnessCenter),
//                Pair("Mendengarkan Musik", Icons.Default.MusicNote),
//                Pair("Menulis Jurnal", Icons.Default.Create),
//                Pair("Mandi Air Hangat", Icons.Default.Bathtub),
//                Pair("Berkumpul dengan Teman", Icons.Default.Group),
//                Pair("Melakukan Hobi", Icons.Default.Palette),
//                Pair("Olahraga Ringan", Icons.Default.RunCircle)
//            )
//
//            activities.forEach { activity ->
//                Row(
//                    modifier = Modifier
//                        .fillMaxWidth()
//                        .padding(vertical = 8.dp)
//                        .clickable { /* Handle klik jika diperlukan */ }
//                        .padding(16.dp),
//                    verticalAlignment = Alignment.CenterVertically
//                ) {
//                    Icon(
//                        imageVector = activity.second,
//                        contentDescription = activity.first,
//                        modifier = Modifier.size(30.dp),
//                        tint = MaterialTheme.colorScheme.primary
//                    )
//                    Spacer(modifier = Modifier.width(16.dp))
//                    Text(
//                        text = activity.first,
//                        style = MaterialTheme.typography.bodyLarge,
//                        modifier = Modifier.weight(1f)
//                    )
//                }
//            }
//
//            Spacer(modifier = Modifier.height(16.dp))

            Text("Rekomendasi video meditasi berdasarkan mood Anda:", style = MaterialTheme.typography.headlineSmall)

            Spacer(modifier = Modifier.height(16.dp))

            when (val state = videoState) {
                is UiState.Loading -> {
                    CircularProgressIndicator()
                }
                is UiState.Success -> {
                    val videoList = state.data
                    videoList.forEach { video ->
                        Card(modifier = Modifier.padding(8.dp).clickable {
                            Log.d("CardClick", "Card diklik! Video ID: ${video.id.videoId}")
                            showBottomSheet = true
                            videoIdToPlay = video.id.videoId
                            coroutineScope.launch { sheetState.show() }
                        }) {
                            Column(modifier = Modifier.padding(8.dp)) {
                                Text(text = video.snippet.title, style = MaterialTheme.typography.titleLarge)
                                Text(
                                    text = video.snippet.channelTitle,
                                    style = MaterialTheme.typography.bodySmall,
                                    color = Color.Gray
                                )
                                Spacer(modifier = Modifier.height(8.dp))
                                AsyncImage(
                                    model = video.snippet.thumbnails.medium.url,
                                    contentDescription = "Thumbnail",
                                    modifier = Modifier.fillMaxWidth().height(180.dp),
                                    contentScale = ContentScale.Crop
                                )
                                Spacer(modifier = Modifier.height(8.dp))
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


//@OptIn(ExperimentalMaterial3Api::class)
//@Composable
//fun RecommendationScreen(
//    navController: NavController,
//    mood: Int,
//    note: String,
//    viewModel: RecommendationViewModel = hiltViewModel()
//) {
//    val videoState by viewModel.videoState.collectAsState()
//    val insightState by viewModel.insightState.collectAsState()
//
//    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
//    val coroutineScope = rememberCoroutineScope()
//    var showBottomSheet by remember { mutableStateOf(false) }
//
//    var videoIdToPlay by remember { mutableStateOf<String?>(null) }
//
//    LaunchedEffect(Unit) {
//        viewModel.analyzeMood(mood.toMoodLabel(), note)
//        viewModel.fetchVideosByMood("AIzaSyAUnKlATFOkY_QO6Y0sACMNfTvuJIqYTuI", mood)
//    }
//
//    Scaffold(
//        topBar = {
//            TopAppBar(
//                title = { Text("Rekomendasi Kegiatan") },
//                navigationIcon = {
//                    IconButton(onClick = { navController.popBackStack() }) {
//                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
//                    }
//                }
//            )
//        }
//    ) { paddingValues ->
//        Column(
//            modifier = Modifier
//                .fillMaxSize()
//                .padding(paddingValues)
//                .padding(16.dp)
//                .verticalScroll(rememberScrollState())
//        ) {
//            Text("Berdasarkan mood Anda, kami merekomendasikan:", style = MaterialTheme.typography.headlineSmall)
//
//            Spacer(modifier = Modifier.height(16.dp))
//
//            when (val state = insightState) {
//                is UiState.Loading -> {
//                    CircularProgressIndicator()
//                }
//                is UiState.Success -> {
//                    Text(
//                        text = state.data,
//                        style = MaterialTheme.typography.bodyLarge,
//                        modifier = Modifier.padding(8.dp)
//                    )
//                }
//                is UiState.Error -> {
//                    Text(
//                        text = "Terjadi kesalahan: ${state.errorMessage}",
//                        color = Color.Red,
//                        style = MaterialTheme.typography.bodySmall
//                    )
//                }
//                else -> {
//                    Text("Tidak ada data tersedia", color = Color.Gray)
//                }
//            }
//
//            Spacer(modifier = Modifier.height(16.dp))
//
//            Text("Rekomendasi video meditasi berdasarkan mood Anda:", style = MaterialTheme.typography.headlineSmall)
//
//            Spacer(modifier = Modifier.height(16.dp))
//
//            when (val state = videoState) {
//                is UiState.Loading -> {
//                    CircularProgressIndicator()
//                }
//                is UiState.Success -> {
//                    val videoList = state.data
//                    videoList.forEach { video ->
//                        Card(modifier = Modifier.padding(8.dp).clickable {
//                            Log.d("CardClick", "Card diklik! Video ID: ${video.id.videoId}")
//                            showBottomSheet = true
//                            videoIdToPlay = video.id.videoId
//                            coroutineScope.launch { sheetState.show() }
//                        }) {
//                            Column(modifier = Modifier.padding(8.dp)) {
//                                Text(text = video.snippet.title, style = MaterialTheme.typography.titleLarge)
//                                Text(
//                                    text = video.snippet.channelTitle,
//                                    style = MaterialTheme.typography.bodySmall,
//                                    color = Color.Gray
//                                )
//                                Spacer(modifier = Modifier.height(8.dp))
//                                AsyncImage(
//                                    model = video.snippet.thumbnails.medium.url,
//                                    contentDescription = "Thumbnail",
//                                    modifier = Modifier.fillMaxWidth().height(180.dp),
//                                    contentScale = ContentScale.Crop
//                                )
//                                Spacer(modifier = Modifier.height(8.dp))
//                            }
//                        }
//                    }
//                }
//                is UiState.Error -> {
//                    Text(text = state.errorMessage, color = Color.Red)
//                }
//                else -> {
//                    Text(text = "Tidak ada data tersedia", color = Color.Gray)
//                }
//            }
//        }
//
//        if (showBottomSheet) {
//            ModalBottomSheet(
//                onDismissRequest = {
//                    showBottomSheet = false
//                },
//                sheetState = sheetState
//            ) {
//                Column(modifier = Modifier.padding(8.dp)) {
//                    Spacer(modifier = Modifier)
//                    videoIdToPlay?.let { videoId ->
//                        YouTubePlayer(
//                            youtubeId = videoId,
//                            lifecycleOwner = androidx.compose.ui.platform.LocalLifecycleOwner.current
//                        )
//                    }
//                }
//            }
//        }
//    }
//}