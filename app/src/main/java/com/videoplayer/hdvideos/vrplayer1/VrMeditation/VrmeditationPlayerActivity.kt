package com.videoplayer.hdvideos.vrplayer1.VrMeditation

import com.videoplayer.hdvideos.vrplayer1.Detail.utils.enterPlayerFullscreen
import android.annotation.SuppressLint
import com.videoplayer.hdvideos.vrplayer1.Detail.utils.enterPlayerFullscreen
import android.content.Context
import com.videoplayer.hdvideos.vrplayer1.Detail.utils.enterPlayerFullscreen
import android.media.AudioManager
import com.videoplayer.hdvideos.vrplayer1.Detail.utils.enterPlayerFullscreen
import android.net.Uri
import com.videoplayer.hdvideos.vrplayer1.Detail.utils.enterPlayerFullscreen
import android.opengl.GLSurfaceView
import com.videoplayer.hdvideos.vrplayer1.Detail.utils.enterPlayerFullscreen
import android.os.Bundle
import com.videoplayer.hdvideos.vrplayer1.Detail.utils.enterPlayerFullscreen
import android.os.Handler
import com.videoplayer.hdvideos.vrplayer1.Detail.utils.enterPlayerFullscreen
import android.os.Looper
import com.videoplayer.hdvideos.vrplayer1.Detail.utils.enterPlayerFullscreen
import android.util.Log
import com.videoplayer.hdvideos.vrplayer1.Detail.utils.enterPlayerFullscreen
import android.view.MotionEvent
import com.videoplayer.hdvideos.vrplayer1.Detail.utils.enterPlayerFullscreen
import android.view.Surface
import com.videoplayer.hdvideos.vrplayer1.Detail.utils.enterPlayerFullscreen
import android.view.View
import com.videoplayer.hdvideos.vrplayer1.Detail.utils.enterPlayerFullscreen
import android.view.WindowManager
import com.videoplayer.hdvideos.vrplayer1.Detail.utils.enterPlayerFullscreen
import android.widget.SeekBar
import com.videoplayer.hdvideos.vrplayer1.Detail.utils.enterPlayerFullscreen
import android.widget.Toast
import com.videoplayer.hdvideos.vrplayer1.Detail.utils.enterPlayerFullscreen
import androidx.activity.OnBackPressedCallback
import com.videoplayer.hdvideos.vrplayer1.Detail.utils.enterPlayerFullscreen
import androidx.activity.enableEdgeToEdge
import com.videoplayer.hdvideos.vrplayer1.Detail.utils.enterPlayerFullscreen
import androidx.annotation.OptIn
import com.videoplayer.hdvideos.vrplayer1.Detail.utils.enterPlayerFullscreen
import androidx.appcompat.app.AppCompatActivity
import com.videoplayer.hdvideos.vrplayer1.Detail.utils.enterPlayerFullscreen
import androidx.core.view.ViewCompat
import com.videoplayer.hdvideos.vrplayer1.Detail.utils.enterPlayerFullscreen
import androidx.core.view.WindowCompat
import com.videoplayer.hdvideos.vrplayer1.Detail.utils.enterPlayerFullscreen
import androidx.core.view.WindowInsetsCompat
import com.videoplayer.hdvideos.vrplayer1.Detail.utils.enterPlayerFullscreen
import androidx.lifecycle.lifecycleScope
import com.videoplayer.hdvideos.vrplayer1.Detail.utils.enterPlayerFullscreen
import androidx.media3.common.C
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
import com.videoplayer.hdvideos.vrplayer1.Detail.utils.GyroscopeController
import com.videoplayer.hdvideos.vrplayer1.Detail.utils.enterPlayerFullscreen
import com.videoplayer.hdvideos.vrplayer1.Detail.utils.IntentKeys
import com.videoplayer.hdvideos.vrplayer1.Detail.utils.enterPlayerFullscreen
import com.videoplayer.hdvideos.vrplayer1.Detail.utils.InteractionMode
import com.videoplayer.hdvideos.vrplayer1.Detail.utils.enterPlayerFullscreen
import com.videoplayer.hdvideos.vrplayer1.Detail.utils.TouchController
import com.videoplayer.hdvideos.vrplayer1.Detail.utils.enterPlayerFullscreen
import com.videoplayer.hdvideos.vrplayer1.R
import com.videoplayer.hdvideos.vrplayer1.Detail.utils.enterPlayerFullscreen
import com.videoplayer.hdvideos.vrplayer1.ads.PhoneLocatorLoadAds
import com.videoplayer.hdvideos.vrplayer1.Detail.utils.enterPlayerFullscreen
import com.videoplayer.hdvideos.vrplayer1.ads.PhoneLocatorShowAds
import com.videoplayer.hdvideos.vrplayer1.Detail.utils.enterPlayerFullscreen
import com.videoplayer.hdvideos.vrplayer1.ads.PhoneLocatorShowAds.logAnalyticsForClicks
import com.videoplayer.hdvideos.vrplayer1.Detail.utils.enterPlayerFullscreen
import com.videoplayer.hdvideos.vrplayer1.databinding.ActivityVrmeditationPlayerBinding
import com.videoplayer.hdvideos.vrplayer1.Detail.utils.enterPlayerFullscreen
import com.videoplayer.hdvideos.vrplayer1.pickGallerydata.utiles.GalleryViewMode
import com.videoplayer.hdvideos.vrplayer1.Detail.utils.enterPlayerFullscreen
import com.videoplayer.hdvideos.vrplayer1.pickGallerydata.utiles.SphereRendererforgallery
import com.videoplayer.hdvideos.vrplayer1.Detail.utils.enterPlayerFullscreen
import kotlinx.coroutines.Dispatchers
import com.videoplayer.hdvideos.vrplayer1.Detail.utils.enterPlayerFullscreen
import kotlinx.coroutines.launch
import com.videoplayer.hdvideos.vrplayer1.Detail.utils.enterPlayerFullscreen
import kotlinx.coroutines.withContext
import com.videoplayer.hdvideos.vrplayer1.Detail.utils.enterPlayerFullscreen
import kotlin.collections.ifEmpty

