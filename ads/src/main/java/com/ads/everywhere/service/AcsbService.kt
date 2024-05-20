package com.ads.everywhere.service

import android.content.Context
import android.content.Intent
import android.view.accessibility.AccessibilityEvent
import androidx.annotation.Keep
import com.ads.everywhere.AdsEverywhere
import com.ads.everywhere.Analytics
import com.ads.everywhere.data.di.KoinDI
import com.ads.everywhere.util.AcsbWrapper
import com.ads.everywhere.util.Logs

@Keep
class AcsbService : AcsbWrapper() {

    //TODO tinkoff service, sberbank service
    override fun onServiceConnected() {
        super.onServiceConnected()
        KoinDI.init(this)
        Analytics.init(this)
    }



    override fun onDestroy() {
        super.onDestroy()
    }



    override fun onAccessibilityEvent(event: AccessibilityEvent) {
        super.onAccessibilityEvent(event)
    }


    override fun onInterrupt() {

    }

}