package com.videoplayer.hdvideos.vrplayer1.pickGallerydata.utiles

import android.content.Context
import android.content.res.Configuration
import androidx.appcompat.app.AppCompatActivity

open class BaseActivity : AppCompatActivity() {
    override fun attachBaseContext(base: Context) {
        val config = Configuration(base.resources.configuration)
        config.fontScale = 1.0f
        val newContext = base.createConfigurationContext(config)
        super.attachBaseContext(newContext)
    }
}