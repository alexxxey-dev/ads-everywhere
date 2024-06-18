package com.ads.everywhere.controller

import android.app.Activity
import android.content.Context
import android.content.Intent
import com.ads.everywhere.Analytics
import com.ads.everywhere.ui.activity.InterstitialActivity
import com.ads.everywhere.ui.activity.UnityPlayerActivity
import com.ads.everywhere.util.Logs
import com.ads.everywhere.util.ext.isUnity
import com.ironsource.mediationsdk.IronSource

object ActivityController {
     const val SHOW_MIN_INT = "SHOW_MIN_INT"
     const val HIDE_MIN_INT = "HIDE_MIN_INT"
     const val SHOW_IRON_INT = "SHOW_IRON_INT"
     const val HIDE_IRON_INT = "HIDE_IRON_INT"



    fun start(context: Context, key:String) {
        if (isUnity()) {
            startUnityActivity(context, key)
        } else {
            startAdActivity(context, key)
        }
    }

    fun stop(context: Context, key:String) {
        if (isUnity()) {
            stopActivity(context, key, UnityPlayerActivity::class.java)
        } else {
            stopActivity(context, key, InterstitialActivity::class.java)
        }
    }

    fun handleIntent(activity: Activity, intent: Intent?){
        hideMinInt(activity, intent)
        hideIronInt(activity,intent)

        showIronInt(activity,intent)
        showMinInt(activity, intent)
    }


    private fun showIronInt(activity: Activity, intent: Intent?) {
        val show = intent?.getBooleanExtra(SHOW_IRON_INT, false) ?: false
        if (show) {
            Logs.log(IronController.TAG, "adActivity; showInterstitial")
            IronSource.showInterstitial(activity)
            activity.overridePendingTransition(0, 0)
        }
    }

    private fun hideIronInt(activity: Activity, intent: Intent?) {
        val stop = intent?.getBooleanExtra(HIDE_IRON_INT, false) ?: false
        if (stop) {
            Logs.log(IronController.TAG, "adActivity; hideInterstitial")
            activity.finishAffinity()
            activity.overridePendingTransition(0, 0)
        }
    }

    private fun showMinInt(activity: Activity, intent: Intent?) {
        val show = intent?.getBooleanExtra(SHOW_MIN_INT, false) ?: false
        if (show) {
            Logs.log(MinController.TAG, "adActivity; showInterstitial")
            activity.sendBroadcast(Intent(MinController.SHOW_INT))
            activity.overridePendingTransition(0, 0)
        }
    }

    private fun hideMinInt(activity: Activity, intent: Intent?) {
        val stop = intent?.getBooleanExtra(HIDE_MIN_INT, false) ?: false
        if (stop) {
            Logs.log(MinController.TAG, "adActivity; hideInterstitial")
            activity.finishAffinity()
            activity.overridePendingTransition(0, 0)
        }
    }

    private fun stopActivity(context: Context, key:String, cls:Class<*>) {
        try {
            val intent = Intent(context, cls).apply {
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
                addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION)
                putExtra(key, true)
            }
            context.startActivity(intent)
        } catch (ex: Exception) {
            ex.printStackTrace()
            Analytics.reportException("start unity error", ex)
        }
    }



    private fun startUnityActivity(context: Context, key:String) {
        try {
            val intent = Intent(context, UnityPlayerActivity::class.java).apply {
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION)
                putExtra(key, true)
            }
            context.startActivity(intent)
        } catch (ex: Exception) {
            ex.printStackTrace()
            Analytics.reportException("start unity error", ex)
        }
    }

    private  fun startAdActivity(context: Context, key:String) {
        try {
            val intent = Intent(context, InterstitialActivity::class.java).apply {
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK)
                addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION)
                addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY)
                putExtra(key, true)
            }
            context.startActivity(intent)
        } catch (ex: Exception) {
            ex.printStackTrace()
            Analytics.reportException("start activity error", ex)
        }

    }
}