package com.vibe.weather.ui.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.vibe.weather.data.WeatherRepository
import com.vibe.weather.domain.Forecast
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

sealed interface UiState {
    data object Loading : UiState
    data class Ready(val forecast: Forecast) : UiState
    data class Error(val message: String) : UiState
}

class HomeViewModel(private val repository: WeatherRepository) : ViewModel() {

    private val _state = MutableStateFlow<UiState>(UiState.Loading)
    val state: StateFlow<UiState> = _state.asStateFlow()

    init {
        refresh()
    }

    fun refresh() {
        viewModelScope.launch {
            // M0：假数据瞬时返回；M2 这里变成 SWR（先发缓存，再静默换新）。
            runCatching { repository.forecast() }
                .onSuccess { _state.value = UiState.Ready(it) }
                .onFailure { e -> _state.value = UiState.Error(e.message ?: "加载失败") }
        }
    }

    /** 点按头部轮换演示天气，预览各场景（M1.5 起换成数据源切换）。 */
    fun cycleConditions() {
        repository.cycleDemoConditions()
        refresh()
    }
}
