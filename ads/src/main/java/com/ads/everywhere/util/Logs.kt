package com.ads.everywhere.util

import android.util.Log
import com.ads.everywhere.AdsEverywhere
import com.ads.everywhere.di.KoinDI
import io.appmetrica.analytics.AppMetrica

//TODO disable on release
object Logs {

    fun log(tag: String, msg: String) {
        Log.d(tag, msg)
    }

}