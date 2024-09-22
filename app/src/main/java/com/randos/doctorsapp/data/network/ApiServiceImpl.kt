package com.randos.doctorsapp.data.network

import com.randos.doctorsapp.presentation.utils.stringFormat
import com.randos.domain.model.Address
import com.randos.domain.model.Appointment
import com.randos.domain.model.Doctor
import com.randos.domain.model.Time
import com.randos.domain.model.User
import kotlinx.coroutines.delay
import java.time.LocalDateTime
import javax.inject.Inject

class ApiServiceImpl @Inject constructor() : ApiService {
    companion object {
        private const val DELAY = 1000L
    }

    override suspend fun getAuthToken(email: String, password: String): String {
        delay(DELAY)
        return "top_secret_auth_token"
    }

    override suspend fun getDoctorList(lat: Double, lng: Double): List<Doctor> {
        delay(DELAY)
        val doctors = mutableListOf<Doctor>()
        repeat(10) {
            doctors.add(
                Doctor(
                    it.toLong(),
                    "Doctor $it",
                    Address("852", "Sunnyvale", "CA", "95560", "USA")
                )
            )
        }
        return doctors
    }

    override suspend fun getDoctorList(address: Address): List<Doctor> {
        delay(DELAY)
        val doctors = mutableListOf<Doctor>()
        repeat(10) {
            doctors.add(
                Doctor(
                    it.toLong(),
                    "Doctor $it",
                    Address("852", "Sunnyvale", "CA", "95560", "USA")
                )
            )
        }
        return doctors
    }

    override suspend fun requestOtp(): Boolean {
        delay(DELAY)
        return true
    }

    override suspend fun verifyOtp(): Boolean {
        delay(DELAY)
        return true
    }

    override suspend fun getAvailableAppointments(doctorId: Long): List<Appointment> {
        delay(DELAY)
        val appointments = mutableListOf<Appointment>()
        var localTime = LocalDateTime.now()
        repeat(5) {
            localTime = localTime.plusMinutes(30)
            appointments.add(
                Appointment(
                    id = it.toLong(),
                    date = "Tomorrow",
                    time = Time(
                        from = localTime.stringFormat(),
                        to = localTime.plusMinutes(30).stringFormat()
                    )
                )
            )
        }
        return appointments
    }

    override suspend fun bookAppointment(appointmentId: Long): Boolean {
        delay(DELAY)
        return true
    }

    override suspend fun getUser(authToken: String): User {
        delay(DELAY)
        return User(
            id = 1,
            name = "Kumar",
            address = Address("852", "Sunnyvale", "CA", "95560", "USA")
        )
    }
}