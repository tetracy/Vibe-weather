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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.vibe.weather.domain.Forecast
import com.vibe.weather.ui.theme.SceneBackground
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

@Composable
private fun ReadyView(forecast: Forecast, onHeaderTap: () -> Unit) {
    val hour = forecast.hourly.firstOrNull()?.time?.hour ?: 12
    // 当前场景由「当前天气 + 此刻小时」决定，夜里自动切夜空（docs/PLAN.md §3）。
    val scene = sceneFor(forecast.current.code, hour)

    Box(Modifier.fillMaxSize()) {
        SceneBackground(scene)
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                // 暗纱：保证任何场景下文字对比度（docs/PLAN.md §3）。
                .background(
                    Brush.verticalGradient(
                        listOf(
                            Color.Black.copy(alpha = 0.10f),
                            Color.Black.copy(alpha = 0.45f),
                        )
                    )
                )
                .statusBarsPadding()
                .padding(bottom = 24.dp),
        ) {
            Header(forecast, onTap = onHeaderTap)
            Spacer(Modifier.height(12.dp))
            SectionLabel("未来 24 小时 · 降雨概率")
            HourlyRainStrip(forecast.hourly)
            Spacer(Modifier.height(20.dp))
            SectionLabel("未来 7 天")
            DailyList(forecast.daily)
            Spacer(Modifier.height(16.dp))
            Text(
                "M0 原型 · 演示数据（点按顶部轮换天气场景）",
                fontSize = 11.sp,
                color = Color.White.copy(alpha = 0.5f),
                modifier = Modifier.padding(horizontal = 20.dp),
            )
        }
    }
}

@Composable
private fun SectionLabel(text: String) {
    Text(
        text = text,
        fontSize = 13.sp,
        color = Color.White.copy(alpha = 0.7f),
        modifier = Modifier.padding(horizontal = 20.dp),
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
            fontWeight = FontWeight.Bold,
            color = Color.White,
        )
        Text(forecast.current.code.label, fontSize = 20.sp, color = Color.White.copy(alpha = 0.95f))
        Text(
            "演示数据 · 点按这里切换场景",
            fontSize = 12.sp,
            color = Color.White.copy(alpha = 0.55f),
        )
    }
}
