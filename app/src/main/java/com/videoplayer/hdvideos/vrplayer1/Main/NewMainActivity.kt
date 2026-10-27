package com.videoplayer.hdvideos.vrplayer1.Main

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.View
import android.animation.ObjectAnimator
import android.animation.ValueAnimator
import android.view.animation.AccelerateDecelerateInterpolator
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.lifecycleScope
import com.videoplayer.hdvideos.vrplayer1.BuildConfig
import com.videoplayer.hdvideos.vrplayer1.Drawer.DrawerActivity
import com.videoplayer.hdvideos.vrplayer1.InApp.InAppActivity
import com.videoplayer.hdvideos.vrplayer1.InApp.InAppBActivity
import com.videoplayer.hdvideos.vrplayer1.InApp.InAppCActivity
import com.videoplayer.hdvideos.vrplayer1.R
import com.videoplayer.hdvideos.vrplayer1.Reels.ReelsConstant
import com.videoplayer.hdvideos.vrplayer1.Reels.ReelsMainActivity
import com.videoplayer.hdvideos.vrplayer1.Reels.model.TrendingVideoModel
import com.videoplayer.hdvideos.vrplayer1.SelectedCatergory.SelectedCategoryActivity
import com.videoplayer.hdvideos.vrplayer1.StreakManager.StreakAdGate
import com.videoplayer.hdvideos.vrplayer1.StreakManager.StreakDialogHelper
import com.videoplayer.hdvideos.vrplayer1.StreakManager.StreakManager
import com.videoplayer.hdvideos.vrplayer1.StreakManager.StreakResult
import com.videoplayer.hdvideos.vrplayer1.VrGaming.VrgamingActivity
import com.videoplayer.hdvideos.vrplayer1.VrMeditation.VrmeditationActivity
import com.videoplayer.hdvideos.vrplayer1.ads.AnalyticsHelper
import com.videoplayer.hdvideos.vrplayer1.ads.IapPopupHelper
import com.videoplayer.hdvideos.vrplayer1.ads.InAppABTestHelper
import com.videoplayer.hdvideos.vrplayer1.ads.PhoneLocatorAppPurchaseHelper
import com.videoplayer.hdvideos.vrplayer1.ads.PhoneLocatorLoadAds
import com.videoplayer.hdvideos.vrplayer1.ads.PhoneLocatorLoadAds.canShowBannerOnsplash
import com.videoplayer.hdvideos.vrplayer1.ads.PhoneLocatorLoadAds.canShowFirstAdInter
import com.videoplayer.hdvideos.vrplayer1.ads.PhoneLocatorLoadAds.canShowInappIcon
import com.videoplayer.hdvideos.vrplayer1.ads.PhoneLocatorLoadAds.canShowNativeOnBoarding
import com.videoplayer.hdvideos.vrplayer1.ads.PhoneLocatorLoadAds.canShowNativeOnBoardingFullScreen
import com.videoplayer.hdvideos.vrplayer1.ads.PhoneLocatorLoadAds.canShowPremium
import com.videoplayer.hdvideos.vrplayer1.ads.PhoneLocatorLoadAds.mainBanneradControl
import com.videoplayer.hdvideos.vrplayer1.ads.PhoneLocatorShowAds
import com.videoplayer.hdvideos.vrplayer1.ads.utlites.ContentsLocator
import com.videoplayer.hdvideos.vrplayer1.cinema.TheatreVideoPickerActivity
import com.videoplayer.hdvideos.vrplayer1.databinding.ActivityNewMain2Binding
import com.videoplayer.hdvideos.vrplayer1.pickGallerydata.GalleryPickActivity
import com.videoplayer.hdvideos.vrplayer1.pickGallerydata.utiles.BaseActivity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.json.JSONArray
import kotlin.text.category

class NewMainActivity : BaseActivity() {
    private lateinit var binding: ActivityNewMain2Binding
    private var backPressedOnce = false
    private val backPressHandler = Handler(Looper.getMainLooper())
    private var sharedAIGpsNavPreferences: SharedPreferences? = null

    private var wasRewardActivePreviously = false

