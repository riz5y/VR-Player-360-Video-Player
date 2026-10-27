package com.videoplayer.hdvideos.vrplayer1.ads

import android.app.Activity
import android.content.Context
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.DisplayMetrics
import android.util.Log
import android.view.View
import android.widget.LinearLayout
import com.google.ads.mediation.admob.AdMobAdapter
import com.google.android.gms.ads.AdListener
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.AdSize
import com.google.android.gms.ads.AdView
import com.google.android.gms.ads.LoadAdError
import com.google.android.gms.ads.interstitial.InterstitialAd
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback
import com.google.firebase.database.FirebaseDatabase

import com.videoplayer.hdvideos.vrplayer1.ads.MyApp.Companion.getStr
import com.videoplayer.hdvideos.vrplayer1.BuildConfig
import com.videoplayer.hdvideos.vrplayer1.R
import com.videoplayer.hdvideos.vrplayer1.ads.utlites.SplashAdsCallBack

object PhoneLocatorLoadAds {
    var appid_admob_inApp: String = getStr(R.string.admob_ad_id)
    var interstitial_admob_inApp: String = BuildConfig.admob_interstitial_id
    var admob_interstitial_id_onBoarding: String = BuildConfig.admob_interstitial_id_onBoarding

    var admob_interstitial_splash_id: String = BuildConfig.admob_interstitial_splash_id
    var banner_admob_inApp: String = BuildConfig.admob_banner_id

    var admob_banner_id_Splash: String = BuildConfig.admob_banner_id_Splash

    var app_open_ad_id_admob: String = BuildConfig.app_open_ad_id_admob

//    var app_open_high_splash_ad_id_admob: String = BuildConfig.app_open_high_splash_ad_id_admob

    var admob_native_on_boarding: String = getStr(R.string.admob_native_on_boarding)
    var admob_native_on_boarding_full_screen: String =
        getStr(R.string.admob_native_on_boarding_full_screen)


    //    var splashInterstitialAdYandex: com.yandex.mobile.ads.interstitial.InterstitialAd? = null
//    var yandexInterstitialAd: com.yandex.mobile.ads.interstitial.InterstitialAd? = null
    var canReloadYandex = true


    /* public static String intro_native_admob_inApp = MyApp.Companion.BuildConfig.intro_admob_native_id);*/
    var shouldShowAdmob: Boolean = true
    var should_show_app_open: Boolean = true

    @JvmField

    var ad_click_value_key_var: Double = 1.0
    var ad_impression_value_key_var: Double = 6.0
    var percentage: Float = 0f
    var haveGotSnapshot: Boolean = false
    var should_show_allfb_ads: Boolean = true

    var current_counter: Double = 19.0

    /*new 13/1/2022*/
    var splash_counter: Double = 1.0
    var splash_threshold: Double = 2.0


    var ctr_control: Boolean = false
    var show_splash_ad: Boolean = true

    @JvmField
    var next_ads_time: Long = 12000

    var shouldGoForAds: Boolean = true


    @JvmField
    var admobInterstitialAdHoneyBee: InterstitialAd? = null
    var admobInterstitialAdHoneyBeeSplash: InterstitialAd? = null
    var admobInterstitialAdHoneyBeeOnboarding: InterstitialAd? = null


    // Add this variable at the top with the other ad variables
    var admobReelsInterstitialAd: InterstitialAd? = null

    var admob_reels_interstitial_id: String = BuildConfig.admob_reels_interstitial_id


    var canShowNativeOnBoardingFullScreenafterfirst: Boolean = true
    var canShowNativeOnBoardingFullScreenafterfirstaddunit: String =
        getStr(R.string.canShowNativeOnBoardingFullScreenafterfirstaddunit)

    var canReLoadedAdMob: Boolean = false


    @JvmField
    var adClickCounter: Int = 0

    @JvmField
    var adShowAfter: Int = 3
    var user_count: Int = 0


    private val myHandler = Handler()

    var canShowOnBoarding = true
    var canShowPremium = true
    var OnboardingAdshow = true
    var canShowFirstAdInter = true
    var canShowInappIcon = true
    var canShowBannerOnsplash = true


    var should_show_native: Boolean = true


    var canShowNativeOnBoarding: Boolean = true

    var mainBanneradControl: Boolean = true

    var canShowNativeOnBoardingFullScreen: Boolean = true
    var canShowAppOpen: Boolean = true

    var reelsInterstitialShow: Boolean = true


    var nextreelsShow: Int = 3

    var inAppVariant: String = "A"

    var canShowAdsOnTimer: Boolean = false
    var adTimerIntervalMillis: Long = 12000


    var canShowSplashInterstitial: Boolean = true


    var canShowMainInterstitial: Boolean = true
    var canShowBackPressInterstitial: Boolean = true

    // Remote Config: per-screen in-app content controls
    var inAppCtaText: String = "Continue"
    var onboardingPremiumCard: Boolean = true
    var inAppSubtitle: String = "Enhance your experience with premium subscription"
    var inAppSaveBadge: String = "Save 55%"
    var canShowInAppA: Boolean = true
    var canShowInAppB: Boolean = true
    var canShowInAppC: Boolean = true

    //for checking
//    var adsRestoredAfterReward = false


    private val bannerRefreshHandler = Handler(Looper.getMainLooper())

    private var bannerRefreshRunnable: Runnable? = null


    private val databaseReference =
        FirebaseDatabase.getInstance().getReference("PhoneLocatorTrackerAdsID")


    fun setHandlerForAd() {
        shouldGoForAds = false
        Log.d("ConstantAdsLoadAds", "shouldGoForAds onTimeStart: " + shouldGoForAds)
        myHandler.postDelayed(object : Runnable {
            override fun run() {
                shouldGoForAds = true
                Log.d("ConstantAdsLoadAds", "shouldGoForAds onTimeComplete: " + shouldGoForAds)
            }
        }, next_ads_time)
    }

