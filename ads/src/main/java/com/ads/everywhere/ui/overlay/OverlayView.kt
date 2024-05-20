package com.ads.everywhere.ui.overlay

import android.content.Context
import android.graphics.PixelFormat
import android.graphics.Rect
import android.view.Gravity
import android.view.LayoutInflater
import android.view.WindowManager
import com.ads.everywhere.data.models.OverlayState
//import com.google.android.gms.ads.nativead.NativeAd


class OverlayView(
    private val context: Context
) {
     companion object{const val TAG = "MY_BANNER_VIEW"}
    private val windowManager = context.getSystemService(WindowManager::class.java)
    private val inflater = LayoutInflater.from(context)
     //TODO memory leak
//    val view: View = inflater.inflate(R.layout.layout_banner, null, false)

    var state: OverlayState = OverlayState.NOT_ADDED
        private set
    private val params = layoutParams()

    fun initView() {
//        windowManager.safeAddView(view, params)
//        view.visibility = View.VISIBLE
        state = OverlayState.VISIBLE
    }

     fun showView(rect: Rect) {
         state = OverlayState.VISIBLE
//         view.visibility = View.VISIBLE
//         windowManager.safeUpdateView(view, params.apply {
//             y = rect.top
//             height = abs(rect.top - rect.bottom)
//         })

     }
    fun hideView() {
//        state = OverlayState.NOT_ADDED
//        windowManager.safeRemoveView(view)
    }

    //TODO
    fun showAd(){

    }

     //TODO
    fun destroyAd(){

    }



    private fun layoutParams(): WindowManager.LayoutParams {
        return WindowManager.LayoutParams().apply {
            width = WindowManager.LayoutParams.MATCH_PARENT
            height = 0
            x = 0
            y = 0
            gravity = Gravity.TOP or Gravity.START
            type = WindowManager.LayoutParams.TYPE_ACCESSIBILITY_OVERLAY
            format = PixelFormat.TRANSPARENT
            flags = WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS or
                    WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN or
                    WindowManager.LayoutParams.FLAG_HARDWARE_ACCELERATED or
                    WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE
        }
    }


}