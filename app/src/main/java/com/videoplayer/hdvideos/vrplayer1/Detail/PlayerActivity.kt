package com.videoplayer.hdvideos.vrplayer1.Detail

import com.videoplayer.hdvideos.vrplayer1.Detail.utils.enterPlayerFullscreen
import android.annotation.SuppressLint
import com.videoplayer.hdvideos.vrplayer1.Detail.utils.enterPlayerFullscreen
import android.content.Context
import com.videoplayer.hdvideos.vrplayer1.Detail.utils.enterPlayerFullscreen
import android.content.Intent
import com.videoplayer.hdvideos.vrplayer1.Detail.utils.enterPlayerFullscreen
import android.media.AudioManager
import com.videoplayer.hdvideos.vrplayer1.Detail.utils.enterPlayerFullscreen
import android.net.Uri
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
import android.provider.Settings
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
import androidx.media3.datasource.DefaultDataSource
import com.videoplayer.hdvideos.vrplayer1.Detail.utils.enterPlayerFullscreen
import androidx.media3.datasource.okhttp.OkHttpDataSource
import com.videoplayer.hdvideos.vrplayer1.Detail.utils.enterPlayerFullscreen
import androidx.media3.exoplayer.DefaultRenderersFactory
import com.videoplayer.hdvideos.vrplayer1.Detail.utils.enterPlayerFullscreen
import androidx.media3.exoplayer.ExoPlayer
import com.videoplayer.hdvideos.vrplayer1.Detail.utils.enterPlayerFullscreen
import androidx.media3.exoplayer.mediacodec.MediaCodecSelector
import com.videoplayer.hdvideos.vrplayer1.Detail.utils.enterPlayerFullscreen
import androidx.media3.exoplayer.source.DefaultMediaSourceFactory
import com.videoplayer.hdvideos.vrplayer1.Detail.utils.enterPlayerFullscreen
import com.videoplayer.hdvideos.vrplayer1.Detail.utils.GyroscopeController
import com.videoplayer.hdvideos.vrplayer1.Detail.utils.enterPlayerFullscreen
import com.videoplayer.hdvideos.vrplayer1.Detail.utils.IntentKeys
import com.videoplayer.hdvideos.vrplayer1.Detail.utils.enterPlayerFullscreen
import com.videoplayer.hdvideos.vrplayer1.Detail.utils.InteractionMode
import com.videoplayer.hdvideos.vrplayer1.Detail.utils.enterPlayerFullscreen
import com.videoplayer.hdvideos.vrplayer1.Detail.utils.SphereRenderer
import com.videoplayer.hdvideos.vrplayer1.Detail.utils.enterPlayerFullscreen
import com.videoplayer.hdvideos.vrplayer1.Detail.utils.TouchController
import com.videoplayer.hdvideos.vrplayer1.Detail.utils.enterPlayerFullscreen
import com.videoplayer.hdvideos.vrplayer1.Detail.utils.ViewMode
import com.videoplayer.hdvideos.vrplayer1.Detail.utils.enterPlayerFullscreen
import com.videoplayer.hdvideos.vrplayer1.R
import com.videoplayer.hdvideos.vrplayer1.Detail.utils.enterPlayerFullscreen
import com.videoplayer.hdvideos.vrplayer1.databinding.ActivityPlayerBinding
import com.videoplayer.hdvideos.vrplayer1.Detail.utils.enterPlayerFullscreen
import com.videoplayer.hdvideos.vrplayer1.pickGallerydata.utiles.BaseActivity
import com.videoplayer.hdvideos.vrplayer1.Detail.utils.enterPlayerFullscreen
import com.videoplayer.hdvideos.vrplayer1.utils.UrlUtils
import com.videoplayer.hdvideos.vrplayer1.Detail.utils.enterPlayerFullscreen
import okhttp3.OkHttpClient
import com.videoplayer.hdvideos.vrplayer1.Detail.utils.enterPlayerFullscreen
import androidx.media3.exoplayer.mediacodec.MediaCodecUtil as ExoMediaCodecUtil


@UnstableApi
class PlayerActivity  : BaseActivity() {

    private lateinit var binding: ActivityPlayerBinding
    private lateinit var viewMode: ViewMode
    private lateinit var interaction: InteractionMode
    private var repeat = false
    private var exitOnFinish = false
    private var videoUriStr: String? = null

