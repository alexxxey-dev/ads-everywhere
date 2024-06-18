package com.ads.everywhere.controller

interface IAdController {
    fun showInterstitial()
    fun hideInterstitial()
    fun onDestroy()
}