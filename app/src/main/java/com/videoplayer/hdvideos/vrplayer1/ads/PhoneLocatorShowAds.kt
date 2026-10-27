package com.videoplayer.hdvideos.vrplayer1.ads

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import com.google.android.gms.ads.AdError
import com.google.android.gms.ads.FullScreenContentCallback
import com.google.android.gms.ads.interstitial.InterstitialAd
import com.google.firebase.analytics.FirebaseAnalytics
import com.videoplayer.hdvideos.vrplayer1.ads.PhoneLocatorLoadAds.preReLoadAdsHoneyBee
import com.videoplayer.hdvideos.vrplayer1.ads.PhoneLocatorLoadAds.setHandlerForAd

import com.videoplayer.hdvideos.vrplayer1.ads.utlites.ContentsLocator



object PhoneLocatorShowAds {
    var TAG: String = "AdLogger:"



    fun mediationBackPressedSimpleHoneyBeeMapNavigation(
        context: Activity,
        mInterstitialAd: InterstitialAd?
    ) {
        if (!PhoneLocatorLoadAds.canShowBackPressInterstitial) {
            context.finish()
            return
        }
        if (PhoneLocatorLoadAds.canShowAdsOnTimer) {
            // timer-based
            if (PhoneLocatorLoadAds.shouldGoForAds) {
                if (mInterstitialAd != null) {
                    mInterstitialAd.show(context)
                    mInterstitialAd.fullScreenContentCallback = object : FullScreenContentCallback() {
                        override fun onAdShowedFullScreenContent() {
                            super.onAdShowedFullScreenContent()
                            PhoneLocatorLoadAds.canShowAppOpen = false
                        }
                        override fun onAdDismissedFullScreenContent() {
                            super.onAdDismissedFullScreenContent()
                            PhoneLocatorLoadAds.canShowAppOpen = true
                            PhoneLocatorLoadAds.admobInterstitialAdHoneyBee = null
                            PhoneLocatorLoadAds.canReLoadedAdMob = true
                            setHandlerForAd()
                            preReLoadAdsHoneyBee(context)
                            context.finish()
                        }
                        override fun onAdFailedToShowFullScreenContent(adError: AdError) {
                            super.onAdFailedToShowFullScreenContent(adError)
                            PhoneLocatorLoadAds.canShowAppOpen = true
                            PhoneLocatorLoadAds.admobInterstitialAdHoneyBee = null
                            PhoneLocatorLoadAds.canReLoadedAdMob = true
                            preReLoadAdsHoneyBee(context)
                            context.finish()
                        }
                    }
                } else {
                    preReLoadAdsHoneyBee(context)
                    context.finish()
                }
            } else {
                preReLoadAdsHoneyBee(context)
                context.finish()
            }
        } else {
            // counter-based
            if (adShowCounter()) {
                if (mInterstitialAd != null) {
                    mInterstitialAd.show(context)
                    mInterstitialAd.fullScreenContentCallback = object : FullScreenContentCallback() {
                        override fun onAdShowedFullScreenContent() {
                            super.onAdShowedFullScreenContent()
                            PhoneLocatorLoadAds.canShowAppOpen = false
                        }
                        override fun onAdDismissedFullScreenContent() {
                            super.onAdDismissedFullScreenContent()
                            PhoneLocatorLoadAds.canShowAppOpen = true
                            PhoneLocatorLoadAds.admobInterstitialAdHoneyBee = null
                            PhoneLocatorLoadAds.canReLoadedAdMob = true
                            preReLoadAdsHoneyBee(context)
                            context.finish()
                        }
                        override fun onAdFailedToShowFullScreenContent(adError: AdError) {
                            super.onAdFailedToShowFullScreenContent(adError)
                            PhoneLocatorLoadAds.canShowAppOpen = true
                            PhoneLocatorLoadAds.admobInterstitialAdHoneyBee = null
                            PhoneLocatorLoadAds.canReLoadedAdMob = true
                            preReLoadAdsHoneyBee(context)
                            context.finish()
                        }
                    }
                } else {
                    preReLoadAdsHoneyBee(context)
                    context.finish()
                }
            } else {
                preReLoadAdsHoneyBee(context)
                context.finish()
            }
        }
    }