    private var exoPlayer: ExoPlayer? = null
    private lateinit var renderer: SphereRenderer
    private var surfaceReady = false
    private var pendingSurface: Surface? = null   // ← holds surface until player is ready

    private lateinit var gyro: GyroscopeController
    private lateinit var touch: TouchController
    private lateinit var audioManager: AudioManager

    private val frameHandler = Handler(Looper.getMainLooper())
    private val uiHandler = Handler(Looper.getMainLooper())
    private val progressHandler = Handler(Looper.getMainLooper())

    private val TAG = "VRPlayer"

    private var controlsVisible = true
    private var isDraggingSeek = false
    private var networkRetries = 0
    private var triedSoftwareDecoder = false

    // ── Runnables ───────────────────────────────────────────────────────────
    private val hideControlsRunnable = Runnable { hideControls() }

    private val progressRunnable = object : Runnable {
        override fun run() {
            updateProgress()
            progressHandler.postDelayed(this, 500)
        }
    }

    private val frameRunnable = object : Runnable {
        override fun run() {
            updateCameraAngles()
            frameHandler.postDelayed(this, 16)
        }
    }

    // ── Codec selector: HW first on API 29+, SW first on older/fallback ─────
    private val codecSelector =
        MediaCodecSelector { mimeType, requiresSecureDecoder, requiresTunnelingDecoder ->
            val all = ExoMediaCodecUtil.getDecoderInfos(
                mimeType, requiresSecureDecoder, requiresTunnelingDecoder
            )
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                // Android 10+: use default order (HW preferred)
                all
            } else {
                // Older: prefer SW to avoid HW quirks
                val sw = all.filter { isSw(it.name) }
                val hw = all.filter { !isSw(it.name) }
                sw + hw
            }
        }

    private fun isSw(name: String) =
        name.lowercase().let { it.startsWith("omx.google.") || it.startsWith("c2.android.") }

    // ────────────────────────────────────────────────────────────────────────
    //  Lifecycle
    // ────────────────────────────────────────────────────────────────────────

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Keep screen on + hardware acceleration
        window.addFlags(
            WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON or
                    WindowManager.LayoutParams.FLAG_HARDWARE_ACCELERATED
        )

        binding = ActivityPlayerBinding.inflate(layoutInflater)
        setContentView(binding.root)
        com.videoplayer.hdvideos.vrplayer1.ads.AnalyticsHelper.logScreen(this, "screen_player")

        ViewCompat.setOnApplyWindowInsetsListener(binding.main) { v, insets ->
            val sys = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(sys.left, sys.top, sys.right, sys.bottom)
            insets
        }

        window.statusBarColor = android.graphics.Color.BLACK

        enterPlayerFullscreen()
        WindowCompat.getInsetsController(window, window.decorView).apply {
            isAppearanceLightStatusBars = false  // false = white icons
        }


        audioManager = getSystemService(Context.AUDIO_SERVICE) as AudioManager

        parseIntent()
        setupControllers()
        setupGLSurface()
        setupOverlayButtons()
        setupSeekBar()
        setupVolumeControl()
        setupBrightnessControl()
        binding.loadingOverlay.visibility = View.VISIBLE
        initPlayer()
        scheduleHideControls()

        onBackPressedDispatcher.addCallback(this, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() = finish()
        })

        binding.tvVideoTitle.text = intent.getStringExtra("video_title") ?: ""

        binding.btnRewind.setOnClickListener {
            exoPlayer?.seekTo((exoPlayer!!.currentPosition - 10_000L).coerceAtLeast(0L))
            scheduleHideControls()
        }
        binding.btnForward.setOnClickListener {
            val dur = exoPlayer?.duration ?: 0L
            exoPlayer?.seekTo((exoPlayer!!.currentPosition + 10_000L).coerceAtMost(dur))
            scheduleHideControls()
        }
    }

    override fun onResume() {
        super.onResume()
        binding.glSurface.onResume()
        if (interaction == InteractionMode.MOTION || interaction == InteractionMode.BOTH) {
            gyro.start()
        }
        frameHandler.post(frameRunnable)
        progressHandler.post(progressRunnable)
        exoPlayer?.play()
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

    // ← Moved player release from onStop → onDestroy so background audio pause
    //   doesn't destroy the player on every home-button press.
    override fun onDestroy() {
        releasePlayer()
        super.onDestroy()
    }

    override fun onTouchEvent(event: MotionEvent): Boolean {
        if (event.action == MotionEvent.ACTION_DOWN) toggleControls()
        if (interaction == InteractionMode.TOUCH || interaction == InteractionMode.BOTH) {
            if (touch.onTouchEvent(event)) return true
        }
        return super.onTouchEvent(event)
    }

    // ────────────────────────────────────────────────────────────────────────
    //  Setup helpers
    // ────────────────────────────────────────────────────────────────────────

    private fun parseIntent() {
        viewMode = ViewMode.valueOf(
            intent.getStringExtra(IntentKeys.VIEW_MODE) ?: ViewMode.DEVICE.name
        )
        interaction = InteractionMode.valueOf(
            intent.getStringExtra(IntentKeys.INTERACTION) ?: InteractionMode.TOUCH.name
        )
        repeat = intent.getBooleanExtra(IntentKeys.REPEAT, false)
        exitOnFinish = intent.getBooleanExtra(IntentKeys.EXIT_ON_FINISH, false)
        videoUriStr = intent.getStringExtra(IntentKeys.VIDEO_URI)
    }

    private fun setupControllers() {
        gyro = GyroscopeController(this)
        touch = TouchController()
    }

    private fun setupGLSurface() {
        binding.glSurface.setEGLContextClientVersion(2)

        renderer = SphereRenderer(viewMode) { surface ->
            // This callback fires on the GL thread; jump to main thread
            runOnUiThread {
                surfaceReady = true
                pendingSurface = surface
                // If the player already exists, attach the surface now
                attachSurfaceIfReady()
            }
        }

        binding.glSurface.setRenderer(renderer)
        binding.glSurface.renderMode = GLSurfaceView.RENDERMODE_CONTINUOUSLY
    }

    /**
     * Called whenever either the player or the surface becomes available.
     * Only attaches when BOTH are ready, avoiding the race condition that
     * causes "video surface is null" errors on Android 10.
     */
    private fun attachSurfaceIfReady() {
        val player = exoPlayer ?: return
        val surface = pendingSurface ?: return
        if (!surfaceReady) return

        try {
            player.setVideoSurface(surface)
            Log.d(TAG, "Surface attached to player")
        } catch (e: Exception) {
            Log.e(TAG, "Failed to attach surface", e)
        }
    }

    private fun setupOverlayButtons() {
        binding.btnBack.setOnClickListener { onBackPressedDispatcher.onBackPressed() }
        binding.btnRecentre.setOnClickListener {
            gyro.reset(); touch.reset()
            Toast.makeText(this, "View re-centred", Toast.LENGTH_SHORT).show()
        }
        binding.btnPlayPause.setOnClickListener { togglePlayPause() }
    }

    private fun setupSeekBar() {
        binding.seekBar.setOnTouchListener { v, event ->
            if (event.action == MotionEvent.ACTION_DOWN || event.action == MotionEvent.ACTION_MOVE) {
                v.parent?.requestDisallowInterceptTouchEvent(true)
            }
            false
        }
        binding.seekBar.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(sb: SeekBar, progress: Int, fromUser: Boolean) {
                if (fromUser) {
                    val pos = (exoPlayer?.duration ?: 0L) * progress / 1000L
                    binding.tvCurrentTime.text = formatTime(pos)
                }
            }

            override fun onStartTrackingTouch(sb: SeekBar) {
                isDraggingSeek = true
            }

            override fun onStopTrackingTouch(sb: SeekBar) {
                isDraggingSeek = false
                val pos = (exoPlayer?.duration ?: 0L) * sb.progress / 1000L
                exoPlayer?.seekTo(pos)
                scheduleHideControls()
            }
        })
    }

    private fun setupVolumeControl() {
        // Volume SeekBar wired up but kept as no-op stubs — uncomment sbVolume views to activate
        @Suppress("UNUSED_VARIABLE")
        val maxVol = audioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC)
    }

    private fun setupBrightnessControl() {
        // Brightness SeekBar wired up — uncomment sbBrightness views to activate
        @Suppress("UNUSED_VARIABLE")
        val currentBrightness = try {
            Settings.System.getInt(contentResolver, Settings.System.SCREEN_BRIGHTNESS)
        } catch (e: Exception) {
            128
        }
    }

    // ────────────────────────────────────────────────────────────────────────
    //  Player init
    // ────────────────────────────────────────────────────────────────────────

