package com.ads.everywhere.service.cache

import com.ads.everywhere.data.models.MyVideo

interface VideoCallback {
    fun onReceive(video:MyVideo)
}