    fun meidationForClickSimpleHoneyBeeMapNavigation(
        context: Context,
        mInterstitialAd: InterstitialAd?,
        intent: Intent?
    ) {
        if (!PhoneLocatorLoadAds.canShowMainInterstitial) {
            context.startActivity(intent)
            return
        }

        if (PhoneLocatorLoadAds.canShowAdsOnTimer) {
            // timer-based
            if (PhoneLocatorLoadAds.shouldGoForAds) {
                if (mInterstitialAd != null) {
                    mInterstitialAd.show((context as Activity))
                    mInterstitialAd.fullScreenContentCallback = object : FullScreenContentCallback() {
                        override fun onAdShowedFullScreenContent() {
                            super.onAdShowedFullScreenContent()
                            PhoneLocatorLoadAds.canShowAppOpen = false
                        }
                        override fun onAdDismissedFullScreenContent() {
                            super.onAdDismissedFullScreenContent()
                            PhoneLocatorLoadAds.canShowAppOpen = true
                            PhoneLocatorLoadAds.admobInterstitialAdHoneyBee = null
                            PhoneLocatorLoadAds.canReLoadedAdMob = true
                            setHandlerForAd()
                            preReLoadAdsHoneyBee(context)
                            Log.d("ConstantAdsLoadAds", "onAdDismissedFullScreenContent: ${PhoneLocatorLoadAds.next_ads_time}")
                            context.startActivity(intent)
                        }
                        override fun onAdFailedToShowFullScreenContent(adError: AdError) {
                            super.onAdFailedToShowFullScreenContent(adError)
                            PhoneLocatorLoadAds.canShowAppOpen = true
                            PhoneLocatorLoadAds.admobInterstitialAdHoneyBee = null
                            PhoneLocatorLoadAds.canReLoadedAdMob = true
                            preReLoadAdsHoneyBee(context)
                            context.startActivity(intent)
                        }
                    }
                } else {
                    preReLoadAdsHoneyBee(context)
                    context.startActivity(intent)
                }
            } else {
                preReLoadAdsHoneyBee(context)
                context.startActivity(intent)
            }
        } else {
            // counter-based
            if (adShowCounter()) {
                if (mInterstitialAd != null) {
                    mInterstitialAd.show((context as Activity))
                    mInterstitialAd.fullScreenContentCallback = object : FullScreenContentCallback() {
                        override fun onAdShowedFullScreenContent() {
                            super.onAdShowedFullScreenContent()
                            PhoneLocatorLoadAds.canShowAppOpen = false
                        }
                        override fun onAdDismissedFullScreenContent() {
                            super.onAdDismissedFullScreenContent()
                            PhoneLocatorLoadAds.canShowAppOpen = true
                            PhoneLocatorLoadAds.admobInterstitialAdHoneyBee = null
                            PhoneLocatorLoadAds.canReLoadedAdMob = true
                            preReLoadAdsHoneyBee(context)
                            Log.d("ConstantAdsLoadAds", "onAdDismissedFullScreenContent: ${PhoneLocatorLoadAds.next_ads_time}")
                            context.startActivity(intent)
                        }
                        override fun onAdFailedToShowFullScreenContent(adError: AdError) {
                            super.onAdFailedToShowFullScreenContent(adError)
                            PhoneLocatorLoadAds.canShowAppOpen = true
                            PhoneLocatorLoadAds.admobInterstitialAdHoneyBee = null
                            PhoneLocatorLoadAds.canReLoadedAdMob = true
                            preReLoadAdsHoneyBee(context)
                            context.startActivity(intent)
                        }
                    }
                } else {
                    preReLoadAdsHoneyBee(context)
                    context.startActivity(intent)
                }
            } else {
                preReLoadAdsHoneyBee(context)
                context.startActivity(intent)
            }
        }
    }


