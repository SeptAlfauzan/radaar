package com.kudos.radaar.core.helper

import android.Manifest
import android.annotation.SuppressLint
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import android.bluetooth.BluetoothSocket
import android.content.Context
import android.content.pm.PackageManager
import android.util.Log
import androidx.core.app.ActivityCompat
import com.kudos.radaar.core.domain.BTHelper
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch
import java.io.IOException
import java.io.InputStream
import java.io.OutputStream
import java.util.Date
import java.util.UUID


class BTHelperImpl : BTHelper {

    lateinit var mmOutputStream: OutputStream
    lateinit var mmInputStream: InputStream

    var mmSocket: BluetoothSocket? = null
    var mmDevice: BluetoothDevice? = null

    private val _connectionFlow = MutableSharedFlow<BTState>()
    override val connectionFlow: SharedFlow<BTState> = _connectionFlow.asSharedFlow()
    private val _rawIncomingDatasFlow = MutableSharedFlow<List<String>>()
    override val rawIncomingDatasFlow: SharedFlow<List<String>>
        get() = _rawIncomingDatasFlow
    private val listRawDatas: MutableList<String> = mutableListOf()

    private var connectThread: ConnectThread? = null

    override fun findBT(context: Context): List<BluetoothDevice> {
        val mBluetoothAdapter: BluetoothAdapter = BluetoothAdapter.getDefaultAdapter()
        if (mBluetoothAdapter == null) {
            throw Exception("No bluetooth adapter available")
        }

        if (!mBluetoothAdapter.isEnabled()) {
//            val enableBluetooth = Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE)
//            startActivityForResult(enableBluetooth, 0)
            throw Exception("Please turn on your bluetooth!")
        }

        if (ActivityCompat.checkSelfPermission(
                context,
                Manifest.permission.BLUETOOTH_CONNECT
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            throw Exception("Bluetooth permission is not granted!")
        }

        val pairedDevices: Set<BluetoothDevice> = mBluetoothAdapter.bondedDevices

        val devices: MutableList<BluetoothDevice> = mutableListOf()

        if (pairedDevices.isNotEmpty()) {
            for (device in pairedDevices) {
                devices.add(device)
            }
        }
        return devices
    }

    override fun listenBT() {
        TODO("Not yet implemented")
    }

    override fun openBT(context: Context, device: BluetoothDevice) {
       connectThread = ConnectThread(device).apply {
            start()
        }
    }

    override fun closeBTConnection(){
        connectThread?.cancel()
    }

    @SuppressLint("MissingPermission")
    private inner class ConnectThread(val device: BluetoothDevice) : Thread() {
        private var outStream: OutputStream? = null
        private var mmInStream: InputStream? = null
        private val mmBuffer: ByteArray = ByteArray(1024) // Adjust buffer size as needed


        private val socket: BluetoothSocket? by lazy(LazyThreadSafetyMode.NONE) {
            device.createRfcommSocketToServiceRecord(MY_UUID)
        }

        private fun updateBTState(state: BTState){
            CoroutineScope(Dispatchers.IO).launch{
                _connectionFlow.emit(state)
            }
        }

        private fun emitIncomingRawData(data: String){
            Log.d("RadarViewModel", ": $data")
            synchronized(listRawDatas) {
                listRawDatas.add(data)
                _rawIncomingDatasFlow.tryEmit(listRawDatas) // Emit as a list, you can adjust this according to your needs
            }
        }

        override fun run() {

            try {
                updateBTState(BTState.CONNECTING)

                var numBytes: Int // Number of bytes returned from read()
                socket?.connect()
                outStream = socket?.outputStream
                mmInStream = socket?.inputStream

                Log.d("CLASSIC", "run success connect")

              updateBTState(BTState.CONNECTED)

                while (true) {
                    // Read from the InputStream.
                    if (mmInStream == null) break
                    numBytes = try {
                        mmInStream!!.read(mmBuffer)
                    } catch (e: IOException) {
                        Log.d("CLASSIC", "Input stream was disconnected", e)
                        updateBTState(BTState.DISCONNECT)
                        break
                    }
                    // Send the obtained bytes to the UI activity.
                    val readMessage = String(mmBuffer, 0, numBytes)

                    CoroutineScope(Dispatchers.IO).launch {
                        Log.d("CLASSIC", "Received: $readMessage")
                        emitIncomingRawData(readMessage)
                    }
                    // Here, you can update your UI or handle the received data as needed.
                }

            } catch (connectException: IOException) {
                Log.d("CLASSIC", "run fail to connect: ${connectException.message}")
                // Unable to connect; close the socket and return.
                try {
                    socket?.close()
                } catch (closeException: IOException) {
                    Log.e("CLASSIC", "Could not close the client socket", closeException)

                }
                updateBTState(BTState.DISCONNECT)
                return
            }
        }

        // Closes the client socket and causes the thread to finish.
        fun cancel() {
            try {
                socket?.close()
                updateBTState(BTState.DISCONNECT)
            } catch (e: IOException) {
                Log.e("CLASSIC", "Could not close the client socket", e)
//                throw e
            }
        }
    }

    companion object {
        // This is a well-known SPP UUID (Serial Port Profile).
        // You may need to replace it with your device's specific UUID.
        val MY_UUID: UUID = UUID.fromString("00001101-0000-1000-8000-00805f9b34fb")
    }

}