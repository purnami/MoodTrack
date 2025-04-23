package com.example.moodtrack.data.remote.response

data class AudioDbResponse(
    val track: List<Track>
)

data class Track(
    val strTrack: String,
    val strArtist: String,
    val strMusicVid: String?
)