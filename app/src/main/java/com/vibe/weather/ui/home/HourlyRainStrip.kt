package com.vibe.weather.ui.home

import androidx.compose.foundation.background
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.vibe.weather.domain.HourForecast
import com.vibe.weather.ui.theme.PixelFont

/**
 * 定稿视觉的降雨带：纯白系条，高度=概率、不透明度随概率爬升；
 * ≥50% 拉满并标点阵数字（docs/research/design-references.md §5.1）。
 * 柱底 1dp 基线贯穿整条（列无缝相接自然连成），当前小时列提亮做「现在」锚点。
 */
@Composable
fun MonoRainStrip(hours: List<HourForecast>, modifier: Modifier = Modifier) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .horizontalScroll(rememberScrollState()),
    ) {
        hours.forEachIndexed { index, hour ->
            MonoHourColumn(hour, isNow = index == 0)
        }
    }
}

@Composable
private fun MonoHourColumn(hour: HourForecast, isNow: Boolean) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.width(42.dp),
    ) {
        if (hour.precipProbability >= 50) {
            Text(
                text = "${hour.precipProbability}%",
                fontSize = 10.sp,
                fontFamily = PixelFont,
                color = Color.White,
            )
        } else {
            Spacer(Modifier.height(14.dp))
        }
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(96.dp),
            contentAlignment = Alignment.BottomCenter,
        ) {
            // 基线：列与列无缝相接，连成一条贯穿的参照线
            Box(
                Modifier
                    .fillMaxWidth()
                    .height(1.dp)
                    .background(Color.White.copy(alpha = if (isNow) 0.45f else 0.28f))
            )
            if (hour.precipProbability > 0) {
                val alpha =
                    if (hour.precipProbability >= 50) 0.95f
                    else 0.25f + hour.precipProbability / 100f * 0.4f
                Box(
                    Modifier
                        .fillMaxWidth(0.45f)
                        .fillMaxHeight(hour.precipProbability / 100f)
                        .background(Color.White.copy(alpha = alpha))
                )
            }
        }
        Text(
            text = "${hour.time.hour}",
            fontSize = 10.sp,
            fontFamily = PixelFont,
            color = if (isNow) Color.White else Color.White.copy(alpha = 0.55f),
        )
    }
}
