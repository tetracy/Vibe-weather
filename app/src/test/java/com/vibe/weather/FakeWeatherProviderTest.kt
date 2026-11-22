package com.vibe.weather

import com.vibe.weather.data.weather.FakeWeatherProvider
import com.vibe.weather.domain.WeatherCode
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import java.time.LocalDateTime

class FakeWeatherProviderTest {

    private val provider = FakeWeatherProvider()

    @Test
    fun returnsFullForecast() = runBlocking {
        val forecast = provider.forecast(0.0, 0.0)
        assertEquals(24, forecast.hourly.size)
        assertEquals(7, forecast.daily.size)
        assertEquals("我的城市", forecast.locationName)
    }

    @Test
    fun hourlyStartsAtCurrentHour() = runBlocking {
        val now = LocalDateTime.now().withMinute(0).withSecond(0).withNano(0)
        val forecast = provider.forecast(0.0, 0.0)
        assertEquals(now, forecast.hourly.first().time)
    }

    @Test
    fun probabilitiesStayWithinBounds() = runBlocking {
        val forecast = provider.forecast(0.0, 0.0)
        forecast.hourly.forEach { assertTrue(it.precipProbability in 0..100) }
        forecast.daily.forEach { assertTrue(it.precipProbability in 0..100) }
    }

    @Test
    fun forcingCyclesThroughAllCodes() = runBlocking {
        // 从自动模式起按一轮：SUNNY → PARTLY_CLOUDY → CLOUDY → RAIN → HEAVY_RAIN → THUNDER → SNOW
        WeatherCode.entries.forEach { expected ->
            provider.forceNextCode()
            assertEquals(expected, provider.forecast(0.0, 0.0).current.code)
        }
    }
}
