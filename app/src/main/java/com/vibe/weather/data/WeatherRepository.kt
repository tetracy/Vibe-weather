package com.vibe.weather.data

import com.vibe.weather.data.weather.FakeWeatherProvider
import com.vibe.weather.domain.Forecast
import com.vibe.weather.domain.WeatherProvider

/**
 * M0 版：直通 Provider。M2 在这里加入快照缓存与 SWR 策略。
 */
class WeatherRepository(val provider: WeatherProvider) {

    /** M2 接入定位后传真实坐标。 */
    suspend fun forecast(): Forecast = provider.forecast(0.0, 0.0)

    /** M0 预览用：轮换演示天气，看遍每种沉浸场景。M1.5 替换为数据源切换。 */
    fun cycleDemoConditions() {
        (provider as? FakeWeatherProvider)?.forceNextCode()
    }
}
