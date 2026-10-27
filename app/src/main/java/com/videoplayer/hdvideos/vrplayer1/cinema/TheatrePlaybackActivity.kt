package com.videoplayer.hdvideos.vrplayer1.cinema

//import android.os.Bundle
//import androidx.activity.enableEdgeToEdge
//import androidx.appcompat.app.AppCompatActivity
//import androidx.core.view.ViewCompat
//import androidx.core.view.WindowInsetsCompat
//import com.videoplayer.hdvideos.vrplayer1.R
//
//class TheatrePlaybackActivity : AppCompatActivity() {
//    override fun onCreate(savedInstanceState: Bundle?) {
//        super.onCreate(savedInstanceState)
//        enableEdgeToEdge()
//        setContentView(R.layout.activity_theatre_playback)
//        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
//            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
//            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
//            insets
//        }
//    }
//}


import com.videoplayer.hdvideos.vrplayer1.Detail.utils.enterPlayerFullscreen
import android.Manifest
import com.videoplayer.hdvideos.vrplayer1.Detail.utils.enterPlayerFullscreen
import android.content.Intent
import com.videoplayer.hdvideos.vrplayer1.Detail.utils.enterPlayerFullscreen
import android.content.pm.ActivityInfo
import com.videoplayer.hdvideos.vrplayer1.Detail.utils.enterPlayerFullscreen
import android.opengl.GLSurfaceView
import com.videoplayer.hdvideos.vrplayer1.Detail.utils.enterPlayerFullscreen
import android.os.Build
import com.videoplayer.hdvideos.vrplayer1.Detail.utils.enterPlayerFullscreen
import android.os.Bundle
import com.videoplayer.hdvideos.vrplayer1.Detail.utils.enterPlayerFullscreen
import android.os.Handler
import com.videoplayer.hdvideos.vrplayer1.Detail.utils.enterPlayerFullscreen
import android.os.Looper
import com.videoplayer.hdvideos.vrplayer1.Detail.utils.enterPlayerFullscreen
import android.os.VibrationEffect
import com.videoplayer.hdvideos.vrplayer1.Detail.utils.enterPlayerFullscreen
import android.os.Vibrator
import com.videoplayer.hdvideos.vrplayer1.Detail.utils.enterPlayerFullscreen
import android.os.VibratorManager
import com.videoplayer.hdvideos.vrplayer1.Detail.utils.enterPlayerFullscreen
import android.util.Log
import com.videoplayer.hdvideos.vrplayer1.Detail.utils.enterPlayerFullscreen
import android.view.Choreographer
import com.videoplayer.hdvideos.vrplayer1.Detail.utils.enterPlayerFullscreen
import android.view.MotionEvent
import com.videoplayer.hdvideos.vrplayer1.Detail.utils.enterPlayerFullscreen
import android.view.ScaleGestureDetector
import com.videoplayer.hdvideos.vrplayer1.Detail.utils.enterPlayerFullscreen
import android.view.Surface
import com.videoplayer.hdvideos.vrplayer1.Detail.utils.enterPlayerFullscreen
import android.view.View
import com.videoplayer.hdvideos.vrplayer1.Detail.utils.enterPlayerFullscreen
import android.widget.SeekBar
import com.videoplayer.hdvideos.vrplayer1.Detail.utils.enterPlayerFullscreen
import android.widget.Toast
import com.videoplayer.hdvideos.vrplayer1.Detail.utils.enterPlayerFullscreen
import androidx.activity.OnBackPressedCallback
import com.videoplayer.hdvideos.vrplayer1.Detail.utils.enterPlayerFullscreen
import androidx.activity.addCallback
import com.videoplayer.hdvideos.vrplayer1.Detail.utils.enterPlayerFullscreen
import androidx.activity.enableEdgeToEdge
import com.videoplayer.hdvideos.vrplayer1.Detail.utils.enterPlayerFullscreen
import androidx.annotation.RequiresPermission
import com.videoplayer.hdvideos.vrplayer1.Detail.utils.enterPlayerFullscreen
import androidx.appcompat.app.AppCompatActivity
import com.videoplayer.hdvideos.vrplayer1.Detail.utils.enterPlayerFullscreen
import androidx.core.net.toUri
import com.videoplayer.hdvideos.vrplayer1.Detail.utils.enterPlayerFullscreen
import androidx.core.view.ViewCompat
import com.videoplayer.hdvideos.vrplayer1.Detail.utils.enterPlayerFullscreen
import androidx.core.view.WindowCompat
import com.videoplayer.hdvideos.vrplayer1.Detail.utils.enterPlayerFullscreen
import androidx.core.view.WindowInsetsCompat
import com.videoplayer.hdvideos.vrplayer1.Detail.utils.enterPlayerFullscreen
import androidx.media3.common.MediaItem
import com.videoplayer.hdvideos.vrplayer1.Detail.utils.enterPlayerFullscreen
import androidx.media3.common.PlaybackException
import com.videoplayer.hdvideos.vrplayer1.Detail.utils.enterPlayerFullscreen
import androidx.media3.common.Player
import com.videoplayer.hdvideos.vrplayer1.Detail.utils.enterPlayerFullscreen
import androidx.media3.common.VideoSize
import com.videoplayer.hdvideos.vrplayer1.Detail.utils.enterPlayerFullscreen
import androidx.media3.common.util.UnstableApi
import com.videoplayer.hdvideos.vrplayer1.Detail.utils.enterPlayerFullscreen
import androidx.media3.exoplayer.DefaultRenderersFactory
import com.videoplayer.hdvideos.vrplayer1.Detail.utils.enterPlayerFullscreen
import androidx.media3.exoplayer.ExoPlayer
import com.videoplayer.hdvideos.vrplayer1.Detail.utils.enterPlayerFullscreen
import androidx.media3.exoplayer.mediacodec.MediaCodecSelector
import com.videoplayer.hdvideos.vrplayer1.Detail.utils.enterPlayerFullscreen
import androidx.media3.exoplayer.mediacodec.MediaCodecUtil
import com.videoplayer.hdvideos.vrplayer1.Detail.utils.enterPlayerFullscreen
import com.videoplayer.hdvideos.vrplayer1.Detail.utils.IntentKeys
import com.videoplayer.hdvideos.vrplayer1.Detail.utils.enterPlayerFullscreen
import com.videoplayer.hdvideos.vrplayer1.Detail.utils.InteractionMode
import com.videoplayer.hdvideos.vrplayer1.Detail.utils.enterPlayerFullscreen
import com.videoplayer.hdvideos.vrplayer1.R
import com.videoplayer.hdvideos.vrplayer1.Detail.utils.enterPlayerFullscreen
import com.videoplayer.hdvideos.vrplayer1.ads.PhoneLocatorLoadAds
import com.videoplayer.hdvideos.vrplayer1.Detail.utils.enterPlayerFullscreen
import com.videoplayer.hdvideos.vrplayer1.ads.PhoneLocatorShowAds
import com.videoplayer.hdvideos.vrplayer1.Detail.utils.enterPlayerFullscreen
import com.videoplayer.hdvideos.vrplayer1.ads.PhoneLocatorShowAds.logAnalyticsForClicks
import com.videoplayer.hdvideos.vrplayer1.Detail.utils.enterPlayerFullscreen
import com.videoplayer.hdvideos.vrplayer1.cinema.utiles.CinemaRenderer
import com.videoplayer.hdvideos.vrplayer1.Detail.utils.enterPlayerFullscreen
import com.videoplayer.hdvideos.vrplayer1.databinding.ActivityTheatrePlaybackBinding


