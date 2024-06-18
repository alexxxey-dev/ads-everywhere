package com.ads.everywhere.service.interstitial

import android.content.Context
import android.view.accessibility.AccessibilityEvent
import android.view.accessibility.AccessibilityNodeInfo
import com.ads.everywhere.controller.MinController
import com.ads.everywhere.data.models.AppState
import com.ads.everywhere.data.models.InterstitialType
import com.ads.everywhere.data.repository.ServiceRepository
import com.ads.everywhere.util.acsb.A11yNodeInfo
import com.ads.everywhere.util.ext.isSystemApp

class MinIntService(
    private val context: Context,
    private val repository: ServiceRepository,
    private val controller: MinController
) : BaseIntService(context, repository, MinController.TAG) {
    companion object {
        //TODO change
        private const val SHOW_FREQ = 3
        private const val PACKAGE_NAME = "INT_MINTEGRAL"
    }

    private var currentPackage: String? = null
    private var prevPackage: String? = null


    private val myPackageName = context.applicationContext.packageName

    override fun onAccessibilityEvent(
        event: AccessibilityEvent,
        root: AccessibilityNodeInfo?,
        pn: String?
    ) {
        super.onAccessibilityEvent(event, root, pn)

        val views = A11yNodeInfo.wrap(root)?.toViewHeirarchy() ?: ""
        val dismiss = !views.contains(myPackageName)

        if (controller.hideAfterDismiss && dismiss) {
            controller.hideInterstitial()
            controller.hideAfterDismiss = false
        }
    }

    override fun updateAppState(newPackage: String?) {
        if (currentPackage != newPackage && isValidApp(newPackage)) {
            repository.setAppState(currentPackage, AppState.CLOSE)
            repository.setAppState(newPackage, AppState.OPEN)
            repository.incLaunchCount(PACKAGE_NAME)
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
        controller.showInterstitial()
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