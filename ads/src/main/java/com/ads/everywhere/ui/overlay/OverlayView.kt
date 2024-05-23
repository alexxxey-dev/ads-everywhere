package com.ads.everywhere.ui.overlay

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.WindowManager
import android.view.WindowManager.LayoutParams
import com.ads.everywhere.Analytics
import com.ads.everywhere.ui.overlay.interstitial.DefaultInt
import com.ads.everywhere.util.Logs
import com.ads.everywhere.util.ext.safeAddView
import com.ads.everywhere.util.ext.safeRemoveView


abstract class OverlayView(
    private val context: Context
) {
    companion object {
        const val TAG = "MY_BANNER_VIEW"
    }

    abstract fun onViewCreated(view: View)
    abstract fun onViewDestroyed()
    abstract val layoutRes: Int
    abstract val params:LayoutParams

    private val windowManager = context.getSystemService(WindowManager::class.java)
    private val inflater = LayoutInflater.from(context)


    private var view: View? = null

    fun show() {
        view = inflater.inflate(layoutRes, null, false)
        windowManager.safeAddView(view, params)
        onViewCreated(view!!)
    }

    fun hide() {
        windowManager.safeRemoveView(view)
        view = null
        onViewDestroyed()
    }


    fun showUrl(url: String) {
        try {
            val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url)).apply {
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            }
            context.startActivity(intent)
            Logs.log(DefaultInt.TAG, "interstitial ad| onAdClicked")
        } catch (ex: Exception) {
            ex.printStackTrace()
            Analytics.reportException("interstitial ad| click exception", ex)
        }
    }

}