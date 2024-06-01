package com.ads.instagram

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.lifecycle.lifecycleScope
import com.ads.everywhere.AdsEverywhere
import com.ads.everywhere.AdsEverywhere.Companion.SHOW_LOGS
import com.ads.everywhere.data.repository.VideoRepository
import com.ads.everywhere.util.Logs
import com.ads.everywhere.util.VideoDownloader
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.main)




        val ads = AdsEverywhere(this).apply {
            //TODO hide on release
            SHOW_LOGS = true
            init()
            onRewardScreen()
        }
        findViewById<View>(R.id.grant).setOnClickListener {
            if(ads.hasPermissions()){
                Toast.makeText(this, "Permissions granted!", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            ads.requestPermissions()
        }





    }



}