package com.videoplayer.hdvideos.vrplayer1.OnBoarding

import android.app.Dialog
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.view.View
import android.view.WindowManager
import androidx.activity.OnBackPressedCallback
import androidx.activity.enableEdgeToEdge
import androidx.core.view.ViewCompat
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.viewpager2.widget.ViewPager2
import com.videoplayer.hdvideos.vrplayer1.InApp.InAppActivity
import com.videoplayer.hdvideos.vrplayer1.InApp.InAppBActivity
import com.videoplayer.hdvideos.vrplayer1.InApp.InAppCActivity
import com.videoplayer.hdvideos.vrplayer1.Main.NewMainActivity
import com.videoplayer.hdvideos.vrplayer1.R
import com.videoplayer.hdvideos.vrplayer1.ads.AnalyticsHelper
import com.videoplayer.hdvideos.vrplayer1.ads.InAppABTestHelper
import com.videoplayer.hdvideos.vrplayer1.ads.PhoneLocatorAppPurchaseHelper
import com.videoplayer.hdvideos.vrplayer1.ads.PhoneLocatorLoadAds
import com.videoplayer.hdvideos.vrplayer1.ads.PhoneLocatorLoadAds.inAppVariant
import com.videoplayer.hdvideos.vrplayer1.ads.PhoneLocatorMyAppNativeAds
import com.videoplayer.hdvideos.vrplayer1.ads.PhoneLocatorPurchaseHelper
import com.videoplayer.hdvideos.vrplayer1.ads.PhoneLocatorShowAds
import com.videoplayer.hdvideos.vrplayer1.ads.PhoneLocatorShowAds.logAnalyticsForClicks
import com.videoplayer.hdvideos.vrplayer1.ads.PreferenceHelper
import com.videoplayer.hdvideos.vrplayer1.databinding.ActivityOnboardingBinding
import com.videoplayer.hdvideos.vrplayer1.pickGallerydata.utiles.BaseActivity

class OnboardingActivity : BaseActivity() {
    private lateinit var binding: ActivityOnboardingBinding
    private lateinit var viewPagerAdapter: OnboardingViewPagerAdapter
    private lateinit var billingHelper: PhoneLocatorPurchaseHelper
    private var waitDialog: Dialog? = null

    private var isBtnClicked = false
    private var canMoveToNext = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityOnboardingBinding.inflate(layoutInflater)
        setContentView(binding.root)

        ViewCompat.setOnApplyWindowInsetsListener(binding.main) { _, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            binding.main.setPadding(0, 0, 0, systemBars.bottom)
            insets
        }

        AnalyticsHelper.logScreen(this@OnboardingActivity, "screen_onboarding")