    private lateinit var billingHelper: PhoneLocatorAppPurchaseHelper
    private var streakShownThisSession = false
    private var iapPopupShownThisSession = false
    private var floatingGiftAnimator: ObjectAnimator? = null
    private var floatingGiftDismissed = false
    private val homeImageDriftAnimators = mutableListOf<ObjectAnimator>()
    private val homeImageDriftViews = mutableListOf<View>()
    private var homeEntranceStarted = false


    companion object {
        var videosList = ArrayList<TrendingVideoModel>()
        const val TAG = "TrackPlayService"
        var gamingVideosList = ArrayList<TrendingVideoModel>()
        var meditationVideosList = ArrayList<TrendingVideoModel>() // ADD THIS
        private const val REQUEST_NOTIFICATION_PERMISSION = 1001


    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityNewMain2Binding.inflate(layoutInflater)
        setContentView(binding.root)
        AnalyticsHelper.logScreen(this, "screen_main")
        VideoRepository.prewarm(this)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        window.statusBarColor = android.graphics.Color.BLACK
        WindowCompat.getInsetsController(window, window.decorView).apply {
            isAppearanceLightStatusBars = false
        }
        sharedAIGpsNavPreferences = getSharedPreferences(packageName, Context.MODE_PRIVATE)

        // ── STREAK: apply ad gate on every cold start ──────────────────────
        // Must run AFTER billingHelper init, BEFORE ad loading below.
        // StreakAdGate reads both real billing + streak reward and flips
        // StreetViewTruckCTLoadAds flags if either says suppress.
        billingHelper = PhoneLocatorAppPurchaseHelper(this)
        StreakAdGate.applyAdState(this)
        checkNotificationPermission()
        mHoneyBeeMapNavigationBannerAdsSmall()
        loadVideosFromPreferences()
        loadGamingVideosFromPreferences()
        loadMeditationVideosFromPreferences()
        VideoRepository.init(this)

        if (ContentsLocator.requestAds == 0) {
            ContentsLocator.requestAds = 1
            PhoneLocatorLoadAds.preLoadAdsHoneyBee(this)
            Log.d("reward", "without reward preLoadAdsHoneyBee loadeded ")

        }

        setupProIcon()
        setupClickListeners()
        setupHomeMotion()
        setupBackPress()

        binding.layoutHeader.streakBtn.setOnClickListener {
            handleStreakLogic()
        }
    }

