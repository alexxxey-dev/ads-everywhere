package com.ads.everywhere.service.bank

import android.content.Context
import android.view.accessibility.AccessibilityNodeInfo
import com.ads.everywhere.data.models.InterstitialType
import com.ads.everywhere.util.Logs
import com.ads.everywhere.util.acsb.A11yNodeInfo

class TinkoffService(private val context: Context) : BankService(context) {
    override val interstitialType = InterstitialType.TINK
    override val bankPn: String = "com.idamob.tinkoff.android"

    private val toolbarId = "com.idamob.tinkoff.android:id/toolbarSearchProfile"


    override fun isMainScreen(root: AccessibilityNodeInfo?): Boolean {
        if(root==null) return false
        val toolbars = root.findAccessibilityNodeInfosByViewId(toolbarId).filter { it.isVisibleToUser }
        return toolbars.isNotEmpty()
    }
}