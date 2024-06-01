package com.ads.everywhere.data.service.interstitial

import android.content.Context
import android.view.accessibility.AccessibilityNodeInfo
import com.ads.everywhere.data.models.AppState
import com.ads.everywhere.data.models.InterstitialType
import com.ads.everywhere.data.repository.ServiceRepository
import com.ads.everywhere.ui.interstitial.PersonalIntOverlay
import com.ads.everywhere.ui.overlay.OverlayCallback

class PersonalIntService(
    private val context: Context,
    private val repository: ServiceRepository,
    private val type: InterstitialType
) : BaseIntService(context, repository, "INT_${type.name}") {
    private val appPackage = type.toAppPackage()
    companion object{
        private const val SHOW_FREQ = 2
    }
    override fun canShowAd(root: AccessibilityNodeInfo?): Boolean {
        val count = repository.getLaunchCount(appPackage)
        log("count = $count ($appPackage)")
        if (count % SHOW_FREQ != 0) return false

        if(root==null) return false
        val id = type.toAcsbView()
        val isMainScreen = root.findAccessibilityNodeInfosByViewId(id).any { it.isVisibleToUser }
        log("main = $isMainScreen ($appPackage)")
        return isMainScreen
    }

    override fun updateAppState(newPackage: String?) {
        if (newPackage != appPackage) {
            log("set close state ($appPackage)")
            repository.setAppState(appPackage, AppState.CLOSE)
            return
        }

        val appState = repository.getAppState(appPackage)
        log("app state = $appState ($appPackage)")

        if (appState == AppState.CLOSE) {
            log("set open state ($appPackage)")
            repository.setAppState(appPackage, AppState.OPEN)
            repository.incLaunchCount(appPackage)
        }
    }


    override fun showAd(pn: String?):Boolean {
        if(pn==null) return false
        ads[pn] = PersonalIntOverlay(
            context,
            type,
            object : OverlayCallback {
                override fun onViewDestroyed() {
                    ads[pn] = null
                }
            })
        ads[pn]?.show()
        return true
    }


}