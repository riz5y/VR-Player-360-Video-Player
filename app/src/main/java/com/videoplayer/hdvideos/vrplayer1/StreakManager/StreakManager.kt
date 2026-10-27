package com.videoplayer.hdvideos.vrplayer1.StreakManager

import android.content.Context
import android.content.SharedPreferences
import java.time.LocalDate


class StreakManager(context: Context) {

    private val prefs: SharedPreferences =
        context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)

    companion object {
        const val PREFS_NAME = "StreakPrefs"
        private const val KEY_COUNT = "streak_count"
        private const val KEY_DATE = "last_open_date"
        private const val KEY_EXPIRY = "reward_expiry_epoch"
        const val MAX_DAYS = 3

//        private const val REWARD_MS = 2L * 60 * 1000               // 2 minutes  (testing)

        // private const val REWARD_MS  = 1L * 24 * 60 * 60 * 1000  // 1 day
// private const val REWARD_MS  = 3L * 24 * 60 * 60 * 1000  // 3 days
// private const val REWARD_MS  = 5L * 24 * 60 * 60 * 1000  // 5 days
        private const val REWARD_MS = 7L * 24 * 60 * 60 * 1000  // 7 days
// private const val REWARD_MS  = 30L * 24 * 60 * 60 * 1000
    }


    val streakCount: Int
        get() = prefs.getInt(KEY_COUNT, 0)

    val rewardExpiryEpoch: Long
        get() = prefs.getLong(KEY_EXPIRY, 0L)

    /** True while the 30-day reward window is active */
    val isRewardActive: Boolean
        get() = rewardExpiryEpoch > 0L && System.currentTimeMillis() < rewardExpiryEpoch


    private fun grantReward() {
        prefs.edit()
            .putLong(KEY_EXPIRY, System.currentTimeMillis() + REWARD_MS)
            .putInt(KEY_COUNT, 0)          // reset to 0 — no duplicate
            .remove(KEY_DATE)              // remove date so after expiry next open = day 1
            .apply()
    }


    fun recordOpen(): StreakResult {
        if (isRewardActive) return StreakResult.RewardOngoing(rewardExpiryEpoch)

        val today = LocalDate.now()
        val lastStr = prefs.getString(KEY_DATE, null)
        val currentCount = prefs.getInt(KEY_COUNT, 0)

        val newCount: Int = when {
            lastStr == null -> 1
            else -> {
                val last = LocalDate.parse(lastStr)
                val daysDiff = today.toEpochDay() - last.toEpochDay()
                when {
                    daysDiff == 0L -> currentCount
                    daysDiff == 1L -> currentCount + 1
                    else -> 1
                }
            }
        }

        if (lastStr == null || LocalDate.parse(lastStr) != today) {
            prefs.edit()
                .putInt(KEY_COUNT, newCount)
                .putString(KEY_DATE, today.toString())
                .apply()
        }

        return if (newCount >= MAX_DAYS) {
            // ── DO NOT call grantReward() here anymore ────────────────────
            // Reward is only granted when user explicitly taps "Claim"
            StreakResult.StreakCompleted
        } else {
            StreakResult.StreakProgress(newCount)
        }
    }

    /** Called ONLY when user taps Claim button */
    fun grantRewardOnClaim() {
        prefs.edit()
            .putLong(KEY_EXPIRY, System.currentTimeMillis() + REWARD_MS)
            .putInt(KEY_COUNT, 0)
            .remove(KEY_DATE)
            .apply()
    }


    /** Call when user buys a real plan — wipe streak state so it never re-triggers */
    fun clearOnPurchase() {
        prefs.edit().clear().apply()
    }

    // Add alongside other read-only state properties
    val lastOpenDate: String?
        get() = prefs.getString(KEY_DATE, null)

    // ── TEST HELPERS (remove before release) ─────────────────────────────

    /** Sets streak so next recordOpen() lands on [day] */
    fun forceSetDay(day: Int) {
        prefs.edit()
            .putInt(KEY_COUNT, day - 1)
            .putString(KEY_DATE, LocalDate.now().minusDays(1).toString())
            .putLong(KEY_EXPIRY, 0L)
            .apply()
    }

    /** Wipes all streak state */
    fun resetAll() {
        prefs.edit().clear().apply()
    }

    /** Grants reward immediately without going through 7 days */
    fun forceGrantReward() {
        grantReward()
    }

    /** Expires the reward so normal ad flow resumes */
    fun forceExpireReward() {
        prefs.edit()
            .putLong(KEY_EXPIRY, System.currentTimeMillis() - 1000)
            .apply()
    }

}
