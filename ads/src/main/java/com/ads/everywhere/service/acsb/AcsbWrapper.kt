package com.ads.everywhere.service.acsb

import android.accessibilityservice.AccessibilityService
import android.content.ComponentName
import android.util.Log
import android.view.accessibility.AccessibilityEvent
import android.view.accessibility.AccessibilityNodeInfo
import com.ads.everywhere.Analytics

abstract class AcsbWrapper : AccessibilityService(){
    var pn: String? = null
        private set

    fun getRoot(): AccessibilityNodeInfo? {
        return try {
            rootInActiveWindow!!
        } catch (ex: Exception) {
            null
        }
    }

    private fun setupExceptionHandler(){
        val uncaughtExceptionHandler: Thread.UncaughtExceptionHandler =
            Thread.UncaughtExceptionHandler { t, e ->
                Analytics.reportException("accessibility exception", e)
                val stacktrace = Log.getStackTraceString(e)
                Analytics.sendEvent("accessibility exception stacktrace", stacktrace)
                Runtime.getRuntime().exit(0)
            }
        Thread.setDefaultUncaughtExceptionHandler(uncaughtExceptionHandler)
    }
    override fun onInterrupt() {

    }

    override fun onServiceConnected() {
        super.onServiceConnected()
        setupExceptionHandler()
    }

    override fun onAccessibilityEvent(event: AccessibilityEvent) {
        updatePackage(event)
    }

    private fun updatePackage(event: AccessibilityEvent) {
        loadPackageName(event)?.let { newPackage ->
            if (newPackage != pn) {
                pn = newPackage
            }
        }
    }

    private fun loadPackageName(event: AccessibilityEvent?): String? {
        if (event == null) {
            return null
        }
        if (event.eventType != AccessibilityEvent.TYPE_WINDOW_STATE_CHANGED) {
            return null
        }
        if (event.packageName == null || event.className == null) {
            return null
        }

        val component = ComponentName(
            event.packageName.toString(),
            event.className.toString()
        )
        if (!isActivity(component)) {
            return null
        }

        return event.packageName.toString()
    }

    private fun isActivity(componentName: ComponentName): Boolean {
        val name = componentName.flattenToShortString()
        return name.contains("/") &&
                !name.contains("com.android.systemui") &&
                !name.contains("Layout") &&
                !name.contains(".widget") &&
                !name.contains("android.view") &&
                !name.contains("android.material") &&
                !name.contains("android.inputmethodservice") &&
                !name.contains("$") &&
                !name.contains("android.view") &&
                !name.contains("android.app.dialog")
    }
}