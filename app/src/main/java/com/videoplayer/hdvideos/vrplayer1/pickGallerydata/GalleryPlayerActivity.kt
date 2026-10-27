package com.videoplayer.hdvideos.vrplayer1.pickGallerydata

import com.videoplayer.hdvideos.vrplayer1.Detail.utils.enterPlayerFullscreen
import android.annotation.SuppressLint
import com.videoplayer.hdvideos.vrplayer1.Detail.utils.enterPlayerFullscreen
import android.content.Context
import com.videoplayer.hdvideos.vrplayer1.Detail.utils.enterPlayerFullscreen
import android.graphics.Bitmap
import com.videoplayer.hdvideos.vrplayer1.Detail.utils.enterPlayerFullscreen
import android.graphics.ImageDecoder
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
import android.provider.MediaStore
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
import androidx.media3.exoplayer.source.DefaultMediaSourceFactory
import com.videoplayer.hdvideos.vrplayer1.Detail.utils.enterPlayerFullscreen
import com.videoplayer.hdvideos.vrplayer1.Detail.utils.GyroscopeController
import com.videoplayer.hdvideos.vrplayer1.Detail.utils.enterPlayerFullscreen
import com.videoplayer.hdvideos.vrplayer1.Detail.utils.IntentKeys
import com.videoplayer.hdvideos.vrplayer1.Detail.utils.enterPlayerFullscreen
import com.videoplayer.hdvideos.vrplayer1.Detail.utils.InteractionMode
 import com.videoplayer.hdvideos.vrplayer1.Detail.utils.TouchController
import com.videoplayer.hdvideos.vrplayer1.Detail.utils.enterPlayerFullscreen
import com.videoplayer.hdvideos.vrplayer1.Detail.utils.ViewMode
import com.videoplayer.hdvideos.vrplayer1.Detail.utils.enterPlayerFullscreen
import com.videoplayer.hdvideos.vrplayer1.R
import com.videoplayer.hdvideos.vrplayer1.Detail.utils.enterPlayerFullscreen
import com.videoplayer.hdvideos.vrplayer1.databinding.ActivityGalleryPlayerBinding
import com.videoplayer.hdvideos.vrplayer1.Detail.utils.enterPlayerFullscreen
import com.videoplayer.hdvideos.vrplayer1.pickGallerydata.utiles.BaseActivity
import com.videoplayer.hdvideos.vrplayer1.Detail.utils.enterPlayerFullscreen
import com.videoplayer.hdvideos.vrplayer1.pickGallerydata.utiles.GalleryViewMode
import com.videoplayer.hdvideos.vrplayer1.Detail.utils.enterPlayerFullscreen
import com.videoplayer.hdvideos.vrplayer1.pickGallerydata.utiles.SphereRendererforgallery
import com.videoplayer.hdvideos.vrplayer1.Detail.utils.enterPlayerFullscreen
import kotlinx.coroutines.CoroutineScope
import com.videoplayer.hdvideos.vrplayer1.Detail.utils.enterPlayerFullscreen
import kotlinx.coroutines.Dispatchers
import com.videoplayer.hdvideos.vrplayer1.Detail.utils.enterPlayerFullscreen
import kotlinx.coroutines.Job
import com.videoplayer.hdvideos.vrplayer1.Detail.utils.enterPlayerFullscreen
import kotlinx.coroutines.delay
import com.videoplayer.hdvideos.vrplayer1.Detail.utils.enterPlayerFullscreen
import kotlinx.coroutines.isActive
import com.videoplayer.hdvideos.vrplayer1.Detail.utils.enterPlayerFullscreen
import kotlinx.coroutines.launch



@OptIn(UnstableApi::class)
class GalleryPlayerActivity  : BaseActivity() {

    private lateinit var binding: ActivityGalleryPlayerBinding

    // ── Mode / flags ──────────────────────────────────────────────────────────
    private lateinit var viewMode: GalleryViewMode
    private lateinit var interaction: InteractionMode
    private var repeat       = false
    private var exitOnFinish = false
    private var isImage      = false
    private var videoUriStr: String? = null

    // ── Player + GL ───────────────────────────────────────────────────────────
    private var exoPlayer: ExoPlayer? = null
    private lateinit var renderer: SphereRendererforgallery
    private var surfaceReady    = false
    private var pendingSurface: Surface? = null

    // ── Controllers (dedicated instances — not shared) ────────────────────────
    private lateinit var gyro:  GyroscopeController
    private lateinit var touch: TouchController
    private lateinit var audioManager: AudioManager

    // ── Handlers ──────────────────────────────────────────────────────────────
    private val frameHandler    = Handler(Looper.getMainLooper())
    private val uiHandler       = Handler(Looper.getMainLooper())
    private val progressHandler = Handler(Looper.getMainLooper())

    private val TAG = "GalleryPlayer"

    private var controlsVisible = true
    private var isDraggingSeek  = false

    // ── Zoom constants ────────────────────────────────────────────────────────
    private val ZOOM_MIN  = 0.3f
    private val ZOOM_MAX  = 1.5f
    private val ZOOM_STEP = 0.1f

    // ── Zoom repeat jobs ──────────────────────────────────────────────────────
    private var zoomInJob:  Job? = null
    private var zoomOutJob: Job? = null

    companion object {
        const val EXTRA_URI      = "gallery_uri"
        const val EXTRA_IS_IMAGE = "gallery_is_image"
        const val EXTRA_TITLE    = "gallery_title"
    }

    // ── Runnables ─────────────────────────────────────────────────────────────
    private val hideControlsRunnable = Runnable { hideControls() }

    private val progressRunnable = object : Runnable {
        override fun run() {
            updateProgress()
            progressHandler.postDelayed(this, 500)
        }
    }

    /**
     * Called at ~60 fps.
     * 1. Advances touch fling physics (deceleration when finger is lifted).
     * 2. Pushes the latest yaw/pitch into the renderer.
     */
    private val frameRunnable = object : Runnable {
        override fun run() {
            touch.update()          // fling deceleration
            updateCameraAngles()    // push to renderer
            frameHandler.postDelayed(this, 16)
        }
    }

