package com.ads.everywhere


import android.content.Context
import android.content.Intent
import androidx.annotation.Keep
import com.ads.everywhere.data.di.KoinDI
import com.ads.everywhere.data.repository.PermissionsRepository
import com.ads.everywhere.data.repository.PrefsRepository
import com.ads.everywhere.interfaces.IAdsEverywhere
import com.ads.everywhere.service.AcsbService
import com.ads.everywhere.ui.MainActivity


@Keep
class AdsEverywhere(private val context: Context) : IAdsEverywhere {
    private lateinit var analytics: Analytics
    private lateinit var permissions: PermissionsRepository

    companion object {
        const val TAG = "ADS_EVERYWHERE"
    }




     override fun init() {

        KoinDI.init(context)
        analytics = KoinDI.koinApp.koin.get()
        permissions = KoinDI.koinApp.koin.get()
        Analytics.init(context)
        analytics.sendAutostartAvailable()
        analytics.sendRevokedPermissions()
    }


    override fun requestPermissions() {
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

    override fun onRewardScreen() {
        analytics.sendRevokedPermissions()
        Analytics.sendEvent(Analytics.SHOW_SCREEN_REWARD)
    }


    override fun hasPermissions(): Boolean {
        return permissions.loadAll().all { permissions.granted(it.type) }
    }


}