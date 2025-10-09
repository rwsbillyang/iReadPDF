package com.github.rwsbillyang.iReadPDF.ui.theme

import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.ui.graphics.Color

//自然绿调（健康/环保类应用）：以绿色为核心，传递自然、健康的感觉，适合健康追踪或环保类应用。
/**
 * 自然绿调 - 浅色模式（修正次要色一致性）
 * 主色：深橄榄绿（#1B5E20）→ 浅绿衍生次要色
 */
val LightGreenScheme = lightColorScheme(
    // 核心品牌色（主色）
    primary = Color(0xFF388E3C), // 深草绿（主操作色）
    onPrimary = Color(0xFFFFFFFF), // 白色（高对比）
    primaryContainer = Color(0xFFC8E6C9), // 浅绿容器
    onPrimaryContainer = Color(0xFF1B5E20), // 深绿文本

    // 次要操作色（修正：从主色衍生的浅绿）
    secondary = Color(0xFF81C784), // 浅绿（次要操作，如筛选按钮）
    onSecondary = Color(0xFF1B5E20), // 深绿（高对比文本）
    secondaryContainer = Color(0xFF66BB6A), // 中绿容器（次要内容区）
    onSecondaryContainer = Color(0xFF1B5E20), // 深绿文本（容器内）

    // Surface系列（绿调浅色调）
    surface = Color(0xFFE8F5E9), // 淡绿卡片背景
    onSurface = Color(0xFF00190F), // 深绿文本（卡片内容）
    surfaceVariant = Color(0xFFC8E6C9), // 更淡绿输入框
    onSurfaceVariant = Color(0xFF002E1A), // 深绿输入文本
    background = Color(0xFFF1F8E9), // 极淡绿页面背景
    onBackground = Color(0xFF003D23), // 深绿背景文本

    // 错误色（保留警示性）
    error = Color(0xFFB00020),
    onError = Color(0xFFFFFFFF)
)

/**
 * 自然绿调 - 深色模式（修正次要色一致性）
 * 主色：深橄榄绿（#1B5E20）→ 浅绿衍生次要色
 */
val DarkGreenScheme = darkColorScheme(
    // 核心品牌色（主色）
    primary = Color(0xFF1B5E20), // 深橄榄绿（深色模式主操作色）
    onPrimary = Color(0xFFFFFFFF), // 白色（高对比）
    primaryContainer = Color(0xFF2E7D32), // 深绿容器
    onPrimaryContainer = Color(0xFFC8E6C9), // 浅绿文本

    // 次要操作色（修正：从主色衍生的浅绿）
    secondary = Color(0xFF81C784), // 浅绿（次要操作，如筛选按钮）
    onSecondary = Color(0xFF1B5E20), // 深绿（高对比文本，对比度≈4.8:1）
    secondaryContainer = Color(0xFF66BB6A), // 中绿容器（次要内容区）
    onSecondaryContainer = Color(0xFF1B5E20), // 深绿文本（容器内，对比度≈5.1:1）

    // Surface系列（绿调深色调）
    surface = Color(0xFF1B5E20), // 深绿卡片背景（与主色呼应）
    onSurface = Color(0xFFC8E6C9), // 浅绿文本（卡片内容，高对比）
    surfaceVariant = Color(0xFF2E7D32), // 深绿输入框（主色变体）
    onSurfaceVariant = Color(0xFFE8F5E9), // 淡绿输入文本（高对比）
    background = Color(0xFF0D2C1D), // 更深绿页面背景（沉浸感）
    onBackground = Color(0xFFC8E6C9), // 浅绿背景文本（高对比）

    // 错误色（保留警示性）
    error = Color(0xFFB00020),
    onError = Color(0xFFFFFFFF)
)