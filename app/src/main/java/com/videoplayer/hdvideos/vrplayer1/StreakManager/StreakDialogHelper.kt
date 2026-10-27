package com.videoplayer.hdvideos.vrplayer1.StreakManager

import android.app.Dialog
import android.content.Context
import android.graphics.Color
import android.graphics.Typeface
import android.graphics.drawable.ColorDrawable
import android.graphics.drawable.GradientDrawable
import android.view.Gravity
import android.view.ViewGroup
import android.view.Window
import android.widget.Button
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.ProgressBar
import android.widget.TextView
import com.videoplayer.hdvideos.vrplayer1.R
import com.videoplayer.hdvideos.vrplayer1.ads.PhoneLocatorAppPurchaseHelper


object StreakDialogHelper {

    // ── Called directly from HomeFragment — no recordOpen() inside ────────
    // HomeFragment controls the flow; these just show the dialog.

    fun showProgressOnly(context: Context, day: Int) {
        showProgressDialog(context, day)
    }

    fun showRewardOnly(context: Context, onRewardClaim: (() -> Unit)?) {
        showRewardDialog(context, onRewardClaim)
    }


    fun handleAndShow(context: Context, onRewardClaim: (() -> Unit)? = null) {
        val billing    = PhoneLocatorAppPurchaseHelper(context)
        val isPaidUser = !billing.shouldShowAds()
        if (isPaidUser) return

        val streakMgr = StreakManager(context)

        if (streakMgr.isRewardActive) {
            StreakAdGate.suppressAllAds()
            return
        }

        val result = streakMgr.recordOpen()

        // Re-check after recordOpen in case grantReward() just fired
        if (streakMgr.isRewardActive) {
            showRewardDialog(context, onRewardClaim)
            return
        }

        when (result) {
            is StreakResult.StreakProgress  -> showProgressDialog(context, result.day)
            is StreakResult.RewardOngoing   -> StreakAdGate.suppressAllAds()
            is StreakResult.StreakCompleted  -> showRewardDialog(context, onRewardClaim)
        }
    }

    // ── Private implementations ───────────────────────────────────────────

    private fun showProgressDialog(context: Context, day: Int) {
        val dialog = Dialog(context)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        dialog.setContentView(R.layout.dialog_streak_progress)
        dialog.setCancelable(true)

        val width = (context.resources.displayMetrics.widthPixels * 0.88).toInt()
        dialog.window?.setLayout(width, ViewGroup.LayoutParams.WRAP_CONTENT)

        dialog.findViewById<ProgressBar>(R.id.streakProgressBar).progress = day
        dialog.findViewById<TextView>(R.id.tvStreakSubtitle).text =
            if (day == 2)
                "One more day to go. Come back tomorrow\nto unlock 7 days of VIP access."
            else
                "Open VR Player 3 days in a row to unlock\n7 days of VIP and an ad-free experience."

        buildDayDots(context, dialog.findViewById(R.id.llDayDots), day)

        dialog.findViewById<Button>(R.id.btnStreakClose).setOnClickListener {
            dialog.dismiss()
        }
        dialog.findViewById<ImageView>(R.id.closebtn).setOnClickListener {
            dialog.dismiss()
        }
        dialog.show()
    }
    private fun showRewardDialog(context: Context, onRewardClaim: (() -> Unit)?) {
        val dialog = Dialog(context)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        dialog.setContentView(R.layout.dialog_streak_reward)
        dialog.setCancelable(false)

        val width = (context.resources.displayMetrics.widthPixels * 0.88).toInt()
        dialog.window?.setLayout(width, ViewGroup.LayoutParams.WRAP_CONTENT)

        // Mark the three completed challenge days.
        buildRewardDots(context, dialog.findViewById(R.id.llDayDots))

        dialog.findViewById<Button>(R.id.btnClaimReward).setOnClickListener {
            StreakManager(context).grantRewardOnClaim()
            dialog.dismiss()
            onRewardClaim?.invoke()
        }
        dialog.findViewById<ImageView>(R.id.closebtnreward).setOnClickListener {
            dialog.dismiss()
        }
        dialog.show()
    }

    // All 7 dots filled with orange checkmarks for reward dialog
    private fun buildRewardDots(context: Context, container: LinearLayout) {
        container.removeAllViews()
        val density   = context.resources.displayMetrics.density
        val dotSize   = (32 * density).toInt()
        val dotMargin = (4  * density).toInt()

        for (i in 1..3) {
            val dot = TextView(context).apply {
                text      = "✓"
                textSize  = 14f
                gravity   = Gravity.CENTER
                typeface  = Typeface.DEFAULT_BOLD
                setTextColor(Color.parseColor("#0A0816"))
                background = GradientDrawable().apply {
                    shape = GradientDrawable.OVAL
                    setColor(Color.parseColor("#F5A623"))
                }
                layoutParams = LinearLayout.LayoutParams(dotSize, dotSize).apply {
                    setMargins(dotMargin, 0, dotMargin, 0)
                }
            }
            container.addView(dot)
        }
    }
    private fun buildDayDots(context: Context, container: LinearLayout, completedDays: Int) {
        container.removeAllViews()
        val density   = context.resources.displayMetrics.density
        val dotSize   = (28 * density).toInt()
        val dotMargin = (5  * density).toInt()

        for (i in 1..3) {
            val filled = i <= completedDays
            val dot = TextView(context).apply {
                text      = "$i"
                textSize  = 11f
                gravity   = Gravity.CENTER
                typeface  = Typeface.DEFAULT_BOLD
                setTextColor(if (filled) Color.parseColor("#0A0816") else Color.parseColor("#4B5563"))
                background = GradientDrawable().apply {
                    shape = GradientDrawable.OVAL
                    setColor(Color.parseColor(if (filled) "#F5A623" else "#E1E3E6"))
                }
                layoutParams = LinearLayout.LayoutParams(dotSize, dotSize).apply {
                    setMargins(dotMargin, 0, dotMargin, 0)
                }
            }
            container.addView(dot)
        }
    }
}