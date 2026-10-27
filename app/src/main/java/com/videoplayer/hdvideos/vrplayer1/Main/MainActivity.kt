package com.videoplayer.hdvideos.vrplayer1.Main

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.View
import androidx.activity.OnBackPressedCallback
import androidx.activity.enableEdgeToEdge
import androidx.core.view.ViewCompat
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.chip.Chip
import com.videoplayer.hdvideos.vrplayer1.Detail.DetailActivity
import com.videoplayer.hdvideos.vrplayer1.Download.DownloadActivity
import com.videoplayer.hdvideos.vrplayer1.Drawer.DrawerActivity
import com.videoplayer.hdvideos.vrplayer1.R
import com.videoplayer.hdvideos.vrplayer1.SelectedCatergory.SelectedCategoryActivity
import com.videoplayer.hdvideos.vrplayer1.SelectedCatergory.CategoryAdapter
import com.videoplayer.hdvideos.vrplayer1.StreakManager.StreakAdGate
import com.videoplayer.hdvideos.vrplayer1.TrendingAdapter
import com.videoplayer.hdvideos.vrplayer1.VrMeditation.VrmeditationActivity
import com.videoplayer.hdvideos.vrplayer1.ads.AnalyticsHelper
import com.videoplayer.hdvideos.vrplayer1.ads.IapPopupHelper
import com.videoplayer.hdvideos.vrplayer1.ads.InAppABTestHelper
import com.videoplayer.hdvideos.vrplayer1.ads.PhoneLocatorAppPurchaseHelper
import com.videoplayer.hdvideos.vrplayer1.ads.PhoneLocatorLoadAds
import com.videoplayer.hdvideos.vrplayer1.ads.PhoneLocatorLoadAds.mainBanneradControl
import com.videoplayer.hdvideos.vrplayer1.ads.PhoneLocatorShowAds
import com.videoplayer.hdvideos.vrplayer1.databinding.ActivityMainBinding
import com.videoplayer.hdvideos.vrplayer1.pickGallerydata.utiles.BaseActivity

enum class Tab { CATEGORY, DOWNLOADS, SETTING }

class MainActivity : BaseActivity() {

    private lateinit var binding: ActivityMainBinding
    private var selectedCategory: String = "All"
    private var lastShownCategory: String? = null

    private var categoryAdapter: CategoryAdapter? = null
    private var trendingHeaderAdapter: TrendingHeaderAdapter? = null
    private var trendingVideos: List<VideoItem> = emptyList()
    private var trendingAdapter: TrendingAdapter? = null
    private var trendingAutoScrollHandler: Handler? = null
    private var trendingAutoScrollRunnable: Runnable? = null
    private var currentTrendingPos = 0

