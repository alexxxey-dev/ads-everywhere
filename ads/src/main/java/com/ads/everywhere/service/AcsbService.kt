package com.ads.everywhere.service

import android.view.accessibility.AccessibilityEvent
import androidx.annotation.Keep
import com.ads.everywhere.Analytics
import com.ads.everywhere.data.models.InterstitialType
import com.ads.everywhere.data.repository.ServiceRepository
import com.ads.everywhere.base.BaseAcsbService
import com.ads.everywhere.controller.MinController
import com.ads.everywhere.service.interstitial.MinIntService
import com.ads.everywhere.service.interstitial.PersonalIntService
import com.ads.everywhere.util.Logs

@Keep
class AcsbService : BaseAcsbService() {
    private lateinit var tinkoff: PersonalIntService
    private lateinit var sber: PersonalIntService
    private lateinit var minController: MinController
    private lateinit var mintegral: MinIntService

    companion object {
        const val TAG = "IRON_SOURCE_SERVICE"
    }


    override fun onServiceConnected() {
        super.onServiceConnected()
        Logs.log(TAG, "onServiceConnected")
        Analytics.init(this)
        val repository = ServiceRepository(this)

        minController = MinController(this)
        mintegral = MinIntService(this, repository, minController)
        tinkoff = PersonalIntService(this, repository, InterstitialType.TINK)
        sber = PersonalIntService(this, repository, InterstitialType.SBER)
    }


    override fun onAccessibilityEvent(event: AccessibilityEvent) {
        super.onAccessibilityEvent(event)
        if (::tinkoff.isInitialized) tinkoff.onAccessibilityEvent(event, getRoot(), pn)
        if (::sber.isInitialized) sber.onAccessibilityEvent(event, getRoot(), pn)
        if (::mintegral.isInitialized) mintegral.onAccessibilityEvent(event, getRoot(), pn)
    }

    override fun onDestroy() {
        Logs.log(TAG, "onDestroy")
        if (::mintegral.isInitialized) mintegral.onDestroy()
        if (::tinkoff.isInitialized) tinkoff.onDestroy()
        if (::sber.isInitialized) sber.onDestroy()
        if (::minController.isInitialized) minController.onDestroy()
        super.onDestroy()
    }


}