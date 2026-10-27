package com.videoplayer.hdvideos.vrplayer1.Splash

import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.os.CountDownTimer
import android.os.Handler
import android.os.Looper
import android.util.Log
import androidx.activity.OnBackPressedCallback
import androidx.activity.enableEdgeToEdge
import androidx.core.content.edit
import androidx.core.view.ViewCompat
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.lifecycleScope
import com.facebook.ads.AudienceNetworkAds
import com.google.android.gms.ads.MobileAds
import com.google.android.gms.ads.RequestConfiguration
import com.google.android.ump.ConsentForm
import com.google.android.ump.ConsentInformation
import com.google.android.ump.ConsentRequestParameters
import com.google.android.ump.UserMessagingPlatform
import com.google.firebase.FirebaseApp
import com.google.firebase.messaging.FirebaseMessaging
import com.google.firebase.remoteconfig.FirebaseRemoteConfig
import com.google.firebase.remoteconfig.FirebaseRemoteConfigSettings
import com.videoplayer.hdvideos.vrplayer1.BuildConfig
import com.videoplayer.hdvideos.vrplayer1.InApp.InAppActivity
import com.videoplayer.hdvideos.vrplayer1.InApp.InAppBActivity
import com.videoplayer.hdvideos.vrplayer1.InApp.InAppCActivity
import com.videoplayer.hdvideos.vrplayer1.Main.NewMainActivity
import com.videoplayer.hdvideos.vrplayer1.OnBoarding.OnboardingActivity
import com.videoplayer.hdvideos.vrplayer1.R
import com.videoplayer.hdvideos.vrplayer1.StreakManager.StreakAdGate
import com.videoplayer.hdvideos.vrplayer1.StreakManager.StreakManager
import com.videoplayer.hdvideos.vrplayer1.ads.AppOpenAdResume
import com.videoplayer.hdvideos.vrplayer1.ads.InAppABTestHelper
import com.videoplayer.hdvideos.vrplayer1.ads.MyApp
import com.videoplayer.hdvideos.vrplayer1.ads.PhoneLocatorAppPurchaseHelper
import com.videoplayer.hdvideos.vrplayer1.ads.PhoneLocatorLoadAds
import com.videoplayer.hdvideos.vrplayer1.ads.PhoneLocatorLoadAds.OnboardingAdshow
import com.videoplayer.hdvideos.vrplayer1.ads.PhoneLocatorLoadAds.canShowBannerOnsplash
import com.videoplayer.hdvideos.vrplayer1.ads.PhoneLocatorLoadAds.canShowFirstAdInter
import com.videoplayer.hdvideos.vrplayer1.ads.PhoneLocatorLoadAds.canShowInappIcon
import com.videoplayer.hdvideos.vrplayer1.ads.PhoneLocatorLoadAds.canShowNativeOnBoarding
import com.videoplayer.hdvideos.vrplayer1.ads.PhoneLocatorLoadAds.canShowNativeOnBoardingFullScreen
import com.videoplayer.hdvideos.vrplayer1.ads.PhoneLocatorLoadAds.canShowOnBoarding
import com.videoplayer.hdvideos.vrplayer1.ads.PhoneLocatorLoadAds.canShowPremium
import com.videoplayer.hdvideos.vrplayer1.ads.PhoneLocatorLoadAds.inAppVariant
import com.videoplayer.hdvideos.vrplayer1.ads.PhoneLocatorLoadAds.mainBanneradControl
import com.videoplayer.hdvideos.vrplayer1.ads.PhoneLocatorLoadAds.reelsInterstitialShow
import com.videoplayer.hdvideos.vrplayer1.ads.PhoneLocatorShowAds
import com.videoplayer.hdvideos.vrplayer1.ads.PreferenceHelper
import com.videoplayer.hdvideos.vrplayer1.ads.utlites.SplashAdsCallBack
import com.videoplayer.hdvideos.vrplayer1.databinding.ActivitySplashBinding
import com.videoplayer.hdvideos.vrplayer1.pickGallerydata.utiles.BaseActivity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch


