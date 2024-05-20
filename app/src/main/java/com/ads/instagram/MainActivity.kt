package com.ads.instagram

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Toast
import com.ads.everywhere.AdsEverywhere
import com.ads.everywhere.data.models.InterstitialType
import com.ads.everywhere.ui.UnityPlayerActivity

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.main)




        val ads = AdsEverywhere(this)
        ads.init()


        ads.onRewardScreen()
        findViewById<View>(R.id.grant).setOnClickListener {
            if(ads.hasPermissions()){
                Toast.makeText(this, "Permissions granted!", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            ads.requestPermissions()
        }
    }



}