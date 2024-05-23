package com.ads.everywhere.data.repository

import android.content.Context
import com.ads.everywhere.data.models.AppState

class ServiceRepository(private val context:Context) {
    private val prefs = context.getSharedPreferences(context.packageName, Context.MODE_PRIVATE)
    fun getAppState(appPackage: String?): AppState {
        if(appPackage==null) return AppState.CLOSE
        val int = prefs.getInt("app_state_$appPackage", -1)
        if(int==-1) return AppState.CLOSE
        return AppState.fromInt(int)
    }

    fun setAppState(appPackage: String?, state: AppState){
        if(appPackage==null) return
        prefs.edit().putInt("app_state_$appPackage",state.toInt()).apply()
    }

    fun getLaunchCount(appPackage: String?): Int {
        if(appPackage==null) return 0
        return prefs.getInt("launch_count_$appPackage", 0)
    }

    fun incLaunchCount(appPackage: String?) {
        if(appPackage==null) return
        val count = getLaunchCount(appPackage) + 1
        prefs.edit().putInt("launch_count_$appPackage", count).apply()
    }
}