    /*    public static void loadAdMobMediumBannerAd(Context context, final LinearLayout adContainer, final View mView) {
        HoneyBeeMapNavigationAppPurchaseHelper billingHelper = new HoneyBeeMapNavigationAppPurchaseHelper(context);
        if (billingHelper.shouldShowAds()) {
            AdView mAdView = new AdView(context);
            mAdView.setAdUnitId(HoneyBeeMapNavigationLoadAds.banner_medium_admob_inApp);
            mAdView.setAdSize(AdSize.MEDIUM_RECTANGLE);
            mAdView.setAdListener(new com.google.android.gms.ads.AdListener() {
                @Override
                public void onAdLoaded() {
                    super.onAdLoaded();
                    Log.d("ConstantAdsLoadAds", "Bannen onAdLoaded: ");
                    try {
                        adContainer.removeAllViews();
                        adContainer.addView(mAdView);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }

                @Override
                public void onAdFailedToLoad(@NonNull LoadAdError loadAdError) {
                    super.onAdFailedToLoad(loadAdError);
                    Log.d("ConstantAdsLoadAds", "Bannen onAdFailedToLoad: " + loadAdError.toString());
                    mAdView.destroy();
                    mView.setVisibility(View.GONE);
                }

            });
            try {
                mAdView.loadAd(new AdRequest.Builder().build());
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            mView.setVisibility(View.GONE);
        }
    }*/


    /*    private fun loadMIStreetViewYandexBanner(
            adContainer: LinearLayout,
            view: View?,
            context: Context
        ) {
            val billingHelper = PhoneLocatorAppPurchaseHelper(context)
            if (billingHelper.shouldShowAds()) {
                val adView = BannerAdView(context).apply {
                    val displayMetrics = Resources.getSystem().displayMetrics
                    val screenWidthPx = displayMetrics.widthPixels
                    // Use your real Yandex Banner Ad Unit ID here
                    setAdUnitId(yandex_banner_id)
                    // Adaptive sticky banner size
                    setAdSize(BannerAdSize.inlineSize(context,screenWidthPx,100))
                    setBannerAdEventListener(object : BannerAdEventListener {
                        override fun onAdLoaded() {
                            Log.d("YandexAd", "Banner loaded")
                            try {

                                view?.visibility = View.VISIBLE
                                adContainer.visibility = View.VISIBLE
                                adContainer.removeAllViews()
                                adContainer.addView(this@apply)
                            } catch (e: Exception) {
                                e.printStackTrace()
                            }
                        }

                        override fun onAdFailedToLoad(error: AdRequestError) {
                            Log.d("YandexAd", "Banner failed: ${error.description}")

                            view?.visibility = View.GONE
                        }

                        override fun onAdClicked() {
                        }
                        override fun onLeftApplication() {}
                        override fun onReturnedToApplication() {}
                        override fun onImpression(p0: ImpressionData?) {}
                    })
                }

                try {
                    adView.loadAd(com.yandex.mobile.ads.common.AdRequest.Builder().build())
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            } else {
                view?.visibility = View.GONE
            }
        }*/


    fun getAdSize(context: Activity): AdSize {
        val display = context.getWindowManager().getDefaultDisplay()
        val outMetrics = DisplayMetrics()
        display.getMetrics(outMetrics)

        val widthPixels = outMetrics.widthPixels.toFloat()
        val density = outMetrics.density

        val sideInsets = androidx.core.view.ViewCompat.getRootWindowInsets(context.window.decorView)
            ?.getInsets(androidx.core.view.WindowInsetsCompat.Type.systemBars())
            ?.let { it.left + it.right } ?: 0
        val adWidth = ((widthPixels - sideInsets) / density).toInt()

        // Step 3 - Get adaptive ad size and return for setting on the ad view.
        return AdSize.getCurrentOrientationAnchoredAdaptiveBannerAdSize(context, adWidth)
    }


//Old banner code
//    fun loadHoneyBeeMapNavigationBannerForMainMediation(
//        adContainer: LinearLayout,
//        mView: View,
//        context: Context
//    ) {
//        val billingHelper = PhoneLocatorAppPurchaseHelper(context)
//        if (billingHelper.shouldShowAds()) {
//            loadHoneyBeeMapNavigationAdMobBanner(adContainer, mView, context)
//            //loadGlobeMapBannerMax(adContainer, mView,context);
//        } else {
//            mView.setVisibility(View.GONE)
//        }
//    }
//    private fun loadHoneyBeeMapNavigationAdMobBanner(
//        adContainer: LinearLayout,
//        mView: View?,
//        context: Context
//    ) {
//
//        val mAdView = AdView(context)
//        mAdView.setAdUnitId(banner_admob_inApp)
//        mAdView.setAdSize(getAdSize((context as Activity)))
//        mAdView.setAdListener(object : AdListener() {
//            override fun onAdLoaded() {
//                super.onAdLoaded()
//                Log.d("ConstantAdsLoadAds", "Bannen onAdLoaded: ")
//                try {
//                    adContainer.removeAllViews()
//                    adContainer.addView(mAdView)
//                } catch (e: Exception) {
//                    e.printStackTrace()
//                }
//            }
//
//            override fun onAdFailedToLoad(loadAdError: LoadAdError) {
//                super.onAdFailedToLoad(loadAdError)
//                Log.d("ConstantAdsLoadAds", "Bannen onAdFailedToLoad: " + loadAdError.toString())
//                mAdView.destroy()
//
////                loadMIStreetViewYandexBanner(adContainer,mView,context)
//            }
//        })
//        try {
//            mAdView.loadAd(AdRequest.Builder().build())
//        } catch (e: Exception) {
//            e.printStackTrace()
//        }
//    }


