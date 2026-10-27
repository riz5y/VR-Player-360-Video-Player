package com.videoplayer.hdvideos.vrplayer1.cinema

import android.Manifest
import android.content.Intent
import android.content.pm.ActivityInfo
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.OpenableColumns
import android.view.View
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.OptIn
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.media3.common.util.UnstableApi
import com.bumptech.glide.Glide
import com.videoplayer.hdvideos.vrplayer1.Detail.utils.IntentKeys
import com.videoplayer.hdvideos.vrplayer1.Detail.utils.InteractionMode
import com.videoplayer.hdvideos.vrplayer1.R
import com.videoplayer.hdvideos.vrplayer1.ads.PhoneLocatorLoadAds
import com.videoplayer.hdvideos.vrplayer1.ads.PhoneLocatorLoadAds.mainBanneradControl
import com.videoplayer.hdvideos.vrplayer1.ads.PhoneLocatorShowAds
import com.videoplayer.hdvideos.vrplayer1.ads.PhoneLocatorShowAds.logAnalyticsForClicks
import com.videoplayer.hdvideos.vrplayer1.databinding.ActivityTheatreVideoPickerBinding


class TheatreVideoPickerActivity : AppCompatActivity() {

    private lateinit var binding: ActivityTheatreVideoPickerBinding

    private var videoUriStr: String? = null
    private var selectedInteraction: InteractionMode? = null

    private lateinit var gyro: TheatreGyroscopeController
    private lateinit var permissionLauncher: ActivityResultLauncher<String>

    private val pickVideo = registerForActivityResult(
        ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        uri ?: return@registerForActivityResult
        try {
            contentResolver.takePersistableUriPermission(
                uri, Intent.FLAG_GRANT_READ_URI_PERMISSION
            )
        } catch (_: SecurityException) {
        }

        videoUriStr = uri.toString()
        val name = resolveFileName(uri) ?: uri.lastPathSegment ?: "video"
        com.videoplayer.hdvideos.vrplayer1.ads.AnalyticsHelper.logEvent(
            this,
            "feature_vr_cinema_video_selected",
            mapOf("name" to name)
        )
        showSelectedVideo(uri, name)
        updatePlayButtonState()
    }

    private val TAG = "TheatreSetup"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityTheatreVideoPickerBinding.inflate(layoutInflater)
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(binding.main) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        mHoneyBeeMapNavigationBannerAdsSmall()
        com.videoplayer.hdvideos.vrplayer1.ads.AnalyticsHelper.logScreen(this, "screen_vr_cinema")

        window.statusBarColor = android.graphics.Color.BLACK
        WindowCompat.getInsetsController(window, window.decorView).apply {
            isAppearanceLightStatusBars = false  // false = white icons
        }
        requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT

        gyro = TheatreGyroscopeController(this)

        permissionLauncher = registerForActivityResult(
            ActivityResultContracts.RequestPermission()
        ) { granted ->
            if (granted) pickVideo.launch("video/*")
            else Toast.makeText(this, "Storage permission denied", Toast.LENGTH_SHORT).show()
        }