    //    private fun setupProIcon() {
//        val billingHelper = PhoneLocatorAppPurchaseHelper(this)
//        val hasPurchased = !billingHelper.shouldShowAds()
//
//
////        binding.layoutHeader.imgPro.visibility =
////            if (hasPurchased) View.GONE
////            else if (PhoneLocatorLoadAds.canShowInappIcon) View.VISIBLE
////            else View.GONE
//
//        binding.layoutHeader.imgPro.setOnClickListener {
//            val intent = when (PhoneLocatorLoadAds.inAppVariant) {
//                "B" -> Intent(this, InAppBActivity::class.java)
//                "C" -> Intent(this, InAppCActivity::class.java)
//                else -> Intent(this, InAppActivity::class.java)
//            }
//            startActivity(intent)
//        }
//    }
    private fun setupProIcon() {
        val hasPurchased = !billingHelper.shouldShowAds()
        val showPaywall = !hasPurchased && canShowInappIcon

        binding.layoutHeader.imgPro.visibility = if (showPaywall) View.VISIBLE else View.GONE
        binding.layoutHeader.btnProContainer.visibility = if (showPaywall) View.VISIBLE else View.GONE
        if (showPaywall) {
            android.animation.AnimatorInflater.loadAnimator(this, R.animator.pro_badge_pulse)
                .apply { setTarget(binding.layoutHeader.btnProContainer); start() }
        }
        updateFloatingGift(showPaywall)

        binding.layoutHeader.imgPro.setOnClickListener {
            startActivity(InAppABTestHelper.getInAppIntent(this))
        }
        binding.layoutHeader.btnProContainer.setOnClickListener {
            startActivity(InAppABTestHelper.getInAppIntent(this))
        }

        // QA Tester shortcut: Long press to choose variant directly or enable auto-rotate
        binding.layoutHeader.imgPro.setOnLongClickListener {
            InAppABTestHelper.showQaVariantPicker(this)
            true
        }
        binding.layoutHeader.btnProContainer.setOnLongClickListener {
            InAppABTestHelper.showQaVariantPicker(this)
            true
        }
    }
    private fun setupClickListeners() {
        binding.cardVrCinema.setOnClickListener {
            AnalyticsHelper.logEvent(this, "feature_click_vr_cinema")
            intentWithAd(Intent(this@NewMainActivity, TheatreVideoPickerActivity::class.java))
        }

        binding.cardVrGaming.setOnClickListener {
            AnalyticsHelper.logEvent(this, "feature_click_vr_gaming")
            intentWithAd(Intent(this@NewMainActivity, VrgamingActivity::class.java))
        }

        binding.cardReels.setOnClickListener {
            AnalyticsHelper.logEvent(this, "feature_click_reels")
            ReelsConstant.videoCategory = "all"
            intent.putExtra("selectedCategory", "reels")
            intentWithAd(Intent(this@NewMainActivity, ReelsMainActivity::class.java))
        }
        binding.Vrvideos.setOnClickListener {
            AnalyticsHelper.logEvent(this, "feature_click_vr_videos")
            intentWithAd(Intent(this@NewMainActivity, MainActivity::class.java))
        }
        binding.personalmedia.setOnClickListener {
            AnalyticsHelper.logEvent(this, "feature_click_personal_media")
            intentWithAd(Intent(this@NewMainActivity, GalleryPickActivity::class.java))
        }
        binding.layoutHeader.imgLogo.setOnClickListener {
            val intent = Intent(this@NewMainActivity, DrawerActivity::class.java).apply {
                putExtra("from_new_main", true)
            }
            intentWithAd(intent)
        }
        binding.layoutHeader.btnMenuContainer.setOnClickListener {
            val intent = Intent(this@NewMainActivity, DrawerActivity::class.java).apply {
                putExtra("from_new_main", true)
            }
            intentWithAd(intent)
        }

        // Floating VIP Giftbox
        binding.btnFloatingGift.setOnClickListener {
            AnalyticsHelper.logEvent(this, "floating_giftbox_clicked")
            IapPopupHelper.showIapPopup(this)
        }
        binding.btnDismissFloatingGift.setOnClickListener {
            floatingGiftDismissed = true
            updateFloatingGift(shouldShow = true)
        }

        BottomNavigationDock.bind(
            binding.bottomNavDock,
            MainNavigationTab.HOME,
            ::onBottomTabSelected
        )
    }

    private fun onBottomTabSelected(tab: MainNavigationTab) {
        when (tab) {
            MainNavigationTab.HOME -> binding.nestedScroll.smoothScrollTo(0, 0)
            MainNavigationTab.MEDITATION -> {
                AnalyticsHelper.logEvent(this, "nav_click_vr_meditation")
                intentWithAd(Intent(this, VrmeditationActivity::class.java))
            }
            MainNavigationTab.ROLLER_COASTER -> openCategoryFromNavigation(
                "Roller Coaster",
                "VR Roller Coaster",
                "nav_click_roller_coaster",
                "nav_click_roller_coaster_locked"
            )
            MainNavigationTab.JURASSIC -> openCategoryFromNavigation(
                "Jurassic Video",
                "VR Jurassic Park",
                "nav_click_jurassic",
                "nav_click_jurassic_locked"
            )
        }
    }