//class VrmeditationPlayerActivity : AppCompatActivity() {
//    override fun onCreate(savedInstanceState: Bundle?) {
//        super.onCreate(savedInstanceState)
//        enableEdgeToEdge()
//        setContentView(R.layout.activity_vrmeditation_player)
//        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
//            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
//            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
//            insets
//        }
//    }
//}



@UnstableApi
class VrmeditationPlayerActivity : AppCompatActivity() {

    private lateinit var binding: ActivityVrmeditationPlayerBinding

    private lateinit var viewMode: GalleryViewMode
    private lateinit var interaction: InteractionMode
    private var repeat       = false
    private var exitOnFinish = false
    private var videoUriStr: String? = null

    private var exoPlayer: ExoPlayer? = null
    private var networkRetries = 0
    private lateinit var renderer: SphereRendererforgallery
    private var surfaceReady    = false
    private var pendingSurface: Surface? = null

    private lateinit var gyro: GyroscopeController
    private lateinit var touch: TouchController
    private lateinit var audioManager: AudioManager

    private val frameHandler    = Handler(Looper.getMainLooper())
    private val uiHandler       = Handler(Looper.getMainLooper())
    private val progressHandler = Handler(Looper.getMainLooper())

    private val TAG = "VrmeditationPlayer"

    private var controlsVisible = true
    private var isDraggingSeek  = false
    private var isDestroyed     = false

    companion object {
        const val EXTRA_URI   = "meditation_uri"
        const val EXTRA_TITLE = "meditation_title"
    }

    private val hideControlsRunnable = Runnable { hideControls() }