        setupSelectionUI()
    }

    @OptIn(UnstableApi::class)
    private fun setupSelectionUI() {


        onBackPressedDispatcher.addCallback(this, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                PhoneLocatorLoadAds.cancelBannerRefresh()  // ADD
                PhoneLocatorShowAds.mediationBackPressedSimpleHoneyBeeMapNavigation(
                    this@TheatreVideoPickerActivity,
                    PhoneLocatorLoadAds.admobInterstitialAdHoneyBee
                )
            }
        })
        binding.backBtn.setOnClickListener {
            onBackPressedDispatcher.onBackPressed()

        }
        // ── Pick Video ─────────────────────────────────────────────────────
        binding.btnTheatrePickVideo.setOnClickListener {
            when {
                Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU ->
                    pickVideo.launch("video/*")

                ContextCompat.checkSelfPermission(
                    this, Manifest.permission.READ_EXTERNAL_STORAGE
                ) == PackageManager.PERMISSION_GRANTED ->
                    pickVideo.launch("video/*")

                else ->
                    permissionLauncher.launch(Manifest.permission.READ_EXTERNAL_STORAGE)
            }
        }

        // ── Gyroscope check ────────────────────────────────────────────────
        val hasGyro = gyro.isAvailable

        if (!hasGyro) {
            binding.cardMotion.alpha = 0.4f
            binding.cardBoth.alpha = 0.4f
        }

        // ── Interaction mode cards ─────────────────────────────────────────
        binding.cardTouch.setOnClickListener {
            selectedInteraction = InteractionMode.TOUCH
            updateInteractionSelection(InteractionMode.TOUCH)
            updatePlayButtonState()
        }

        binding.cardMotion.setOnClickListener {
            if (!hasGyro) {
                showNoGyroscopeDialog()
                return@setOnClickListener
            }
            selectedInteraction = InteractionMode.MOTION
            updateInteractionSelection(InteractionMode.MOTION)
            updatePlayButtonState()
        }

        binding.cardBoth.setOnClickListener {
            if (!hasGyro) {
                showNoGyroscopeDialog()
                return@setOnClickListener
            }
            selectedInteraction = InteractionMode.BOTH
            updateInteractionSelection(InteractionMode.BOTH)
            updatePlayButtonState()
        }

        // ── Initial state ──────────────────────────────────────────────────
        val preselected = intent.getStringExtra(IntentKeys.VIDEO_URI)
        if (!preselected.isNullOrEmpty()) {
            videoUriStr = preselected
            val uri = Uri.parse(preselected)
            val name = resolveFileName(uri) ?: uri.lastPathSegment ?: "Sample_VR_Cinema_Movie.mp4"
            showSelectedVideo(uri, name)
        }
        updatePlayButtonState()

        // ── Play button ────────────────────────────────────────────────────
        binding.btnTheatrePlay.setOnClickListener {
            if (videoUriStr.isNullOrEmpty()) {
                Toast.makeText(this, "Please select a video first", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            if (selectedInteraction == null) {
                Toast.makeText(this, "Please select an interaction mode", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val intent = Intent(this, TheatrePlaybackActivity::class.java).apply {
                putExtra(IntentKeys.VIDEO_URI, videoUriStr)
                putExtra(IntentKeys.INTERACTION, selectedInteraction!!.name)
                if (videoUriStr!!.startsWith("content://"))
                    addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
            }
            com.videoplayer.hdvideos.vrplayer1.ads.AnalyticsHelper.logEvent(
                this,
                "feature_vr_cinema_play_clicked",
                mapOf("mode" to selectedInteraction!!.name)
            )
            intentWithAd(intent)
//            ShowAds.showAdmobInterstitialForTimer(
//                this,
//                AppLoadAds.admobInterstitialAd,
//                intent
//            )
        }
    }

    private fun intentWithAd(intent: Intent) {
        PhoneLocatorLoadAds.cancelBannerRefresh()  // ADD

        PhoneLocatorShowAds.meidationForClickSimpleHoneyBeeMapNavigation(
            this,
            PhoneLocatorLoadAds.admobInterstitialAdHoneyBee,
            intent
        )
    }

    private fun updateInteractionSelection(mode: InteractionMode) {
        val transparent = android.graphics.Color.TRANSPARENT
        val greenColor = android.graphics.Color.parseColor("#C8F135")
        val selectedBg = android.graphics.Color.parseColor("#2F2D33")
        val unselectedBg = android.graphics.Color.parseColor("#1B1A1D")
        val strokeSelected = 4
        val strokeUnselected = 0

        // ── Reset all ──
        binding.cardTouch.strokeColor = transparent
        binding.cardMotion.strokeColor = transparent
        binding.cardBoth.strokeColor = transparent

        binding.cardTouch.strokeWidth = strokeUnselected
        binding.cardMotion.strokeWidth = strokeUnselected
        binding.cardBoth.strokeWidth = strokeUnselected

        binding.cardTouch.setCardBackgroundColor(unselectedBg)
        binding.cardMotion.setCardBackgroundColor(unselectedBg)
        binding.cardBoth.setCardBackgroundColor(unselectedBg)

        binding.rbTheatreTouch.visibility = View.INVISIBLE
        binding.rbTheatreMotion.visibility = View.INVISIBLE
        binding.rbTheatreBoth.visibility = View.INVISIBLE

        // ── Apply selected ──
        when (mode) {
            InteractionMode.TOUCH -> {
                binding.cardTouch.strokeColor = greenColor
                binding.cardTouch.strokeWidth = strokeSelected
                binding.cardTouch.setCardBackgroundColor(selectedBg)
                binding.rbTheatreTouch.visibility = View.VISIBLE
            }

            InteractionMode.MOTION -> {
                binding.cardMotion.strokeColor = greenColor
                binding.cardMotion.strokeWidth = strokeSelected
                binding.cardMotion.setCardBackgroundColor(selectedBg)
                binding.rbTheatreMotion.visibility = View.VISIBLE
            }

            InteractionMode.BOTH -> {
                binding.cardBoth.strokeColor = greenColor
                binding.cardBoth.strokeWidth = strokeSelected
                binding.cardBoth.setCardBackgroundColor(selectedBg)
                binding.rbTheatreBoth.visibility = View.VISIBLE
            }
        }
    }

    private fun updatePlayButtonState() {
        val ready = !videoUriStr.isNullOrEmpty() && selectedInteraction != null
        binding.btnTheatrePlay.isEnabled = ready
        binding.btnTheatrePlay.alpha = if (ready) 1f else 0.4f
    }

    private fun showSelectedVideo(uri: Uri, name: String) {
        binding.tvTheatreVideoStatus.text = "VIDEO SELECTED"
        binding.tvTheatreVideoStatus.setTextColor(android.graphics.Color.parseColor("#C8F135"))
        binding.tvTheatreSelectedFile.text = name
        binding.cardSelectedVideoInfo.strokeColor =
            android.graphics.Color.parseColor("#C8F135")
        binding.tvBtnSelectVideo.text = "Change Video"
        binding.textView2.text = "Choose another video from your library at any time."

        Glide.with(this)
            .load(uri)
            .centerCrop()
            .placeholder(R.drawable.placeholder_thumb)
            .into(binding.ivTheatreVideoPreview)
    }


    private fun showNoGyroscopeDialog() {
        AlertDialog.Builder(this)
            .setTitle("No Gyroscope Detected")
            .setMessage("Your device does not have a gyroscope sensor. Motion and Both modes are unavailable. Please use Touch mode instead.")
            .setPositiveButton("OK") { dialog, _ -> dialog.dismiss() }
            .show()
    }

    private fun resolveFileName(uri: Uri): String? =
        contentResolver.query(uri, null, null, null, null)?.use { cursor ->
            val idx = cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME)
            if (idx != -1 && cursor.moveToFirst()) cursor.getString(idx) else null
        }

    //    private fun mStreetViewTruckBannerAdsSmall() {
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