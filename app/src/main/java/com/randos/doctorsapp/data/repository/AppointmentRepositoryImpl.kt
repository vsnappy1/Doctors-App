package com.randos.doctorsapp.data.repository

import com.randos.doctorsapp.data.network.ApiService
import com.randos.domain.model.Appointment
import com.randos.domain.repository.AppointmentRepository
import com.randos.domain.type.NetworkResult
import javax.inject.Inject

class AppointmentRepositoryImpl @Inject constructor(
    private val apiService: ApiService
) : AppointmentRepository {

    override suspend fun getAvailableAppointments(doctorId: Long): NetworkResult<List<Appointment>> {
        return try {
            NetworkResult.Success(apiService.getAvailableAppointments(doctorId))
        } catch (e: Exception) {
            NetworkResult.Failure("Failed to fetch appointments", e)
        }
    }

    override suspend fun bookAppointment(appointmentId: Long): NetworkResult<Unit> {
        return try {
            apiService.bookAppointment(appointmentId)
            NetworkResult.Success(Unit)
        } catch (e: Exception) {
            NetworkResult.Failure("Failed to book appointments", e)
        }
    }
}