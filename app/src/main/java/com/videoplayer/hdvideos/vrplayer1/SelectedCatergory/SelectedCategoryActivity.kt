package com.videoplayer.hdvideos.vrplayer1.SelectedCatergory

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.videoplayer.hdvideos.vrplayer1.Detail.DetailActivity
import com.videoplayer.hdvideos.vrplayer1.Main.BottomNavigationDock
import com.videoplayer.hdvideos.vrplayer1.Main.MainNavigationTab
import com.videoplayer.hdvideos.vrplayer1.Main.NewMainActivity
import com.videoplayer.hdvideos.vrplayer1.R
import com.videoplayer.hdvideos.vrplayer1.Main.VideoRepository
import com.videoplayer.hdvideos.vrplayer1.VrMeditation.VrmeditationActivity
import com.videoplayer.hdvideos.vrplayer1.ads.AnalyticsHelper
import com.videoplayer.hdvideos.vrplayer1.ads.InAppABTestHelper
import com.videoplayer.hdvideos.vrplayer1.ads.PhoneLocatorAppPurchaseHelper
import com.videoplayer.hdvideos.vrplayer1.ads.PhoneLocatorLoadAds
import com.videoplayer.hdvideos.vrplayer1.ads.PhoneLocatorLoadAds.mainBanneradControl
import com.videoplayer.hdvideos.vrplayer1.ads.PhoneLocatorShowAds
import com.videoplayer.hdvideos.vrplayer1.databinding.ActivitySelectedCategoryBinding
import com.videoplayer.hdvideos.vrplayer1.pickGallerydata.utiles.BaseActivity

class SelectedCategoryActivity : BaseActivity() {
    private lateinit var binding: ActivitySelectedCategoryBinding
    private lateinit var categoryAdapter: CategoryAdapter
    private var currentCategory = ""

