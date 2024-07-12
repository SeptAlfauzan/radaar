package com.kudos.radaar.core.domain.repositories

import android.bluetooth.BluetoothDevice
import com.kudos.radaar.core.domain.entities.RadarData
import com.kudos.radaar.core.helper.BTState
import kotlinx.coroutines.flow.Flow

interface RadarRepository {
    suspend fun getRadarValues(): Flow<List<RadarData>>
    fun getDevices(): Flow<List<BluetoothDevice>>
    fun disconnect()
    fun connect(device: BluetoothDevice)
    fun listenBTState(): Flow<BTState>
}