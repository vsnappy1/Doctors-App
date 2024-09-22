package com.randos.doctorsapp.data.repository.developer

import com.randos.domain.manager.AccelerometerManager
import com.randos.domain.model.Accelerometer
import com.randos.domain.repository.FunGroundRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class FunGroundRepositoryImpl @Inject constructor(
    private val accelerometerManager: AccelerometerManager
) : FunGroundRepository {

    override fun getAccelerometerDataStream(): Flow<Accelerometer> {
        return accelerometerManager.accelerometerDataStream()
    }

    override fun stopAccelerometerDataStream() {
        accelerometerManager.stopAccelerometerDataStream()
    }
}