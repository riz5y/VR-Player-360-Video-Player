package com.videoplayer.hdvideos.vrplayer1.Detail

import android.Manifest
import android.app.Dialog
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.hardware.Sensor
import android.hardware.SensorManager
import android.media.MediaScannerConnection
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.text.SpannableString
import android.text.Spanned
import android.text.style.ForegroundColorSpan
import android.util.Log
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.OptIn
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContentProviderCompat.requireContext
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.media3.common.util.UnstableApi
import com.bumptech.glide.Glide
import com.videoplayer.hdvideos.vrplayer1.Detail.utils.IntentKeys
import com.videoplayer.hdvideos.vrplayer1.Detail.utils.InteractionMode
import com.videoplayer.hdvideos.vrplayer1.Detail.utils.ViewMode
import com.videoplayer.hdvideos.vrplayer1.R
import com.videoplayer.hdvideos.vrplayer1.ads.PhoneLocatorLoadAds
import com.videoplayer.hdvideos.vrplayer1.ads.PhoneLocatorLoadAds.mainBanneradControl
import com.videoplayer.hdvideos.vrplayer1.ads.PhoneLocatorShowAds
import com.videoplayer.hdvideos.vrplayer1.databinding.ActivityDetailBinding
import com.videoplayer.hdvideos.vrplayer1.databinding.DialogDownloadProgressBinding
import com.videoplayer.hdvideos.vrplayer1.pickGallerydata.GalleryPlayerActivity
import com.videoplayer.hdvideos.vrplayer1.pickGallerydata.utiles.BaseActivity
import com.videoplayer.hdvideos.vrplayer1.utils.UrlUtils
import kotlinx.coroutines.*
import java.io.File
import java.io.FileOutputStream
import java.net.HttpURLConnection
import java.net.URL


class DetailActivity : BaseActivity() {
    private lateinit var binding: ActivityDetailBinding
    private var downloadJob: Job? = null
    private var selectedViewMode: ViewMode = ViewMode.DEVICE
    private var selectedInteraction: InteractionMode = InteractionMode.MOTION
    private var isRepeat: Boolean = false
    private var isExitOnFinish: Boolean = true
    private var isFromTrending: Boolean = false

    private var streamUrl: String = ""
    private var title: String = ""

    companion object {
        const val EXTRA_TITLE = "extra_title"
        const val EXTRA_DESCRIPTION = "extra_description"
        const val EXTRA_DURATION = "extra_duration"
        const val EXTRA_STREAM_URL = "extra_stream_url"
        const val EXTRA_IMAGE = "extra_image"
        const val EXTRA_IS_LOCAL = "extra_is_local"
        const val EXTRA_FROM_TRENDING = "extra_from_trending"

        private const val KEY_VIEW_MODE = "key_view_mode"
        private const val KEY_INTERACTION = "key_interaction"
        private const val KEY_REPEAT = "key_repeat"
        private const val KEY_EXIT_FINISH = "key_exit_on_finish"
    }

    @OptIn(UnstableApi::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)
        com.videoplayer.hdvideos.vrplayer1.ads.AnalyticsHelper.logScreen(this, "screen_detail")

        mHoneyBeeMapNavigationBannerAdsSmall()

        ViewCompat.setOnApplyWindowInsetsListener(binding.main) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        window.statusBarColor = android.graphics.Color.BLACK
        WindowCompat.getInsetsController(window, window.decorView).apply {
            isAppearanceLightStatusBars = false
        }

        val isLocal = intent.getBooleanExtra(EXTRA_IS_LOCAL, false)
        isFromTrending = intent.getBooleanExtra(EXTRA_FROM_TRENDING, false)
        title = intent.getStringExtra(EXTRA_TITLE) ?: ""
        val description = intent.getStringExtra(EXTRA_DESCRIPTION)
            ?: "Experience this amazing 360° VR video in high quality."
        val duration = intent.getStringExtra(EXTRA_DURATION) ?: ""
        streamUrl = intent.getStringExtra(EXTRA_STREAM_URL) ?: ""
        val imageUrl = intent.getStringExtra(EXTRA_IMAGE) ?: ""