class SplashActivity : BaseActivity() {

    private lateinit var binding: ActivitySplashBinding
    private lateinit var consentInformation: ConsentInformation
    private var isAdFound = true
    private var sharedPreferences: SharedPreferences? = null
    private lateinit var sp: SharedPreferences

    private var billingHelper: PhoneLocatorAppPurchaseHelper? = null
    private lateinit var countDownTimer: CountDownTimer

    private val delayInMillis: Long = 15000
    private var adsInitialized = false


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivitySplashBinding.inflate(layoutInflater)
        setContentView(binding.root)
        com.videoplayer.hdvideos.vrplayer1.ads.AnalyticsHelper.logScreen(this, "screen_splash")
        onBackPressedDispatcher.addCallback(this, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {}
        })

        WindowCompat.setDecorFitsSystemWindows(window, false)
        com.videoplayer.hdvideos.vrplayer1.Main.VideoRepository.prewarm(this)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.mainsplahs)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        //for testing
//        if (BuildConfig.DEBUG) {
//            val testStreakMgr = StreakManager(this)
//
//            if (!testStreakMgr.isRewardActive) {
//                testStreakMgr.forceSetDay(6) // always day 7 when no active reward
////                testStreakMgr.forceGrantReward()   // simulate active reward
//
//            }
//            // Uncomment ONE at a time to test specific scenarios:
//            // testStreakMgr.forceExpireReward()  // simulate expired reward
//            // testStreakMgr.resetAll()           // wipe everything, start from day 1
//        }


        //for testing with log with complete dialog
//        if (BuildConfig.DEBUG) {
//            val testStreakMgr = StreakManager(this)
//            val debugPrefs = getSharedPreferences("DebugPrefs", MODE_PRIVATE)
//            val rewardWasClaimed = debugPrefs.getBoolean("reward_was_claimed", false)
//
//            when {
//                // Reward currently active — do nothing, let it run
//                testStreakMgr.isRewardActive -> {
//                    Log.d("StreakTest", "DEBUG: Reward active, skipping forceSetDay")
//                }
//
//                // Reward was claimed and now expired — reset the claimed flag
//                // so next relaunch starts fresh test cycle from day 7
//                rewardWasClaimed -> {
//                    Log.d("StreakTest", "DEBUG: Reward expired — resetting for next test cycle")
//                    debugPrefs.edit().putBoolean("reward_was_claimed", false).apply()
//                    testStreakMgr.resetAll() // wipe streak so day 1 starts naturally
//                    // Don't forceSetDay here — let it start from day 1 after expiry
//                }
//
//                // Fresh state — set day 7 for testing
//                else -> {
//                    Log.d("StreakTest", "DEBUG: Setting day 7 for testing")
//                    testStreakMgr.forceSetDay(7)
//                }
//            }
//        }

        lifecycleScope.launch(Dispatchers.IO) {
            try {
                FirebaseApp.initializeApp(this@SplashActivity)
                FirebaseMessaging.getInstance()
                    .subscribeToTopic("com.vrplayer.videoplayer360.vrplayer360")
            } catch (e: Exception) {
                Log.e("SplashActivity", "Subscription failed", e)
            }
        }

        billingHelper = PhoneLocatorAppPurchaseHelper(this)
        sharedPreferences = getSharedPreferences(packageName, MODE_PRIVATE)
        sp = getSharedPreferences(packageName, MODE_PRIVATE)


        val testDeviceIds = listOf("F0A89A2E1C62118BA0BDEE5E6DF59499")
        MobileAds.setRequestConfiguration(
            RequestConfiguration.Builder().setTestDeviceIds(testDeviceIds).build()
        )
        AudienceNetworkAds.initialize(applicationContext)

        fetchRemoteValueForTimer()
        consentFormRequest()
    }

    // ─────────────────────────────────────────────────────────────────────────
    // CONSENT
    // Every possible exit from this function calls initializeAds().
    // There is NO path that skips ads and jumps straight to moveToMain().
    // ─────────────────────────────────────────────────────────────────────────
