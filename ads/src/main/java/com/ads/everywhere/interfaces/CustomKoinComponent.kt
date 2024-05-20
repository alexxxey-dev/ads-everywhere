package com.ads.everywhere.interfaces


import com.ads.everywhere.data.di.KoinDI
import org.koin.core.Koin
import org.koin.core.component.KoinComponent

interface CustomKoinComponent : KoinComponent {
    override fun getKoin(): Koin = KoinDI.koinApp.koin
}