    //new banner code
//    fun loadHoneyBeeMapNavigationBannerForMainMediation(
//        adContainer: LinearLayout,
//        mView: View,
//        viewShimmerFrameLayout: View,
//        context: Context
//    ) {
//        val billingHelper = PhoneLocatorAppPurchaseHelper(context)
//        if (billingHelper.shouldShowAds()) {
//            loadHoneyBeeMapNavigationAdMobBanner(adContainer, mView, viewShimmerFrameLayout, context)
//        } else {
//            Log.d("BannerAdMob", "purchase: ")
//            mView.visibility = View.GONE
//            viewShimmerFrameLayout.visibility = View.GONE
//        }
//    }
//
//    private fun loadHoneyBeeMapNavigationAdMobBanner(
//        adContainer: LinearLayout,
//        mView: View,
//        viewShimmerFrameLayout: View,
//        context: Context
//    ) {
//        val mAdView = AdView(context)
//        mAdView.adUnitId = banner_admob_inApp
//        mAdView.setAdSize(getAdSize((context as Activity)))
//        try {
//            mAdView.loadAd(AdRequest.Builder().build())
//        } catch (e: Exception) {
//            e.printStackTrace()
//        }
//        mAdView.adListener = object : AdListener() {
//            override fun onAdLoaded() {
//                super.onAdLoaded()
//                Log.d("ConstantAdsLoadAds", "Banner onAdLoaded: ")
//                viewShimmerFrameLayout.visibility = View.GONE
//                mView.visibility = View.VISIBLE
//                adContainer.visibility = View.VISIBLE
//                try {
//                    adContainer.removeAllViews()
//                    adContainer.addView(mAdView)
//                } catch (e: Exception) {
//                    e.printStackTrace()
//                }
//            }
//            override fun onAdFailedToLoad(loadAdError: LoadAdError) {
//                super.onAdFailedToLoad(loadAdError)
//                Log.d("ConstantAdsLoadAds", "Banner onAdFailedToLoad: $loadAdError")
//                mAdView.destroy()
//                mView.visibility = View.GONE
//                viewShimmerFrameLayout.visibility = View.GONE
//            }
//            override fun onAdClicked() {
//                super.onAdClicked()
//                // Add your tracking if needed
//                // ShowAds.trackUserEventClick("BannerAdClick", context)
//                // onAdClick++
//            }
//        }
//    }

    fun loadHoneyBeeMapNavigationBannerForMainMediation(
        adContainer: LinearLayout,
        mView: View,
        viewShimmerFrameLayout: View,
        context: Context
    ) {
        val billingHelper = PhoneLocatorAppPurchaseHelper(context)
        if (billingHelper.shouldShowAds()) {
            loadHoneyBeeMapNavigationAdMobCollapsibleBanner(
                adContainer,
                mView,
                viewShimmerFrameLayout,
                context
            )
        } else {
            Log.d("BannerAdMob", "purchase: ads disabled")
            mView.visibility = View.GONE
            viewShimmerFrameLayout.visibility = View.GONE
        }
    }

