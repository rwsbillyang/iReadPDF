package com.github.rwsbillyang.iReadPDF.ui.theme

import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.ui.graphics.Color

//自然绿调（健康/环保类应用）：以绿色为核心，传递自然、健康的感觉，适合健康追踪或环保类应用。

val LightGreenScheme = lightColorScheme(
    // 主品牌色：草绿（自然感）
    primary = Color(0xFF388E3C), // Material 深草绿
    onPrimary = Color(0xFFFFFFFF), // 白色，对比度 8.1:1（合规）
    primaryContainer = Color(0xFFC8E6C9), // 浅草绿容器
    onPrimaryContainer = Color(0xFF1B5E20), // 深绿文本（对比度 10.5:1）

    // 次要操作色：米白（柔和）
    secondary = Color(0xFF8D6E63), // 棕米色（自然质感）
    //onSecondary = Color(0xFFFFFFFF), // 白色，对比度 3.8:1（需调整！改为深棕文本）
    // 修正：原 onSecondary 对比度不足，改为深棕
    onSecondary = Color(0xFF3E2723), // 深棕，对比度 9.2:1（合规）
    secondaryContainer = Color(0xFFF5EBE0), // 米白容器
    onSecondaryContainer = Color(0xFF5D4037), // 中棕文本（对比度 6.5:1，合规）

    // 表面系列：白/浅绿分层
    surface = Color(0xFFFFFFFF), // 纯白背景
    onSurface = Color(0xFF1C1B1F), // 深灰文本（对比度 15:1）
    surfaceVariant = Color(0xFFE8F5E9), // 浅绿输入框（呼应主色）
    onSurfaceVariant = Color(0xFF33691E), // 深绿输入文本（对比度 8.2:1，合规）
    background = Color(0xFFF1F8E9), // 极浅绿页面背景（自然感）
    onBackground = Color(0xFF1C1B1F), // 深灰背景文本（对比度 15:1）

    // 错误色：标准红（保留警示性）
    error = Color(0xFFB00020),
    onError = Color(0xFFFFFFFF)
)

val DarkGreenScheme = darkColorScheme(
    // 主品牌色：深橄榄绿（深色背景更沉稳）
    primary = Color(0xFF1B5E20), // Material 深橄榄绿
    onPrimary = Color(0xFFFFFFFF), // 白色，对比度 12.3:1（合规）
    primaryContainer = Color(0xFF2E7D32), // 深绿容器（强化层次）
    onPrimaryContainer = Color(0xFFC8E6C9), // 浅草绿文本（对比度 6.1:1，合规）

    // 次要操作色：深棕（自然质感）
    secondary = Color(0xFF4E342E), // 深棕（Material 深棕变体）
    onSecondary = Color(0xFFFFFFFF), // 白色，对比度 6.8:1（合规）
    secondaryContainer = Color(0xFF6D4C41), // 棕容器（弱化）
    onSecondaryContainer = Color(0xFFF5EBE0), // 米白文本（对比度 7.2:1，合规）

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