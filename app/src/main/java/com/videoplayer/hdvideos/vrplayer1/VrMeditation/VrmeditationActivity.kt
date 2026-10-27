package com.videoplayer.hdvideos.vrplayer1.VrMeditation

import android.graphics.Color
import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.activity.OnBackPressedCallback
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.gms.ads.AdView
import com.videoplayer.hdvideos.vrplayer1.Main.BottomNavigationDock
import com.videoplayer.hdvideos.vrplayer1.Main.MainNavigationTab
import com.videoplayer.hdvideos.vrplayer1.Main.NewMainActivity
import com.videoplayer.hdvideos.vrplayer1.R
import com.videoplayer.hdvideos.vrplayer1.SelectedCatergory.SelectedCategoryActivity
import com.videoplayer.hdvideos.vrplayer1.ads.AnalyticsHelper
import com.videoplayer.hdvideos.vrplayer1.ads.InAppABTestHelper
import com.videoplayer.hdvideos.vrplayer1.ads.PhoneLocatorAppPurchaseHelper
import com.videoplayer.hdvideos.vrplayer1.ads.PhoneLocatorLoadAds
import com.videoplayer.hdvideos.vrplayer1.ads.PhoneLocatorLoadAds.mainBanneradControl
import com.videoplayer.hdvideos.vrplayer1.ads.PhoneLocatorShowAds
import com.videoplayer.hdvideos.vrplayer1.ads.PhoneLocatorShowAds.logAnalyticsForClicks
import com.videoplayer.hdvideos.vrplayer1.databinding.ActivityVrmeditationBinding

//class VrmeditationActivity : AppCompatActivity() {
//    override fun onCreate(savedInstanceState: Bundle?) {
//        super.onCreate(savedInstanceState)
//        enableEdgeToEdge()
//        setContentView(R.layout.activity_vrmeditation)
//        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
//            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
//            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
//            insets
//        }
//    }
//}


class VrmeditationActivity : AppCompatActivity() {
    private lateinit var binding: ActivityVrmeditationBinding
    private lateinit var meditationAdapter: MeditationVideoAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityVrmeditationBinding.inflate(layoutInflater)
        setContentView(binding.root)
        com.videoplayer.hdvideos.vrplayer1.ads.AnalyticsHelper.logScreen(this, "screen_vr_meditation")
        ViewCompat.setOnApplyWindowInsetsListener(binding.root) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        window.statusBarColor = Color.BLACK
        WindowCompat.getInsetsController(window, window.decorView).apply {
            isAppearanceLightStatusBars = false
        }

        mHoneyBeeMapNavigationBannerAdsSmall()
        setupRecyclerView()
        setupClickListeners()
        setupBottomNavigation()
    }

    private fun setupRecyclerView() {
        val videos = NewMainActivity.meditationVideosList

        if (videos.isEmpty()) {
            binding.emptyStateTv.visibility = View.VISIBLE
            binding.rvMeditationVideos.visibility = View.GONE
        } else {
            binding.emptyStateTv.visibility = View.GONE
            binding.rvMeditationVideos.visibility = View.VISIBLE
            meditationAdapter = MeditationVideoAdapter(this, videos)
            binding.rvMeditationVideos.layoutManager = LinearLayoutManager(this)
            binding.rvMeditationVideos.itemAnimator =
                androidx.recyclerview.widget.DefaultItemAnimator().apply {
                    addDuration = 300
                    removeDuration = 300
                    moveDuration = 300
                    changeDuration = 300
                }
            binding.rvMeditationVideos.adapter = meditationAdapter
        }
    }

    private fun setupClickListeners() {
        binding.backBtn.setOnClickListener {
            onBackPressedDispatcher.onBackPressed()
        }

        onBackPressedDispatcher.addCallback(this, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                PhoneLocatorLoadAds.cancelBannerRefresh()  // ADD

                PhoneLocatorShowAds.mediationBackPressedSimpleHoneyBeeMapNavigation(
                    this@VrmeditationActivity,
                    PhoneLocatorLoadAds.admobInterstitialAdHoneyBee
                )
            }
        })
    }

    private fun setupBottomNavigation() {
        BottomNavigationDock.bind(binding.bottomNavDock, MainNavigationTab.MEDITATION) { tab ->
            when (tab) {
                MainNavigationTab.HOME -> returnToHome()
                MainNavigationTab.MEDITATION -> Unit
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

    private fun returnToHome() {
        startActivity(
            Intent(this, NewMainActivity::class.java).addFlags(
                Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP
            )
        )
        finish()
    }

    private fun openCategoryFromNavigation(category: String, title: String, eventSuffix: String) {
        if (PhoneLocatorAppPurchaseHelper(this).shouldShowAds()) {
            AnalyticsHelper.logEvent(this, "nav_click_${eventSuffix}_locked")
            startActivity(InAppABTestHelper.getInAppIntent(this))
            return
        }

        AnalyticsHelper.logEvent(this, "nav_click_${eventSuffix}")
        startActivity(
            Intent(this, SelectedCategoryActivity::class.java).apply {
                putExtra(SelectedCategoryActivity.EXTRA_CATEGORY, category)
                putExtra(SelectedCategoryActivity.EXTRA_TITLE, title)
            }
        )
        finish()
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
//    private fun showAdaptiveBannerAds() {
//        PhoneLocatorLoadAds.loadHoneyBeeMapNavigationBannerForMainMediationforotherbanner(
//            binding.bannerAd.adContainer,
//            binding.bannerAd.mainAdsView,
//            binding.bannerAd.shimmerLayout,
//            this
//        )
//    }

    override fun onResume() {
        super.onResume()
        BottomNavigationDock.setSelectedTab(
            binding.bottomNavDock,
            MainNavigationTab.MEDITATION
        )
        (binding.bannerAd.adContainer.getChildAt(0) as? AdView)?.resume()
    }

    //    override fun onPause() {
//        super.onPause()
//        (binding.bannerAd.adContainer.getChildAt(0) as? AdView)?.pause()
//    }
//
//    override fun onDestroy() {
//        super.onDestroy()
//        (binding.bannerAd.adContainer.getChildAt(0) as? AdView)?.destroy()
//    }
    override fun onPause() {
        super.onPause()
        PhoneLocatorLoadAds.cancelBannerRefresh()  // ADD
        (binding.bannerAd.adContainer.getChildAt(0) as? AdView)?.pause()
    }

    override fun onDestroy() {
        super.onDestroy()
        PhoneLocatorLoadAds.cancelBannerRefresh()  // ADD
        (binding.bannerAd.adContainer.getChildAt(0) as? AdView)?.destroy()
    }
}