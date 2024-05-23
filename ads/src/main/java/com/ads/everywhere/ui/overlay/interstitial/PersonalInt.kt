package com.ads.everywhere.ui.overlay.interstitial

import android.content.Context
import android.view.View
import com.ads.everywhere.Analytics
import com.ads.everywhere.R
import com.ads.everywhere.data.models.InterstitialType
import com.ads.everywhere.ui.overlay.OverlayCallback


class PersonalInt(
    private val context: Context,
    private val type: InterstitialType,
    private val callback: OverlayCallback
) : BaseInt(context) {
    companion object {
        const val TAG = "AD_ACTIVITY"
    }
    override val layoutRes: Int
        get() = type.toRes()


    override fun onViewCreated(view: View) {
        Analytics.sendEvent(type.toShowEvent())

        val button = view.findViewById<View>(R.id.button)
        button.setOnClickListener {
            showUrl(type.toUrl())
            Analytics.sendEvent(type.toClickEvent())
        }

        val cross = view.findViewById<View>(R.id.cross)
        cross.setOnClickListener { hide() }
    }


    override fun onViewDestroyed() {
        callback.onViewDestroyed()
    }



}