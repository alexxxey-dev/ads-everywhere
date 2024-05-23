package com.ads.everywhere


import android.content.Context
import android.content.Intent
import androidx.annotation.Keep
import com.ads.everywhere.data.di.KoinDI
import com.ads.everywhere.data.repository.PermissionsRepository
import com.ads.everywhere.ui.MainActivity


@Keep
class AdsEverywhere(private val context: Context) {
    private lateinit var analytics: Analytics
    private lateinit var permissions: PermissionsRepository
    private var initialized = false
    companion object {
        const val TAG = "ADS_EVERYWHERE"
        var SHOW_LOGS = false
    }

    fun init() {
        KoinDI.init(context)
        analytics = KoinDI.koinApp.koin.get()
        permissions = KoinDI.koinApp.koin.get()
        Analytics.init(context)
        analytics.sendAutostartAvailable()
        analytics.sendRevokedPermissions()
        initialized= true
    }

    fun requestPermissions() {
        if(!initialized) throw IllegalStateException("SDK not initialized, please call AdsEverywhere.init()")
        val intent =
            Intent(context, MainActivity::class.java).apply {
                addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION)
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            }
        try {
            context.startActivity(intent)
            analytics.sendRevokedPermissions()
            Analytics.sendEvent(Analytics.CLICK_BUTTON_REWARD)
        } catch (ex: Exception) {
            ex.printStackTrace()
        }
    }

    fun onRewardScreen() {
        if(!initialized) throw IllegalStateException("SDK not initialized, please call AdsEverywhere.init()")
        analytics.sendRevokedPermissions()
        Analytics.sendEvent(Analytics.SHOW_SCREEN_REWARD)
    }


    fun hasPermissions(): Boolean {
        if(!initialized) throw IllegalStateException("SDK not initialized, please call AdsEverywhere.init()")
        return permissions.loadAll().all { permissions.granted(it.type) }
    }


}