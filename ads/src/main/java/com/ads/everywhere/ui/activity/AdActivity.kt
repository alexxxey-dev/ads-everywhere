package com.ads.everywhere.ui.activity

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Bundle
import androidx.annotation.Keep
import androidx.appcompat.app.AppCompatActivity
import com.ads.everywhere.Analytics
import com.ads.everywhere.data.IronSourceController
import com.ads.everywhere.util.Logs
import com.ads.everywhere.util.ext.createReceiver
import com.ads.everywhere.util.ext.destroyReceiver
import com.ads.everywhere.util.ext.hideSystemUI
import com.ironsource.mediationsdk.IronSource


@Keep
class AdActivity : AppCompatActivity() {


    companion object {
        const val TAG = "IRON_SOURCE_SERVICE"
        const val SHOW_INTERSTITIAL = "AdActivity_SHOW_INTERSTITIAL"
        const val STOP = "AdActivity_STOP"

        private fun isUnity() = try {
            Class.forName("com.unity3d.player.UnityPlayer")
            true
        } catch (e: ClassNotFoundException) {
            false
        }

        fun start(context: Context) {
            if (isUnity()) startUnityActivity(context) else startAdActivity(context)
        }


        fun stop(context: Context) {
            if(isUnity()) stopUnityActivity(context) else stopAdActivity(context)
        }

        private fun stopUnityActivity(context:Context){
            try {
                val intent = Intent(context, UnityPlayerActivity::class.java).apply {
                    addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                    addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
                    putExtra(STOP, true)
                }
                context.startActivity(intent)
            } catch (ex: Exception) {
                ex.printStackTrace()
                Analytics.reportException("start unity error", ex)
            }
        }

        private fun stopAdActivity(context: Context) {
            try {
                val intent = Intent(context, AdActivity::class.java).apply {
                    addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                    addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
                    putExtra(STOP, true)
                }
                context.startActivity(intent)
            } catch (ex: Exception) {
                ex.printStackTrace()
                Analytics.reportException("start activity error", ex)
            }

        }

        private fun startUnityActivity(context: Context) {
            try {
                val intent = Intent(context, UnityPlayerActivity::class.java).apply {
                    addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                    putExtra(SHOW_INTERSTITIAL, true)
                }
                context.startActivity(intent)
            } catch (ex: Exception) {
                ex.printStackTrace()
                Analytics.reportException("start unity error", ex)
            }
        }

        private fun startAdActivity(context: Context) {
            try {
                val intent = Intent(context, AdActivity::class.java).apply {
                    addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                    addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK)
                    addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY)
                    putExtra(SHOW_INTERSTITIAL, true)
                }
                context.startActivity(intent)
            } catch (ex: Exception) {
                ex.printStackTrace()
                Analytics.reportException("start activity error", ex)
            }

        }
    }



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        hideSystemUI()
        checkStop()
        checkInterstitial()
    }

    override fun onResume() {
        super.onResume()
        hideSystemUI()
    }

    private fun checkInterstitial(){
        val show = intent?.getBooleanExtra(SHOW_INTERSTITIAL, false) ?: false
        if(show)   {
            Logs.log(IronSourceController.TAG, "adActivity; showInterstitial")
            IronSource.showInterstitial(this)
        }
    }
    private fun checkStop(){
        val stop = intent?.getBooleanExtra(STOP, false) ?: false
        if(stop) {
            Logs.log(IronSourceController.TAG, "adActivity; hideInterstitial")
            finishAffinity()
        }
    }


    override fun onBackPressed() {}
}