    // ── SW-first codec selector ───────────────────────────────────────────────
    private val softwareFirstSelector =
        MediaCodecSelector { mimeType, requiresSecureDecoder, requiresTunnelingDecoder ->
            val all = MediaCodecUtil.getDecoderInfos(mimeType, requiresSecureDecoder, requiresTunnelingDecoder)
            val sw  = all.filter { isSw(it.name) }
            val hw  = all.filter { !isSw(it.name) }
            sw + hw
        }

    private fun isSw(name: String) =
        name.lowercase().let { it.startsWith("omx.google.") || it.startsWith("c2.android.") }

    // ──────────────────────────────────────────────────────────────────────────
    //  Lifecycle
    // ──────────────────────────────────────────────────────────────────────────

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        window.addFlags(
            WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON or
                    WindowManager.LayoutParams.FLAG_HARDWARE_ACCELERATED
        )

        binding = ActivityGalleryPlayerBinding.inflate(layoutInflater)
        setContentView(binding.root)
        com.videoplayer.hdvideos.vrplayer1.ads.AnalyticsHelper.logScreen(this, "screen_gallery_player")

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

        audioManager = getSystemService(Context.AUDIO_SERVICE) as AudioManager

        parseIntent()
        setupControllers()      // create fresh GyroscopeController & TouchController
        setupGLSurface()
        setupOverlayButtons()
        setupSeekBar()
        setupPanoramicControls()
        scheduleHideControls()

