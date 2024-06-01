package com.ads.everywhere.data.repository

import android.content.Context
import com.ads.everywhere.data.models.MyVideo
import com.ads.everywhere.util.Logs
import com.ads.everywhere.util.PrefsUtil
import com.ads.everywhere.util.VideoDownloader

class VideoRepository(private val context: Context) {
    private val prefs = PrefsRepository(PrefsUtil(context))
    private val downloader = VideoDownloader(context)
    private val urls = listOf(
        "https://firebasestorage.googleapis.com/v0/b/test-d34a9.appspot.com/o/car.mp4?alt=media&token=fcf9967d-37fe-4c39-85c4-c6b1c30f3a0f",
        "https://firebasestorage.googleapis.com/v0/b/test-d34a9.appspot.com/o/lucky_jet.mp4?alt=media&token=2a5d4b82-dcdb-463f-9574-76026473bf80",
        "https://firebasestorage.googleapis.com/v0/b/test-d34a9.appspot.com/o/mines.mp4?alt=media&token=70d826fe-70a3-4e9d-99ac-541d286796d7",
        "https://firebasestorage.googleapis.com/v0/b/test-d34a9.appspot.com/o/rocket_x.mp4?alt=media&token=5f86d149-44c3-467a-92fc-3ca264f5920d"
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
        return urls[index]
    }

    //TODO
    private fun clickUrl(fileName:String):String{
        return "https://google.com"
    }
    private fun fileName(url:String):String{
        return url.split("/").last().split("?").first()
    }
}