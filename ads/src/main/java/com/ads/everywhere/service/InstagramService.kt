package com.ads.everywhere.service

import android.content.Context
import android.graphics.Rect
import android.view.accessibility.AccessibilityEvent
import android.view.accessibility.AccessibilityNodeInfo
import com.ads.everywhere.data.models.Post
import com.ads.everywhere.data.repository.InstagramRepository
import com.ads.everywhere.data.repository.PrefsRepository
import com.ads.everywhere.util.Instagram
import com.ads.everywhere.util.Logs
//post
//profile header=row_feed_profile_header; profile name=row_feed_photo_profile_name
//media carousel; id=carousel_media_group
//action buttons; id=row_feed_view_group_buttons
//likes count; id=com.instagram.android:id/like_row
//post text; id=row_feed_comment_textview_layout (!!!! if reply in comment this appears too)
//view all comments; id=row_feed_view_all_comments_text
//add comment; id=feed_inline_composer_button_container
//time + translation;


class InstagramService(
    private val parser: InstagramRepository,
    private val overlay: OverlayService,
    private val prefs: PrefsRepository
) {
    companion object {
        const val TAG = "INSTAGRAM_SERVICE"
    }


    private var feedTopY: Int? = null
    private var feedBottomY: Int? = null

    private val visible = ArrayList<Post>()
    private val all = ArrayList<Post>()
    private var instagramOpened = false
    private var isFeedScreen: Boolean = false

    private var initialized = true

     fun create(context: Context){
        if(initialized) return
        initialized = true
    }

     fun destroy() {
//        initialized = false
//        feedTopY = null
//        feedBottomY = null
//        visible.clear()
//        all.clear()
    }

    fun update(pn:String?, event:AccessibilityEvent, root: AccessibilityNodeInfo?){
        if(!initialized) throw IllegalStateException("Instagram not initialized")

//        if(pn==null) return
//        if(root==null) return
//
//        if (pn == Instagram.PN) {
//           onAppUpdated(root, event)
//        } else {
//           onAppClosed()
//        }
    }




    private fun onAppUpdated(root: AccessibilityNodeInfo?, event: AccessibilityEvent) {
//        if(!instagramOpened){
//            Logs.log(TAG, "onAppOpened")
//        }
//        instagramOpened = true
//        if (event.eventType == AccessibilityEvent.TYPE_WINDOW_CONTENT_CHANGED) return
//
//        isFeedScreen = parser.isFeedScreen(root)
//        if (!isFeedScreen || root == null) {
//            Logs.log(TAG, "isFeed=${isFeedScreen}; rootIsNull=${root == null}")
//            overlay.hide()
//            return
//        }
//
//
//        updateFeed(root)
//        updateBounds(root)
//        showOverlay()
    }

    private fun showOverlay() {
        val post = visible.find { post ->
            val index = all.indexOfFirst { it.description.equals(post.description,true) }
            val freq = prefs.instagramShowFreq()
            index != -1 && (index + 1) % freq == 0
        }
        if (post == null) {
            Logs.log(TAG, "post is null; allSize=${all.size}; visibleSize=${visible.size}")

            overlay.hide()
            return
        }

        val maxTop = feedTopY ?: 0
        val maxBottom = feedBottomY ?: Int.MAX_VALUE
        val top = if (post.position.top < maxTop) maxTop else post.position.top
        val bottom = if (post.position.bottom > maxBottom) maxBottom else post.position.bottom
        overlay.show(top, bottom )
    }

    private fun updateFeed(root: AccessibilityNodeInfo): List<Post> {
        val feed = parser.parseFeed(root)
        visible.clear()

        feed.forEach { post ->
            visible.add(post)
            if (all.find { it.description.equals(post.description,true) } == null) {
                all.add(post)
            }
        }

        return feed
    }


    private fun updateBounds(root: AccessibilityNodeInfo) {
        root.findAccessibilityNodeInfosByViewId(Instagram.ACTION_BAR).firstOrNull()
            ?.let { actionBar ->
                val bounds = Rect()
                actionBar.getBoundsInScreen(bounds)
                feedTopY = bounds.bottom
            }
        root.findAccessibilityNodeInfosByViewId(Instagram.TAB_BAR).firstOrNull()?.let { tabBar ->
            val bounds = Rect()
            tabBar.getBoundsInScreen(bounds)
            feedBottomY = bounds.top
        }
    }

    private fun onAppClosed() {
        if (instagramOpened) {
            Logs.log(TAG, "onAppClosed")
            overlay.hide()
            instagramOpened = false
        }
    }






}