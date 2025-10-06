package com.github.rwsbillyang.iReadPDF.ui.theme

import android.app.Activity
import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import androidx.core.view.ViewCompat


/**
 * 经典黑白风格 - 浅色模式 ColorScheme
 * 遵循 Material Design 3 规范，聚焦高对比度与可读性
 */
val LightColorScheme = lightColorScheme(
    // ------------------------------ 核心品牌色 ------------------------------
    /**
     * 主品牌色（Primary）：应用的核心标识色，用于按钮、重要操作入口
     * 浅色模式选纯黑（#000000），保证视觉冲击力
     */
    primary = Color(0xFF000000),
    /**
     * 主色上的文本/图标颜色（OnPrimary）：与 Primary 对比强烈的颜色，确保可读性
     * 这里用纯白（#FFFFFF），对比度 21:1（远高于 WCAG AA 级 4.5:1）
     */
    onPrimary = Color(0xFFFFFFFF),

    // ------------------------------ 主色容器（Primary Container） ------------------------------
    /**
     * 主色容器背景（PrimaryContainer）：用于强调主色的区域（如选中状态卡片）
     * 浅灰（#F5F5F5）弱化主色的攻击性，同时区分层级
     */
    primaryContainer = Color(0xFFF5F5F5),
    /**
     * 主容器内的文本/图标颜色（OnPrimaryContainer）：与 PrimaryContainer 对比的颜色
     * 黑色（#000000）确保容器内内容清晰
     */
    onPrimaryContainer = Color(0xFF000000),

    // ------------------------------ 次要操作色（Secondary） ------------------------------
    /**
     * 次要操作色（Secondary）：用于次要按钮、链接等非核心操作
     * 深灰（#333333）避免干扰主操作，同时保持可见性
     */
    secondary = Color(0xFF333333),
    /**
     * 次要色上的文本/图标颜色（OnSecondary）：与 Secondary 对比的颜色
     * 白色（#FFFFFF）确保次要操作可识别
     */
    onSecondary = Color(0xFFFFFFFF),

    // ------------------------------ 次要容器（Secondary Container） ------------------------------
    /**
     * 次要容器背景（SecondaryContainer）：用于次要内容的容器（如标签、提示）
     * 浅灰（#E0E0E0）区分次要内容与主内容
     */
    secondaryContainer = Color(0xFFE0E0E0),
    /**
     * 次要容器内的文本/图标颜色（OnSecondaryContainer）：与 SecondaryContainer 对比的颜色
     * 黑色（#000000）确保次要容器内内容清晰
     */
    onSecondaryContainer = Color(0xFF000000),

    // ------------------------------ 表面（Surface）系列 ------------------------------
    /**
     * 表面背景（Surface）：页面/卡片/对话框的基础背景色
     * 纯白（#FFFFFF）保持干净简洁，是浅色模式的默认背景
     */
    surface = Color(0xFFFFFFFF),
    /**
     * 表面上的文本/图标颜色（OnSurface）：表面上的主要内容（如标题、正文）
     * 黑色（#000000）是浅色模式的标准阅读色
     */
    onSurface = Color(0xFF000000),
    /**
     * 表面变体（SurfaceVariant）：输入框、分割线、禁用状态的背景色
     * 浅灰（#F0F0F0）柔和，避免视觉疲劳
     */
    surfaceVariant = Color(0xFFF0F0F0),
    /**
     * 表面变体上的文本/图标颜色（OnSurfaceVariant）：表面变体中的内容（如输入框文字）
     * 黑色（#000000）确保输入内容清晰
     */
    onSurfaceVariant = Color(0xFF000000),
    /**
     * 页面背景（Background）：整个页面的背景色（可覆盖 Surface）
     * 纯白（#FFFFFF）统一页面色调
     */
    background = Color(0xFFFFFFFF),
    /**
     * 背景上的文本/图标颜色（OnBackground）：背景层的全局文本颜色
     * 黑色（#000000）通用且易读
     */
    onBackground = Color(0xFF000000),

    // ------------------------------ 错误色（Error） ------------------------------
    /**
     * 错误提示色（Error）：用于错误提示框、无效输入等场景
     * Material 标准红色（#B00020），保证警示性
     */
    error = Color(0xFFB00020),
    /**
     * 错误信息文本颜色（OnError）：错误提示中的文字颜色
     * 白色（#FFFFFF）与错误色对比强烈，确保可读
     */
    onError = Color(0xFFFFFFFF)
)


/**
 * 经典黑白风格 - 深色模式 ColorScheme
 * 遵循 Material Design 3 深色模式规范，优化夜间使用体验
 */
