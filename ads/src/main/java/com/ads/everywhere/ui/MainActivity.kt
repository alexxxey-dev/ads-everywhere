package com.ads.everywhere.ui

import android.os.Bundle
import androidx.annotation.Keep
import androidx.navigation.fragment.NavHostFragment
import com.ads.everywhere.R
import com.ads.everywhere.ui.base.BaseActivity

@Keep
class MainActivity : BaseActivity() {


    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_ads_everywhere)
        val navHostFragment =
            supportFragmentManager.findFragmentById(R.id.nav_host_fragment) as NavHostFragment
        navHostFragment.navController.setGraph(R.navigation.nav_graph)


    }


}