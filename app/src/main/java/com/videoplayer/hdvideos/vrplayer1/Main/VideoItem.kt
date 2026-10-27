package com.videoplayer.hdvideos.vrplayer1.Main

data class VideoItem(
    val title: String,
    val thumbnail: String,
    val videoUrl: String,
    val timeDuration: String,
    val category: String,
    val description: String = ""
)
