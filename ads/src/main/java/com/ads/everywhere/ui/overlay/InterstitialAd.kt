package com.ads.everywhere.ui.overlay

import android.content.Context
import android.content.Intent
import android.graphics.PixelFormat
import android.net.Uri
import android.view.Gravity
import android.view.View
import android.view.WindowManager
import com.ads.everywhere.Analytics
import com.ads.everywhere.R
import com.ads.everywhere.data.models.InterstitialType
import com.ads.everywhere.util.Logs
import com.ads.everywhere.util.OutsideTouchListener
import com.ads.everywhere.util.ScreenMetricsCompat


class InterstitialAd(
    private val context: Context,
    private val type: InterstitialType,
    private val callback: OverlayCallback
) : OverlayView(context) {
    companion object {
        const val TAG = "AD_ACTIVITY"
    }

    override val layoutRes: Int
        get() = type.toRes()
    override val params: WindowManager.LayoutParams
        get() = WindowManager.LayoutParams().apply {
            width = ScreenMetricsCompat.screenSize(context).width
            height = WindowManager.LayoutParams.WRAP_CONTENT
            x = 0
            y = 0
            gravity = Gravity.CENTER
            type = WindowManager.LayoutParams.TYPE_ACCESSIBILITY_OVERLAY
            format = PixelFormat.TRANSPARENT
            flags = WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS or
                    WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN or
                    WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE or
                    WindowManager.LayoutParams.FLAG_WATCH_OUTSIDE_TOUCH
        }

    override fun onViewCreated(view: View) {
        Analytics.init(context)
        Analytics.sendEvent(Analytics.SHOW_INTERSTITIAL)

        val button = view.findViewById<View>(R.id.button)
        button.setOnClickListener {
            onAdClicked()
            Analytics.sendEvent(Analytics.CLICK_INTERSTITIAL)
            hide()
        }

        val cross = view.findViewById<View>(R.id.cross)
        cross.setOnClickListener { hide() }

        val root = view.findViewById<View>(R.id.root)
        root.setOnTouchListener(OutsideTouchListener { hide() })
    }

    override fun onViewDestroyed() {
        callback.onViewDestroyed()
    }

    private fun onAdClicked() {
        try {
            val intent = Intent(Intent.ACTION_VIEW, Uri.parse(type.toUrl())).apply {
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            }
            context.startActivity(intent)
            Logs.log(TAG, "interstitial ad| onAdClicked")
        } catch (ex: Exception) {
            ex.printStackTrace()
            Analytics.reportException("interstitial ad| click exception", ex)
        }
    }

}