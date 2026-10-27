package com.videoplayer.hdvideos.vrplayer1.Reels.model

import java.io.Serializable


data class TrendingVideoModel(
    var title: String,
    var category: String,
    val thumnail: String,
    val videoUrl: String,
    val duration: String,
    val description: String
) : Serializable