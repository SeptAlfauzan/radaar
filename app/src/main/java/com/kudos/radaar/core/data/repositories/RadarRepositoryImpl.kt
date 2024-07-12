package com.kudos.radaar.core.data.repositories

import android.bluetooth.BluetoothDevice
import android.content.Context
import android.util.Log
import com.google.gson.Gson
import com.google.gson.JsonObject
import com.kudos.radaar.core.data.raw.RadarRawResponse
import com.kudos.radaar.core.domain.BTHelper
import com.kudos.radaar.core.domain.entities.RadarData
import com.kudos.radaar.core.domain.repositories.RadarRepository
import com.kudos.radaar.core.helper.BTState
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOf
import javax.inject.Inject


class RadarRepositoryImpl @Inject constructor(
    @ApplicationContext val context: Context,
private val btHelper: BTHelper
) : RadarRepository  {



    override suspend fun getRadarValues(): Flow<List<RadarData>> {
        return flow {
            btHelper.rawIncomingDatasFlow.collect { rawJsonList ->
                Log.d("RadarViewModel", " raw : $rawJsonList")
                val result = rawJsonList.map { json ->
                    val convertedObject: RadarRawResponse = Gson().fromJson(json, RadarRawResponse::class.java)
                    RadarData(distance = convertedObject.distance ?: 0.0, degree = convertedObject.degree ?: 0)
                }
                Log.d("RadarViewModel", ": $result")
                emit(result)
            }
        }
    }
    override fun getDevices(): Flow<List<BluetoothDevice>> {
        val result = btHelper.findBT(context)
        return flowOf(result)
    }
    override fun disconnect() {
        btHelper.closeBTConnection()
    }
    override fun connect(device: BluetoothDevice) {
        btHelper.openBT(context, device)
    }
    override fun listenBTState(): Flow<BTState> = btHelper.connectionFlow
}