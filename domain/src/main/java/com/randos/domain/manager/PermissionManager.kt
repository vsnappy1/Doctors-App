package com.randos.domain.manager

interface PermissionManager {
    fun isLocationPermissionGranted(): Boolean
    fun isReceiveSmsPermissionGranted(): Boolean
    fun isReadContactPermissionGranted(): Boolean
}