package com.ads.everywhere.data.models

data class Permission(
    val titleRes:Int,
    val type: PermissionType
)

enum class PermissionType{
    AUTO_START, ACSB, USAGE_STATS, BACKGROUND, BATTERY
}