package com.ads.everywhere.util

import android.content.Context
import com.ads.everywhere.data.models.MyVideo
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.ResponseBody
import java.io.File
import java.io.FileOutputStream

class VideoDownloader(private val context: Context) {

    private val client = OkHttpClient()

    companion object {
        const val TAG = "VIDEO_SERVICE"
    }


    suspend fun download(url: String, fileName: String): File? = withContext(Dispatchers.IO) {
        val file = File(context.filesDir, fileName)
        if (file.exists()) {
            Logs.log(TAG, "file already exists")
            return@withContext file
        }

        val body = request(url) ?: return@withContext null
        saveToFile(body, file)
    }

    private fun request(url: String): ResponseBody? {
        return try {
            val request = Request.Builder()
                .url(url)
                .build()
            client.newCall(request).execute().body
        } catch (ex: Exception) {
            ex.printStackTrace()
            null
        }
    }


    private fun saveToFile(body: ResponseBody, file: File): File {
        body.byteStream().use { input ->
            FileOutputStream(file).use { output ->
                input.copyTo(output)
            }
            return file
        }
    }
}