    private fun setupHomeMotion() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O && !ValueAnimator.areAnimatorsEnabled()) {
            return
        }

        val entranceViews = listOf(
            binding.Vrvideos,
            binding.layoutTwoCol,
            binding.personalmedia,
            binding.cardReels
        )
        val driftViews = listOf(
            binding.ivExploreBackground,
            binding.ivCinemaBackground,
            binding.ivGamingBackground,
            binding.ivPersonalMediaBackground,
            binding.ivReelsBackground
        )
        val density = resources.displayMetrics.density
        homeImageDriftViews.addAll(driftViews)

        driftViews.forEachIndexed { index, image ->
            homeImageDriftAnimators += ObjectAnimator.ofFloat(
                image,
                View.TRANSLATION_X,
                -3f * density,
                3f * density
            ).apply {
                duration = 4800L
                startDelay = index * 180L
                repeatCount = ObjectAnimator.INFINITE
                repeatMode = ObjectAnimator.REVERSE
                interpolator = AccelerateDecelerateInterpolator()
            }
        }

        binding.nestedScroll.post {
            if (isFinishing || isDestroyed || homeEntranceStarted) return@post
            homeEntranceStarted = true
            entranceViews.forEachIndexed { index, view ->
                view.alpha = 0f
                view.translationY = 12f * density
                view.animate()
                    .alpha(1f)
                    .translationY(0f)
                    .setStartDelay(index * 45L)
                    .setDuration(260L)
                    .setInterpolator(AccelerateDecelerateInterpolator())
                    .start()
            }
        }
    }

    private fun startHomeImageDrift() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O && !ValueAnimator.areAnimatorsEnabled()) return
        homeImageDriftAnimators.forEach { animator ->
            when {
                animator.isPaused -> animator.resume()
                !animator.isStarted -> animator.start()
            }
        }
    }

    private fun stopHomeImageDrift() {
        homeImageDriftAnimators.forEach { it.pause() }
    }

    private fun openCategoryFromNavigation(
        category: String,
        title: String,
        purchasedEvent: String,
        lockedEvent: String
    ) {
        if (!billingHelper.shouldShowAds()) {
            AnalyticsHelper.logEvent(this, purchasedEvent)
            val intent = Intent(this, SelectedCategoryActivity::class.java).apply {
                putExtra(SelectedCategoryActivity.EXTRA_CATEGORY, category)
                putExtra(SelectedCategoryActivity.EXTRA_TITLE, title)
            }
            intentWithAd(intent)
        } else {
            AnalyticsHelper.logEvent(this, lockedEvent)
            startActivity(InAppABTestHelper.getInAppIntent(this))
        }
    }

    private var lastClickTime = 0L
    private fun canClick(): Boolean {
        val now = android.os.SystemClock.elapsedRealtime()
        if (now - lastClickTime < 600L) return false
        lastClickTime = now
        return true
    }

    private fun intentWithAd(intent: Intent) {
        if (!canClick()) return
        PhoneLocatorLoadAds.cancelBannerRefresh()
        if (canShowFirstAdInter) {
            PhoneLocatorShowAds.meidationForClickSimpleHoneyBeeMapNavigation(
                this,
                PhoneLocatorLoadAds.admobInterstitialAdHoneyBee,
                intent
            )
        } else {
            canShowFirstAdInter = true
            PhoneLocatorLoadAds.preReLoadAdsHoneyBee(this)
            try {
                startActivity(intent)
            } catch (e: Exception) {
                Log.e("checking", "Error starting activity: ${e.message}")
            }
        }
    }

    private fun mHoneyBeeMapNavigationBannerAdsSmall() {
        Log.d("reward", "without reward banner loadeded ")

        if (mainBanneradControl) {
            PhoneLocatorLoadAds.collapsableBanner(
                binding.bannerAd.adContainer,
                binding.bannerAd.mainAdsView,
                this
            )
        } else {
            Log.d("bannnermainelse", "mHoneyBeeMapNavigationBannerAdsSmall: ")
            binding.bannerAd.mainAdsView.visibility = View.GONE
            binding.bannerAd.adContainer.visibility = View.GONE
        }
    }

    private fun checkNotificationPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS)
                != PackageManager.PERMISSION_GRANTED
            ) {
                notificationPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
            }
        }
    }

    private val notificationPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { granted ->
        Log.d("MainActivity", "Notification permission granted: $granted")
    }

    // Your original had this — it works fine:
    private fun setupBackPress() {
        onBackPressedDispatcher.addCallback(this, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                if (backPressedOnce) {
                    PhoneLocatorLoadAds.cancelBannerRefresh()  // ADD
                    backPressHandler.removeCallbacksAndMessages(null)
                    finishAffinity()
                    return
                }
                backPressedOnce = true
                Toast.makeText(
                    this@NewMainActivity,
                    "Press back again to exit",
                    Toast.LENGTH_SHORT
                ).show()
                backPressHandler.postDelayed({ backPressedOnce = false }, 2000)
            }
        })
    }

    private fun loadVideosFromPreferences() {
        lifecycleScope.launch(Dispatchers.IO) {
            val categories = listOf(
                "Ai_ART", "ART", "Dance", "fantasy", "Food",
                "Funny", "Heal_and_fitness", "learning", "Music", "Viral"
            )

            val videoList = mutableListOf<TrendingVideoModel>()

            for (category in categories) {
                val jsonString = sharedAIGpsNavPreferences?.getString(category, "")
                Log.d("CatListData", "loadVideosFromPreferences: jsonString${jsonString} ")
                if (!jsonString.isNullOrEmpty()) {
                    val parsedList = parseGlobeDroneJsonStringToNewsList(jsonString)
                    parsedList.forEach { it.category = category }
                    videoList.addAll(parsedList)
                }
            }

            withContext(Dispatchers.Main) {
                videosList.clear()
                videosList.addAll(videoList)
                Log.d("CatListData", "loadVideosFromPreferences: ${videosList.size}")
            }
        }
    }

    private fun parseGlobeDroneJsonStringToNewsList(jsonString: String): List<TrendingVideoModel> {
        val globeDroneList = mutableListOf<TrendingVideoModel>()
        try {
            val newsArray = JSONArray(jsonString)
            for (i in 0 until newsArray.length()) {
                val objInside = newsArray.getJSONObject(i)
                val name = objInside.optString("name", "Unknown")
                val thumbnail = objInside.optString("thumnail", "")
                val video = objInside.optString("video", "")
                val duration = objInside.optString("duration", "")
                val description = objInside.optString("description", "")


                globeDroneList.add(
                    TrendingVideoModel(
                        title = name,
                        category = "",
                        thumnail = thumbnail,
                        videoUrl = video,
                        duration = duration,
                        description = description
                    )
                )
            }
        } catch (e: Exception) {
            Log.e("JSON_PARSE_ERROR", "Error parsing JSON: ${e.localizedMessage}")
        }
        return globeDroneList
    }

    private fun loadGamingVideosFromPreferences() {
        lifecycleScope.launch(Dispatchers.IO) {
            val gamingCategories =
                listOf("Gaming") // replace with "gaming" when you add it to remote config

            val gamingList = mutableListOf<TrendingVideoModel>()

            for (category in gamingCategories) {
                val jsonString = sharedAIGpsNavPreferences?.getString(category, "")
                Log.d("GamingListData", "loadGamingVideos: jsonString=$jsonString")
                if (!jsonString.isNullOrEmpty()) {
                    val parsedList = parseGamingJsonStringToList(jsonString)
                    parsedList.forEach { it.category = category }
                    gamingList.addAll(parsedList)
                }
            }

            withContext(Dispatchers.Main) {
                gamingVideosList.clear()
                gamingVideosList.addAll(gamingList)
                Log.d("GamingListData", "loadGamingVideos size: ${gamingVideosList.size}")
            }
        }
    }

    private fun parseGamingJsonStringToList(jsonString: String): List<TrendingVideoModel> {
        val gamingList = mutableListOf<TrendingVideoModel>()
        try {
            val jsonArray = JSONArray(jsonString)
            for (i in 0 until jsonArray.length()) {
                val obj = jsonArray.getJSONObject(i)

                val name = obj.optString("title", "Unknown")
                val thumbnail = obj.optString("thumbnailResId", "")
                val video = obj.optString("videoUri", "")
                val desc = obj.optString("description", "")
                val duration = obj.optString("duration", "")
                val description = obj.optString("description", "")

                gamingList.add(
                    TrendingVideoModel(
                        title = name,
                        category = "",
                        thumnail = thumbnail,
                        videoUrl = video,
                        duration = duration,
                        description = description


                    )
                )
            }
        } catch (e: Exception) {
            Log.e("GAMING_JSON_ERROR", "Error parsing gaming JSON: ${e.localizedMessage}")
        }
        return gamingList
    }


    private fun loadMeditationVideosFromPreferences() {
        lifecycleScope.launch(Dispatchers.IO) {
            val categories = listOf("Meditation")
            val meditationList = mutableListOf<TrendingVideoModel>()
            for (category in categories) {
                val jsonString = sharedAIGpsNavPreferences?.getString(category, "")
                Log.d("MeditationListData", "loadMeditationVideos: jsonString=$jsonString")
                if (!jsonString.isNullOrEmpty()) {
                    val parsedList = parseMeditationJsonStringToList(jsonString)
                    parsedList.forEach { it.category = category }
                    meditationList.addAll(parsedList)
                }
            }

            withContext(Dispatchers.Main) {
                meditationVideosList.clear()
                meditationVideosList.addAll(meditationList)
                Log.d("MeditationListData", "size: ${meditationVideosList.size}")
            }
        }
    }

    private fun parseMeditationJsonStringToList(jsonString: String): List<TrendingVideoModel> {
        val meditationList = mutableListOf<TrendingVideoModel>()
        try {
            val jsonArray = JSONArray(jsonString)
            for (i in 0 until jsonArray.length()) {
                val obj = jsonArray.getJSONObject(i)
                meditationList.add(
                    TrendingVideoModel(
                        title = obj.optString("title", "Unknown"),        // ✅ was "name"
                        category = "",
                        thumnail = obj.optString("thumbnailResId", ""),      // ✅ was "thumnail"
                        videoUrl = obj.optString("videoUri", ""),          // ✅ was "video"
                        duration = obj.optString("duration", ""),           // ✅ was "video"
                        description = obj.optString("description", "")           // ✅ was "video"
                    )
                )
            }
        } catch (e: Exception) {
            Log.e("MEDITATION_JSON_ERROR", "Error: ${e.localizedMessage}")
        }
        return meditationList
    }


    //streak
    private fun handleStreakLogic() {
        val billing = PhoneLocatorAppPurchaseHelper(this@NewMainActivity)
        val isPaidUser = !billing.shouldShowAds()

        if (BuildConfig.DEBUG) {
            val mgr = StreakManager(this@NewMainActivity)
            Log.d("StreakTest", "=== STREAK STATE ===")
            Log.d("StreakTest", "isPaidUser     : $isPaidUser")
            Log.d("StreakTest", "streakCount    : ${mgr.streakCount}")
            Log.d("StreakTest", "isRewardActive : ${mgr.isRewardActive}")
            Log.d("StreakTest", "rewardExpiry   : ${mgr.rewardExpiryEpoch}")
            Log.d("StreakTest", "====================")
        }
        if (isPaidUser) return

        val streakMgr = StreakManager(this@NewMainActivity)

        // ── Already rewarded in a previous session ────────────────────────
        if (streakMgr.isRewardActive) {
            StreakAdGate.suppressAllAds()
            suppressAdsForReward()
            refreshPaywallUi()
            return
        }

        // ── Run streak for this session ───────────────────────────────────
        val result = streakMgr.recordOpen()

        // Re-read after recordOpen — grantReward() may have just written expiry
//        if (streakMgr.isRewardActive) {
//            showDay7Dialog()
//            return
//        }

        when (result) {
            is StreakResult.StreakProgress -> StreakDialogHelper.showProgressOnly(
                this@NewMainActivity,
                result.day
            )

            is StreakResult.StreakCompleted -> showDay7Dialog()
            is StreakResult.RewardOngoing -> {}
        }
    }

    private fun showDay7Dialog() {
        StreakDialogHelper.showRewardOnly(this@NewMainActivity) {
            StreakAdGate.suppressAllAds()
            suppressAdsForReward()
            refreshPaywallUi()
        }
    }

    fun suppressAdsForReward() {
        StreakAdGate.suppressAllAds()
        binding.bannerAd.root.visibility = View.GONE
        binding.bannerAd.adContainer.tag = null
    }

    private fun updateFloatingGift(shouldShow: Boolean) {
        val isVisible = shouldShow && !floatingGiftDismissed
        binding.btnFloatingGift.visibility = if (isVisible) View.VISIBLE else View.GONE

        if (!isVisible) {
            floatingGiftAnimator?.cancel()
            binding.btnFloatingGift.translationY = 0f
            return
        }

        if (floatingGiftAnimator?.isRunning == true) return

        binding.btnFloatingGift.translationY = 0f
        floatingGiftAnimator = ObjectAnimator.ofFloat(
            binding.btnFloatingGift,
            View.TRANSLATION_Y,
            0f,
            -8f,
            0f
        ).apply {
            duration = 1500L
            repeatCount = ObjectAnimator.INFINITE
            repeatMode = ObjectAnimator.RESTART
            interpolator = AccelerateDecelerateInterpolator()
            start()
        }
    }

