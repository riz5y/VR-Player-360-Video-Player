package com.videoplayer.hdvideos.vrplayer1.Download

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.media.MediaMetadataRetriever
import android.media.MediaScannerConnection
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.videoplayer.hdvideos.vrplayer1.Detail.DetailActivity
import com.videoplayer.hdvideos.vrplayer1.Drawer.DrawerActivity
import com.videoplayer.hdvideos.vrplayer1.Main.MainActivity
import com.videoplayer.hdvideos.vrplayer1.Main.Tab
import com.videoplayer.hdvideos.vrplayer1.ads.PhoneLocatorLoadAds
import com.videoplayer.hdvideos.vrplayer1.ads.PhoneLocatorLoadAds.mainBanneradControl
import com.videoplayer.hdvideos.vrplayer1.ads.PhoneLocatorShowAds
import com.videoplayer.hdvideos.vrplayer1.ads.PreferenceHelper
import com.videoplayer.hdvideos.vrplayer1.databinding.ActivityDownloadBinding
import com.videoplayer.hdvideos.vrplayer1.pickGallerydata.utiles.BaseActivity
import java.io.File


class DownloadActivity : BaseActivity() {

    private lateinit var binding: ActivityDownloadBinding
    private lateinit var adapter: DownloadsAdapter
    private var allVideos = mutableListOf<DownloadedVideo>()

