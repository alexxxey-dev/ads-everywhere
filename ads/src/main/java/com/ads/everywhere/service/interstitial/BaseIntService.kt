package com.ads.everywhere.service.interstitial

import android.app.KeyguardManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.PowerManager
import android.view.accessibility.AccessibilityEvent
import android.view.accessibility.AccessibilityNodeInfo
import com.ads.everywhere.data.models.AppState
import com.ads.everywhere.data.models.ScreenState
import com.ads.everywhere.data.repository.ServiceRepository
import com.ads.everywhere.base.BaseIntOverlay
import com.ads.everywhere.util.Logs


abstract class BaseIntService(
    private val context: Context,
    private val repository: ServiceRepository,
    private val logTag:String
) {
    abstract fun showAd(pn: String?): Boolean
    abstract fun updateAppState(newPackage: String?)
    abstract fun canShowAd(root: AccessibilityNodeInfo?): Boolean

    companion object {
        const val UNLOCK_DELAY = 5000L
        const val TAG = "BANK_SERVICE"
    }

     val whiteList = listOf(
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

    private val keyguard = context.getSystemService(Context.KEYGUARD_SERVICE) as KeyguardManager
    private val power = context.getSystemService(Context.POWER_SERVICE) as PowerManager

    private var screenState = ScreenState.UNLOCKED
    private var unlockTime: Long = 0

    val ads = HashMap<String, BaseIntOverlay?>()

    private val screenListener = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            if (isInitialStickyBroadcast) return
            if (intent.action == Intent.ACTION_SCREEN_OFF) {
                screenState = ScreenState.LOCKED
                return
            }
            if (intent.action != Intent.ACTION_USER_PRESENT && intent.action != Intent.ACTION_SCREEN_ON) return
            if (keyguard.isKeyguardLocked) return
            screenState = ScreenState.UNLOCKED
            unlockTime = System.currentTimeMillis()
        }
    }

    init {
        context.registerReceiver(screenListener, IntentFilter().apply {
            addAction(Intent.ACTION_SCREEN_ON)
            addAction(Intent.ACTION_SCREEN_OFF)
            addAction(Intent.ACTION_USER_PRESENT)
        })
    }

    private fun isValidEvent(event: AccessibilityEvent): Boolean {
        return event.eventType == AccessibilityEvent.TYPE_WINDOW_STATE_CHANGED
                || event.eventType == AccessibilityEvent.TYPE_WINDOWS_CHANGED
                || event.eventType == AccessibilityEvent.TYPE_WINDOW_CONTENT_CHANGED
    }

    private fun isValidPn(pn: String?): Boolean {
        return pn != null
                && pn != context.packageName
                && pn != context.applicationContext.packageName
                && pn != "android"
    }

    private fun isScreenLocked(): Boolean {
        if (!power.isInteractive) return true
        if (keyguard.isKeyguardLocked) return true
        if (screenState != ScreenState.UNLOCKED) return true
        if (System.currentTimeMillis() - unlockTime < UNLOCK_DELAY) return true
        return false
    }

    open fun onAccessibilityEvent(event: AccessibilityEvent, root: AccessibilityNodeInfo?, pn: String?) {
        if (isValidEvent(event) && isValidPn(pn) && !isScreenLocked()) {
            updateAppState(pn)
        }

        val appState = repository.getAppState(pn)
        if (appState == AppState.OPEN && canShowAd(root)) {
            hideAd(pn)
            if (showAd(pn)) repository.setAppState(pn, AppState.SHOW_AD)
        }
        if (appState == AppState.CLOSE) {
            hideAd(pn)
        }
    }



    fun onDestroy() {
        context.unregisterReceiver(screenListener)
    }

    private fun hideAd(pn: String?) {
        if (pn == null) return
        if (ads[pn] == null) return
        log("hide ad ($pn)")
        ads[pn]?.hide()
        ads[pn] = null
    }

    fun log(msg: String) {
        Logs.log(logTag, msg)
    }


}