package com.videoplayer.hdvideos.vrplayer1.OnBoarding

import android.content.Context
import androidx.core.content.edit


fun saveOnboarded(context: Context) {
    val sharedPref = context.getSharedPreferences("AppPrefs", Context.MODE_PRIVATE)
    sharedPref.edit { putBoolean("onboarded", true) }
}

fun isOnboarded(context: Context): Boolean {
    val sharedPref = context.getSharedPreferences("AppPrefs", Context.MODE_PRIVATE)
    return sharedPref.getBoolean("onboarded", false)
}

fun saveLanguage(context: Context, language: String) {
    val sharedPref = context.getSharedPreferences("AppPrefs", Context.MODE_PRIVATE)
    sharedPref.edit { putString("language", language) }
}

fun getSavedLanguage(context: Context): String {
    val sharedPref = context.getSharedPreferences("AppPrefs", Context.MODE_PRIVATE)
    return sharedPref.getString("language", "en") ?: "en"
}
