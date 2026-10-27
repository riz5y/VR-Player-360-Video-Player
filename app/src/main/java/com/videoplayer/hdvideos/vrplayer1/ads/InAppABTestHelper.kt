package com.videoplayer.hdvideos.vrplayer1.ads

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import com.videoplayer.hdvideos.vrplayer1.InApp.InAppActivity
import com.videoplayer.hdvideos.vrplayer1.InApp.InAppBActivity

object InAppABTestHelper {
    private const val TAG = "ABTesting"
    private const val PREFS_NAME = "app_ab_testing_prefs"
    private const val KEY_INAPP_VARIANT = "key_inapp_variant"
    private const val KEY_INAPP_CYCLE_INDEX = "key_inapp_cycle_index"
    private const val KEY_BANNER_CYCLE_COUNT = "key_banner_cycle_count"
    private const val KEY_MANUAL_OVERRIDE = "key_manual_override"

    private val VARIANTS = listOf("A", "B")

    /**
     * Resolves the active In-App paywall variant ("A", "B", or "C").
     * Supports:
     * 1. Manual QA Selection (via long-press QA dialog)
     * 2. Firebase Remote Config override
     * 3. Automatic sequential rotation (A -> B -> C -> A) so QA and users experience EVERY variant!
     */
    fun getInAppVariant(context: Context): String {
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)

        // 1. Check if user/QA explicitly locked a variant
        val manualOverride = prefs.getString(KEY_MANUAL_OVERRIDE, null)
        if (!manualOverride.isNullOrEmpty() && manualOverride in VARIANTS) {
            PhoneLocatorLoadAds.inAppVariant = manualOverride
            return manualOverride
        }

        // 2. Check if RemoteConfig provided a valid fixed override
        val remoteVariant = PhoneLocatorLoadAds.inAppVariant.trim().uppercase()
        if (remoteVariant in VARIANTS && remoteVariant != prefs.getString(KEY_INAPP_VARIANT, "")) {
            prefs.edit().putString(KEY_INAPP_VARIANT, remoteVariant).apply()
            return remoteVariant
        }

        // 3. Sequential rotation so QA can experience all variants during testing
        val currentIndex = prefs.getInt(KEY_INAPP_CYCLE_INDEX, 0)
        val variant = VARIANTS[currentIndex % VARIANTS.size]

        // Advance to next variant for next visit
        val nextIndex = (currentIndex + 1) % VARIANTS.size
        prefs.edit()
            .putInt(KEY_INAPP_CYCLE_INDEX, nextIndex)
            .putString(KEY_INAPP_VARIANT, variant)
            .apply()

        PhoneLocatorLoadAds.inAppVariant = variant
        AnalyticsHelper.logEvent(context, "ab_test_inapp_assigned", mapOf("variant" to variant))
        return variant
    }

    /**
     * Returns the target Intent based on the active A/B test variant.
     * Shows a brief informative indicator so QA knows which variant is being evaluated.
     */
    fun getInAppIntent(context: Context): Intent {
        val variant = getInAppVariant(context)
        Log.d(TAG, "Serving InApp Paywall Variant: $variant")
        AnalyticsHelper.logEvent(context, "ab_test_inapp_view", mapOf("variant" to variant))

        // Respect Remote Config per-variant kill switches
        val effectiveVariant = when {
            variant == "B" && !PhoneLocatorLoadAds.canShowInAppB -> "A"
            variant == "A" && !PhoneLocatorLoadAds.canShowInAppA -> "B"
            else -> variant
        }
        return when (effectiveVariant) {
            "B" -> Intent(context, InAppBActivity::class.java)
            else -> Intent(context, InAppActivity::class.java)
        }
    }

    /**
     * Resolves Collapsible Banner vs Standard Banner A/B test variant ("collapsible" vs "standard").
     * Alternates on consecutive banner loads so QA testers experience both collapsible
     * and standard banner experiences seamlessly.
     */
    fun isCollapsibleBanner(context: Context): Boolean {
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        val cycleCount = prefs.getInt(KEY_BANNER_CYCLE_COUNT, 0)

        // Alternate: Even counts = Collapsible, Odd counts = Standard
        val isCollapsible = (cycleCount % 2 == 0)
        val variantName = if (isCollapsible) "collapsible" else "standard"

        // Increment for next load/refresh
        prefs.edit().putInt(KEY_BANNER_CYCLE_COUNT, cycleCount + 1).apply()

        Log.d(TAG, "Banner A/B Load #$cycleCount: $variantName (isCollapsible=$isCollapsible)")
        AnalyticsHelper.logEvent(
            context,
            "ab_test_banner_assigned",
            mapOf("variant" to variantName, "load_index" to cycleCount.toString())
        )
        return isCollapsible
    }

    /**
     * QA Dialog: Allows testers to explicitly select Variant A, B, or C,
     * or return to automatic rotation.
     */
    fun showQaVariantPicker(activity: Activity, onSelected: (() -> Unit)? = null) {
        val prefs = activity.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        val current = prefs.getString(KEY_MANUAL_OVERRIDE, "Auto-Rotate")

        val options = arrayOf(
            "Auto-Rotate (A -> B each time)",
            "Lock Variant A (Classic Yellow & Black)",
            "Lock Variant B (Modern Glass Card)"
        )

        AlertDialog.Builder(activity)
            .setTitle("QA Tester: Choose Paywall Variant")
            .setItems(options) { dialog, which ->
                when (which) {
                    0 -> {
                        prefs.edit().remove(KEY_MANUAL_OVERRIDE).apply()
                        Toast.makeText(activity, "Auto-Rotation enabled (A -> B)", Toast.LENGTH_SHORT).show()
                    }
                    1 -> {
                        prefs.edit().putString(KEY_MANUAL_OVERRIDE, "A").apply()
                        PhoneLocatorLoadAds.inAppVariant = "A"
                        Toast.makeText(activity, "Locked to Variant A", Toast.LENGTH_SHORT).show()
                    }
                    2 -> {
                        prefs.edit().putString(KEY_MANUAL_OVERRIDE, "B").apply()
                        PhoneLocatorLoadAds.inAppVariant = "B"
                        Toast.makeText(activity, "Locked to Variant B", Toast.LENGTH_SHORT).show()
                    }
                }
                dialog.dismiss()
                onSelected?.invoke()
            }
            .setNegativeButton("Cancel", null)
            .show()
    }
}
