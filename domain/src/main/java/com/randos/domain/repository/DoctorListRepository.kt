package com.randos.domain.repository

import com.randos.domain.type.NetworkResult
import com.randos.domain.model.Doctor
import com.randos.domain.type.Location

interface DoctorListRepository {
    suspend fun getDoctors(): NetworkResult<Pair<List<Doctor>, Location>>
}