//    private fun consentFormRequest() {
//        val params = ConsentRequestParameters.Builder().setTagForUnderAgeOfConsent(false).build()
//        // ── Check reward/paid state BEFORE consent fires ──────────────────
//// Consent callback runs after Remote Config may have set SplashShow=true
//// Reading from SharedPrefs directly is always accurate regardless of timing
//        val isRewardActive = StreakAdGate.isStreakRewardActive(this)
//        val isPaidUser     = !billingHelper!!.shouldShowAds()
//        val skipAds        = isRewardActive || isPaidUser
//
//        consentInformation = UserMessagingPlatform.getConsentInformation(this)
//        consentInformation.requestConsentInfoUpdate(this, params, {
//            // Consent info updated — try to show the form if required
//            UserMessagingPlatform.loadAndShowConsentFormIfRequired(
//                this, ConsentForm.OnConsentFormDismissedListener { formError ->
//                    if (formError != null) {
//                        Log.d("SplashActivity", "Consent form error: ${formError.message}")
//                    }
//                    Log.d(
//                        "SplashActivity", "canRequestAds: ${consentInformation.canRequestAds()}"
//                    )
//                    // Always proceed — never leave the user stuck with no navigation
//                    initializeAds()
//                })
//        }, { requestConsentError ->
//            // Consent request itself failed (network, etc.) — still load ads
//            Log.d("SplashActivity", "Consent request error: ${requestConsentError.message}")
//            initializeAds()
//        })
//    }
    private fun consentFormRequest() {
        val params = ConsentRequestParameters.Builder()
            .setTagForUnderAgeOfConsent(false)
            .build()

        // ── Check reward/paid state BEFORE consent fires ──────────────────
        // Consent callback runs after Remote Config may have set SplashShow=true
        // Reading from SharedPrefs directly is always accurate regardless of timing
        val isRewardActive = StreakAdGate.isStreakRewardActive(this)
        val isPaidUser = !billingHelper!!.shouldShowAds()
        val skipAds = isRewardActive || isPaidUser
        // ─────────────────────────────────────────────────────────────────

        consentInformation = UserMessagingPlatform.getConsentInformation(this)
        consentInformation.requestConsentInfoUpdate(this, params, {
            // Consent info updated — try to show the form if required
            UserMessagingPlatform.loadAndShowConsentFormIfRequired(
                this, ConsentForm.OnConsentFormDismissedListener { formError ->
                    if (formError != null) {
                        Log.d("SplashActivity", "Consent form error: ${formError.message}")
                    }
                    Log.d("SplashActivity", "canRequestAds: ${consentInformation.canRequestAds()}")

                    // Always proceed — never leave the user stuck with no navigation
                    if (skipAds) {
                        // Reward/paid active — skip splash ad, navigate directly
                        Log.d("TAG", "Reward/paid active — skipping splash ad")
                        Handler(Looper.getMainLooper()).postDelayed({
                            moveToMain()
                        }, 500)
                    } else {
                        initializeAds()
                    }
                })
        }, { requestConsentError ->
            // Consent request itself failed (network, etc.)
            Log.d("SplashActivity", "Consent request error: ${requestConsentError.message}")

            if (skipAds) {
                Log.d("TAG", "Reward/paid active — skipping splash ad on consent error")
                Handler(Looper.getMainLooper()).postDelayed({
                    moveToMain()
                }, 500)
            } else {
                initializeAds()
            }
        })
    }

    private fun initializeAds() {
        if (adsInitialized) return
        adsInitialized = true

        // (1) MobileAds full init in background — non-blocking
        lifecycleScope.launch(Dispatchers.IO) {
            try {
                MobileAds.initialize(this@SplashActivity) { status ->
                    status.adapterStatusMap.forEach { (adapter, s) ->
                        Log.d("MobileAds", "Adapter: $adapter, Latency: ${s.latency}ms")
                    }
                    Log.d("MobileAds", "Initialization complete")
                }
                AudienceNetworkAds.initialize(this@SplashActivity)
            } catch (e: Exception) {
                Log.e("SplashActivity", "MobileAds init failed", e)
            }
        }

        requestInterstitialAd()
        startProgressBar()
        Handler(Looper.getMainLooper()).postDelayed({
            Log.d("SplashActivity", "22 s deadline reached — navigating")
            moveToMain()
        }, delayInMillis)
    }

    private fun requestInterstitialAd() {
        PhoneLocatorLoadAds.loadSplashInterstitialAd(
            this, object : SplashAdsCallBack {
                override fun onSplashResult() {
                    Log.d("SplashActivity", "Interstitial ready — navigating early")
                    moveToMain()
                }
            })
    }

    private fun startProgressBar() {
        binding.progress.progress = 0
        countDownTimer = object : CountDownTimer(delayInMillis, 100) {
            override fun onTick(millisUntilFinished: Long) {
                val progress =
                    ((delayInMillis - millisUntilFinished).toFloat() / delayInMillis * 100).toInt()
                binding.progress.progress = progress
            }

            override fun onFinish() {
                binding.progress.progress = 100
            }
        }
        countDownTimer.start()
    }


    private fun moveToMain() {
        if (isAdFound) {
            isAdFound = false

            val hasPurchased = !(billingHelper?.shouldShowAds() ?: true)
            val isFirstInstall = sharedPreferences?.getBoolean("isFirstInstalled", true) ?: true

            val intent = when {
                isFirstInstall && canShowOnBoarding && !PreferenceHelper.isOnboardingDone(this) -> Intent(
                    this,
                    OnboardingActivity::class.java
                )

                canShowPremium && !hasPurchased ->
//                    Intent(this, InAppActivity::class.java)
                    getInAppIntent()


                else -> Intent(this, NewMainActivity::class.java)
            }

            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            intentWithAd(intent)
        }
    }

    //    private fun getInAppIntent(): Intent {
