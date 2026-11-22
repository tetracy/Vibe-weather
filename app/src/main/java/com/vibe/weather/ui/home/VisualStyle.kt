package com.vibe.weather.ui.home

/**
 * M0 视觉对比：两套候选风格并存，底部点按切换。
 * 定稿后删除败者（docs/research/design-references.md §7）。
 */
enum class VisualStyle(val label: String) {
    /** 方向一「夜窗」：Apple 式玻璃卡 + 概率色档 + 彩色温度范围条。 */
    NIGHT_WINDOW("夜窗"),

    /** 方向二「无卡·单色」：Nothing 式极简，灰阶三档 + 点阵点缀。 */
    MONO("单色"),
}
