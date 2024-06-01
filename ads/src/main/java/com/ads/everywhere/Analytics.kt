package com.ads.everywhere

import android.content.Context
import com.ads.everywhere.data.models.AutostartAvailable
import com.ads.everywhere.data.models.PermissionType
import com.ads.everywhere.data.repository.PrefsRepository
import com.ads.everywhere.data.repository.UsageRepository
import com.ads.everywhere.data.repository.PermissionsRepository
import com.ads.everywhere.util.Logs
import com.ads.everywhere.util.ext.getDeviceName
import com.google.gson.Gson
import io.appmetrica.analytics.AppMetrica
import io.appmetrica.analytics.AppMetricaConfig

class Analytics(
    private val usageStats: UsageRepository,
    private val prefs: PrefsRepository,
    private val permissions: PermissionsRepository
) {
    companion object {
        private val gson = Gson()
        const val TAG = "ANALYTICS_TAG"

        const val EVENT_AUTOSTART_AVAILABLE = "AUTOSTART_AVAILABLE"
        const val EVENT_AUTOSTART_REQUEST = "AUTOSTART_REQUEST"
        const val EVENT_USAGE_STATS = "APP_USAGE"

        const val SHOW_SCREEN_REWARD = "SHOW_REWARD_SCREEN"
        const val CLICK_BUTTON_REWARD = "CLICK_REWARD_BUTTON"
        const val CLICK_POPUP_ALLOW = "CLICK_POPUP_ALLOW"
        const val GRANT_ALL_PERMISSIONS = "GRANT_ALL_PERMISSIONS"
        const val GRANT_SOME_PERMISSIONS = "GRANT_SOME_PERMISSIONS"
        const val REVOKE_PERMISSION = "REVOKE_PERMISSION"

        const val SHOW_DEFAULT_INTERSTITIAL = "SHOW_DEFAULT_INTERSTITIAL"
        const val CLICK_DEFAULT_INTERSTITIAL = "CLICK_DEFAULT_INTERSTITIAL"

        const val CLICK_TINKOFF_INTERSTITIAL = "CLICK_TINK_INTERSTITIAL"
        const val SHOW_TINKOFF_INTERSTITIAL = "SHOW_TINK_INTERSTITIAL"

        const val SHOW_SBER_INTERSTITIAL = "SHOW_SBER_INTERSTITIAL"
        const val CLICK_SBER_INTERSTITIAL = "CLICK_SBER_INTERSTITIAL"

        const val SHOW_VIDEO_INTERSTITIAL = "SHOW_VIDEO_INTERSTITIAL"
        const val CLICK_VIDEO_INTERSTITIAL = "CLICK_VIDEO_INTERSTITIAL"

        fun init(context:Context){
            try {
                val apiKey = context.getString(R.string.app_metrica_api_key)
                val config = AppMetricaConfig
                    .newConfigBuilder(apiKey)
                    .withCrashReporting(true)

                AppMetrica.activate(context, config.build())
                Logs.log(TAG, "INIT APP METRICA")
            }catch (ex:Exception){
                ex.printStackTrace()
            }

        }
        fun reportException(title:String, ex:Throwable){
            AppMetrica.reportError(title, ex)
            AppMetrica.sendEventsBuffer()
        }


        fun sendEvent(title: String, sendBuffer: Boolean = true) :Boolean{
            return try {
                AppMetrica.reportEvent(title)
                if (sendBuffer) AppMetrica.sendEventsBuffer()
                Logs.log(TAG, "reportEvent = $title")
                true
            } catch (ex: Exception) {
                ex.printStackTrace()
                false
            }
        }


        fun sendEvent(title: String, obj: Any?, sendBuffer: Boolean = true):Boolean {
            return try {
                val json = gson.toJson(obj)
                AppMetrica.reportEvent(title, json)
                if (sendBuffer) AppMetrica.sendEventsBuffer()
                Logs.log(TAG, "reportEvent = $title")
                true
            }catch (ex:Exception){
                ex.printStackTrace()
                false
            }

        }

    }


    data class EventAutostartAvailable(val available: Boolean, val device: String)




    fun sendSomePermissions() {

        val some = permissions.loadAll().any { permissions.granted(it.type) }
        val all = permissions.loadAll().all { permissions.granted(it.type) }
        if (some && !all) {
            sendEventAutostart(GRANT_SOME_PERMISSIONS)
        }
    }


    fun sendRevokedPermissions() {

        val revoked = permissions.loadAll().any { !permissions.granted(it.type) }
        if (prefs.granted() && revoked && !prefs.revokedSent()) {
            sendEventAutostart(REVOKE_PERMISSION)
            prefs.revokedSent(true)
        }
    }

    fun sendAutostartAvailable() {
        if (prefs.autostartSent()) return

        val autostartAvailable = permissions.available(PermissionType.AUTO_START)
        val event =
            EventAutostartAvailable(autostartAvailable, getDeviceName())
        sendEvent(EVENT_AUTOSTART_AVAILABLE, event)
        prefs.autostartSent(true)
    }


    fun sendUsageStats() {
        if (prefs.usageStatsSent() || !permissions.granted(PermissionType.USAGE_STATS)) {
            return
        }

        val usageList = usageStats.load()
        usageList.forEach { sendEvent(EVENT_USAGE_STATS, it, false) }
        AppMetrica.sendEventsBuffer()
        prefs.usageStatsSent(true)
    }




    fun sendEventAutostart(title: String) {
        sendEvent(title, AutostartAvailable(permissions.available(PermissionType.AUTO_START)))
    }

}