package com.videoplayer.hdvideos.vrplayer1.ads

import android.app.Activity
import android.app.Dialog
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.view.Window
import android.widget.ImageView
import android.widget.TextView
import com.videoplayer.hdvideos.vrplayer1.OnBoarding.topCrop
import com.videoplayer.hdvideos.vrplayer1.R

object IapPopupHelper {
    private var iapDialog: Dialog? = null

    fun showIapPopup(activity: Activity, onDismiss: (() -> Unit)? = null) {
        if (activity.isFinishing || activity.isDestroyed) return
        if (iapDialog?.isShowing == true) return

        AnalyticsHelper.logEvent(activity, "iap_popup_shown")

        iapDialog = Dialog(activity).apply {
            requestWindowFeature(Window.FEATURE_NO_TITLE)
            setContentView(R.layout.dialog_iap_popup)
            window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
            window?.setLayout(
                (activity.resources.displayMetrics.widthPixels * 0.88).toInt(),
                android.view.ViewGroup.LayoutParams.WRAP_CONTENT
            )
            setCancelable(true)

            findViewById<ImageView>(R.id.ivIapHero)?.topCrop()

            findViewById<android.view.View>(R.id.btnCloseIapPopup)?.setOnClickListener {
                dismiss()
                onDismiss?.invoke()
            }

            findViewById<ImageView>(R.id.ivIapGift)?.setOnLongClickListener {
                InAppABTestHelper.showQaVariantPicker(activity)
                true
            }

            findViewById<TextView>(R.id.tvMaybeLater)?.setOnClickListener {
                dismiss()
                onDismiss?.invoke()
            }

            findViewById<TextView>(R.id.btnUnlockVip)?.setOnClickListener {
                AnalyticsHelper.logEvent(activity, "iap_popup_unlock_clicked")
                dismiss()
                activity.startActivity(InAppABTestHelper.getInAppIntent(activity))
            }

            setOnDismissListener {
                iapDialog = null
                onDismiss?.invoke()
            }
        }
        try {
            iapDialog?.show()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}
