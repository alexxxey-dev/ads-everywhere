package com.ads.everywhere.data.service.cache

import com.ads.everywhere.data.models.MyVideo

interface VideoCallback {
    fun onReceive(video:MyVideo)
}