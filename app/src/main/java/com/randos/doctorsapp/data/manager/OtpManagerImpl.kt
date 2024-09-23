package com.randos.doctorsapp.data.manager

import android.app.Application
import android.content.IntentFilter
import android.util.Log
import com.randos.doctorsapp.broadcastreceiver.OtpBroadcastReceiver
import com.randos.doctorsapp.broadcastreceiver.OtpBroadcastReceiver.Companion.SMS_RECEIVED_INTENT_FILTER
import com.randos.domain.manager.OtpManager
import com.randos.domain.manager.PermissionManager
import kotlinx.coroutines.suspendCancellableCoroutine
import javax.inject.Inject
import kotlin.coroutines.resume


class OtpManagerImpl @Inject constructor(
    private val application: Application,
    private val permissionManager: PermissionManager,
    private val broadcastReceiver: OtpBroadcastReceiver
) : OtpManager {

    companion object {
        private const val TAG = "OtpManagerImpl"
    }

    override suspend fun getOtp(): String? = suspendCancellableCoroutine { continuation ->
        if (permissionManager.isReceiveSmsPermissionGranted()) {

            broadcastReceiver.apply {
                val onOtpReceived: (String?) -> Unit = {
                    continuation.resume(it)
                    unregisterReceiver(broadcastReceiver)
                }
                setOnOtpReceivedListener(onOtpReceived)
            }

            val smsReceivedIntentFilter = IntentFilter(SMS_RECEIVED_INTENT_FILTER)
            application.registerReceiver(broadcastReceiver, smsReceivedIntentFilter)
            Log.i(TAG, "OtpBroadcastReceiver registered")

            continuation.invokeOnCancellation { unregisterReceiver(broadcastReceiver) }
        } else {
            continuation.resume(null)
        }
    }

    private fun unregisterReceiver(broadcastReceiver: OtpBroadcastReceiver?) {
        application.unregisterReceiver(broadcastReceiver)
        Log.i(TAG, "OtpBroadcastReceiver unregistered")
    }
}