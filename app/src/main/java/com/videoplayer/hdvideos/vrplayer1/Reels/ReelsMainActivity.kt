package com.videoplayer.hdvideos.vrplayer1.Reels

import android.Manifest
import android.app.DownloadManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.pm.PackageManager
import android.net.ConnectivityManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
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
import androidx.viewpager2.widget.ViewPager2
import com.videoplayer.hdvideos.vrplayer1.Main.NewMainActivity.Companion.videosList
import com.videoplayer.hdvideos.vrplayer1.R
import com.videoplayer.hdvideos.vrplayer1.Reels.ReelsConstant.isLottieShownTimer
import com.videoplayer.hdvideos.vrplayer1.Reels.adapter.VideoPagerAdapter
import com.videoplayer.hdvideos.vrplayer1.ads.PhoneLocatorLoadAds
import com.videoplayer.hdvideos.vrplayer1.ads.PhoneLocatorLoadAds.mainBanneradControl
import com.videoplayer.hdvideos.vrplayer1.ads.PhoneLocatorLoadAds.nextreelsShow
import com.videoplayer.hdvideos.vrplayer1.ads.PhoneLocatorShowAds
import com.videoplayer.hdvideos.vrplayer1.ads.PhoneLocatorShowAds.TAG
import com.videoplayer.hdvideos.vrplayer1.ads.PhoneLocatorShowAds.logAnalyticsForClicks
import com.videoplayer.hdvideos.vrplayer1.databinding.ActivityReelsMainBinding
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File
class ReelsMainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityReelsMainBinding
    var viewPager: ViewPager2? = null
    var posCurrent = 0
    var videoAdapter: VideoPagerAdapter? = null
    private var isLottieShown = true
    private var videoUrlToDownload: String? = null
    var scrollCount = 0
    private var downloadId: Long = -1
    val fragmentName = "TrendingCategoryScroll"
    var categoryName: String? = ""
    val categoryVideoList = (ReelsConstant.categoryVideoList ?: emptyList()).toMutableList()
    private var pageChangeCallback: ViewPager2.OnPageChangeCallback? = null
    private var onCompleteReceiver: BroadcastReceiver? = null

    private val storagePermissionLauncher =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted ->
            if (isGranted) {
                videoUrlToDownload?.let { startDownloadInBackground(it) }
            } else {
                Toast.makeText(this, "Storage permission denied", Toast.LENGTH_SHORT).show()
            }
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityReelsMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        com.videoplayer.hdvideos.vrplayer1.ads.AnalyticsHelper.logScreen(this, "screen_reels")
        ViewCompat.setOnApplyWindowInsetsListener(binding.main) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        window.statusBarColor = android.graphics.Color.BLACK
        WindowCompat.getInsetsController(window, window.decorView).apply {
            isAppearanceLightStatusBars = false  // false = white icons
        }
        if (PhoneLocatorLoadAds.reelsInterstitialShow) {
            PhoneLocatorLoadAds.preLoadReelsInterstitial(this)
        }
        mHoneyBeeMapNavigationBannerAdsSmall()

        viewPager = binding.viewPager
        val selectedCategory = intent.getStringExtra("selectedCategory")
        if (!selectedCategory.isNullOrEmpty()) {
            categoryName = selectedCategory.replaceFirstChar { it.uppercaseChar() }
            val formattedCountry = categoryName
                ?.split("_")
                ?.joinToString(" ") { it.replaceFirstChar { c -> c.uppercaseChar() } }
                ?: "Unknown Category"
            binding.tvTitle.text = formattedCountry
        } else {
            Log.w("MusicScrollActivity", "selectedCategory is null or empty in intent")
            binding.tvTitle.text = "VR Trending Reels"
            categoryName = "Unknown"
        }

        binding.cardBack.setOnClickListener {
            onBackPressedDispatcher.onBackPressed()
        }


        onBackPressedDispatcher.addCallback(this, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                PhoneLocatorLoadAds.cancelBannerRefresh()  // stop refresh before leaving

                videoAdapter?.pausePlayerAt(posCurrent)
                videoAdapter?.releaseAllPlayers()
                PhoneLocatorShowAds.mediationBackPressedSimpleHoneyBeeMapNavigation(
                    this@ReelsMainActivity,
                    PhoneLocatorLoadAds.admobInterstitialAdHoneyBee
                )
            }
        })

        lifecycleScope.launch(Dispatchers.Default) {
            val finalList = if (ReelsConstant.videoCategory == "all") {
                Log.d("List", "if videosList:${videosList.size}")
                videosList
            } else {
                Log.d("List", "else categoryVideoList:${categoryVideoList.size}")
                categoryVideoList
            }

            withContext(Dispatchers.Main) {
                if (finalList.isNotEmpty()) {
                    finalList.shuffle()
                    videoAdapter = VideoPagerAdapter(
                        this@ReelsMainActivity,
                        finalList,
                        fragmentName,
                        {},
                        { url ->
                            videoUrlToDownload = url
                            checkPermissionsAndDownload()
                        }
                    )

                    viewPager?.adapter = videoAdapter
                    viewPager?.setCurrentItem(posCurrent, false)
                    videoAdapter?.playPlayerAt(posCurrent)
                    if (isLottieShownTimer) {
                        isLottieShownTimer = false
                        binding.clSwipe.visibility = View.VISIBLE
                        binding.clSwipe.postDelayed({
                            binding.clSwipe.visibility = View.GONE
                        }, 3000)
                    } else {
                        binding.clSwipe.visibility = View.GONE
                    }

                    setupPageChangeCallback(finalList.size)
                } else {
                    Log.d("TrendingCategory", "Video list is empty")
                }
            }
        }
    }

