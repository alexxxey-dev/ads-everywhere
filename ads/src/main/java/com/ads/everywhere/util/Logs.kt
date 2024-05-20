package com.ads.everywhere.util

import android.util.Log
import com.ads.everywhere.data.di.KoinDI
import io.appmetrica.analytics.AppMetrica

object Logs {


    fun log(tag: String, msg: String) {
        Log.d(tag, msg)
        try {
            AppMetrica.reportError(tag, msg)
        }catch (ex:Exception){
            ex.printStackTrace()
        }

    }


}