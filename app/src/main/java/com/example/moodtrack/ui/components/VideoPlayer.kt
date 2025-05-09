package com.example.moodtrack.ui.components

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.lifecycle.LifecycleOwner
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.YouTubePlayer
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.listeners.AbstractYouTubePlayerListener
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.views.YouTubePlayerView

@Composable
fun YouTubePlayer(youtubeId: String, lifecycleOwner: LifecycleOwner) {
    AndroidView(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp)
            .clip(RoundedCornerShape(10.dp)),
        factory = {
            YouTubePlayerView(context = it).apply {
                lifecycleOwner.lifecycle.addObserver(this)
                addYouTubePlayerListener(object : AbstractYouTubePlayerListener(){
                    override fun onReady(youTubePlayer: YouTubePlayer) {
                        youTubePlayer.loadVideo(youtubeId, 0f)
                    }
                })
            }
        }
    )
}