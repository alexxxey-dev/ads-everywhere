package com.ads.everywhere.service

import android.content.Context
import android.graphics.Rect
import com.ads.everywhere.ui.overlay.OverlayView

//TODO rewrite this shit
class OverlayService() {
    private var current: Rect? = null
    private var view: OverlayView? = null
    private var initialized = false
    companion object {
        const val TAG = "BANNER_VIEW"
    }

     fun create(context:Context){
        if(initialized) return
        initialized = true
    }
     fun destroy() {
        initialized = false
//        current = null
//        banner.destroyAd()
//        banner.hideView()
    }

    fun show(top: Int,bottom: Int) {
       if(!initialized) throw IllegalStateException("Overlay not initialized")
//        if(ads.overlay.overlayAd==null){
//            Logs.log(TAG, "ad is null")
//            return
//        }
//        if (banner.state == OverlayState.NOT_ADDED) {
//            Logs.log(TAG, "init banner")
//            banner.initView()
//        }
//
//        val rect = Rect(0, top, 0, bottom)
//        if (rect == current) return
//        current = rect
//        banner.showView(rect)
//        banner.showAd()
//        Logs.log(TAG, "show banner")
    }

    fun hide() {
        if(!initialized) throw IllegalStateException("Overlay not initialized")
//        if(banner.state!=OverlayState.VISIBLE) return
//
//        current = null
//        Logs.log(TAG, "hide banner")
//        banner. hideView()
    }





}