        if (isLocal) binding.downloadBtn.visibility = View.GONE

        Log.d(
            "DetailActivity",
            "title=$title | duration=$duration | streamUrl=$streamUrl | isFromTrending=$isFromTrending"
        )

        // ── Bind views ─────────────────────────────────────────────────────
        binding.tvTitle.text = title
        binding.tvDescription.text = description
        binding.tvDuration.text = duration

        val thumbModel: Any = if (imageUrl.startsWith("drawable/")) {
            val resName = imageUrl.removePrefix("drawable/")
            val resId = resources.getIdentifier(resName, "drawable", packageName)
            if (resId != 0) resId else imageUrl
        } else if (isLocal) {
            Uri.fromFile(File(imageUrl))
        } else {
            imageUrl
        }

        Glide.with(this)
            .load(thumbModel)
            .placeholder(R.drawable.placeholder_thumb)
            .error(R.drawable.placeholder_thumb)
            .diskCacheStrategy(com.bumptech.glide.load.engine.DiskCacheStrategy.ALL)
            .centerCrop()
            .into(binding.heroImage)

        // ── Back button ────────────────────────────────────────────────────
        onBackPressedDispatcher.addCallback(this, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                PhoneLocatorLoadAds.cancelBannerRefresh()  // ADD

                PhoneLocatorShowAds.mediationBackPressedSimpleHoneyBeeMapNavigation(
                    this@DetailActivity,
                    PhoneLocatorLoadAds.admobInterstitialAdHoneyBee
                )
            }
        })

        binding.backbtn.setOnClickListener { onBackPressedDispatcher.onBackPressed() }

        // ── See More toggle ────────────────────────────────────────────────
        var expanded = false
        binding.tvSeeMore.setOnClickListener {
            expanded = !expanded
            binding.tvDescription.maxLines = if (expanded) Int.MAX_VALUE else 1
            binding.tvSeeMore.text = if (expanded) "See Less" else "See More"
        }

        // ── Gyroscope check ────────────────────────────────────────────────
        checkGyroscope()

