package com.ads.everywhere.data.service

import android.view.accessibility.AccessibilityEvent
import androidx.annotation.Keep
import com.ads.everywhere.Analytics
import com.ads.everywhere.data.models.InterstitialType
import com.ads.everywhere.data.repository.ServiceRepository
import com.ads.everywhere.data.service.base.BaseAcsbService
import com.ads.everywhere.data.service.cache.CacheVideoService
import com.ads.everywhere.data.service.interstitial.PersonalIntService
import com.ads.everywhere.data.service.interstitial.DefaultIntService

@Keep
class AcsbService : BaseAcsbService() {
    private lateinit var tinkoff: PersonalIntService
    private lateinit var sber: PersonalIntService
    private lateinit var default: DefaultIntService
    private lateinit var video: CacheVideoService

    override fun onServiceConnected() {
        super.onServiceConnected()
        Analytics.init(this)
        val repository = ServiceRepository(this)
        video = CacheVideoService(this)
        tinkoff = PersonalIntService(this,repository, InterstitialType.TINK)
        sber = PersonalIntService(this, repository, InterstitialType.SBER)
        default = DefaultIntService(this,repository, video)
    }


    override fun onAccessibilityEvent(event: AccessibilityEvent) {
        super.onAccessibilityEvent(event)
        tinkoff.onAccessibilityEvent(event,getRoot(), pn)
        sber.onAccessibilityEvent(event,getRoot(), pn)
        default.onAccessibilityEvent(event,getRoot(), pn)
    }

    override fun onDestroy() {
        video.onDestroy()
        tinkoff.onDestroy()
        sber.onDestroy()
        super.onDestroy()
    }



}