package com.ads.everywhere.controller

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import com.ads.everywhere.Analytics
import com.ads.everywhere.util.Logs
import com.ads.everywhere.util.ext.createReceiver
import com.ads.everywhere.util.ext.destroyReceiver
import com.ads.everywhere.util.ext.isConnected
import com.mbridge.msdk.newinterstitial.out.MBNewInterstitialHandler
import com.mbridge.msdk.newinterstitial.out.NewInterstitialListener
import com.mbridge.msdk.out.MBridgeIds
import com.mbridge.msdk.out.MBridgeSDKFactory
import com.mbridge.msdk.out.RewardInfo
import com.mbridge.msdk.out.SDKInitStatusListener
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlinx.coroutines.withContext
import kotlin.coroutines.resume

class MinController(private val context: Context) : IAdController {
    companion object {
        const val TAG = "MINTEGRAL_SERVICE"

        private const val APP_KEY = "2a60937f9db64fdd549dd6b4543321ce"
        private const val APP_ID = "295811"

        private const val PLACEMENT_ID = "1577924"
        private const val AD_UNIT_ID = "3427347"

        const val AD_CLOSED = "ON_AD_CLOSED"
        const val SHOW_INT = "SHOW_INT"
        private const val DELAY = 1000L
    }

    private var cacheJob: Job? = null
    private var initJob: Job? = null

    private var showFromService: Boolean = false
    var hideAfterDismiss: Boolean = false
    private var showAfterLoad: Boolean = false

    private var handler: MBNewInterstitialHandler? = null

    private val adsListener = object : NewInterstitialListener {
        override fun onLoadCampaignSuccess(p0: MBridgeIds?) {
            Logs.log(TAG, "onLoadCampaignSuccess; $p0")
            if (showAfterLoad) {
                showInterstitial()
                showAfterLoad = false
            }
        }

        override fun onResourceLoadSuccess(p0: MBridgeIds?) {
            Logs.log(TAG, "onResourceLoadSuccess; $p0")

        }

        override fun onResourceLoadFail(p0: MBridgeIds?, p1: String?) {
            Logs.log(TAG, "onResourceLoadFail; $p0; $p1")

        }

        override fun onAdShow(p0: MBridgeIds?) {
            Logs.log(TAG, "onAdShow; $p0")
            Analytics.sendEvent(Analytics.SHOW_MIN_INTERSTITIAL)
            if (showFromService) {
                hideAfterDismiss = true
                showFromService = false
            }
        }

        override fun onAdClose(p0: MBridgeIds?, p1: RewardInfo?) {
            Logs.log(TAG, "onAdClose; $p0; $p1")
            context.sendBroadcast(Intent(AD_CLOSED))
        }

        override fun onShowFail(p0: MBridgeIds?, p1: String?) {
            Logs.log(TAG, "onShowFail; $p0; $p1")

        }

        override fun onAdClicked(p0: MBridgeIds?) {
            Logs.log(TAG, "onAdClicked; $p0")
            Analytics.sendEvent(Analytics.CLICK_MIN_INTERSTITIAL)
        }

        override fun onVideoComplete(p0: MBridgeIds?) {
            Logs.log(TAG, "onVideoComplete; $p0")

        }

        override fun onAdCloseWithNIReward(p0: MBridgeIds?, p1: RewardInfo?) {
            Logs.log(TAG, "onAdCloseWithNIReward; $p0; $p1")

        }

        override fun onEndcardShow(p0: MBridgeIds?) {
            Logs.log(TAG, "onEndcardShow; $p0")

        }
    }

    private val showListener = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            if (intent?.action == SHOW_INT && handler?.isReady == true) {
                handler?.show()
            }
        }
    }

    init {
        initJob = CoroutineScope(Dispatchers.Main).launch {
            while (!initSdk()) {
                delay(DELAY * 10)
            }
            context.createReceiver(showListener, IntentFilter(SHOW_INT))
            cacheJob = cacheInterstitial()
        }
    }

    private suspend fun initSdk() = suspendCancellableCoroutine { cont ->
        val sdk = MBridgeSDKFactory.getMBridgeSDK()
        val config = sdk.getMBConfigurationMap(APP_ID, APP_KEY)
        sdk.init(config, context, object : SDKInitStatusListener {
            override fun onInitFail(p0: String?) {
                Logs.log(TAG, "onInitFail; $p0")
                if (cont.isActive) cont.resume(false)
            }

            override fun onInitSuccess() {
                Logs.log(TAG, "onInitSuccess;")

                handler = MBNewInterstitialHandler(context, PLACEMENT_ID, AD_UNIT_ID)
                handler?.setInterstitialVideoListener(adsListener)
                if (cont.isActive) cont.resume(true)
            }
        })
    }

    override fun onDestroy() {
        initJob?.cancel()
        cacheJob?.cancel()
        handler = null
        context.destroyReceiver(showListener)
    }


    override fun showInterstitial() {
        if (handler?.isReady == false) {
            showAfterLoad = true
            return
        }
        ActivityController.start(context, ActivityController.SHOW_MIN_INT)
        showFromService = true
    }

    override fun hideInterstitial() {
        ActivityController.stop(context, ActivityController.HIDE_MIN_INT)
    }

    private fun cacheInterstitial() = CoroutineScope(Dispatchers.IO).launch {
        while (this.isActive) {
            if (handler?.isReady == false && context.isConnected) {
                withContext(Dispatchers.Main) { handler?.load() }
            }
            delay(DELAY)
        }
    }
}