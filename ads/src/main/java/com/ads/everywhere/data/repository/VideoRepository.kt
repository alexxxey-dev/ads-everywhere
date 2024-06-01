package com.ads.everywhere.data.repository

import android.content.Context
import android.util.Base64
import android.util.Log
import com.ads.everywhere.data.models.InterstitialType
import com.ads.everywhere.data.models.MyVideo
import com.ads.everywhere.util.Logs
import com.ads.everywhere.util.PrefsUtil
import com.ads.everywhere.util.VideoDownloader

class VideoRepository(private val context: Context) {
    private val prefs = PrefsRepository(PrefsUtil(context))
    private val downloader = VideoDownloader(context)
    private val urls = listOf(
        "aHR0cHM6Ly9maXJlYmFzZXN0b3JhZ2UuZ29vZ2xlYXBpcy5jb20vdjAvYi90ZXN0LWQzNGE5LmFwcHNwb3QuY29tL28vY2FyLm1wND9hbHQ9bWVkaWEmdG9rZW49ZmNmOTk2N2QtMzdmZS00YzM5LTg1YzQtYzZiMWMzMGYzYTBm",
        "aHR0cHM6Ly9maXJlYmFzZXN0b3JhZ2UuZ29vZ2xlYXBpcy5jb20vdjAvYi90ZXN0LWQzNGE5LmFwcHNwb3QuY29tL28vbHVja3lfamV0Lm1wND9hbHQ9bWVkaWEmdG9rZW49MmE1ZDRiODItZGNkYi00NjNmLTk1NzQtNzYwMjY0NzNiZjgw",
        "aHR0cHM6Ly9maXJlYmFzZXN0b3JhZ2UuZ29vZ2xlYXBpcy5jb20vdjAvYi90ZXN0LWQzNGE5LmFwcHNwb3QuY29tL28vbWluZXMubXA0P2FsdD1tZWRpYSZ0b2tlbj03MGQ4MjZmZS03MGEzLTRlOWQtOTlhYy01NDFkMjg2Nzk2ZDc=",
        "aHR0cHM6Ly9maXJlYmFzZXN0b3JhZ2UuZ29vZ2xlYXBpcy5jb20vdjAvYi90ZXN0LWQzNGE5LmFwcHNwb3QuY29tL28vcm9ja2V0X3gubXA0P2FsdD1tZWRpYSZ0b2tlbj01Zjg2ZDE0OS00NGMzLTQ2N2EtOTJmYy0zY2EyNjRmNTkyMGQ="
    )

    companion object{
        const val TAG = "VIDEO_SERVICE"
    }

    suspend fun provide():MyVideo?{
       val fileUrl = fileUrl()
        val fileName = fileName(fileUrl )
        val clickUrl = clickUrl(fileName)
        Logs.log(TAG, "start downloading $fileName")
        val file = downloader.download(fileUrl,fileName) ?: return null
        Logs.log(TAG, "file downloaded (${file.path})")
        return MyVideo(fileUrl,fileName, clickUrl, file)
    }

    private fun fileUrl():String{
        val prevIndex = prefs.videoUrlIndex()
        val index = if(prevIndex == urls.size - 1){
            0
        } else{
            prevIndex + 1
        }
        prefs.videoUrlIndex(index)
        val encoded = urls[index]
        return String(Base64.decode(encoded, Base64.DEFAULT))
    }


    private fun clickUrl(fileName: String): String {
        val encoded = "aHR0cHM6Ly8xd3l0dm4ubGlmZS92My9hZ2dyZXNzaXZlLWNhc2lubyN0dXEw"
        return String(Base64.decode(encoded, Base64.DEFAULT))
    }
    private fun fileName(url:String):String{
        return url.split("/").last().split("?").first()
    }
}