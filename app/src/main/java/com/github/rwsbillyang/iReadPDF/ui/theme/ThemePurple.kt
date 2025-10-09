package com.github.rwsbillyang.iReadPDF.ui.theme

import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.ui.graphics.Color

//高级紫调（金融/科技类应用）：以紫色为核心，传递高端、专业的感觉，适合金融或科技类应用。
// 浅色模式（高级紫）
val LightPurpleScheme = lightColorScheme(
    primary = Color(0xFF6200EE), // 深紫
    onPrimary = Color(0xFFFFFFFF), // 白色
    primaryContainer = Color(0xFFBB86FC), // 浅紫容器
    onPrimaryContainer = Color(0xFF3700B3), // 深紫文本

    secondary = Color(0xFF616161), // 中灰
    onSecondary = Color(0xFFE0E0E0), // 浅灰
    secondaryContainer = Color(0xFF9E9E9E), // 浅灰容器
    onSecondaryContainer = Color(0xFF616161), // 中灰文本

    // Surface系列（紫调浅色调）
    surface = Color(0xFFF3E5F5), // 主色加白 → 淡紫卡片
    onSurface = Color(0xFF1A0033), // 主色加黑 → 深紫文本
    surfaceVariant = Color(0xFFE1BEE7), // 主色的浅色调 → 更淡紫输入框
    onSurfaceVariant = Color(0xFF28004D), // 主色的深色调 → 深紫输入文本
    background = Color(0xFFFCE4EC), // 更淡紫 → 页面背景
    onBackground = Color(0xFF3C0069), // 主色的深色调 → 深紫背景文本

    error = Color(0xFFB00020),
    onError = Color(0xFFFFFFFF)
)

// 深色模式（高级紫）
val DarkPurpleScheme = darkColorScheme(
    primary = Color(0xFFBB86FC), // 浅紫
    onPrimary = Color(0xFF000000), // 黑色（高对比）
    primaryContainer = Color(0xFF3700B3), // 深紫容器
    onPrimaryContainer = Color(0xFFE0E0E0), // 浅灰文本

    secondary = Color(0xFF9E9E9E), // 中灰
    onSecondary = Color(0xFF000000), // 黑色
    secondaryContainer = Color(0xFF616161), // 深灰容器
    onSecondaryContainer = Color(0xFFE0E0E0), // 浅灰文本

    // Surface系列（紫调深色调）
    surface = Color(0xFF6200EE), // 深紫 → 卡片背景
    onSurface = Color(0xFFF3E5F5), // 淡紫 → 卡片文本
    surfaceVariant = Color(0xFF7C4DFF), // 深紫变体 → 输入框背景
    onSurfaceVariant = Color(0xFFE1BEE7), // 更淡紫 → 输入框文本
    background = Color(0xFF4A148C), // 更深紫 → 页面背景
    onBackground = Color(0xFFF3E5F5), // 淡紫 → 背景文本

    error = Color(0xFFB00020),
    onError = Color(0xFFFFFFFF)
)