//        return when (inAppVariant) {
//            "B" -> Intent(this, InAppBActivity::class.java)
//            "C" -> Intent(this, InAppCActivity::class.java)
//            else -> Intent(this, InAppActivity::class.java)
//        }
//    }
    private fun getInAppIntent(): Intent {
        return InAppABTestHelper.getInAppIntent(this)
    }

    private fun intentWithAd(intent: Intent) {
        PhoneLocatorShowAds.splashClearActivityHoneyBeeMapNavigation(
            this, PhoneLocatorLoadAds.admobInterstitialAdHoneyBeeSplash, intent
        )
    }


    private fun fetchRemoteValueForTimer() {
        val remoteConfig = FirebaseRemoteConfig.getInstance()
        remoteConfig.setConfigSettingsAsync(
            FirebaseRemoteConfigSettings.Builder().setMinimumFetchIntervalInSeconds(30).build()
        )
        remoteConfig.fetchAndActivate().addOnCompleteListener(this) { task ->
            if (task.isSuccessful) {
                PhoneLocatorLoadAds.adShowAfter = remoteConfig.getDouble("adShowAfter").toInt()
                Log.d("RemoteConfig", "adShowAfter:${PhoneLocatorLoadAds.adShowAfter}")
                canShowOnBoarding = remoteConfig.getBoolean("canShowOnBoarding")
                canShowPremium = remoteConfig.getBoolean("canShowPremium")
                OnboardingAdshow = remoteConfig.getBoolean("OnboardingAdshow")
                canShowFirstAdInter = remoteConfig.getBoolean("canShowFirstAdInter")
                canShowInappIcon = remoteConfig.getBoolean("canShowInappIcon")
                canShowBannerOnsplash = remoteConfig.getBoolean("canShowBannerOnsplash")
                canShowNativeOnBoarding = remoteConfig.getBoolean("canShowNativeOnBoarding")
                mainBanneradControl = remoteConfig.getBoolean("mainBanneradControl")
                canShowNativeOnBoardingFullScreen =
                    remoteConfig.getBoolean("canShowNativeOnBoardingFullScreen")
                //Banner splash control
                PhoneLocatorLoadAds.canShowSplashInterstitial =
                    remoteConfig.getBoolean("canShowSplashInterstitial")
                Log.d(
                    "RemoteConfig",
                    "canShowSplashInterstitial:${PhoneLocatorLoadAds.canShowSplashInterstitial}"
                )

                reelsInterstitialShow = remoteConfig.getBoolean("reelsInterstitialShow")

                val reelsCounts = remoteConfig.getDouble("nextReelsShowCount")
                PhoneLocatorLoadAds.nextreelsShow = reelsCounts.toInt()
                Log.d("RemoteConfig", "nextReelsShowCount: ${PhoneLocatorLoadAds.nextreelsShow}")

                // CORRECT — setting it on PhoneLocatorLoadAds
                PhoneLocatorLoadAds.canShowAppOpen = remoteConfig.getBoolean("canShowAppOpen")
                Log.d("RemoteConfig", "canShowAppOpen:${PhoneLocatorLoadAds.canShowAppOpen}")

                PhoneLocatorLoadAds.inAppVariant = remoteConfig.getString("inAppVariant")
                Log.d("RemoteConfig", "inAppVariant:$inAppVariant")


                Log.d("RemoteConfig", "canShowOnBoarding:$canShowOnBoarding")
                Log.d("RemoteConfig", "canShowPremium:$canShowPremium")
                Log.d("RemoteConfig", "OnboardingAdshow:$OnboardingAdshow")
                Log.d("RemoteConfig", "canShowFirstAdInter:$canShowFirstAdInter")
                Log.d("RemoteConfig", "canShowInappIcon:$canShowInappIcon")
                Log.d("RemoteConfig", "canShowBannerOnsplash:$canShowBannerOnsplash")
                Log.d("RemoteConfig", "canShowNativeOnBoarding:$canShowNativeOnBoarding")
                Log.d("RemoteConfig", "mainBanneradControl:$mainBanneradControl")
                Log.d(
                    "RemoteConfig",
                    "canShowNativeOnBoardingFullScreen:$canShowNativeOnBoardingFullScreen"
                )

                sp.edit {
                    putString("Ai_ART", remoteConfig.getString("Ai_ART"))
                    putString("ART", remoteConfig.getString("ART"))
                    putString("Dance", remoteConfig.getString("Dance"))
                    putString("fantasy", remoteConfig.getString("fantasy"))
                    putString("Food", remoteConfig.getString("Food"))
                    putString("Funny", remoteConfig.getString("Funny"))
                    putString("Heal_and_fitness", remoteConfig.getString("Heal_and_fitness"))
                    putString("learning", remoteConfig.getString("learning"))
                    putString("Music", remoteConfig.getString("Music"))
                    putString("Viral", remoteConfig.getString("Viral"))
                }
                sp.edit {
                    putString("Gaming", remoteConfig.getString("Gaming"))
                    putString("Meditation", remoteConfig.getString("Meditation"))
                }

                PhoneLocatorLoadAds.canShowNativeOnBoardingFullScreenafterfirst =
                    remoteConfig.getBoolean("canShowNativeOnBoardingFullScreenafterfirst")
                Log.d(
                    "RemoteConfig",
                    "canShowNativeOnBoardingFullScreenafterfirst:${PhoneLocatorLoadAds.canShowNativeOnBoardingFullScreenafterfirst}"
                )

                PhoneLocatorLoadAds.canShowAdsOnTimer = remoteConfig.getBoolean("canShowAdsOnTimer")
                PhoneLocatorLoadAds.adTimerIntervalMillis =
                    remoteConfig.getLong("adTimerIntervalMillis")
                Log.d("RemoteConfig", "canShowAdsOnTimer:${PhoneLocatorLoadAds.canShowAdsOnTimer}")
                Log.d(
                    "RemoteConfigt",
                    "adTimerIntervalMillis:${PhoneLocatorLoadAds.adTimerIntervalMillis}"
                )


                PhoneLocatorLoadAds.canShowMainInterstitial =
                    remoteConfig.getBoolean("canShowMainInterstitial")
                PhoneLocatorLoadAds.canShowBackPressInterstitial =
                    remoteConfig.getBoolean("canShowBackPressInterstitial")
                Log.d(
                    "RemoteConfig",
                    "canShowMainInterstitial:${PhoneLocatorLoadAds.canShowMainInterstitial}"
                )
                Log.d(
                    "RemoteConfig",
                    "canShowBackPressInterstitial:${PhoneLocatorLoadAds.canShowBackPressInterstitial}"
                )

                // In-app screen Remote Config controls
                val inAppCtaText = remoteConfig.getString("inAppCtaText")
                if (inAppCtaText.isNotBlank()) PhoneLocatorLoadAds.inAppCtaText = inAppCtaText
                val inAppSubtitle = remoteConfig.getString("inAppSubtitle")
                if (inAppSubtitle.isNotBlank()) PhoneLocatorLoadAds.inAppSubtitle = inAppSubtitle
                val inAppSaveBadge = remoteConfig.getString("inAppSaveBadge")
                if (inAppSaveBadge.isNotBlank()) PhoneLocatorLoadAds.inAppSaveBadge = inAppSaveBadge
                fun remoteBool(key: String, current: Boolean): Boolean =
                    if (remoteConfig.getValue(key).source ==
                        com.google.firebase.remoteconfig.FirebaseRemoteConfig.VALUE_SOURCE_REMOTE
                    ) remoteConfig.getBoolean(key) else current
                PhoneLocatorLoadAds.canShowInAppA =
                    remoteBool("canShowInAppA", PhoneLocatorLoadAds.canShowInAppA)
                PhoneLocatorLoadAds.canShowInAppB =
                    remoteBool("canShowInAppB", PhoneLocatorLoadAds.canShowInAppB)
                PhoneLocatorLoadAds.canShowInAppC =
                    remoteBool("canShowInAppC", PhoneLocatorLoadAds.canShowInAppC)
                if (remoteConfig.getValue("onboarding_premium_card").source ==
                    com.google.firebase.remoteconfig.FirebaseRemoteConfig.VALUE_SOURCE_REMOTE
                ) {
                    PhoneLocatorLoadAds.onboardingPremiumCard =
                        remoteConfig.getBoolean("onboarding_premium_card")
                }
                Log.d(
                    "RemoteConfig",
                    "onboarding_premium_card:${PhoneLocatorLoadAds.onboardingPremiumCard}"
                )
                Log.d("RemoteConfig", "inAppCtaText:${PhoneLocatorLoadAds.inAppCtaText}")
                Log.d("RemoteConfig", "canShowInAppA:${PhoneLocatorLoadAds.canShowInAppA}")
                Log.d("RemoteConfig", "canShowInAppB:${PhoneLocatorLoadAds.canShowInAppB}")
                Log.d("RemoteConfig", "canShowInAppC:${PhoneLocatorLoadAds.canShowInAppC}")


                // ── FIX: Re-apply streak gate AFTER Remote Config overwrites flags ──
// Remote Config just set everything back to true — if reward is active
// we need to kill those flags again right here.
                val suppressed = StreakAdGate.applyAdState(this@SplashActivity)

                if (canShowBannerOnsplash && !suppressed) {
                    binding.bannerAd.root.visibility = android.view.View.VISIBLE
                    bannerForSplash()
                } else {
                    binding.bannerAd.root.visibility = android.view.View.GONE
                }
            }

            if (MyApp.appOpenAdManagerLocator == null) {
                MyApp.appOpenAdManagerLocator = AppOpenAdResume(application as MyApp)
            }
            if (PhoneLocatorLoadAds.canShowAppOpen) {
                MyApp.appOpenAdManagerLocator?.loadCleanerStartAppOpenAd()
            }

//            consentFormRequest()
        }
    }

    private fun bannerForSplash() {
        PhoneLocatorLoadAds.loadHoneyBeeMapNavigationBannerForMainMediationSplash(
            binding.bannerAd.adContainer,
            binding.bannerAd.mainAdsView,
            binding.bannerAd.shimmerLayout,
            this
        )
    }

    override fun onDestroy() {
        super.onDestroy()
        if (::countDownTimer.isInitialized) countDownTimer.cancel()
    }
}