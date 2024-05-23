package com.ads.everywhere.ui.overlay.interstitial

import android.content.Context
import android.graphics.PixelFormat
import android.view.Gravity
import android.view.WindowManager
import com.ads.everywhere.ui.overlay.OverlayView
import com.ads.everywhere.util.ScreenMetricsCompat
import com.ads.everywhere.util.ext.statusBarHeight

abstract class BaseInt(private val context: Context) : OverlayView(context) {
    override val params = WindowManager.LayoutParams().apply {
        width = ScreenMetricsCompat.screenSize(context).width
        height = ScreenMetricsCompat.screenSize(context).height
        x = 0
        y = 0
        gravity = Gravity.TOP
        type = WindowManager.LayoutParams.TYPE_ACCESSIBILITY_OVERLAY
        format = PixelFormat.TRANSPARENT
        flags = WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN or
                WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS or
                WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE
    }
}