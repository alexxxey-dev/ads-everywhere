package com.ads.everywhere.service

import android.view.KeyEvent
import android.view.accessibility.AccessibilityEvent
import androidx.annotation.Keep
import com.ads.everywhere.Analytics
import com.ads.everywhere.service.base.BaseAcsbService

@Keep
class AcsbService : BaseAcsbService() {
    private lateinit var tinkoff: TinkoffService
    private lateinit var sber: SberService
    private lateinit var default: IntService

    override fun onServiceConnected() {
        super.onServiceConnected()
        Analytics.init(this)
        tinkoff = TinkoffService(this)
        sber = SberService(this)
        default = IntService(this)
    }


    override fun onAccessibilityEvent(event: AccessibilityEvent) {
        super.onAccessibilityEvent(event)
        tinkoff.onAccessibilityEvent(getRoot(), pn)
        sber.onAccessibilityEvent(getRoot(), pn)
        default.onAccessibilityEvent(getRoot(), pn)
    }

    override fun onDestroy() {
        tinkoff.onDestroy()
        sber.onDestroy()
        super.onDestroy()
    }


    override fun onKeyEvent(event: KeyEvent): Boolean {
        if (tinkoff.ad != null || sber.ad != null) {
            return true
        }

        return super.onKeyEvent(event)
    }

}