        onBackPressedDispatcher.addCallback(this, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() = finish()
        })

        binding.tvVideoTitle.text = intent.getStringExtra(EXTRA_TITLE) ?: ""

        // ── Rewind / Forward ───────────────────────────────────────────────────
        binding.btnRewind.setOnClickListener {
            exoPlayer?.seekTo((exoPlayer!!.currentPosition - 10_000L).coerceAtLeast(0L))
            scheduleHideControls()
        }
        binding.btnForward.setOnClickListener {
            val dur = exoPlayer?.duration ?: 0L
            exoPlayer?.seekTo((exoPlayer!!.currentPosition + 10_000L).coerceAtMost(dur))
            scheduleHideControls()
        }

        if (isImage) setupForImage() else setupForVideo()
    }

    override fun onResume() {
        super.onResume()
        binding.glSurface.onResume()

        // Always start frame loop for smooth camera
        frameHandler.post(frameRunnable)

        if (!isImage) {
            progressHandler.post(progressRunnable)
            exoPlayer?.play()
        }

        // Gyroscope: start() calls reset() internally, so the centre is always
        // re-snapped to the physical orientation at the moment the user returns.
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
        zoomInJob?.cancel()
        zoomOutJob?.cancel()
        super.onPause()
    }

    override fun onDestroy() {
        releasePlayer()
        super.onDestroy()
    }

    /**
     * Route touch events:
     *  1. Toggle controls overlay on tap.
     *  2. Forward to TouchController only in TOUCH / BOTH mode.
     */
    override fun onTouchEvent(event: MotionEvent): Boolean {
        if (event.action == MotionEvent.ACTION_DOWN) toggleControls()
        if (interaction == InteractionMode.TOUCH || interaction == InteractionMode.BOTH) {
            if (touch.onTouchEvent(event)) return true
        }
        return super.onTouchEvent(event)
    }

    // ──────────────────────────────────────────────────────────────────────────
    //  Intent parsing
    // ──────────────────────────────────────────────────────────────────────────

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
        isImage      = intent.getBooleanExtra(EXTRA_IS_IMAGE, false)
    }

    // ──────────────────────────────────────────────────────────────────────────
    //  Setup helpers
    // ──────────────────────────────────────────────────────────────────────────

    /** Creates fresh, dedicated controller instances for this activity. */
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
                if (!isImage) attachSurfaceIfReady()
            }
        }

        binding.glSurface.setRenderer(renderer)
        binding.glSurface.renderMode = GLSurfaceView.RENDERMODE_CONTINUOUSLY
    }

    private fun setupOverlayButtons() {
        binding.btnBack.setOnClickListener { finish() }

        binding.btnRecentre.setOnClickListener {
            // Reset BOTH controllers so both modes re-centre
            gyro.reset()
            touch.reset()
            Toast.makeText(this, "View re-centred", Toast.LENGTH_SHORT).show()
        }

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

    // ──────────────────────────────────────────────────────────────────────────
    //  Panoramic controls (zoom, re-centre) — shown only for PANORAMIC image
    // ──────────────────────────────────────────────────────────────────────────

    private fun setupPanoramicControls() {
        val isPanoramic = viewMode == GalleryViewMode.PANORAMIC && isImage
        binding.panoramicControlsGroup.visibility =
            if (isPanoramic) View.VISIBLE else View.GONE

        if (!isPanoramic) return

        binding.btnPanoRecentre.setOnClickListener {
            gyro.reset()
            touch.reset()
            renderer.zoom = 1.0f
            Toast.makeText(this, "View re-centred", Toast.LENGTH_SHORT).show()
            scheduleHideControls()
        }

        binding.btnZoomIn.setOnTouchListener { _, event ->
            when (event.action) {
                MotionEvent.ACTION_DOWN -> {
                    applyZoom(-ZOOM_STEP)
                    zoomInJob = CoroutineScope(Dispatchers.Main).launch {
                        delay(400)
                        while (isActive) { applyZoom(-ZOOM_STEP); delay(100) }
                    }
                    scheduleHideControls()
                    true
                }
                MotionEvent.ACTION_UP, MotionEvent.ACTION_CANCEL -> {
                    zoomInJob?.cancel(); zoomInJob = null; true
                }
                else -> false
            }
        }

        binding.btnZoomOut.setOnTouchListener { _, event ->
            when (event.action) {
                MotionEvent.ACTION_DOWN -> {
                    applyZoom(ZOOM_STEP)
                    zoomOutJob = CoroutineScope(Dispatchers.Main).launch {
                        delay(400)
                        while (isActive) { applyZoom(ZOOM_STEP); delay(100) }
                    }
                    scheduleHideControls()
                    true
                }
                MotionEvent.ACTION_UP, MotionEvent.ACTION_CANCEL -> {
                    zoomOutJob?.cancel(); zoomOutJob = null; true
                }
                else -> false
            }
        }
    }

    private fun applyZoom(delta: Float) {
        renderer.zoom = (renderer.zoom + delta).coerceIn(ZOOM_MIN, ZOOM_MAX)
    }

    // ──────────────────────────────────────────────────────────────────────────
    //  Image mode
    // ──────────────────────────────────────────────────────────────────────────

    private fun setupForImage() {
        binding.seekBar.visibility       = View.GONE
        binding.btnRewind.visibility     = View.GONE
        binding.btnForward.visibility    = View.GONE
        binding.tvCurrentTime.visibility = View.GONE
        binding.tvTotalTime.visibility   = View.GONE
        binding.btnPlayPause.visibility  = View.GONE
        binding.loadingOverlay.visibility = View.VISIBLE

        val uriStr = videoUriStr
        if (uriStr.isNullOrEmpty()) {
            Toast.makeText(this, "No image URI provided", Toast.LENGTH_LONG).show()
            finish(); return
        }

        val uri = Uri.parse(uriStr)

        Thread {
            try {
                val bitmap: Bitmap = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                    val source = ImageDecoder.createSource(contentResolver, uri)
                    ImageDecoder.decodeBitmap(source) { decoder, _, _ ->
                        decoder.allocator = ImageDecoder.ALLOCATOR_SOFTWARE
                    }
                } else {
                    @Suppress("DEPRECATION")
                    MediaStore.Images.Media.getBitmap(contentResolver, uri)
                }

                runOnUiThread { binding.loadingOverlay.visibility = View.GONE }

                binding.glSurface.queueEvent { renderer.setImageBitmap(bitmap) }

            } catch (e: Exception) {
                Log.e(TAG, "Image decode failed", e)
                runOnUiThread {
                    binding.loadingOverlay.visibility = View.GONE
                    Toast.makeText(this, "Failed to load image: ${e.message}", Toast.LENGTH_LONG).show()
                    finish()
                }
            }
        }.start()
    }

    // ──────────────────────────────────────────────────────────────────────────
    //  Video mode
    // ──────────────────────────────────────────────────────────────────────────

    private fun setupForVideo() {
        val uriStr = videoUriStr
        if (uriStr.isNullOrEmpty()) {
            Toast.makeText(this, "No video URI provided", Toast.LENGTH_LONG).show()
            finish(); return
        }

        val uri = Uri.parse(uriStr)

        try {
            val renderersFactory = DefaultRenderersFactory(this).apply {
                setExtensionRendererMode(DefaultRenderersFactory.EXTENSION_RENDERER_MODE_PREFER)
                setMediaCodecSelector(softwareFirstSelector)
            }

            val player = ExoPlayer.Builder(this, renderersFactory)
                .setMediaSourceFactory(DefaultMediaSourceFactory(this))
                .build()
                .also { exoPlayer = it }

            if (uri.scheme == "content") {
                try {
                    contentResolver.takePersistableUriPermission(
                        uri, android.content.Intent.FLAG_GRANT_READ_URI_PERMISSION
                    )
                } catch (e: SecurityException) {
                    Log.w(TAG, "takePersistableUriPermission non-fatal: ${e.message}")
                }
            }

            player.repeatMode    = if (repeat) Player.REPEAT_MODE_ONE else Player.REPEAT_MODE_OFF
            player.playWhenReady = true

            player.addListener(object : Player.Listener {
                override fun onPlaybackStateChanged(state: Int) {
                    runOnUiThread {
                        binding.loadingOverlay.visibility =
                            if (state == Player.STATE_BUFFERING) View.VISIBLE else View.GONE
                    }
                    when (state) {
                        Player.STATE_READY -> {
                            binding.tvTotalTime.text = formatTime(player.duration)
                            binding.seekBar.max = 1000
                            updatePlayPauseIcon()
                            // ─── Centre the view when video first becomes ready ───
                            runOnUiThread {
                                gyro.reset()
                                touch.reset()
                            }
                        }
                        Player.STATE_ENDED -> {
                            if (exitOnFinish) finish()
                            else { player.seekTo(0); player.play() }
                        }
                        else -> {}
                    }
                }

                override fun onIsPlayingChanged(isPlaying: Boolean) = updatePlayPauseIcon()

                override fun onPlayerError(error: PlaybackException) {
                    Log.e(TAG, "Player error [${error.errorCode}]", error)
                    if (error.errorCode == PlaybackException.ERROR_CODE_DECODER_INIT_FAILED ||
                        error.errorCode == PlaybackException.ERROR_CODE_DECODING_FAILED) {
                        retryWithSoftwareDecoder(uri); return
                    }
                    val msg = when (error.errorCode) {
                        PlaybackException.ERROR_CODE_IO_FILE_NOT_FOUND ->
                            "File not found. It may have been moved or deleted."
                        PlaybackException.ERROR_CODE_IO_NO_PERMISSION ->
                            "Permission denied. Re-pick the file from gallery."
                        else -> "Playback error (${error.errorCode}): ${error.message}"
                    }
                    Toast.makeText(this@GalleryPlayerActivity, msg, Toast.LENGTH_LONG).show()
                    finish()
                }

                override fun onVideoSizeChanged(videoSize: VideoSize) {
                    Log.d(TAG, "Video size: ${videoSize.width}×${videoSize.height}")
                }

                override fun onRenderedFirstFrame() {
                    Log.d(TAG, "✓ First frame rendered")
                    // Final safety-net: centre again the moment the first frame appears
                    runOnUiThread {
                        gyro.reset()
                        touch.reset()
                    }
                }
            })

            player.setMediaItem(MediaItem.Builder().setUri(uri).build())
            player.prepare()
            attachSurfaceIfReady()

        } catch (e: Exception) {
            Log.e(TAG, "setupForVideo failed", e)
            Toast.makeText(this, "Failed to init player: ${e.message}", Toast.LENGTH_LONG).show()
            finish()
        }
    }

    // ──────────────────────────────────────────────────────────────────────────
    //  SW decoder fallback
    // ──────────────────────────────────────────────────────────────────────────

    private fun retryWithSoftwareDecoder(uri: Uri) {
        Log.w(TAG, "Retrying with SW-only decoder")
        releasePlayer()

        try {
            val swSelector = MediaCodecSelector { mimeType, requiresSecure, requiresTunneling ->
                MediaCodecUtil.getDecoderInfos(mimeType, requiresSecure, requiresTunneling)
                    .filter { isSw(it.name) }
                    .ifEmpty { MediaCodecUtil.getDecoderInfos(mimeType, requiresSecure, requiresTunneling) }
            }

            val rf = DefaultRenderersFactory(this).apply {
                setExtensionRendererMode(DefaultRenderersFactory.EXTENSION_RENDERER_MODE_PREFER)
                setMediaCodecSelector(swSelector)
            }

            val player = ExoPlayer.Builder(this, rf)
                .setMediaSourceFactory(DefaultMediaSourceFactory(this))
                .build()
                .also { exoPlayer = it }

            player.repeatMode = if (repeat) Player.REPEAT_MODE_ONE else Player.REPEAT_MODE_OFF

            player.addListener(object : Player.Listener {
                override fun onPlaybackStateChanged(state: Int) {
                    if (state == Player.STATE_READY) {
                        binding.tvTotalTime.text = formatTime(player.duration)
                        binding.seekBar.max = 1000
                        updatePlayPauseIcon()
                        runOnUiThread { gyro.reset(); touch.reset() }
                    } else if (state == Player.STATE_ENDED && exitOnFinish) finish()
                }
                override fun onIsPlayingChanged(isPlaying: Boolean) = updatePlayPauseIcon()
                override fun onPlayerError(error: PlaybackException) {
                    Log.e(TAG, "SW fallback also failed [${error.errorCode}]", error)
                    Toast.makeText(this@GalleryPlayerActivity, "Cannot play this file on your device.", Toast.LENGTH_LONG).show()
                    finish()
                }
            })

            player.setMediaItem(MediaItem.Builder().setUri(uri).build())
            player.prepare()
            attachSurfaceIfReady()
            player.play()

        } catch (e: Exception) {
            Log.e(TAG, "SW retry failed", e)
            Toast.makeText(this, "Playback not supported: ${e.message}", Toast.LENGTH_LONG).show()
            finish()
        }
    }

    // ──────────────────────────────────────────────────────────────────────────
    //  Surface attachment
    // ──────────────────────────────────────────────────────────────────────────

    private fun attachSurfaceIfReady() {
        val player  = exoPlayer      ?: return
        val surface = pendingSurface ?: return
        if (!surfaceReady) return
        try {
            player.setVideoSurface(surface)
            Log.d(TAG, "Surface attached to player")
        } catch (e: Exception) {
            Log.e(TAG, "Failed to attach surface", e)
        }
    }

    // ──────────────────────────────────────────────────────────────────────────
    //  Controls visibility
    // ──────────────────────────────────────────────────────────────────────────

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
        if (isImage) return
        exoPlayer?.let { if (it.isPlaying) it.pause() else it.play() }
        scheduleHideControls()
    }

    private fun updatePlayPauseIcon() {
        binding.btnPlayPause.setImageResource(
            if (exoPlayer?.isPlaying == true) R.drawable.pause else R.drawable.ic_play_circle
        )
    }

    // ──────────────────────────────────────────────────────────────────────────
    //  Progress + camera
    // ──────────────────────────────────────────────────────────────────────────

    private fun updateProgress() {
        val player   = exoPlayer ?: return
        val duration = player.duration
        val position = player.currentPosition
        if (duration > 0 && !isDraggingSeek) {
            binding.seekBar.progress = (1000L * position / duration).toInt()
        }
        binding.tvCurrentTime.text = formatTime(position)
        if (duration > 0) binding.tvTotalTime.text = formatTime(duration)
    }

    /**
     * Merges gyro + touch into a single yaw/pitch for the renderer.
     * Called every frame (~60 fps) by [frameRunnable].
     */
    private fun updateCameraAngles() {
        val (resolvedYaw, resolvedPitch) = when (interaction) {
            InteractionMode.MOTION -> gyro.yaw   to gyro.pitch
            InteractionMode.TOUCH  -> touch.yaw  to touch.pitch
            // BOTH: gyroscope gives the base orientation, touch offsets on top
            InteractionMode.BOTH   -> (gyro.yaw + touch.yaw) to (gyro.pitch + touch.pitch)
        }
        renderer.yaw   = resolvedYaw
        renderer.pitch = resolvedPitch
    }

    // ──────────────────────────────────────────────────────────────────────────
    //  Cleanup
    // ──────────────────────────────────────────────────────────────────────────

    private fun releasePlayer() {
        try { exoPlayer?.release(); exoPlayer = null } catch (_: Exception) {}
    }

    // ──────────────────────────────────────────────────────────────────────────
    //  Utility
    // ──────────────────────────────────────────────────────────────────────────

    @SuppressLint("DefaultLocale")
    private fun formatTime(timeMs: Long): String {
        if (timeMs == C.TIME_UNSET || timeMs < 0) return "00:00"
        val s = (timeMs / 1_000) % 60
        val m = (timeMs / 60_000) % 60
        val h = timeMs / 3_600_000
        return if (h > 0) String.format("%d:%02d:%02d", h, m, s)
        else       String.format("%02d:%02d", m, s)
    }
}

