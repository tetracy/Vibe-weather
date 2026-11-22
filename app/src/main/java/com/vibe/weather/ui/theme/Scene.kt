package com.vibe.weather.ui.theme

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.withFrameNanos
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import com.vibe.weather.domain.WeatherCode
import kotlin.math.abs
import kotlin.math.sin

/** 一个天气场景的全部视觉参数：天空渐变 + 粒子动效（docs/PLAN.md §3）。 */
data class SceneSpec(
    val skyTop: Color,
    val skyBottom: Color,
    val effect: SceneEffect,
)

enum class SceneEffect { NONE, RAIN, HEAVY_RAIN, SNOW, STARS, CLOUDS }

// 天色：自上而下的渐变两端
private val SunnyTop = Color(0xFF1565C0)
private val SunnyBottom = Color(0xFF64B5F6)
private val PartlyTop = Color(0xFF37699C)
private val PartlyBottom = Color(0xFF8FB8DE)
private val CloudyTop = Color(0xFF546E7A)
private val CloudyBottom = Color(0xFF90A4AE)
private val RainTop = Color(0xFF37474F)
private val RainBottom = Color(0xFF607D8B)
private val HeavyTop = Color(0xFF263238)
private val HeavyBottom = Color(0xFF455A64)
private val ThunderTop = Color(0xFF1A237E)
private val ThunderBottom = Color(0xFF283593)
private val SnowTop = Color(0xFF5C7A8A)
private val SnowBottom = Color(0xFFB0BEC5)
private val NightTop = Color(0xFF0B1026)
private val NightBottom = Color(0xFF1C2E4A)
private val NightRainTop = Color(0xFF101828)
private val NightRainBottom = Color(0xFF2A3648)

/** 昼夜分界：19 点后算夜。 */
fun isNight(hour: Int): Boolean = hour < 6 || hour >= 19

/** 纯函数映射：统一天气码 + 时刻 → 场景规格。可单测。 */
fun sceneFor(code: WeatherCode, hour: Int): SceneSpec = when (code) {
    WeatherCode.SUNNY ->
        if (isNight(hour)) SceneSpec(NightTop, NightBottom, SceneEffect.STARS)
        else SceneSpec(SunnyTop, SunnyBottom, SceneEffect.NONE)

    WeatherCode.PARTLY_CLOUDY ->
        if (isNight(hour)) SceneSpec(NightTop, NightBottom, SceneEffect.STARS)
        else SceneSpec(PartlyTop, PartlyBottom, SceneEffect.CLOUDS)

    WeatherCode.CLOUDY -> SceneSpec(CloudyTop, CloudyBottom, SceneEffect.CLOUDS)

    WeatherCode.RAIN ->
        if (isNight(hour)) SceneSpec(NightRainTop, NightRainBottom, SceneEffect.RAIN)
        else SceneSpec(RainTop, RainBottom, SceneEffect.RAIN)

    WeatherCode.HEAVY_RAIN ->
        if (isNight(hour)) SceneSpec(NightRainTop, NightRainBottom, SceneEffect.HEAVY_RAIN)
        else SceneSpec(HeavyTop, HeavyBottom, SceneEffect.HEAVY_RAIN)

    WeatherCode.THUNDER ->
        if (isNight(hour)) SceneSpec(NightRainTop, NightRainBottom, SceneEffect.HEAVY_RAIN)
        else SceneSpec(ThunderTop, ThunderBottom, SceneEffect.HEAVY_RAIN)

    WeatherCode.SNOW -> SceneSpec(SnowTop, SnowBottom, SceneEffect.SNOW)
}

/** 沉浸背景：渐变天空 + 一层 Canvas 粒子，不引入游戏引擎（docs/PLAN.md §3）。 */
@Composable
fun SceneBackground(spec: SceneSpec, modifier: Modifier = Modifier) {
    Box(modifier = modifier.fillMaxSize()) {
        Box(
            Modifier
                .fillMaxSize()
                .background(Brush.verticalGradient(listOf(spec.skyTop, spec.skyBottom)))
        )
        when (spec.effect) {
            SceneEffect.NONE -> Unit
            SceneEffect.RAIN -> RainLayer(intensity = 0.6f)
            SceneEffect.HEAVY_RAIN -> RainLayer(intensity = 1f)
            SceneEffect.SNOW -> SnowLayer()
            SceneEffect.STARS -> StarLayer()
            SceneEffect.CLOUDS -> CloudLayer()
        }
    }
}

/** 帧驱动的动画时钟：进入组合以来的秒数。 */
@Composable
private fun secondsSinceStart(): Float {
    var elapsed by remember { mutableStateOf(0f) }
    LaunchedEffect(Unit) {
        val start = withFrameNanos { it }
        while (true) {
            withFrameNanos { now -> elapsed = (now - start) / 1_000_000_000f }
        }
    }
    return elapsed
}

@Composable
private fun RainLayer(intensity: Float) {
    val t = secondsSinceStart()
    Canvas(Modifier.fillMaxSize()) {
        val w = size.width
        val h = size.height
        val count = (50 * intensity).toInt()
        repeat(count) { i ->
            val depth = (i % 3) / 3f
            val speed = 700f + depth * 900f
            val x = ((i * 977f) % w) + depth * 30f
            val y = ((t * speed + i * 613f) % (h + 200f)) - 100f
            val len = 16f + depth * 26f
            drawLine(
                color = Color.White.copy(alpha = 0.18f + depth * 0.22f),
                start = Offset(x, y),
                end = Offset(x - len * 0.18f, y + len),
                strokeWidth = 1f + depth,
            )
        }
    }
}

@Composable
private fun SnowLayer() {
    val t = secondsSinceStart()
    Canvas(Modifier.fillMaxSize()) {
        val w = size.width
        val h = size.height
        repeat(45) { i ->
            val depth = (i % 3) / 3f
            val baseX = (i * 733f) % w
            val y = ((t * (35f + depth * 55f) + i * 613f) % (h + 40f)) - 20f
            val x = baseX + sin(t * 0.8f + i) * 28f
            drawCircle(
                color = Color.White.copy(alpha = 0.5f + depth * 0.4f),
                radius = 2.2f + depth * 2.6f,
                center = Offset(x, y),
            )
        }
    }
}

@Composable
private fun StarLayer() {
    val t = secondsSinceStart()
    Canvas(Modifier.fillMaxSize()) {
        val w = size.width
        val h = size.height
        repeat(60) { i ->
            val x = (i * 887f) % w
            val y = (i * 449f) % (h * 0.72f)
            val twinkle = 0.4f + 0.6f * abs(sin(t * (0.6f + (i % 7) * 0.25f) + i))
            drawCircle(
                color = Color.White.copy(alpha = twinkle * 0.9f),
                radius = if (i % 9 == 0) 2.4f else 1.3f,
                center = Offset(x, y),
            )
        }
    }
}

@Composable
private fun CloudLayer() {
    val t = secondsSinceStart()
    Canvas(Modifier.fillMaxSize()) {
        val w = size.width
        val h = size.height
        repeat(5) { i ->
            val speed = 6f + i * 4f
            val x = ((t * speed + i * 577f) % (w + 700f)) - 350f
            val y = h * (0.05f + (i % 3) * 0.13f)
            drawOval(
                color = Color.White.copy(alpha = 0.07f + (i % 3) * 0.04f),
                topLeft = Offset(x, y),
                size = Size(340f + i * 110f, 110f + i * 26f),
            )
        }
    }
}
