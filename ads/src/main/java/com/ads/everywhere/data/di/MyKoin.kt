package com.ads.everywhere.data.di


import org.koin.core.Koin
import org.koin.core.component.KoinComponent

interface MyKoin : KoinComponent {
    override fun getKoin(): Koin = KoinDI.koinApp.koin
}