@UnstableApi
class TheatrePlaybackActivity : AppCompatActivity() {

    private lateinit var binding: ActivityTheatrePlaybackBinding
    private val seekHandler = Handler(Looper.getMainLooper())
    private var isSeekBarTracking = false

    private var interaction: InteractionMode = InteractionMode.TOUCH
    private var videoUriStr: String? = null

    private var exoPlayer: ExoPlayer? = null
    private lateinit var renderer: CinemaRenderer
    private lateinit var gyro: TheatreGyroscopeController
    private lateinit var touch: TheatreTouchController

    private val TAG = "TheatrePlayback"

    // ── FIX 1 – Choreographer replaces the 16 ms fixed timer ─────────────────
    private val choreographer = Choreographer.getInstance()
    private val frameCallback: Choreographer.FrameCallback = Choreographer.FrameCallback {
        updateCameraAngles()
        choreographer.postFrameCallback(frameCallback)
    }


    // ── Controls auto-hide ────────────────────────────────────────────────────
    private val controlsHandler    = Handler(Looper.getMainLooper())
    private val hideControlsAction = Runnable {
        listOf(binding.overlayButtons, binding.playbackControls,
            binding.btnZoomIn, binding.btnZoomOut).forEach {
            it.animate().alpha(0f).setDuration(350).start()
        }
        controlsVisible = false
    }
    private var controlsVisible = true

