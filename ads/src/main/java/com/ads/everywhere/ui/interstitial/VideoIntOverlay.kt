package com.ads.everywhere.ui.interstitial

import android.content.Context
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import android.widget.VideoView
import androidx.core.net.toUri
import com.ads.everywhere.Analytics
import com.ads.everywhere.R
import com.ads.everywhere.data.models.MyVideo
import com.ads.everywhere.data.repository.AppInfoRepository
import com.ads.everywhere.ui.overlay.OverlayCallback
import com.ads.everywhere.util.Logs


class VideoIntOverlay(
    private val context: Context,
    private val packageName: String?,
    private val callback: OverlayCallback,
    private val video: MyVideo
) : BaseIntOverlay(context){
    private val repository = AppInfoRepository(context)

    companion object{
        const val TAG = "VIDEO_SERVICE"
    }

    override fun onViewCreated(view: View) {
        Logs.log(TAG, "show video ($packageName)")
        Analytics.sendEvent(Analytics.SHOW_VIDEO_INTERSTITIAL)
        draw(view)
    }

    override fun onViewDestroyed() {
        callback.onViewDestroyed()
    }

    private fun draw(view:View){
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
            hide()
        }
        view.findViewById<View>(R.id.back).setOnClickListener {
            hide()
        }

        val videoView = view.findViewById<VideoView>(R.id.video).apply {
            setVideoURI(video.file.toUri())
            requestFocus()
            start()
        }
        videoView.setOnClickListener {
            Analytics.sendEvent(Analytics.CLICK_VIDEO_INTERSTITIAL)
            showUrl(video.clickUrl)
            hide()
        }
    }

    override val layoutRes: Int
        get() = R.layout.interstitial_video
}