    private val readPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { granted ->
        if (granted) loadDownloadedVideos()
        else Toast.makeText(
            this,
            "Storage permission denied — cannot load videos",
            Toast.LENGTH_SHORT
        ).show()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityDownloadBinding.inflate(layoutInflater)
        setContentView(binding.root)
        com.videoplayer.hdvideos.vrplayer1.ads.AnalyticsHelper.logScreen(this, "screen_downloads")

        ViewCompat.setOnApplyWindowInsetsListener(binding.main) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        mHoneyBeeMapNavigationBannerAdsSmall()
        setupRecyclerView()
        setupSearch()
        setupBottomNav()

        onBackPressedDispatcher.addCallback(this, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                PhoneLocatorLoadAds.cancelBannerRefresh()  // ADD

            }
        })

    }

    override fun onResume() {
        super.onResume()
        PreferenceHelper.cleanupDeletedVideoPaths(this)
        checkReadPermissionAndLoad()
    }

    private fun checkReadPermissionAndLoad() {
        when {
            // API 33+ — READ_MEDIA_VIDEO not declared, load directly
            Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU -> {
                loadDownloadedVideos()
            }
            // API 29–32 — READ_EXTERNAL_STORAGE declared up to API 32
            Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q -> {
                if (ContextCompat.checkSelfPermission(
                        this,
                        Manifest.permission.READ_EXTERNAL_STORAGE
                    )
                    == PackageManager.PERMISSION_GRANTED
                ) {
                    loadDownloadedVideos()
                } else {
                    readPermissionLauncher.launch(Manifest.permission.READ_EXTERNAL_STORAGE)
                }
            }

            else -> loadDownloadedVideos()
        }
    }

    private fun setupRecyclerView() {
        adapter = DownloadsAdapter(
            mutableListOf(),
            onItemClick = { video ->
                val intent = Intent(this, DetailActivity::class.java).apply {
                    putExtra(DetailActivity.EXTRA_TITLE, video.title)
                    putExtra(DetailActivity.EXTRA_DURATION, video.duration)
                    putExtra(DetailActivity.EXTRA_STREAM_URL, video.filePath)
                    putExtra(
                        DetailActivity.EXTRA_DESCRIPTION,
                        "Experience this amazing 360° VR video in high quality."
                    )
                    putExtra(DetailActivity.EXTRA_IMAGE, video.filePath ?: "")
                    putExtra(DetailActivity.EXTRA_IS_LOCAL, true)
                }
                startActivity(intent)
            },
            onDeleteConfirmed = { video ->
                val file = File(video.filePath)

                PreferenceHelper.saveDeletedVideoPath(this, video.filePath)

                allVideos.remove(video)
                adapter.updateList(allVideos.toMutableList())
                updateEmptyState()

                if (file.exists()) {
                    try {
                        val deleted = file.delete()
                        if (!deleted) {
                            contentResolver.delete(
                                android.provider.MediaStore.Files.getContentUri("external"),
                                "${android.provider.MediaStore.Files.FileColumns.DATA} = ?",
                                arrayOf(video.filePath)
                            )
                        }
                        MediaScannerConnection.scanFile(this, arrayOf(video.filePath), null, null)
                        Toast.makeText(this, "Video deleted successfully", Toast.LENGTH_SHORT)
                            .show()
                    } catch (e: Exception) {
                        Toast.makeText(
                            this,
                            "Error deleting video: ${e.message}",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                } else {
                    Toast.makeText(this, "Video removed", Toast.LENGTH_SHORT).show()
                }
            }
        )

        binding.rvDownloads.apply {
            layoutManager = LinearLayoutManager(this@DownloadActivity)
            adapter = this@DownloadActivity.adapter
        }
    }

    private fun loadDownloadedVideos() {
        val downloadsDir =
            Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)
        val vrFolder = File(downloadsDir, "VRVideos")

        if (!vrFolder.exists()) vrFolder.mkdirs()

        val deletedPaths = PreferenceHelper.getDeletedVideoPaths(this)

        allVideos = vrFolder.listFiles { file ->
            file.isFile && file.extension.lowercase() in listOf("mp4", "mkv", "avi", "mov", "webm")
        }?.map { file ->
            DownloadedVideo(
                title = file.nameWithoutExtension.replace("_", " "),
                filePath = file.absolutePath,
                duration = getVideoDuration(file.absolutePath),
                fileSize = formatFileSize(file.length()),
                thumbnailPath = null,
            )
        }
            ?.filter { it.filePath !in deletedPaths }
            ?.toMutableList() ?: mutableListOf()

        adapter.updateList(allVideos)
        updateEmptyState()
    }

    private fun setupSearch() {
        binding.etSearch.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                val query = s.toString().lowercase()
                val filtered = if (query.isEmpty()) {
                    allVideos
                } else {
                    allVideos.filter { it.title.lowercase().contains(query) }.toMutableList()
                }
                adapter.updateList(filtered)
            }

            override fun afterTextChanged(s: Editable?) {}
        })
    }

    private fun updateEmptyState() {
        binding.tvEmpty.visibility = if (allVideos.isEmpty()) View.VISIBLE else View.GONE
        binding.rvDownloads.visibility = if (allVideos.isEmpty()) View.GONE else View.VISIBLE
    }

    private fun getVideoDuration(path: String): String {
        return try {
            val retriever = MediaMetadataRetriever()
            retriever.setDataSource(path)
            val durationMs = retriever.extractMetadata(
                MediaMetadataRetriever.METADATA_KEY_DURATION
            )?.toLong() ?: 0L
            retriever.release()
            val minutes = durationMs / 1000 / 60
            val seconds = durationMs / 1000 % 60
            "${minutes}m ${seconds}s"
        } catch (e: Exception) {
            "0m 0s"
        }
    }

    private fun formatFileSize(bytes: Long): String {
        return when {
            bytes >= 1024 * 1024 * 1024 -> "%.1f GB".format(bytes / (1024.0 * 1024.0 * 1024.0))
            bytes >= 1024 * 1024 -> "%.1f MB".format(bytes / (1024.0 * 1024.0))
            bytes >= 1024 -> "%.1f KB".format(bytes / 1024.0)
            else -> "$bytes B"
        }
    }

    private fun setupBottomNav() {
        binding.layoutBottomNav.tabDownloadsSelected.visibility = View.VISIBLE
        binding.layoutBottomNav.tabDownloadsUnselected.visibility = View.GONE
        binding.layoutBottomNav.tabCategorySelected.visibility = View.GONE
        binding.layoutBottomNav.tabCategoryUnselected.visibility = View.VISIBLE
        binding.layoutBottomNav.tabSettingSelected.visibility = View.GONE
        binding.layoutBottomNav.tabSettingUnselected.visibility = View.VISIBLE

        binding.layoutBottomNav.tabCategorySelected.setOnClickListener { goToCategory() }
        binding.layoutBottomNav.tabCategoryUnselected.setOnClickListener { goToCategory() }
        binding.layoutBottomNav.tabDownloadsSelected.setOnClickListener { /* already here */ }
        binding.layoutBottomNav.tabDownloadsUnselected.setOnClickListener { /* already here */ }
        binding.layoutBottomNav.tabSettingSelected.setOnClickListener { goToSetting() }
        binding.layoutBottomNav.tabSettingUnselected.setOnClickListener { goToSetting() }
    }

    private fun goToCategory() {
        intentWithAd(Intent(this, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP
            putExtra(MainActivity.EXTRA_TAB, Tab.CATEGORY.name)
        })
    }

    private fun goToSetting() {
        intentWithAd(Intent(this, DrawerActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP
        })
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

    private fun intentWithAd(intent: Intent) {
        PhoneLocatorLoadAds.cancelBannerRefresh()

        PhoneLocatorShowAds.meidationForClickSimpleHoneyBeeMapNavigation(
            this,
            PhoneLocatorLoadAds.admobInterstitialAdHoneyBee,
            intent
        )
        finish()
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