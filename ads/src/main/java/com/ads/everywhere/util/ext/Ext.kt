package com.ads.everywhere.util.ext

import android.annotation.SuppressLint
import android.app.ActivityManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.pm.ApplicationInfo
import android.os.Build
import android.view.View
import android.view.WindowManager
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.ads.everywhere.util.Logs
import io.appmetrica.analytics.AppMetrica
import java.util.Locale



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
        e.printStackTrace()
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

