package com.randos.domain.repository

import com.randos.domain.model.Appointment
import com.randos.domain.type.NetworkResult

interface AppointmentRepository {
    suspend fun getAvailableAppointments(doctorId: Long): NetworkResult<List<Appointment>>
    suspend fun bookAppointment(appointmentId: Long): NetworkResult<Unit>
}