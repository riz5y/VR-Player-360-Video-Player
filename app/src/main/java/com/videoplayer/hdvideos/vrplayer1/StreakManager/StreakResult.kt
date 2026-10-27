package com.videoplayer.hdvideos.vrplayer1.StreakManager

sealed class StreakResult {
    data class StreakProgress(val day: Int)       : StreakResult()
     object StreakCompleted                         : StreakResult()
    data class RewardOngoing(val expiryEpoch: Long): StreakResult()
}