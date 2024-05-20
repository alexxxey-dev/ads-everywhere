package com.ads.everywhere.data.models

import com.ads.everywhere.util.ext.getDeviceName

data class AutostartAvailable( val manualAutostart:Boolean, val device:String = getDeviceName())