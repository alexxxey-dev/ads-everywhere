package com.ads.everywhere.data.repository

import android.graphics.Rect
import android.view.accessibility.AccessibilityNodeInfo
import com.ads.everywhere.data.models.Post
import com.ads.everywhere.util.Instagram
import com.ads.everywhere.util.Logs

class InstagramRepository {
    companion object {
        const val TAG = "INSTAGRAM_PARSER"
    }

     fun parseFeed(root: AccessibilityNodeInfo): List<Post>{
        val listNode = root
            .findAccessibilityNodeInfosByViewId(Instagram.MAIN_LIST)
            .firstOrNull() ?: return emptyList()
        val result = ArrayList<Post>()

        for (i in 0 until listNode.childCount) {
            listNode.getChild(i)?.let { childNode ->
                parsePost(childNode)?.let { post->
                    result.add(post)
                }
            }
        }

        return result
    }

    private fun parsePost(childNode:AccessibilityNodeInfo): Post?{
        val position = Rect()
        val mediaGroup = childNode.findAccessibilityNodeInfosByViewId(Instagram.MEDIA_GROUP).firstOrNull()
        if(mediaGroup!=null){

            mediaGroup.getBoundsInScreen(position)
            val description = mediaGroup.contentDescription?.toString()
            return Post(position,description)
        }
        val carousel = childNode.findAccessibilityNodeInfosByViewId(Instagram.CAROUSEL_MEDIA_GROUP).firstOrNull()
        if(carousel!=null){
            carousel.getBoundsInScreen(position)
            val description = carousel.contentDescription?.toString()
            return Post(position,description)
        }

        return null
    }



     fun isFeedScreen(root: AccessibilityNodeInfo?): Boolean {
        if (root == null) return false
        val media = root
            .findAccessibilityNodeInfosByViewId(Instagram.MEDIA_GROUP)
            .filter { it.isVisibleToUser }
         val carousel = root.findAccessibilityNodeInfosByViewId(Instagram.CAROUSEL_MEDIA_GROUP)
             .filter { it.isVisibleToUser }
        val largeTitle = root
            .findAccessibilityNodeInfosByViewId(Instagram.LARGE_TITLE)
            .filter { it.isVisibleToUser }
        val bottomSheet = root
            .findAccessibilityNodeInfosByViewId(Instagram.BOTTOM_SHEET)
         val isFeed =  (media.isNotEmpty() || carousel.isNotEmpty())  && bottomSheet.isEmpty() && largeTitle.isEmpty()
         if(!isFeed){
             Logs.log(TAG, "mediaSize=${media.size}; carouselSize=${carousel.size}; titleSize=${largeTitle.size}; bottomSheet=${bottomSheet.size}")
         }
        return isFeed
    }



}