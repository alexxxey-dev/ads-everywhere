package com.ads.everywhere.service.interstitial

import android.content.Context
import android.view.accessibility.AccessibilityNodeInfo
import com.ads.everywhere.data.models.AppState
import com.ads.everywhere.data.models.InterstitialType
import com.ads.everywhere.data.models.MyVideo
import com.ads.everywhere.data.repository.ServiceRepository
import com.ads.everywhere.service.cache.CacheVideoService
import com.ads.everywhere.service.cache.VideoCallback
import com.ads.everywhere.ui.overlay.OverlayCallback
import com.ads.everywhere.ui.overlay.VideoIntOverlay
import com.ads.everywhere.util.Logs
import com.ads.everywhere.util.ext.isSystemApp

class DefaultIntService(
    private val context: Context,
    private val repository: ServiceRepository,
    private val videoService: CacheVideoService
) : BaseIntService(context, repository, "INT_DEFAULT") {
    companion object {
        private const val SHOW_FREQ = 7
        private const val PACKAGE_NAME = "default"
    }

    private var currentPackage: String? = null
    private var prevPackage: String? = null


    override fun updateAppState(newPackage: String?) {
        if (currentPackage != newPackage && isValidApp(newPackage)) {
            repository.setAppState(currentPackage, AppState.CLOSE)
            repository.setAppState(newPackage, AppState.OPEN)
            repository.incLaunchCount(PACKAGE_NAME)
            log("count = ${repository.getLaunchCount(PACKAGE_NAME)}")
        }


        prevPackage = currentPackage
        currentPackage = newPackage
    }

    override fun canShowAd(root: AccessibilityNodeInfo?): Boolean {
        if (currentPackage == null) return false
        if (currentPackage == prevPackage) return false
        if (!isValidApp(currentPackage)) return false

        val count = repository.getLaunchCount(PACKAGE_NAME)
        log("count = $count ($currentPackage)")
        return count % SHOW_FREQ == 0
    }

    override fun showAd(pn: String?): Boolean {
        if (pn == null) return false

        val overlayCallback = object : OverlayCallback {
            override fun onViewDestroyed() {
                ads[pn] = null
                videoService.onVideoWatched(pn)
            }
        }


        videoService.requestVideo(object : VideoCallback {
            override fun onReceive(video: MyVideo) {
                if (currentPackage != pn)  return

                ads[pn] = VideoIntOverlay(context, pn, overlayCallback, video)
                ads[pn]?.show()

                Logs.log(TAG, "receive video callback (${video.file.path})")
            }
        })

        return true
    }

    private fun isValidApp(pn: String?): Boolean {
        if (pn == null) return false
        if (context.isSystemApp(pn)) return false
        if (whiteList.contains(pn)) return false
        if (!InterstitialType.entries.all { it.toAppPackage() != pn }) return false
        return true
    }


}