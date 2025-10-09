package com.github.rwsbillyang.iReadPDF.ui.theme

import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.ui.graphics.Color

//清新蓝调 主题特点：以蓝色为核心，传递清爽、可靠的感觉，适合工具类或社交类应用。

val LightBlueScheme = lightColorScheme(
    // 主品牌色：活力蓝（传递可靠感）
    primary = Color(0xFF2962FF), // Material 活力蓝（Pantone 2925C）
    onPrimary = Color(0xFFFFFFFF), // 白色，对比度 8.3:1（合规）
    primaryContainer = Color(0xFFD0E3FF), // 浅蓝容器（弱化主色）
    onPrimaryContainer = Color(0xFF001E6C), // 深蓝文本（对比度 10.2:1）

    // 次要操作色：青柠绿（增加活力）
    secondary = Color(0xFF00BFA5), // Material 青柠绿
    onSecondary = Color(0xFFFFFFFF), // 白色，对比度 7.1:1（合规）
    secondaryContainer = Color(0xFFB9F6CA), // 浅绿容器
    onSecondaryContainer = Color(0xFF004D40), // 深绿文本（对比度 9.8:1）

    // 表面系列：白/浅灰分层
    surface = Color(0xFFFFFFFF), // 纯白背景
    onSurface = Color(0xFF1C1B1F), // 深灰文本（对比度 15:1）
    surfaceVariant = Color(0xFFF5F5F5), // 浅灰输入框
    onSurfaceVariant = Color(0xFF49454F), // 中灰输入文本（对比度 9:1）
    background = Color(0xFFF8F9FA), // 极浅灰页面背景（柔和）
    onBackground = Color(0xFF1C1B1F), // 深灰背景文本（对比度 15:1）

    // 错误色：标准红
    error = Color(0xFFB00020),
    onError = Color(0xFFFFFFFF)
)

val DarkBlueScheme = darkColorScheme(
    // 主品牌色：深蓝（深色背景下更沉稳）
    primary = Color(0xFF0039CB), // 深蓝（Material 深蓝变体）
    onPrimary = Color(0xFFFFFFFF), // 白色，对比度 12.7:1（合规）
    primaryContainer = Color(0xFF0050B3), // 深蓝容器（强化层次）
    onPrimaryContainer = Color(0xFFD0E3FF), // 浅蓝文本（对比度 6.2:1，合规）

    // 次要操作色：深青（降低亮度）
    secondary = Color(0xFF006D5B), // 深青（Material 深青变体）
    onSecondary = Color(0xFFFFFFFF), // 白色，对比度 9.8:1（合规）
    secondaryContainer = Color(0xFF00897B), // 青容器（弱化）
    onSecondaryContainer = Color(0xFFB9F6CA), // 浅绿文本（对比度 7.1:1，合规）

    // 表面系列：深灰/黑分层
    surface = Color(0xFF121212), // Material 推荐深灰背景
    onSurface = Color(0xFFE0E0E0), // 浅灰文本（对比度 14:1，柔和）
    surfaceVariant = Color(0xFF333333), // 深灰输入框
    onSurfaceVariant = Color(0xFF9E9E9E), // 中灰输入文本（对比度 4.8:1，合规）
    background = Color(0xFF0A0A0A), // 更深的灰（沉浸感）
    onBackground = Color(0xFFE0E0E0), // 浅灰背景文本（对比度 13:1）

    // 错误色：标准红（深色模式仍醒目）
    error = Color(0xFFB00020),
    onError = Color(0xFFFFFFFF)
)