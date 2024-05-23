package com.ads.everywhere.data.service.interstitial

import android.content.Context
import android.view.accessibility.AccessibilityNodeInfo
import com.ads.everywhere.data.models.AppState
import com.ads.everywhere.data.models.InterstitialType
import com.ads.everywhere.data.repository.ServiceRepository
import com.ads.everywhere.data.service.base.BaseIntService
import com.ads.everywhere.ui.overlay.interstitial.PersonalInt
import com.ads.everywhere.ui.overlay.OverlayCallback

class PersonalIntService(
    private val context: Context,
    private val repository: ServiceRepository,
    private val type: InterstitialType
) : BaseIntService(context, repository, type) {
    private val appPackage = type.toAppPackage()

    override fun canShowAd(root: AccessibilityNodeInfo?): Boolean {
        val count = repository.getLaunchCount(appPackage)
        log("count = $count ($appPackage)")
        if (count % SHOW_FREQ != 0) return false

        if(root==null) return false
        val id = type.toAcsbView() ?: return false
        val isMainScreen = root.findAccessibilityNodeInfosByViewId(id).any { it.isVisibleToUser }
        log("main = $isMainScreen ($appPackage)")
        return isMainScreen
    }

    override fun updateAppState(newPackage: String?) {
        if (newPackage != appPackage) {
            log("set close state ($newPackage)")
            repository.setAppState(appPackage, AppState.CLOSE)
            return
        }

        val appState = repository.getAppState(appPackage)
        log("old state = $appState ($appPackage)")

        if (repository.getAppState(appPackage) == AppState.CLOSE) {
            log("set open state ($newPackage)")
            repository.setAppState(appPackage, AppState.OPEN)
            repository.incLaunchCount(appPackage)
        }
    }


    override fun showAd(pn: String?) {
        ad = PersonalInt(
            context,
            type,
            object : OverlayCallback {
                override fun onViewDestroyed() {
                    ad = null
                }
            })
        ad?.show()
    }


}