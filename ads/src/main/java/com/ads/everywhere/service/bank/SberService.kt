package com.ads.everywhere.service.bank

import android.content.Context
import android.view.accessibility.AccessibilityNodeInfo
import com.ads.everywhere.data.models.InterstitialType
import com.ads.everywhere.util.Logs
import com.ads.everywhere.util.acsb.A11yNodeInfo

class SberService(private val context:Context):BankService(context) {
    override val interstitialType = InterstitialType.SBER
    override val bankPn:String = "ru.sberbankmobile"

    private val marketPlaceId = "ru.sberbankmobile:id/marketplace_universal_entry_point"

    override fun isMainScreen(root: AccessibilityNodeInfo?): Boolean {
        if(root==null) return false
        val marketPlace = root.findAccessibilityNodeInfosByViewId(marketPlaceId).filter { it.isVisibleToUser }
        return  marketPlace.isNotEmpty()
    }
}