    //for simple backPressed
//    fun mediationBackPressedSimpleHoneyBeeMapNavigation(
//        context: Activity,
//        mInterstitialAd: InterstitialAd?
//    ) {
//        if (adShowCounter()) {
//            if (mInterstitialAd != null) {
//                mInterstitialAd.show(context)
//                mInterstitialAd.fullScreenContentCallback = object : FullScreenContentCallback() {
//                    override fun onAdDismissedFullScreenContent() {
//                        super.onAdDismissedFullScreenContent()
//                        PhoneLocatorLoadAds.canShowAppOpen = true
//                        PhoneLocatorLoadAds.admobInterstitialAdHoneyBee = null
//                        preReLoadAdsHoneyBee(context)
//                        context.finish()
//                    }
//
//                    override fun onAdShowedFullScreenContent() {
//                        super.onAdShowedFullScreenContent()
//                        PhoneLocatorLoadAds.canShowAppOpen = false
//                    }
//                }
//            } else {
//                preReLoadAdsHoneyBee(context)
//                context.finish()
//            }
//        } else {
//            preReLoadAdsHoneyBee(context)
//            context.finish()
//        }
//    }



    fun directAdsPhoneLocator(
        context: Activity,
        mInterstitialAd: InterstitialAd?,
        intent: Intent?
    ) {
        if (mInterstitialAd != null) {
            mInterstitialAd.show(context)
            mInterstitialAd.fullScreenContentCallback = object : FullScreenContentCallback() {
                override fun onAdDismissedFullScreenContent() {
                    super.onAdDismissedFullScreenContent()
                    PhoneLocatorLoadAds.canShowAppOpen = true
                    PhoneLocatorLoadAds.admobInterstitialAdHoneyBee = null
                    preReLoadAdsHoneyBee(context)
                    context.startActivity(intent)
                }

                override fun onAdShowedFullScreenContent() {
                    super.onAdShowedFullScreenContent()
                    PhoneLocatorLoadAds.canShowAppOpen = false
                }
            }
        } else {
            preReLoadAdsHoneyBee(context)
            context.startActivity(intent)
        }
    }