    private fun loadHoneyBeeMapNavigationAdMobCollapsibleBanner(
        adContainer: LinearLayout,
        mView: View,
        viewShimmerFrameLayout: View,
        context: Context
    ) {
        val mAdView = AdView(context)
        mAdView.adUnitId =
            banner_admob_inApp // or use collapsible_banner_admob if you have separate ID
        mAdView.setAdSize(getAdSize((context as Activity)))
        try {
            val extras = Bundle()
            extras.putString("collapsible", "bottom")

            val adRequest = AdRequest.Builder()
                .addNetworkExtrasBundle(AdMobAdapter::class.java, extras)
                .build()

            mAdView.loadAd(adRequest)
        } catch (e: Exception) {
            e.printStackTrace()
        }

        mAdView.adListener = object : AdListener() {
            override fun onAdLoaded() {
                super.onAdLoaded()
                Log.d("ConstantAdsLoadAds", "onAdLoaded: bannerCollapsible load")
                viewShimmerFrameLayout.visibility = View.GONE
                mView.visibility = View.VISIBLE
                adContainer.visibility = View.VISIBLE
                try {
                    adContainer.removeAllViews()
                    adContainer.addView(mAdView)
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }

            override fun onAdFailedToLoad(loadAdError: LoadAdError) {
                super.onAdFailedToLoad(loadAdError)
                Log.d(
                    "ConstantAdsLoadAds",
                    "onAdFailedToLoad: bannerCollapsible failed - ${loadAdError.message}"
                )
                mAdView.destroy()
                mView.visibility = View.GONE
                viewShimmerFrameLayout.visibility = View.GONE

                // Optional: Load backup ad network
                // loadBackupBanner(adContainer, mView, viewShimmerFrameLayout, context)
            }

            override fun onAdClicked() {
                super.onAdClicked()
                Log.d("ConstantAdsLoadAds", "Collapsible banner clicked")
                // Add your tracking if needed
            }
        }
    }

//collpasabe banner old code but without refreshing

//    fun collapsableBanner(
//        adContainer: LinearLayout,
//        view: LinearLayout,
//        context: Context
//    ) {
//        val billingHelper: PhoneLocatorAppPurchaseHelper =
//            PhoneLocatorAppPurchaseHelper(context)
//        if (billingHelper.shouldShowAds()) {
//            val mAdView = AdView(context)
//
//            mAdView.setAdSize(
//                getAdSize(
//                    (context as Activity)
//                )
//            )
////            mAdView.setAdUnitId("ca-app-pub-3940256099942544/2014213617") // Test collapsible banner
//            mAdView.setAdUnitId(banner_admob_inApp)
//
//            try {
//                val extras = Bundle()
//                extras.putString("collapsible", "bottom")
//                val adRequest = AdRequest.Builder()
//                    .addNetworkExtrasBundle(AdMobAdapter::class.java, extras)
//                    .build()
//
//                // Debug log
//                Log.d("CollapsibleBanner", "🔥 Loading COLLAPSIBLE banner with position: bottom")
//
//                mAdView.loadAd(adRequest)
//            } catch (e: Exception) {
//                e.printStackTrace()
//                Log.e("CollapsibleBanner", "Exception: ${e.message}")
//            }
//
//            mAdView.setAdListener(object : AdListener() {
//                //                override fun onAdLoaded() {
////                    super.onAdLoaded()
////                    Log.d("CollapsibleBanner", "✅ COLLAPSIBLE Ad Loaded Successfully")
////                    Log.d(
////                        "CollapsibleBanner",
////                        "👉 Look for a small arrow button (⌄) on the right side of the banner"
////                    )
////
////                    view.visibility = View.VISIBLE
////                    adContainer.visibility = View.VISIBLE
////
////                    try {
////                        adContainer.removeAllViews()
////                        adContainer.addView(mAdView)
////                        Log.d("CollapsibleBanner", "✅ AdView added to container")
////                    } catch (e: Exception) {
////                        e.printStackTrace()
////                        Log.e("CollapsibleBanner", "❌ Error adding ad: ${e.message}")
////                    }
////                }
//                override fun onAdLoaded() {
//                    super.onAdLoaded()
//                    view.visibility = View.VISIBLE
//                    adContainer.visibility = View.VISIBLE
//                    adContainer.removeAllViews()
//                    adContainer.addView(mAdView)
//                    Log.d("CollapsibleBanner", "✅ AdView added to container")
//
//                    // ── Auto-refresh every 15 seconds ────────────────────────────
//                    val handler = Handler(Looper.getMainLooper())
//                    handler.postDelayed(object : Runnable {
//                        override fun run() {
//                            if (adContainer.isAttachedToWindow) {
//                                adContainer.tag = null  // reset guard so new request fires
//                                Log.d("CollapsibleBanner", "🔄 Refreshing banner after 15s")
//                                collapsableBanner(adContainer, view, context)
//                            }
//                        }
//                    }, 15_000)
//                    // ─────────────────────────────────────────────────────────────
//                }
//
//                override fun onAdFailedToLoad(loadAdError: LoadAdError) {
//                    super.onAdFailedToLoad(loadAdError)
//                    Log.e("CollapsibleBanner", "❌ Failed: ${loadAdError.message}")
//                    Log.e("CollapsibleBanner", "Error Code: ${loadAdError.code}")
//                    Log.e("CollapsibleBanner", "Domain: ${loadAdError.domain}")
//
//                    view.visibility = View.GONE
//                    mAdView.destroy()
//                }
//
//                override fun onAdClicked() {
//                    super.onAdClicked()
//                    Log.d("CollapsibleBanner", "🎯 Banner Clicked")
//                }
//            })
//        } else {
//            view.visibility = View.GONE
//            Log.d("CollapsibleBanner", "Ads disabled - user has purchased")
//        }
//    }


    fun collapsableBanner(
        adContainer: LinearLayout,
        view: LinearLayout,
        context: Context
    ) {
         bannerRefreshRunnable?.let { bannerRefreshHandler.removeCallbacks(it) }

        val billingHelper = PhoneLocatorAppPurchaseHelper(context)
        if (!billingHelper.shouldShowAds()) {
            view.visibility = View.GONE
            Log.d("CollapsibleBanner", "Ads disabled - user has purchased")
            return
        }

        if (adContainer.tag == "banner_loading") return
        adContainer.tag = "banner_loading"

        val mAdView = AdView(context)
        mAdView.setAdSize(getAdSize(context as Activity))
        mAdView.setAdUnitId(banner_admob_inApp)

        try {
            val builder = AdRequest.Builder()
            val useCollapsible = InAppABTestHelper.isCollapsibleBanner(context)
            if (useCollapsible) {
                val extras = Bundle()
                extras.putString("collapsible", "bottom")
                builder.addNetworkExtrasBundle(AdMobAdapter::class.java, extras)
                Log.d("CollapsibleBanner", "🔥 Loading COLLAPSIBLE banner (A/B Test)")
            } else {
                Log.d("CollapsibleBanner", "🔥 Loading STANDARD banner (A/B Test)")
            }
            val adRequest = builder.build()
            mAdView.loadAd(adRequest)
        } catch (e: Exception) {
            adContainer.tag = null
            e.printStackTrace()
            return
        }

        mAdView.setAdListener(object : AdListener() {
            override fun onAdLoaded() {
                super.onAdLoaded()
                view.visibility = View.VISIBLE
                adContainer.visibility = View.VISIBLE
                adContainer.removeAllViews()
                val lp = LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.WRAP_CONTENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
                )
                lp.gravity = android.view.Gravity.CENTER_HORIZONTAL
                mAdView.layoutParams = lp
                adContainer.addView(mAdView)
                Log.d("CollapsibleBanner", "✅ AdView added to container")

                // ── Schedule next refresh ─────────────────────────────────
                bannerRefreshRunnable = Runnable {
                    val billing = PhoneLocatorAppPurchaseHelper(context)
                    if (billing.shouldShowAds() && mainBanneradControl) {
                        Log.d("CollapsibleBanner", "🔄 Refreshing banner")
                        adContainer.tag = null
                        collapsableBanner(adContainer, view, context)
                    } else {
                        adContainer.visibility = View.GONE
                        view.visibility = View.GONE
                        Log.d("CollapsibleBanner", "🔄 Refresh skipped — ads suppressed")
                    }
                }
                bannerRefreshHandler.postDelayed(bannerRefreshRunnable!!, 30_000L)
                // ─────────────────────────────────────────────────────────
            }

            override fun onAdFailedToLoad(loadAdError: LoadAdError) {
                super.onAdFailedToLoad(loadAdError)
                Log.e("CollapsibleBanner", "❌ Failed: ${loadAdError.message}")
                view.visibility = View.GONE
                adContainer.tag = null
                mAdView.destroy()
            }

            override fun onAdClicked() {
                super.onAdClicked()
                Log.d("CollapsibleBanner", "🎯 Banner Clicked")
            }
        })
    }
    fun cancelBannerRefresh() {
        bannerRefreshRunnable?.let { bannerRefreshHandler.removeCallbacks(it) }
        bannerRefreshRunnable = null
        Log.d("CollapsibleBanner", "🛑 Banner refresh cancelled")
    }


//    fun loadStreetViewTruckCTCollapsibleAdMob(
//        adContainer: LinearLayout,
//        view: LinearLayout,
//        context: Context
//    ) {
//        val billingHelper: PhoneLocatorAppPurchaseHelper =
//            PhoneLocatorAppPurchaseHelper(context)
//        if (billingHelper.shouldShowAds()) {
//            val mAdView = AdView(context)
//
//            mAdView.setAdSize(getAdSize((context as Activity)))
//            mAdView.setAdUnitId(banner_admob_inApp)
//
//            try {
//                val extras = Bundle()
//                extras.putString("collapsible", "bottom")
//                val adRequest = AdRequest.Builder()
//                    .addNetworkExtrasBundle(AdMobAdapter::class.java, extras)
//                    .build()
//                mAdView.loadAd(adRequest)
//            } catch (e: java.lang.Exception) {
//                e.printStackTrace()
//            }
//
//            mAdView.setAdListener(object : AdListener() {
//                override fun onAdLoaded() {
//                    super.onAdLoaded()
//                    Log.d("ConstantAdsLoadAdscollapsabkle", "onAdLoaded: bannerCollapsible load")
//                    view.setVisibility(View.VISIBLE)
//                    try {
//                        adContainer.removeAllViews()
//                        adContainer.addView(mAdView)
//                    } catch (e: java.lang.Exception) {
//                        e.printStackTrace()
//                    }
//                }
//
//                override fun onAdFailedToLoad(@NonNull loadAdError: LoadAdError) {
//                    super.onAdFailedToLoad(loadAdError)
//                     mAdView.destroy()
//                    Log.d("ConstantAdsLoadAdscollapsabkle", "onAdFailedToLoad: bannerCollapsible failed")
//                 }
//                override fun onAdClicked() {
//                    super.onAdClicked()
//                }
//            })
//        } else {
//            view.setVisibility(View.GONE)
//        }
//    }\

//
//fun loadStreetViewTruckCTCollapsibleAdMob(
//    adContainer: LinearLayout,
//    view: LinearLayout,
//    context: Context
//) {
//    val billingHelper: PhoneLocatorAppPurchaseHelper =
//        PhoneLocatorAppPurchaseHelper(context)
//    if (billingHelper.shouldShowAds()) {
//        val mAdView = AdView(context)
//
//        mAdView.setAdSize(getAdSize((context as Activity)))
//        mAdView.setAdUnitId(banner_admob_inApp)
//
//        try {
//            val extras = Bundle()
//            extras.putString("collapsible", "bottom")
//            val adRequest = AdRequest.Builder()
//                .addNetworkExtrasBundle(AdMobAdapter::class.java, extras)
//                .build()
//            mAdView.loadAd(adRequest)
//        } catch (e: Exception) {
//            e.printStackTrace()
//        }
//
//        mAdView.setAdListener(object : AdListener() {
//            override fun onAdLoaded() {
//                super.onAdLoaded()
//                Log.d("CollapsibleBanner", "✅ Ad Loaded Successfully")
//
//                view.visibility = View.VISIBLE
//                adContainer.visibility = View.VISIBLE
//
//                try {
//                    adContainer.removeAllViews()
//                    adContainer.addView(mAdView)
//                    Log.d("CollapsibleBanner", "✅ AdView added to container")
//                } catch (e: Exception) {
//                    e.printStackTrace()
//                    Log.e("CollapsibleBanner", "❌ Error adding ad: ${e.message}")
//                }
//            }
//
//            override fun onAdFailedToLoad(loadAdError: LoadAdError) {
//                super.onAdFailedToLoad(loadAdError)
//                Log.e("CollapsibleBanner", "❌ Failed: ${loadAdError.message}")
//                Log.e("CollapsibleBanner", "Error Code: ${loadAdError.code}")
//
//                view.visibility = View.GONE
//                mAdView.destroy()
//            }
//
//            override fun onAdClicked() {
//                super.onAdClicked()
//                Log.d("CollapsibleBanner", "Banner Clicked")
//            }
//        })
//    } else {
//        view.visibility = View.GONE
//        Log.d("CollapsibleBanner", "Ads disabled - user has purchased")
//    }
//}


