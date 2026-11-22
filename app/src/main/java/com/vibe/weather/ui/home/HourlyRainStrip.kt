package com.vibe.weather.ui.home

import androidx.compose.foundation.background
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.lerp
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.vibe.weather.domain.HourForecast

private val BarEmpty = Color(0xFF81D4FA)
private val BarFull = Color(0xFF0277BD)

/** 界面主角：未来 24 小时逐时降雨概率带（docs/PLAN.md §3）。 */
@Composable
fun HourlyRainStrip(hours: List<HourForecast>, modifier: Modifier = Modifier) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .horizontalScroll(rememberScrollState())
            .padding(horizontal = 20.dp),
    ) {
        hours.forEach { hour ->
            HourColumn(hour)
        }
    }
}

@Composable
private fun HourColumn(hour: HourForecast) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.width(46.dp),
    ) {
        Text(
            text = "${hour.precipProbability}%",
            fontSize = 11.sp,
            fontWeight = if (hour.precipProbability >= 50) FontWeight.Bold else FontWeight.Normal,
            color = Color.White.copy(
                alpha = if (hour.precipProbability >= 50) 0.95f else 0.6f,
            ),
        )
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(96.dp),
            contentAlignment = Alignment.BottomCenter,
        ) {
            if (hour.precipProbability > 0) {
                Box(
                    Modifier
                        .fillMaxWidth(0.6f)
                        .fillMaxHeight(hour.precipProbability / 100f)
                        .background(
                            color = lerp(BarEmpty, BarFull, hour.precipProbability / 100f),
                            shape = RoundedCornerShape(topStart = 5.dp, topEnd = 5.dp),
                        )
                )
            }
        }
        Text(
            text = "${hour.time.hour}时",
            fontSize = 11.sp,
            color = Color.White.copy(alpha = 0.65f),
        )
    }
}