val DarkColorScheme = darkColorScheme(
    // ------------------------------ 核心品牌色 ------------------------------
    /**
     * 主品牌色（Primary）：深色背景下的核心标识色
     * 浅灰（#E0E0E0）突出但不刺眼，避免纯白的强光刺激
     */
    primary = Color(0xFFE0E0E0),
    /**
     * 主色上的文本/图标颜色（OnPrimary）：与 Primary 对比的颜色
     * 黑色（#000000）确保主色上的内容清晰
     */
    onPrimary = Color(0xFF000000),

    // ------------------------------ 主色容器（Primary Container） ------------------------------
    /**
     * 主色容器背景（PrimaryContainer）：深色模式下的主色强调区域
     * 深灰（#333333）区分层级，避免与背景混淆
     */
    primaryContainer = Color(0xFF333333),
    /**
     * 主容器内的文本/图标颜色（OnPrimaryContainer）：与 PrimaryContainer 对比的颜色
     * 白色（#FFFFFF）确保容器内内容可读
     */
    onPrimaryContainer = Color(0xFFFFFFFF),

    // ------------------------------ 次要操作色（Secondary） ------------------------------
    /**
     * 次要操作色（Secondary）：深色模式下的次要操作色
     * 中灰（#9E9E9E）降低对比度，避免夜间视觉疲劳
     */
    secondary = Color(0xFF9E9E9E),
    /**
     * 次要色上的文本/图标颜色（OnSecondary）：与 Secondary 对比的颜色
     * 黑色（#000000）稳定且易读
     */
    onSecondary = Color(0xFF000000),

    // ------------------------------ 次要容器（Secondary Container） ------------------------------
    /**
     * 次要容器背景（SecondaryContainer）：深色模式下的次要内容容器
     * 深灰（#4D4D4D）区分次要内容与背景
     */
    secondaryContainer = Color(0xFF4D4D4D),
    /**
     * 次要容器内的文本/图标颜色（OnSecondaryContainer）：与 SecondaryContainer 对比的颜色
     * 白色（#FFFFFF）确保次要容器内内容清晰
     */
    onSecondaryContainer = Color(0xFFFFFFFF),

    // ------------------------------ 表面（Surface）系列 ------------------------------
    /**
     * 表面背景（Surface）：深色模式下的页面/卡片背景
     * 接近纯黑的深灰（#1E1E1E），避免纯黑（#000000）的屏幕反光
     */
    surface = Color(0xFF1E1E1E),
    /**
     * 表面上的文本/图标颜色（OnSurface）：深色模式下的主要内容颜色
     * 白色（#FFFFFF）是深色模式的标准阅读色
     */
    onSurface = Color(0xFFFFFFFF),
    /**
     * 表面变体（SurfaceVariant）：深色模式下的输入框/分割线背景
     * 深灰（#333333）柔和，避免刺眼
     */
    surfaceVariant = Color(0xFF333333),
    /**
     * 表面变体上的文本/图标颜色（OnSurfaceVariant）：表面变体中的内容
     * 白色（#FFFFFF）确保输入内容清晰
     */
    onSurfaceVariant = Color(0xFFFFFFFF),
    /**
     * 页面背景（Background）：整个页面的背景色（可覆盖 Surface）
     * 更深的灰（#121212）模拟“暗环境”，增强沉浸感
     */
    background = Color(0xFF121212),
    /**
     * 背景上的文本/图标颜色（OnBackground）：背景层的全局文本颜色
     * 白色（#FFFFFF）通用且易读
     */
    onBackground = Color(0xFFFFFFFF),

    // ------------------------------ 错误色（Error） ------------------------------
    /**
     * 错误提示色（Error）：保留 Material 标准红色（#B00020）
     * 深色模式下红色仍醒目，无需修改
     */
    error = Color(0xFFB00020),
    /**
     * 错误信息文本颜色（OnError）：与错误色对比的颜色
     * 白色（#FFFFFF）确保错误提示可读
     */
    onError = Color(0xFFFFFFFF)
)

@Composable
fun MyAppTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    // Dynamic color is available on Android 12+
    dynamicColor: Boolean = true,
    content: @Composable () -> Unit
) {
    val colorScheme = when {
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            val context = LocalContext.current
            if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        }
        darkTheme -> DarkColorScheme
        else -> LightColorScheme
    }
    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            (view.context as Activity).window.statusBarColor = colorScheme.primary.toArgb()
            ViewCompat.getWindowInsetsController(view)?.isAppearanceLightStatusBars = darkTheme
        }
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}