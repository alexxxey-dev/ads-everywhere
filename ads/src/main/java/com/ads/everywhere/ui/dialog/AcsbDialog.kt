package com.ads.everywhere.ui.dialog

import android.os.Bundle
import android.view.View
import androidx.annotation.Keep
import com.ads.everywhere.R
import com.ads.everywhere.data.models.PermissionType
import com.ads.everywhere.data.repository.PermissionsRepository
import com.ads.everywhere.base.BaseDialog
import org.koin.android.ext.android.inject

@Keep
class AcsbDialog : BaseDialog(R.layout.dialog_acsb) {
    private val permissions by inject<PermissionsRepository>()
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        view.findViewById<View>(R.id.allow).setOnClickListener {
            permissions.request(PermissionType.ACSB)
            dismissAllowingStateLoss()
        }
    }
}