    // ── Pinch-to-zoom ─────────────────────────────────────────────────────────
    private lateinit var scaleDetector: ScaleGestureDetector

    // ── Double-tap to reset zoom ──────────────────────────────────────────────
    private var lastTapTime = 0L

    // ── Haptics ───────────────────────────────────────────────────────────────
    private val vibrator: Vibrator by lazy {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            (getSystemService(VIBRATOR_MANAGER_SERVICE) as VibratorManager).defaultVibrator
        } else {
            @Suppress("DEPRECATION")
            getSystemService(VIBRATOR_SERVICE) as Vibrator
        }
    }

    // ── FIX 2 – Surface race: player init deferred until GL surface is ready ──
    // initPlayer() is called from the onSurfaceReady callback, not from onCreate
    private var playerInitialised = false

    // Hardware accelerated decoder first with software fallback
    private val hardwareFirstSelector =
        MediaCodecSelector { mimeType, requiresSecure, requiresTunneling ->
            val all = MediaCodecUtil.getDecoderInfos(mimeType, requiresSecure, requiresTunneling)
            val hw  = all.filter { !isSoftwareCodec(it.name) }
            val sw  = all.filter { isSoftwareCodec(it.name) }
            hw + sw
        }

    private fun isSoftwareCodec(name: String) =
        name.lowercase().let { it.startsWith("omx.google.") || it.startsWith("c2.android.") }

    // ─────────────────────────────────────────────────────────────────────────
    // Lifecycle
    // ─────────────────────────────────────────────────────────────────────────

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityTheatrePlaybackBinding.inflate(layoutInflater)
        setContentView(binding.root)

        ViewCompat.setOnApplyWindowInsetsListener(binding.main) { v, insets ->
            val bars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(bars.left, bars.top, bars.right, bars.bottom)
            insets
        }

        com.videoplayer.hdvideos.vrplayer1.ads.AnalyticsHelper.logScreen(this, "screen_theatre_player")


        window.statusBarColor = android.graphics.Color.BLACK


        enterPlayerFullscreen()
        WindowCompat.getInsetsController(window, window.decorView).apply {
            isAppearanceLightStatusBars = false
        }
        requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_SENSOR_LANDSCAPE

        parseIntent()
        setupControllers()
        setupGLSurface()          // renderer created here; initPlayer deferred
        setupOverlayButtons()
        setupPlaybackControls()
        setupPinchZoom()
        mStreetViewTruckBannerAdsSmall()

        // Zoom buttons (kept alongside pinch for accessibility)
        binding.btnZoomIn.setOnClickListener {
            binding.glSurface.queueEvent { renderer.zoomBy(5f) }
            showControls()
        }
        binding.btnZoomOut.setOnClickListener {
            binding.glSurface.queueEvent { renderer.zoomBy(-5f) }
            showControls()
        }

        // Start controls-hide countdown
        showControls()
    }

    override fun onResume() {
        super.onResume()
        binding.glSurface.onResume()
        if (interaction == InteractionMode.MOTION ||
            interaction == InteractionMode.BOTH) gyro.start()
        choreographer.postFrameCallback(frameCallback)   // FIX 1
        seekHandler.post(seekRunnable)
        exoPlayer?.play()
    }

    override fun onPause() {
        choreographer.removeFrameCallback(frameCallback) // FIX 1
        seekHandler.removeCallbacks(seekRunnable)
        controlsHandler.removeCallbacks(hideControlsAction)
        gyro.stop()
        exoPlayer?.pause()
        binding.glSurface.onPause()
        super.onPause()
    }

    override fun onStop() {
        // Do NOT release here. The SurfaceTexture is still alive and MediaCodec
        // may still be writing frames. Releasing here causes "BufferQueue abandoned"
        // errors. Release happens in onDestroy instead.
        super.onStop()
    }

    override fun onDestroy() {
        releasePlayer()
        super.onDestroy()
    }

    override fun onTouchEvent(event: MotionEvent): Boolean {
        // Pinch-to-zoom takes priority
        scaleDetector.onTouchEvent(event)

        if (event.action == MotionEvent.ACTION_UP) {
            // Double-tap resets zoom
            val now = System.currentTimeMillis()
            if (now - lastTapTime < 280 && !scaleDetector.isInProgress) {
                binding.glSurface.queueEvent { renderer.resetZoom() }
            }
            lastTapTime = now
        }

        // Show controls on any touch, restart the hide countdown
        showControls()

        // Pass rotation to touch controller only when not pinching
        if (!scaleDetector.isInProgress) {
            if (interaction == InteractionMode.TOUCH ||
                interaction == InteractionMode.BOTH) {
                if (touch.onTouchEvent(event)) return true
            }
        }
        return super.onTouchEvent(event)
    }

    // ─────────────────────────────────────────────────────────────────────────
    // Controls visibility
    // ─────────────────────────────────────────────────────────────────────────

    private fun showControls() {
        controlsHandler.removeCallbacks(hideControlsAction)
        controlsVisible = true
        listOf(binding.overlayButtons, binding.playbackControls,
            binding.btnZoomIn, binding.btnZoomOut).forEach {
            it.animate().cancel()
            it.animate().alpha(1f).setDuration(200).start()
        }
        controlsHandler.postDelayed(hideControlsAction, 3500)
    }

    // ─────────────────────────────────────────────────────────────────────────
    // Pinch-to-zoom
    // ─────────────────────────────────────────────────────────────────────────

    private fun setupPinchZoom() {
        scaleDetector = ScaleGestureDetector(this,
            object : ScaleGestureDetector.SimpleOnScaleGestureListener() {
                override fun onScale(detector: ScaleGestureDetector): Boolean {
                    val delta = (detector.scaleFactor - 1f) * 30f
                    binding.glSurface.queueEvent { renderer.zoomBy(delta) }
                    return true
                }
            })
    }

    // ─────────────────────────────────────────────────────────────────────────
    // Intent
    // ─────────────────────────────────────────────────────────────────────────

    private fun parseIntent() {
        videoUriStr = intent.getStringExtra(IntentKeys.VIDEO_URI)
        interaction = InteractionMode.valueOf(
            intent.getStringExtra(IntentKeys.INTERACTION) ?: InteractionMode.TOUCH.name
        )
        Log.d(TAG, "Video URI: $videoUriStr | Interaction: $interaction")
    }

    // ─────────────────────────────────────────────────────────────────────────
    // Controllers
    // ─────────────────────────────────────────────────────────────────────────

    private fun setupControllers() {
        gyro  = TheatreGyroscopeController(this)
        touch = TheatreTouchController()
    }

    // ─────────────────────────────────────────────────────────────────────────
    // GL Surface
    // ─────────────────────────────────────────────────────────────────────────

    private fun setupGLSurface() {
        binding.glSurface.setEGLContextClientVersion(2)

        renderer = CinemaRenderer(this) { surface ->
            // FIX 2 – this callback fires ON the GL thread once the surface is
            // truly ready; we hop to the main thread to init the player safely.
            runOnUiThread {
                Log.d(TAG, "GL surface ready → attaching player")
                if (!playerInitialised) {
                    playerInitialised = true
                    initPlayer(surface)   // pass Surface directly — no race
                } else {
                    exoPlayer?.setVideoSurface(surface)
                }
            }
        }

        binding.glSurface.setRenderer(renderer)
        binding.glSurface.renderMode = GLSurfaceView.RENDERMODE_CONTINUOUSLY
    }

    // ─────────────────────────────────────────────────────────────────────────
    // Overlay buttons
    // ─────────────────────────────────────────────────────────────────────────

    private fun setupOverlayButtons() {

//        onBackPressedDispatcher.addCallback(this, object : OnBackPressedCallback(true) {
//            override fun handleOnBackPressed() {
//                ShowAds.logAnalyticsForClicks("TheatrePlaybackActivity back to TheatreVideoPickerActivity", this@TheatrePlaybackActivity)
//
//                ShowAds.backPressInterstitial(
//                    this@TheatrePlaybackActivity,
//                    AppLoadAds.admobInterstitialAd
//                )
//            }
//        })
        onBackPressedDispatcher.addCallback(this, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                PhoneLocatorShowAds.mediationBackPressedSimpleHoneyBeeMapNavigation(
                    this@TheatrePlaybackActivity,
                    PhoneLocatorLoadAds.admobInterstitialAdHoneyBee
                )
            }
        })

        binding.btnBack.setOnClickListener {
            onBackPressedDispatcher.onBackPressed()
        }

        binding.btnRecentre.setOnClickListener {
            haptic()
            gyro.reset()
            touch.reset()
            Toast.makeText(this, "Re-centered", Toast.LENGTH_SHORT).show()
            showControls()
        }

        binding.btnResetView.setOnClickListener {
            haptic()
            gyro.reset()
            touch.reset()
            renderer.yaw   = 0f
            renderer.pitch = 0f
            Toast.makeText(this, "Facing screen", Toast.LENGTH_SHORT).show()
            showControls()
        }
    }

    // ─────────────────────────────────────────────────────────────────────────
    // ExoPlayer
    // ─────────────────────────────────────────────────────────────────────────

    // FIX 2 – accepts the Surface directly so it's set before prepare()
    private fun initPlayer(surface: Surface) {

        if (videoUriStr.isNullOrEmpty()) {
            Toast.makeText(this, "No video selected", Toast.LENGTH_SHORT).show()
            finish()
            return
        }

        try {
            val renderersFactory = DefaultRenderersFactory(this).apply {
                setExtensionRendererMode(DefaultRenderersFactory.EXTENSION_RENDERER_MODE_PREFER)
                setMediaCodecSelector(hardwareFirstSelector)
            }

            val player = ExoPlayer.Builder(this, renderersFactory)
                .build()
                .also { exoPlayer = it }

            player.setVideoSurface(surface)   // set BEFORE prepare – no race
            player.repeatMode = Player.REPEAT_MODE_OFF

            player.addListener(object : Player.Listener {

                override fun onPlaybackStateChanged(state: Int) {
                    Log.d(TAG, "State: ${state.toStateName()}")
                    runOnUiThread {
                        // Smooth fade instead of snap visibility change
                        val buffering = state == Player.STATE_BUFFERING
                        binding.loadingOverlay.animate()
                            .alpha(if (buffering) 1f else 0f)
                            .setDuration(150)
                            .withStartAction {
                                if (buffering) {
                                    binding.loadingOverlay.visibility = View.VISIBLE
                                }
                            }
                            .withEndAction {
                                if (!buffering) {
                                    binding.loadingOverlay.visibility = View.GONE
                                }
                            }.start()

                        if (state == Player.STATE_ENDED) {
                            binding.btnPlayPause.setImageResource(R.drawable.ic_play)
                        }
                    }
                }

                override fun onPlayerError(error: PlaybackException) {
                    Log.e(TAG, "Player error", error)
                    runOnUiThread {
                        Toast.makeText(this@TheatrePlaybackActivity,
                            "Playback error: ${error.message}", Toast.LENGTH_LONG).show()
                        finish()
                    }
                }

                override fun onVideoSizeChanged(videoSize: VideoSize) {
                    val w = videoSize.width
                    val h = videoSize.height
                    val r = videoSize.unappliedRotationDegrees
                    Log.d(TAG, "Video size: ${w}x$h | rotation=$r")
                    if (w > 0 && h > 0) {
                        binding.glSurface.queueEvent {
                            renderer.updateVideoAspect(w, h, r)
                        }
                    }
                }

                override fun onRenderedFirstFrame() {
                    Log.d(TAG, "First frame rendered")
                }
            })

            player.setMediaItem(MediaItem.fromUri(videoUriStr!!.toUri()))
            player.prepare()
            player.play()

        } catch (e: Exception) {
            Log.e(TAG, "Init error", e)
            Toast.makeText(this, "Failed to play video: ${e.message}", Toast.LENGTH_LONG).show()
            finish()
        }
    }

    private fun releasePlayer() {
        try {
            // Release ExoPlayer FIRST so MediaCodec stops writing to the SurfaceTexture.
            // Only after that is it safe to destroy the GL/SurfaceTexture resources.
            // Reversing this order causes "BufferQueue has been abandoned" (-19) errors.
            exoPlayer?.release()
            exoPlayer = null
            playerInitialised = false
            binding.glSurface.queueEvent { renderer.release() }
        } catch (e: Exception) {
            Log.e(TAG, "Release error", e)
        }
    }

    // ─────────────────────────────────────────────────────────────────────────
    // Camera updates (called from Choreographer — synced to vsync)
    // ─────────────────────────────────────────────────────────────────────────

    private fun updateCameraAngles() {
        val (yaw, pitch) = when (interaction) {
            InteractionMode.MOTION -> gyro.yaw   to gyro.pitch
            InteractionMode.TOUCH  -> touch.yaw  to touch.pitch
            InteractionMode.BOTH   -> (gyro.yaw + touch.yaw) to (gyro.pitch + touch.pitch)
        }
        renderer.yaw   = yaw
        renderer.pitch = pitch
    }

    // ─────────────────────────────────────────────────────────────────────────
    // Playback controls
    // ─────────────────────────────────────────────────────────────────────────

    private fun setupPlaybackControls() {
        binding.btnReplay10.setOnClickListener {
            val player = exoPlayer ?: return@setOnClickListener
            haptic()
            val newPos = maxOf(0L, player.currentPosition - 10_000L)
            player.seekTo(newPos)
            showControls()
        }

        binding.btnPlayPause.setOnClickListener {
            val player = exoPlayer ?: return@setOnClickListener
            haptic()
            if (player.isPlaying) {
                player.pause()
                binding.btnPlayPause.setImageResource(R.drawable.ic_play)
            } else {
                player.play()
                binding.btnPlayPause.setImageResource(R.drawable.ic_pause)
            }
            showControls()
        }

        binding.seekBar.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(sb: SeekBar, progress: Int, fromUser: Boolean) {
                if (fromUser) binding.tvCurrentTime.text = formatTime(progress.toLong())
            }
            override fun onStartTrackingTouch(sb: SeekBar) {
                isSeekBarTracking = true
                showControls()
            }
            override fun onStopTrackingTouch(sb: SeekBar) {
                exoPlayer?.seekTo(sb.progress.toLong())
                isSeekBarTracking = false
            }
        })
    }

    private val seekRunnable = object : Runnable {
        override fun run() {
            updateSeekBar()
            seekHandler.postDelayed(this, 500)
        }
    }

    private fun updateSeekBar() {
        val player   = exoPlayer ?: return
        val duration = player.duration.takeIf { it > 0 } ?: return
        val position = player.currentPosition
        binding.seekBar.max = duration.toInt()
        if (!isSeekBarTracking) binding.seekBar.progress = position.toInt()
        binding.tvCurrentTime.text = formatTime(position)
        binding.tvTotalTime.text   = formatTime(duration)
    }

    private fun formatTime(ms: Long): String {
        val s   = ms / 1000
        val min = s / 60
        val sec = s % 60
        return "%d:%02d".format(min, sec)
    }

    // ─────────────────────────────────────────────────────────────────────────
    // Haptics
    // ─────────────────────────────────────────────────────────────────────────

    @RequiresPermission(Manifest.permission.VIBRATE)
    private fun haptic() {
        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                vibrator.vibrate(VibrationEffect.createPredefined(VibrationEffect.EFFECT_CLICK))
            } else {
                @Suppress("DEPRECATION")
                vibrator.vibrate(25)
            }
        } catch (_: Exception) { /* vibrator not available on all devices */ }
    }

    // ─────────────────────────────────────────────────────────────────────────
    // Ads
    // ─────────────────────────────────────────────────────────────────────────

    private fun mStreetViewTruckBannerAdsSmall() {
//        StreetViewTruckCTLoadAds.loadStreetViewTruckCTBannerAdMobPlayer(
//            binding.smallAd.adContainer,
//            binding.smallAd.root,
//            binding.smallAd.shimmerLayout,
//            this@TheatrePlaybackActivity
//        )
    }

    // ─────────────────────────────────────────────────────────────────────────
    // Helpers
    // ─────────────────────────────────────────────────────────────────────────

    private fun Int.toStateName() = when (this) {
        Player.STATE_IDLE      -> "IDLE"
        Player.STATE_BUFFERING -> "BUFFERING"
        Player.STATE_READY     -> "READY"
        Player.STATE_ENDED     -> "ENDED"
        else                   -> "UNKNOWN"
    }
}