//    private fun initPlayer() {
//        try {
//            val renderersFactory = DefaultRenderersFactory(this).apply {
//                // PREFER software extension but fall back to hardware gracefully
//                setExtensionRendererMode(DefaultRenderersFactory.EXTENSION_RENDERER_MODE_ON)
//                setMediaCodecSelector(codecSelector)
//            }
//
//            val player = ExoPlayer.Builder(this, renderersFactory)
//                .setSeekForwardIncrementMs(10_000)
//                .setSeekBackIncrementMs(10_000)
//                .build()
//                .also { exoPlayer = it }
//
//            player.repeatMode = if (repeat) Player.REPEAT_MODE_ONE else Player.REPEAT_MODE_OFF
//
//            player.addListener(object : Player.Listener {
//
//                override fun onPlaybackStateChanged(state: Int) {
//                    when (state) {
//                        Player.STATE_READY -> {
//                            binding.tvTotalTime.text = formatTime(player.duration)
//                            binding.seekBar.max = 1000
//                            updatePlayPauseIcon()
//                        }
//                        Player.STATE_ENDED -> if (exitOnFinish) finish()
//                        else -> {}
//                    }
//                }
//
//                override fun onIsPlayingChanged(isPlaying: Boolean) = updatePlayPauseIcon()
//
//                override fun onPlayerError(error: PlaybackException) {
//                    Log.e(TAG, "Player error [${error.errorCode}]: ${error.message}", error)
//
//                    val msg = when (error.errorCode) {
//                        PlaybackException.ERROR_CODE_IO_FILE_NOT_FOUND ->
//                            "File not found. Check the URI."
//                        PlaybackException.ERROR_CODE_IO_NO_PERMISSION ->
//                            "Permission denied. Re-open file from your file manager."
//                        PlaybackException.ERROR_CODE_DECODER_INIT_FAILED ->
//                            "Decoder init failed — retrying with fallback decoder."
//                        PlaybackException.ERROR_CODE_DECODING_FAILED ->
//                            "Decoding error — trying software decoder."
//                        else -> "Playback error (${error.errorCode}): ${error.message}"
//                    }
//
//                    // For decoder failures, retry with pure SW decoder before giving up
//                    if (error.errorCode == PlaybackException.ERROR_CODE_DECODER_INIT_FAILED ||
//                        error.errorCode == PlaybackException.ERROR_CODE_DECODING_FAILED
//                    ) {
//                        retryWithSoftwareDecoder()
//                        return
//                    }
//
//                    Toast.makeText(this@PlayerActivity, msg, Toast.LENGTH_LONG).show()
//                    finish()
//                }
//
//                override fun onVideoSizeChanged(videoSize: VideoSize) {
//                    Log.d(TAG, "Video: ${videoSize.width}×${videoSize.height}")
//                }
//
//                override fun onRenderedFirstFrame() {
//                    Log.d(TAG, "✓ First frame rendered")
//                }
//            })
//
//            // ── Grant URI read permission explicitly (required on Android 10) ──
//            val uri = buildUri()
//            if (uri != null && uri.scheme == "content") {
//                try {
//                    contentResolver.takePersistableUriPermission(
//                        uri,
//                        Intent.FLAG_GRANT_READ_URI_PERMISSION
//                    )
//                } catch (e: SecurityException) {
//                    // Not a persistable URI — that's fine, we still have temporary access
//                    Log.w(TAG, "takePersistableUriPermission failed (non-fatal): ${e.message}")
//                }
//            }
//
//            player.setMediaItem(buildMediaItem())
//            player.prepare()
//
//            // If surface arrived before player was built, attach it now
//            attachSurfaceIfReady()
//
//        } catch (e: Exception) {
//            Log.e(TAG, "initPlayer failed", e)
//            Toast.makeText(this, "Failed to init player: ${e.message}", Toast.LENGTH_LONG).show()
//            finish()
//        }
//    }


    private val softwareFirstSelector =
        MediaCodecSelector { mimeType, requiresSecureDecoder, requiresTunnelingDecoder ->
            val all = ExoMediaCodecUtil.getDecoderInfos(
                mimeType, requiresSecureDecoder, requiresTunnelingDecoder
            )
            val sw = all.filter { isSw(it.name) }
            val hw = all.filter { !isSw(it.name) }
            sw + hw
        }

    //    private fun initPlayer() {
