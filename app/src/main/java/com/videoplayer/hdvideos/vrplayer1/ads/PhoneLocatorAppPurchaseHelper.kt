package com.videoplayer.hdvideos.vrplayer1.ads

import android.content.Context
import android.content.SharedPreferences

class PhoneLocatorAppPurchaseHelper(content: Context?) {
    private val billingPreferences: SharedPreferences

    init {
        billingPreferences = content!!.getSharedPreferences("PurchasePrefs", Context.MODE_PRIVATE)
    }

    fun shouldShowAds(): Boolean {
        //return !(billingPreferences.getBoolean("ads_purchase", false));
//        return !((billingPreferences.getBoolean("one_weekly", false) || (billingPreferences.getBoolean("one_annual", false)) || (billingPreferences.getBoolean("one_monthly", false))))
        return !((billingPreferences.getBoolean("one_weekly", false)
                || (billingPreferences.getBoolean("one_annual", false))
                || (billingPreferences.getBoolean("one_monthly", false))
                || (billingPreferences.getBoolean("one_annually", false))))
    }
}