    //test add from Timer

//    fun interstitialAdsForActivityTimer(
//        context: Activity?,
//        mInterstitialAd: InterstitialAd?,
//        intent: Intent?,
//        adBgWhite: View
//    ) {
//        val dialog = ADsLoadingDialog(context!!)
//        val billingHelper = AppPurchaseHelperVdMi(context)
//        if (billingHelper.shouldShowAds()) {
//            if (LoadAdsVdMi.shouldGoForAds) {
//                if (mInterstitialAd != null) {
//                    try {
//                        dialog.show()
//                    } catch (e: Exception) {
//                        e.printStackTrace()
//                    }
//                    adBgWhite.visibility = View.VISIBLE
//                    Handler().postDelayed({
//                        try {
//                            dialog.dismiss()
//                        } catch (e: Exception) {
//                            e.printStackTrace()
//                        }
//                        mInterstitialAd.show(context)
//                        mInterstitialAd.fullScreenContentCallback =
//                            object : FullScreenContentCallback() {
//                                override fun onAdShowedFullScreenContent() {
//                                    super.onAdShowedFullScreenContent()
//                                    Log.d(TAG, "onAdShowedFullScreenContent: ")
//                                    PhoneLocatorLoadAds.canShowAppOpen = false
//                                }
//
//                                override fun onAdClicked() {
//                                    super.onAdClicked()
//                                    LoadAdsVdMi.userClickedOnAd++
//                                }
//
//
//                                override fun onAdDismissedFullScreenContent() {
//                                    super.onAdDismissedFullScreenContent()
//                                    PhoneLocatorLoadAds.canShowAppOpen = true
//                                    LoadAdsVdMi.admobInterstitialAd = null
//                                    try {
//                                        Handler().postDelayed(
//                                            { adBgWhite.visibility = View.GONE },
//                                            1000
//                                        )
//                                    } catch (e: Exception) {
//                                    }
//                                    Log.d(TAG, "onAdDismissedFullScreenContent: ")
//                                    LoadAdsVdMi.setHandlerForAd()
//                                    LoadAdsVdMi.setHandlerForAdRequest(context)
//                                    LoadAdsVdMi.preReLoadAdsAdmob(context)
//                                    LoadAdsVdMi.preAdsRequestYandex(context)
//                                    context.startActivity(intent)
//                                }
//                            }
//                    }, adDialogTime.toLong())
//                } else if (LoadAdsVdMi.yandexInterstitialAd != null) {
//
//                    try {
//                        dialog.show()
//                    } catch (e: Exception) {
//                    }
//                    adBgWhite.visibility = View.VISIBLE
//                    Handler().postDelayed({
//                        try {
//                            dialog.dismiss()
//                        } catch (e: Exception) {
//                        }
//
//                        LoadAdsVdMi.yandexInterstitialAd?.apply {
//                            setAdEventListener(object : InterstitialAdEventListener {
//                                override fun onAdShown() {
//                                    LoadAdsVdMi.canShowAppOpen = false
//                                }
//
//                                override fun onAdDismissed() {
//
//                                    LoadAdsVdMi.yandexInterstitialAd = null
//                                    LoadAdsVdMi.canShowAppOpen = true
//                                    LoadAdsVdMi.setHandlerForAd()
//                                    LoadAdsVdMi.preReLoadAdsAdmob(context)
//                                    LoadAdsVdMi.preAdsRequestYandex(context)
//                                    try {
//                                        Handler().postDelayed(
//                                            { adBgWhite.visibility = View.GONE },
//                                            1000
//                                        )
//                                    } catch (e: Exception) {
//                                    }
//                                    context.startActivity(intent)
//                                }
//
//                                override fun onAdFailedToShow(adError: com.yandex.mobile.ads.common.AdError) {
//                                    LoadAdsVdMi.yandexInterstitialAd?.setAdEventListener(
//                                        null
//                                    )
//                                    LoadAdsVdMi.yandexInterstitialAd = null
//                                    LoadAdsVdMi.canShowAppOpen = true
//                                    LoadAdsVdMi.preReLoadAdsAdmob(context)
//                                    LoadAdsVdMi.preAdsRequestYandex(context)
//                                    context.startActivity(intent)
//                                }
//
//                                override fun onAdClicked() {
//                                    // Called when a click is recorded for an ad.
//                                }
//
//                                override fun onAdImpression(impressionData: ImpressionData?) {
//                                    // Called when an impression is recorded for an ad.
//                                }
//                            })
//                            show(context)
//                        }
//
//
//                    }, adDialogTime.toLong())
//                } else {
//                    LoadAdsVdMi.preReLoadAdsAdmob(context)
//                    LoadAdsVdMi.preAdsRequestYandex(context)
//                    context.startActivity(intent)
//                }
//
//
//            } else {
//                LoadAdsVdMi.preReLoadAdsAdmob(context)
//                LoadAdsVdMi.preAdsRequestYandex(context)
//                context.startActivity(intent)
//            }
//        }else{
//            context.startActivity(intent)
//        }
//    }



    //Main click ads with fb ad destructive
//    fun meidationForClickSimpleHoneyBeeMapNavigation(
//        context: Context,
//        mInterstitialAd: InterstitialAd?,
//        intent: Intent?
//    ) {
//        if (adShowCounter()) {
//            if (mInterstitialAd != null) {
//                mInterstitialAd.show((context as Activity?)!!)
//                mInterstitialAd.fullScreenContentCallback = object : FullScreenContentCallback() {
//                    override fun onAdDismissedFullScreenContent() {
//                        super.onAdDismissedFullScreenContent()
//                        PhoneLocatorLoadAds.canShowAppOpen = true
//                        PhoneLocatorLoadAds.admobInterstitialAdHoneyBee = null
//                        preReLoadAdsHoneyBee(context)
//                         Log.d(
//                            "ConstantAdsLoadAds",
//                            "onAdDismissedFullScreenContent: " + PhoneLocatorLoadAds.next_ads_time
//                        )
//                        context.startActivity(intent)
//                    }
//                    override fun onAdShowedFullScreenContent() {
//                        super.onAdShowedFullScreenContent()
//                        PhoneLocatorLoadAds.canShowAppOpen = false
//                    }
//                }
//            } else {
//                preReLoadAdsHoneyBee(context)
//                 context.startActivity(intent)
//            }
//        } else {
//            preReLoadAdsHoneyBee(context)
//             context.startActivity(intent)
//        }
//    }

