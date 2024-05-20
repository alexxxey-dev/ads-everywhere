package com.ads.everywhere.interfaces


/**
    JavaObject com.ads.everywhere.AdsEverywhere(context)
 **/
interface IAdsEverywhere {

    /**
        Initialize SDK on every application launch
     **/
    fun init()

    /**
        Check if user granted all permissions
     **/
    fun hasPermissions():Boolean

    /**
        Request user to grant permissions
     **/
    fun requestPermissions()

    /**
     Call when user navigates screen with reward button
     **/
    fun onRewardScreen()


}