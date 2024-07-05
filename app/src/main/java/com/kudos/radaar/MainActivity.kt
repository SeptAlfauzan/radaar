package com.kudos.radaar

import android.annotation.SuppressLint
import android.bluetooth.BluetoothDevice
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewModelScope
import com.kudos.radaar.core.helper.BTHelperImpl
import com.kudos.radaar.core.helper.BTState
import com.kudos.radaar.core.presentation.radar.RadarViewModel
import com.kudos.radaar.ui.theme.RadaarTheme
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.launch

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    private val radarViewModel: RadarViewModel by viewModels()
    @SuppressLint("MissingPermission")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            RadaarTheme {
                val status = radarViewModel.status.collectAsState().value
                val devices = radarViewModel.devices.toList()

                Scaffold(modifier = Modifier.fillMaxSize(), floatingActionButton = {
                    FloatingActionButton(onClick = { radarViewModel.getBTDevices() }) {
                        Text(text = "Scan")
                    }
                }) { innerPadding ->
                    Column(Modifier.padding(innerPadding)) {
                        Text(text = "Status: $status", modifier = Modifier.clickable {
                            if(status == BTState.CONNECTED) radarViewModel.disconnectBT()
                        })
                        LazyColumn {
                            if (devices == null || devices?.isEmpty() == true) {
                                item {
                                    Text(text = "Tidak ada perangkat bluetooth")
                                }
                            } else {
                                items(devices!!) {
                                    Text(text = "${it.address} - ${it.name}", modifier = Modifier
                                        .clickable {
                                            radarViewModel.connectBT(it)
                                        }
                                        .fillMaxWidth()
                                        .padding(8.dp))
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    override fun onDestroy() {
        radarViewModel.disconnectBT()
        super.onDestroy()
    }
}

@Composable
fun Greeting(name: String, modifier: Modifier = Modifier) {
    Text(
        text = "Hello $name!",
        modifier = modifier
    )
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    RadaarTheme {
        Greeting("Android")
    }
}