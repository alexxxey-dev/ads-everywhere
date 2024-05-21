package com.ads.everywhere.di


import com.ads.everywhere.di.KoinDI
import org.koin.core.Koin
import org.koin.core.component.KoinComponent

interface CustomKoinComponent : KoinComponent {
    override fun getKoin(): Koin = KoinDI.koinApp.koin
}

