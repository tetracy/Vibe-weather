package com.vibe.weather.ui.home

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.vibe.weather.domain.DayForecast
import com.vibe.weather.ui.theme.PixelFont
import com.vibe.weather.ui.theme.TextPrimary
import com.vibe.weather.ui.theme.TextSecondary
import com.vibe.weather.ui.theme.TextTertiary
import java.time.format.TextStyle
import java.util.Locale

/** 定稿视觉的 7 天列表：灰阶行，无图形条，层次靠字重与透明度。 */
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
