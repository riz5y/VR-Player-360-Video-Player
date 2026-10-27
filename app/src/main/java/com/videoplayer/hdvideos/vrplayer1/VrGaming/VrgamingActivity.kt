package com.videoplayer.hdvideos.vrplayer1.VrGaming

import android.graphics.Color
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
import com.videoplayer.hdvideos.vrplayer1.Main.NewMainActivity
import com.videoplayer.hdvideos.vrplayer1.R
import com.videoplayer.hdvideos.vrplayer1.ads.PhoneLocatorLoadAds
import com.videoplayer.hdvideos.vrplayer1.ads.PhoneLocatorLoadAds.mainBanneradControl
import com.videoplayer.hdvideos.vrplayer1.ads.PhoneLocatorShowAds
import com.videoplayer.hdvideos.vrplayer1.ads.PhoneLocatorShowAds.logAnalyticsForClicks
import com.videoplayer.hdvideos.vrplayer1.databinding.ActivityVrgamingBinding

class VrgamingActivity : AppCompatActivity() {
    private lateinit var binding: ActivityVrgamingBinding
    private lateinit var gamingAdapter: GamingVideoAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityVrgamingBinding.inflate(layoutInflater)
        setContentView(binding.root)

        ViewCompat.setOnApplyWindowInsetsListener(binding.root) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        com.videoplayer.hdvideos.vrplayer1.ads.AnalyticsHelper.logScreen(this, "screen_vr_gaming")

        window.statusBarColor = Color.BLACK
        WindowCompat.getInsetsController(window, window.decorView).apply {
            isAppearanceLightStatusBars = false
        }

        mHoneyBeeMapNavigationBannerAdsSmall()
        setupRecyclerView()
        setupClickListeners()
    }



    private fun setupRecyclerView() {
        val videos = NewMainActivity.gamingVideosList

        if (videos.isEmpty()) {
            binding.emptyStateTv.visibility = View.VISIBLE
            binding.rvGamingVideos.visibility = View.GONE
        } else {
            binding.emptyStateTv.visibility = View.GONE
            binding.rvGamingVideos.visibility = View.VISIBLE
            gamingAdapter = GamingVideoAdapter(this, videos)
            binding.rvGamingVideos.layoutManager = LinearLayoutManager(this)

            // Smooth item animator
            binding.rvGamingVideos.itemAnimator =
                androidx.recyclerview.widget.DefaultItemAnimator().apply {
                    addDuration    = 300
                    removeDuration = 300
                    moveDuration   = 300
                    changeDuration = 300
                }

            binding.rvGamingVideos.adapter = gamingAdapter
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
                    this@VrgamingActivity,
                    PhoneLocatorLoadAds.admobInterstitialAdHoneyBee
                )
            }
        })
    }

//    private fun showAdaptiveBannerAds() {
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

    override fun onResume() {
        super.onResume()
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