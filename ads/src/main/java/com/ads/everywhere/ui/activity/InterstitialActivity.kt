package com.ads.everywhere.ui.activity

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Bundle
import androidx.annotation.Keep
import androidx.appcompat.app.AppCompatActivity
import com.ads.everywhere.controller.ActivityController.handleIntent
import com.ads.everywhere.controller.MinController
import com.ads.everywhere.util.ext.createReceiver
import com.ads.everywhere.util.ext.destroyReceiver
import com.ads.everywhere.util.ext.hideSystemUI


@Keep
class InterstitialActivity : AppCompatActivity() {
    private val receiver = object:BroadcastReceiver(){
        override fun onReceive(context: Context?, intent: Intent?) {
            if(intent?.action == MinController.AD_CLOSED){
                finishAffinity()
                overridePendingTransition(0,0)
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        hideSystemUI()
        createReceiver(receiver, IntentFilter(MinController.AD_CLOSED))
        handleIntent(this, intent)
    }

    override fun onDestroy() {
        destroyReceiver(receiver)
        super.onDestroy()
    }


    override fun onBackPressed() {}
}