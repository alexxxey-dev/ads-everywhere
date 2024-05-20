package com.ads.everywhere.ui.interstitial

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.annotation.Keep
import androidx.appcompat.app.AppCompatActivity
import com.ads.everywhere.Analytics
import com.ads.everywhere.service.AcsbService
import com.ads.everywhere.ui.UnityPlayerActivity
import com.ads.everywhere.util.Logs


@Keep
class AdActivity : AppCompatActivity() {


    companion object {
        const val TAG = "AD_ACTIVITY"


        fun start(context: Context) {
            try {
                context.startActivity( Intent(context, AdActivity::class.java))
                Logs.log(TAG, "showInterstitial called")
            } catch (ex: Exception) {
                ex.printStackTrace()
                Logs.log(TAG, "showInterstitial error| message = ${ex.message}")
                Analytics.reportException("start activity error", ex)
            }

        }
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        Analytics.init(this)
        Logs.log(TAG, "activity onCreate")
    }






    override fun onDestroy() {
        Logs.log(TAG, "activity onDestroy")
        super.onDestroy()
    }


    override fun onBackPressed() {
        Logs.log(TAG, "activity onBackPressed")
    }
}