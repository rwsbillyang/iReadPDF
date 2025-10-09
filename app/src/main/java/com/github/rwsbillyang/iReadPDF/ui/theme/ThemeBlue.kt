package com.github.rwsbillyang.iReadPDF.ui.theme

import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.ui.graphics.Color

//清新蓝调 主题特点：以蓝色为核心，传递清爽、可靠的感觉，适合工具类或社交类应用。

// 浅色模式（清新蓝）
val LightBlueScheme = lightColorScheme(
    // 核心品牌色
    primary = Color(0xFF2962FF), // 活力蓝
    onPrimary = Color(0xFFFFFFFF), // 白色（高对比）
    primaryContainer = Color(0xFFD0E3FF), // 浅蓝容器
    onPrimaryContainer = Color(0xFF001E6C), // 深蓝文本

    // 次要操作色
    secondary = Color(0xFF00BFA5), // 青柠绿
    onSecondary = Color(0xFFFFFFFF), // 白色
    secondaryContainer = Color(0xFFB9F6CA), // 浅绿容器
    onSecondaryContainer = Color(0xFF004D40), // 深绿文本

    // Surface系列（蓝调浅色调）
    surface = Color(0xFFE3F2FD), // 主色加白 → 淡蓝背景（卡片/对话框）
    onSurface = Color(0xFF00195C), // 主色加黑 → 深蓝文本（高对比）
    surfaceVariant = Color(0xFFBBDEFB), // 主色的浅色调 → 更淡蓝（输入框）
    onSurfaceVariant = Color(0xFF001E6C), // 主色的深色调 → 深蓝文本（输入框内容）
    background = Color(0xFFF5FBFF), // 更淡蓝 → 页面全局背景
    onBackground = Color(0xFF002984), // 主色的深色调 → 深蓝文本（背景装饰）

    // 错误色（保留警示性）
    error = Color(0xFFB00020),
    onError = Color(0xFFFFFFFF)
)

// 深色模式（清新蓝）
val DarkBlueScheme = darkColorScheme(
    primary = Color(0xFF0039CB), // 深蓝
    onPrimary = Color(0xFFFFFFFF), // 白色
    primaryContainer = Color(0xFF0050B3), // 深蓝容器
    onPrimaryContainer = Color(0xFFD0E3FF), // 浅蓝文本

    secondary = Color(0xFF006D5B), // 深青
    onSecondary = Color(0xFFFFFFFF), // 白色
    secondaryContainer = Color(0xFF00897B), // 青容器
    onSecondaryContainer = Color(0xFFB9F6CA), // 浅绿文本

    // Surface系列（蓝调深色调）
    surface = Color(0xFF1E3A8A), // 深蓝 → 卡片背景
    onSurface = Color(0xFFBBDEFB), // 浅蓝 → 卡片文本（高对比）
    surfaceVariant = Color(0xFF1565C0), // 深蓝变体 → 输入框背景
    onSurfaceVariant = Color(0xFFE3F2FD), // 淡蓝 → 输入框文本
    background = Color(0xFF0F172A), // 更深蓝 → 页面背景
    onBackground = Color(0xFFBBDEFB), // 浅蓝 → 背景文本

    error = Color(0xFFB00020),
    onError = Color(0xFFFFFFFF)
)