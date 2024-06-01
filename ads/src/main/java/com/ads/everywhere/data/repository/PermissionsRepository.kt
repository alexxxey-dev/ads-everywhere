package com.ads.everywhere.data.repository

import android.Manifest
import android.accessibilityservice.AccessibilityServiceInfo
import android.app.AppOpsManager
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.PowerManager
import android.os.Process
import android.provider.Settings
import android.text.TextUtils
import android.view.accessibility.AccessibilityManager
import android.widget.Toast
import com.ads.everywhere.R
import com.ads.everywhere.data.models.Permission
import com.ads.everywhere.data.models.PermissionType
import com.ads.everywhere.data.models.AutostartRequest
import com.ads.everywhere.data.service.acsb.AcsbService
import com.ads.everywhere.util.permissions.AutoStart
import com.ads.everywhere.util.Logs
import com.ads.everywhere.util.permissions.MIUI

class PermissionsRepository(
    private val prefs: PrefsRepository,
    private val context: Context,
    private val miui: MIUI,
    private val autostart: AutoStart,
    private val appOps:AppOpsManager
) {
    companion object {
        const val TAG = "MY_PERMISSIONS"
    }

     fun granted(type: PermissionType): Boolean {
         when (type) {
            PermissionType.BACKGROUND -> {
                return if (miui.isMIUI()) miui.startFromBackgroundGranted() else true
            }
            PermissionType.ACSB -> return checkAcsbA() || checkAcsbB()
            PermissionType.USAGE_STATS -> return usageStatsEnabled()
            PermissionType.AUTO_START -> {
                if (!autostartAvailable()) return true
                return if (miui.isMIUI()) miui.autostartGranted() else prefs.autostart()
            }
            PermissionType.BATTERY -> return batteryEnabled()
        }
    }

     fun available(type: PermissionType): Boolean {
        return when (type) {
            PermissionType.BACKGROUND -> miui.isMIUI()
            PermissionType.AUTO_START -> autostartAvailable()
            else -> true
        }
    }

     fun request(type: PermissionType):Any? {
        return  when (type) {
            PermissionType.BATTERY -> requestBattery()
            PermissionType.BACKGROUND -> miui.requestStartFromBackground()
            PermissionType.ACSB -> requestAcsb()
            PermissionType.USAGE_STATS -> requestUsageStats()
            PermissionType.AUTO_START -> {
               requestAutostart()

            }
        }
    }

     fun loadAll(): List<Permission> {
        return listOf(
            Permission(R.string.permission_usage_access, PermissionType.USAGE_STATS),
            Permission(R.string.permission_background, PermissionType.BACKGROUND),
            Permission(R.string.permission_autostart, PermissionType.AUTO_START),
            Permission(R.string.permission_acsb, PermissionType.ACSB)
        )
    }

    private fun usageStatsEnabled(): Boolean {
        val mode = appOps.checkOpNoThrow(
            AppOpsManager.OPSTR_GET_USAGE_STATS,
            Process.myUid(), context.packageName
        )

        return if (mode == AppOpsManager.MODE_DEFAULT) {
            context.checkCallingOrSelfPermission(Manifest.permission.PACKAGE_USAGE_STATS) == PackageManager.PERMISSION_GRANTED
        } else {
            mode == AppOpsManager.MODE_ALLOWED
        }
    }

    private fun requestUsageStats() {
        try {
            val intent = Intent(
                Settings.ACTION_USAGE_ACCESS_SETTINGS,
                Uri.parse("package:${context.packageName}")
            )
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            context.startActivity(intent)
        } catch (ex: Exception) {
            ex.printStackTrace()
            try {
                val intent = Intent(Settings.ACTION_USAGE_ACCESS_SETTINGS)
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                context.startActivity(intent)
            } catch (ex: Exception) {
                ex.printStackTrace()
            }
        }
    }

    private fun autostartAvailable(): Boolean {
        return autostart.autoStartBruteforce(context, open = false, newTask = false)
    }

    private fun batteryEnabled() = (context.getSystemService(Context.POWER_SERVICE) as PowerManager)
        .isIgnoringBatteryOptimizations(context.packageName)

    private fun requestBattery() {
        try {
            val intent = Intent(
                Settings.ACTION_REQUEST_IGNORE_BATTERY_OPTIMIZATIONS,
                Uri.parse("package:${context.packageName}")
            )
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            context.startActivity(intent)
        } catch (ex: java.lang.Exception) {
            ex.printStackTrace()
            try {
                val intent = Intent(
                    Settings.ACTION_REQUEST_IGNORE_BATTERY_OPTIMIZATIONS
                )
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                context.startActivity(intent)
            } catch (ex: Exception) {
                ex.printStackTrace()
            }
        }
    }


    private fun requestAcsb() {
        try {
            val intent =
                Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            context.startActivity(intent)
        } catch (ex: Exception) {
            ex.printStackTrace()
            Logs.log(TAG, "error requesting acsb; message = ${ex.message}")
        }
    }


    private fun requestAutostart(): AutostartRequest? {
        val request = try {
            autostart.autoStartBruteforce(context, open = true, newTask = false)
            prefs.autostart(true)
            AutostartRequest(showSettingsScreen = false, showAutostartScreen = true)
        } catch (e: Exception) {
            e.printStackTrace()
            try {
                val appName = context.getString(R.string.app_name)
                val intent = Intent(Settings.ACTION_SETTINGS)
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                context.startActivity(intent)
                Toast.makeText(
                    context,
                    "Приложения -> Автозапуск -> Включить $appName",
                    Toast.LENGTH_LONG
                ).show()
                prefs.autostart(true)
                AutostartRequest(showSettingsScreen = true, showAutostartScreen = false)
            } catch (ex: Exception) {
                Logs.log(TAG, "open settings exception; message = ${ex.message}")
                ex.printStackTrace()
                null
            }


        }

        return request
    }


    private fun checkAcsbA(): Boolean {
        val am = context.getSystemService(Context.ACCESSIBILITY_SERVICE) as AccessibilityManager
        val enabledServices =
            am.getEnabledAccessibilityServiceList(AccessibilityServiceInfo.FEEDBACK_ALL_MASK)
        for (enabledService in enabledServices) {
            val enabledServiceInfo = enabledService.resolveInfo.serviceInfo
            val packageOk =
                enabledServiceInfo.packageName.equals(context.packageName, ignoreCase = true)
            val nameOk =
                enabledServiceInfo.name.equals(AcsbService::class.java.name, ignoreCase = true)
            if (packageOk && nameOk) {
                return true
            }
        }
        return false

    }


    private fun checkAcsbB(): Boolean {
        val accessibilityEnabled = try {
            Settings.Secure.getInt(
                context.contentResolver,
                Settings.Secure.ACCESSIBILITY_ENABLED
            )
        } catch (e: Settings.SettingNotFoundException) {
            e.printStackTrace()
            0
        }
        val mStringColonSplitter = TextUtils.SimpleStringSplitter(':')
        if (accessibilityEnabled == 1) {
            val settingValue = Settings.Secure.getString(
                context.contentResolver,
                Settings.Secure.ENABLED_ACCESSIBILITY_SERVICES
            )
            if (settingValue != null) {
                mStringColonSplitter.setString(settingValue)
                while (mStringColonSplitter.hasNext()) {
                    val accessibilityService = mStringColonSplitter.next()
                    val myName = "${context.packageName}/${AcsbService::class.java.name}"
                    val nameEquals = accessibilityService.equals(
                        myName,
                        ignoreCase = true
                    )
                    if (nameEquals) {
                        return true
                    }
                }
            }
        }
        return false
    }


}