//    try {
//        val renderersFactory = DefaultRenderersFactory(this).apply {
//            setExtensionRendererMode(DefaultRenderersFactory.EXTENSION_RENDERER_MODE_PREFER)
//            setMediaCodecSelector(softwareFirstSelector)
//        }
//
//        // ✅ Same OkHttp client used for Glide — IPv4 + SSL trust-all + headers
//        val okHttpClient = OkHttpClient.Builder()
//            .dns(object : okhttp3.Dns {
//                override fun lookup(hostname: String): List<java.net.InetAddress> {
//                    return okhttp3.Dns.SYSTEM.lookup(hostname)
//                        .filterIsInstance<java.net.Inet4Address>()
//                        .ifEmpty { okhttp3.Dns.SYSTEM.lookup(hostname) }
//                }
//            })
//            .sslSocketFactory(
//                UrlUtils.sslSocketFactory,
//                UrlUtils.trustAllCerts[0] as javax.net.ssl.X509TrustManager
//            )
//            .hostnameVerifier(UrlUtils.hostnameVerifier)
//            .addInterceptor { chain ->
//                val request = chain.request().newBuilder()
//                    .header("Referer",    UrlUtils.REFERER)
//                    .header("User-Agent", UrlUtils.USER_AGENT)
//                    .build()
//                chain.proceed(request)
//            }
//            .connectTimeout(15, java.util.concurrent.TimeUnit.SECONDS)
//            .readTimeout(30, java.util.concurrent.TimeUnit.SECONDS)
//            .build()
//
//        val okHttpDataSourceFactory = DefaultDataSource.Factory(
//            this,
//            OkHttpDataSource.Factory(okHttpClient)
//        )
//        val mediaSourceFactory = DefaultMediaSourceFactory(okHttpDataSourceFactory)
//        val player = ExoPlayer.Builder(this, renderersFactory)
//            .setMediaSourceFactory(mediaSourceFactory)  // ✅ this line was missing
//            .build()
//            .also { exoPlayer = it }
//
//        player.repeatMode    = if (repeat) Player.REPEAT_MODE_ONE else Player.REPEAT_MODE_OFF
//        player.playWhenReady = true
//
//        player.addListener(object : Player.Listener {
//            override fun onPlaybackStateChanged(state: Int) {
//                runOnUiThread {
//                    binding.loadingOverlay.visibility =
//                        if (state == Player.STATE_BUFFERING) View.VISIBLE else View.GONE
//                }
//                when (state) {
//                    Player.STATE_READY -> {
//                        binding.tvTotalTime.text = formatTime(player.duration)
//                        binding.seekBar.max = 1000
//                        updatePlayPauseIcon()
//                    }
//                    Player.STATE_ENDED -> {
//                        if (exitOnFinish) finish()
//                        else { player.seekTo(0); player.play() }
//                    }
//                    else -> {}
//                }
//            }
//
//            override fun onIsPlayingChanged(isPlaying: Boolean) { updatePlayPauseIcon() }
//
//            override fun onPlayerError(error: PlaybackException) {
//                Log.e(TAG, "Player error [${error.errorCode}]", error)
//                Toast.makeText(this@PlayerActivity, "Playback error: ${error.message}", Toast.LENGTH_LONG).show()
//                finish()
//            }
//
//            override fun onVideoSizeChanged(videoSize: VideoSize) {
//                Log.d(TAG, "Video: ${videoSize.width}×${videoSize.height}")
//            }
//
//            override fun onRenderedFirstFrame() {
//                Log.d(TAG, "✓ First frame rendered")
//            }
//        })
//
//        player.setMediaItem(buildMediaItem() ?: return)
//        player.prepare()
//
//    } catch (e: Exception) {
//        Toast.makeText(this, "Failed to init player: ${e.message}", Toast.LENGTH_LONG).show()
//        finish()
//    }
//}
    private fun initPlayer() {
        try {
            val renderersFactory = DefaultRenderersFactory(this).apply {
                setExtensionRendererMode(DefaultRenderersFactory.EXTENSION_RENDERER_MODE_PREFER)
                setMediaCodecSelector(codecSelector)
                setEnableDecoderFallback(true)
            }

            // Standard fast network client
            val okHttpClient = OkHttpClient.Builder()
                .sslSocketFactory(
                    UrlUtils.sslSocketFactory,
                    UrlUtils.trustAllCerts[0] as javax.net.ssl.X509TrustManager
                )
                .hostnameVerifier(UrlUtils.hostnameVerifier)
                .addInterceptor { chain ->
                    val request = chain.request().newBuilder()
                        .header("Referer", UrlUtils.REFERER)
                        .header("User-Agent", UrlUtils.USER_AGENT)
                        .build()
                    chain.proceed(request)
                }
                .connectTimeout(15, java.util.concurrent.TimeUnit.SECONDS)
                .readTimeout(30, java.util.concurrent.TimeUnit.SECONDS)
                .build()

            val okHttpDataSourceFactory = DefaultDataSource.Factory(
                this,
                OkHttpDataSource.Factory(okHttpClient)
            )
            val mediaSourceFactory = DefaultMediaSourceFactory(okHttpDataSourceFactory)

            val mediaItem = buildMediaItem()
            if (mediaItem == null) {
                finish()
                return
            }

            val loadControl = androidx.media3.exoplayer.DefaultLoadControl.Builder()
                .setBufferDurationsMs(
                    20_000,
                    60_000,
                    2_500,
                    5_000
                )
                .setPrioritizeTimeOverSizeThresholds(true)
                .build()

            val player = ExoPlayer.Builder(this, renderersFactory)
                .setMediaSourceFactory(mediaSourceFactory)
                .setLoadControl(loadControl)
                .build()
                .also { exoPlayer = it }

            player.repeatMode = if (repeat) Player.REPEAT_MODE_ONE else Player.REPEAT_MODE_OFF
            player.playWhenReady = true

            player.addListener(object : Player.Listener {
                override fun onPlaybackStateChanged(state: Int) {
                    runOnUiThread {
                        binding.loadingOverlay.visibility =
                            if (state == Player.STATE_BUFFERING || state == Player.STATE_IDLE) View.VISIBLE else View.GONE
                    }
                    when (state) {
                        Player.STATE_READY -> {
                            binding.loadingOverlay.visibility = View.GONE
                            binding.tvTotalTime.text = formatTime(player.duration)
                            binding.seekBar.max = 1000
                            updatePlayPauseIcon()
                        }

                        Player.STATE_ENDED -> {
                            if (exitOnFinish) finish()
                            else {
                                player.seekTo(0); player.play()
                            }
                        }

                        else -> {}
                    }
                }

                override fun onIsPlayingChanged(isPlaying: Boolean) {
                    updatePlayPauseIcon()
                }

                override fun onPlayerError(error: PlaybackException) {
                    Log.e(TAG, "Player error [${error.errorCode}]", error)
                    val code = error.errorCode
                    val transient = code == PlaybackException.ERROR_CODE_IO_NETWORK_CONNECTION_FAILED ||
                            code == PlaybackException.ERROR_CODE_IO_NETWORK_CONNECTION_TIMEOUT ||
                            code == PlaybackException.ERROR_CODE_IO_UNSPECIFIED ||
                            code == PlaybackException.ERROR_CODE_TIMEOUT
                    if (transient && networkRetries < 3) {
                        networkRetries++
                        player.prepare()
                        return
                    }
                    val decoderIssue = code == PlaybackException.ERROR_CODE_DECODER_INIT_FAILED ||
                            code == PlaybackException.ERROR_CODE_DECODING_FAILED ||
                            code == PlaybackException.ERROR_CODE_DECODING_FORMAT_EXCEEDS_CAPABILITIES ||
                            code == PlaybackException.ERROR_CODE_DECODING_FORMAT_UNSUPPORTED
                    if (decoderIssue && !triedSoftwareDecoder) {
                        triedSoftwareDecoder = true
                        runOnUiThread { retryWithSoftwareDecoder() }
                        return
                    }
                    Toast.makeText(
                        this@PlayerActivity,
                        "This video can't be played right now. Please try another one.",
                        Toast.LENGTH_LONG
                    ).show()
                    finish()
                }

                override fun onVideoSizeChanged(videoSize: VideoSize) {
                    Log.d(TAG, "Video: ${videoSize.width}×${videoSize.height}")
                }

                override fun onRenderedFirstFrame() {
                    Log.d(TAG, "✓ First frame rendered")
                    runOnUiThread {
                        binding.loadingOverlay.visibility = View.GONE
                    }
                }
            })

            player.setMediaItem(mediaItem)  // ✅ Now it's guaranteed non-null
            player.prepare()
            attachSurfaceIfReady()

        } catch (e: Exception) {
            Toast.makeText(this, "Failed to init player: ${e.message}", Toast.LENGTH_LONG).show()
            finish()
        }
    }

    /**
     * Called when a decoder error occurs. Releases the current player and
     * rebuilds it forcing software-only decoding.
     */