    //Fixaddd
    fun fixInterstitial(
        context: Context,
        mInterstitialAd: InterstitialAd?,
        intent: Intent?
    ) {
        if (mInterstitialAd != null) {
            mInterstitialAd.show((context as Activity?)!!)
            mInterstitialAd.fullScreenContentCallback = object : FullScreenContentCallback() {
                override fun onAdDismissedFullScreenContent() {
                    super.onAdDismissedFullScreenContent()
                    PhoneLocatorLoadAds.canShowAppOpen = true
                    PhoneLocatorLoadAds.admobInterstitialAdHoneyBee = null
                    preReLoadAdsHoneyBee(context)
                    Log.d(
                        "ConstantAdsLoadAds",
                        "onAdDismissedFullScreenContent: " + PhoneLocatorLoadAds.next_ads_time
                    )
                    context.startActivity(intent)
                }

                override fun onAdShowedFullScreenContent() {
                    super.onAdShowedFullScreenContent()
                    PhoneLocatorLoadAds.canShowAppOpen = false
                }
            }
        } else {
            preReLoadAdsHoneyBee(context)
            context.startActivity(intent)
        }
    }



    fun meidationForClickSimpleHoneyBeeMapOnBoarding(
        context: Context,
        mInterstitialAd: InterstitialAd?,
        intent: Intent
    ) {
        if (mInterstitialAd != null) {
            mInterstitialAd.show(context as Activity)
            mInterstitialAd.fullScreenContentCallback = object : FullScreenContentCallback() {
                override fun onAdDismissedFullScreenContent() {
                    // Ad closed, go to next activity
                    PhoneLocatorLoadAds.admobInterstitialAdHoneyBeeOnboarding = null
                    context.startActivity(intent)
                }

                override fun onAdFailedToShowFullScreenContent(p0: AdError) {
                    super.onAdFailedToShowFullScreenContent(p0)
                    PhoneLocatorLoadAds.admobInterstitialAdHoneyBeeOnboarding = null
                    PhoneLocatorLoadAds.canShowAppOpen = false

                    // If ad fails, still continue to next activity
                    context.startActivity(intent)
                }

                override fun onAdShowedFullScreenContent() {
                    super.onAdShowedFullScreenContent()
                }

                override fun onAdClicked() {
                    super.onAdClicked()
                }
            }
        } else {
            // If ad not available, open directly
            context.startActivity(intent)
        }
    }

    /*  fun meidationForClickSimpleHoneyBeeMapOnBoarding(
          context: Context,
          mInterstitialAd: InterstitialAd?,
          intent: Intent?
      ) {

          if (mInterstitialAd != null) {
              mInterstitialAd.show((context as android.app.Activity?)!!)
              mInterstitialAd.fullScreenContentCallback = object : FullScreenContentCallback() {
                  override fun onAdDismissedFullScreenContent() {
                      super.onAdDismissedFullScreenContent()
                      PhoneLocatorLoadAds.canShowAppOpen = true
                      PhoneLocatorLoadAds.admobInterstitialAdHoneyBeeOnboarding = null
                      preReLoadAdsHoneyBee(context)
                      preAdsRequestYandex(context)
                      Log.d(
                          "ConstantAdsLoadAds",
                          "onAdDismissedFullScreenContent: " + PhoneLocatorLoadAds.next_ads_time
                      )
                      context.startActivity(intent)
                  }

                  override fun onAdShowedFullScreenContent() {
                      super.onAdShowedFullScreenContent()
                      PhoneLocatorLoadAds.canShowAppOpen = false
                  }

              }
          } else {
              preReLoadAdsHoneyBee(context)
              context.startActivity(intent)
          }

      }
  */

