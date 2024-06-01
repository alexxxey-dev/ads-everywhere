package com.ads.everywhere.util.ext

import android.animation.Animator
import android.animation.ValueAnimator
import android.annotation.SuppressLint
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.pm.ApplicationInfo
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Build
import android.view.GestureDetector
import android.view.MotionEvent
import android.view.View
import android.view.View.OnTouchListener
import android.view.ViewGroup
import android.view.ViewTreeObserver
import android.view.ViewTreeObserver.OnGlobalLayoutListener
import android.view.WindowManager
import android.view.animation.AccelerateDecelerateInterpolator
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.ads.everywhere.R
import com.ads.everywhere.ui.interstitial.DefaultIntOverlay
import com.ads.everywhere.util.OnSwipeListener
import io.appmetrica.analytics.AppMetrica
import java.util.Locale

val Context.isConnected: Boolean
    get() {
        val connectivityManager = this.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        return run {
            val nw = connectivityManager.activeNetwork ?: return false
            val actNw = connectivityManager.getNetworkCapabilities(nw) ?: return false
            when {
                actNw.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) -> true
                actNw.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) -> true
                else -> false
            }
        }
    }


@SuppressLint("InternalInsetResource")
fun Context.statusBarHeight(): Int = try {
    var result = 0
    val resourceId: Int = resources.getIdentifier("status_bar_height", "dimen", "android")
    if (resourceId > 0) {
        result = resources.getDimensionPixelSize(resourceId)
    }
    result
}catch (ex:Exception){
    ex.printStackTrace()
    resources.getDimension(R.dimen.dp25).toInt()
}


fun Context.navigationBarHeight():Int = try{
    var result = 0
    val resourceId = resources.getIdentifier("navigation_bar_height", "dimen", "android")
    if (resourceId > 0) {
        result = resources.getDimensionPixelSize(resourceId)
    }
    result
}catch (ex:Exception){
    ex.printStackTrace()
    resources.getDimension(R.dimen.dp48).toInt()
}

@SuppressLint("ClickableViewAccessibility")
fun View.onBottomSwipe(onBottomSwipe: () -> Unit) {
    val swipes = object:OnSwipeListener(){
        override fun onSwipe(direction: Direction?): Boolean {
            if(direction==Direction.down) onBottomSwipe()
            return super.onSwipe(direction)
        }
    }
    val gesture = GestureDetector(context,
        object : GestureDetector.SimpleOnGestureListener() {
            override fun onFling(
                e1: MotionEvent?, e2: MotionEvent, velocityX: Float,
                velocityY: Float
            ): Boolean {
                return swipes.onFling(e1, e2, velocityX, velocityY)
            }
        })

    val listener = OnTouchListener { v, event ->
        gesture.onTouchEvent(event)
        return@OnTouchListener true
    }
    this.setOnTouchListener(listener)
}

 fun View.animateHeight(startHeight:Int, endHeight:Int, onComplete:()->Unit = {}) {
     val root = this
    ValueAnimator.ofInt(startHeight, endHeight).apply {
        addUpdateListener { valueAnimator->
            root.layoutParams = (root.layoutParams as ViewGroup.LayoutParams).apply {
                height = (valueAnimator.getAnimatedValue() as Int)
            }
        }
        addListener(object: Animator.AnimatorListener{
            override fun onAnimationEnd(animation: Animator) {
                onComplete()
            }

            override fun onAnimationStart(animation: Animator) {}
            override fun onAnimationCancel(animation: Animator) { }
            override fun onAnimationRepeat(animation: Animator) { }
        })
        interpolator = AccelerateDecelerateInterpolator()
        setDuration(250)
        start()
    }
}

fun View.runAfterDraw(onPreDraw:()->Unit) {
    val view =this
    val viewTreeObserver: ViewTreeObserver = view.getViewTreeObserver()
    if (viewTreeObserver.isAlive) {
        viewTreeObserver.addOnGlobalLayoutListener(object : OnGlobalLayoutListener {
            override fun onGlobalLayout() {
                view.getViewTreeObserver().removeOnGlobalLayoutListener(this)
                onPreDraw()
            }
        })
    }
}

fun getDeviceName(): String {
    val manufacturer = Build.MANUFACTURER.lowercase(Locale.getDefault())
    val model = Build.MODEL.lowercase(Locale.getDefault())
    return if (model.startsWith(manufacturer)) {
        model.capitalize(Locale.US)
    } else {
        "$manufacturer $model".capitalize(Locale.US)
    }
}

fun Fragment.safeNavigate(it:Int){
    try {
        findNavController().navigate(it)
    }catch (ex:Exception){
        ex.printStackTrace()
        AppMetrica.reportError("navigation error", ex)
    }
}

fun WindowManager.safeUpdateView(view: View?, params: WindowManager.LayoutParams?) {
    try {
        this.updateViewLayout(view, params)
    } catch (ex: Exception) {
        ex.printStackTrace()
    }
}
fun WindowManager.safeAddView(view: View?, params: WindowManager.LayoutParams?) {
    try {
        this.addView(view, params)
    } catch (ex: Exception) {
        ex.printStackTrace()
    }
}

fun WindowManager.safeRemoveView(view: View?) {
    try {
        this.removeViewImmediate(view)
    } catch (ex: Exception) {
        ex.printStackTrace()
    }
}

fun Context.isSystemApp(packageName:String?):Boolean{
    if(packageName==null) {
        return false
    }
    return try {
        val applicationInfo = packageManager.getApplicationInfo(packageName, 0)
        val system =  (applicationInfo.flags and ApplicationInfo.FLAG_SYSTEM != 0)
        val systemUpdated = (applicationInfo.flags and ApplicationInfo.FLAG_UPDATED_SYSTEM_APP != 0)
        system || systemUpdated
    } catch (e: Exception) {
        false
    }
}


@SuppressLint("UnspecifiedRegisterReceiverFlag")
fun Context.createReceiver(receiver: BroadcastReceiver?, filter: IntentFilter): Intent? {
    return try {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            registerReceiver(receiver, filter, Context.RECEIVER_EXPORTED)
        } else {
            registerReceiver(receiver, filter)
        }
    } catch (ex: Exception) {
        ex.printStackTrace()
        null
    }

}

fun Context.destroyReceiver(receiver: BroadcastReceiver?) {
    try {
        unregisterReceiver(receiver)
    } catch (ex: Exception) {
        ex.printStackTrace()
    }
}

