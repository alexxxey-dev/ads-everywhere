package com.ads.everywhere.service.cache

import android.content.Context
import com.ads.everywhere.data.models.MyVideo
import com.ads.everywhere.data.repository.VideoRepository
import com.ads.everywhere.util.Logs
import com.ads.everywhere.util.ext.isConnected
import io.appmetrica.analytics.impl.pn
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class CacheVideoService(private val context: Context) {
    private val videoRepository = VideoRepository(context)

    private var video: MyVideo? = null
    private var callback: VideoCallback? = null

    companion object {
        const val TAG = "VIDEO_SERVICE"
    }

    private val job = CoroutineScope(Dispatchers.IO).launch {
        while (this.isActive) {
            delay(1000)

            if(!context.isConnected) continue
            if (video != null) continue

            video = videoRepository.provide()
            withContext(Dispatchers.Main) {
                video?.let {
                    callback?.onReceive(it)
                    callback = null
                }
            }
        }
    }

    fun requestVideo(callback: VideoCallback) {
        if (video != null) {
            Logs.log(TAG, "return cached video, ignoring callback")
            callback.onReceive(video!!)
            this.callback = null
            return
        }

        Logs.log(TAG, "register video callback")
        this.callback = callback
    }


    fun onVideoWatched(pn: String) {
        video = null
        callback = null
    }

    fun onDestroy() {
        job.cancel()
    }
}