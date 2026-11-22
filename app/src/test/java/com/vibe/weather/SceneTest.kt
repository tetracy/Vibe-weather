package com.vibe.weather

import com.vibe.weather.domain.WeatherCode
import com.vibe.weather.ui.theme.SceneEffect
import com.vibe.weather.ui.theme.isNight
import com.vibe.weather.ui.theme.sceneFor
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class SceneTest {

    @Test
    fun sunnyDayHasNoParticles() {
        assertEquals(SceneEffect.NONE, sceneFor(WeatherCode.SUNNY, 13).effect)
    }

    @Test
    fun sunnyNightShowsStars() {
        assertEquals(SceneEffect.STARS, sceneFor(WeatherCode.SUNNY, 23).effect)
    }

    @Test
    fun beforeDawnAlsoCountsAsNight() {
        assertEquals(SceneEffect.STARS, sceneFor(WeatherCode.PARTLY_CLOUDY, 5).effect)
        assertTrue(isNight(5))
    }

    @Test
    fun rainFallsDayAndNight() {
        assertEquals(SceneEffect.RAIN, sceneFor(WeatherCode.RAIN, 15).effect)
        assertEquals(SceneEffect.RAIN, sceneFor(WeatherCode.RAIN, 3).effect)
    }

    @Test
    fun heavyRainAndThunderUseHeavyLayer() {
        assertEquals(SceneEffect.HEAVY_RAIN, sceneFor(WeatherCode.HEAVY_RAIN, 15).effect)
        assertEquals(SceneEffect.HEAVY_RAIN, sceneFor(WeatherCode.THUNDER, 15).effect)
    }

    @Test
    fun snowFallsDayAndNight() {
        assertEquals(SceneEffect.SNOW, sceneFor(WeatherCode.SNOW, 10).effect)
        assertEquals(SceneEffect.SNOW, sceneFor(WeatherCode.SNOW, 22).effect)
    }

    @Test
    fun cloudyDayShowsClouds() {
        assertEquals(SceneEffect.CLOUDS, sceneFor(WeatherCode.PARTLY_CLOUDY, 12).effect)
        assertEquals(SceneEffect.CLOUDS, sceneFor(WeatherCode.CLOUDY, 12).effect)
    }
}