    companion object {
        const val EXTRA_TITLE = "title"
        const val EXTRA_THUMBNAIL = "thumbnail"
        const val EXTRA_VIDEO_URL = "videoUrl"
        const val EXTRA_TIME_DURATION = "timeDuration"
        const val EXTRA_CATEGORY = "category"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySelectedCategoryBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val categoryForScreen = intent.getStringExtra(EXTRA_CATEGORY) ?: ""
        val selectedTab = when {
            categoryForScreen.contains("roller", ignoreCase = true) ->
                MainNavigationTab.ROLLER_COASTER
            categoryForScreen.contains("jurassic", ignoreCase = true) ->
                MainNavigationTab.JURASSIC
            else -> null
        }
        when (selectedTab) {
            MainNavigationTab.ROLLER_COASTER -> AnalyticsHelper.logScreen(this, "screen_roller_coaster")
            MainNavigationTab.JURASSIC -> AnalyticsHelper.logScreen(this, "screen_jurassic")
            else -> AnalyticsHelper.logScreen(this, "screen_selected_category")
        }

        setupBottomNavigation(selectedTab)
        mHoneyBeeMapNavigationBannerAdsSmall()

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        onBackPressedDispatcher.addCallback(this, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                PhoneLocatorLoadAds.cancelBannerRefresh()  // ADD
                PhoneLocatorShowAds.mediationBackPressedSimpleHoneyBeeMapNavigation(
                    this@SelectedCategoryActivity,
                    PhoneLocatorLoadAds.admobInterstitialAdHoneyBee
                )
            }
        })

        binding.backbtn.setOnClickListener {
            onBackPressedDispatcher.onBackPressed()
        }

        window.statusBarColor = android.graphics.Color.BLACK
        WindowCompat.getInsetsController(window, window.decorView).apply {
            isAppearanceLightStatusBars = false  // false = white icons
        }

        val title = intent.getStringExtra(EXTRA_TITLE) ?: ""
        currentCategory = intent.getStringExtra(EXTRA_CATEGORY) ?: ""

        binding.tvToolbarTitle.text = title

        val videos = VideoRepository.getVideosForCategory(currentCategory)

        val adapter = CategoryAdapter(videos) { video ->
            // Show paywall for premium categories if user hasn't purchased
            val isPaidCategory = CategoryAdapter.PAID_CATEGORIES.any {
                video.category.lowercase().contains(it)
            }
            val hasPurchased = !PhoneLocatorAppPurchaseHelper(this).shouldShowAds()
            if (isPaidCategory && !hasPurchased) {
                intentWithAd(InAppABTestHelper.getInAppIntent(this))
                return@CategoryAdapter
            }
            val intent = Intent(this, DetailActivity::class.java).apply {
                putExtra(DetailActivity.EXTRA_TITLE, video.title)
                putExtra(DetailActivity.EXTRA_DURATION, video.timeDuration)
                putExtra(DetailActivity.EXTRA_STREAM_URL, video.videoUrl)
                putExtra(DetailActivity.EXTRA_IMAGE, video.thumbnail)
                putExtra(
                    DetailActivity.EXTRA_DESCRIPTION,
                    if (video.description.isNotEmpty()) video.description else "It is a long established fact that a reader will be distracted by the readable content of a page when looking at its layout."
                )
            }
            intentWithAd(intent)
        }
        binding.SeCategories.apply {
            setHasFixedSize(true)
            setItemViewCacheSize(10)
            layoutManager = LinearLayoutManager(this@SelectedCategoryActivity)
            itemAnimator = null  // prevents items from sliding away during DiffUtil updates
            this.adapter = adapter
        }
        categoryAdapter = adapter

        VideoRepository.init(this) {
            runOnUiThread {
                if (!isFinishing && !isDestroyed) {
                    val updatedVideos = VideoRepository.getVideosForCategory(currentCategory)
                    adapter.updateItems(updatedVideos)
                }
            }
        }


    }

    private fun setupBottomNavigation(selectedTab: MainNavigationTab?) {
        if (selectedTab == null) {
            binding.bottomNavDock.root.visibility = View.GONE
            return
        }

        var activeTab: MainNavigationTab = selectedTab
        BottomNavigationDock.bind(binding.bottomNavDock, selectedTab) { tab ->
            if (tab == activeTab) return@bind

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
                MainNavigationTab.ROLLER_COASTER -> if (switchCategory(
                        "Roller Coaster", "VR Roller Coaster", "roller_coaster"
                    )
                ) activeTab = tab
                MainNavigationTab.JURASSIC -> if (switchCategory(
                        "Jurassic Video", "VR Jurassic Park", "jurassic"
                    )
                ) activeTab = tab
            }
        }
    }

    private fun switchCategory(category: String, title: String, eventSuffix: String): Boolean {
        if (PhoneLocatorAppPurchaseHelper(this).shouldShowAds()) {
            AnalyticsHelper.logEvent(this, "nav_click_${eventSuffix}_locked")
            startActivity(InAppABTestHelper.getInAppIntent(this))
            return false
        }

        AnalyticsHelper.logEvent(this, "nav_click_${eventSuffix}")
        currentCategory = category
        intent.putExtra(EXTRA_CATEGORY, category)
        intent.putExtra(EXTRA_TITLE, title)
        binding.tvToolbarTitle.text = title
        BottomNavigationDock.setSelectedTab(
            binding.bottomNavDock,
            if (eventSuffix == "jurassic") MainNavigationTab.JURASSIC else MainNavigationTab.ROLLER_COASTER
        )
        categoryAdapter.replaceItems(VideoRepository.getVideosForCategory(category))
        binding.SeCategories.scrollToPosition(0)
        return true
    }


    private fun intentWithAd(intent: Intent) {
        PhoneLocatorLoadAds.cancelBannerRefresh()  // ADD

        PhoneLocatorShowAds.meidationForClickSimpleHoneyBeeMapNavigation(
            this,
            PhoneLocatorLoadAds.admobInterstitialAdHoneyBee,
            intent
        )
    }

    //    private fun mHoneyBeeMapNavigationBannerAdsSmall() {
//        PhoneLocatorLoadAds.loadHoneyBeeMapNavigationBannerForMainMediationforotherbanner(
//            binding.bannerAd.adContainer,
//            binding.bannerAd.mainAdsView,
//            binding.bannerAd.shimmerLayout,
//            this
//        )
//    }
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

    override fun onPause() {
        super.onPause()
        PhoneLocatorLoadAds.cancelBannerRefresh()
    }

    override fun onDestroy() {
        super.onDestroy()
        PhoneLocatorLoadAds.cancelBannerRefresh()
    }

}