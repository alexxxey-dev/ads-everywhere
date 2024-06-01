package com.ads.everywhere.data.models

import com.ads.everywhere.Analytics
import com.ads.everywhere.R

enum class InterstitialType {
    TINK, SBER;


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
        return when (this) {
            TINK -> "https://pxl.leads.su/click/0dcfd804e48738857842c6c1d7c7e35c?source=place2&erid=LjN8K4PHN"
            SBER -> "https://pxl.leads.su/click/0dcfd804e48738857842c6c1d7c7e35c?source=place1&erid=LjN8K4PHN"
        }
    }
}