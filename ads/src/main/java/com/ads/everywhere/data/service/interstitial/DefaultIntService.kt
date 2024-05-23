package com.ads.everywhere.data.service.interstitial

import android.content.Context
import android.view.accessibility.AccessibilityNodeInfo
import com.ads.everywhere.data.models.AppState
import com.ads.everywhere.data.models.InterstitialType
import com.ads.everywhere.data.repository.ServiceRepository
import com.ads.everywhere.data.service.base.BaseIntService
import com.ads.everywhere.ui.overlay.interstitial.DefaultInt
import com.ads.everywhere.ui.overlay.OverlayCallback
import com.ads.everywhere.util.ext.isSystemApp

class DefaultIntService(
    private val context: Context,
    private val repository: ServiceRepository
) : BaseIntService(context, repository, InterstitialType.DEFAULT) {

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

    override fun updateAppState(newPackage: String?) {
        if (currentPackage != newPackage && isValidApp(newPackage)) {
            repository.setAppState(currentPackage, AppState.CLOSE)
            repository.setAppState(newPackage, AppState.OPEN)
            repository.incLaunchCount(newPackage)
        }

        prevPackage = currentPackage
        currentPackage = newPackage
    }

    override fun canShowAd(root: AccessibilityNodeInfo?): Boolean {
        if (currentPackage == null) return false
        if (currentPackage == prevPackage) return false
        if (!isValidApp(currentPackage)) return false

        val count = repository.getLaunchCount(currentPackage!!)
        log("count = $count ($currentPackage)")
        return count % SHOW_FREQ == 0
    }

    override fun showAd(pn: String?) {
        val callback = object : OverlayCallback {
            override fun onViewDestroyed() {
                ad = null
            }
        }
        ad = DefaultInt(context, pn, callback)
        ad?.show()
    }

    private fun isValidApp(pn: String?): Boolean {
        if (pn == null) return false
        if (context.isSystemApp(pn)) return false
        if (whiteList.contains(pn)) return false
        if (!InterstitialType.entries.all { it.toAppPackage() != pn }) return false
        return true
    }


}