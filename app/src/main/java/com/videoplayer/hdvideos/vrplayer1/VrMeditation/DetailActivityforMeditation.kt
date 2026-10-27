package com.videoplayer.hdvideos.vrplayer1.VrMeditation

import android.Manifest
import android.app.Dialog
import android.content.pm.PackageManager
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.hardware.Sensor
import android.hardware.SensorManager
import android.media.MediaScannerConnection
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.text.SpannableString
import android.text.Spanned
import android.text.style.ForegroundColorSpan
import android.util.Log
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.OptIn
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.media3.common.util.UnstableApi
import com.bumptech.glide.Glide
import com.google.android.gms.ads.AdView
import com.videoplayer.hdvideos.vrplayer1.Detail.utils.IntentKeys
import com.videoplayer.hdvideos.vrplayer1.Detail.utils.InteractionMode
import com.videoplayer.hdvideos.vrplayer1.Detail.utils.ViewMode
import com.videoplayer.hdvideos.vrplayer1.R
 import com.videoplayer.hdvideos.vrplayer1.ads.PhoneLocatorLoadAds
import com.videoplayer.hdvideos.vrplayer1.ads.PhoneLocatorLoadAds.mainBanneradControl
import com.videoplayer.hdvideos.vrplayer1.ads.PhoneLocatorShowAds
import com.videoplayer.hdvideos.vrplayer1.ads.PhoneLocatorShowAds.logAnalyticsForClicks
import com.videoplayer.hdvideos.vrplayer1.databinding.ActivityDetailActivityforGamingBinding
import com.videoplayer.hdvideos.vrplayer1.databinding.ActivityDetailActivityforMeditationBinding
import com.videoplayer.hdvideos.vrplayer1.databinding.DialogDownloadProgressBinding
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileOutputStream
import java.net.HttpURLConnection
import java.net.URL
import javax.net.ssl.HostnameVerifier
import javax.net.ssl.HttpsURLConnection
import javax.net.ssl.SSLContext
import javax.net.ssl.TrustManager
import javax.net.ssl.X509TrustManager


class DetailActivityforMeditation : AppCompatActivity() {

    private lateinit var binding: ActivityDetailActivityforMeditationBinding

    private var selectedViewMode: ViewMode = ViewMode.DEVICE
    private var selectedInteraction: InteractionMode = InteractionMode.BOTH
    private var isRepeat: Boolean = false
    private var isExitOnFinish: Boolean = false

    private var streamUrl: String = ""
    private var title: String = ""
    private var thumbnail: String = ""
    private var description: String = ""
    private var duration: String = ""

    private var downloadJob: Job? = null

