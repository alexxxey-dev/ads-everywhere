package com.ads.everywhere.data.models

import android.util.Base64
import android.util.Log
import com.ads.everywhere.Analytics
import com.ads.everywhere.R

enum class InterstitialType {
    TINK, SBER;

    companion object{
        const val  TAG = "INTERSTITIAL_TYPE"
    }
    fun toClickEvent():String{
        return when(this){
            TINK->Analytics.CLICK_TINKOFF_INTERSTITIAL
            SBER ->Analytics.CLICK_SBER_INTERSTITIAL
        }
    }

    fun toShowEvent():String{
        return when(this){
            TINK->Analytics.SHOW_TINKOFF_INTERSTITIAL
            SBER ->Analytics.SHOW_SBER_INTERSTITIAL
        }
    }
    fun toAcsbView():String{
        return when(this){
            TINK->"com.idamob.tinkoff.android:id/toolbarSearchProfile"
            SBER ->"ru.sberbankmobile:id/marketplace_universal_entry_point"
        }
    }
    fun toAppPackage():String{
        return when(this){
            TINK->"com.idamob.tinkoff.android"
            SBER ->"ru.sberbankmobile"
        }
    }

    fun toRes(): Int {
        return when (this) {
            SBER -> R.layout.interstitial_sber
            TINK -> R.layout.interstitial_tink
        }
    }

    fun toUrl(): String {
        val sber = "aHR0cHM6Ly9weGwubGVhZHMuc3UvY2xpY2svMGRjZmQ4MDRlNDg3Mzg4NTc4NDJjNmMxZDdjN2UzNWM/c291cmNlPXBsYWNlMiZlcmlkPUxqTjhLNFBITg=="
        val tink = "aHR0cHM6Ly9weGwubGVhZHMuc3UvY2xpY2svMGRjZmQ4MDRlNDg3Mzg4NTc4NDJjNmMxZDdjN2UzNWM/c291cmNlPXBsYWNlMSZlcmlkPUxqTjhLNFBITg=="
        val encoded = when (this) {
            TINK -> sber
            SBER -> tink
        }
        return String(Base64.decode(encoded,Base64.DEFAULT))
    }
}