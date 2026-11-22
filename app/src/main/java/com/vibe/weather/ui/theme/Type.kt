package com.vibe.weather.ui.theme

import androidx.compose.ui.text.ExperimentalTextApi
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontVariation
import androidx.compose.ui.text.font.FontWeight
import com.vibe.weather.R

/**
 * 大温度数字用细字重 display：Outfit 可变字体（OFL，仅 108KB）。
 * 中文不在字体里，自动回退系统黑体（国产 ROM 上是 MiSans/OPPO Sans 等）。
 */
@OptIn(ExperimentalTextApi::class)
val OutfitDisplay = FontFamily(
    Font(
        resId = R.font.outfit_variable,
        weight = FontWeight.ExtraLight,
        variationSettings = FontVariation.Settings(FontVariation.weight(200)),
    ),
)

/** 单色风的点阵点缀：Silkscreen（OFL，32KB）。 */
val PixelFont = FontFamily(
    Font(resId = R.font.silkscreen_regular, weight = FontWeight.Normal),
)