    companion object {
        const val EXTRA_TITLE         = "title"
        const val EXTRA_STREAM_URL    = "videoUrl"
        const val EXTRA_THUMBNAIL     = "thumbnail"
        const val EXTRA_DESCRIPTION   = "description"
        const val EXTRA_DURATION      = "duration"

        private const val KEY_VIEW_MODE   = "key_view_mode"
        private const val KEY_INTERACTION = "key_interaction"
        private const val KEY_REPEAT      = "key_repeat"
        private const val KEY_EXIT_FINISH = "key_exit_on_finish"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityDetailActivityforMeditationBinding.inflate(layoutInflater)
        setContentView(binding.root)

        ViewCompat.setOnApplyWindowInsetsListener(binding.main) { v, insets ->
            val sys = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(sys.left, sys.top, sys.right, sys.bottom)
            insets
        }

        mHoneyBeeMapNavigationBannerAdsSmall()
        com.videoplayer.hdvideos.vrplayer1.ads.AnalyticsHelper.logScreen(this, "screen_vr_meditation_detail")
        window.statusBarColor = android.graphics.Color.BLACK
        WindowCompat.getInsetsController(window, window.decorView).apply {
            isAppearanceLightStatusBars = false
        }

        // Read extras
        title       = intent.getStringExtra(EXTRA_TITLE) ?: ""
        streamUrl   = intent.getStringExtra(EXTRA_STREAM_URL) ?: ""
        thumbnail   = intent.getStringExtra(EXTRA_THUMBNAIL) ?: ""
        description = intent.getStringExtra(EXTRA_DESCRIPTION)
            ?: "Experience this amazing 360° VR Gaming video in high quality."
        duration    = intent.getStringExtra(EXTRA_DURATION) ?: ""

        Log.d("DetailGaming", "title=$title | streamUrl=$streamUrl")

        binding.tvTitle.text       = title
        binding.tvDescription.text = description
        binding.tvDuration.text    = duration

        Glide.with(this)
            .load(thumbnail)
            .placeholder(R.drawable.personalvrbg)
            .error(R.drawable.personalvrbg)
            .override(800, 450)
            .centerCrop()
            .into(binding.heroImage)

        // Back press
        onBackPressedDispatcher.addCallback(this, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                PhoneLocatorLoadAds.cancelBannerRefresh()  // ADD
                PhoneLocatorShowAds.mediationBackPressedSimpleHoneyBeeMapNavigation(
                    this@DetailActivityforMeditation,
                    PhoneLocatorLoadAds.admobInterstitialAdHoneyBee
                )
            }
        })
        binding.backbtn.setOnClickListener { onBackPressedDispatcher.onBackPressed() }

        // See more / less
        var expanded = false
        binding.tvSeeMore.setOnClickListener {
            expanded = !expanded
            binding.tvDescription.maxLines = if (expanded) Int.MAX_VALUE else 1
            binding.tvSeeMore.text = if (expanded) "See Less" else "See More"
        }

        checkGyroscope()

        // Interaction chips
        binding.chipMotion.setOnClickListener {
            if (!hasGyroscope()) {
                Toast.makeText(this, "No gyroscope — Motion mode not supported", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            selectedInteraction = InteractionMode.MOTION
            updateModeChips(InteractionMode.MOTION)
        }
        binding.chipBoth.setOnClickListener {
            if (!hasGyroscope()) {
                Toast.makeText(this, "No gyroscope — Motion mode not supported", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            selectedInteraction = InteractionMode.BOTH
            updateModeChips(InteractionMode.BOTH)
        }
        binding.chipTouch.setOnClickListener {
            selectedInteraction = InteractionMode.TOUCH
            updateModeChips(InteractionMode.TOUCH)
        }

        // Repeat / Exit on finish
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

        // Play buttons
        binding.icSbs.setOnClickListener       { selectedViewMode = ViewMode.SBS;       launchPlayer() }
        binding.iccardboard.setOnClickListener { selectedViewMode = ViewMode.CARDBOARD;  launchPlayer() }
        binding.ondevice.setOnClickListener    { selectedViewMode = ViewMode.DEVICE;     launchPlayer() }
        binding.panoramic.setOnClickListener   { selectedViewMode = ViewMode.PANORAMIC;  launchPlayer() }

        // Download button
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
            isRepeat = true
            binding.rbRepeat.isChecked = true
            binding.rbRepeat.setTextColor(ContextCompat.getColor(this, R.color.white))
        }
    }

    // ─────────────────────────────────────────────────────────────────────────
    // Player Launch — uses PhoneLocatorShowAds interstitial (same as DetailActivity)
    // ─────────────────────────────────────────────────────────────────────────
    @OptIn(UnstableApi::class)
    private fun launchPlayer() {
        if (streamUrl.isEmpty()) {
            Toast.makeText(this, "No video URL available", Toast.LENGTH_SHORT).show()
            return
        }
        PhoneLocatorLoadAds.cancelBannerRefresh()  // ADD

        val intent = android.content.Intent(this, VrmeditationPlayerActivity::class.java).apply {
            putExtra(VrmeditationPlayerActivity.EXTRA_URI,   streamUrl)
            putExtra(VrmeditationPlayerActivity.EXTRA_TITLE, title)
            putExtra(IntentKeys.VIEW_MODE,      selectedViewMode.name)
            putExtra(IntentKeys.INTERACTION,    selectedInteraction.name)
            putExtra(IntentKeys.REPEAT,         isRepeat)
            putExtra(IntentKeys.EXIT_ON_FINISH, isExitOnFinish)
        }

        // ✅ Replaced ShowAds.showAdmobInterstitialForTimer with your PhoneLocatorShowAds
        PhoneLocatorShowAds.meidationForClickSimpleHoneyBeeMapNavigation(
            this,
            PhoneLocatorLoadAds.admobInterstitialAdHoneyBee,
            intent
        )
    }

    // ─────────────────────────────────────────────────────────────────────────
    // Download — copied from DetailActivity pattern
    // ─────────────────────────────────────────────────────────────────────────
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
            0, 8,
            Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
        )
        spannable.setSpan(
            ForegroundColorSpan(Color.parseColor("#3C8CEF")),
            9, 17,
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
            // ✅ Bypass SSL certificate validation for untrusted/self-signed certs
            val trustAllCerts = arrayOf<TrustManager>(object : X509TrustManager {
                override fun checkClientTrusted(chain: Array<java.security.cert.X509Certificate>, authType: String) {}
                override fun checkServerTrusted(chain: Array<java.security.cert.X509Certificate>, authType: String) {}
                override fun getAcceptedIssuers(): Array<java.security.cert.X509Certificate> = arrayOf()
            })

            val sslContext = SSLContext.getInstance("SSL")
            sslContext.init(null, trustAllCerts, java.security.SecureRandom())

            val httpsConn: HttpURLConnection

            val url = URL(urlString)
            val connection = url.openConnection()

            if (connection is HttpsURLConnection) {
                connection.sslSocketFactory = sslContext.socketFactory
                connection.hostnameVerifier = HostnameVerifier { _, _ -> true }
                httpsConn = connection
            } else {
                httpsConn = connection as HttpURLConnection
            }

            httpsConn.connectTimeout = 15000
            httpsConn.readTimeout    = 15000
            httpsConn.connect()

            val totalBytes = httpsConn.contentLengthLong
            val inputStream = httpsConn.inputStream
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
                this@DetailActivityforMeditation,
                arrayOf(outputFile.absolutePath),
                null,
                null
            )

            withContext(Dispatchers.Main) {
                dialog.dismiss()
                if (downloadJob?.isCancelled != true) {
                    Toast.makeText(
                        this@DetailActivityforMeditation,
                        "Downloaded to Downloads/VRVideos/$fileName",
                        Toast.LENGTH_LONG
                    ).show()
                }
            }
        } catch (e: Exception) {
            Log.e("DownloadDebug", "FULL ERROR: ${e.javaClass.simpleName} | ${e.message}", e)
            withContext(Dispatchers.Main) {
                dialog.dismiss()
                Toast.makeText(
                    this@DetailActivityforMeditation,
                    "Download failed: ${e.message}",
                    Toast.LENGTH_LONG
                ).show()
            }
        }
    }

