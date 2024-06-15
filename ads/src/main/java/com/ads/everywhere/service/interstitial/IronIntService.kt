package com.ads.everywhere.service.interstitial

import android.content.Context
import android.view.accessibility.AccessibilityEvent
import android.view.accessibility.AccessibilityNodeInfo
import com.ads.everywhere.data.models.AppState
import com.ads.everywhere.data.models.InterstitialType
import com.ads.everywhere.data.repository.ServiceRepository
import com.ads.everywhere.data.IronSourceController
import com.ads.everywhere.util.acsb.A11yNodeInfo
import com.ads.everywhere.util.ext.isSystemApp


class IronIntService(
    private val context: Context,
    private val repository: ServiceRepository,
    private val controller: IronSourceController
) : BaseIntService(context, repository, "IRON_SOURCE_SERVICE") {
    companion object {
        private const val SHOW_FREQ = 3
        private const val PACKAGE_NAME = "INT_IRON"
    }

    private var currentPackage: String? = null
    private var prevPackage: String? = null
    private val whiteList = listOf(
        "com.google.android.apps.walletnfcrel",
        "com.google.ar.lens",
        "com.google.android.calendar",
        "com.google.android.apps.nbu.files",
        "com.google.android.apps.subscriptions.red",
        "com.google.android.apps.photos",
        "com.google.android.inputmethod.latin",
        "com.google.android.apps.mapslite",
        "com.google.android.contacts",
        "com.google.android.apps.googleassistant",
        "com.google.android.apps.photosgo",
        "com.google.android.calculator",
        "com.google.android.deskclock",
        "com.google.android.apps.messaging",
        "com.google.android.dialer",
        "com.google.android.apps.maps",
        "com.google.android.GoogleCamera",
    )

    private val myPackageName = context.applicationContext.packageName
    private val webView = "android.webkit.WebView"
    private val viewId = "android:id/content"

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