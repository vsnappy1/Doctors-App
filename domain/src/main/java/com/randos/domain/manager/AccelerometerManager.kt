package com.randos.domain.manager

import com.randos.domain.model.Accelerometer
import kotlinx.coroutines.flow.Flow

interface AccelerometerManager {
    fun accelerometerDataStream(): Flow<Accelerometer>
    fun stopAccelerometerDataStream()
}