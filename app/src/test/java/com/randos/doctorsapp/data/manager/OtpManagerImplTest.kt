package com.randos.doctorsapp.data.manager

import android.app.Application
import android.content.Intent
import android.util.Log
import com.randos.doctorsapp.broadcastreceiver.OtpBroadcastReceiver
import com.randos.domain.manager.OtpManager
import com.randos.domain.manager.PermissionManager
import io.mockk.MockKAnnotations
import io.mockk.Runs
import io.mockk.every
import io.mockk.impl.annotations.MockK
import io.mockk.just
import io.mockk.mockkStatic
import io.mockk.slot
import io.mockk.unmockkAll
import io.mockk.verify
import kotlinx.coroutines.async
import kotlinx.coroutines.delay
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.withTimeoutOrNull
import org.junit.Assert.*

import org.junit.After
import org.junit.Before
import org.junit.Test
import kotlin.time.Duration.Companion.milliseconds

class OtpManagerImplTest {

    @MockK(relaxed = true)
    private lateinit var application: Application

    @MockK
    private lateinit var permissionManager: PermissionManager

    /*
      In MockK, relaxed = true allows mocks to return default values for un configured method
      calls, preventing test failures for unexpected invocations.
     */
    @MockK(relaxed = true)
    private lateinit var broadcastReceiver: OtpBroadcastReceiver

    private lateinit var otpManager: OtpManager

    @Before
    fun setUp() {
        MockKAnnotations.init(this)
        otpManager = OtpManagerImpl(application, permissionManager, broadcastReceiver)
        mockkStatic(Log::class)
        every { Log.i(any(), any()) } returns 1
    }

    @After
    fun tearDown() {
        unmockkAll()
    }

    @Test
    fun getOtp_whenSmsReceivePermissionNotGranted_shouldReturnNull() = runTest {
        // Given
        every { permissionManager.isReceiveSmsPermissionGranted() } returns false

        // When
        val otp = otpManager.getOtp()

        // Then
        assertNull(otp)
    }

    @Test
    fun getOtp_whenSmsReceivePermissionGranted_shouldRegisterBroadcastReceiver() = runTest {
        // Given
        every { permissionManager.isReceiveSmsPermissionGranted() } returns true
        every { application.registerReceiver(broadcastReceiver, any()) } returns Intent()

        // When
        /*
         Since this is a suspended function and will only be resumed either otp is received or canceled,
         that why using withTimeout.
         */
        withTimeoutOrNull(1.milliseconds) {
            otpManager.getOtp()
        }

        // Then
        verify { application.registerReceiver(broadcastReceiver, any()) }
    }

    @Test
    fun getOtp_whenSmsReceivePermissionGranted_shouldReceiveOtp() = runTest {
        // Given
        val otp = "123654"
        val otpListenerSlot = slot<(String?) -> Unit>()
        every { permissionManager.isReceiveSmsPermissionGranted() } returns true
        every { broadcastReceiver.setOnOtpReceivedListener(capture(otpListenerSlot)) } just Runs

        // When
        val deferredOtp = async { otpManager.getOtp() }
        delay(1.milliseconds)  // Simulating some delay before the listener is invoked
        otpListenerSlot.captured.invoke(otp)  // Manually invoke the listener with a mock OTP
        val result = deferredOtp.await()

        // Then
        assertEquals(otp, result)
    }

    @Test
    fun getOtp_whenSmsReceived_shouldUnregisterOtpBroadcastReceiver() = runTest {
        // Given
        val otp = "123654"
        val otpListenerSlot = slot<(String?) -> Unit>()
        every { permissionManager.isReceiveSmsPermissionGranted() } returns true
        every { broadcastReceiver.setOnOtpReceivedListener(capture(otpListenerSlot)) } just Runs

        // When
        val deferredOtp = async { otpManager.getOtp() }
        delay(1.milliseconds)  // Simulating some delay before the listener is invoked
        otpListenerSlot.captured.invoke(otp)  // Manually invoke the listener with a mock OTP
        deferredOtp.await()

        // Then
        verify { application.unregisterReceiver(broadcastReceiver) }
    }

    @Test
    fun getOtp_whenSuspendedFunctionCanceled_shouldUnregisterOtpBroadcastReceiver() = runTest {
        // Given
        every { permissionManager.isReceiveSmsPermissionGranted() } returns true

        // When
        withTimeoutOrNull(1.milliseconds) {
            otpManager.getOtp()
        }

        // Then
        verify { application.unregisterReceiver(broadcastReceiver) }
    }
}