package com.videoplayer.hdvideos.vrplayer1.pickGallerydata

import android.Manifest
import android.app.AlertDialog
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Color
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.OpenableColumns
import android.provider.Settings
import android.text.SpannableString
import android.text.Spanned
import android.text.style.ForegroundColorSpan
import android.view.View
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import com.videoplayer.hdvideos.vrplayer1.Detail.DetailActivity
import com.videoplayer.hdvideos.vrplayer1.ads.PhoneLocatorLoadAds
import com.videoplayer.hdvideos.vrplayer1.ads.PhoneLocatorLoadAds.mainBanneradControl
import com.videoplayer.hdvideos.vrplayer1.ads.PhoneLocatorShowAds
import com.videoplayer.hdvideos.vrplayer1.databinding.ActivityGalleryPickBinding
import com.videoplayer.hdvideos.vrplayer1.pickGallerydata.utiles.BaseActivity


//class GalleryPickActivity  : BaseActivity() {
//
//    private lateinit var binding: ActivityGalleryPickBinding
//
//    companion object {
//        const val EXTRA_FROM_GALLERY = "extra_from_gallery"
//        private const val PERMISSION_REQUEST_CODE = 1001
//    }
//
//    private var pendingMimeType: String? = null
//
//    private val galleryLauncher = registerForActivityResult(
//        ActivityResultContracts.StartActivityForResult()
//    ) { result ->
//        if (result.resultCode == RESULT_OK) {
//            val uri = result.data?.data ?: return@registerForActivityResult
//
//            try {
//                contentResolver.takePersistableUriPermission(
//                    uri, Intent.FLAG_GRANT_READ_URI_PERMISSION
//                )
//            } catch (_: SecurityException) {}
//
//            val mimeType = contentResolver.getType(uri) ?: ""
//            val isImage  = mimeType.startsWith("image")
//            val isVideo  = mimeType.startsWith("video")
//
//            if (!isImage && !isVideo) {
//                Toast.makeText(this, "Please pick an image or video", Toast.LENGTH_SHORT).show()
//                return@registerForActivityResult
//            }
//
//            val fileName = getFileName(uri)
//
//            val intent = Intent(this, DetailPlayeractvity::class.java).apply {
//                putExtra(DetailPlayeractvity.EXTRA_TITLE,         fileName)
//                putExtra(DetailPlayeractvity.EXTRA_STREAM_URL,    uri.toString())
//                putExtra(DetailPlayeractvity.EXTRA_IMAGE,         uri.toString())
//                putExtra(DetailPlayeractvity.EXTRA_DESCRIPTION,   "")
//                putExtra(DetailPlayeractvity.EXTRA_DURATION,      "")
//                putExtra(DetailPlayeractvity.EXTRA_IS_LOCAL,      true)
//                putExtra(DetailPlayeractvity.EXTRA_FROM_TRENDING, true)
//                putExtra(EXTRA_FROM_GALLERY,                      true)
//                putExtra("is_image",                              isImage)
//                addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
//            }
//            intentWithAd(intent)
//        }
//    }
//
//    // ─── Lifecycle ────────────────────────────────────────────────────────────
//
//    override fun onCreate(savedInstanceState: Bundle?) {
//        super.onCreate(savedInstanceState)
//        binding = ActivityGalleryPickBinding.inflate(layoutInflater)
//        setContentView(binding.root)
//
//        ViewCompat.setOnApplyWindowInsetsListener(binding.root) { v, insets ->
//            val sys = insets.getInsets(WindowInsetsCompat.Type.systemBars())
//            v.setPadding(sys.left, sys.top, sys.right, sys.bottom)
//            insets
//        }
//
//        window.statusBarColor = android.graphics.Color.BLACK
//        WindowCompat.getInsetsController(window, window.decorView).apply {
//            isAppearanceLightStatusBars = false
//        }
//
//        onBackPressedDispatcher.addCallback(this, object : OnBackPressedCallback(true) {
//            override fun handleOnBackPressed() {
//                PhoneLocatorShowAds.mediationBackPressedSimpleHoneyBeeMapNavigation(
//                    this@GalleryPickActivity,
//                    PhoneLocatorLoadAds.admobInterstitialAdHoneyBee
//                )
//            }
//        })
//
//        mHoneyBeeMapNavigationBannerAdsSmall()
//        applyColoredTitles()
//
//        binding.backBtn.setOnClickListener { onBackPressedDispatcher.onBackPressed() }
//        binding.videopicker.setOnClickListener { checkPermissionAndOpen("video/*") }
//        binding.imagepicker.setOnClickListener { checkPermissionAndOpen("image/*") }
//    }
//
//    // ─── UI ───────────────────────────────────────────────────────────────────
//
//    private fun applyColoredTitles() {
//        binding.videotitle.text = SpannableString("Personal Video in VR").apply {
//            setSpan(ForegroundColorSpan(Color.parseColor("#DAE639")), 9, 14, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
//        }
//        binding.imagetitle.text = SpannableString("Personal Photo in VR").apply {
//            setSpan(ForegroundColorSpan(Color.parseColor("#DAE639")), 9, 14, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
//        }
//    }
//
//    // ─── Ads ──────────────────────────────────────────────────────────────────
//
//    private fun mHoneyBeeMapNavigationBannerAdsSmall() {
//        PhoneLocatorLoadAds.loadHoneyBeeMapNavigationBannerForMainMediationforotherbanner(
//            binding.bannerAd.adContainer,
//            binding.bannerAd.mainAdsView,
//            binding.bannerAd.shimmerLayout,
//            this
//        )
//    }
//
//    // ─── Navigation ───────────────────────────────────────────────────────────
//
//    private fun intentWithAd(intent: Intent) {
//        PhoneLocatorShowAds.meidationForClickSimpleHoneyBeeMapNavigation(
//            this,
//            PhoneLocatorLoadAds.admobInterstitialAdHoneyBee,
//            intent
//        )
//    }
//
//    // ─── Permissions ──────────────────────────────────────────────────────────
//
//    private fun requiredPermissions(): Array<String> {
//        return when {
//            Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU -> arrayOf(
//                Manifest.permission.READ_MEDIA_IMAGES,
//                Manifest.permission.READ_MEDIA_VIDEO
//            )
//            else -> arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE)
//        }
//    }
//
//    private fun hasPermissions(): Boolean {
//        return requiredPermissions().all {
//            ContextCompat.checkSelfPermission(this, it) == PackageManager.PERMISSION_GRANTED
//        }
//    }
//
//    private fun requestPermissions() {
//        ActivityCompat.requestPermissions(this, requiredPermissions(), PERMISSION_REQUEST_CODE)
//    }
//
//    private fun checkPermissionAndOpen(mimeType: String?) {
//        if (hasPermissions()) {
//            openGallery(mimeType)
//        } else {
//            pendingMimeType = mimeType ?: "any"
//            if (requiredPermissions().any {
//                    ActivityCompat.shouldShowRequestPermissionRationale(this, it)
//                }) {
//                showPermissionRationale()
//            } else {
//                requestPermissions()
//            }
//        }
//    }
//
//    private fun showPermissionRationale() {
//        AlertDialog.Builder(this)
//            .setTitle("Permission Required")
//            .setMessage("Storage permission is needed to pick images and videos from your gallery.")
//            .setPositiveButton("Grant") { _, _ -> requestPermissions() }
//            .setNegativeButton("Cancel", null)
//            .show()
//    }
//
//    override fun onRequestPermissionsResult(
//        requestCode: Int,
//        permissions: Array<out String>,
//        grantResults: IntArray
//    ) {
//        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
//        if (requestCode == PERMISSION_REQUEST_CODE) {
//            if (grantResults.all { it == PackageManager.PERMISSION_GRANTED }) {
//                val mime = pendingMimeType
//                pendingMimeType = null
//                openGallery(if (mime == "any") null else mime)
//            } else {
//                pendingMimeType = null
//                val permanentlyDenied = requiredPermissions().any {
//                    !ActivityCompat.shouldShowRequestPermissionRationale(this, it)
//                }
//                if (permanentlyDenied) showSettingsDialog()
//                else Toast.makeText(this, "Permission denied", Toast.LENGTH_SHORT).show()
//            }
//        }
//    }
//
//    private fun showSettingsDialog() {
//        AlertDialog.Builder(this)
//            .setTitle("Permission Denied")
//            .setMessage("Please enable storage permission from App Settings to pick media.")
//            .setPositiveButton("Open Settings") { _, _ ->
//                startActivity(Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS).apply {
//                    data = Uri.fromParts("package", packageName, null)
//                })
//            }
//            .setNegativeButton("Cancel", null)
//            .show()
//    }
//
//    // ─── Gallery ──────────────────────────────────────────────────────────────
//
//    private fun openGallery(mimeType: String?) {
//        val intent = Intent(Intent.ACTION_OPEN_DOCUMENT).apply {
//            addCategory(Intent.CATEGORY_OPENABLE)
//            if (mimeType == null) {
//                type = "*/*"
//                putExtra(Intent.EXTRA_MIME_TYPES, arrayOf("image/*", "video/*"))
//            } else {
//                type = mimeType
//            }
//            addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION or Intent.FLAG_GRANT_PERSISTABLE_URI_PERMISSION)
//        }
//        galleryLauncher.launch(intent)
//    }
//
//    private fun getFileName(uri: Uri): String {
//        return contentResolver.query(uri, null, null, null, null)?.use { cursor ->
//            val idx = cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME)
//            cursor.moveToFirst()
//            if (idx >= 0) cursor.getString(idx) else "Gallery File"
//        } ?: "Gallery File"
//    }
//}


