package com.ads.everywhere.service.bank

import android.app.KeyguardManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.PowerManager
import android.view.accessibility.AccessibilityEvent
import android.view.accessibility.AccessibilityNodeInfo
import com.ads.everywhere.data.models.BankState
import com.ads.everywhere.data.models.InterstitialType
import com.ads.everywhere.data.models.ScreenState
import com.ads.everywhere.ui.overlay.InterstitialAd
import com.ads.everywhere.ui.overlay.OverlayCallback
import com.ads.everywhere.util.Logs


abstract class BankService(private val context: Context) {
    abstract val interstitialType: InterstitialType
    abstract val bankPn: String
    abstract fun isMainScreen(root: AccessibilityNodeInfo?): Boolean

    companion object {
        const val SHOW_FREQ = 2
        const val UNLOCK_DELAY = 1000L
        const val TAG = "BANK_SERVICE"
    }

    private val keyguard = context.getSystemService(Context.KEYGUARD_SERVICE) as KeyguardManager
    private val power = context.getSystemService(Context.POWER_SERVICE) as PowerManager
    private val prefs = context.getSharedPreferences(context.packageName, Context.MODE_PRIVATE)

    private var bankState = BankState.CLOSE

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

    fun onAccessibilityEvent(event: AccessibilityEvent, root: AccessibilityNodeInfo?, pn: String?) {
        if (!isValidPn(pn)) return
        if (!isValidEvent(event)) return
        if (isScreenLocked()) return

        updateStatus(pn)

        if (canShowAd(root)) showAd()
    }

    private fun isValidPn(pn: String?): Boolean {
        return pn != null
                && pn != context.packageName
                && pn != context.applicationContext.packageName
                && pn != "android"
    }

    private fun isValidEvent(event: AccessibilityEvent): Boolean {
        return event.eventType == AccessibilityEvent.TYPE_WINDOWS_CHANGED
                || event.eventType == AccessibilityEvent.TYPE_WINDOW_CONTENT_CHANGED
    }

    private fun isScreenLocked(): Boolean {
        if (!power.isInteractive) return true
        if (keyguard.isKeyguardLocked) return true
        if (screenState != ScreenState.UNLOCKED) return true
        if (System.currentTimeMillis() - unlockTime < UNLOCK_DELAY) return true
        return false
    }

    private fun updateStatus(pn: String?) {
        if (pn != bankPn) {
            log("package = $pn")
            bankState = BankState.CLOSE
            return
        }

        if (bankState == BankState.CLOSE && pn == bankPn) {
            bankState = BankState.OPEN
            setLaunchCount(getLaunchCount() + 1)
        }
    }

    private fun canShowAd(root: AccessibilityNodeInfo?): Boolean {
        log("status = $bankState")
        if (bankState != BankState.OPEN) return false

        val count = getLaunchCount()
        log("count = $count")
        if (count % SHOW_FREQ != 0) return false

        val main = isMainScreen(root)
        log("main = $main")
        return isMainScreen(root)

    }

    private fun showAd() {
        ad?.hide()
        ad = null
        ad = InterstitialAd(context, interstitialType, object : OverlayCallback {
            override fun onViewDestroyed() {
                ad = null
            }
        })
        ad?.show()
        bankState = BankState.SHOW
    }

    private fun log(msg: String) {
        if (interstitialType == InterstitialType.TINK) {
            Logs.log("${TAG}_TINK", msg)
        } else if (interstitialType == InterstitialType.SBER) {
            Logs.log("${TAG}_SBER", msg)
        }
    }

    private fun getLaunchCount(): Int {
        return prefs.getInt("launch_count_$bankPn", 0)
    }

    private fun setLaunchCount(count: Int) {
        prefs.edit().putInt("launch_count_$bankPn", count).apply()
    }

}