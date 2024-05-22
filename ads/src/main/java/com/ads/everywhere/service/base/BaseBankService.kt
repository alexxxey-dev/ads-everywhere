package com.ads.everywhere.service.base

import android.content.Context
import android.view.accessibility.AccessibilityNodeInfo
import com.ads.everywhere.data.models.AppState
import com.ads.everywhere.util.Logs

abstract class BaseBankService(private val context: Context) : BaseIntService(context) {
    abstract fun isMainScreen(root: AccessibilityNodeInfo?): Boolean
    abstract val appPackage: String

    override fun canShowAd(root: AccessibilityNodeInfo?): Boolean {
        val count = getLaunchCount(appPackage)
        log("count = $count")
        if (count % SHOW_FREQ != 0) return false

        val main = isMainScreen(root)
        log("main = $main")
        return isMainScreen(root)
    }

    override fun updateAppState(newPackage: String?) {
        if (newPackage != appPackage) {
            log("package = $newPackage")
            setAppState(newPackage, AppState.CLOSE)
            return
        }

        val appState = getAppState(newPackage)
        log("old state = $appState ($newPackage)")
        if (getAppState(newPackage) == AppState.CLOSE && newPackage == appPackage) {
            log("set open state ($newPackage)")
            setAppState(newPackage, AppState.OPEN)

            incLaunchCount(appPackage)
        }
    }
}