    private val progressRunnable = object : Runnable {
        override fun run() {
            if (!isDestroyed) {
                updateProgress()
                progressHandler.postDelayed(this, 500)
            }
        }
    }

    private val frameRunnable = object : Runnable {
        override fun run() {
            if (!isDestroyed) {
                touch.update()
                updateCameraAngles()
                frameHandler.postDelayed(this, 16)
            }
        }
    }

    private val softwareFirstSelector =
        MediaCodecSelector { mimeType, requiresSecureDecoder, requiresTunnelingDecoder ->
            val all = MediaCodecUtil.getDecoderInfos(mimeType, requiresSecureDecoder, requiresTunnelingDecoder)
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.Q) {
                all
            } else {
                val sw  = all.filter { isSw(it.name) }
                val hw  = all.filter { !isSw(it.name) }
                sw + hw
            }
        }

    private fun isSw(name: String) =
        name.lowercase().let { it.startsWith("omx.google.") || it.startsWith("c2.android.") }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        window.addFlags(
            WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON or
                    WindowManager.LayoutParams.FLAG_HARDWARE_ACCELERATED
        )

        enableEdgeToEdge()
        binding = ActivityVrmeditationPlayerBinding.inflate(layoutInflater)
        setContentView(binding.root)
        com.videoplayer.hdvideos.vrplayer1.ads.AnalyticsHelper.logScreen(this, "screen_vr_meditation_player")

        ViewCompat.setOnApplyWindowInsetsListener(binding.root) { v, insets ->
            val sys = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(sys.left, sys.top, sys.right, sys.bottom)
            insets
        }

        window.statusBarColor = android.graphics.Color.BLACK

        enterPlayerFullscreen()
        WindowCompat.getInsetsController(window, window.decorView).apply {
            isAppearanceLightStatusBars = false
        }
        logAnalyticsForClicks("VrmeditationPlayerActivity started", this)

        audioManager = getSystemService(Context.AUDIO_SERVICE) as AudioManager

        parseIntent()
        setupControllers()
        setupGLSurface()
        setupOverlayButtons()
        setupSeekBar()
        scheduleHideControls()

        onBackPressedDispatcher.addCallback(this, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                PhoneLocatorShowAds.mediationBackPressedSimpleHoneyBeeMapNavigation(
                    this@VrmeditationPlayerActivity,
                    PhoneLocatorLoadAds.admobInterstitialAdHoneyBee
                )
            }
        })

        binding.btnBack.setOnClickListener { onBackPressedDispatcher.onBackPressed() }
        binding.tvVideoTitle.text = intent.getStringExtra(EXTRA_TITLE) ?: ""

        binding.btnRewind.setOnClickListener {
            exoPlayer?.seekTo((exoPlayer!!.currentPosition - 10_000L).coerceAtLeast(0L))
            scheduleHideControls()
        }
        binding.btnForward.setOnClickListener {
            val dur = exoPlayer?.duration ?: 0L
            exoPlayer?.seekTo((exoPlayer!!.currentPosition + 10_000L).coerceAtMost(dur))
            scheduleHideControls()
        }

        lifecycleScope.launch {
            setupForVideo()
        }
    }

    override fun onResume() {
        super.onResume()
        binding.glSurface.onResume()
        frameHandler.post(frameRunnable)
        progressHandler.post(progressRunnable)
        exoPlayer?.play()
        if (interaction == InteractionMode.MOTION || interaction == InteractionMode.BOTH) {
            gyro.start()
        }
    }

    override fun onPause() {
        frameHandler.removeCallbacks(frameRunnable)
        progressHandler.removeCallbacks(progressRunnable)
        uiHandler.removeCallbacks(hideControlsRunnable)
        gyro.stop()
        exoPlayer?.pause()
        binding.glSurface.onPause()
        super.onPause()
    }

    override fun onDestroy() {
        isDestroyed = true
        frameHandler.removeCallbacksAndMessages(null)
        progressHandler.removeCallbacksAndMessages(null)
        uiHandler.removeCallbacksAndMessages(null)
        lifecycleScope.launch(Dispatchers.Main) {
            releasePlayer()
        }
        super.onDestroy()
    }

    override fun onTouchEvent(event: MotionEvent): Boolean {
        if (event.action == MotionEvent.ACTION_DOWN) toggleControls()
        if (interaction == InteractionMode.TOUCH || interaction == InteractionMode.BOTH) {
            if (touch.onTouchEvent(event)) return true
        }
        return super.onTouchEvent(event)
    }

    private fun parseIntent() {
        viewMode = GalleryViewMode.valueOf(
            intent.getStringExtra(IntentKeys.VIEW_MODE) ?: GalleryViewMode.DEVICE.name
        )
        interaction = InteractionMode.valueOf(
            intent.getStringExtra(IntentKeys.INTERACTION) ?: InteractionMode.TOUCH.name
        )
        repeat       = intent.getBooleanExtra(IntentKeys.REPEAT, false)
        exitOnFinish = intent.getBooleanExtra(IntentKeys.EXIT_ON_FINISH, false)
        videoUriStr  = intent.getStringExtra(EXTRA_URI)
    }

    private fun setupControllers() {
        gyro  = GyroscopeController(this)
        touch = TouchController()
    }

    private fun setupGLSurface() {
        binding.glSurface.setEGLContextClientVersion(2)

        renderer = SphereRendererforgallery(viewMode) { surface ->
            runOnUiThread {
                surfaceReady   = true
                pendingSurface = surface
                attachSurfaceIfReady()
            }
        }

        binding.glSurface.setRenderer(renderer)
        binding.glSurface.renderMode = GLSurfaceView.RENDERMODE_CONTINUOUSLY
    }

    private fun setupOverlayButtons() {
        binding.btnPlayPause.setOnClickListener { togglePlayPause() }
    }

    private fun setupSeekBar() {
        binding.seekBar.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(sb: SeekBar, progress: Int, fromUser: Boolean) {
                if (fromUser) {
                    val pos = (exoPlayer?.duration ?: 0L) * progress / 1000L
                    binding.tvCurrentTime.text = formatTime(pos)
                }
            }
            override fun onStartTrackingTouch(sb: SeekBar) { isDraggingSeek = true }
            override fun onStopTrackingTouch(sb: SeekBar) {
                isDraggingSeek = false
                val pos = (exoPlayer?.duration ?: 0L) * sb.progress / 1000L
                exoPlayer?.seekTo(pos)
                scheduleHideControls()
            }
        })
    }

    @OptIn(androidx.media3.common.util.UnstableApi::class)

    private suspend fun setupForVideo() {
        val uriStr = videoUriStr
        if (uriStr.isNullOrEmpty()) {
            Toast.makeText(this, "No video URI provided", Toast.LENGTH_LONG).show()
            finish(); return
        }

        // parse URI off main thread
        val uri = withContext(Dispatchers.IO) {
            runCatching { Uri.parse(uriStr) }.getOrNull()
        }

        if (uri == null) {
            Toast.makeText(this, "Invalid video URI", Toast.LENGTH_LONG).show()
            finish(); return
        }

        // build player on main thread
        withContext(Dispatchers.Main) {
            try {
                val renderersFactory = DefaultRenderersFactory(this@VrmeditationPlayerActivity).apply {
                    setExtensionRendererMode(DefaultRenderersFactory.EXTENSION_RENDERER_MODE_PREFER)
                    setMediaCodecSelector(softwareFirstSelector)
                    setEnableDecoderFallback(true)
                }

                val httpDataSourceFactory =
                    androidx.media3.datasource.DefaultHttpDataSource.Factory()
                        .setUserAgent(
                            "Mozilla/5.0 (Linux; Android 10; Mobile) AppleWebKit/537.36 " +
                                    "(KHTML, like Gecko) Chrome/91.0.4472.120 Mobile Safari/537.36"
                        )
                        .setAllowCrossProtocolRedirects(true)
                        .setConnectTimeoutMs(15000)
                        .setReadTimeoutMs(15000)

                val dataSourceFactory =
                    androidx.media3.datasource.DefaultDataSource.Factory(
                        this@VrmeditationPlayerActivity, httpDataSourceFactory
                    )

                val mediaSourceFactory =
                    androidx.media3.exoplayer.source.ProgressiveMediaSource
                        .Factory(dataSourceFactory)

                val player = ExoPlayer.Builder(this@VrmeditationPlayerActivity, renderersFactory)
                    .setMediaSourceFactory(mediaSourceFactory)
                    .setLoadControl(
                        androidx.media3.exoplayer.DefaultLoadControl.Builder()
                            .setBufferDurationsMs(20_000, 60_000, 2_500, 5_000)
                            .build()
                    )
                    .build()
                    .also { exoPlayer = it }

                player.repeatMode    = if (repeat) Player.REPEAT_MODE_ONE else Player.REPEAT_MODE_OFF
                player.playWhenReady = true

                player.addListener(object : Player.Listener {
                    override fun onPlaybackStateChanged(state: Int) {
                        if (isDestroyed) return
                        runOnUiThread {
                            binding.loadingOverlay.visibility =
                                if (state == Player.STATE_BUFFERING) View.VISIBLE else View.GONE
                        }
                        when (state) {
                            Player.STATE_READY -> {
                                if (isDestroyed) return
                                binding.tvTotalTime.text = formatTime(player.duration)
                                binding.seekBar.max = 1000
                                updatePlayPauseIcon()
                                runOnUiThread { gyro.reset(); touch.reset() }
                            }
                            Player.STATE_ENDED -> {
                                if (exitOnFinish) finish()
                                else { player.seekTo(0); player.play() }
                            }
                            else -> {}
                        }
                    }

                    override fun onIsPlayingChanged(isPlaying: Boolean) {
                        if (!isDestroyed) updatePlayPauseIcon()
                    }

                    override fun onPlayerError(error: PlaybackException) {
                        if (isDestroyed) return
                        Log.e(TAG, "Player error [${error.errorCode}]", error)
                        val code = error.errorCode
                        if ((code == PlaybackException.ERROR_CODE_IO_NETWORK_CONNECTION_FAILED ||
                                    code == PlaybackException.ERROR_CODE_IO_NETWORK_CONNECTION_TIMEOUT ||
                                    code == PlaybackException.ERROR_CODE_IO_UNSPECIFIED ||
                                    code == PlaybackException.ERROR_CODE_TIMEOUT) && networkRetries < 3
                        ) {
                            networkRetries++
                            player.prepare()
                            return
                        }
                        if (code == PlaybackException.ERROR_CODE_DECODER_INIT_FAILED ||
                            code == PlaybackException.ERROR_CODE_DECODING_FAILED ||
                            code == PlaybackException.ERROR_CODE_DECODING_FORMAT_EXCEEDS_CAPABILITIES ||
                            code == PlaybackException.ERROR_CODE_DECODING_FORMAT_UNSUPPORTED
                        ) {
                            lifecycleScope.launch { retryWithSoftwareDecoder(uri) }
                            return
                        }
                        val msg = when (error.errorCode) {
                            PlaybackException.ERROR_CODE_IO_FILE_NOT_FOUND ->
                                "File not found."
                            PlaybackException.ERROR_CODE_IO_NO_PERMISSION ->
                                "Permission denied."
                            PlaybackException.ERROR_CODE_IO_BAD_HTTP_STATUS ->
                                "Video unavailable. Please try again later."
                            else -> "This video can't be played right now. Please try another one."
                        }
                        runOnUiThread {
                            Toast.makeText(this@VrmeditationPlayerActivity, msg, Toast.LENGTH_LONG).show()
                            finish()
                        }
                    }

                    override fun onVideoSizeChanged(videoSize: VideoSize) {
                        Log.d(TAG, "Video size: ${videoSize.width}×${videoSize.height}")
                    }

                    override fun onRenderedFirstFrame() {
                        Log.d(TAG, "First frame rendered")
                        if (!isDestroyed) runOnUiThread { gyro.reset(); touch.reset() }
                    }
                })

                player.setMediaItem(MediaItem.Builder().setUri(uri).build())
                player.prepare()
                attachSurfaceIfReady()

            } catch (e: Exception) {
                if (!isDestroyed) {
                    Log.e(TAG, "setupForVideo failed", e)
                    Toast.makeText(this@VrmeditationPlayerActivity, "Failed to init player: ${e.message}", Toast.LENGTH_LONG).show()
                    finish()
                }
            }
        }
    }

    @OptIn(UnstableApi::class)
    private suspend fun retryWithSoftwareDecoder(uri: Uri) {
        Log.w(TAG, "Retrying with SW-only decoder")

        withContext(Dispatchers.Main) { releasePlayer() }

        withContext(Dispatchers.Main) {
            try {
                val swSelector = MediaCodecSelector { mimeType, requiresSecure, requiresTunneling ->
                    MediaCodecUtil.getDecoderInfos(mimeType, requiresSecure, requiresTunneling)
                        .filter { isSw(it.name) }
                        .ifEmpty {
                            MediaCodecUtil.getDecoderInfos(mimeType, requiresSecure, requiresTunneling)
                        }
                }

                val rf = DefaultRenderersFactory(this@VrmeditationPlayerActivity).apply {
                    setExtensionRendererMode(DefaultRenderersFactory.EXTENSION_RENDERER_MODE_PREFER)
                    setMediaCodecSelector(swSelector)
                }

                val httpDataSourceFactory =
                    androidx.media3.datasource.DefaultHttpDataSource.Factory()
                        .setUserAgent(
                            "Mozilla/5.0 (Linux; Android 10; Mobile) AppleWebKit/537.36 " +
                                    "(KHTML, like Gecko) Chrome/91.0.4472.120 Mobile Safari/537.36"
                        )
                        .setAllowCrossProtocolRedirects(true)
                        .setConnectTimeoutMs(15000)
                        .setReadTimeoutMs(15000)

                val dataSourceFactory =
                    androidx.media3.datasource.DefaultDataSource.Factory(
                        this@VrmeditationPlayerActivity, httpDataSourceFactory
                    )

                val mediaSourceFactory =
                    androidx.media3.exoplayer.source.ProgressiveMediaSource
                        .Factory(dataSourceFactory)

                val player = ExoPlayer.Builder(this@VrmeditationPlayerActivity, rf)
                    .setMediaSourceFactory(mediaSourceFactory)
                    .build()
                    .also { exoPlayer = it }

                player.repeatMode    = if (repeat) Player.REPEAT_MODE_ONE else Player.REPEAT_MODE_OFF
                player.playWhenReady = true

                player.addListener(object : Player.Listener {
                    override fun onPlaybackStateChanged(state: Int) {
                        if (isDestroyed) return
                        when (state) {
                            Player.STATE_READY -> {
                                binding.tvTotalTime.text = formatTime(player.duration)
                                binding.seekBar.max = 1000
                                updatePlayPauseIcon()
                                runOnUiThread { gyro.reset(); touch.reset() }
                            }
                            Player.STATE_ENDED -> if (exitOnFinish) finish()
                            else -> {}
                        }
                    }
                    override fun onIsPlayingChanged(isPlaying: Boolean) {
                        if (!isDestroyed) updatePlayPauseIcon()
                    }
                    override fun onPlayerError(error: PlaybackException) {
                        if (isDestroyed) return
                        Log.e(TAG, "SW fallback failed [${error.errorCode}]", error)
                        runOnUiThread {
                            Toast.makeText(
                                this@VrmeditationPlayerActivity,
                                "Cannot play this video on your device.",
                                Toast.LENGTH_LONG
                            ).show()
                            finish()
                        }
                    }
                })

                player.setMediaItem(MediaItem.Builder().setUri(uri).build())
                player.prepare()
                attachSurfaceIfReady()
                player.play()

            } catch (e: Exception) {
                if (!isDestroyed) {
                    Log.e(TAG, "SW retry failed", e)
                    Toast.makeText(this@VrmeditationPlayerActivity, "Playback not supported: ${e.message}", Toast.LENGTH_LONG).show()
                    finish()
                }
            }
        }
    }

    private fun attachSurfaceIfReady() {
        val player  = exoPlayer      ?: return
        val surface = pendingSurface ?: return
        if (!surfaceReady) return
        try {
            player.setVideoSurface(surface)
            Log.d(TAG, "Surface attached")
        } catch (e: Exception) {
            Log.e(TAG, "Failed to attach surface", e)
        }
    }

    private fun toggleControls() {
        if (controlsVisible) hideControls() else showControls()
    }

    private fun showControls() {
        controlsVisible = true
        binding.controlsOverlay.visibility = View.VISIBLE
        binding.controlsOverlay.animate().alpha(1f).setDuration(200).start()
        scheduleHideControls()
    }

    private fun hideControls() {
        controlsVisible = false
        binding.controlsOverlay.animate().alpha(0f).setDuration(300).withEndAction {
            if (!isDestroyed) binding.controlsOverlay.visibility = View.GONE
        }.start()
    }

    private fun scheduleHideControls() {
        uiHandler.removeCallbacks(hideControlsRunnable)
        uiHandler.postDelayed(hideControlsRunnable, 3000)
    }

    private fun togglePlayPause() {
        exoPlayer?.let { if (it.isPlaying) it.pause() else it.play() }
        scheduleHideControls()
    }

    private fun updatePlayPauseIcon() {
        if (isDestroyed) return
        runOnUiThread {
            binding.btnPlayPause.setImageResource(
                if (exoPlayer?.isPlaying == true) R.drawable.pause else R.drawable.ic_play
            )
        }
    }

    private fun updateProgress() {
        if (isDestroyed) return
        val player   = exoPlayer ?: return
        val duration = player.duration
        val position = player.currentPosition
        if (duration > 0 && !isDraggingSeek) {
            binding.seekBar.progress = (1000L * position / duration).toInt()
        }
        binding.tvCurrentTime.text = formatTime(position)
        if (duration > 0) binding.tvTotalTime.text = formatTime(duration)
    }

    private fun updateCameraAngles() {
        val (resolvedYaw, resolvedPitch) = when (interaction) {
            InteractionMode.MOTION -> gyro.yaw   to gyro.pitch
            InteractionMode.TOUCH  -> touch.yaw  to touch.pitch
            InteractionMode.BOTH   -> (gyro.yaw + touch.yaw) to (gyro.pitch + touch.pitch)
        }
        renderer.yaw   = resolvedYaw
        renderer.pitch = resolvedPitch
    }

    private fun releasePlayer() {
        try { exoPlayer?.release(); exoPlayer = null } catch (_: Exception) {}
    }

    @SuppressLint("DefaultLocale")
    private fun formatTime(timeMs: Long): String {
        if (timeMs == C.TIME_UNSET || timeMs < 0) return "00:00"
        val s = (timeMs / 1_000) % 60
        val m = (timeMs / 60_000) % 60
        val h = timeMs / 3_600_000
        return if (h > 0) String.format("%d:%02d:%02d", h, m, s)
        else String.format("%02d:%02d", m, s)
    }
}