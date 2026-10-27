package com.videoplayer.hdvideos.vrplayer1.ads

import android.content.Context
import android.content.SharedPreferences
import java.io.File

//object PreferenceHelper {
//    fun isOnboardingDone(context: Context): Boolean {
//        val prefs = context.getSharedPreferences("MyPrefs", Context.MODE_PRIVATE)
//        return prefs.getBoolean("onboarding_done", false)
//    }
//
//    fun isLanguageSelected(context: Context): Boolean {
//        val prefs = context.getSharedPreferences("MyPrefs", Context.MODE_PRIVATE)
//        return prefs.getBoolean("language_selected", false)
//    }
//
//
//    fun setOnboardingDone(context: Context) {
//        val prefs = context.getSharedPreferences("MyPrefs", Context.MODE_PRIVATE)
//        prefs.edit().putBoolean("onboarding_done", true).apply()
//    }
//
//    fun setLanguageSelected(context: Context) {
//        val prefs = context.getSharedPreferences("MyPrefs", Context.MODE_PRIVATE)
//        prefs.edit().putBoolean("language_selected", true).apply()
//    }
//    private const val PREFS_NAME = "marinetracker_prefs"
//    private const val KEY_ONBOARDING_COMPLETE = "onboarding_complete"
//
//    private fun getPrefs(context: Context): SharedPreferences {
//        return context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
//    }
//
//    fun isOnboardingComplete(context: Context): Boolean {
//        return getPrefs(context).getBoolean(KEY_ONBOARDING_COMPLETE, false)
//    }
//
//    fun setOnboardingComplete(context: Context) {
//        getPrefs(context).edit().putBoolean(KEY_ONBOARDING_COMPLETE, true).apply()
//    }
//}


object PreferenceHelper {
    // Existing preferences
    private const val PREFS_NAME_GENERAL = "MyPrefs"
    private const val PREFS_NAME_MARINE = "marinetracker_prefs"
    private const val PREFS_NAME_DOWNLOADS = "DownloadActivityPrefs"  // ✅ Added

    private const val KEY_ONBOARDING_DONE = "onboarding_done"
    private const val KEY_LANGUAGE_SELECTED = "language_selected"
    private const val KEY_ONBOARDING_COMPLETE = "onboarding_complete"
    private const val KEY_DELETED_PATHS = "deletedPaths"  // ✅ Added

    // ─────────────────────────────────────────────────────────────────────────
    // General Preferences
    // ─────────────────────────────────────────────────────────────────────────

    fun isOnboardingDone(context: Context): Boolean {
        val prefs = context.getSharedPreferences(PREFS_NAME_GENERAL, Context.MODE_PRIVATE)
        return prefs.getBoolean(KEY_ONBOARDING_DONE, false)
    }

    fun isLanguageSelected(context: Context): Boolean {
        val prefs = context.getSharedPreferences(PREFS_NAME_GENERAL, Context.MODE_PRIVATE)
        return prefs.getBoolean(KEY_LANGUAGE_SELECTED, false)
    }

    fun setOnboardingDone(context: Context) {
        val prefs = context.getSharedPreferences(PREFS_NAME_GENERAL, Context.MODE_PRIVATE)
        prefs.edit().putBoolean(KEY_ONBOARDING_DONE, true).apply()
    }

    fun setLanguageSelected(context: Context) {
        val prefs = context.getSharedPreferences(PREFS_NAME_GENERAL, Context.MODE_PRIVATE)
        prefs.edit().putBoolean(KEY_LANGUAGE_SELECTED, true).apply()
    }



    // ─────────────────────────────────────────────────────────────────────────
    // ✅ Download Activity Preferences (Deleted Videos)
    // ─────────────────────────────────────────────────────────────────────────

    private fun getDownloadPrefs(context: Context): SharedPreferences {
        return context.getSharedPreferences(PREFS_NAME_DOWNLOADS, Context.MODE_PRIVATE)
    }

    /**
     * Get the set of deleted video file paths
     */
    fun getDeletedVideoPaths(context: Context): MutableSet<String> {
        return getDownloadPrefs(context)
            .getStringSet(KEY_DELETED_PATHS, emptySet())
            ?.toMutableSet() ?: mutableSetOf()
    }

    /**
     * Save a deleted video path to persistent storage
     */
    fun saveDeletedVideoPath(context: Context, path: String) {
        val deletedPaths = getDeletedVideoPaths(context)
        deletedPaths.add(path)
        getDownloadPrefs(context)
            .edit()
            .putStringSet(KEY_DELETED_PATHS, deletedPaths)
            .apply()
    }

    /**
     * Remove a video path from deleted list (if user wants to restore it)
     */
    fun removeDeletedVideoPath(context: Context, path: String) {
        val deletedPaths = getDeletedVideoPaths(context)
        deletedPaths.remove(path)
        getDownloadPrefs(context)
            .edit()
            .putStringSet(KEY_DELETED_PATHS, deletedPaths)
            .apply()
    }

    /**
     * Clear all deleted video paths
     */
    fun clearDeletedVideoPaths(context: Context) {
        getDownloadPrefs(context)
            .edit()
            .remove(KEY_DELETED_PATHS)
            .apply()
    }

    /**
     * Clean up deleted paths that no longer exist in the filesystem
     * Returns the number of paths cleaned up
     */
    fun cleanupDeletedVideoPaths(context: Context): Int {
        val deletedPaths = getDeletedVideoPaths(context)
        val existingPaths = deletedPaths.filter { File(it).exists() }.toSet()

        val cleanedCount = deletedPaths.size - existingPaths.size

        if (cleanedCount > 0) {
            getDownloadPrefs(context)
                .edit()
                .putStringSet(KEY_DELETED_PATHS, existingPaths)
                .apply()
        }

        return cleanedCount
    }

    /**
     * Check if a video path is marked as deleted
     */
    fun isVideoDeleted(context: Context, path: String): Boolean {
        return getDeletedVideoPaths(context).contains(path)
    }
}