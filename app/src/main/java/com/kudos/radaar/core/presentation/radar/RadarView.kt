package com.kudos.radaar.core.presentation.radar

import android.util.Log
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.tooling.preview.Devices
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.kudos.radaar.core.domain.entities.RadarData
import com.kudos.radaar.core.helper.BTState
import com.kudos.radaar.ui.theme.RadaarTheme
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.collect

@Composable
fun RadarView(
    radarDatas: List<RadarData>,
    modifier: Modifier = Modifier
) {
    val filteredRadarDatas = radarDatas.takeLast(50)
    Scaffold {
        Box(
            modifier
                .fillMaxSize()
                .padding(it)
        ) {
            Canvas(
                modifier = Modifier
                    .size(360.dp)
                    .align(Alignment.Center)
                    .background(Color.Blue.copy(alpha = 0.1f))
            ) {
                val canvasHalfWidth = size.width / 2
                val canvasHalfHeight = size.height / 2

                filteredRadarDatas.map {
                    rotate(
                        degrees = -(it.degree).toFloat() + 90,
                        Offset(canvasHalfWidth, canvasHalfHeight)
                    ) {
                        val n = filteredRadarDatas.size
                        val i = filteredRadarDatas.indexOf(it)
                        val opacity = Math.pow(i.toDouble(), 1.0) / Math.pow((n - 1).toDouble(), 1.0)
                        drawLine(
                            color = Color.White.copy(alpha = opacity.toFloat()),
                            start = Offset(x = canvasHalfWidth, y = canvasHalfHeight),
                            end = Offset(
                                x = canvasHalfWidth,
                                y = (canvasHalfHeight - (it.distance + 300).toFloat())
                            ),
                            strokeWidth = 4f,
                            cap = StrokeCap.Round
                        )
                    }
                }

            }
        }
    }
}
//
@Preview(device = Devices.PIXEL_4)
@Composable
private fun preview() {

    val radarDatas = listOf(
        RadarData(degree = 0, distance = 100.0),
        RadarData(degree = 1, distance = 100.0),
        RadarData(degree = 2, distance = 100.0),
        RadarData(degree = 3, distance = 100.0),
        RadarData(degree = 4, distance = 10.0),
        RadarData(degree = 5, distance = 4.0),
        RadarData(degree = 6, distance = 4.0),
        RadarData(degree = 7, distance = 100.0),
        RadarData(degree = 8, distance = 400.0),
        RadarData(degree = 9, distance = 40.0),
    )
    RadaarTheme {
        Surface {
            RadarView(radarDatas = radarDatas)
        }
    }
}