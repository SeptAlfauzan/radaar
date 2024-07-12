package com.kudos.radaar.core.presentation.radar

import android.bluetooth.BluetoothDevice
import android.os.CountDownTimer
import android.util.Log
import androidx.compose.runtime.mutableStateListOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kudos.radaar.core.domain.entities.RadarData
import com.kudos.radaar.core.domain.repositories.RadarRepository
import com.kudos.radaar.core.helper.BTHelperImpl
import com.kudos.radaar.core.helper.BTState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import javax.inject.Inject


@HiltViewModel
class RadarViewModel @Inject constructor(
    private val radarRepository: RadarRepository
) : ViewModel() {
    private var _status: MutableStateFlow<BTState> = MutableStateFlow(BTState.DISCONNECT)
    val status: StateFlow<BTState> = _status
    private var _devices = mutableStateListOf<BluetoothDevice>()
    val devices = _devices

    private val _radarDatas: MutableStateFlow<List<RadarData>> =
        MutableStateFlow(listOf())
    val radarDatas: StateFlow<List<RadarData>> = _radarDatas
    val radarStateFlow = MutableSharedFlow<List<RadarData>>()
    private var radarDataMutableList = mutableStateListOf<RadarData>()
    val radarDataStateList = radarDataMutableList


    init {
        getConnectedStatus()
        viewModelScope.launch {
            generateDummyRadarStateFlow()

        }
    }

    private suspend fun generateDummyRadarStateFlow() {
        var degree = 0
        var listRadarData: MutableList<RadarData> = mutableListOf()
        val countDownTimer = object : CountDownTimer(100000, 10) {
            override fun onTick(millisUntilFinished: Long) {
                viewModelScope.launch {
                    val timeLeft = millisUntilFinished / 1000
                    Log.d("TIMER", "onTick: $timeLeft")
                    listRadarData.add(RadarData(distance = timeLeft.toDouble(), degree = degree))
                    Log.d("TIMER", "onTick: $listRadarData")
                    radarStateFlow.emit(listRadarData)

                    radarDataMutableList.addAll(listRadarData)
                    _radarDatas.value = listRadarData
                }
                degree += 1
            }

            override fun onFinish() {}
        }.start()
    }

    private fun getConnectedStatus() {
        viewModelScope.launch(Dispatchers.IO) {
            radarRepository.listenBTState().catch {
                Log.d("TAG", "getConnectedStatus: $it")
            }.collect {
                _status.value = it
            }
        }
    }

    fun getBTDevices() {
        viewModelScope.launch {
            try {
                radarRepository.getDevices().collect {
                    _devices.addAll(it)
                }
                Log.d("TAG", "onCreate: ${devices.map { it.address }}")
            } catch (e: Exception) {
                Log.d("TAG", "onCreate: $e")
            }
        }
    }

    fun disconnectBT() {
        radarRepository.disconnect()
    }

    fun connectBT(bluetoothDevice: BluetoothDevice) {
        radarRepository.connect(bluetoothDevice)
    }
}