//    override fun onResume() {
//        super.onResume()
//        if (videoAdapter != null) {
//            videoAdapter?.playPlayerAt(posCurrent)
//        }
//    }
//
//    override fun onPause() {
//        videoAdapter?.pausePlayerAt(posCurrent)
//        super.onPause()
//    }
//
//    override fun onDestroy() {
//        try {
//            videoAdapter?.releaseAllPlayers()
//            videoAdapter = null
//        } catch (e: Exception) {
//            Log.e(TAG, "Error releasing video players", e)
//        }
//
//        try {
//            if (::binding.isInitialized) {
//                binding.lottieView?.cancelAnimation()
//            }
//        } catch (e: Exception) {
//            Log.e(TAG, "Error cancelling lottie", e)
//        }
//
//        try {
//            pageChangeCallback?.let { callback ->
//                viewPager?.unregisterOnPageChangeCallback(callback)
//                pageChangeCallback = null
//            }
//        } catch (e: Exception) {
//            Log.e(TAG, "Error unregistering pageChangeCallback", e)
//        }
//
//        unregisterDownloadReceiver()
//        super.onDestroy()
//    }
override fun onResume() {
    super.onResume()
    if (videoAdapter != null) {
        videoAdapter?.playPlayerAt(posCurrent)
    }
    // Restart banner refresh when returning to this activity
    mHoneyBeeMapNavigationBannerAdsSmall()
}

    override fun onPause() {
        videoAdapter?.pausePlayerAt(posCurrent)
        PhoneLocatorLoadAds.cancelBannerRefresh()  // stop refresh when leaving
        super.onPause()
    }

    override fun onDestroy() {
        PhoneLocatorLoadAds.cancelBannerRefresh()  // cleanup on destroy
        try {
            videoAdapter?.releaseAllPlayers()
            videoAdapter = null
        } catch (e: Exception) {
            Log.e(TAG, "Error releasing video players", e)
        }

        try {
            if (::binding.isInitialized) {
                binding.lottieView?.cancelAnimation()
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error cancelling lottie", e)
        }

        try {
            pageChangeCallback?.let { callback ->
                viewPager?.unregisterOnPageChangeCallback(callback)
                pageChangeCallback = null
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error unregistering pageChangeCallback", e)
        }

        unregisterDownloadReceiver()
        super.onDestroy()
    }

    private fun setupPageChangeCallback(listSize: Int) {
        pageChangeCallback = object : ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                posCurrent = position
                if (posCurrent > 0) videoAdapter?.pausePlayerAt(posCurrent - 1)
                if (posCurrent < listSize - 1) videoAdapter?.pausePlayerAt(posCurrent + 1)

                scrollCount++
                if (PhoneLocatorLoadAds.reelsInterstitialShow &&
                    nextreelsShow > 0 &&  // ✅ guard against divide by zero
                    scrollCount > 0 && scrollCount % nextreelsShow == 0
                ) {
                    lifecycleScope.launch(Dispatchers.Main) {
                        videoAdapter?.pausePlayerAt(position)
                        delay(150)
                        try {
                            PhoneLocatorShowAds.showReelsInterstitial(
                                this@ReelsMainActivity,
                                PhoneLocatorLoadAds.admobReelsInterstitialAd,
                                binding.adView
                            )
                        } catch (e: Exception) {
                            e.printStackTrace()
                        }
                    }
                }

                videoAdapter?.playPlayerAt(position)
            }

            override fun onPageScrollStateChanged(state: Int) {
                if (state == ViewPager2.SCROLL_STATE_DRAGGING && isLottieShown) {
                    binding.clSwipe.visibility = View.GONE
                    isLottieShown = false
                }
                if (state == ViewPager2.SCROLL_STATE_IDLE) {
                    binding.clSwipe.visibility = View.GONE
                }
            }
        }
        viewPager?.registerOnPageChangeCallback(pageChangeCallback!!)
    }

    private fun checkPermissionsAndDownload() {
        if (!isInternetAvailable()) {
            Toast.makeText(this, "No internet connection", Toast.LENGTH_SHORT).show()
            return
        }
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.Q) {
            if (ContextCompat.checkSelfPermission(
                    this,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                storagePermissionLauncher.launch(Manifest.permission.WRITE_EXTERNAL_STORAGE)
                return
            }
        }
        videoUrlToDownload?.let { startDownloadInBackground(it) }
    }

    private fun isInternetAvailable(): Boolean {
        val cm = getSystemService(CONNECTIVITY_SERVICE) as ConnectivityManager
        return cm.activeNetworkInfo?.isConnected == true
    }

    private fun startDownloadInBackground(url: String) {
        lifecycleScope.launch(Dispatchers.IO) {
            try {
                val directory = File(
                    Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS),
                    "MusicPlayer/Reels"
                ).apply { if (!exists()) mkdirs() }

                val fileName = "TrendingVideo${System.currentTimeMillis()}.mp4"
                val filePath = File(directory, fileName)
                val downloadManager = getSystemService(DOWNLOAD_SERVICE) as DownloadManager

                val request = DownloadManager.Request(Uri.parse(url)).apply {
                    setTitle("Downloading Video")
                    setDescription("Please wait while the video is being downloaded.")
                    setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED)
                    setDestinationUri(Uri.fromFile(filePath))
                }

                downloadId = downloadManager.enqueue(request)

                withContext(Dispatchers.Main) {
                    Toast.makeText(this@ReelsMainActivity, "Download started", Toast.LENGTH_SHORT).show()
                    registerDownloadReceiver()
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    Toast.makeText(
                        this@ReelsMainActivity,
                        "Download failed: ${e.message}",
                        Toast.LENGTH_LONG
                    ).show()
                }
            }
        }
    }

    private fun registerDownloadReceiver() {
        if (onCompleteReceiver != null) return

        onCompleteReceiver = object : BroadcastReceiver() {
            override fun onReceive(context: Context, intent: Intent) {
                if (intent.getLongExtra(DownloadManager.EXTRA_DOWNLOAD_ID, -1L) != downloadId) return

                Handler(Looper.getMainLooper()).post {
                    val layout = LayoutInflater.from(context).inflate(R.layout.custom_toast_layout, null)
                    Toast(context).apply {
                        view = layout
                        duration = Toast.LENGTH_SHORT
                        setGravity(Gravity.TOP or Gravity.CENTER_HORIZONTAL, 40, 120)
                        show()
                    }
                }
                unregisterDownloadReceiver()
            }
        }

        ContextCompat.registerReceiver(
            this,
            onCompleteReceiver!!,
            IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE),
            ContextCompat.RECEIVER_EXPORTED
        )
    }

    private fun unregisterDownloadReceiver() {
        onCompleteReceiver?.let {
            try { unregisterReceiver(it) } catch (_: IllegalArgumentException) {}
            onCompleteReceiver = null
        }
    }

//    private fun showBannerAds() {
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




}