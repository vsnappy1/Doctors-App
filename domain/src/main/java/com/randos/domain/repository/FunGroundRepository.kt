package com.randos.domain.repository

import com.randos.domain.model.Accelerometer
import kotlinx.coroutines.flow.Flow

interface FunGroundRepository {
    fun getAccelerometerDataStream(): Flow<Accelerometer>
    fun stopAccelerometerDataStream()
}