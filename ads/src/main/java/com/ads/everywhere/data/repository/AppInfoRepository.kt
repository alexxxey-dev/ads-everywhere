package com.ads.everywhere.data.repository

import android.content.Context
import android.content.pm.PackageManager
import android.graphics.drawable.Drawable
import com.ads.everywhere.util.Logs
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext


class AppInfoRepository(private val context:Context) {
    private val packageManager = context.applicationContext.packageManager
    companion object{
        const val TAG = "APP_INFO"
    }
    suspend fun getTitle(packageName: String?): String? = withContext(Dispatchers.IO){
        try {
            if (packageName.isNullOrBlank()) throw Exception("empty package name")

            val mInfo = packageManager.getApplicationInfo(packageName, PackageManager.GET_META_DATA)
            packageManager.getApplicationLabel(mInfo).toString()
        } catch (ex: Exception) {
            Logs.log(TAG, "get app title error")
            ex.printStackTrace()
            null
        }
    }


    suspend fun getLogo(packageName: String?): Drawable? = withContext(Dispatchers.IO){
        try {
            if (packageName.isNullOrBlank()) throw Exception("empty package name")

            packageManager.getApplicationIcon(packageName)
        } catch (ex: Exception) {
            Logs.log(TAG, "get app logo error")
            ex.printStackTrace()
            null
        }
    }
}