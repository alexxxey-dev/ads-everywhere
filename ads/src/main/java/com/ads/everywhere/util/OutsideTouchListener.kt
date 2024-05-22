package com.ads.everywhere.util

import android.annotation.SuppressLint
import android.view.MotionEvent
import android.view.View

class OutsideTouchListener(private val onTouchOutside:()->Unit) : View.OnTouchListener {
    @SuppressLint("ClickableViewAccessibility")
    override fun onTouch(v: View, event: MotionEvent): Boolean {
        if (event.action == MotionEvent.ACTION_OUTSIDE) {
            onTouchOutside()
            return true
        }
        return false
    }

}