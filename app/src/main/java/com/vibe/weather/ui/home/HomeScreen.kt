package com.vibe.weather.ui.home

import androidx.compose.animation.Crossfade
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
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

    Crossfade(targetState = state, label = "state") { s ->
        when (s) {
            is UiState.Loading -> LoadingView()
            is UiState.Error -> ErrorView(s.message) { viewModel.refresh() }
            is UiState.Ready -> ReadyView(s.forecast, onHeaderTap = { viewModel.cycleConditions() })
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

/**
 * 定稿视觉：「无卡·单色」（docs/PLAN.md §2，2026-11-23 A/B 后选定）。
 * 无卡片，整屏暗纱承担对比，灰阶三档做层级，大间距呼吸。
 */
@Composable
private fun ReadyView(forecast: Forecast, onHeaderTap: () -> Unit) {
    val hour = forecast.hourly.firstOrNull()?.time?.hour ?: 12
    // 当前场景由「当前天气 + 此刻小时」决定，夜里自动切夜空（docs/PLAN.md §3）。
    val scene = sceneFor(forecast.current.code, hour)

    Box(Modifier.fillMaxSize()) {
        SceneBackground(scene)
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
                .navigationBarsPadding()
                .padding(bottom = 24.dp),
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
        }
    }
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
private fun MonoSectionLabel(text: String) {
    Text(
        text,
        fontSize = 12.sp,
        letterSpacing = 3.sp,
        color = TextSecondary,
        modifier = Modifier.padding(horizontal = 24.dp),
    )
}