        onBackPressedDispatcher.addCallback(this, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                moveNext()
            }
        })

        billingHelper = PhoneLocatorPurchaseHelper(this)

        if (PhoneLocatorLoadAds.canShowNativeOnBoarding) {
            showNativeAd()
        } else {
            binding.nativeAds.adViewUnified.visibility = View.GONE
        }

        viewPagerAdapter = OnboardingViewPagerAdapter(this)
        binding.viewPager.adapter = viewPagerAdapter
        binding.dotsIndicator.attachTo(binding.viewPager)


        binding.viewPager.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                val totalFragments = viewPagerAdapter.itemCount

                if (position == totalFragments - 1) {
                    val hasPurchased =
                        !PhoneLocatorAppPurchaseHelper(this@OnboardingActivity).shouldShowAds()
                    if (PhoneLocatorLoadAds.OnboardingAdshow && !hasPurchased) {
                        PhoneLocatorLoadAds.loadSplashOnboardingAd(
                            this@OnboardingActivity,
                            object : PhoneLocatorLoadAds.AdLoadCallbackForIntro {
                                override fun onAdResult() {
                                    canMoveToNext = true
                                    if (isBtnClicked) {
                                        removeAdsDialog()
                                        adsShow()
                                    }
                                }
                            }
                        )
                    } else {
                        canMoveToNext = true
                    }
                }

                val isFullScreenAdPage = viewPagerAdapter.isFullScreenAdPage(position)

                val showPromoCard = position == 0 && PhoneLocatorLoadAds.onboardingPremiumCard
                if (showPromoCard && binding.cardInAppPromo.visibility != View.VISIBLE) {
                    binding.cardInAppPromo.apply {
                        alpha = 0f
                        translationY = 40f
                        visibility = View.VISIBLE
                        animate().alpha(1f).translationY(0f).setDuration(350).start()
                    }
                } else if (!showPromoCard) {
                    binding.cardInAppPromo.visibility = View.GONE
                }

                if (isFullScreenAdPage || showPromoCard) {
                    binding.nativeAds.adViewUnified.visibility = View.GONE
                } else {
                    binding.nativeAds.adViewUnified.visibility =
                        if (PhoneLocatorLoadAds.canShowNativeOnBoarding) View.VISIBLE else View.GONE
                }

                if (isFullScreenAdPage) {
                    binding.dotsIndicator.visibility = View.GONE
                    binding.btnNext.visibility = View.GONE
                } else {
                    binding.dotsIndicator.visibility = View.VISIBLE
                    binding.btnNext.visibility = View.VISIBLE
                }

                if (isFullScreenAdPage || position == totalFragments - 1) {
                    binding.lottieView.visibility = View.GONE
                    binding.lottieView.pauseAnimation()
                } else {
                    binding.lottieView.visibility = View.VISIBLE
                    binding.lottieView.playAnimation()
                }

                if (position == totalFragments - 1) {
                    binding.tvDownload.text = "Get Started"
                } else {
                    binding.tvDownload.text = "Next"
                }
            }
        })

        binding.viewPager.setPageTransformer { page, position ->
            val closeness = 1f - kotlin.math.abs(position).coerceIn(0f, 1f)
            page.alpha = 0.4f + 0.6f * closeness
            val scale = 0.94f + 0.06f * closeness
            page.scaleX = scale
            page.scaleY = scale
        }

        binding.btnNext.setOnClickListener { v ->
            v.animate().scaleX(0.94f).scaleY(0.94f).setDuration(80).withEndAction {
                v.animate().scaleX(1f).scaleY(1f).setDuration(120).start()
            }.start()
            moveNext()
        }
        binding.cardInAppPromo.setOnClickListener {
            startActivity(InAppABTestHelper.getInAppIntent(this))
        }
    }

    private fun showNativeAd() {
        PhoneLocatorMyAppNativeAds.adsNativeOnBoarding(
            this@OnboardingActivity,
            binding.nativeAds.adViewUnified
        )
    }

    private fun moveNext() {
        val currentItem = binding.viewPager.currentItem
        val totalFragments = viewPagerAdapter.itemCount

        if (currentItem == totalFragments - 1) {
            logAnalyticsForClicks("OnboardingClickOnGetStarted", this@OnboardingActivity)
            saveOnboarded(this@OnboardingActivity)
            isBtnClicked = true
            if (canMoveToNext) {
                adsShow()
            } else {
                showAdsDialog()
            }
        } else {
            binding.viewPager.currentItem = currentItem + 1
        }
    }

    private fun adsShow() {
        moveToForward()
    }

    private fun moveToForward() {
        PreferenceHelper.setOnboardingDone(this@OnboardingActivity)
        val billingHelper = PhoneLocatorAppPurchaseHelper(this@OnboardingActivity)
        com.videoplayer.hdvideos.vrplayer1.ads.AnalyticsHelper.logEvent(
            this@OnboardingActivity,
            "onboarding_completed"
        )
        val hasPurchased = !billingHelper.shouldShowAds()
        val intent = if (PhoneLocatorLoadAds.canShowPremium && !hasPurchased) {
            getInAppIntent()
        } else {
            PhoneLocatorShowAds.logAnalyticsForClicks(
                "OnboardingActivity to MainActivity",
                this@OnboardingActivity
            )
            Intent(this@OnboardingActivity, NewMainActivity::class.java)
        }
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        intentWithAd(intent)
    }

    //    private fun getInAppIntent(): Intent {
//        return when (inAppVariant) {
//            "B" -> Intent(this@OnboardingActivity, InAppBActivity::class.java)
//            "C" -> Intent(this@OnboardingActivity, InAppCActivity::class.java)
//            else -> Intent(this@OnboardingActivity, InAppActivity::class.java)
//        }
//    }
    private fun getInAppIntent(): Intent {
        return InAppABTestHelper.getInAppIntent(this@OnboardingActivity)
    }

    private fun intentWithAd(intent: Intent) {
        PhoneLocatorShowAds.meidationForClickSimpleHoneyBeeMapOnBoarding(
            this,
            PhoneLocatorLoadAds.admobInterstitialAdHoneyBeeOnboarding,
            intent
        )
    }

    private fun showAdsDialog() {
        if (waitDialog?.isShowing == true) return
        waitDialog = Dialog(
            this@OnboardingActivity,
            android.R.style.Theme_Black_NoTitleBar_Fullscreen
        ).apply {
            setContentView(R.layout.dialog_loading)
            setCancelable(false)
            window?.apply {
                setLayout(
                    WindowManager.LayoutParams.MATCH_PARENT,
                    WindowManager.LayoutParams.MATCH_PARENT
                )
                setFlags(
                    WindowManager.LayoutParams.FLAG_FULLSCREEN,
                    WindowManager.LayoutParams.FLAG_FULLSCREEN
                )
                statusBarColor = Color.WHITE
            }
            show()
        }
    }

    private fun removeAdsDialog() {
        waitDialog?.dismiss()
        waitDialog = null
    }

    fun goToNextPage() {
        val currentItem = binding.viewPager.currentItem
        val totalFragments = viewPagerAdapter.itemCount
        if (currentItem == totalFragments - 1) {
            saveOnboarded(this@OnboardingActivity)
            adsShow()
        } else {
            binding.viewPager.currentItem = currentItem + 1
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        removeAdsDialog()
    }

}
