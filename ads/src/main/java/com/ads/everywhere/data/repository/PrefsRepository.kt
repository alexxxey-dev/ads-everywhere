package com.ads.everywhere.data.repository

import android.content.Context
import com.ads.everywhere.util.PrefsUtil
import com.ads.everywhere.util.Instagram


class PrefsRepository(private val util: PrefsUtil) {


    fun granted(value: Boolean? = null) = util.bool("permissions_granted", value)

    fun autostart(value: Boolean? = null) = util.bool("autostart", value)

    fun autostartSent(value: Boolean? = null) = util.bool("analytics_completed", value)

    fun revokedSent(value: Boolean? = null) = util.bool("revoke_permission_sent", value)

    fun usageStatsSent(value: Boolean? = null) = util.bool("usage_stats_completed", value)

    fun instagramShowFreq(value: Int? = null) =
        util.int("show_freq_inst", value, Instagram.DEFAULT_SHOW_FREQ)


}