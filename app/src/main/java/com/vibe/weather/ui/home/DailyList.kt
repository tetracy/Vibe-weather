package com.vibe.weather.ui.home

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.vibe.weather.domain.DayForecast
import com.vibe.weather.domain.WeatherCode
import java.time.format.TextStyle
import java.util.Locale

/** 未来 7 天：星期 · 图标 · 温度区间 · 降雨概率（docs/PLAN.md §3）。 */
@Composable
fun DailyList(days: List<DayForecast>, modifier: Modifier = Modifier) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp),
    ) {
        days.forEachIndexed { index, day ->
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 10.dp),
            ) {
                Text(
                    text = dayLabel(day, index),
                    fontSize = 15.sp,
                    color = Color.White.copy(alpha = 0.9f),
                    modifier = Modifier.weight(1f),
                )
                Text(
                    text = emojiFor(day.code),
                    fontSize = 18.sp,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.weight(0.6f),
                )
                Text(
                    text = "${day.minTemperature}° / ${day.maxTemperature}°",
                    fontSize = 15.sp,
                    color = Color.White.copy(alpha = 0.85f),
                    modifier = Modifier.weight(1.1f),
                )
                Text(
                    text = if (day.precipProbability >= 10) "${day.precipProbability}%" else "—",
                    fontSize = 14.sp,
                    fontWeight = if (day.precipProbability >= 50) FontWeight.Bold else FontWeight.Normal,
                    color = Color.White.copy(
                        alpha = if (day.precipProbability >= 10) 0.85f else 0.35f,
                    ),
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
