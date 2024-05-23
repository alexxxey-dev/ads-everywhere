package com.ads.everywhere.data.service

import android.view.accessibility.AccessibilityEvent
import androidx.annotation.Keep
import com.ads.everywhere.Analytics
import com.ads.everywhere.data.models.InterstitialType
import com.ads.everywhere.data.repository.ServiceRepository
import com.ads.everywhere.data.service.base.BaseAcsbService
import com.ads.everywhere.data.service.interstitial.PersonalIntService
import com.ads.everywhere.data.service.interstitial.DefaultIntService

@Keep
class AcsbService : BaseAcsbService() {
    private lateinit var tinkoff: PersonalIntService
    private lateinit var sber: PersonalIntService
    private lateinit var default: DefaultIntService

    override fun onServiceConnected() {
        super.onServiceConnected()
        Analytics.init(this)
        val repository = ServiceRepository(this)
        tinkoff = PersonalIntService(this,repository, InterstitialType.TINK)
        sber = PersonalIntService(this, repository, InterstitialType.SBER)
        default = DefaultIntService(this,repository)
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



}