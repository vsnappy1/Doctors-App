package com.randos.doctorsapp.broadcastreceiver


import android.content.Context
import android.content.Intent
import android.provider.Telephony
import android.telephony.SmsMessage
import android.util.Log
import io.mockk.every
import io.mockk.mockk
import io.mockk.mockkStatic
import io.mockk.verify
import org.junit.Before
import org.junit.Test


class OtpBroadcastReceiverTest {

    private lateinit var context: Context
    private lateinit var intent: Intent
    private lateinit var otpBroadcastReceiver: OtpBroadcastReceiver
    private val onOtpReceived: (String?) -> Unit = mockk(relaxed = true)

    @Before
    fun setup() {
        context = mockk()
        intent = mockk()
        otpBroadcastReceiver = OtpBroadcastReceiver().apply {
            setOnOtpReceivedListener(onOtpReceived)
        }

        mockkStatic(Log::class)
        mockkStatic(Telephony.Sms.Intents::class)

        every { Log.d(any(), any()) } returns 1
        every { Log.i(any(), any()) } returns 1
    }

    @Test
    fun `onReceive should trigger onOtpReceived when SMS is received`() {
        // Given
        val message = "123456"
        val smsMessage = mockk<SmsMessage>()
        val smsMessages = arrayOf(smsMessage)

        every { intent.action } returns OtpBroadcastReceiver.SMS_RECEIVED_INTENT_FILTER
        every { smsMessage.messageBody } returns message
        every { Telephony.Sms.Intents.getMessagesFromIntent(any()) } returns smsMessages

        // When
        otpBroadcastReceiver.onReceive(context, intent)

        // Then
        verify(exactly = 1) { onOtpReceived.invoke("123456") }
    }

    @Test
    fun `onReceive should not trigger onOtpReceived for non-SMS intent`() {
        // Given
        every { intent.action } returns "OTHER_ACTION"

        // When
        otpBroadcastReceiver.onReceive(context, intent)

        // Then
        verify(exactly = 0) { onOtpReceived.invoke(any()) }
    }
}
