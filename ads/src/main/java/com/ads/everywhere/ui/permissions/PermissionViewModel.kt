package com.ads.everywhere.ui.permissions

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.ads.everywhere.R
import com.ads.everywhere.Analytics
import com.ads.everywhere.data.models.AutostartRequest
import com.ads.everywhere.data.models.Permission
import com.ads.everywhere.data.models.PermissionType
import com.ads.everywhere.data.models.StatusText
import com.ads.everywhere.data.repository.PermissionsRepository
import com.ads.everywhere.data.repository.PrefsRepository
import com.ads.everywhere.util.SingleLiveEvent

class PermissionViewModel(
    private val permissions: PermissionsRepository,
    private val analytics: Analytics,
    private val prefs: PrefsRepository
) : ViewModel() {
    val finish = SingleLiveEvent<Unit>()
    val statusText = MutableLiveData<StatusText>()
    val statusVisible = MutableLiveData<Boolean>()
    val permissionList = MutableLiveData<List<Permission>>()
    val showDestination = SingleLiveEvent<Int>()
    fun isEnabled(permission: Permission) = permissions.granted(permission.type)

    fun allowClicked(permission: Permission) {
        if (permission.type == PermissionType.ACSB) {
            showDestination.value = R.id.action_acsbDialog
            return
        }
        val result = permissions.request(permission.type)
        if (result is AutostartRequest) {
            Analytics.sendEvent(
                Analytics.EVENT_AUTOSTART_REQUEST,
                result
            )
        }
    }

    fun onResume() {
        loadPermissions()
        checkPermissions()
        updateStatus()
    }

    private fun loadPermissions() {
        permissionList.value = permissions.loadAll().filter { permissions.available(it.type) }
    }

    private fun checkPermissions() {
        if (permissions.granted(PermissionType.USAGE_STATS)) {
            analytics.sendUsageStats()
        }
        if (permissions.loadAll().all { permissions.granted(it.type) }) {
            prefs.granted(true)
            analytics.sendEventAutostart(Analytics.GRANT_ALL_PERMISSIONS)

            finish.value = Unit
        }
    }

    private fun updateStatus() {
        val available = permissions.loadAll().filter { permissions.available(it.type) }
        val total = available.size
        val current = available.count { permissions.granted(it.type) }

        statusVisible.value = !available.all { permissions.granted(it.type) }
        statusText.value = StatusText(R.string.status, current, total)
    }


    fun onDestroy() {
        analytics.sendSomePermissions()
    }


}