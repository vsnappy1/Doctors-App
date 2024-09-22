package com.randos.doctorsapp.data.network

import com.randos.domain.model.Address
import com.randos.domain.model.Appointment
import com.randos.domain.model.Doctor
import com.randos.domain.model.User
import kotlin.jvm.Throws

interface ApiService {
    @Throws(IllegalArgumentException::class)
    suspend fun getAuthToken(email: String, password: String): String

    suspend fun getDoctorList(lat: Double, lng: Double): List<Doctor>

    suspend fun getDoctorList(address: Address): List<Doctor>

    suspend fun requestOtp() : Boolean

    suspend fun verifyOtp() : Boolean

    suspend fun getAvailableAppointments(doctorId: Long): List<Appointment>

    suspend fun bookAppointment(appointmentId: Long): Boolean

    suspend fun getUser(authToken: String): User
}