class GalleryPickActivity : BaseActivity() {

    private lateinit var binding: ActivityGalleryPickBinding

    companion object {
        const val EXTRA_FROM_GALLERY = "extra_from_gallery"
    }

    // ─── Photo Picker Launchers ───────────────────────────────────────────────

    private val videoPickerLauncher = registerForActivityResult(
        ActivityResultContracts.PickVisualMedia()
    ) { uri -> uri?.let { handlePickedUri(it) } }

    private val imagePickerLauncher = registerForActivityResult(
        ActivityResultContracts.PickVisualMedia()
    ) { uri -> uri?.let { handlePickedUri(it) } }

    // ─── Lifecycle ────────────────────────────────────────────────────────────

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityGalleryPickBinding.inflate(layoutInflater)
        setContentView(binding.root)
        com.videoplayer.hdvideos.vrplayer1.ads.AnalyticsHelper.logScreen(this, "screen_personal_media")

        ViewCompat.setOnApplyWindowInsetsListener(binding.root) { v, insets ->
            val sys = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(sys.left, sys.top, sys.right, sys.bottom)
            insets
        }

        window.statusBarColor = android.graphics.Color.BLACK
        WindowCompat.getInsetsController(window, window.decorView).apply {
            isAppearanceLightStatusBars = false
        }

