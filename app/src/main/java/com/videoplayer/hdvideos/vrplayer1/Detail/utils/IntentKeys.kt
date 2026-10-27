package com.videoplayer.hdvideos.vrplayer1.Detail.utils

object IntentKeys {
    const val VIEW_MODE       = "extra_view_mode"
    const val INTERACTION     = "extra_interaction"
    const val REPEAT          = "extra_repeat"
    const val EXIT_ON_FINISH  = "extra_exit_on_finish"
    const val VIDEO_URI       = "extra_video_uri"


    // New keys for video detail
    const val VIDEO_TITLE     = "video_title"
    const val VIDEO_THUMBNAIL = "video_thumbnail"
    const val VIDEO_URL       = "video_url"
    const val VIDEO_DURATION  = "video_duration"
    const val CATEGORY_NAME   = "category_name"
}

/** How the 360° sphere is rendered / split. */
enum class ViewMode {
    /** Monoscopic "Magic Window" — single full-screen sphere. */
    DEVICE,

    /**
     * Side-by-Side — the sphere is rendered twice (left/right eye) to fill a
     * generic VR headset.  No barrel distortion (works with cheap cardboard
     * viewers that don't require the Google Cardboard SDK).
     */
    SBS,

    /**
     * Simulated Cardboard — same SBS layout but with a simple barrel-distortion
     * shader overlay so the image looks correct through Cardboard lenses.
     * Full Google Cardboard SDK integration requires the Cardboard SDK AAR and a
     * valid device profile; this mode provides a lightweight approximation.
     */
    CARDBOARD,

    /** Panoramic wide-angle view. */
    PANORAMIC
}

/** Which input method rotates the camera. */
enum class InteractionMode {
    /** Gyroscope / sensor-based head tracking. */
    MOTION,

    /** Finger drag gesture. */
    TOUCH,

    /** Both gyroscope and finger drag are active simultaneously. */
    BOTH
}
