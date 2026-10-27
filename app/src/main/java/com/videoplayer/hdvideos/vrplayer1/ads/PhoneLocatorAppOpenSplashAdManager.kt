package com.videoplayer.hdvideos.vrplayer1.ads

import android.app.Activity
import android.app.Application
import android.os.Bundle
import android.util.Log
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleObserver
import androidx.lifecycle.OnLifecycleEvent
import androidx.lifecycle.ProcessLifecycleOwner
import com.google.android.gms.ads.AdError
import com.google.android.gms.ads.FullScreenContentCallback
import com.google.android.gms.ads.appopen.AppOpenAd
import com.videoplayer.hdvideos.vrplayer1.Splash.SplashActivity
import java.util.*

class PhoneLocatorAppOpenSplashAdManager(private val myApplication: MyApp) : LifecycleObserver,
    Application.ActivityLifecycleCallbacks {
    private val TAG = "StartAdLogs: Splashzzzz"

    private var callBackLoading: AppOpenAd.AppOpenAdLoadCallback? = null
    private var runningActivity: Activity? = null
    private var adLoadingTime: Long = 0

    companion object {
        public var startOpenAd: AppOpenAd? = null
        private var isDisplayingAd = false
    }

    init {
        myApplication.registerActivityLifecycleCallbacks(this)
        ProcessLifecycleOwner.get().lifecycle.addObserver(this)
    }

    fun showStreetViewTruckCTStartAppOpenAd(callBack:SplashShowAppOpenCallback) {
        if (runningActivity is SplashActivity){
            if (!isDisplayingAd && isStartAdAvailable) {
                val fullScreenContentCallback: FullScreenContentCallback =
                    object : FullScreenContentCallback() {
                        override fun onAdDismissedFullScreenContent() {
                            startOpenAd = null
                            isDisplayingAd = false
                            /* if (StreetViewTruckCTLoadAds.should_show_app_open) {
                                 //loadStreetViewTruckCTStartAppOpenAd()
                             }*/
                            PhoneLocatorShowAds.logAnalyticsForClicks("SplashAdDismissedFullScreenContent",myApplication)
                            callBack.onAdDismissedFullScreenContent()
                        }

                        override fun onAdFailedToShowFullScreenContent(adError: AdError) {
                            callBack.onAdFailedToShowFullScreenContent()
                        }

                        override fun onAdShowedFullScreenContent() {
                            PhoneLocatorShowAds.logAnalyticsForClicks("SplashAdShowedFullScreenContent",myApplication)
                            isDisplayingAd = true
                        }
                    }
                startOpenAd!!.show(runningActivity!!)
                Log.d(TAG, "showStartAppOpenAd: Showing App open ad")
                startOpenAd!!.fullScreenContentCallback = fullScreenContentCallback
            } else {
                Log.d(TAG, "Can not show ad.")
                /*if (StreetViewTruckCTLoadAds.should_show_app_open) {
                    //loadStreetViewTruckCTStartAppOpenAd()
                }*/
                callBack.onAdFailedToShowFullScreenContent()
            }
        }
    }

//    fun loadStreetViewTruckCTStartAppOpenAd(/*callBack:SplashLoadAppOpenCallback*/) {
//        if (isStartAdAvailable) {
//           // callBack.onAdCallBack()
//            Log.d(TAG, "Ad is available App Open Ad ")
//        } else {
//            Log.d(TAG, "Ad requesting App Open Ad ")
//            callBackLoading = object : AppOpenAd.AppOpenAdLoadCallback() {
//                override fun onAdLoaded(p0: AppOpenAd) {
//                    super.onAdLoaded(p0)
//                    Log.d(TAG, "onAppOpenAdLoaded")
//                    PhoneLocatorShowAds.logAnalyticsForClicks("SplashAdLoaded",myApplication)
//                    startOpenAd = p0
//                    adLoadingTime = Date().time
//                   // callBack.onAdCallBack()
//                }
//
//                override fun onAdFailedToLoad(p0: LoadAdError) {
//                    super.onAdFailedToLoad(p0)
//                    Log.d(TAG, "onAppOpenAdFailedToLoad : Error : $p0")
//                    PhoneLocatorShowAds.logAnalyticsForClicks("SplashAdFailedToLoad",myApplication)
//                   // callBack.onAdCallBack()
//                }
//            }
//            val billingHelper =
//                PhoneLocatorAppPurchaseHelper(
//                    myApplication
//                )
//            if (billingHelper.shouldShowAds()) {
//                Log.d(TAG, "loadStartAppOpenAd: Not Purchased")
//                try {
//                    AppOpenAd.load(myApplication, PhoneLocatorLoadAds.app_open_high_splash_ad_id_admob,
//                        AdRequest.Builder().build(), callBackLoading!!)
//                } catch (e: Exception) {
//                }
//            }else{
//               // callBack.onAdCallBack()
//            }
//
//        }
//    }

    private fun callForLoadingTime(hours: Long): Boolean {
        val difference = Date().time - adLoadingTime
        val numMilliSecondsPerHour: Long = 3600000
        return difference < numMilliSecondsPerHour * hours
    }

    val isStartAdAvailable: Boolean
        get() = startOpenAd != null && callForLoadingTime(4)

    override fun onActivityCreated(activity: Activity, bundle: Bundle?) {
    }

    override fun onActivityPaused(activity: Activity) {
    }

    override fun onActivityStopped(activity: Activity) {
    }

    override fun onActivitySaveInstanceState(activity: Activity, bundle: Bundle) {
    }

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

    @OnLifecycleEvent(Lifecycle.Event.ON_START)
    fun onStart() {
        Log.d(TAG, "OnLifecycleEvent onStart")
        Log.d(TAG, "onStart: Showing App open Resume AD")
        //showStreetViewTruckCTStartAppOpenAd()
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_STOP)
    fun onStop() {
        Log.d(TAG, "OnLifecycleEvent onStop")
    }

    interface SplashShowAppOpenCallback{
        fun onAdDismissedFullScreenContent()
        fun onAdFailedToShowFullScreenContent()
    }
    interface SplashLoadAppOpenCallback{
        fun onAdCallBack()
    }
}