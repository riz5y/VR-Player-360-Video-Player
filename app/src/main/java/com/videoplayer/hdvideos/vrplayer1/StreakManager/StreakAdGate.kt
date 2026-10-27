package com.videoplayer.hdvideos.vrplayer1.StreakManager
import android.content.Context
import com.videoplayer.hdvideos.vrplayer1.ads.PhoneLocatorAppPurchaseHelper
import com.videoplayer.hdvideos.vrplayer1.ads.PhoneLocatorLoadAds

object StreakAdGate {

    fun applyAdState(context: Context): Boolean {
        val billing = PhoneLocatorAppPurchaseHelper(context)
        val isPaidUser = !billing.shouldShowAds()
        val streakMgr = StreakManager(context)
        val rewardActive = streakMgr.isRewardActive
        val suppressAds = isPaidUser || rewardActive

        if (suppressAds) {
            suppressAllAds()
        }

        return suppressAds
    }

    fun isStreakRewardActive(context: Context): Boolean = StreakManager(context).isRewardActive

    fun suppressAllAds() {

//        PhoneLocatorLoadAds.adsRestoredAfterReward = false
        PhoneLocatorLoadAds.shouldGoForAds = false
        PhoneLocatorLoadAds.canReLoadedAdMob = false

        // ── Interstitial Ad Flags ─────────────────────────────────────────
        PhoneLocatorLoadAds.canShowFirstAdInter = false  // Main Interstitial
        PhoneLocatorLoadAds.reelsInterstitialShow = false  // Reels Interstitial
        PhoneLocatorLoadAds.OnboardingAdshow = false  // Onboarding Interstitial
        PhoneLocatorLoadAds.canShowOnBoarding = false  // Onboarding Flow
        PhoneLocatorLoadAds.canShowPremium = false
        PhoneLocatorLoadAds.canShowInappIcon = false
        PhoneLocatorLoadAds.canShowSplashInterstitial = false
        PhoneLocatorLoadAds.canShowMainInterstitial = false
        PhoneLocatorLoadAds.canShowBackPressInterstitial = false


        // ── Banner Ad Flags ───────────────────────────────────────────────
        PhoneLocatorLoadAds.mainBanneradControl = false  // Main Banner
        PhoneLocatorLoadAds.canShowBannerOnsplash = false  // Splash Banner

        // ── Native Ad Flags ───────────────────────────────────────────────
        PhoneLocatorLoadAds.should_show_native = false  // General Native
        PhoneLocatorLoadAds.canShowNativeOnBoarding = false  // Onboarding Native
        PhoneLocatorLoadAds.canShowNativeOnBoardingFullScreen = false  // Fullscreen Onboarding Native
        PhoneLocatorLoadAds.canShowNativeOnBoardingFullScreenafterfirst = false

        // ── App Open & Timer Gates ───────────────────────────────────────
        PhoneLocatorLoadAds.canShowAppOpen = false  // App Open Gate
        PhoneLocatorLoadAds.canShowAdsOnTimer = false  // Timer-based Ad Gate


    }

    fun restoreAdsAfterExpiry() {
        PhoneLocatorLoadAds.canShowMainInterstitial = true
        PhoneLocatorLoadAds.canShowBackPressInterstitial = true
        PhoneLocatorLoadAds.canShowFirstAdInter = true
        PhoneLocatorLoadAds.reelsInterstitialShow = true
        PhoneLocatorLoadAds.canShowSplashInterstitial = true
        PhoneLocatorLoadAds.mainBanneradControl = true
        PhoneLocatorLoadAds.canShowBannerOnsplash = true
        PhoneLocatorLoadAds.should_show_native = true
        PhoneLocatorLoadAds.canShowNativeOnBoarding = true
        PhoneLocatorLoadAds.canShowNativeOnBoardingFullScreen = true
        PhoneLocatorLoadAds.canShowAppOpen = true
        PhoneLocatorLoadAds.canShowAdsOnTimer = true
        PhoneLocatorLoadAds.shouldGoForAds = true
        PhoneLocatorLoadAds.canReLoadedAdMob = true
        PhoneLocatorLoadAds.canShowInappIcon = true
        PhoneLocatorLoadAds.canShowPremium = true


    }
}