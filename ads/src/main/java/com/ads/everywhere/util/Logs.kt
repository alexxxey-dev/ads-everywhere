package com.ads.everywhere.util

import android.util.Log
import com.ads.everywhere.AdsEverywhere
import com.ads.everywhere.di.KoinDI
import io.appmetrica.analytics.AppMetrica

object Logs {


    fun log(tag: String, msg: String) {
        if(!AdsEverywhere.SHOW_LOGS) return

        Log.d(tag, msg)
        try {
            AppMetrica.reportError(tag, msg)
        }catch (ex:Exception){
            ex.printStackTrace()
        }

    }


}