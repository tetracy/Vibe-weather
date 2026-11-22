package com.vibe.weather.domain

/**
 * 唯一的缝（docs/PLAN.md §5）：HTTP、JSON 解析、天气码映射全部关在实现内部。
 * 计划中的实现：FakeWeatherProvider（演示/测试替身）、OpenMeteoProvider、QWeatherProvider。
 */
interface WeatherProvider {
    val id: String
    val label: String
    suspend fun forecast(lat: Double, lon: Double): Forecast
}
