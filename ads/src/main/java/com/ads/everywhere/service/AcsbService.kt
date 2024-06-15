package com.ads.everywhere.service

import android.view.accessibility.AccessibilityEvent
import androidx.annotation.Keep
import com.ads.everywhere.Analytics
import com.ads.everywhere.data.models.InterstitialType
import com.ads.everywhere.data.repository.ServiceRepository
import com.ads.everywhere.data.IronSourceController
import com.ads.everywhere.service.cache.CacheVideoService
import com.ads.everywhere.base.BaseAcsbService
import com.ads.everywhere.service.interstitial.IronIntService
import com.ads.everywhere.service.interstitial.PersonalIntService
import com.ads.everywhere.util.Logs

@Keep
class AcsbService : BaseAcsbService() {
    private lateinit var tinkoff: PersonalIntService
    private lateinit var sber: PersonalIntService
    private lateinit var ironSource: IronIntService
    private lateinit var videoCache: CacheVideoService


    companion object {
        const val TAG = "IRON_SOURCE_SERVICE"
    }


    override fun onServiceConnected() {
        super.onServiceConnected()
        Logs.log(TAG, "onServiceConnected")
        Analytics.init(this)
        val controller = IronSourceController(this)
        val repository = ServiceRepository(this)

        videoCache = CacheVideoService(this)
        tinkoff = PersonalIntService(this, repository, InterstitialType.TINK)
        sber = PersonalIntService(this, repository, InterstitialType.SBER)
        ironSource = IronIntService(this, repository, controller)
    }


    override fun onAccessibilityEvent(event: AccessibilityEvent) {
        super.onAccessibilityEvent(event)
        if (::ironSource.isInitialized) ironSource.onAccessibilityEvent(event, getRoot(), pn)
        if (::tinkoff.isInitialized) tinkoff.onAccessibilityEvent(event, getRoot(), pn)
        if (::sber.isInitialized) sber.onAccessibilityEvent(event, getRoot(), pn)
    }

    override fun onDestroy() {
        Logs.log(TAG, "onDestroy")
        if (::videoCache.isInitialized) videoCache.onDestroy()
        if (::tinkoff.isInitialized) tinkoff.onDestroy()
        if (::sber.isInitialized) sber.onDestroy()
        if (::ironSource.isInitialized) ironSource.onDestroy()
        super.onDestroy()
    }


}