//    fun refreshPaywallUi() {
//        val hasPurchased = !billingHelper.shouldShowAds()
//        val isRewardActive = StreakAdGate.isStreakRewardActive(this)
//        val hidePaywallUi = hasPurchased || isRewardActive
//
//        // Update premium icon
//        binding.layoutHeader.imgPro.visibility =
//            if (!hidePaywallUi && canShowInappIcon) View.VISIBLE else View.GONE
//
//        // Update streak button too
//        binding.layoutHeader.streakBtn.visibility =
//            if (!hidePaywallUi && canShowInappIcon) View.VISIBLE else View.GONE
//
//
//    }


    fun refreshPaywallUi() {
        val hasPurchased = !billingHelper.shouldShowAds()
        val isRewardActive = StreakAdGate.isStreakRewardActive(this)
        val hidePaywallUi = hasPurchased || isRewardActive

        // Update premium icon
        val showPremium = !hidePaywallUi && canShowInappIcon
        binding.layoutHeader.imgPro.visibility = if (showPremium) View.VISIBLE else View.GONE
        binding.layoutHeader.btnProContainer.visibility = if (showPremium) View.VISIBLE else View.GONE

        updateFloatingGift(showPremium)

        // Update streak button too
        binding.layoutHeader.streakBtn.visibility = if (showPremium) View.VISIBLE else View.GONE
        binding.layoutHeader.btnStreakContainer.visibility = if (showPremium) View.VISIBLE else View.GONE

        // ── Only restore ads when reward JUST expired this resume ─────────
        // wasRewardActivePreviously = true  means reward was active last time
        // isRewardActive = false            means it just expired now        if (!isRewardActive && !hasPurchased) {
        // Reward expired — restore ad views and reload flags
        val rewardJustExpired = wasRewardActivePreviously && !isRewardActive && !hasPurchased
        if (rewardJustExpired) {
            Log.d("StreakTest", "Reward just expired — restoring ads")
            binding.bannerAd.root.visibility = View.VISIBLE
            StreakAdGate.restoreAdsAfterExpiry()
            mHoneyBeeMapNavigationBannerAdsSmall()
        }

        // Update flag for next resume check
        wasRewardActivePreviously = isRewardActive
    }

    override fun onResume() {
        super.onResume()
        startHomeImageDrift()
        floatingGiftDismissed = false
        BottomNavigationDock.setSelectedTab(
            binding.bottomNavDock,
            MainNavigationTab.HOME
        )
//        restoreAdFlagsAfterRewardExpiry()
        refreshPaywallUi()
        if (!streakShownThisSession) {
            streakShownThisSession = true
            handleStreakLogic()
        }
        val hasPurchased = !billingHelper.shouldShowAds()
        if (!hasPurchased && !iapPopupShownThisSession) {
            iapPopupShownThisSession = true
            Handler(Looper.getMainLooper()).postDelayed({
                if (!isFinishing && !isDestroyed) {
                    IapPopupHelper.showIapPopup(this@NewMainActivity)
                }
            }, 30_000L)
        }
    }

    private fun restoreAdFlagsAfterRewardExpiry() {
        val isRewardActive = StreakAdGate.isStreakRewardActive(this)
        val isPaidUser = !billingHelper.shouldShowAds()

        // Only restore if reward just expired (was active, now isn't)
        if (isRewardActive || isPaidUser) return
//        if (PhoneLocatorLoadAds.adsRestoredAfterReward) return  // already restored this session

        Log.d("StreakReward", "Reward expired — restoring ad flags")
//        PhoneLocatorLoadAds.adsRestoredAfterReward = true

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


        if (ContentsLocator.requestAds == 0) {
            ContentsLocator.requestAds = 1
            PhoneLocatorLoadAds.preLoadAdsHoneyBee(this)
        }
        mHoneyBeeMapNavigationBannerAdsSmall()

     }


    override fun onPause() {
        stopHomeImageDrift()
        floatingGiftAnimator?.cancel()
        floatingGiftAnimator = null
        binding.btnFloatingGift.translationY = 0f
        super.onPause()
        PhoneLocatorLoadAds.cancelBannerRefresh()
    }

    override fun onDestroy() {
        homeImageDriftAnimators.forEach { it.cancel() }
        homeImageDriftViews.forEach { it.translationX = 0f }
        floatingGiftAnimator?.cancel()
        super.onDestroy()
        PhoneLocatorLoadAds.cancelBannerRefresh()
        backPressHandler.removeCallbacksAndMessages(null)
    }

}