    //Collapspabe Banner
//    fun loadStreetViewTruckCTCollapsibleAdMob(
//        adContainer: LinearLayout,
//        view: LinearLayout,
//        context: Context
//    ) {
//        val billingHelper: PhoneLocatorAppPurchaseHelper =
//            PhoneLocatorAppPurchaseHelper(context)
//        if (billingHelper.shouldShowAds()) {
//            val mAdView = AdView(context)
//
//            mAdView.setAdSize(getAdSize((context as Activity)))
//            //            mAdView.setAdUnitId("ca-app-pub-3940256099942544/2014213617") // Test collapsible banner
//            mAdView.setAdUnitId(banner_admob_inApp) // Your production ID
//
//            try {
//                val extras = Bundle()
//                extras.putString("collapsible", "bottom")
//                val adRequest = AdRequest.Builder()
//                    .addNetworkExtrasBundle(AdMobAdapter::class.java, extras)
//                    .build()
//
//                // Debug log
//                Log.d("CollapsibleBanner", "🔥 Loading COLLAPSIBLE banner with position: bottom")
//
//                mAdView.loadAd(adRequest)
//            } catch (e: Exception) {
//                e.printStackTrace()
//                Log.e("CollapsibleBanner", "Exception: ${e.message}")
//            }
//
//            mAdView.setAdListener(object : AdListener() {
//                override fun onAdLoaded() {
//                    super.onAdLoaded()
//                    Log.d("CollapsibleBanner", "✅ COLLAPSIBLE Ad Loaded Successfully")
//                    Log.d(
//                        "CollapsibleBanner",
//                        "👉 Look for a small arrow button (⌄) on the right side of the banner"
//                    )
//
//                    view.visibility = View.VISIBLE
//                    adContainer.visibility = View.VISIBLE
//
//                    try {
//                        adContainer.removeAllViews()
//                        adContainer.addView(mAdView)
//                        Log.d("CollapsibleBanner", "✅ AdView added to container")
//                    } catch (e: Exception) {
//                        e.printStackTrace()
//                        Log.e("CollapsibleBanner", "❌ Error adding ad: ${e.message}")
//                    }
//                }
//
//                override fun onAdFailedToLoad(loadAdError: LoadAdError) {
//                    super.onAdFailedToLoad(loadAdError)
//                    Log.e("CollapsibleBanner", "❌ Failed: ${loadAdError.message}")
//                    Log.e("CollapsibleBanner", "Error Code: ${loadAdError.code}")
//                    Log.e("CollapsibleBanner", "Domain: ${loadAdError.domain}")
//
//                    view.visibility = View.GONE
//                    mAdView.destroy()
//                }
//
//                override fun onAdClicked() {
//                    super.onAdClicked()
//                    Log.d("CollapsibleBanner", "🎯 Banner Clicked")
//                }
//            })
//        } else {
//            view.visibility = View.GONE
//            Log.d("CollapsibleBanner", "Ads disabled - user has purchased")
//        }
//    }

