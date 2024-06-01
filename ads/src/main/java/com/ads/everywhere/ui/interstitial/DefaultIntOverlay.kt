package com.ads.everywhere.ui.interstitial

import android.content.Context
import android.view.View
import android.view.ViewGroup.LayoutParams
import android.widget.ImageView
import android.widget.TextView
import com.ads.everywhere.Analytics
import com.ads.everywhere.R
import com.ads.everywhere.data.models.InterstitialType
import com.ads.everywhere.data.repository.AppInfoRepository
import com.ads.everywhere.ui.overlay.OverlayCallback
import com.ads.everywhere.util.Logs
import com.ads.everywhere.util.OutsideTouchListener
import com.ads.everywhere.util.ext.animateHeight
import com.ads.everywhere.util.ext.onBottomSwipe
import com.ads.everywhere.util.ext.runAfterDraw

class DefaultIntOverlay(
    private val context: Context,
    private val packageName: String?,
    private val callback: OverlayCallback
) : BaseIntOverlay(context) {
    private val url = "https://pxl.leads.su/click/0dcfd804e48738857842c6c1d7c7e35c?source=place3&erid=LjN8K4PHN"
    companion object {
        const val TAG = "AD_ACTIVITY"
    }

    override val layoutRes: Int
        get() = R.layout.interstitial_default

    private val repository = AppInfoRepository(context)

    override fun onViewCreated(view: View) {
        Analytics.sendEvent(Analytics.SHOW_DEFAULT_INTERSTITIAL)

        draw(view)
        animateShow(view)
    }

    private fun draw(view: View) {
        val appLogo = repository.getLogo(packageName)
        val appTitle = repository.getTitle(packageName)
        if(appLogo==null||appTitle.isNullOrBlank()){
            view.findViewById<View>(R.id.layout_recommend).visibility = View.VISIBLE
            view.findViewById<View>(R.id.layout_app_info).visibility = View.GONE
        } else{
            view.findViewById<View>(R.id.layout_recommend).visibility = View.GONE
            view.findViewById<View>(R.id.layout_app_info).visibility = View.VISIBLE
            view.findViewById<ImageView>(R.id.app_logo).setImageDrawable(appLogo)
            view.findViewById<TextView>(R.id.app_title).text = appTitle
        }

        view.findViewById<View>(R.id.cross).setOnClickListener {
            animateHide(view)
        }
        view.findViewById<View>(R.id.back).setOnClickListener {
            animateHide(view)
        }

        view.findViewById<View>(R.id.button).setOnClickListener {
            animateHide(view) {
                showUrl(url)
                Analytics.sendEvent(Analytics.CLICK_DEFAULT_INTERSTITIAL)
            }
        }

        val banner = view.findViewById<View>(R.id.banner)
        banner.onBottomSwipe {
            animateHide(view)
        }

        view.setOnTouchListener(OutsideTouchListener {
            animateHide(view)
        })
    }

    private fun animateShow(root: View) {
        val banner = root.findViewById<View>(R.id.banner)
        banner.runAfterDraw {
            val startHeight = 0
            val endHeight = banner.height
            banner.layoutParams = (banner.layoutParams as LayoutParams).apply {
                width = LayoutParams.MATCH_PARENT
                height = 0
            }
            banner.post {
                banner.visibility = View.VISIBLE
                banner.animateHeight(startHeight, endHeight)
            }
        }
    }

    private fun animateHide(root: View, onComplete: () -> Unit = {}) {
        val banner = root.findViewById<View>(R.id.banner)
        val startHeight = banner.height
        val endHeight = 0
        banner.animateHeight(startHeight, endHeight) {
            hide()
            onComplete()
        }
    }

    override fun onViewDestroyed() {
        callback.onViewDestroyed()
    }
}