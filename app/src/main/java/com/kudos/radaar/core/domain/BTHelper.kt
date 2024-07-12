package com.kudos.radaar.core.domain

import android.bluetooth.BluetoothDevice
import android.content.Context
import com.kudos.radaar.core.helper.BTState
import kotlinx.coroutines.flow.SharedFlow

interface BTHelper {
    fun findBT(context: Context): List<BluetoothDevice>
    fun listenBT()
    fun openBT(context: Context, device: BluetoothDevice)
    fun closeBTConnection()
    val connectionFlow: SharedFlow<BTState>
    val rawIncomingDatasFlow: SharedFlow<List<String>>
}