    fun splashClearActivityHoneyBeeMapNavigation(
        context: Activity,
        mInterstitialAd: InterstitialAd?,
        intent: Intent?
    ) {
        if (mInterstitialAd != null) {
            Log.d(TAG, "splashClearActivityHoneyBeeMapNavigation: ")
            mInterstitialAd.show(context)
            mInterstitialAd.fullScreenContentCallback = object : FullScreenContentCallback() {
                override fun onAdDismissedFullScreenContent() {
                    super.onAdDismissedFullScreenContent()
                    logAnalyticsForClicks("SplashAdDismissedFullScreenContent", context)
                    PhoneLocatorLoadAds.canShowAppOpen = true
                    ContentsLocator.isDirectAds = false
                    context.startActivity(intent)
                    context.finish()
                }

                override fun onAdFailedToShowFullScreenContent(p0: AdError) {
                    super.onAdFailedToShowFullScreenContent(p0)
                    ContentsLocator.isDirectAds = true
                }

                override fun onAdShowedFullScreenContent() {
                    super.onAdShowedFullScreenContent()
                    logAnalyticsForClicks("SplashAdShowedFullScreenContent", context)
                    PhoneLocatorLoadAds.canShowAppOpen = false
                }
            }
        } else {
            context.startActivity(intent)
            context.finish()
        }
    }

    fun logAnalyticsForClicks(value: String, context: Context) {
        // Suppress legacy transitional events ("Screen A to Screen B") per QA requirement
        if (value.contains(" to ", ignoreCase = true) || 
            value.contains("back to", ignoreCase = true) || 
            value.contains("backpress", ignoreCase = true)) {
            Log.d("AppAnalytics", "Suppressed cross-screen event: $value")
            return
        }
        val firebaseAnalytics: FirebaseAnalytics?
        firebaseAnalytics = FirebaseAnalytics.getInstance(context)
        val bundle = Bundle()
        try {
            val sanitized = value.replace(" ", "_").take(40)
            firebaseAnalytics.logEvent(sanitized, bundle)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }


    fun adShowCounter(): Boolean {
        PhoneLocatorLoadAds.adClickCounter++
        Log.d("adShowCounter", " " + PhoneLocatorLoadAds.adClickCounter)
        if (PhoneLocatorLoadAds.adClickCounter == 1) {
            Log.d("adShowCounter", "one:  1  show")
            return true
        } else if (PhoneLocatorLoadAds.adClickCounter > PhoneLocatorLoadAds.adShowAfter) {
            PhoneLocatorLoadAds.adClickCounter = 1
            Log.d("adShowCounter", "two:   >  show ")
            return true
        } else {
            Log.d("adShowCounter", " three  else  No")
            return false
        }
    }


    fun showReelsInterstitial(
        context: Context,
        mInterstitialAd: InterstitialAd?,
        adView: View
    ) {
        if (mInterstitialAd != null) {
            mInterstitialAd.show(context as Activity)
            mInterstitialAd.fullScreenContentCallback = object : FullScreenContentCallback() {
                override fun onAdShowedFullScreenContent() {
                    super.onAdShowedFullScreenContent()
                    PhoneLocatorLoadAds.canShowAppOpen = false
                    adView.visibility = View.VISIBLE
                    Log.d(TAG, "Reels ad showed")
                }

                override fun onAdDismissedFullScreenContent() {
                    super.onAdDismissedFullScreenContent()
                    PhoneLocatorLoadAds.canShowAppOpen = true
                    PhoneLocatorLoadAds.admobReelsInterstitialAd = null
                    PhoneLocatorLoadAds.preLoadReelsInterstitial(context)
                    PhoneLocatorLoadAds.setHandlerForAd()
                    adView.visibility = View.GONE
                    Log.d(TAG, "Reels ad dismissed")
                }

                override fun onAdFailedToShowFullScreenContent(adError: AdError) {
                    super.onAdFailedToShowFullScreenContent(adError)
                    PhoneLocatorLoadAds.canShowAppOpen = true
                    PhoneLocatorLoadAds.admobReelsInterstitialAd = null
                    PhoneLocatorLoadAds.preLoadReelsInterstitial(context)
                    adView.visibility = View.GONE
                    Log.d(TAG, "Reels ad failed to show: ${adError.message}")
                }
            }
        } else {
            PhoneLocatorLoadAds.preLoadReelsInterstitial(context)
        }
    }
}




