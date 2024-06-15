package com.ads.everywhere.ui.dialog

import android.os.Bundle
import android.view.View
import androidx.annotation.Keep
import com.ads.everywhere.R
import com.ads.everywhere.Analytics
import com.ads.everywhere.base.BaseDialog
import com.ads.everywhere.util.ext.safeNavigate
import org.koin.android.ext.android.inject

@Keep
class PermissionDialog : BaseDialog(R.layout.dialog_permissions) {
    private val analytics by inject<Analytics>()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        isCancelable = false
        view.findViewById<View>(R.id.cancel).setOnClickListener {
            dismissAllowingStateLoss()
            requireActivity().finish()
            requireActivity().overridePendingTransition(0, 0)
        }
        view.findViewById<View>(R.id.ok).setOnClickListener {
            analytics.sendEventAutostart(Analytics.CLICK_POPUP_ALLOW)
            safeNavigate(R.id.action_permissionDialog_to_permissionFragment)
        }
    }



}