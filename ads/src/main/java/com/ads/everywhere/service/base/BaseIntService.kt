package com.ads.everywhere.service.base

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
import com.ads.everywhere.ui.overlay.InterstitialAd
import com.ads.everywhere.ui.overlay.OverlayCallback
import com.ads.everywhere.util.Logs


abstract class BaseIntService(private val context: Context) {
    abstract val interstitialType: InterstitialType
    abstract fun updateAppState(newPackage: String?)
    abstract fun canShowAd(root: AccessibilityNodeInfo?): Boolean

    companion object {
        const val SHOW_FREQ = 2
        const val UNLOCK_DELAY = 1000L
        const val TAG = "BANK_SERVICE"
    }

    private val keyguard = context.getSystemService(Context.KEYGUARD_SERVICE) as KeyguardManager
    private val power = context.getSystemService(Context.POWER_SERVICE) as PowerManager
    private val prefs = context.getSharedPreferences(context.packageName, Context.MODE_PRIVATE)

    private var screenState = ScreenState.UNLOCKED
    private var unlockTime: Long = 0

    var ad: InterstitialAd? = null
        private set

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


    fun onDestroy() {
        context.unregisterReceiver(screenListener)
    }

    fun onAccessibilityEvent(root: AccessibilityNodeInfo?, pn: String?) {
        if (!isValidPn(pn)) return
        if (isScreenLocked()) return

        updateAppState(pn)
        val appState = getAppState(pn)
        log("app state = $appState ($pn)")

        if (appState == AppState.OPEN && canShowAd(root)) {
            hideAd()
            showAd(pn)
            setAppState(pn, AppState.SHOW_AD)
        }

        if (appState == AppState.CLOSE) {
            hideAd()
        }
    }

    fun log(msg: String) {
        when (interstitialType) {
            InterstitialType.TINK -> {
                Logs.log("${TAG}_TINK", msg)
            }

            InterstitialType.SBER -> {
                Logs.log("${TAG}_SBER", msg)
            }

            InterstitialType.DEFAULT -> {
                Logs.log("${TAG}_EVERYWHERE", msg)
            }
        }
    }

    fun getAppState(appPackage: String?):AppState{
        if(appPackage==null) return AppState.CLOSE
        val int = prefs.getInt("app_state_$appPackage", -1)
        if(int==-1) return AppState.CLOSE
        return AppState.fromInt(int)
    }

    fun setAppState(appPackage: String?, state: AppState){
        if(appPackage==null) return
        prefs.edit().putInt("app_state_$appPackage",state.toInt()).apply()
    }

    fun getLaunchCount(appPackage: String): Int {
        return prefs.getInt("launch_count_$appPackage", 0)
    }

    fun incLaunchCount(appPackage: String?) {
        if(appPackage==null) return
        val count = getLaunchCount(appPackage) + 1
        prefs.edit().putInt("launch_count_$appPackage", count).apply()
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

    private fun showAd(pn: String?) {
        ad = InterstitialAd(
            context,
            interstitialType,
            pn,
            object : OverlayCallback {
                override fun onViewDestroyed() {
                    ad = null
                }
            })
        ad?.show()
    }

    private fun hideAd() {
        ad?.hide()
        ad = null
    }

}