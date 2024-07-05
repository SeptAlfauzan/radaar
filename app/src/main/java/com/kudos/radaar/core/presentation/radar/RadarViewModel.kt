package com.kudos.radaar.core.presentation.radar

import android.bluetooth.BluetoothDevice
import android.content.Context
import android.util.Log
import androidx.compose.runtime.mutableStateListOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kudos.radaar.core.helper.BTHelperImpl
import com.kudos.radaar.core.helper.BTState
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.launch
import javax.inject.Inject


@HiltViewModel
class RadarViewModel @Inject constructor(
    @ApplicationContext val context: Context
): ViewModel() {
    private var _status: MutableStateFlow<BTState> = MutableStateFlow(BTState.DISCONNECT)
    val status: StateFlow<BTState> = _status
    private var _devices = mutableStateListOf<BluetoothDevice>()
    val devices = _devices
    val btHelper = BTHelperImpl()

    init {
        getConnectedStatus()
    }
    private fun getConnectedStatus() {
        viewModelScope.launch(Dispatchers.IO) {
            btHelper.connectionFlow.catch {
                Log.d("TAG", "getConnectedStatus: $it")
            }.collect {
                _status.value = it
            }
        }
    }

    fun getBTDevices(){
        viewModelScope.launch {
            try {
                _devices.addAll(btHelper.findBT(context))
                Log.d("TAG", "onCreate: ${devices.map { it.address }}")
            } catch (e: Exception) {
                Log.d("TAG", "onCreate: $e")
            }
        }
    }

    fun disconnectBT(){
        btHelper.closeBTConnection()
    }
    fun connectBT(bluetoothDevice: BluetoothDevice){
        btHelper.openBT(context, bluetoothDevice)
    }
}