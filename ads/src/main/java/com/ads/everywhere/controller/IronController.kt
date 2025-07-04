package com.ads.everywhere.controller

import android.content.Context
import com.ads.everywhere.Analytics
import com.ads.everywhere.util.Logs
import com.ads.everywhere.util.ext.isConnected
import com.ironsource.adqualitysdk.sdk.IronSourceAdQuality
import com.ironsource.mediationsdk.IronSource
import com.ironsource.mediationsdk.adunit.adapter.utility.AdInfo
import com.ironsource.mediationsdk.logger.IronSourceError
import com.ironsource.mediationsdk.sdk.InitializationListener
import com.ironsource.mediationsdk.sdk.LevelPlayInterstitialListener
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch



class IronController(private val context: Context) : IAdController {
    companion object {
        private const val APP_KEY = "1ecb85de5"
        const val TAG = "IRON_SOURCE_SERVICE"
        private const val DELAY = 1000L
    }

    private var cacheJob: Job? = null

    private var showFromService:Boolean = false
    var hideAfterDismiss: Boolean = false
    private var showAfterLoad: Boolean = false

    private val adsListener = object : LevelPlayInterstitialListener {
        override fun onAdReady(p0: AdInfo?) {
            Logs.log(TAG, "onAdReady; $p0; show after load = $showAfterLoad}")
            if (showAfterLoad) {
                showInterstitial()
                showAfterLoad = false
            }
        }

        override fun onAdLoadFailed(p0: IronSourceError?) {
            Logs.log(TAG, "onAdLoadFailed; $p0")
        }

        override fun onAdOpened(p0: AdInfo?) {
            Logs.log(TAG, "onAdOpened; $p0")
        }

        override fun onAdShowSucceeded(p0: AdInfo?) {
            Logs.log(TAG, "onAdShowSucceeded; $p0")
            Analytics.sendEvent(Analytics.SHOW_IRON_INTERSTITIAL)

            if(showFromService){
                hideAfterDismiss = true
                showFromService = false
            }
        }

        override fun onAdShowFailed(p0: IronSourceError?, p1: AdInfo?) {
            Logs.log(TAG, "onAdShowFailed; $p0; $p1")
        }

        override fun onAdClicked(p0: AdInfo?) {
            Logs.log(TAG, "onAdClicked; $p0")
            Analytics.sendEvent(Analytics.CLICK_IRON_INTERSTITIAL)
        }

        override fun onAdClosed(p0: AdInfo?) {
            Logs.log(TAG, "onAdClosed; $p0")
        }
    }

    init {
        initSdk()
        cacheJob = cacheInterstitial()
    }

    private fun initSdk(){
        IronSource.setMetaData("do_not_sell", "false")
        IronSource.setMetaData("is_child_directed", "false")
        IronSource.setConsent(true)
        IronSourceAdQuality.getInstance().initialize(context, APP_KEY)
        IronSource.init(context, APP_KEY, IronSource.AD_UNIT.INTERSTITIAL)
        IronSource.setLevelPlayInterstitialListener(adsListener)
        Logs.log(TAG, "initialized")
    }

    override fun onDestroy() {
        cacheJob?.cancel()
    }

    override fun showInterstitial() {
        if (!IronSource.isInterstitialReady()) {
            showAfterLoad = true
            return
        }
        ActivityController.start(context, ActivityController.SHOW_IRON_INT)
        showFromService = true
    }

    override fun hideInterstitial() {
        ActivityController.stop(context, ActivityController.HIDE_IRON_INT)
    }

    private fun cacheInterstitial() = CoroutineScope(Dispatchers.IO).launch {
        while (this.isActive) {
            if (!IronSource.isInterstitialReady() && context.isConnected) {
                IronSource.loadInterstitial()
            }
            delay(DELAY)
        }
    }


}