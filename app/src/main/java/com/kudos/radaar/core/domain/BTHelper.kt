package com.kudos.radaar.core.domain

import android.bluetooth.BluetoothDevice
import android.content.Context

interface BTHelper {
    fun findBT(context: Context): List<BluetoothDevice>
    fun listenBT()
}