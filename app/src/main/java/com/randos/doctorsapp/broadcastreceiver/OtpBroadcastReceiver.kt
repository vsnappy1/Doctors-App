package com.randos.doctorsapp.broadcastreceiver

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.provider.Telephony
import android.util.Log


private const val TAG = "OtpBroadcastReceiver"

class OtpBroadcastReceiver() : BroadcastReceiver() {
    private var onOtpReceived: (String?) -> Unit = {}
    companion object {
        const val SMS_RECEIVED_INTENT_FILTER = "android.provider.Telephony.SMS_RECEIVED"
    }

    override fun onReceive(p0: Context?, p1: Intent) {
        Log.d(TAG, "onReceive: ${p1.action}")

        if (p1.action === SMS_RECEIVED_INTENT_FILTER) {
            val smsMessages = Telephony.Sms.Intents.getMessagesFromIntent(p1)
            smsMessages.forEach { message ->
                Log.i(TAG, "Message received: " + message?.messageBody)
                onOtpReceived(message?.messageBody)
            }
        }
    }

    fun setOnOtpReceivedListener(onOtpReceived: (String?) -> Unit){
        this.onOtpReceived = onOtpReceived
    }
}