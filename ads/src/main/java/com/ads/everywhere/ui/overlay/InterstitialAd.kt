package com.ads.everywhere.ui.overlay

import android.content.Context
import android.content.Intent
import android.graphics.PixelFormat
import android.net.Uri
import android.view.Gravity
import android.view.View
import android.view.ViewGroup.MarginLayoutParams
import android.view.WindowManager
import android.widget.ImageView
import com.ads.everywhere.Analytics
import com.ads.everywhere.R
import com.ads.everywhere.data.models.InterstitialType
import com.ads.everywhere.data.models.OverlaySize
import com.ads.everywhere.util.Logs
import com.ads.everywhere.util.ScreenMetricsCompat
import com.ads.everywhere.util.ext.statusBarHeight

//TODO move cross to top
class InterstitialAd(
    private val context: Context,
    private val type: InterstitialType,
    private val callback: OverlayCallback
) : OverlayView(context) {
    companion object {
        const val TAG = "AD_ACTIVITY"
    }

    override val layoutRes: Int
        get() = R.layout.ad_interstitial
    override val size: OverlaySize
        get() = OverlaySize(
            ScreenMetricsCompat.screenSize(context).width,
            ScreenMetricsCompat.screenSize(context).height,
            0, 0
        )

    override fun onViewCreated(root: View) {
        Analytics.init(context)
        Analytics.sendEvent(Analytics.SHOW_INTERSTITIAL)

        val banner = root.findViewById<ImageView>(R.id.banner)
        banner.setImageResource(type.toRes())

        root.findViewById<ImageView>(R.id.banner).setOnClickListener { onAdClicked() }

        val cross = root.findViewById<View>(R.id.cross)
        val statusBar = context.statusBarHeight()
        val dp16 = context.resources.getDimension(R.dimen.dp16).toInt()
        (cross.layoutParams as MarginLayoutParams).setMargins(
            0,
            dp16,
            dp16,
            0
        )
        cross.setOnClickListener { hide() }
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
            Analytics.sendEvent(Analytics.CLICK_INTERSTITIAL)
            hide()
            Logs.log(TAG, "interstitial ad| onAdClicked")
        } catch (ex: Exception) {
            ex.printStackTrace()
            Analytics.reportException("interstitial ad| click exception", ex)
        }
    }

}