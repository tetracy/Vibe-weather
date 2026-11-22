package com.vibe.weather.ui.theme

import androidx.compose.ui.graphics.Color

// 灰阶三档：Nothing 式层级（docs/research/design-references.md §4.1）
val TextPrimary = Color.White
val TextSecondary = Color.White.copy(alpha = 0.7f)
val TextTertiary = Color.White.copy(alpha = 0.45f)

/**
 * Apple 六档温度色（docs/research §3.3）：
 * <0 深蓝 · 0-15 浅蓝 · 15-20 绿 · 20-25 黄 · 25-30 橙 · ≥30 红。
 */
fun temperatureTierColor(tempC: Int): Color = when {
    tempC < 0 -> Color(0xFF2E5FA3)
    tempC < 15 -> Color(0xFF5AAFFA)
    tempC < 20 -> Color(0xFF6BBF59)
    tempC < 25 -> Color(0xFFE8C547)
    tempC < 30 -> Color(0xFFF08C4A)
    else -> Color(0xFFE05252)
}

/** 概率色档（docs/research §5.1 模式 3）：<40 灰淡 · 40-69 中蓝 · ≥70 亮青。 */
enum class ProbTier { LOW, MID, HIGH }

fun probTier(prob: Int): ProbTier = when {
    prob >= 70 -> ProbTier.HIGH
    prob >= 40 -> ProbTier.MID
    else -> ProbTier.LOW
}

fun probTierColor(prob: Int): Color = when (probTier(prob)) {
    ProbTier.HIGH -> Color(0xFF4DD8E6)
    ProbTier.MID -> Color(0xFF4FA8E8)
    ProbTier.LOW -> Color(0xFF81D4FA).copy(alpha = 0.35f)
}