        onBackPressedDispatcher.addCallback(this, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                PhoneLocatorLoadAds.cancelBannerRefresh()  // ADD

                PhoneLocatorShowAds.mediationBackPressedSimpleHoneyBeeMapNavigation(
                    this@GalleryPickActivity,
                    PhoneLocatorLoadAds.admobInterstitialAdHoneyBee
                )
            }
        })

        mHoneyBeeMapNavigationBannerAdsSmall()
        applyColoredTitles()

        binding.backBtn.setOnClickListener { onBackPressedDispatcher.onBackPressed() }
        binding.videopicker.setOnClickListener {
            videoPickerLauncher.launch(
                PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.VideoOnly)
            )
        }
        binding.imagepicker.setOnClickListener {
            imagePickerLauncher.launch(
                PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
            )
        }
    }

    // ─── Handle Picked URI ────────────────────────────────────────────────────

    private fun handlePickedUri(uri: Uri) {
        try {
            contentResolver.takePersistableUriPermission(
                uri, Intent.FLAG_GRANT_READ_URI_PERMISSION
            )
        } catch (_: SecurityException) {
        }

        val mimeType = contentResolver.getType(uri) ?: ""
        val isImage = mimeType.startsWith("image")
        val isVideo = mimeType.startsWith("video")

        if (!isImage && !isVideo) {
            Toast.makeText(this, "Please pick an image or video", Toast.LENGTH_SHORT).show()
            return
        }

        val fileName = getFileName(uri)

        val intent = Intent(this, DetailPlayeractvity::class.java).apply {
            putExtra(DetailPlayeractvity.EXTRA_TITLE, fileName)
            putExtra(DetailPlayeractvity.EXTRA_STREAM_URL, uri.toString())
            putExtra(DetailPlayeractvity.EXTRA_IMAGE, uri.toString())
            putExtra(DetailPlayeractvity.EXTRA_DESCRIPTION, "")
            putExtra(DetailPlayeractvity.EXTRA_DURATION, "")
            putExtra(DetailPlayeractvity.EXTRA_IS_LOCAL, true)
            putExtra(DetailPlayeractvity.EXTRA_FROM_TRENDING, true)
            putExtra(EXTRA_FROM_GALLERY, true)
            putExtra("is_image", isImage)
            addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        }
        intentWithAd(intent)
    }

    // ─── UI ───────────────────────────────────────────────────────────────────

    private fun applyColoredTitles() {
        binding.videotitle.text = SpannableString("Personal Video in VR").apply {
            setSpan(
                ForegroundColorSpan(Color.parseColor("#DAE639")),
                9,
                14,
                Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
            )
        }
        binding.imagetitle.text = SpannableString("Personal Photo in VR").apply {
            setSpan(
                ForegroundColorSpan(Color.parseColor("#DAE639")),
                9,
                14,
                Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
            )
        }
    }

    // ─── Ads ──────────────────────────────────────────────────────────────────

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

    // ─── Navigation ───────────────────────────────────────────────────────────

    private fun intentWithAd(intent: Intent) {
        PhoneLocatorLoadAds.cancelBannerRefresh()  // ADD
        PhoneLocatorShowAds.meidationForClickSimpleHoneyBeeMapNavigation(
            this,
            PhoneLocatorLoadAds.admobInterstitialAdHoneyBee,
            intent
        )
    }

    // ─── File Name ────────────────────────────────────────────────────────────

    private fun getFileName(uri: Uri): String {
        return contentResolver.query(uri, null, null, null, null)?.use { cursor ->
            val idx = cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME)
            cursor.moveToFirst()
            if (idx >= 0) cursor.getString(idx) else "Gallery File"
        } ?: "Gallery File"
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