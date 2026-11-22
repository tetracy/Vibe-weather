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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.vibe.weather.domain.HourForecast
import com.vibe.weather.ui.theme.PixelFont
import com.vibe.weather.ui.theme.ProbTier
import com.vibe.weather.ui.theme.probTier
import com.vibe.weather.ui.theme.probTierColor

/**
 * 方向一「夜窗」的降雨带：色档概率条（高度=概率、颜色=档位），≥40% 才标数字。
 * docs/research/design-references.md §5.1 模式 3 + Apple 按需标注思想。
 */
@Composable
fun TieredRainStrip(hours: List<HourForecast>, modifier: Modifier = Modifier) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .horizontalScroll(rememberScrollState()),
    ) {
        hours.forEach { hour ->
            TieredHourColumn(hour)
        }
    }
}

@Composable
private fun TieredHourColumn(hour: HourForecast) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.width(46.dp),
    ) {
        if (probTier(hour.precipProbability) == ProbTier.LOW) {
            Spacer(Modifier.height(16.dp))
        } else {
            Text(
                text = "${hour.precipProbability}%",
                fontSize = 11.sp,
                fontWeight = FontWeight.Medium,
                color = Color.White.copy(
                    alpha = if (hour.precipProbability >= 70) 0.95f else 0.8f,
                ),
            )
        }
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
                            color = probTierColor(hour.precipProbability),
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

/**
 * 方向二「单色」的降雨带：纯白系条，不透明度随概率爬升，≥50% 拉满并标点阵数字。
 */
@Composable
fun MonoRainStrip(hours: List<HourForecast>, modifier: Modifier = Modifier) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .horizontalScroll(rememberScrollState()),
    ) {
        hours.forEach { hour ->
            MonoHourColumn(hour)
        }
    }
}

@Composable
private fun MonoHourColumn(hour: HourForecast) {
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
            color = Color.White.copy(alpha = 0.55f),
        )
    }
}
