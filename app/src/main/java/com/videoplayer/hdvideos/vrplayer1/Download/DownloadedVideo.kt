package com.videoplayer.hdvideos.vrplayer1.Download

data class DownloadedVideo(
    val title: String,
    val filePath: String,
    val duration: String,
    val fileSize: String,
    val thumbnailPath: String?
)