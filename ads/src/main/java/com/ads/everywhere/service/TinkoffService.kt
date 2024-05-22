package com.ads.everywhere.service

import android.content.Context
import android.view.accessibility.AccessibilityNodeInfo
import com.ads.everywhere.data.models.InterstitialType
import com.ads.everywhere.service.base.BaseBankService

class TinkoffService(private val context: Context) : BaseBankService(context) {
    override val interstitialType = InterstitialType.TINK
    override val appPackage: String = "com.idamob.tinkoff.android"

    private val toolbarId = "com.idamob.tinkoff.android:id/toolbarSearchProfile"


    override fun isMainScreen(root: AccessibilityNodeInfo?): Boolean {
        if(root==null) return false
        val toolbars = root.findAccessibilityNodeInfosByViewId(toolbarId).filter { it.isVisibleToUser }
        return toolbars.isNotEmpty()
    }
}