package com.ads.everywhere.service

import android.content.Context
import android.view.accessibility.AccessibilityNodeInfo
import com.ads.everywhere.data.models.AppState
import com.ads.everywhere.data.models.InterstitialType
import com.ads.everywhere.service.base.BaseIntService
import com.ads.everywhere.util.ext.isSystemApp

class IntService(private val context: Context) : BaseIntService(context) {
    override val interstitialType: InterstitialType = InterstitialType.DEFAULT
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
        if(!InterstitialType.isDefaultPn(newPackage)) return

        if (currentPackage != newPackage) {
            setAppState(currentPackage, AppState.CLOSE)
            setAppState(newPackage, AppState.OPEN)
            incLaunchCount(newPackage)
        }
        prevPackage = currentPackage
        currentPackage = newPackage
    }

    override fun canShowAd(root: AccessibilityNodeInfo?): Boolean {
        if (currentPackage == null) return false
        if (currentPackage == prevPackage) return false
        if (context.isSystemApp(currentPackage)) return false
        if (whiteList.contains(currentPackage)) return false
        if (!InterstitialType.isDefaultPn(currentPackage)) return false

        val count = getLaunchCount(currentPackage!!)
        log("count = $count")
        return count % SHOW_FREQ == 0
    }
}