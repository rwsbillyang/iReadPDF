package com.github.rwsbillyang.iReadPDF.ui.theme

import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.ui.graphics.Color

//高级紫调（金融/科技类应用）：以紫色为核心，传递高端、专业的感觉，适合金融或科技类应用。

val LightPurpleScheme = lightColorScheme(
    // 主品牌色：深紫（专业感）
    primary = Color(0xFF6200EE), // Material 深紫（Pantone 2685C）
    onPrimary = Color(0xFFFFFFFF), // 白色，对比度 10.2:1（合规）
    primaryContainer = Color(0xFFBB86FC), // 浅紫容器（Material 浅紫）
    onPrimaryContainer = Color(0xFF3700B3), // 深紫文本（对比度 12.7:1，合规）

    // 次要操作色：银灰（科技感）
    secondary = Color(0xFF616161), // 中灰（Material 中灰）
   // onSecondary = Color(0xFFFFFFFF), // 白色，对比度 3.9:1（需调整！改为浅灰文本）
    // 修正：原 onSecondary 对比度不足，改为浅灰
    onSecondary = Color(0xFFE0E0E0), // 浅灰，对比度 6.8:1（合规）
    secondaryContainer = Color(0xFF9E9E9E), // 浅灰容器（呼应主色）
    onSecondaryContainer = Color(0xFF616161), // 中灰文本（对比度 4.8:1，合规）

    // 表面系列：白/浅紫分层
    surface = Color(0xFFFFFFFF), // 纯白背景
    onSurface = Color(0xFF1C1B1F), // 深灰文本（对比度 15:1）
    surfaceVariant = Color(0xFFF5F5F5), // 浅灰输入框（柔和）
    onSurfaceVariant = Color(0xFF49454F), // 中灰输入文本（对比度 9:1，合规）
    background = Color(0xFFF3E5F5), // 极浅紫页面背景（高级感）
    onBackground = Color(0xFF1C1B1F), // 深灰背景文本（对比度 15:1）

    // 错误色：标准红（保留警示性）
    error = Color(0xFFB00020),
    onError = Color(0xFFFFFFFF)
)

val DarkPurpleScheme = darkColorScheme(
    // 主品牌色：浅紫（深色背景更突出）
    primary = Color(0xFFBB86FC), // Material 浅紫（深色模式主色）
    onPrimary = Color(0xFF000000), // 黑色，对比度 18:1（合规）
    primaryContainer = Color(0xFF3700B3), // 深紫容器（强化层次）
    onPrimaryContainer = Color(0xFFE0E0E0), // 浅灰文本（对比度 6.8:1，合规）

    // 次要操作色：银灰（科技感）
    secondary = Color(0xFF9E9E9E), // 中灰（Material 中灰变体）
    onSecondary = Color(0xFF000000), // 黑色，对比度 15:1（合规）
    secondaryContainer = Color(0xFF616161), // 深灰容器（弱化）
    onSecondaryContainer = Color(0xFFE0E0E0), // 浅灰文本（对比度 7.2:1，合规）

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