package com.vibe.weather

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.lifecycle.viewmodel.compose.viewModel
import com.vibe.weather.ui.home.HomeScreen
import com.vibe.weather.ui.home.HomeViewModel
import com.vibe.weather.ui.theme.VibeTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        val container = (application as VibeApp).container
        setContent {
            VibeTheme {
                HomeScreen(
                    viewModel = viewModel { HomeViewModel(container.repository) },
                )
            }
        }
    }
}