//@OptIn(UnstableApi::class)
//class GalleryPlayerActivity : AppCompatActivity() {
//
//    private lateinit var binding: ActivityGalleryPlayerBinding
//
//    // ── Mode / flags ──────────────────────────────────────────────────────────
//    private lateinit var viewMode: GalleryViewMode
//    private lateinit var interaction: InteractionMode
//    private var repeat       = false
//    private var exitOnFinish = false
//    private var isImage      = false
//    private var videoUriStr: String? = null
//
//    // ── Player + GL ───────────────────────────────────────────────────────────
//    private var exoPlayer: ExoPlayer? = null
//    private lateinit var renderer: SphereRendererforgallery
//    private var surfaceReady   = false
//    private var pendingSurface: Surface? = null
//
//    // ── Controllers ───────────────────────────────────────────────────────────
//    private lateinit var gyro: GyroscopeController
//    private lateinit var touch: TouchController
//    private lateinit var audioManager: AudioManager
//
//    // ── Handlers ──────────────────────────────────────────────────────────────
//    private val frameHandler    = Handler(Looper.getMainLooper())
//    private val uiHandler       = Handler(Looper.getMainLooper())
//    private val progressHandler = Handler(Looper.getMainLooper())
//
//    private val TAG = "GalleryPlayer"
//
//    private var controlsVisible = true
//    private var isDraggingSeek  = false
//
//    // ── Zoom constants ────────────────────────────────────────────────────────
//    private val ZOOM_MIN  = 0.3f   // maximum zoom-in
//    private val ZOOM_MAX  = 1.5f   // maximum zoom-out
//    private val ZOOM_STEP = 0.1f   // per button tap
//
//    // ── Zoom repeat (hold button) ─────────────────────────────────────────────
//    private var zoomInJob:  Job? = null
//    private var zoomOutJob: Job? = null
//
//    companion object {
//        const val EXTRA_URI      = "gallery_uri"
//        const val EXTRA_IS_IMAGE = "gallery_is_image"
//        const val EXTRA_TITLE    = "gallery_title"
//    }
//
//    // ── Runnables ─────────────────────────────────────────────────────────────
//    private val hideControlsRunnable = Runnable { hideControls() }
//
//    private val progressRunnable = object : Runnable {
//        override fun run() {
//            updateProgress()
//            progressHandler.postDelayed(this, 500)
//        }
//    }
//
//    private val frameRunnable = object : Runnable {
//        override fun run() {
//            updateCameraAngles()
//            frameHandler.postDelayed(this, 16)
//        }
//    }
//
//    // ── SW-first codec selector ───────────────────────────────────────────────
//    private val softwareFirstSelector =
//        MediaCodecSelector { mimeType, requiresSecureDecoder, requiresTunnelingDecoder ->
//            val all = MediaCodecUtil.getDecoderInfos(
//                mimeType, requiresSecureDecoder, requiresTunnelingDecoder
//            )
//            val sw = all.filter { isSw(it.name) }
//            val hw = all.filter { !isSw(it.name) }
//            sw + hw
//        }
//
//    private fun isSw(name: String) =
//        name.lowercase().let {
//            it.startsWith("omx.google.") || it.startsWith("c2.android.")
//        }
//
//    // ──────────────────────────────────────────────────────────────────────────
//    //  Lifecycle
//    // ──────────────────────────────────────────────────────────────────────────
//
//    override fun onCreate(savedInstanceState: Bundle?) {
//        super.onCreate(savedInstanceState)
//
//        window.addFlags(
//            WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON or
//                    WindowManager.LayoutParams.FLAG_HARDWARE_ACCELERATED
//        )
//
//        binding = ActivityGalleryPlayerBinding.inflate(layoutInflater)
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
//        audioManager = getSystemService(Context.AUDIO_SERVICE) as AudioManager
//
//        parseIntent()
//        setupControllers()
//        setupGLSurface()
//        setupOverlayButtons()
//        setupSeekBar()
//        setupPanoramicControls()   // ← new
//        scheduleHideControls()
//
//        onBackPressedDispatcher.addCallback(this, object : OnBackPressedCallback(true) {
//            override fun handleOnBackPressed() = finish()
//        })
//
//        binding.tvVideoTitle.text = intent.getStringExtra(EXTRA_TITLE) ?: ""
//
//        // ── Rewind / Forward (video only) ──────────────────────────────────
//        binding.btnRewind.setOnClickListener {
//            exoPlayer?.seekTo(
//                (exoPlayer!!.currentPosition - 10_000L).coerceAtLeast(0L)
//            )
//            scheduleHideControls()
//        }
//        binding.btnForward.setOnClickListener {
//            val dur = exoPlayer?.duration ?: 0L
//            exoPlayer?.seekTo(
//                (exoPlayer!!.currentPosition + 10_000L).coerceAtMost(dur)
//            )
//            scheduleHideControls()
//        }
//
//        // ── Route to image or video path ───────────────────────────────────
//        if (isImage) {
//            setupForImage()
//        } else {
//            setupForVideo()
//        }
//    }
//
//    override fun onResume() {
//        super.onResume()
//        binding.glSurface.onResume()
//        if (!isImage) {
//            if (interaction == InteractionMode.MOTION ||
//                interaction == InteractionMode.BOTH
//            ) gyro.start()
//            frameHandler.post(frameRunnable)
//            progressHandler.post(progressRunnable)
//            exoPlayer?.play()
//        } else {
//            if (interaction == InteractionMode.MOTION ||
//                interaction == InteractionMode.BOTH
//            ) gyro.start()
//            frameHandler.post(frameRunnable)
//        }
//    }
//
//    override fun onPause() {
//        frameHandler.removeCallbacks(frameRunnable)
//        progressHandler.removeCallbacks(progressRunnable)
//        uiHandler.removeCallbacks(hideControlsRunnable)
//        gyro.stop()
//        exoPlayer?.pause()
//        binding.glSurface.onPause()
//        zoomInJob?.cancel()
//        zoomOutJob?.cancel()
//        super.onPause()
//    }
//
//    override fun onDestroy() {
//        releasePlayer()
//        super.onDestroy()
//    }
//
//    override fun onTouchEvent(event: MotionEvent): Boolean {
//        if (event.action == MotionEvent.ACTION_DOWN) toggleControls()
//        if (interaction == InteractionMode.TOUCH ||
//            interaction == InteractionMode.BOTH
//        ) {
//            if (touch.onTouchEvent(event)) return true
//        }
//        return super.onTouchEvent(event)
//    }
//
//    // ──────────────────────────────────────────────────────────────────────────
//    //  Parse intent
//    // ──────────────────────────────────────────────────────────────────────────
//
//    private fun parseIntent() {
//        viewMode = GalleryViewMode.valueOf(
//            intent.getStringExtra(IntentKeys.VIEW_MODE) ?: GalleryViewMode.DEVICE.name
//        )
//        interaction = InteractionMode.valueOf(
//            intent.getStringExtra(IntentKeys.INTERACTION) ?: InteractionMode.TOUCH.name
//        )
//        repeat       = intent.getBooleanExtra(IntentKeys.REPEAT, false)
//        exitOnFinish = intent.getBooleanExtra(IntentKeys.EXIT_ON_FINISH, false)
//        videoUriStr  = intent.getStringExtra(EXTRA_URI)
//        isImage      = intent.getBooleanExtra(EXTRA_IS_IMAGE, false)
//    }
//
//    // ──────────────────────────────────────────────────────────────────────────
//    //  Setup
//    // ──────────────────────────────────────────────────────────────────────────
//
//    private fun setupControllers() {
//        gyro  = GyroscopeController(this)
//        touch = TouchController()
//    }
//
//    private fun setupGLSurface() {
//        binding.glSurface.setEGLContextClientVersion(2)
//
//        renderer = SphereRendererforgallery(viewMode) { surface ->
//            runOnUiThread {
//                surfaceReady   = true
//                pendingSurface = surface
//                if (!isImage) attachSurfaceIfReady()
//            }
//        }
//
//        binding.glSurface.setRenderer(renderer)
//        binding.glSurface.renderMode = GLSurfaceView.RENDERMODE_CONTINUOUSLY
//    }
//
//    private fun setupOverlayButtons() {
//        binding.btnBack.setOnClickListener { finish() }
//
//        binding.btnRecentre.setOnClickListener {
//            gyro.reset()
//            touch.reset()
//            Toast.makeText(this, "View re-centred", Toast.LENGTH_SHORT).show()
//        }
//
//        binding.btnPlayPause.setOnClickListener { togglePlayPause() }
//    }
//
//    private fun setupSeekBar() {
//        binding.seekBar.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
//            override fun onProgressChanged(sb: SeekBar, progress: Int, fromUser: Boolean) {
//                if (fromUser) {
//                    val pos = (exoPlayer?.duration ?: 0L) * progress / 1000L
//                    binding.tvCurrentTime.text = formatTime(pos)
//                }
//            }
//            override fun onStartTrackingTouch(sb: SeekBar) { isDraggingSeek = true }
//            override fun onStopTrackingTouch(sb: SeekBar) {
//                isDraggingSeek = false
//                val pos = (exoPlayer?.duration ?: 0L) * sb.progress / 1000L
//                exoPlayer?.seekTo(pos)
//                scheduleHideControls()
//            }
//        })
//    }
//
//    // ──────────────────────────────────────────────────────────────────────────
//    //  Panoramic controls (zoom-in, zoom-out, re-centre) — image + PANORAMIC only
//    // ──────────────────────────────────────────────────────────────────────────
//
//    private fun setupPanoramicControls() {
//        // Only show panoramic control group for panoramic image mode
//        val isPanoramic = viewMode == GalleryViewMode.PANORAMIC && isImage
//        binding.panoramicControlsGroup.visibility =
//            if (isPanoramic) View.VISIBLE else View.GONE
//
//        if (!isPanoramic) return
//
//        // ── Re-centre ─────────────────────────────────────────────────────
//        binding.btnPanoRecentre.setOnClickListener {
//            gyro.reset()
//            touch.reset()
//            renderer.zoom = 1.0f
//            Toast.makeText(this, "View re-centred", Toast.LENGTH_SHORT).show()
//            scheduleHideControls()
//        }
//
//        // ── Zoom In (tap + hold) ───────────────────────────────────────────
//        binding.btnZoomIn.setOnTouchListener { _, event ->
//            when (event.action) {
//                MotionEvent.ACTION_DOWN -> {
//                    applyZoom(-ZOOM_STEP)   // immediate first tick
//                    zoomInJob = CoroutineScope(Dispatchers.Main).launch {
//                        delay(400)          // hold delay before repeat
//                        while (isActive) {
//                            applyZoom(-ZOOM_STEP)
//                            delay(100)
//                        }
//                    }
//                    scheduleHideControls()
//                    true
//                }
//                MotionEvent.ACTION_UP,
//                MotionEvent.ACTION_CANCEL -> {
//                    zoomInJob?.cancel()
//                    zoomInJob = null
//                    true
//                }
//                else -> false
//            }
//        }
//
//        // ── Zoom Out (tap + hold) ──────────────────────────────────────────
//        binding.btnZoomOut.setOnTouchListener { _, event ->
//            when (event.action) {
//                MotionEvent.ACTION_DOWN -> {
//                    applyZoom(ZOOM_STEP)    // immediate first tick
//                    zoomOutJob = CoroutineScope(Dispatchers.Main).launch {
//                        delay(400)
//                        while (isActive) {
//                            applyZoom(ZOOM_STEP)
//                            delay(100)
//                        }
//                    }
//                    scheduleHideControls()
//                    true
//                }
//                MotionEvent.ACTION_UP,
//                MotionEvent.ACTION_CANCEL -> {
//                    zoomOutJob?.cancel()
//                    zoomOutJob = null
//                    true
//                }
//                else -> false
//            }
//        }
//    }
//
//    /** Adjusts renderer zoom and clamps to [ZOOM_MIN, ZOOM_MAX]. */
//    private fun applyZoom(delta: Float) {
//        renderer.zoom = (renderer.zoom + delta).coerceIn(ZOOM_MIN, ZOOM_MAX)
//    }
//
//    // ──────────────────────────────────────────────────────────────────────────
//    //  Image mode
//    // ──────────────────────────────────────────────────────────────────────────
//
//    private fun setupForImage() {
//        binding.seekBar.visibility        = View.GONE
//        binding.btnRewind.visibility      = View.GONE
//        binding.btnForward.visibility     = View.GONE
//        binding.tvCurrentTime.visibility  = View.GONE
//        binding.tvTotalTime.visibility    = View.GONE
//        binding.btnPlayPause.visibility   = View.GONE
//        binding.loadingOverlay.visibility = View.VISIBLE
//
//        val uriStr = videoUriStr
//        if (uriStr.isNullOrEmpty()) {
//            Toast.makeText(this, "No image URI provided", Toast.LENGTH_LONG).show()
//            finish()
//            return
//        }
//
//        val uri = Uri.parse(uriStr)
//
//        Thread {
//            try {
//                val bitmap: Bitmap = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
//                    val source = ImageDecoder.createSource(contentResolver, uri)
//                    ImageDecoder.decodeBitmap(source) { decoder, _, _ ->
//                        decoder.allocator = ImageDecoder.ALLOCATOR_SOFTWARE
//                    }
//                } else {
//                    @Suppress("DEPRECATION")
//                    MediaStore.Images.Media.getBitmap(contentResolver, uri)
//                }
//
//                runOnUiThread {
//                    binding.loadingOverlay.visibility = View.GONE
//                }
//
//                binding.glSurface.queueEvent {
//                    renderer.setImageBitmap(bitmap)
//                }
//
//            } catch (e: Exception) {
//                Log.e(TAG, "Image decode failed", e)
//                runOnUiThread {
//                    binding.loadingOverlay.visibility = View.GONE
//                    Toast.makeText(
//                        this,
//                        "Failed to load image: ${e.message}",
//                        Toast.LENGTH_LONG
//                    ).show()
//                    finish()
//                }
//            }
//        }.start()
//    }
//
//    // ──────────────────────────────────────────────────────────────────────────
//    //  Video mode
//    // ──────────────────────────────────────────────────────────────────────────
//
//    private fun setupForVideo() {
//        val uriStr = videoUriStr
//        if (uriStr.isNullOrEmpty()) {
//            Toast.makeText(this, "No video URI provided", Toast.LENGTH_LONG).show()
//            finish()
//            return
//        }
//
//        val uri = Uri.parse(uriStr)
//
//        try {
//            val renderersFactory = DefaultRenderersFactory(this).apply {
//                setExtensionRendererMode(
//                    DefaultRenderersFactory.EXTENSION_RENDERER_MODE_PREFER
//                )
//                setMediaCodecSelector(softwareFirstSelector)
//            }
//
//            val mediaSourceFactory = DefaultMediaSourceFactory(this)
//            val mediaItem = MediaItem.Builder().setUri(uri).build()
//
//            val player = ExoPlayer.Builder(this, renderersFactory)
//                .setMediaSourceFactory(mediaSourceFactory)
//                .build()
//                .also { exoPlayer = it }
//
//            if (uri.scheme == "content") {
//                try {
//                    contentResolver.takePersistableUriPermission(
//                        uri, android.content.Intent.FLAG_GRANT_READ_URI_PERMISSION
//                    )
//                } catch (e: SecurityException) {
//                    Log.w(TAG, "takePersistableUriPermission non-fatal: ${e.message}")
//                }
//            }
//
//            player.repeatMode    = if (repeat) Player.REPEAT_MODE_ONE else Player.REPEAT_MODE_OFF
//            player.playWhenReady = true
//
//            player.addListener(object : Player.Listener {
//
//                override fun onPlaybackStateChanged(state: Int) {
//                    runOnUiThread {
//                        binding.loadingOverlay.visibility =
//                            if (state == Player.STATE_BUFFERING) View.VISIBLE else View.GONE
//                    }
//                    when (state) {
//                        Player.STATE_READY -> {
//                            binding.tvTotalTime.text = formatTime(player.duration)
//                            binding.seekBar.max = 1000
//                            updatePlayPauseIcon()
//                        }
//                        Player.STATE_ENDED -> {
//                            if (exitOnFinish) finish()
//                            else { player.seekTo(0); player.play() }
//                        }
//                        else -> {}
//                    }
//                }
//
//                override fun onIsPlayingChanged(isPlaying: Boolean) {
//                    updatePlayPauseIcon()
//                }
//
//                override fun onPlayerError(error: PlaybackException) {
//                    Log.e(TAG, "Player error [${error.errorCode}]", error)
//                    if (error.errorCode == PlaybackException.ERROR_CODE_DECODER_INIT_FAILED ||
//                        error.errorCode == PlaybackException.ERROR_CODE_DECODING_FAILED
//                    ) {
//                        retryWithSoftwareDecoder(uri)
//                        return
//                    }
//                    val msg = when (error.errorCode) {
//                        PlaybackException.ERROR_CODE_IO_FILE_NOT_FOUND ->
//                            "File not found. It may have been moved or deleted."
//                        PlaybackException.ERROR_CODE_IO_NO_PERMISSION ->
//                            "Permission denied. Re-pick the file from gallery."
//                        else -> "Playback error (${error.errorCode}): ${error.message}"
//                    }
//                    Toast.makeText(this@GalleryPlayerActivity, msg, Toast.LENGTH_LONG).show()
//                    finish()
//                }
//
//                override fun onVideoSizeChanged(videoSize: VideoSize) {
//                    Log.d(TAG, "Video size: ${videoSize.width}×${videoSize.height}")
//                }
//
//                override fun onRenderedFirstFrame() {
//                    Log.d(TAG, "✓ First frame rendered")
//                }
//            })
//
//            player.setMediaItem(mediaItem)
//            player.prepare()
//            attachSurfaceIfReady()
//
//        } catch (e: Exception) {
//            Log.e(TAG, "setupForVideo failed", e)
//            Toast.makeText(
//                this, "Failed to init player: ${e.message}", Toast.LENGTH_LONG
//            ).show()
//            finish()
//        }
//    }
//
//    // ──────────────────────────────────────────────────────────────────────────
//    //  SW decoder fallback
//    // ──────────────────────────────────────────────────────────────────────────
//
//    private fun retryWithSoftwareDecoder(uri: Uri) {
//        Log.w(TAG, "Retrying with SW-only decoder")
//        releasePlayer()
//
//        try {
//            val swSelector = MediaCodecSelector { mimeType, requiresSecure, requiresTunneling ->
//                MediaCodecUtil.getDecoderInfos(mimeType, requiresSecure, requiresTunneling)
//                    .filter { isSw(it.name) }
//                    .ifEmpty {
//                        MediaCodecUtil.getDecoderInfos(
//                            mimeType, requiresSecure, requiresTunneling
//                        )
//                    }
//            }
//
//            val rf = DefaultRenderersFactory(this).apply {
//                setExtensionRendererMode(
//                    DefaultRenderersFactory.EXTENSION_RENDERER_MODE_PREFER
//                )
//                setMediaCodecSelector(swSelector)
//            }
//
//            val player = ExoPlayer.Builder(this, rf)
//                .setMediaSourceFactory(DefaultMediaSourceFactory(this))
//                .build()
//                .also { exoPlayer = it }
//
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
//                        this@GalleryPlayerActivity,
//                        "Cannot play this file on your device.",
//                        Toast.LENGTH_LONG
//                    ).show()
//                    finish()
//                }
//            })
//
//            player.setMediaItem(MediaItem.Builder().setUri(uri).build())
//            player.prepare()
//            attachSurfaceIfReady()
//            player.play()
//
//        } catch (e: Exception) {
//            Log.e(TAG, "SW retry failed", e)
//            Toast.makeText(
//                this, "Playback not supported: ${e.message}", Toast.LENGTH_LONG
//            ).show()
//            finish()
//        }
//    }
//
//    // ──────────────────────────────────────────────────────────────────────────
//    //  Surface attachment
//    // ──────────────────────────────────────────────────────────────────────────
//
//    private fun attachSurfaceIfReady() {
//        val player  = exoPlayer      ?: return
//        val surface = pendingSurface ?: return
//        if (!surfaceReady) return
//        try {
//            player.setVideoSurface(surface)
//            Log.d(TAG, "Surface attached to player")
//        } catch (e: Exception) {
//            Log.e(TAG, "Failed to attach surface", e)
//        }
//    }
//
//    // ──────────────────────────────────────────────────────────────────────────
//    //  Controls
//    // ──────────────────────────────────────────────────────────────────────────
//
//    private fun toggleControls() {
//        if (controlsVisible) hideControls() else showControls()
//    }
//
//    private fun showControls() {
//        controlsVisible = true
//        binding.controlsOverlay.visibility = View.VISIBLE
//        binding.controlsOverlay.animate().alpha(1f).setDuration(200).start()
//        scheduleHideControls()
//    }
//
//    private fun hideControls() {
//        controlsVisible = false
//        binding.controlsOverlay.animate().alpha(0f).setDuration(300).withEndAction {
//            binding.controlsOverlay.visibility = View.GONE
//        }.start()
//    }
//
//    private fun scheduleHideControls() {
//        uiHandler.removeCallbacks(hideControlsRunnable)
//        uiHandler.postDelayed(hideControlsRunnable, 3000)
//    }
//
//    private fun togglePlayPause() {
//        if (isImage) return
//        exoPlayer?.let { if (it.isPlaying) it.pause() else it.play() }
//        scheduleHideControls()
//    }
//
//    private fun updatePlayPauseIcon() {
//        val isPlaying = exoPlayer?.isPlaying == true
//        binding.btnPlayPause.setImageResource(
//            if (isPlaying) R.drawable.pause else R.drawable.ic_play_circle
//        )
//    }
//
//    // ──────────────────────────────────────────────────────────────────────────
//    //  Progress / camera
//    // ──────────────────────────────────────────────────────────────────────────
//
//    private fun updateProgress() {
//        val player   = exoPlayer ?: return
//        val duration = player.duration
//        val position = player.currentPosition
//        if (duration > 0 && !isDraggingSeek) {
//            binding.seekBar.progress = (1000L * position / duration).toInt()
//        }
//        binding.tvCurrentTime.text = formatTime(position)
//        if (duration > 0) binding.tvTotalTime.text = formatTime(duration)
//    }
//
//    private fun updateCameraAngles() {
//        val (yaw, pitch) = when (interaction) {
//            InteractionMode.MOTION -> gyro.yaw   to gyro.pitch
//            InteractionMode.TOUCH  -> touch.yaw  to touch.pitch
//            InteractionMode.BOTH   -> (gyro.yaw  + touch.yaw) to (gyro.pitch + touch.pitch)
//        }
//        renderer.yaw   = yaw
//        renderer.pitch = pitch
//    }
//
//    // ──────────────────────────────────────────────────────────────────────────
//    //  Release
//    // ──────────────────────────────────────────────────────────────────────────
//
//    private fun releasePlayer() {
//        try {
//            exoPlayer?.release()
//            exoPlayer = null
//        } catch (_: Exception) {}
//    }
//
//    // ──────────────────────────────────────────────────────────────────────────
//    //  Utility
//    // ──────────────────────────────────────────────────────────────────────────
//
//    @SuppressLint("DefaultLocale")
//    private fun formatTime(timeMs: Long): String {
//        if (timeMs == C.TIME_UNSET || timeMs < 0) return "00:00"
//        val s = (timeMs / 1_000) % 60
//        val m = (timeMs / 60_000) % 60
//        val h = timeMs / 3_600_000
//        return if (h > 0) String.format("%d:%02d:%02d", h, m, s)
//        else String.format("%02d:%02d", m, s)
//    }
//}
