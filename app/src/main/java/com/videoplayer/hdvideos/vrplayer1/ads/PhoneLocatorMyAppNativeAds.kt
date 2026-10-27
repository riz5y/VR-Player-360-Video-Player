package com.videoplayer.hdvideos.vrplayer1.ads

import android.app.Activity
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.widget.*
import com.google.android.gms.ads.AdListener
import com.google.android.gms.ads.AdLoader
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.LoadAdError
import com.google.android.gms.ads.VideoController
import com.google.android.gms.ads.VideoOptions
import com.google.android.gms.ads.nativead.MediaView
import com.google.android.gms.ads.nativead.NativeAd
import com.google.android.gms.ads.nativead.NativeAdOptions
import com.google.android.gms.ads.nativead.NativeAdView
import com.google.android.material.button.MaterialButton
import com.videoplayer.hdvideos.vrplayer1.R
import com.videoplayer.hdvideos.vrplayer1.ads.PhoneLocatorLoadAds.should_show_native


class PhoneLocatorMyAppNativeAds {

    companion object {

        fun adsNativeOnBoarding(
            mContext: Activity,
            frameLayout: FrameLayout
        ) {
            val billingHelper = PhoneLocatorAppPurchaseHelper(mContext)
            if (billingHelper.shouldShowAds()) {
                     val inflater = LayoutInflater.from(mContext)
                    val builder = AdLoader.Builder(
                        mContext,
                        PhoneLocatorLoadAds.admob_native_on_boarding
                    )
                    var admobNativeAd: NativeAd? = null
                    builder.forNativeAd { unifiedNativeAd ->
                        if (admobNativeAd != null) {
                            admobNativeAd!!.destroy()
                        }
                        try {
                            admobNativeAd = unifiedNativeAd
                            val adView =  //actually card view of fb ad
                                inflater.inflate(
                                    R.layout.native_small,
                                    null
                                ) as NativeAdView
                            showLocatorCTNativeAdView(unifiedNativeAd, adView)
                            frameLayout.removeAllViews()
                            frameLayout.addView(adView)
                        } catch (e: Exception) {
                            e.printStackTrace()
                        }
                    }

                    val videoOptions = VideoOptions.Builder()
                        .setStartMuted(true/*startVideoAdsMuted.isChecked()*/)
                        .build()

                    val adOptions = NativeAdOptions.Builder()
                        .setVideoOptions(videoOptions)
                        .build()

                    builder.withNativeAdOptions(adOptions)
                    val adLoader =
                        builder.withAdListener(object :
                            AdListener() {

                            override fun onAdFailedToLoad(p0: LoadAdError) {
                                super.onAdFailedToLoad(p0)
                                Log.d("NativeAd:", "onAdFailedToLoad   Error)  $p0")
                                frameLayout.removeAllViews()
                                frameLayout.visibility = View.GONE


                            }

                            override fun onAdLoaded() {
                                super.onAdLoaded()
                                Log.d("NativeAd:", "onAdLoaded   Native ad")
                            }

                            override fun onAdClicked() {
                                super.onAdClicked()
                                /*StreetViewTruckCTShowAds.trackUserEventClick(
                                    "NativeAdClick",
                                    mContext
                                )
                                StreetViewTruckCTLoadAds.onAdClick++*/

                            }

                        }).build()

                    val billingHelper = PhoneLocatorAppPurchaseHelper(mContext)
                    if (billingHelper.shouldShowAds()) {
                        adLoader.loadAd(AdRequest.Builder().build())
                    }

            } else {
                frameLayout.visibility = View.GONE
            }
            /*}else{
                adManager = YandexNativeAdManager(
                    mContext,
                    frameLayout,
                    LoadAdsMarine.yandex_native_id
                )
                adManager.loadNativeAd()
            }*/

        }



        fun adsNativeOnBoardingFullScreen(
            mContext: Activity,
            frameLayout: FrameLayout
        ) {
            Log.d("NativeFullScreen", "Starting to load full-screen native ad")

            val billingHelper = PhoneLocatorAppPurchaseHelper(mContext)
            if (!billingHelper.shouldShowAds()) {
                Log.d("NativeFullScreen", "Ads disabled - user has purchased")
                frameLayout.visibility = View.GONE
                return
            }

            if (!should_show_native) {
                Log.d("NativeFullScreen", "should_show_native is false")
                frameLayout.visibility = View.GONE
                return
            }

            Log.d("NativeFullScreen", "Loading ad with ID: ${PhoneLocatorLoadAds.admob_native_on_boarding_full_screen}")

            val inflater = LayoutInflater.from(mContext)
            val builder = AdLoader.Builder(
                mContext,
                PhoneLocatorLoadAds.admob_native_on_boarding_full_screen
            )
            var admobNativeAd: NativeAd? = null

            builder.forNativeAd { unifiedNativeAd ->
                Log.d("NativeFullScreen", "Native ad received")
                if (admobNativeAd != null) {
                    admobNativeAd!!.destroy()
                }
                try {
                    admobNativeAd = unifiedNativeAd
                    val adView = inflater.inflate(
                        R.layout.native_large,
                        null
                    ) as NativeAdView

                    showLocatorCTNativeAdViewfullscren(unifiedNativeAd, adView)

                    frameLayout.removeAllViews()
                    frameLayout.addView(adView)
                    frameLayout.visibility = View.VISIBLE

                    Log.d("NativeFullScreen", "Ad view added to frame layout successfully")
                } catch (e: Exception) {
                    Log.e("NativeFullScreen", "Error inflating ad view", e)
                    e.printStackTrace()
                }
            }

            val videoOptions = VideoOptions.Builder()
                .setStartMuted(true)
                .build()

            val adOptions = NativeAdOptions.Builder()
                .setVideoOptions(videoOptions)
                .build()

            builder.withNativeAdOptions(adOptions)
            val adLoader = builder.withAdListener(object : AdListener() {

                override fun onAdFailedToLoad(p0: LoadAdError) {
                    super.onAdFailedToLoad(p0)
                    Log.e("NativeFullScreen", "onAdFailedToLoad - Error: ${p0.message}, Code: ${p0.code}")
                    frameLayout.removeAllViews()
                    frameLayout.visibility = View.GONE
                }

                override fun onAdLoaded() {
                    super.onAdLoaded()
                    Log.d("NativeFullScreen", "onAdLoaded - Native ad loaded successfully")
                }

                override fun onAdClicked() {
                    super.onAdClicked()
                    Log.d("NativeFullScreen", "onAdClicked")

                }

            }).build()

            adLoader.loadAd(AdRequest.Builder().build())
        }



        fun adsNativeOnBoardingFullScreenAfterFirst(
            mContext: Activity,
            frameLayout: FrameLayout
        ) {
            Log.d("NativeFullScreenAfterFirst", "Starting to load full-screen native ad after first")

            val billingHelper = PhoneLocatorAppPurchaseHelper(mContext)
            if (!billingHelper.shouldShowAds()) {
                Log.d("NativeFullScreenAfterFirst", "Ads disabled - user has purchased")
                frameLayout.visibility = View.GONE
                return
            }

            if (!should_show_native) {
                Log.d("NativeFullScreenAfterFirst", "should_show_native is false")
                frameLayout.visibility = View.GONE
                return
            }

            Log.d("NativeFullScreenAfterFirst", "Loading ad with ID: ${PhoneLocatorLoadAds.canShowNativeOnBoardingFullScreenafterfirstaddunit}")

            val inflater = LayoutInflater.from(mContext)
            val builder = AdLoader.Builder(
                mContext,
                PhoneLocatorLoadAds.canShowNativeOnBoardingFullScreenafterfirstaddunit
            )
            var admobNativeAd: NativeAd? = null

            builder.forNativeAd { unifiedNativeAd ->
                Log.d("NativeFullScreenAfterFirst", "Native ad received")
                if (admobNativeAd != null) {
                    admobNativeAd!!.destroy()
                }
                try {
                    admobNativeAd = unifiedNativeAd
                    val adView = inflater.inflate(
                        R.layout.native_large,
                        null
                    ) as NativeAdView

                    showLocatorCTNativeAdViewfullscren(unifiedNativeAd, adView)

                    frameLayout.removeAllViews()
                    frameLayout.addView(adView)
                    frameLayout.visibility = View.VISIBLE

                    Log.d("NativeFullScreenAfterFirst", "Ad view added to frame layout successfully")
                } catch (e: Exception) {
                    Log.e("NativeFullScreenAfterFirst", "Error inflating ad view", e)
                    e.printStackTrace()
                }
            }

            val videoOptions = VideoOptions.Builder()
                .setStartMuted(true)
                .build()

            val adOptions = NativeAdOptions.Builder()
                .setVideoOptions(videoOptions)
                .build()

            builder.withNativeAdOptions(adOptions)
            val adLoader = builder.withAdListener(object : AdListener() {

                override fun onAdFailedToLoad(p0: LoadAdError) {
                    super.onAdFailedToLoad(p0)
                    Log.e("NativeFullScreenAfterFirst", "onAdFailedToLoad - Error: ${p0.message}, Code: ${p0.code}")
                    frameLayout.removeAllViews()
                    frameLayout.visibility = View.GONE
                }

                override fun onAdLoaded() {
                    super.onAdLoaded()
                    Log.d("NativeFullScreenAfterFirst", "onAdLoaded - Native ad loaded successfully")
                }

                override fun onAdClicked() {
                    super.onAdClicked()
                    Log.d("NativeFullScreenAfterFirst", "onAdClicked")
                }

            }).build()

            adLoader.loadAd(AdRequest.Builder().build())
        }


        private fun showLocatorCTNativeAdView(nativeAd: NativeAd, adView: NativeAdView) {

             adView.headlineView = adView.findViewById(R.id.ad_headline)
            adView.bodyView = adView.findViewById(R.id.ad_body)
            adView.callToActionView = adView.findViewById(R.id.ad_call_to_action)
            adView.iconView = adView.findViewById(R.id.ad_app_icon)
            adView.priceView = adView.findViewById(R.id.ad_price)
            adView.starRatingView = adView.findViewById(R.id.ad_stars)
            adView.storeView = adView.findViewById(R.id.ad_store)
            adView.advertiserView = adView.findViewById(R.id.ad_advertiser)
            (adView.headlineView as TextView).text = nativeAd.headline

            if (nativeAd.body == null) {
                adView.bodyView?.visibility = View.INVISIBLE
            } else {
                adView.bodyView?.visibility = View.VISIBLE
                (adView.bodyView as TextView).text = nativeAd.body
            }

            if (nativeAd.callToAction == null) {
                adView.callToActionView?.visibility = View.INVISIBLE
            } else {
                adView.callToActionView?.visibility = View.VISIBLE
                (adView.callToActionView as TextView).text = nativeAd.callToAction
            }

            if (nativeAd.icon == null) {
                adView.iconView?.visibility = View.INVISIBLE
            } else {
                (adView.iconView as ImageView).setImageDrawable(
                    nativeAd.icon?.drawable
                )
                adView.iconView?.visibility = View.VISIBLE
            }

            if (nativeAd.price == null) {
                adView.priceView?.visibility = View.INVISIBLE
            } else {
                adView.priceView?.visibility = View.VISIBLE
                (adView.priceView as TextView).text = nativeAd.price
            }

            if (nativeAd.store == null) {
                adView.storeView?.visibility = View.INVISIBLE
            } else {
                adView.storeView?.visibility = View.VISIBLE
                (adView.storeView as TextView).text = nativeAd.store
            }

            if (nativeAd.starRating == null) {
                adView.starRatingView?.visibility = View.INVISIBLE
            } else {
                (adView.starRatingView as RatingBar).rating = nativeAd.starRating!!.toFloat()
                adView.starRatingView?.visibility = View.VISIBLE
            }

            if (nativeAd.advertiser == null) {
                adView.advertiserView?.visibility = View.INVISIBLE
            } else {
                (adView.advertiserView as TextView).text = nativeAd.advertiser
                adView.advertiserView?.visibility = View.VISIBLE
            }
            adView.setNativeAd(nativeAd)
            val vc = nativeAd.mediaContent?.videoController

            if (vc != null) {
                if (vc.hasVideoContent()) {
                    vc.videoLifecycleCallbacks =
                        object : VideoController.VideoLifecycleCallbacks() {
                        }
                } else {
                }
            }
        }




        private fun showLocatorCTNativeAdViewfullscren(nativeAd: NativeAd, adView: NativeAdView) {
            adView.headlineView = adView.findViewById(R.id.ad_headline)
            adView.bodyView = adView.findViewById(R.id.ad_body)
            adView.callToActionView = adView.findViewById(R.id.ad_call_to_action)
            adView.iconView = adView.findViewById(R.id.ad_app_icon)
            adView.mediaView = adView.findViewById(R.id.media_view)

            // Headline (required)
            (adView.headlineView as TextView).text = nativeAd.headline

            // Body (optional)
            if (nativeAd.body == null) {
                adView.bodyView?.visibility = View.GONE
            } else {
                adView.bodyView?.visibility = View.VISIBLE
                (adView.bodyView as TextView).text = nativeAd.body
            }

            // CTA (MaterialButton, not TextView!)
            if (nativeAd.callToAction == null) {
                adView.callToActionView?.visibility = View.GONE
            } else {
                adView.callToActionView?.visibility = View.VISIBLE
                (adView.callToActionView as MaterialButton).text =
                    nativeAd.callToAction
            }
            // Icon
            if (nativeAd.icon == null) {
                adView.iconView?.visibility = View.GONE
            } else {
                (adView.iconView as ImageView).setImageDrawable(nativeAd.icon!!.drawable)
                adView.iconView?.visibility = View.VISIBLE
            }
            // Media
            val mediaView = adView.findViewById<MediaView>(R.id.media_view)
            adView.mediaView = mediaView
            adView.setNativeAd(nativeAd)
        }

    }









}