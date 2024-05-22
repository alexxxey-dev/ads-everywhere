package com.ads.everywhere.ui.overlay

import android.content.Context
import android.graphics.PixelFormat
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.WindowManager
import com.ads.everywhere.data.models.OverlaySize
import com.ads.everywhere.util.ScreenMetricsCompat
import com.ads.everywhere.util.ext.safeAddView
import com.ads.everywhere.util.ext.safeRemoveView

//import com.google.android.gms.ads.nativead.NativeAd


abstract class OverlayView(
    private val context: Context
) {
     companion object{const val TAG = "MY_BANNER_VIEW"}

    abstract fun onViewCreated(root:View)
    abstract fun onViewDestroyed()
    abstract val layoutRes:Int
    abstract val size:OverlaySize

    private val windowManager = context.getSystemService(WindowManager::class.java)
    private val inflater = LayoutInflater.from(context)

    private var view: View? = null

    fun show() {
        val params = params()
        view =  inflater.inflate(layoutRes, null, false)
        windowManager.safeAddView(view, params)
        onViewCreated(view!!)
    }

     fun hide() {
         windowManager.safeRemoveView(view)
         view = null
         onViewDestroyed()
     }


    private fun params() = WindowManager.LayoutParams().apply {
        width = size.width
        height = size.height
        x = size.x
        y = size.y
        gravity = Gravity.TOP or Gravity.START
        type = WindowManager.LayoutParams.TYPE_ACCESSIBILITY_OVERLAY
        format = PixelFormat.TRANSPARENT
        flags = WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS or
                WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN or
                WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE
    }

}