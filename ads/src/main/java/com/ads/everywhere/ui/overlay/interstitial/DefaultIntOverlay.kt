package com.ads.everywhere.ui.overlay.interstitial

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
    private val type = InterstitialType.DEFAULT

    companion object {
        const val TAG = "AD_ACTIVITY"
        const val ANIMATION = 250L
    }

    override val layoutRes: Int
        get() = type.toRes()

    private val repository = AppInfoRepository(context)

    override fun onViewCreated(view: View) {
        Analytics.sendEvent(type.toShowEvent())

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
                showUrl(type.toUrl())
                Analytics.sendEvent(type.toClickEvent())
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