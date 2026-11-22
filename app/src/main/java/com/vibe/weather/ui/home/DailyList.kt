package com.vibe.weather.ui.home

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.vibe.weather.domain.DayForecast
import com.vibe.weather.domain.WeatherCode
import com.vibe.weather.ui.theme.PixelFont
import com.vibe.weather.ui.theme.TextPrimary
import com.vibe.weather.ui.theme.TextSecondary
import com.vibe.weather.ui.theme.TextTertiary
import com.vibe.weather.ui.theme.probTierColor
import com.vibe.weather.ui.theme.temperatureTierColor
import java.time.format.TextStyle
import java.util.Locale

/**
 * 方向一「夜窗」的 7 天列表：星期 · 图标+概率 · 低温 ─六档彩色范围条─ 高温。
 * 范围条端点对全周 min/max 归一化（Apple 模式，docs/research §3.3 / §5.2）。
 */
@Composable
fun RangeDailyList(days: List<DayForecast>, modifier: Modifier = Modifier) {
    val weekMin = days.minOf { it.minTemperature }
    val weekSpan = (days.maxOf { it.maxTemperature } - weekMin).coerceAtLeast(1)

    Column(modifier = modifier.fillMaxWidth()) {
        days.forEachIndexed { index, day ->
            RangeDailyRow(day, index, weekMin, weekSpan)
        }
    }
}

@Composable
private fun RangeDailyRow(day: DayForecast, index: Int, weekMin: Int, weekSpan: Int) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 10.dp),
    ) {
        Text(
            text = dayLabel(day, index),
            fontSize = 15.sp,
            color = TextPrimary,
            modifier = Modifier.weight(1f),
        )
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.weight(0.7f),
        ) {
            Text(text = emojiFor(day.code), fontSize = 16.sp)
            if (day.precipProbability >= 10) {
                Text(
                    text = "${day.precipProbability}%",
                    fontSize = 10.sp,
                    color = probTierColor(day.precipProbability),
                )
            }
        }
        Text(
            text = "${day.minTemperature}°",
            fontSize = 14.sp,
            color = TextSecondary,
            textAlign = TextAlign.End,
            modifier = Modifier.weight(0.5f),
        )
        TemperatureRangeBar(
            day = day,
            weekMin = weekMin,
            weekSpan = weekSpan,
            modifier = Modifier.weight(1.5f),
        )
        Text(
            text = "${day.maxTemperature}°",
            fontSize = 14.sp,
            fontWeight = FontWeight.Medium,
            color = TextPrimary,
            modifier = Modifier.weight(0.5f),
        )
    }
}

@Composable
private fun TemperatureRangeBar(
    day: DayForecast,
    weekMin: Int,
    weekSpan: Int,
    modifier: Modifier = Modifier,
) {
    val shape = RoundedCornerShape(2.5.dp)
    BoxWithConstraints(
        modifier = modifier
            .padding(horizontal = 8.dp)
            .height(5.dp)
            .background(Color.White.copy(alpha = 0.15f), shape),
    ) {
        val start = (day.minTemperature - weekMin).toFloat() / weekSpan
        val fraction =
            ((day.maxTemperature - weekMin).toFloat() / weekSpan - start).coerceAtLeast(0.06f)
        Box(
            Modifier
                .offset(x = maxWidth * start)
                .width((maxWidth * fraction).coerceAtLeast(3.dp))
                .fillMaxHeight()
                .background(
                    Brush.horizontalGradient(
                        listOf(
                            temperatureTierColor(day.minTemperature),
                            temperatureTierColor(day.maxTemperature),
                        )
                    ),
                    shape,
                )
        )
    }
}

/** 方向二「单色」的 7 天列表：灰阶行，无图形条，层次靠字重与透明度。 */
@Composable
fun MonoDailyList(days: List<DayForecast>, modifier: Modifier = Modifier) {
    Column(modifier = modifier.fillMaxWidth()) {
        days.forEachIndexed { index, day ->
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 12.dp),
            ) {
                Text(
                    text = dayLabel(day, index),
                    fontSize = 15.sp,
                    color = TextPrimary,
                    modifier = Modifier.weight(1f),
                )
                Text(
                    text = day.code.label,
                    fontSize = 13.sp,
                    color = TextTertiary,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.weight(1.1f),
                )
                Text(
                    text = "${day.minTemperature}°",
                    fontSize = 14.sp,
                    color = TextTertiary,
                    textAlign = TextAlign.End,
                    modifier = Modifier.weight(0.6f),
                )
                Text(
                    text = "${day.maxTemperature}°",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Medium,
                    color = TextPrimary,
                    textAlign = TextAlign.End,
                    modifier = Modifier.weight(0.6f),
                )
                Text(
                    text = if (day.precipProbability >= 50) "${day.precipProbability}%" else "",
                    fontSize = 10.sp,
                    fontFamily = PixelFont,
                    color = TextSecondary,
                    textAlign = TextAlign.End,
                    modifier = Modifier.weight(0.7f),
                )
            }
        }
    }
}

private fun dayLabel(day: DayForecast, index: Int): String =
    if (index == 0) "今天"
    else day.date.dayOfWeek.getDisplayName(TextStyle.SHORT, Locale.CHINESE)

private fun emojiFor(code: WeatherCode): String = when (code) {
    WeatherCode.SUNNY -> "☀️"
    WeatherCode.PARTLY_CLOUDY -> "⛅"
    WeatherCode.CLOUDY -> "☁️"
    WeatherCode.RAIN -> "🌧️"
    WeatherCode.HEAVY_RAIN -> "🌧️"
    WeatherCode.THUNDER -> "⛈️"
    WeatherCode.SNOW -> "❄️"
}
