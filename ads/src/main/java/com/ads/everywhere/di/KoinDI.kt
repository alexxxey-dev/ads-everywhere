package com.ads.everywhere.di

import android.app.AppOpsManager
import android.app.usage.UsageStatsManager
import android.content.Context
import com.ads.everywhere.Analytics
import com.ads.everywhere.data.repository.InstagramRepository
import com.ads.everywhere.util.permissions.MIUI
import com.ads.everywhere.data.repository.PermissionsRepository
import com.ads.everywhere.data.repository.PrefsRepository
import com.ads.everywhere.data.repository.UsageRepository
import com.ads.everywhere.ui.permissions.PermissionViewModel
import com.ads.everywhere.util.Logs
import com.ads.everywhere.util.PrefsUtil
import com.ads.everywhere.util.permissions.AutoStart
import org.koin.android.ext.koin.androidContext
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.core.KoinApplication
import org.koin.dsl.koinApplication
import org.koin.dsl.module


object KoinDI {
    const val TAG = "KOIN_DI"

    lateinit var koinApp: KoinApplication

    private val util = module {
        single { androidContext().applicationContext.getSystemService(Context.APP_OPS_SERVICE) as AppOpsManager }
        single { androidContext().getSystemService(Context.USAGE_STATS_SERVICE) as UsageStatsManager }
        single { PrefsUtil(androidContext()) }
        single { MIUI(androidContext(), get()) }
        single { AutoStart() }
        single { Analytics(get(), get(), get()) }
    }

    private val repositories = module {
        single { InstagramRepository() }
        single { PrefsRepository(get()) }
        single { UsageRepository(get(), androidContext()) }
        single { PermissionsRepository(get(), androidContext(), get(), get(), get()) }
    }

    private val viewModels = module {
        viewModel { PermissionViewModel(get(), get(), get()) }
    }

    fun init(context: Context) {
        if (isInitialized()) return
        Logs.log(TAG, "initialize koin")
        koinApp = koinApplication {
            androidContext(context.applicationContext)
            modules(util, repositories, viewModels)
        }
    }

    fun isInitialized() = KoinDI::koinApp.isInitialized

}