//    private suspend fun downloadFile(
//        urlString: String,
//        title: String,
//        dialogBinding: DialogDownloadProgressBinding,
//        dialog: Dialog
//    ) {
//        try {
//            val trustAllCerts = arrayOf<TrustManager>(object : X509TrustManager {
//                override fun checkClientTrusted(chain: Array<java.security.cert.X509Certificate>, authType: String) {}
//                override fun checkServerTrusted(chain: Array<java.security.cert.X509Certificate>, authType: String) {}
//                override fun getAcceptedIssuers(): Array<java.security.cert.X509Certificate> = arrayOf()
//            })
//
//            val sslContext = SSLContext.getInstance("SSL")
//            sslContext.init(null, trustAllCerts, java.security.SecureRandom())
//
//            var currentUrl = urlString
//            var httpConn: HttpURLConnection
//            var redirectCount = 0
//
//            while (true) {
//                val url = URL(currentUrl)
//                val connection = url.openConnection()
//
//                if (connection is HttpsURLConnection) {
//                    connection.sslSocketFactory = sslContext.socketFactory
//                    connection.hostnameVerifier = HostnameVerifier { _, _ -> true }
//                    connection.instanceFollowRedirects = false
//                } else {
//                    (connection as HttpURLConnection).instanceFollowRedirects = false
//                }
//
//                httpConn = connection as HttpURLConnection
//                httpConn.connectTimeout = 15000
//                httpConn.readTimeout    = 30000
//                httpConn.connect()
//
//                val responseCode = httpConn.responseCode
//                if (responseCode in 300..399 && redirectCount < 5) {
//                    val location = httpConn.getHeaderField("Location")
//                    httpConn.disconnect()
//                    currentUrl = if (location.startsWith("http")) location
//                    else URL(URL(currentUrl), location).toString()
//                    redirectCount++
//                    continue
//                }
//                break
//            }
//
//            // ✅ Try every possible header for total size
//            var totalBytes = httpConn.contentLengthLong
//            if (totalBytes <= 0) totalBytes = httpConn.getHeaderField("Content-Length")?.toLongOrNull() ?: -1L
//            if (totalBytes <= 0) totalBytes = httpConn.getHeaderField("x-content-length")?.toLongOrNull() ?: -1L
//            if (totalBytes <= 0) totalBytes = httpConn.getHeaderField("X-Original-Content-Length")?.toLongOrNull() ?: -1L
//
//            Log.d("DownloadDebug", "totalBytes=$totalBytes | url=$currentUrl")
//
//            val inputStream = httpConn.inputStream
//            val fileName = "${title.replace("[^a-zA-Z0-9]".toRegex(), "_")}.mp4"
//
//            val vrFolder = File(
//                Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS),
//                "VRVideos"
//            )
//            if (!vrFolder.exists()) vrFolder.mkdirs()
//
//            val outputFile = File(vrFolder, fileName)
//
//            // ✅ For unknown size: read ALL bytes into memory first, then we know the real size
//            if (totalBytes <= 0) {
//                withContext(Dispatchers.Main) {
//                    dialogBinding.progressBar.isIndeterminate = true  // spinner mode
//                    dialogBinding.tvProgressSize.text = "Downloading..."
//                }
//
//                val allBytes = inputStream.readBytes()
//                totalBytes = allBytes.size.toLong()
//
//                withContext(Dispatchers.Main) {
//                    dialogBinding.progressBar.isIndeterminate = false
//                    dialogBinding.progressBar.progress = 50
//                    dialogBinding.tvProgressSize.text = "%.2f MB / %.2f MB".format(
//                        totalBytes / (1024.0 * 1024.0),
//                        totalBytes / (1024.0 * 1024.0)
//                    )
//                }
//
//                outputFile.writeBytes(allBytes)
//
//                withContext(Dispatchers.Main) {
//                    dialogBinding.progressBar.progress = 100
//                    dialogBinding.tvProgressSize.text = "%.2f MB / %.2f MB".format(
//                        totalBytes / (1024.0 * 1024.0),
//                        totalBytes / (1024.0 * 1024.0)
//                    )
//                }
//
//            } else {
//                // ✅ Known size: normal streaming with progress
//                withContext(Dispatchers.Main) {
//                    dialogBinding.progressBar.progress = 0
//                    dialogBinding.tvProgressSize.text = "0.00 MB / %.2f MB".format(
//                        totalBytes / (1024.0 * 1024.0)
//                    )
//                }
//
//                val outputStream = FileOutputStream(outputFile)
//                val buffer = ByteArray(8192)
//                var downloadedBytes = 0L
//                var bytesRead: Int
//
//                while (inputStream.read(buffer).also { bytesRead = it } != -1) {
//                    if (downloadJob?.isCancelled == true) break
//                    outputStream.write(buffer, 0, bytesRead)
//                    downloadedBytes += bytesRead
//
//                    val progress     = ((downloadedBytes * 100) / totalBytes).toInt()
//                    val downloadedMB = "%.2f MB".format(downloadedBytes / (1024.0 * 1024.0))
//                    val totalMB      = "%.2f MB".format(totalBytes / (1024.0 * 1024.0))
//
//                    withContext(Dispatchers.Main) {
//                        dialogBinding.progressBar.progress = progress
//                        dialogBinding.tvProgressSize.text  = "$downloadedMB / $totalMB"
//                    }
//                }
//
//                outputStream.flush()
//                outputStream.close()
//            }
//
//            inputStream.close()
//            httpConn.disconnect()
//
//            MediaScannerConnection.scanFile(
//                this@DetailActivityforMeditation, // 👈 change to this@DetailActivityforMeditation in Meditation
//                arrayOf(outputFile.absolutePath),
//                null, null
//            )
//
//            withContext(Dispatchers.Main) {
//                dialog.dismiss()
//                if (downloadJob?.isCancelled != true) {
//                    Toast.makeText(
//                        this@DetailActivityforMeditation, // 👈 change in Meditation
//                        "Downloaded to Downloads/VRVideos/$fileName",
//                        Toast.LENGTH_LONG
//                    ).show()
//                }
//            }
//
//        } catch (e: Exception) {
//            Log.e("DownloadDebug", "FULL ERROR: ${e.javaClass.simpleName} | ${e.message}", e)
//            withContext(Dispatchers.Main) {
//                dialog.dismiss()
//                Toast.makeText(
//                    this@DetailActivityforMeditation, // 👈 change in Meditation
//                    "Download failed: ${e.message}",
//                    Toast.LENGTH_LONG
//                ).show()
//            }
//        }
//    }


    // ─────────────────────────────────────────────────────────────────────────
    // Helpers
    // ─────────────────────────────────────────────────────────────────────────
    private fun hasGyroscope(): Boolean {
        val sm = getSystemService(SENSOR_SERVICE) as SensorManager
        return sm.getDefaultSensor(Sensor.TYPE_GYROSCOPE) != null
    }

    private fun checkGyroscope() {
        if (!hasGyroscope()) {
            Toast.makeText(this, "No gyroscope — Motion mode disabled", Toast.LENGTH_LONG).show()
        }
    }

    private fun updateModeChips(mode: InteractionMode) {
        val selected   = R.drawable.bg_chip_selected
        val unselected = R.drawable.bg_chip_unselected

        binding.chipMotion.setBackgroundResource(if (mode == InteractionMode.MOTION) selected else unselected)
        binding.chipTouch.setBackgroundResource(if (mode == InteractionMode.TOUCH)  selected else unselected)
        binding.chipBoth.setBackgroundResource(if (mode == InteractionMode.BOTH)    selected else unselected)

        if (!hasGyroscope()) {
            binding.chipMotion.alpha = 0.4f
            binding.chipBoth.alpha   = 0.4f
            if (mode == InteractionMode.MOTION || mode == InteractionMode.BOTH) {
                selectedInteraction = InteractionMode.TOUCH
                binding.chipTouch.setBackgroundResource(selected)
                binding.chipMotion.setBackgroundResource(unselected)
                binding.chipBoth.setBackgroundResource(unselected)
            }
        } else {
            binding.chipMotion.alpha = 1.0f
            binding.chipBoth.alpha   = 1.0f
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

    // ─────────────────────────────────────────────────────────────────────────
    // Save / Restore
    // ─────────────────────────────────────────────────────────────────────────
    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putString(KEY_VIEW_MODE,   selectedViewMode.name)
        outState.putString(KEY_INTERACTION, selectedInteraction.name)
        outState.putBoolean(KEY_REPEAT,      isRepeat)
        outState.putBoolean(KEY_EXIT_FINISH, isExitOnFinish)
    }

    private fun restoreState(b: Bundle) {
        selectedViewMode    = ViewMode.valueOf(b.getString(KEY_VIEW_MODE, ViewMode.DEVICE.name)!!)
        selectedInteraction = InteractionMode.valueOf(b.getString(KEY_INTERACTION, InteractionMode.TOUCH.name)!!)
        isRepeat       = b.getBoolean(KEY_REPEAT, false)
        isExitOnFinish = b.getBoolean(KEY_EXIT_FINISH, false)
        updateModeChips(selectedInteraction)
        binding.rbRepeat.isChecked       = isRepeat
        binding.rbExitOnFinish.isChecked = isExitOnFinish
    }

    // ─────────────────────────────────────────────────────────────────────────
    // AdView lifecycle
    // ─────────────────────────────────────────────────────────────────────────
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
//        downloadJob?.cancel()
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
        downloadJob?.cancel()
        (binding.bannerAd.adContainer.getChildAt(0) as? AdView)?.destroy()
    }

    private val storagePermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { granted ->
        if (granted) showDownloadDialog(streamUrl, title)
        else Toast.makeText(this, "Storage permission denied", Toast.LENGTH_SHORT).show()
    }
}