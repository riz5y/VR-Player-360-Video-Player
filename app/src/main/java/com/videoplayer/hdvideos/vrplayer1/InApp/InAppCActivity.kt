package com.videoplayer.hdvideos.vrplayer1.InApp

import android.content.Context
import android.content.Intent
import android.content.res.Configuration
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.View
import android.view.Window
import android.view.WindowManager
import androidx.activity.addCallback
import androidx.activity.enableEdgeToEdge
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import com.videoplayer.hdvideos.vrplayer1.Main.NewMainActivity
import com.videoplayer.hdvideos.vrplayer1.R
import com.videoplayer.hdvideos.vrplayer1.ads.PhoneLocatorPurchaseHelper
import com.videoplayer.hdvideos.vrplayer1.ads.PhoneLocatorShowAds
import com.videoplayer.hdvideos.vrplayer1.ads.utlites.ContentsLocator
import com.videoplayer.hdvideos.vrplayer1.databinding.ActivityPaywallCBinding
import com.videoplayer.hdvideos.vrplayer1.pickGallerydata.utiles.BaseActivity


class InAppCActivity  : BaseActivity() {
    private lateinit var binding: ActivityPaywallCBinding
    private lateinit var helper: PhoneLocatorPurchaseHelper
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityPaywallCBinding.inflate(layoutInflater)
        setContentView(binding.root)

        onBackPressedDispatcher.addCallback(this) {
            moveToNext()
        }

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        Handler(Looper.getMainLooper()).postDelayed({
            binding.cross.visibility = View.VISIBLE
        }, 3000)

        val window: Window = this.window
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS)
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
        window.statusBarColor = ContextCompat.getColor(this, R.color.bg_dark)
        WindowCompat.getInsetsController(window, window.decorView).isAppearanceLightStatusBars = false

        com.videoplayer.hdvideos.vrplayer1.ads.AnalyticsHelper.logScreen(
            this@InAppCActivity,
            "screen_premium"
        )

        helper = PhoneLocatorPurchaseHelper(this) {
            binding.weekly.text = "${ContentsLocator.WeeklyPrice} / week"
            binding.titleMonthlyPrice.text = ContentsLocator.monthlyPrice
            binding.lifeTimePrice.text = ContentsLocator.oneTimePrice
        }

        binding.title2.text = "One-time purchase"
        binding.mainText.text = "One-time purchase gives lifetime access. Weekly plan renews automatically."
        binding.paywallBackgroundImage.setImageResource(R.drawable.inappcnewbg)

        binding.btnNext.setOnClickListener {
            PhoneLocatorShowAds.logAnalyticsForClicks("Start Button pressed", this@InAppCActivity)
            helper.purchaseAIGpsNavWeekly()
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
            } catch (e: Exception) {
                println(e.message)
            }
        }
        binding.cross.setOnClickListener {
            onBackPressed()
        }
        binding.monthly.setOnClickListener {
            PhoneLocatorShowAds.logAnalyticsForClicks("Monthly  button pressed", this@InAppCActivity)
            helper.purchaseAIGpsNavMonthlySub()
        }
        binding.lifeTime.setOnClickListener {
            PhoneLocatorShowAds.logAnalyticsForClicks(" OneTime button pressed", this@InAppCActivity)
            helper.purchasedOneTime()
        }
    }

    override fun attachBaseContext(base: Context) {
        val config = Configuration(base.resources.configuration)
        config.fontScale = 1.0f
        val newContext = base.createConfigurationContext(config)
        super.attachBaseContext(newContext)
    }

    private fun moveToNext() {
        if (isTaskRoot) {
            val intent = Intent(this, NewMainActivity::class.java)
            startActivity(intent)
        }
        finish()
    }
}
