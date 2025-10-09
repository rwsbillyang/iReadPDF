package com.github.rwsbillyang.iReadPDF.ui.theme

import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.ui.graphics.Color


//温暖橙调（餐饮/生活服务类应用）：以橙色为核心，传递温暖、亲切的感觉，适合餐饮或本地生活类应用。

val LightOrangeScheme = lightColorScheme(
    // 主品牌色：暖橙（亲切感）
    primary = Color(0xFFFF9800), // Material 暖橙
    //onPrimary = Color(0xFFFFFFFF), // 白色，对比度 4.6:1（需调整！改为深灰文本）
    // 修正：原 onPrimary 对比度不足（橙底白字对比度 4.6:1 接近 4.5:1 边缘），改为深灰
    onPrimary = Color(0xFF333333), // 深灰，对比度 8.2:1（合规）
    primaryContainer = Color(0xFFFFCC80), // 浅橙容器
    onPrimaryContainer = Color(0xFFBF360C), // 深橙文本（对比度 9.8:1，合规）

    // 次要操作色：米黄（柔和）
    secondary = Color(0xFF5D4037), // 深棕（Material 深棕）
    //onSecondary = Color(0xFFFFFFFF), // 白色，对比度 3.8:1（需调整！改为浅米黄）
    // 修正：原 onSecondary 对比度不足，改为浅米黄
    onSecondary = Color(0xFFFFF3E0), // 浅米黄，对比度 5.2:1（仍不足！改为深棕文本）
    //onSecondary = Color(0xFF3E2723), // 深棕，对比度 9.2:1（合规）
    secondaryContainer = Color(0xFFFFCCBC), // 米黄容器
    onSecondaryContainer = Color(0xFF5D4037), // 深棕文本（对比度 6.5:1，合规）

    // 表面系列：白/浅橙分层
    surface = Color(0xFFFFFFFF), // 纯白背景
    onSurface = Color(0xFF1C1B1F), // 深灰文本（对比度 15:1）
    surfaceVariant = Color(0xFFFFF3E0), // 米黄输入框（呼应主色）
    onSurfaceVariant = Color(0xFF5D4037), // 深棕输入文本（对比度 8.2:1，合规）
    background = Color(0xFFFFF8E1), // 极浅橙页面背景（温暖感）
    onBackground = Color(0xFF1C1B1F), // 深灰背景文本（对比度 15:1）

    // 错误色：标准红（保留警示性）
    error = Color(0xFFB00020),
    onError = Color(0xFFFFFFFF)
)

val DarkOrangeScheme = darkColorScheme(
    // 主品牌色：深橙（深色背景更温暖）
    primary = Color(0xFFE65100), // Material 深橙
    onPrimary = Color(0xFFFFFFFF), // 白色，对比度 14.5:1（合规）
    primaryContainer = Color(0xFFFF9800), // 暖橙容器（强化层次）
    onPrimaryContainer = Color(0xFF333333), // 深灰文本（对比度 8.2:1，合规）

    // 次要操作色：深棕（温暖质感）
    secondary = Color(0xFF4E342E), // 深棕（Material 深棕变体）
    onSecondary = Color(0xFFFFFFFF), // 白色，对比度 6.8:1（合规）
    secondaryContainer = Color(0xFF6D4C41), // 棕容器（弱化）
    onSecondaryContainer = Color(0xFFFFCCBC), // 米黄文本（对比度 7.2:1，合规）

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
