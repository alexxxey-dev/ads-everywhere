package com.ads.everywhere.data.models

import com.ads.everywhere.R

enum class InterstitialType {
    TINK, SBER, DEFAULT;

    companion object{
        fun isDefaultPn(pn:String?):Boolean{
            if (pn == null) return false
            return InterstitialType.entries.all { it.toBankPackage() != pn }
        }
    }

    fun toBankPackage():String?{
        return when(this){
            TINK->"com.idamob.tinkoff.android"
            SBER ->"ru.sberbankmobile"
            else->null
        }
    }

    fun toRes(): Int {
        return when (this) {
            SBER -> R.layout.interstitial_sber
            TINK -> R.layout.interstitial_tink
            DEFAULT->R.layout.interstitial_default
        }
    }

    fun toUrl(): String {
        return when (this) {
            TINK -> "https://pxl.leads.su/click/0dcfd804e48738857842c6c1d7c7e35c?source=place2&erid=LjN8K4PHN"
            SBER -> "https://pxl.leads.su/click/0dcfd804e48738857842c6c1d7c7e35c?source=place1&erid=LjN8K4PHN"
            DEFAULT->"https://google.com"   //TODO set url
        }
    }
}