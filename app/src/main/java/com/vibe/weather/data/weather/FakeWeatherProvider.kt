package com.vibe.weather.data.weather

import com.vibe.weather.domain.CurrentWeather
import com.vibe.weather.domain.DayForecast
import com.vibe.weather.domain.Forecast
import com.vibe.weather.domain.HourForecast
import com.vibe.weather.domain.WeatherCode
import com.vibe.weather.domain.WeatherProvider
import java.time.LocalDate
import java.time.LocalDateTime

/** M0 演示数据：写死的假预报；之后保留当测试替身（docs/PLAN.md §10）。 */
class FakeWeatherProvider : WeatherProvider {
    override val id: String = "fake"
    override val label: String = "演示数据"

    private var forcedCode: WeatherCode? = null
    private val cycle: List<WeatherCode?> = WeatherCode.entries + listOf(null)

    fun forceNextCode() {
        forcedCode = cycle[(cycle.indexOf(forcedCode) + 1) % cycle.size]
    }

    override suspend fun forecast(lat: Double, lon: Double): Forecast {
        val now = LocalDateTime.now().let { it.withMinute(0).withSecond(0).withNano(0) }
        val currentCode = forcedCode ?: defaultCodeAt(now.hour)

        val hourly = (0 until 24).map { i ->
            val t = now.plusHours(i.toLong())
            val prob = precipProbAt(t.hour)
            HourForecast(
                time = t,
                precipProbability = prob,
                temperature = temperatureAt(t.hour),
                code = if (prob >= 60) WeatherCode.HEAVY_RAIN
                    else if (prob >= 30) WeatherCode.RAIN
                    else if (t.hour in 6..18) WeatherCode.SUNNY
                    else WeatherCode.CLOUDY,
            )
        }

        val dayCodes = listOf(
            WeatherCode.RAIN, WeatherCode.PARTLY_CLOUDY, WeatherCode.SUNNY,
            WeatherCode.CLOUDY, WeatherCode.THUNDER, WeatherCode.SNOW, WeatherCode.PARTLY_CLOUDY,
        )
        val dayProbs = listOf(70, 40, 5, 10, 85, 30, 20)
        val dayMins = listOf(6, 5, 4, 5, 8, -2, 0)
        val dayMaxs = listOf(13, 12, 11, 12, 15, 3, 5)
        val daily = (0 until 7).map { i ->
            DayForecast(LocalDate.now().plusDays(i.toLong()), dayMins[i], dayMaxs[i], dayProbs[i], dayCodes[i])
        }

        return Forecast(
            locationName = "我的城市",
            current = CurrentWeather(temperatureAt(now.hour), currentCode),
            hourly = hourly,
            daily = daily,
        )
    }

    private fun defaultCodeAt(hour: Int): WeatherCode = when (hour) {
        in 15..19 -> WeatherCode.RAIN
        in 9..14 -> WeatherCode.PARTLY_CLOUDY
        else -> WeatherCode.SUNNY
    }

    private fun precipProbAt(hour: Int): Int = when (hour) {
        15 -> 40
        16 -> 70
        17 -> 85
        18 -> 60
        19 -> 30
        in 9..14 -> 10
        in 20..23 -> 15
        else -> 5
    }

    private fun temperatureAt(hour: Int): Int = 6 + when (hour) {
        in 2..6 -> 1
        in 7..10 -> 3
        in 11..14 -> 7
        in 15..18 -> 6
        in 19..22 -> 4
        else -> 2
    }
}