    //Banner For splash
    fun loadHoneyBeeMapNavigationBannerForMainMediationSplash(
        adContainer: LinearLayout,
        mView: View,
        viewShimmerFrameLayout: View,
        context: Context
    ) {
        val billingHelper = PhoneLocatorAppPurchaseHelper(context)
        if (billingHelper.shouldShowAds()) {
            loadHoneyBeeMapNavigationAdMobBannerSplash(
                adContainer,
                mView,
                viewShimmerFrameLayout,
                context
            )
        } else {
            Log.d("BannerAdMob", "purchase: ")
            mView.visibility = View.GONE
            viewShimmerFrameLayout.visibility = View.GONE
        }
    }

    private fun loadHoneyBeeMapNavigationAdMobBannerSplash(
        adContainer: LinearLayout,
        mView: View,
        viewShimmerFrameLayout: View,
        context: Context
    ) {
        val mAdView = AdView(context)
        mAdView.adUnitId = admob_banner_id_Splash
        mAdView.setAdSize(getAdSize((context as Activity)))
        try {
            mAdView.loadAd(AdRequest.Builder().build())
        } catch (e: Exception) {
            e.printStackTrace()
        }
        mAdView.adListener = object : AdListener() {
            override fun onAdLoaded() {
                super.onAdLoaded()
                Log.d("ConstantAdsLoadAds", "Banner onAdLoaded: ")
                viewShimmerFrameLayout.visibility = View.GONE
                mView.visibility = View.VISIBLE
                adContainer.visibility = View.VISIBLE
                try {
                    adContainer.removeAllViews()
                    adContainer.addView(mAdView)
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }

            override fun onAdFailedToLoad(loadAdError: LoadAdError) {
                super.onAdFailedToLoad(loadAdError)
                Log.d("ConstantAdsLoadAds", "Banner onAdFailedToLoad: $loadAdError")
                mAdView.destroy()
                mView.visibility = View.GONE
                viewShimmerFrameLayout.visibility = View.GONE
            }

            override fun onAdClicked() {
                super.onAdClicked()
                // Add your tracking if needed
                // ShowAds.trackUserEventClick("BannerAdClick", context)
                // onAdClick++
            }
        }
    }


    //Banner FOr other than splash and banner
    fun loadHoneyBeeMapNavigationBannerForMainMediationforotherbanner(
        adContainer: LinearLayout,
        mView: View,
        viewShimmerFrameLayout: View,
        context: Context
    ) {
        val billingHelper = PhoneLocatorAppPurchaseHelper(context)
        if (billingHelper.shouldShowAds()) {
            loadHoneyBeeMapNavigationAdMobBannerforotherbanner(
                adContainer,
                mView,
                viewShimmerFrameLayout,
                context
            )
        } else {
            Log.d("BannerAdMob", "purchase: ")
            mView.visibility = View.GONE
            viewShimmerFrameLayout.visibility = View.GONE
        }
    }

    private fun loadHoneyBeeMapNavigationAdMobBannerforotherbanner(
        adContainer: LinearLayout,
        mView: View,
        viewShimmerFrameLayout: View,
        context: Context
    ) {
        val mAdView = AdView(context)
        mAdView.adUnitId = banner_admob_inApp
        mAdView.setAdSize(getAdSize((context as Activity)))
        try {
            mAdView.loadAd(AdRequest.Builder().build())
        } catch (e: Exception) {
            e.printStackTrace()
        }
        mAdView.adListener = object : AdListener() {
            override fun onAdLoaded() {
                super.onAdLoaded()
                Log.d("ConstantAdsLoadAds", "Banner onAdLoaded: ")
                viewShimmerFrameLayout.visibility = View.GONE
                mView.visibility = View.VISIBLE
                adContainer.visibility = View.VISIBLE
                try {
                    adContainer.removeAllViews()
                    adContainer.addView(mAdView)
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }

            override fun onAdFailedToLoad(loadAdError: LoadAdError) {
                super.onAdFailedToLoad(loadAdError)
                Log.d("ConstantAdsLoadAds", "Banner onAdFailedToLoad: $loadAdError")
                mAdView.destroy()
                mView.visibility = View.GONE
                viewShimmerFrameLayout.visibility = View.GONE
            }

            override fun onAdClicked() {
                super.onAdClicked()
                // Add your tracking if needed
                // ShowAds.trackUserEventClick("BannerAdClick", context)
                // onAdClick++
            }
        }
    }


    //1
//    fun preLoadAdsHoneyBee(context: Context) {
//        val billingHelper = PhoneLocatorAppPurchaseHelper(context)
//        if (billingHelper.shouldShowAds()) {
//            //admobeload
//            if (admobInterstitialAdHoneyBee == null) {
//                canReLoadedAdMob = false
//                InterstitialAd.load(
//                    context,
//                    interstitial_admob_inApp,
//                    AdRequest.Builder().build(),
//                    object : InterstitialAdLoadCallback() {
//                        override fun onAdLoaded(interstitialAd: InterstitialAd) {
//                            super.onAdLoaded(interstitialAd)
//                            Log.d("ConstantAdsLoadAds", "Admob loaded")
//                            canReLoadedAdMob = true
//                            admobInterstitialAdHoneyBee = interstitialAd
//                        }
//
//                        override fun onAdFailedToLoad(loadAdError: LoadAdError) {
//                            super.onAdFailedToLoad(loadAdError)
//                            Log.d("ConstantAdsLoadAds", "Admob Faild: " + loadAdError.toString())
//                            canReLoadedAdMob = true
//                            admobInterstitialAdHoneyBee = null
//                        }
//                    })
//            } else {
//                Log.d("ConstantAdsLoadAds", "admobe AlReady loaded")
//            }
//        }
//    }
//
//
//    //all
//    @JvmStatic
//    fun preReLoadAdsHoneyBee(context: Context) {
//        val billingHelper = PhoneLocatorAppPurchaseHelper(context)
//        if (billingHelper.shouldShowAds()) {
//            if (admobInterstitialAdHoneyBee != null) {
//                Log.d("ConstantAdsLoadAds", "admobe ReAlReady loaded")
//            } else {
//                Log.d("ConstantAdsLoadAds", "canReLoadedAdMob " + canReLoadedAdMob)
//                if (canReLoadedAdMob) {
//                    canReLoadedAdMob = false
//
//                    InterstitialAd.load(
//                        context,
//                        interstitial_admob_inApp,
//                        AdRequest.Builder().build(),
//                        object : InterstitialAdLoadCallback() {
//                            override fun onAdLoaded(interstitialAd: InterstitialAd) {
//                                super.onAdLoaded(interstitialAd)
//                                Log.d("ConstantAdsLoadAds", "Admob Reloaded")
//                                canReLoadedAdMob = true
//                                admobInterstitialAdHoneyBee = interstitialAd
//                            }
//
//                            override fun onAdFailedToLoad(loadAdError: LoadAdError) {
//                                super.onAdFailedToLoad(loadAdError)
//                                Log.d(
//                                    "ConstantAdsLoadAds",
//                                    "Admob ReFaild: " + loadAdError.toString()
//                                )
//                                canReLoadedAdMob = true
//                                admobInterstitialAdHoneyBee = null
//                            }
//                        })
//                } else {
//                    Log.d("ConstantAdsLoadAds", "Admob last ad request is in pending")
//                    canReLoadedAdMob = true
//
//                }
//            }
//
//        }
//    }


    fun preLoadAdsHoneyBee(context: Context) {
        // ── Kill switch — don't load if both interstitial types are disabled ──
        if (!canShowMainInterstitial && !canShowBackPressInterstitial) {
            Log.d("ConstantAdsLoadAds", "Both interstitials disabled — skipping load")
            admobInterstitialAdHoneyBee = null
            return
        }
        // ─────────────────────────────────────────────────────────────────────

        val billingHelper = PhoneLocatorAppPurchaseHelper(context)
        if (billingHelper.shouldShowAds()) {
            if (admobInterstitialAdHoneyBee == null) {
                canReLoadedAdMob = false
                InterstitialAd.load(
                    context,
                    interstitial_admob_inApp,
                    AdRequest.Builder().build(),
                    object : InterstitialAdLoadCallback() {
                        override fun onAdLoaded(interstitialAd: InterstitialAd) {
                            super.onAdLoaded(interstitialAd)
                            Log.d("ConstantAdsLoadAds", "Admob loaded")
                            canReLoadedAdMob = true
                            admobInterstitialAdHoneyBee = interstitialAd
                        }

                        override fun onAdFailedToLoad(loadAdError: LoadAdError) {
                            super.onAdFailedToLoad(loadAdError)
                            Log.d("ConstantAdsLoadAds", "Admob Faild: " + loadAdError.toString())
                            canReLoadedAdMob = true
                            admobInterstitialAdHoneyBee = null
                        }
                    })
            } else {
                Log.d("ConstantAdsLoadAds", "admobe AlReady loaded")
            }
        }
    }

    @JvmStatic
    fun preReLoadAdsHoneyBee(context: Context) {
        // ── Kill switch — don't reload if both interstitial types are disabled ──
        if (!canShowMainInterstitial && !canShowBackPressInterstitial) {
            Log.d("ConstantAdsLoadAds", "Both interstitials disabled — skipping reload")
            admobInterstitialAdHoneyBee = null
            return
        }
        // ──────────────────────────────────────────────────────────────────────

        val billingHelper = PhoneLocatorAppPurchaseHelper(context)
        if (billingHelper.shouldShowAds()) {
            if (admobInterstitialAdHoneyBee != null) {
                Log.d("ConstantAdsLoadAds", "admobe ReAlReady loaded")
            } else {
                Log.d("ConstantAdsLoadAds", "canReLoadedAdMob " + canReLoadedAdMob)
                if (canReLoadedAdMob) {
                    canReLoadedAdMob = false
                    InterstitialAd.load(
                        context,
                        interstitial_admob_inApp,
                        AdRequest.Builder().build(),
                        object : InterstitialAdLoadCallback() {
                            override fun onAdLoaded(interstitialAd: InterstitialAd) {
                                super.onAdLoaded(interstitialAd)
                                Log.d("ConstantAdsLoadAds", "Admob Reloaded")
                                canReLoadedAdMob = true
                                admobInterstitialAdHoneyBee = interstitialAd
                            }

                            override fun onAdFailedToLoad(loadAdError: LoadAdError) {
                                super.onAdFailedToLoad(loadAdError)
                                Log.d(
                                    "ConstantAdsLoadAds",
                                    "Admob ReFaild: " + loadAdError.toString()
                                )
                                canReLoadedAdMob = true
                                admobInterstitialAdHoneyBee = null
                            }
                        })
                } else {
                    Log.d("ConstantAdsLoadAds", "Admob last ad request is in pending")
                    canReLoadedAdMob = true
                }
            }
        }
    }


    fun loadSplashInterstitialAd(context: Context, splashAdsCallBack: SplashAdsCallBack) {
        val billingHelper = PhoneLocatorAppPurchaseHelper(context)
        if (billingHelper.shouldShowAds()) {
            InterstitialAd.load(
                context,
                admob_interstitial_splash_id,
                AdRequest.Builder().build(),
                object : InterstitialAdLoadCallback() {
                    override fun onAdLoaded(interstitialAd: InterstitialAd) {
                        super.onAdLoaded(interstitialAd)
                        Log.d("ConstantAdsLoadAds", "Admob Splash AdLoaded: ")

                        //HoneyBeeMapNavigationShowAds.logAnalyticsForClicks("SplashAdLoaded",context);
                        admobInterstitialAdHoneyBeeSplash = interstitialAd
                        splashAdsCallBack.onSplashResult()
                    }

                    override fun onAdFailedToLoad(loadAdError: LoadAdError) {
                        super.onAdFailedToLoad(loadAdError)
                        Log.d("ConstantAdsLoadAds", "Admob Splash Faild: " + loadAdError.toString())
                        // HoneyBeeMapNavigationShowAds.logAnalyticsForClicks("SplashAdFailedToLoad",context);
                        admobInterstitialAdHoneyBeeSplash = null
                        // splashAdsCallBack.onSplashResult()
//                        loadAdsSplashInterstitialYandex(context,splashAdsCallBack)
                    }
                })
        } else {
            splashAdsCallBack.onSplashResult()
        }
    }

    fun loadSplashOnboardingAd(context: Context, callback: AdLoadCallbackForIntro) {
        val billingHelper = PhoneLocatorAppPurchaseHelper(context)
        if (billingHelper.shouldShowAds()) {
            InterstitialAd.load(
                context,
                admob_interstitial_id_onBoarding,
                AdRequest.Builder().build(),
                object : InterstitialAdLoadCallback() {
                    override fun onAdLoaded(interstitialAd: InterstitialAd) {
                        super.onAdLoaded(interstitialAd)
                        Log.d("ConstantAdsLoadAds", "Admob Splash AdLoaded: ")
                        admobInterstitialAdHoneyBeeOnboarding = interstitialAd
                        callback.onAdResult() // ✅ notify on success

                    }

                    override fun onAdFailedToLoad(loadAdError: LoadAdError) {
                        super.onAdFailedToLoad(loadAdError)
                        Log.d("ConstantAdsLoadAds", "Admob Splash Faild: " + loadAdError.toString())
                        // HoneyBeeMapNavigationShowAds.logAnalyticsForClicks("SplashAdFailedToLoad",context);
                        admobInterstitialAdHoneyBeeOnboarding = null
                        callback.onAdResult() // ✅ notify on success

                    }
                })
        }
    }


//    fun loadAdsSplashInterstitialYandex(context: Context?, callBack: SplashAdsCallBack) {
//        Log.d("ConstantAdsLoadAds", "Splash Yandex")
//        val billingHelper = PhoneLocatorAppPurchaseHelper(context!!)
//            if (billingHelper.shouldShowAds()) {
//                if (splashInterstitialAdYandex == null) {
//                    Log.d("ConstantAdsLoadAds", "Yandex Splash sending request")
//
//                    val interstitialAdLoader = InterstitialAdLoader(context!!).apply {
//                        setAdLoadListener(object : InterstitialAdLoadListener {
//                            override fun onAdFailedToLoad(error: AdRequestError) {
//                                // Ad failed to load with AdRequestError.
//                                // Attempting to load a new ad from the onAdFailedToLoad() method is strongly discouraged.
//                                splashInterstitialAdYandex = null
//                                callBack.onSplashResult()
//                                Log.d(
//                                    "ConstantAdsLoadAds",
//                                    "Yandex Splash Interstitial failed: ${error.description}"
//                                )
//                            }
//
//                            override fun onAdLoaded(interstitialAd: com.yandex.mobile.ads.interstitial.InterstitialAd) {
//                                Log.d("ConstantAdsLoadAds", "Yandex Splash Interstitial onAdLoaded:")
//                                splashInterstitialAdYandex = interstitialAd
//                                callBack.onSplashResult()
//                            }
//                        })
//                    }
//                    val adRequestConfiguration = AdRequestConfiguration.Builder(
//                        yandex_interstitial_splash
//                    ).build()
//                    interstitialAdLoader.loadAd(adRequestConfiguration)
//                }
//            } else {
//                callBack.onSplashResult()
//            }
//
//    }

    /*not using*/
//    fun insertADSToFirebase(mContext: Context?) {
//        databaseReference.child("ADS_IDS").setValue(
//            MyAdsModel(
//                appid_admob_inApp,
//                banner_admob_inApp,
//                banner_large_admob_inApp,
//                app_open_high_splash_ad_id_admob,
//                app_open_ad_id_admob,
//                native_admob_inApp,
//                interstitial_admob_inApp,
//                shouldShowAdmob,
//                current_counter,
//                next_ads_time.toDouble(),
//                ctr_control,
//                adShowAfter
//            )
//        )
//    }

    //reels
    fun preLoadReelsInterstitial(context: Context) {
        val billingHelper = PhoneLocatorAppPurchaseHelper(context)
        if (billingHelper.shouldShowAds()) {
            if (admobReelsInterstitialAd == null) {
                InterstitialAd.load(
                    context,
                    admob_reels_interstitial_id,
                    AdRequest.Builder().build(),
                    object : InterstitialAdLoadCallback() {
                        override fun onAdLoaded(interstitialAd: InterstitialAd) {
                            super.onAdLoaded(interstitialAd)
                            admobReelsInterstitialAd = interstitialAd
                            Log.d("ConstantAdsLoadAds", "Reels Admob loaded")
                        }

                        override fun onAdFailedToLoad(loadAdError: LoadAdError) {
                            super.onAdFailedToLoad(loadAdError)
                            admobReelsInterstitialAd = null
                            Log.d(
                                "ConstantAdsLoadAds",
                                "Reels Admob failed: ${loadAdError.message}"
                            )
                        }
                    })
            } else {
                Log.d("ConstantAdsLoadAds", "Reels Admob already loaded")
            }
        }
    }

    interface AdLoadCallbackForIntro {
        fun onAdResult()
    }

}
