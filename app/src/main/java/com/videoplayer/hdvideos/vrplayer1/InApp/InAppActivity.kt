package com.videoplayer.hdvideos.vrplayer1.InApp

import android.content.Context
import android.content.Intent
import android.content.res.Configuration
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.View
import androidx.activity.addCallback
import androidx.activity.enableEdgeToEdge
import androidx.core.view.ViewCompat
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.updateLayoutParams
import com.videoplayer.hdvideos.vrplayer1.Main.NewMainActivity
import com.videoplayer.hdvideos.vrplayer1.R
import com.videoplayer.hdvideos.vrplayer1.ads.PhoneLocatorPurchaseHelper
import com.videoplayer.hdvideos.vrplayer1.ads.PhoneLocatorShowAds
import com.videoplayer.hdvideos.vrplayer1.ads.utlites.ContentsLocator
import com.videoplayer.hdvideos.vrplayer1.databinding.ActivityPaywallBinding
import com.videoplayer.hdvideos.vrplayer1.pickGallerydata.utiles.BaseActivity


class InAppActivity  : BaseActivity() {
    private lateinit var binding: ActivityPaywallBinding
    private lateinit var helper: PhoneLocatorPurchaseHelper
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityPaywallBinding.inflate(layoutInflater)
        setContentView(binding.root)


        onBackPressedDispatcher.addCallback(this) {
            moveToNext()
        }

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { _, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            binding.scrollContent.setPadding(0, 0, 0, systemBars.bottom)
            binding.cross.updateLayoutParams<android.view.ViewGroup.MarginLayoutParams> {
                topMargin = systemBars.top + (15 * resources.displayMetrics.density).toInt()
            }
            insets
        }


        Handler(Looper.getMainLooper()).postDelayed({
            binding.cross.visibility = View.VISIBLE
        }, 3000)
        WindowCompat.getInsetsController(window, window.decorView).isAppearanceLightStatusBars = false



        com.videoplayer.hdvideos.vrplayer1.ads.AnalyticsHelper.logScreen(
            this@InAppActivity,
            "screen_premium"
        )
        helper = PhoneLocatorPurchaseHelper(this) {
            binding.weekly.text = "${ContentsLocator.WeeklyPrice} / week"
            binding.titleMonthlyPrice.text = ContentsLocator.monthlyPrice
            binding.lifeTimePrice.text = ContentsLocator.yearlyPrice
        }
        binding.title2.text = "One time Pay"
        binding.mainText.text =
            "Subscriptions renew automatically. Lifetime is a one-time purchase."
        binding.paywallBackgroundImage.setImageResource(R.drawable.paywall_hero_a)

        // Apply Remote Config overrides
        with(com.videoplayer.hdvideos.vrplayer1.ads.PhoneLocatorLoadAds) {
            if (inAppCtaText.isNotBlank()) binding.btnNext.text = inAppCtaText
            if (inAppSubtitle.isNotBlank()) binding.tvUnlockSubtitle.text = inAppSubtitle
        }

        binding.btnNext.setOnClickListener {
            PhoneLocatorShowAds.logAnalyticsForClicks(
                "Start Button pressed",
                this@InAppActivity
            )
            helper.purchaseAIGpsNavMonthlySub()
        }
        binding.weeklyPlan.setOnClickListener {
            helper.purchaseAIGpsNavWeekly()
        }
        binding.restoreText.setOnClickListener {
            try {
                val browserIntent = Intent(
                    Intent.ACTION_VIEW,
                    Uri.parse("https://play.google.com/store/account/subscriptions"))
                startActivity(browserIntent)
            }catch (e:Exception){
                println(e.message)
            }
        }

        binding.cross.setOnClickListener {
            onBackPressed()
        }
        binding.monthly.setOnClickListener {
            PhoneLocatorShowAds.logAnalyticsForClicks(
                "Monthly  button pressed",
                this@InAppActivity
            )
            helper.purchaseAIGpsNavMonthlySub()
        }

        binding.lifeTime.setOnClickListener {

            PhoneLocatorShowAds.logAnalyticsForClicks(
                " yearly button pressed",
                this@InAppActivity
            )
            helper.purchaseAIGpsNavYearSub()
        }

    }
    override fun attachBaseContext(base: Context) {
        val config = Configuration(base.resources.configuration)
        config.fontScale = 1.0f
        val newContext = base.createConfigurationContext(config)
        super.attachBaseContext(newContext)
    }
    private fun moveToNext(){
        if (isTaskRoot) {
            val intent = Intent(this, NewMainActivity::class.java)
            startActivity(intent)
        }
        finish()
    }

}