//    private fun retryWithSoftwareDecoder() {
//        Log.w(TAG, "Retrying with software-only decoder")
////        Toast.makeText(this, "Switching to software decoder…", Toast.LENGTH_SHORT).show()
//
//        releasePlayer()
//
//        try {
//            val swSelector = MediaCodecSelector { mimeType, requiresSecure, requiresTunneling ->
//                ExoMediaCodecUtil.getDecoderInfos(mimeType, requiresSecure, requiresTunneling)
//                    .filter { isSw(it.name) }
//                    .ifEmpty {
//                        // No SW decoder found — fall back to full list rather than crashing
//                        ExoMediaCodecUtil.getDecoderInfos(mimeType, requiresSecure, requiresTunneling)
//                    }
//            }
//
//            val rf = DefaultRenderersFactory(this).apply {
//                setExtensionRendererMode(DefaultRenderersFactory.EXTENSION_RENDERER_MODE_PREFER)
//                setMediaCodecSelector(swSelector)
//            }
//
//            val player = ExoPlayer.Builder(this, rf).build().also { exoPlayer = it }
//            player.repeatMode = if (repeat) Player.REPEAT_MODE_ONE else Player.REPEAT_MODE_OFF
//
//            player.addListener(object : Player.Listener {
//                override fun onPlaybackStateChanged(state: Int) {
//                    if (state == Player.STATE_READY) {
//                        binding.tvTotalTime.text = formatTime(player.duration)
//                        binding.seekBar.max = 1000
//                        updatePlayPauseIcon()
//                    } else if (state == Player.STATE_ENDED && exitOnFinish) finish()
//                }
//                override fun onIsPlayingChanged(isPlaying: Boolean) = updatePlayPauseIcon()
//                override fun onPlayerError(error: PlaybackException) {
//                    Log.e(TAG, "SW fallback also failed [${error.errorCode}]", error)
//                    Toast.makeText(
//                        this@PlayerActivity,
//                        "Cannot play this file on your device.",
//                        Toast.LENGTH_LONG
//                    ).show()
//                    finish()
//                }
//            })
//
//            player.setMediaItem(buildMediaItem())
//            player.prepare()
//            attachSurfaceIfReady()
//            player.play()
//
//        } catch (e: Exception) {
//            Log.e(TAG, "SW retry failed", e)
//            Toast.makeText(this, "Playback not supported: ${e.message}", Toast.LENGTH_LONG).show()
//            finish()
//        }
//    }
    private fun retryWithSoftwareDecoder() {
        Log.w(TAG, "Retrying with software-only decoder")

        releasePlayer()

        try {
            val swSelector = MediaCodecSelector { mimeType, requiresSecure, requiresTunneling ->
                ExoMediaCodecUtil.getDecoderInfos(mimeType, requiresSecure, requiresTunneling)
                    .filter { isSw(it.name) }
                    .ifEmpty {
                        ExoMediaCodecUtil.getDecoderInfos(
                            mimeType,
                            requiresSecure,
                            requiresTunneling
                        )
                    }
            }

            val rf = DefaultRenderersFactory(this).apply {
                setExtensionRendererMode(DefaultRenderersFactory.EXTENSION_RENDERER_MODE_PREFER)
                setMediaCodecSelector(swSelector)
            }

            // ✅ Check for null media item
            val mediaItem = buildMediaItem()
            if (mediaItem == null) {
                finish()
                return
            }

            val player = ExoPlayer.Builder(this, rf).build().also { exoPlayer = it }
            player.repeatMode = if (repeat) Player.REPEAT_MODE_ONE else Player.REPEAT_MODE_OFF

            player.addListener(object : Player.Listener {
                override fun onPlaybackStateChanged(state: Int) {
                    if (state == Player.STATE_READY) {
                        binding.tvTotalTime.text = formatTime(player.duration)
                        binding.seekBar.max = 1000
                        updatePlayPauseIcon()
                    } else if (state == Player.STATE_ENDED && exitOnFinish) finish()
                }

                override fun onIsPlayingChanged(isPlaying: Boolean) = updatePlayPauseIcon()
                override fun onPlayerError(error: PlaybackException) {
                    Log.e(TAG, "SW fallback also failed [${error.errorCode}]", error)
                    Toast.makeText(
                        this@PlayerActivity,
                        "Cannot play this file on your device.",
                        Toast.LENGTH_LONG
                    ).show()
                    finish()
                }
            })

            player.setMediaItem(mediaItem)  // ✅ Now safe
            player.prepare()
            attachSurfaceIfReady()
            player.play()

        } catch (e: Exception) {
            Log.e(TAG, "SW retry failed", e)
            Toast.makeText(this, "Playback not supported: ${e.message}", Toast.LENGTH_LONG).show()
            finish()
        }
    }

    // ────────────────────────────────────────────────────────────────────────
    //  Controls
    // ────────────────────────────────────────────────────────────────────────

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
            binding.controlsOverlay.visibility = View.GONE
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
        val isPlaying = exoPlayer?.isPlaying == true
        binding.btnPlayPause.setImageResource(
            if (isPlaying) R.drawable.pause else R.drawable.ic_play_circle
        )
    }

    // ────────────────────────────────────────────────────────────────────────
    //  Progress / camera
    // ────────────────────────────────────────────────────────────────────────

    private fun updateProgress() {
        val player = exoPlayer ?: return
        val duration = player.duration
        val position = player.currentPosition
        if (duration > 0 && !isDraggingSeek) {
            binding.seekBar.progress = (1000L * position / duration).toInt()
        }
        binding.tvCurrentTime.text = formatTime(position)
        if (duration > 0) binding.tvTotalTime.text = formatTime(duration)
    }

    private fun updateCameraAngles() {
        val (yaw, pitch) = when (interaction) {
            InteractionMode.MOTION -> gyro.yaw to gyro.pitch
            InteractionMode.TOUCH -> touch.yaw to touch.pitch
            InteractionMode.BOTH -> (gyro.yaw + touch.yaw) to (gyro.pitch + touch.pitch)
        }
        renderer.yaw = yaw
        renderer.pitch = pitch
    }

    // ────────────────────────────────────────────────────────────────────────
    //  Media item / URI helpers
    // ────────────────────────────────────────────────────────────────────────

    /** Returns the URI to play, or null if we should fall back to the raw demo. */
    private fun buildUri(): Uri? {
        val str = videoUriStr?.takeIf { it.isNotEmpty() } ?: return null
        return try {
            Uri.parse(str)
        } catch (e: Exception) {
            null
        }
    }

    //    private fun buildMediaItem(): MediaItem {
//        val uri = buildUri()
//        return if (uri != null) {
//            MediaItem.Builder()
//                .setUri(uri)
//                .build()
//        } else {
//            MediaItem.fromUri(
//                Uri.parse("android.resource://$packageName/${R.raw.demo_360}")
//            )
//        }
//    }
    private fun buildMediaItem(): MediaItem? {
        val uri = buildUri()
        return if (uri != null) {
            MediaItem.Builder()
                .setUri(uri)
                .build()
        } else {
            // Show toast for no internet
            Toast.makeText(
                this,
                "No internet connection. Please check your network.",
                Toast.LENGTH_LONG
            ).show()
            null
        }
    }

    // ────────────────────────────────────────────────────────────────────────
    //  Release
    // ────────────────────────────────────────────────────────────────────────

    private fun releasePlayer() {
        try {
            exoPlayer?.release()
            exoPlayer = null
        } catch (_: Exception) {
        }
    }

    // ────────────────────────────────────────────────────────────────────────
    //  Utility
    // ────────────────────────────────────────────────────────────────────────

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