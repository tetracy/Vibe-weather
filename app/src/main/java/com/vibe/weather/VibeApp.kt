package com.vibe.weather

import android.app.Application
import com.vibe.weather.data.WeatherRepository
import com.vibe.weather.data.weather.FakeWeatherProvider
import com.vibe.weather.domain.WeatherProvider

class VibeApp : Application() {
    lateinit var container: AppContainer
        private set

    override fun onCreate() {
        super.onCreate()
        container = AppContainer()
    }
}

/** 手写依赖注入（docs/PLAN.md §5：不引入 DI 框架）。 */
class AppContainer {
    val weatherProvider: WeatherProvider = FakeWeatherProvider()
    val repository: WeatherRepository = WeatherRepository(weatherProvider)
}
