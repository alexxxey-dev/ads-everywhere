package com.ads.everywhere.data.service.base

import android.app.KeyguardManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.PowerManager
import android.view.accessibility.AccessibilityNodeInfo
import com.ads.everywhere.data.models.AppState
import com.ads.everywhere.data.models.InterstitialType
import com.ads.everywhere.data.models.ScreenState
import com.ads.everywhere.data.repository.ServiceRepository
import com.ads.everywhere.ui.overlay.OverlayView
import com.ads.everywhere.util.Logs
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext


abstract class BaseIntService(
    private val context: Context,
    private val repository: ServiceRepository,
    private val type: InterstitialType
) {
    abstract fun showAd(pn:String?)
    abstract fun updateAppState(newPackage: String?)
    abstract fun canShowAd(root: AccessibilityNodeInfo?): Boolean

    companion object {
        const val SHOW_FREQ = 2
        const val UNLOCK_DELAY = 1000L
        const val TAG = "BANK_SERVICE"
    }

    private val keyguard = context.getSystemService(Context.KEYGUARD_SERVICE) as KeyguardManager
    private val power = context.getSystemService(Context.POWER_SERVICE) as PowerManager

    private var screenState = ScreenState.UNLOCKED
    private var unlockTime: Long = 0

    var ad: OverlayView? = null

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



    fun onAccessibilityEvent(root: AccessibilityNodeInfo?, pn: String?) {
        if (!isValidPn(pn)) return
        if (isScreenLocked()) return
        updateAppState(pn)

        val appState = repository.getAppState(pn)
        if (appState == AppState.OPEN && canShowAd(root)) {
            hideAd()
            showAd(pn)
            repository.setAppState(pn, AppState.SHOW_AD)
        }
        if (appState == AppState.CLOSE) {
            hideAd()
        }
    }

    fun onDestroy() {
        context.unregisterReceiver(screenListener)
    }

    private fun hideAd(){
        ad?.hide()
        ad = null
    }

    fun log(msg: String) {
        when (type) {
            InterstitialType.TINK -> {
                Logs.log("${TAG}_TINK", msg)
            }

            InterstitialType.SBER -> {
                Logs.log("${TAG}_SBER", msg)
            }

            InterstitialType.DEFAULT -> {
                Logs.log("${TAG}_DEFAULT", msg)
            }
        }
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



}