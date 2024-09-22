package com.randos.doctorsapp.utils.manager

import android.Manifest
import android.app.Application
import android.content.pm.PackageManager
import androidx.core.app.ActivityCompat
import com.randos.domain.manager.PermissionManager
import javax.inject.Inject

class PermissionManagerImpl @Inject constructor(private val application: Application) :
    PermissionManager {

    override fun isLocationPermissionGranted(): Boolean {
        return isPermissionGranted(Manifest.permission.ACCESS_FINE_LOCATION) &&
                isPermissionGranted(Manifest.permission.ACCESS_COARSE_LOCATION)
    }

    override fun isReceiveSmsPermissionGranted(): Boolean {
        return isPermissionGranted(Manifest.permission.RECEIVE_SMS)
    }

    override fun isReadContactPermissionGranted(): Boolean {
        return isPermissionGranted(Manifest.permission.READ_CONTACTS)
    }

    private fun isPermissionGranted(permission: String): Boolean {
        return ActivityCompat.checkSelfPermission(
            application,
            permission
        ) == PackageManager.PERMISSION_GRANTED
    }
}