//        // ── Mode chips (Motion / Touch / Both) ────────────────────────────
//        binding.chipMotion.setOnClickListener {
//            selectedInteraction = InteractionMode.MOTION
//            updateModeChips(InteractionMode.MOTION)
//        }
//        binding.chipTouch.setOnClickListener {
//            selectedInteraction = InteractionMode.TOUCH
//            updateModeChips(InteractionMode.TOUCH)
//        }
//        binding.chipBoth.setOnClickListener {
//            selectedInteraction = InteractionMode.BOTH
//            updateModeChips(InteractionMode.BOTH)
//        }
        binding.chipMotion.setOnClickListener {
            if (!hasGyroscope()) {
                Toast.makeText(this, "No gyroscope — Motion mode not supported", Toast.LENGTH_SHORT)
                    .show()
                return@setOnClickListener
            }
            selectedInteraction = InteractionMode.MOTION
            updateModeChips(InteractionMode.MOTION)
        }

        binding.chipBoth.setOnClickListener {
            if (!hasGyroscope()) {
                Toast.makeText(this, "No gyroscope — Motion mode not supported", Toast.LENGTH_SHORT)
                    .show()
                return@setOnClickListener
            }
            selectedInteraction = InteractionMode.BOTH
            updateModeChips(InteractionMode.BOTH)
        }

        binding.chipTouch.setOnClickListener {
            selectedInteraction = InteractionMode.TOUCH
            updateModeChips(InteractionMode.TOUCH)
        }

        // ── Repeat / Exit on finish ────────────────────────────────────────
        binding.rbRepeat.setOnCheckedChangeListener { _, checked ->
            if (checked) {
                isRepeat = true
                isExitOnFinish = false
                binding.rbRepeat.setTextColor(ContextCompat.getColor(this, R.color.white))
                binding.rbExitOnFinish.isChecked = false
                binding.rbExitOnFinish.setTextColor(ContextCompat.getColor(this, R.color.white))
            }
        }

        binding.rbExitOnFinish.setOnCheckedChangeListener { _, checked ->
            if (checked) {
                isExitOnFinish = true
                isRepeat = false
                binding.rbExitOnFinish.setTextColor(ContextCompat.getColor(this, R.color.white))
                binding.rbRepeat.isChecked = false
                binding.rbRepeat.setTextColor(ContextCompat.getColor(this, R.color.white))
            }
        }

        // ── Play As buttons ────────────────────────────────────────────────
        binding.icSbs.setOnClickListener {
            selectedViewMode = ViewMode.SBS
            updatePlayAsButtons(selectedViewMode)
            launchPlayer(streamUrl, title)
        }
        binding.iccardboard.setOnClickListener {
            selectedViewMode = ViewMode.CARDBOARD
            updatePlayAsButtons(selectedViewMode)
            launchPlayer(streamUrl, title)
        }
        binding.ondevice.setOnClickListener {
            selectedViewMode = ViewMode.DEVICE
            updatePlayAsButtons(selectedViewMode)
            launchPlayer(streamUrl, title)
        }
        binding.panoramic.setOnClickListener {
            selectedViewMode = ViewMode.PANORAMIC
            updatePlayAsButtons(selectedViewMode)
            launchPlayer(streamUrl, title)
        }

        // ── Download button ────────────────────────────────────────────────
        binding.downloadBtn.setOnClickListener {
            if (streamUrl.isEmpty()) {
                Toast.makeText(this, "No video URL available", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.P) {
                if (ContextCompat.checkSelfPermission(
                        this,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE
                    ) == PackageManager.PERMISSION_GRANTED
                ) {
                    showDownloadDialog(streamUrl, title)
                } else {
                    storagePermissionLauncher.launch(Manifest.permission.WRITE_EXTERNAL_STORAGE)
                }
            } else {
                showDownloadDialog(streamUrl, title)
            }
        }

        if (savedInstanceState != null) {
            restoreState(savedInstanceState)
        } else {
            updateModeChips(selectedInteraction)
            updatePlayAsButtons(selectedViewMode)
            isRepeat = false
            isExitOnFinish = true
            binding.rbRepeat.isChecked = false
            binding.rbExitOnFinish.isChecked = true
            binding.rbExitOnFinish.setTextColor(ContextCompat.getColor(this, R.color.white))
        }
    }

    // ─────────────────────────────────────────────────────────────────────────
    // Save & Restore State
    // ─────────────────────────────────────────────────────────────────────────

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putString(KEY_VIEW_MODE, selectedViewMode.name)
        outState.putString(KEY_INTERACTION, selectedInteraction.name)
        outState.putBoolean(KEY_REPEAT, isRepeat)
        outState.putBoolean(KEY_EXIT_FINISH, isExitOnFinish)
    }

    private fun restoreState(savedInstanceState: Bundle) {
        selectedViewMode = ViewMode.valueOf(
            savedInstanceState.getString(KEY_VIEW_MODE, ViewMode.DEVICE.name)!!
        )
        selectedInteraction = InteractionMode.valueOf(
            savedInstanceState.getString(KEY_INTERACTION, InteractionMode.MOTION.name)!!
        )
        isRepeat = savedInstanceState.getBoolean(KEY_REPEAT, false)
        isExitOnFinish = savedInstanceState.getBoolean(KEY_EXIT_FINISH, false)

        updateModeChips(selectedInteraction)
        updatePlayAsButtons(selectedViewMode)
        binding.rbRepeat.isChecked = isRepeat
        binding.rbExitOnFinish.isChecked = isExitOnFinish
    }


    private fun updateModeChips(mode: InteractionMode) {
        val selected = R.drawable.bg_chip_selected
        val unselected = R.drawable.bg_chip_unselected
        binding.chipMotion.setBackgroundResource(if (mode == InteractionMode.MOTION) selected else unselected)
        binding.chipTouch.setBackgroundResource(if (mode == InteractionMode.TOUCH) selected else unselected)
        binding.chipBoth.setBackgroundResource(if (mode == InteractionMode.BOTH) selected else unselected)

        if (!hasGyroscope()) {
             binding.chipMotion.alpha = 0.4f
            binding.chipBoth.alpha = 0.4f

            if (mode == InteractionMode.MOTION || mode == InteractionMode.BOTH) {
                selectedInteraction = InteractionMode.TOUCH
                binding.chipTouch.setBackgroundResource(selected)
                binding.chipMotion.setBackgroundResource(unselected)
                binding.chipBoth.setBackgroundResource(unselected)
            }
        } else {
            // Reset alpha if gyroscope is available
            binding.chipMotion.alpha = 1.0f
            binding.chipBoth.alpha = 1.0f
        }
    }

    private fun updatePlayAsButtons(mode: ViewMode) {
        val selected = R.drawable.bg_play_btn_selected
        val unselected = 0
        binding.icSbs.setBackgroundResource(if (mode == ViewMode.SBS) selected else unselected)
        binding.iccardboard.setBackgroundResource(if (mode == ViewMode.CARDBOARD) selected else unselected)
        binding.ondevice.setBackgroundResource(if (mode == ViewMode.DEVICE) selected else unselected)
        binding.panoramic.setBackgroundResource(if (mode == ViewMode.PANORAMIC) selected else unselected)
    }



    @OptIn(UnstableApi::class)
    private fun launchPlayer(streamUrl: String, title: String) {
        if (streamUrl.isEmpty()) {
            Toast.makeText(this, "No video URL available", Toast.LENGTH_SHORT).show()
            return
        }
        PhoneLocatorLoadAds.cancelBannerRefresh()  // ADD

        val playerIntent = Intent(this, PlayerActivity::class.java).apply {
            putExtra(IntentKeys.VIDEO_URI, streamUrl)
            putExtra(IntentKeys.VIEW_MODE, selectedViewMode.name)
            putExtra(IntentKeys.INTERACTION, selectedInteraction.name)
            putExtra(IntentKeys.REPEAT, isRepeat)
            putExtra(IntentKeys.EXIT_ON_FINISH, isExitOnFinish)
            putExtra("video_title", title)
        }

        com.videoplayer.hdvideos.vrplayer1.ads.AnalyticsHelper.logEvent(
            this,
            "feature_launch_360_player",
            mapOf("view_mode" to selectedViewMode.name, "interaction" to selectedInteraction.name)
        )

        if (isFromTrending) {
            startActivity(playerIntent)
        } else {
            // From category — show interstitial ad then launch
            PhoneLocatorShowAds.meidationForClickSimpleHoneyBeeMapNavigation(
                this,
                PhoneLocatorLoadAds.admobInterstitialAdHoneyBee,
                playerIntent
            )
        }
    }


    private fun hasGyroscope(): Boolean {
        val sm = getSystemService(SENSOR_SERVICE) as SensorManager
        return sm.getDefaultSensor(Sensor.TYPE_GYROSCOPE) != null
    }

    private fun checkGyroscope() {
        if (!hasGyroscope()) {
            Toast.makeText(this, "No gyroscope — Motion mode disabled", Toast.LENGTH_LONG).show()
        }
    }


    private fun showDownloadDialog(url: String, title: String) {
        val dialog = Dialog(this)
        val dialogBinding = DialogDownloadProgressBinding.inflate(layoutInflater)
        dialog.setContentView(dialogBinding.root)
        dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        dialog.setCancelable(false)
        dialog.window?.setLayout(
            (resources.displayMetrics.widthPixels * 0.80).toInt(),
            ViewGroup.LayoutParams.WRAP_CONTENT
        )

        val spannable = SpannableString("Download Progress")
        spannable.setSpan(
            ForegroundColorSpan(Color.parseColor("#0A0816")),
            0,
            8,
            Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
        )
        spannable.setSpan(
            ForegroundColorSpan(Color.parseColor("#3C8CEF")),
            9,
            17,
            Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
        )
        dialogBinding.tvDialogTitle.text = spannable

        dialog.show()

        downloadJob = CoroutineScope(Dispatchers.IO).launch {
            downloadFile(url, title, dialogBinding, dialog)
        }

        dialogBinding.btnCancel.setOnClickListener {
            downloadJob?.cancel()
            dialog.dismiss()
            Toast.makeText(this, "Download cancelled", Toast.LENGTH_SHORT).show()
        }
    }


    private suspend fun downloadFile(
        urlString: String,
        title: String,
        dialogBinding: DialogDownloadProgressBinding,
        dialog: Dialog
    ) {
        try {
            val connection = URL(urlString).openConnection()
             UrlUtils.trustAllForConnection(connection)
            val httpConn = connection as HttpURLConnection
            httpConn.setRequestProperty("Referer", UrlUtils.REFERER)
            httpConn.setRequestProperty("User-Agent", UrlUtils.USER_AGENT)
            httpConn.connect()

            val totalBytes = httpConn.contentLengthLong
            val inputStream = httpConn.inputStream
            val fileName = "${title.replace("[^a-zA-Z0-9]".toRegex(), "_")}.mp4"

            val vrFolder = File(
                Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS),
                "VRVideos"
            )
            if (!vrFolder.exists()) vrFolder.mkdirs()

            val outputFile = File(vrFolder, fileName)
            val outputStream = FileOutputStream(outputFile)
            val buffer = ByteArray(8192)
            var downloadedBytes = 0L
            var bytesRead: Int

            while (inputStream.read(buffer).also { bytesRead = it } != -1) {
                if (downloadJob?.isCancelled == true) break
                outputStream.write(buffer, 0, bytesRead)
                downloadedBytes += bytesRead

                val progress =
                    if (totalBytes > 0) ((downloadedBytes * 100) / totalBytes).toInt() else 0
                val downloadedMB = "%.2f MB".format(downloadedBytes / (1024.0 * 1024.0))
                val totalMB =
                    if (totalBytes > 0) "%.2f MB".format(totalBytes / (1024.0 * 1024.0)) else "?? MB"

                withContext(Dispatchers.Main) {
                    dialogBinding.progressBar.progress = progress
                    dialogBinding.tvProgressSize.text = "$downloadedMB / $totalMB"
                }
            }

            outputStream.flush()
            outputStream.close()
            inputStream.close()

            MediaScannerConnection.scanFile(
                this@DetailActivity,
                arrayOf(outputFile.absolutePath),
                null,
                null
            )

            withContext(Dispatchers.Main) {
                dialog.dismiss()
                if (downloadJob?.isCancelled != true) {
                    Toast.makeText(
                        this@DetailActivity,
                        "Downloaded to Downloads/VRVideos/$fileName",
                        Toast.LENGTH_LONG
                    ).show()
                }
            }
        } catch (e: Exception) {
            withContext(Dispatchers.Main) {
                dialog.dismiss()
                Toast.makeText(
                    this@DetailActivity,
                    "Download failed: ${e.message}",
                    Toast.LENGTH_LONG
                ).show()
            }
        }
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

//    private fun mHoneyBeeMapNavigationBannerAdsSmall() {
//        PhoneLocatorLoadAds.loadHoneyBeeMapNavigationBannerForMainMediationforotherbanner(
//            binding.bannerAd.adContainer,
//            binding.bannerAd.mainAdsView,
//            binding.bannerAd.shimmerLayout,
//            this
//        )
//    }

    private val storagePermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { granted ->
        if (granted) showDownloadDialog(streamUrl, title)
        else Toast.makeText(this, "Storage permission denied", Toast.LENGTH_SHORT).show()
    }


//    override fun onDestroy() {
//        super.onDestroy()
//        downloadJob?.cancel()
//    }

    override fun onPause() {
        super.onPause()
        PhoneLocatorLoadAds.cancelBannerRefresh()
    }

    override fun onDestroy() {
        super.onDestroy()
        PhoneLocatorLoadAds.cancelBannerRefresh()
        downloadJob?.cancel()
    }

}
