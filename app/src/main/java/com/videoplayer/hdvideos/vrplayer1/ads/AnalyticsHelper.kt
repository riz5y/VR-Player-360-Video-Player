package com.videoplayer.hdvideos.vrplayer1.ads

import android.content.Context
import android.os.Bundle
import android.util.Log
import com.google.firebase.analytics.FirebaseAnalytics

object AnalyticsHelper {
    private const val TAG = "AppAnalytics"

    /**
     * Logs a dedicated screen view event for Firebase Analytics.
     * Tells Google Analytics exactly which screen the user is viewing,
     * without confusing intermediate cross-screen transition events.
     */
    fun logScreen(context: Context, screenName: String) {
        try {
            val firebaseAnalytics = FirebaseAnalytics.getInstance(context)
            val bundle = Bundle().apply {
                putString(FirebaseAnalytics.Param.SCREEN_NAME, screenName)
                putString(FirebaseAnalytics.Param.SCREEN_CLASS, screenName)
            }
            firebaseAnalytics.logEvent(FirebaseAnalytics.Event.SCREEN_VIEW, bundle)

            // Dedicated event identifier (clean format conforming to Firebase rules: max 40 chars, alphanumeric/underscore)
            val eventName = if (screenName.startsWith("screen_")) {
                screenName.take(40)
            } else {
                ("screen_" + screenName.lowercase().replace("activity", "").replace(" ", "_")).take(40)
            }
            firebaseAnalytics.logEvent(eventName, bundle)
            Log.d(TAG, "Screen logged: $screenName -> event: $eventName")
        } catch (e: Exception) {
            Log.e(TAG, "Error logging screen $screenName: ${e.message}")
        }
    }

    /**
     * Logs custom analytics events with parameters.
     */
    fun logEvent(context: Context, eventName: String, params: Map<String, String>? = null) {
        try {
            val firebaseAnalytics = FirebaseAnalytics.getInstance(context)
            val bundle = Bundle()
            params?.forEach { (k, v) ->
                bundle.putString(k, v)
            }
            val sanitized = eventName.replace(" ", "_").take(40)
            firebaseAnalytics.logEvent(sanitized, bundle)
            Log.d(TAG, "Event logged: $sanitized with $params")
        } catch (e: Exception) {
            Log.e(TAG, "Error logging event $eventName: ${e.message}")
        }
    }
}
