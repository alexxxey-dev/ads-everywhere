package com.ads.everywhere.util

import android.content.Context

class PrefsUtil(private val context: Context) {
    companion object {
        fun string(
            context: Context,
            key: String,
            value: String? = null,
            defaultValue: String? = null
        ): String? {
            val prefs = context.getSharedPreferences(context.packageName, Context.MODE_PRIVATE)
            if (value != null) {
                prefs.edit().putString(key, value).apply()
            }
            return prefs.getString(key, defaultValue)
        }

        fun bool(
            context: Context,
            key: String,
            value: Boolean? = null,
            default: Boolean = false
        ): Boolean {
            val prefs = context.getSharedPreferences(context.packageName, Context.MODE_PRIVATE)
            if (value != null) {
                prefs.edit().putBoolean(key, value).apply()
            }

            return prefs.getBoolean(key, default)
        }
    }

    private val prefs = context.getSharedPreferences(context.packageName, Context.MODE_PRIVATE)

    fun bool(key: String, value: Boolean? = null, default: Boolean = false): Boolean {
        if (value != null) {
            prefs.edit().putBoolean(key, value).apply()
        }

        return prefs.getBoolean(key, default)
    }

    fun string(key: String, value: String? = null, defaultValue: String? = null): String? {
        if (value != null) {
            prefs.edit().putString(key, value).apply()
        }
        return prefs.getString(key, defaultValue)
    }

    fun long(key: String, value: Long? = null): Long {
        if (value != null) {
            prefs.edit().putLong(key, value).apply()
        }
        return prefs.getLong(key, 0L)
    }

    fun float(key: String, value: Float? = null, default: Float = 0.0f): Float {
        if (value != null) {
            prefs.edit().putFloat(key, value).apply()
        }

        return prefs.getFloat(key, default)
    }

    fun int(key: String, value: Int? = null, default: Int = 0): Int {
        if (value != null) {
            prefs.edit().putInt(key, value).apply()
        }

        return prefs.getInt(key, default)
    }
}