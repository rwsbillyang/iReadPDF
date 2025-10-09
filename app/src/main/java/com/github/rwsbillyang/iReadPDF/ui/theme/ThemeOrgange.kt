package com.github.rwsbillyang.iReadPDF.ui.theme

import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.ui.graphics.Color


//温暖橙调（餐饮/生活服务类应用）：以橙色为核心，传递温暖、亲切的感觉，适合餐饮或本地生活类应用。
// 浅色模式（温暖橙）
val LightOrangeScheme = lightColorScheme(
    primary = Color(0xFFFF9800), // 暖橙
    onPrimary = Color(0xFF333333), // 深灰（高对比）
    primaryContainer = Color(0xFFFFCC80), // 浅橙容器
    onPrimaryContainer = Color(0xFFBF360C), // 深橙文本

    secondary = Color(0xFF5D4037), // 深棕
    onSecondary = Color(0xFFE0E0E0), // 浅灰
    secondaryContainer = Color(0xFFFFCCBC), // 米黄容器
    onSecondaryContainer = Color(0xFF5D4037), // 深棕文本

    // Surface系列（橙调浅色调）
    surface = Color(0xFFFFF3E0), // 主色加白 → 淡橙卡片
    onSurface = Color(0xFF4D0000), // 主色加黑 → 深橙文本
    surfaceVariant = Color(0xFFFFE0B2), // 主色的浅色调 → 更淡橙输入框
    onSurfaceVariant = Color(0xFF660000), // 主色的深色调 → 深橙输入文本
    background = Color(0xFFFFF8E1), // 更淡橙 → 页面背景
    onBackground = Color(0xFF7F0000), // 主色的深色调 → 深橙背景文本

    error = Color(0xFFB00020),
    onError = Color(0xFFFFFFFF)
)

// 深色模式（温暖橙）
val DarkOrangeScheme = darkColorScheme(
    primary = Color(0xFFE65100), // 深橙
    onPrimary = Color(0xFFFFFFFF), // 白色
    primaryContainer = Color(0xFFFF9800), // 暖橙容器
    onPrimaryContainer = Color(0xFF333333), // 深灰文本

    secondary = Color(0xFF4E342E), // 深棕
    onSecondary = Color(0xFFFFFFFF), // 白色
    secondaryContainer = Color(0xFF6D4C41), // 棕容器
    onSecondaryContainer = Color(0xFFFFCCBC), // 米白文本

    // Surface系列（橙调深色调）
    surface = Color(0xFFE65100), // 深橙 → 卡片背景
    onSurface = Color(0xFFFFCCBC), // 米白 → 卡片文本
    surfaceVariant = Color(0xFFEF6C00), // 深橙变体 → 输入框背景
    onSurfaceVariant = Color(0xFFFFF3E0), // 淡橙 → 输入框文本
    background = Color(0xFFBF360C), // 更深橙 → 页面背景
    onBackground = Color(0xFFFFCCBC), // 米白 → 背景文本

    error = Color(0xFFB00020),
    onError = Color(0xFFFFFFFF)
)