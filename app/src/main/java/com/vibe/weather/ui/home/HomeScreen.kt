package com.vibe.weather.ui.home

import androidx.compose.animation.Crossfade
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.vibe.weather.domain.Forecast
import com.vibe.weather.ui.theme.OutfitDisplay
import com.vibe.weather.ui.theme.SceneBackground
import com.vibe.weather.ui.theme.TextSecondary
import com.vibe.weather.ui.theme.sceneFor

@Composable
fun HomeScreen(viewModel: HomeViewModel) {
    val state by viewModel.state.collectAsState()
    val style by viewModel.style.collectAsState()

    Crossfade(targetState = state, label = "state") { s ->
        when (s) {
            is UiState.Loading -> LoadingView()
            is UiState.Error -> ErrorView(s.message) { viewModel.refresh() }
            is UiState.Ready -> ReadyView(
                forecast = s.forecast,
                style = style,
                onHeaderTap = { viewModel.cycleConditions() },
                onFooterTap = { viewModel.cycleStyle() },
            )
        }
    }
}

@Composable
private fun LoadingView() {
    Box(
        Modifier
            .fillMaxSize()
            .background(Color(0xFF0B1026)),
        contentAlignment = Alignment.Center,
    ) {
        CircularProgressIndicator(color = Color.White.copy(alpha = 0.7f))
    }
}

@Composable
private fun ErrorView(message: String, onRetry: () -> Unit) {
    Box(
        Modifier
            .fillMaxSize()
            .background(Color(0xFF0B1026)),
        contentAlignment = Alignment.Center,
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(message, color = Color.White.copy(alpha = 0.8f))
            Spacer(Modifier.height(12.dp))
            Button(onClick = onRetry) { Text("重试") }
        }
    }
}

@Composable
private fun ReadyView(
    forecast: Forecast,
    style: VisualStyle,
    onHeaderTap: () -> Unit,
    onFooterTap: () -> Unit,
) {
    val hour = forecast.hourly.firstOrNull()?.time?.hour ?: 12
    // 当前场景由「当前天气 + 此刻小时」决定，夜里自动切夜空（docs/PLAN.md §3）。
    val scene = sceneFor(forecast.current.code, hour)

    Box(Modifier.fillMaxSize()) {
        SceneBackground(scene)
        when (style) {
            VisualStyle.NIGHT_WINDOW ->
                NightWindowView(forecast, onHeaderTap, onFooterTap)
            VisualStyle.MONO ->
                MonoView(forecast, onHeaderTap, onFooterTap)
        }
    }
}

/** 方向一「夜窗」：大数字压天空，两张玻璃卡承载主体数据。 */
@Composable
private fun NightWindowView(
    forecast: Forecast,
    onHeaderTap: () -> Unit,
    onFooterTap: () -> Unit,
) {
    Column(
        Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .statusBarsPadding()
            .padding(bottom = 24.dp),
    ) {
        Header(forecast, onHeaderTap)
        Spacer(Modifier.height(24.dp))
        GlassCard {
            SectionLabel("未来 24 小时 · 降雨概率")
            Spacer(Modifier.height(10.dp))
            TieredRainStrip(forecast.hourly)
        }
        Spacer(Modifier.height(16.dp))
        GlassCard {
            SectionLabel("未来 7 天")
            Spacer(Modifier.height(4.dp))
            RangeDailyList(forecast.daily)
        }
        Spacer(Modifier.height(16.dp))
        Footer("风格：夜窗 · 点按切换风格", onFooterTap)
    }
}

/** 方向二「无卡·单色」：无卡片，整屏暗纱 + 灰阶三档 + 大间距呼吸。 */
@Composable
private fun MonoView(
    forecast: Forecast,
    onHeaderTap: () -> Unit,
    onFooterTap: () -> Unit,
) {
    Column(
        Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .background(
                // 无卡设计：对比全靠这层纱（docs/research §7 方向二）
                Brush.verticalGradient(
                    listOf(
                        Color.Black.copy(alpha = 0.18f),
                        Color.Black.copy(alpha = 0.58f),
                    )
                )
            )
            .statusBarsPadding()
            .padding(bottom = 32.dp),
    ) {
        Header(forecast, onHeaderTap)
        Spacer(Modifier.height(36.dp))
        MonoSectionLabel("未来 24 小时 · 降雨概率")
        Spacer(Modifier.height(14.dp))
        MonoRainStrip(forecast.hourly)
        Spacer(Modifier.height(36.dp))
        MonoSectionLabel("未来 7 天")
        Spacer(Modifier.height(8.dp))
        MonoDailyList(forecast.daily)
        Spacer(Modifier.height(24.dp))
        Footer("风格：单色 · 点按切换风格", onFooterTap)
    }
}

/**
 * Apple 规格玻璃卡的纱面形态（docs/research §3.1）：
 * 黑 42% 打底 + 顶部一线微光 + 0.5dp 白描边。
 * 天空为柔和渐变，无真模糊时观感差异很小；平台 37 可装后接回 Haze 真模糊（§2.3）。
 */
@Composable
private fun GlassCard(content: @Composable ColumnScope.() -> Unit) {
    val shape = RoundedCornerShape(20.dp)
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp)
            .background(Color.Black.copy(alpha = 0.42f), shape)
            .background(
                Brush.verticalGradient(
                    listOf(Color.White.copy(alpha = 0.09f), Color.Transparent)
                ),
                shape,
            )
            .border(0.5.dp, Color.White.copy(alpha = 0.35f), shape)
            .clip(shape)
            .padding(horizontal = 16.dp, vertical = 14.dp),
        content = content,
    )
}

@Composable
private fun Header(forecast: Forecast, onTap: () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp)
            .clickable(onClick = onTap),
    ) {
        Text(forecast.locationName, fontSize = 15.sp, color = Color.White.copy(alpha = 0.85f))
        Text(
            "${forecast.current.temperature}°",
            fontSize = 76.sp,
            fontFamily = OutfitDisplay,
            color = Color.White,
        )
        Text(forecast.current.code.label, fontSize = 20.sp, color = Color.White.copy(alpha = 0.95f))
        Text(
            "演示数据 · 点按这里切换天气场景",
            fontSize = 12.sp,
            color = Color.White.copy(alpha = 0.55f),
        )
    }
}

@Composable
private fun SectionLabel(text: String) {
    Text(text, fontSize = 13.sp, color = Color.White.copy(alpha = 0.7f))
}

@Composable
private fun MonoSectionLabel(text: String) {
    Text(
        text,
        fontSize = 12.sp,
        letterSpacing = 3.sp,
        color = TextSecondary,
        modifier = Modifier.padding(horizontal = 24.dp),
    )
}

@Composable
private fun Footer(label: String, onTap: () -> Unit) {
    Text(
        label,
        fontSize = 12.sp,
        color = Color.White.copy(alpha = 0.55f),
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onTap)
            .padding(horizontal = 24.dp, vertical = 8.dp),
    )
}