    companion object {
        const val EXTRA_TAB = "extra_tab"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        AnalyticsHelper.logScreen(this, "screen_vr_videos")

        WindowCompat.setDecorFitsSystemWindows(window, false)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        onBackPressedDispatcher.addCallback(this, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                PhoneLocatorLoadAds.cancelBannerRefresh()
                PhoneLocatorShowAds.mediationBackPressedSimpleHoneyBeeMapNavigation(
                    this@MainActivity,
                    PhoneLocatorLoadAds.admobInterstitialAdHoneyBee
                )
            }
        })
        binding.layoutHeader.imgLogo.setOnClickListener {
            onBackPressedDispatcher.onBackPressed()
        }
        binding.layoutHeader.btnBackHeaderContainer.setOnClickListener {
            onBackPressedDispatcher.onBackPressed()
        }
        binding.layoutHeader.btnDownloadsHeader.setOnClickListener {
            selectTab(Tab.DOWNLOADS)
        }
        binding.layoutHeader.btnSettingsHeader.setOnClickListener {
            selectTab(Tab.SETTING)
        }

        trendingAdapter = TrendingAdapter(emptyList()) { video -> openVideo(video) }
        trendingHeaderAdapter = TrendingHeaderAdapter(trendingAdapter!!)
        categoryAdapter = CategoryAdapter(emptyList()) { video -> openVideo(video) }

        val concatAdapter = androidx.recyclerview.widget.ConcatAdapter(
            trendingHeaderAdapter!!,
            categoryAdapter!!
        )

        binding.rvCategories.apply {
            setHasFixedSize(true)
            setItemViewCacheSize(10)
            layoutManager = LinearLayoutManager(this@MainActivity)
            itemAnimator = null
            adapter = concatAdapter
        }

        selectedCategory = intent.getStringExtra("category") ?: "All"
        if (VideoRepository.isReady()) {
            setupCategoryChips()
            setupTrendingSection()
            showVideosForCategory(selectedCategory)
            VideoRepository.fetchFromRemote(this) {
                runOnUiThread {
                    if (!isFinishing && !isDestroyed) {
                        showVideosForCategory(selectedCategory)
                    }
                }
            }
        } else {
            VideoRepository.init(this) {
                runOnUiThread {
                    if (!isFinishing && !isDestroyed) {
                        setupCategoryChips()
                        setupTrendingSection()
                        showVideosForCategory(selectedCategory)
                    }
                }
            }
        }
        setupBottomNav()

        window.statusBarColor = android.graphics.Color.BLACK
        WindowCompat.getInsetsController(window, window.decorView).apply {
            isAppearanceLightStatusBars = false
        }

        val tabName = intent.getStringExtra(EXTRA_TAB)
        selectTab(
            when (tabName) {
                Tab.DOWNLOADS.name -> Tab.DOWNLOADS
                Tab.SETTING.name -> Tab.SETTING
                else -> Tab.CATEGORY
            }
        )

        mHoneyBeeMapNavigationBannerAdsSmall()
    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        selectTab(
            when (intent.getStringExtra(EXTRA_TAB)) {
                Tab.DOWNLOADS.name -> Tab.DOWNLOADS
                Tab.SETTING.name -> Tab.SETTING
                else -> Tab.CATEGORY
            }
        )
    }

    override fun onResume() {
        super.onResume()
        if (selectedCategory == "All") {
            startTrendingAutoScroll()
        }
    }

    override fun onPause() {
        super.onPause()
        stopTrendingAutoScroll()
        PhoneLocatorLoadAds.cancelBannerRefresh()
    }

    override fun onDestroy() {
        super.onDestroy()
        stopTrendingAutoScroll()
        PhoneLocatorLoadAds.cancelBannerRefresh()
    }

    private fun intentWithAd(intent: Intent) {
        PhoneLocatorLoadAds.cancelBannerRefresh()
        PhoneLocatorShowAds.meidationForClickSimpleHoneyBeeMapNavigation(
            this,
            PhoneLocatorLoadAds.admobInterstitialAdHoneyBee,
            intent
        )
    }

    private var lastVideoClickTime = 0L
    private fun canClickVideo(): Boolean {
        val now = android.os.SystemClock.elapsedRealtime()
        if (now - lastVideoClickTime < 600L) return false
        lastVideoClickTime = now
        return true
    }

    private fun openVideo(video: VideoItem) {
        if (!canClickVideo()) return
        val billing = PhoneLocatorAppPurchaseHelper(this@MainActivity)
        val isPaidUser = !billing.shouldShowAds() || StreakAdGate.isStreakRewardActive(this@MainActivity)
        val isPaidModule = CategoryAdapter.PAID_CATEGORIES.any { video.category.lowercase().contains(it) }

        if (isPaidModule && !isPaidUser) {
            AnalyticsHelper.logEvent(this@MainActivity, "paid_module_locked_clicked", mapOf("category" to video.category))
            IapPopupHelper.showIapPopup(this@MainActivity)
            return
        }

        AnalyticsHelper.logEvent(this@MainActivity, "feature_click_video", mapOf("title" to video.title, "category" to video.category))

        intentWithAd(Intent(this@MainActivity, DetailActivity::class.java).apply {
            putExtra(DetailActivity.EXTRA_TITLE, video.title)
            putExtra(DetailActivity.EXTRA_DURATION, video.timeDuration)
            putExtra(DetailActivity.EXTRA_STREAM_URL, video.videoUrl)
            putExtra(DetailActivity.EXTRA_IMAGE, video.thumbnail)
            putExtra(
                DetailActivity.EXTRA_DESCRIPTION,
                video.description.ifEmpty { "Experience this amazing 360° VR video in high quality." }
            )
            putExtra(DetailActivity.EXTRA_IS_LOCAL, false)
        })
    }

    private fun mHoneyBeeMapNavigationBannerAdsSmall() {
        if (mainBanneradControl) {
            PhoneLocatorLoadAds.collapsableBanner(
                binding.bannerAd.adContainer,
                binding.bannerAd.mainAdsView,
                this
            )
        } else {
            binding.bannerAd.mainAdsView.visibility = View.GONE
            binding.bannerAd.adContainer.visibility = View.GONE
        }
    }

    private fun setupTrendingSection() {
        val allVids = VideoRepository.getAllVideos()
        if (allVids.isEmpty()) {
            trendingHeaderAdapter?.isVisible = false
            return
        }

        trendingVideos = allVids.shuffled().distinctBy { it.title }.take(15)
        trendingAdapter?.updateItems(trendingVideos)

        if (selectedCategory == "All") {
            trendingHeaderAdapter?.isVisible = true
            startTrendingAutoScroll()
        } else {
            trendingHeaderAdapter?.isVisible = false
            stopTrendingAutoScroll()
        }
    }

    private fun startTrendingAutoScroll() {
        stopTrendingAutoScroll()
        val count = trendingVideos.size
        if (count <= 1) return

        trendingAutoScrollHandler = Handler(Looper.getMainLooper())
        trendingAutoScrollRunnable = object : Runnable {
            override fun run() {
                val rv = trendingHeaderAdapter?.rvTrending
                if (!isFinishing && !isDestroyed && trendingHeaderAdapter?.isVisible == true && count > 0 && rv != null) {
                    if (!rv.isComputingLayout && rv.scrollState == RecyclerView.SCROLL_STATE_IDLE) {
                        currentTrendingPos = (currentTrendingPos + 1) % count
                        rv.smoothScrollToPosition(currentTrendingPos)
                    }
                }
                trendingAutoScrollHandler?.postDelayed(this, 3000)
            }
        }
        trendingAutoScrollHandler?.postDelayed(trendingAutoScrollRunnable!!, 3000)
    }

    private fun stopTrendingAutoScroll() {
        trendingAutoScrollRunnable?.let { trendingAutoScrollHandler?.removeCallbacks(it) }
        trendingAutoScrollHandler = null
        trendingAutoScrollRunnable = null
    }

    private fun setupCategoryChips() {
        val categories = listOf("All") + VideoRepository.getCategories()
        val chipGroup = binding.chipGroupCategories
        chipGroup.removeAllViews()

        var selectedChipView: Chip? = null

        val refreshChipStyles = {
            for (i in 0 until chipGroup.childCount) {
                val chip = chipGroup.getChildAt(i) as? Chip ?: continue
                val isChecked = (chip.text == selectedCategory)
                chip.isChecked = isChecked
                if (isChecked) {
                    chip.setChipBackgroundColorResource(R.color.appgreen)
                    chip.setTextColor(android.graphics.Color.BLACK)
                    chip.chipStrokeWidth = 0f
                } else {
                    chip.setChipBackgroundColorResource(R.color.surface)
                    chip.setTextColor(resources.getColor(R.color.white, theme))
                    chip.chipStrokeWidth = 1.5f
                    chip.setChipStrokeColorResource(R.color.border_color)
                }
            }
        }

        for (cat in categories) {
            val chip = Chip(this).apply {
                id = View.generateViewId()
                text = cat
                isCheckable = true
                isCheckedIconVisible = false
                chipCornerRadius = 30f
                setEnsureMinTouchTargetSize(false)

                setOnClickListener {
                    if (selectedCategory != cat) {
                        selectedCategory = cat
                        AnalyticsHelper.logEvent(this@MainActivity, "feature_category_switch", mapOf("category" to cat))
                        refreshChipStyles()
                        showVideosForCategory(cat)
                        binding.rvCategories.post {
                            binding.rvCategories.scrollToPosition(0)
                        }
                    }
                }
            }
            chipGroup.addView(chip)
            if (cat == selectedCategory) {
                selectedChipView = chip
            }
        }

        refreshChipStyles()

        binding.categoryScrollView.post {
            if (selectedCategory == "All") {
                binding.categoryScrollView.scrollTo(0, 0)
            } else {
                selectedChipView?.let {
                    binding.categoryScrollView.smoothScrollTo(it.left - 50, 0)
                }
            }
        }
    }

    private fun showVideosForCategory(category: String) {
        if (category == "All") {
            trendingHeaderAdapter?.isVisible = true
            if (trendingVideos.isEmpty()) {
                setupTrendingSection()
            } else {
                startTrendingAutoScroll()
            }
        } else {
            trendingHeaderAdapter?.isVisible = false
            stopTrendingAutoScroll()
        }

        val videos = if (category == "All") {
            VideoRepository.getAllVideos()
        } else {
            VideoRepository.getVideosForCategory(category)
        }

        if (category == lastShownCategory) {
            categoryAdapter?.updateItems(videos)
        } else {
            lastShownCategory = category
            categoryAdapter?.replaceItems(videos)
        }
    }

    private fun setupBottomNav() {
        BottomNavigationDock.bind(binding.bottomNavDock, MainNavigationTab.HOME) { tab ->
            when (tab) {
                MainNavigationTab.HOME -> {
                    startActivity(
                        Intent(this, NewMainActivity::class.java).addFlags(
                            Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP
                        )
                    )
                    finish()
                }
                MainNavigationTab.MEDITATION -> {
                    startActivity(Intent(this, VrmeditationActivity::class.java))
                    finish()
                }
                MainNavigationTab.ROLLER_COASTER -> openCategoryFromNavigation(
                    "Roller Coaster",
                    "VR Roller Coaster",
                    "roller_coaster"
                )
                MainNavigationTab.JURASSIC -> openCategoryFromNavigation(
                    "Jurassic Video",
                    "VR Jurassic Park",
                    "jurassic"
                )
            }
        }
    }

    private fun selectTab(tab: Tab) {
        when (tab) {
            Tab.CATEGORY -> Unit
            Tab.DOWNLOADS -> {
                PhoneLocatorLoadAds.cancelBannerRefresh()
                intentWithAd(Intent(this, DownloadActivity::class.java).apply {
                    flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP
                })
            }

            Tab.SETTING -> {
                PhoneLocatorLoadAds.cancelBannerRefresh()
                startActivity(Intent(this, DrawerActivity::class.java))
            }
        }
    }

    private fun openCategoryFromNavigation(category: String, title: String, eventSuffix: String) {
        if (PhoneLocatorAppPurchaseHelper(this).shouldShowAds()) {
            AnalyticsHelper.logEvent(this, "nav_click_${eventSuffix}_locked")
            startActivity(InAppABTestHelper.getInAppIntent(this))
            return
        }

        AnalyticsHelper.logEvent(this, "nav_click_${eventSuffix}")
        startActivity(Intent(this, SelectedCategoryActivity::class.java).apply {
            putExtra(SelectedCategoryActivity.EXTRA_CATEGORY, category)
            putExtra(SelectedCategoryActivity.EXTRA_TITLE, title)
        })
        finish()
    }
}
