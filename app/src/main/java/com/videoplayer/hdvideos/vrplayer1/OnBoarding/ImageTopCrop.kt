package com.videoplayer.hdvideos.vrplayer1.OnBoarding

import android.graphics.Matrix
import android.widget.ImageView

internal fun ImageView.topCrop() {
    scaleType = ImageView.ScaleType.MATRIX
    val apply = {
        val d = drawable
        if (d != null && width > 0 && height > 0 && d.intrinsicWidth > 0 && d.intrinsicHeight > 0) {
            val scale = maxOf(
                width.toFloat() / d.intrinsicWidth,
                height.toFloat() / d.intrinsicHeight
            )
            imageMatrix = Matrix().apply {
                setScale(scale, scale)
                postTranslate((width - d.intrinsicWidth * scale) / 2f, 0f)
            }
        }
    }
    addOnLayoutChangeListener { _, _, _, _, _, _, _, _, _ -> apply() }
    post(apply)
}
