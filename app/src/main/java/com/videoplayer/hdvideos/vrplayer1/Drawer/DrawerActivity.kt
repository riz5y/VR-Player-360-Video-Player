package com.videoplayer.hdvideos.vrplayer1.Drawer

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.videoplayer.hdvideos.vrplayer1.Main.BottomNavigationDock
import com.videoplayer.hdvideos.vrplayer1.Main.MainNavigationTab
import com.videoplayer.hdvideos.vrplayer1.SelectedCatergory.SelectedCategoryActivity
import com.videoplayer.hdvideos.vrplayer1.VrMeditation.VrmeditationActivity
import com.videoplayer.hdvideos.vrplayer1.R
import com.videoplayer.hdvideos.vrplayer1.ads.PhoneLocatorLoadAds
import com.videoplayer.hdvideos.vrplayer1.ads.PhoneLocatorLoadAds.canShowInappIcon
import com.videoplayer.hdvideos.vrplayer1.ads.PhoneLocatorLoadAds.canShowPremium
import com.videoplayer.hdvideos.vrplayer1.ads.PhoneLocatorLoadAds.mainBanneradControl
import com.videoplayer.hdvideos.vrplayer1.ads.PhoneLocatorShowAds
import com.videoplayer.hdvideos.vrplayer1.databinding.ActivityDrawerBinding
import com.videoplayer.hdvideos.vrplayer1.pickGallerydata.utiles.BaseActivity

class DrawerActivity : BaseActivity() {

    private lateinit var binding: ActivityDrawerBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityDrawerBinding.inflate(layoutInflater)
        setContentView(binding.root)
        com.videoplayer.hdvideos.vrplayer1.ads.AnalyticsHelper.logScreen(this, "screen_drawer")

        ViewCompat.setOnApplyWindowInsetsListener(binding.main) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

//        mHoneyBeeMapNavigationBannerAdsSmall()
////        setupBottomNav()
//
        BottomNavigationDock.bind(binding.bottomNavDock, null) { tab ->
            when (tab) {
                com.videoplayer.hdvideos.vrplayer1.Main.MainNavigationTab.HOME -> {
                    val intent = Intent(this, com.videoplayer.hdvideos.vrplayer1.Main.NewMainActivity::class.java).apply {
                        flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP
                    }
                    intentWithAd(intent)
                }
                com.videoplayer.hdvideos.vrplayer1.Main.MainNavigationTab.MEDITATION -> {
                    intentWithAd(Intent(this, VrmeditationActivity::class.java))
                }
                com.videoplayer.hdvideos.vrplayer1.Main.MainNavigationTab.ROLLER_COASTER -> {
                    val intent = Intent(this, SelectedCategoryActivity::class.java).apply {
                        putExtra(SelectedCategoryActivity.EXTRA_CATEGORY, "Roller Coaster")
                        putExtra(SelectedCategoryActivity.EXTRA_TITLE, "VR Roller Coaster")
                    }
                    intentWithAd(intent)
                }
                com.videoplayer.hdvideos.vrplayer1.Main.MainNavigationTab.JURASSIC -> {
                    val intent = Intent(this, SelectedCategoryActivity::class.java).apply {
                        putExtra(SelectedCategoryActivity.EXTRA_CATEGORY, "Jurassic Video")
                        putExtra(SelectedCategoryActivity.EXTRA_TITLE, "VR Jurassic Park")
                    }
                    intentWithAd(intent)
                }
            }
        }
        onBackPressedDispatcher.addCallback(this, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                finish()
            }
        })
        mHoneyBeeMapNavigationBannerAdsSmall()
        listeners()
    }

    private fun listeners() {
        with(binding) {
//            cardPremium.visibility =
//                if (canShowPremium && canShowInappIcon) View.VISIBLE else View.GONE
//            cardPremium.setOnClickListener {
//                val intent = when (PhoneLocatorLoadAds.inAppVariant) {
//                    "B" -> Intent(this@DrawerActivity, InAppBActivity::class.java)
//                    "C" -> Intent(this@DrawerActivity, InAppCActivity::class.java)
//                    else -> Intent(this@DrawerActivity, InAppActivity::class.java)
//                }
//                startActivity(intent)
//            }
            cardPremium.visibility =
                if (canShowPremium && canShowInappIcon) View.VISIBLE else View.GONE
            cardPremium.setOnClickListener {
                startActivity(com.videoplayer.hdvideos.vrplayer1.ads.InAppABTestHelper.getInAppIntent(this@DrawerActivity))
            }

            // Select Language
            selectlanguage.setOnClickListener {
                Toast.makeText(this@DrawerActivity, "coming soon", Toast.LENGTH_SHORT).show()
            }

            // Rate Us
            raetus.setOnClickListener {
                openPlayStore()
            }

            // Share App
            sgareapps.setOnClickListener {
                shareApp()
            }

            // More Apps
            moreapps.setOnClickListener {
                Moreapps()
            }

            drawerPrivacyPolicy.setOnClickListener {
                val url = getString(R.string.privacy_policy)
                try {
                    val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
                    startActivity(intent)
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
        }
    }

    private fun openPlayStore() {
        try {
            startActivity(
                Intent(
                    Intent.ACTION_VIEW,
                    Uri.parse("https://play.google.com/store/apps/details?id=${packageName}")
                ).apply {
                    addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                }
            )
        } catch (e: Exception) {
            startActivity(
                Intent(
                    Intent.ACTION_VIEW,
                    Uri.parse("https://play.google.com/store/apps/details?id=${packageName}")
                )
            )
        }
    }

    private fun Moreapps() {
        try {
            startActivity(
                Intent(
                    Intent.ACTION_VIEW,
                    Uri.parse("https://play.google.com/store/apps/developer?id=RMAAC+PAKISTAN&hl=en")
                ).apply {
                    addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                }
            )
        } catch (e: Exception) {
            startActivity(
                Intent(
                    Intent.ACTION_VIEW,
                    Uri.parse("https://play.google.com/store/apps/developer?id=RMAAC+PAKISTAN&hl=en")
                )
            )
        }
    }

    private fun shareApp() {
        val shareIntent = Intent().apply {
            action = Intent.ACTION_SEND
            putExtra(
                Intent.EXTRA_TEXT,
                "Check out this app: https://play.google.com/store/apps/details?id=${packageName}"
            )
            type = "text/plain"
        }
        startActivity(Intent.createChooser(shareIntent, "Share App"))
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
        PhoneLocatorLoadAds.cancelBannerRefresh()  // ADD

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