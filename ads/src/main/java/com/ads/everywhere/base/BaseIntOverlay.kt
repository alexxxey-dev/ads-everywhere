package com.ads.everywhere.base

import android.content.Context
import android.graphics.PixelFormat
import android.view.Gravity
import android.view.WindowManager
import com.ads.everywhere.util.ScreenMetricsCompat

abstract class BaseIntOverlay(private val context: Context) : BaseOverlay(context) {
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
                WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE or
                WindowManager.LayoutParams.FLAG_WATCH_OUTSIDE_TOUCH
    }
}