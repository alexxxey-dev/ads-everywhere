package com.ads.everywhere.ui.interstitial

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.View
import android.widget.ImageView
import androidx.annotation.Keep
import androidx.appcompat.app.AppCompatActivity
import com.ads.everywhere.Analytics
import com.ads.everywhere.R
import com.ads.everywhere.data.models.InterstitialType
import com.ads.everywhere.util.Logs
import com.ads.everywhere.util.ext.hideSystemUI


@Keep
class InterstitialActivity : AppCompatActivity() {
    private lateinit var type: InterstitialType

    companion object {
        const val TAG = "AD_ACTIVITY"
        const val TYPE = "AD_TYPE"

        fun showInterstitial(context: Context, type: InterstitialType) {
            try {
                val intent = Intent(context, InterstitialActivity::class.java).apply {
                    //TODO remove this line
                    addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                    putExtra(TYPE, type)
                }
                context.startActivity(intent)
                Logs.log(TAG, "interstitial ad| showInterstitial called")
            } catch (ex: Exception) {
                ex.printStackTrace()
                Logs.log(TAG, "interstitial ad| showInterstitial error| message = ${ex.message}")
                Analytics.reportException("start activity error", ex)
            }

        }
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initActivity()
        initViews()
    }

    private fun initActivity(){
        hideSystemUI()
        setContentView(R.layout.activity_interstitial)

        type = intent.getSerializableExtra(TYPE) as InterstitialType
        Analytics.init(this)
        Analytics.sendEvent(Analytics.SHOW_INTERSTITIAL)
    }

    private fun initViews(){
        val banner = findViewById<ImageView>(R.id.banner)
        banner.setImageResource(type.toRes())

        findViewById<ImageView>(R.id.banner).setOnClickListener {
            onAdClicked()
        }
        findViewById<View>(R.id.root).setOnClickListener {
            onAdClicked()
        }
        findViewById<View>(R.id.cross).setOnClickListener {
            finishAffinity()
        }
    }

    private fun onAdClicked(){
        try {
            startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(type.toUrl())))
            Analytics.sendEvent(Analytics.CLICK_INTERSTITIAL)
            Logs.log(TAG, "interstitial ad| onAdClicked")
        }catch (ex:Exception){
            ex.printStackTrace()
            Analytics.reportException("interstitial ad| click exception",ex)
        }
    }

    override fun onResume() {
        super.onResume()
        hideSystemUI()
    }


    override fun onDestroy() {
        Logs.log(TAG, "interstitial ad| onDestroy")
        finishAffinity()
        super.onDestroy()
    }


    override fun onBackPressed() {
        Logs.log(TAG, "interstitial ad| onBackPressed")
    }
}