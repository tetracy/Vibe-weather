package com.vibe.weather.domain

import java.time.LocalDate
import java.time.LocalDateTime

/** 统一天气码：各家数据源的 API 码先映射到它，界面与沉浸场景只认它。 */
enum class WeatherCode(val label: String) {
    SUNNY("晴"),
    PARTLY_CLOUDY("多云"),
    CLOUDY("阴"),
    RAIN("小雨"),
    HEAVY_RAIN("大雨"),
    THUNDER("雷雨"),
    SNOW("雪"),
}

data class CurrentWeather(
    val temperature: Int,
    val code: WeatherCode,
)

data class HourForecast(
    val time: LocalDateTime,
    /** 0..100 */
    val precipProbability: Int,
    val temperature: Int,
    val code: WeatherCode,
)

data class DayForecast(
    val date: LocalDate,
    val minTemperature: Int,
    val maxTemperature: Int,
    /** 0..100 */
    val precipProbability: Int,
    val code: WeatherCode,
)

data class Forecast(
    val locationName: String,
    val current: CurrentWeather,
    /** 未来 24 小时，按时间升序 */
    val hourly: List<HourForecast>,
    /** 未来 7 天，按日期升序 */
    val daily: List<DayForecast>,
)
