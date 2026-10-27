package com.videoplayer.hdvideos.vrplayer1.ads

import android.app.Activity
import android.app.Application
import android.graphics.Color
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.ProcessLifecycleOwner
import com.google.android.gms.ads.AdError
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.FullScreenContentCallback
import com.google.android.gms.ads.LoadAdError
import com.google.android.gms.ads.appopen.AppOpenAd
import com.videoplayer.hdvideos.vrplayer1.Detail.DetailActivity
import com.videoplayer.hdvideos.vrplayer1.ads.PhoneLocatorLoadAds.shouldGoForAds
import com.videoplayer.hdvideos.vrplayer1.Splash.SplashActivity
import java.util.Date

class AppOpenAdResume(private val myApplicationCleaner: MyApp) : DefaultLifecycleObserver,
    Application.ActivityLifecycleCallbacks {

    private val TAG = "StartAdLogs:"
    private var startOpenAd: AppOpenAd? = null
    private var callBackLoading: AppOpenAd.AppOpenAdLoadCallback? = null
    private var runningActivity: Activity? = null
    private var adLoadingTime: Long = 0
    private var adWhiteView: View? = null

    companion object {
        private var isDisplayingAd = false
    }

    init {
        try {
            myApplicationCleaner.registerActivityLifecycleCallbacks(this)
            ProcessLifecycleOwner.get().lifecycle.addObserver(this)
        } catch (e: Exception) {
        }
    }

    private fun addWhiteAdsView() {
        runningActivity?.let { activity ->
            if (adWhiteView == null) {
                adWhiteView = View(activity).apply {
                    layoutParams = ViewGroup.LayoutParams(
                        ViewGroup.LayoutParams.MATCH_PARENT,
                        ViewGroup.LayoutParams.MATCH_PARENT
                    )
                    setBackgroundColor(Color.WHITE)
                    visibility = View.GONE
                }
                (activity.window.decorView as ViewGroup).addView(adWhiteView)
            }
        }
    }

    private fun showWhiteAdsView() {
        addWhiteAdsView()
        adWhiteView?.visibility = View.VISIBLE
    }

    private fun hideWhiteAdsView() {
        adWhiteView?.visibility = View.GONE
    }

    private fun showCleanerStartAppOpenAd() {

        // Master switch check
        if (!PhoneLocatorLoadAds.canShowAppOpen) {
            Log.d(TAG, "showCleanerStartAppOpenAd: blocked — canShowAppOpen is false")
            startOpenAd = null
            return
        }

        val activity = runningActivity ?: return
        val ad = startOpenAd ?: return

        if (isDisplayingAd) return

        if (!activity.hasWindowFocus()) {
            Handler(Looper.getMainLooper()).postDelayed({
                showCleanerStartAppOpenAd()
            }, 200)
            return
        }

        ad.fullScreenContentCallback = object : FullScreenContentCallback() {

            override fun onAdDismissedFullScreenContent() {
                startOpenAd = null
                isDisplayingAd = false
                hideWhiteAdsView()
                loadCleanerStartAppOpenAd()
            }

            override fun onAdFailedToShowFullScreenContent(e: AdError) {
                hideWhiteAdsView()
                isDisplayingAd = false
            }

            override fun onAdShowedFullScreenContent() {
                isDisplayingAd = true
            }
        }

        hideWhiteAdsView()
        ad.show(activity)
        Log.d(TAG, "AppOpenAd: showing")
    }

    fun loadCleanerStartAppOpenAd() {

        // Master switch check — don't load if disabled
        if (!PhoneLocatorLoadAds.canShowAppOpen) {
            Log.d(TAG, "loadCleanerStartAppOpenAd: skipped — canShowAppOpen is false")
            return
        }

        Log.d(TAG, "Ad requesting App Open Ad ")
        callBackLoading = object : AppOpenAd.AppOpenAdLoadCallback() {
            override fun onAdLoaded(p0: AppOpenAd) {
                super.onAdLoaded(p0)
                Log.d(TAG, "onAppOpenAdLoaded")
                this@AppOpenAdResume.startOpenAd = p0
                adLoadingTime = Date().time
            }

            override fun onAdFailedToLoad(p0: LoadAdError) {
                super.onAdFailedToLoad(p0)
                Log.d(TAG, "onAppOpenAdFailedToLoad : Error : $p0")
            }
        }
        val purchaseHelper = PhoneLocatorAppPurchaseHelper(myApplicationCleaner)
        if (purchaseHelper.shouldShowAds()) {
            try {
                AppOpenAd.load(
                    myApplicationCleaner,
                    PhoneLocatorLoadAds.app_open_ad_id_admob,
                    AdRequest.Builder().build(),
                    callBackLoading!!
                )
            } catch (e: Exception) {
            }
        }
        Log.d(TAG, "loadStartAppOpenAd: Purchased")
    }

    override fun onActivityCreated(activity: Activity, bundle: Bundle?) {}
    override fun onActivityPaused(activity: Activity) {}
    override fun onActivityStopped(activity: Activity) {}
    override fun onActivitySaveInstanceState(activity: Activity, bundle: Bundle) {}

    override fun onActivityStarted(activity: Activity) {
        runningActivity = activity
    }

    override fun onActivityResumed(activity: Activity) {
        runningActivity = activity
    }

    override fun onActivityDestroyed(activity: Activity) {
        runningActivity = null
    }

    fun clearAllOpenAd() {
        Log.d(TAG, "clearAllOpenAd: ")
        startOpenAd = null
    }

    override fun onStart(owner: LifecycleOwner) {
        Log.d(
            TAG,
            "onStart — canShowAppOpen=${PhoneLocatorLoadAds.canShowAppOpen} startOpenAd=$startOpenAd"
        )

        if (!PhoneLocatorLoadAds.canShowAppOpen) {
            Log.d(TAG, "onStart — canShowAppOpen is false, clearing loaded ad")
            startOpenAd = null
            return
        }

        if (shouldGoForAds
            && PhoneLocatorLoadAds.should_show_app_open
            && runningActivity !is SplashActivity && runningActivity !is DetailActivity
        ) {
            Log.d(TAG, "if onStart — showing app open")
            showCleanerStartAppOpenAd()
        } else {
            Log.d(TAG, "else onStart — loading ad")
            if (PhoneLocatorLoadAds.should_show_app_open) {
                loadCleanerStartAppOpenAd()
            }
        }
    }
}

