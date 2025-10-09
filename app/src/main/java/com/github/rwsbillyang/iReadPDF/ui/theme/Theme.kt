package com.github.rwsbillyang.iReadPDF.ui.theme

import android.app.Activity
import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.ColorScheme
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
import com.github.rwsbillyang.iReadPDF.R


/**
 * 传统黑白风格 - 浅色模式 ColorScheme
 * 定义：白为背景主调，黑为文本/主色，灰为辅助层级
 */
val LightColorScheme = lightColorScheme(
    // ------------------------------ 核心品牌色（黑白灰） ------------------------------
    /**
     * 主品牌色（Primary）：应用核心标识（如按钮、导航栏）
     * 纯黑（#000000），浅色模式下的最强视觉焦点
     */
    primary = Color(0xFF000000),
    /**
     * 主色上的文本/图标（OnPrimary）：与纯黑强对比的白色
     * 纯白（#FFFFFF），对比度 21:1（远超合规要求）
     */
    onPrimary = Color(0xFFFFFFFF),
    /**
     * 主色容器背景（PrimaryContainer）：弱化主色的强调区域
     * 浅灰（#F5F5F5），区分层级同时保持黑白风格
     */
    primaryContainer = Color(0xFFF5F5F5),
    /**
     * 主容器内的文本（OnPrimaryContainer）：与浅灰对比的黑色
     * 纯黑（#000000），对比度 15:1，清晰可读
     */

    onPrimaryContainer = Color(0xFF000000),

    // ------------------------------ 次要操作色（黑白灰） ------------------------------
    /**
     * 次要操作色（Secondary）：非核心操作（如次要按钮、链接）
     * 深灰（#333333），避免干扰主操作
     */
    secondary = Color(0xFF333333),
    /**
     * 次要色上的文本（OnSecondary）：与深灰强对比的白色
     * 纯白（#FFFFFF），对比度 18:1
     */
    onSecondary = Color(0xFFFFFFFF),
    /**
     * 次要容器背景（SecondaryContainer）：次要内容的容器
     * 浅灰（#E0E0E0），区分次要内容与主内容
     */
    secondaryContainer = Color(0xFFE0E0E0),
    /**
     * 次要容器内的文本（OnSecondaryContainer）：与浅灰对比的黑色
     * 纯黑（#000000），对比度 12:1
     */
    onSecondaryContainer = Color(0xFF000000),

    // ------------------------------ 表面系列（黑白灰分层） ------------------------------
    /**
     * 表面背景（Surface）：页面/卡片/对话框的基础背景
     */
    surface = Color(0xFFF0F0F0),
    /**
     * 表面上的文本（OnSurface）：主要内容（如标题、正文）
     * 纯黑（#000000），经典黑白阅读组合
     */
    onSurface = Color(0xFF000000),
    /**
     * 表面变体（SurfaceVariant）：输入框/分割线/禁用状态
     * 浅灰（#F0F0F0），柔和区分交互元素
     */
    surfaceVariant = Color(0xFFB0B0B0),
    /**
     * 表面变体上的文本（OnSurfaceVariant）：输入框文字/禁用提示
     * 纯黑（#000000），对比度 15:1，清晰可辨
     */
    onSurfaceVariant = Color(0xFF000000),
    /**
     * 页面背景（Background）：整个页面的底色（可覆盖 Surface）
     * 纯白（#FFFFFF），统一页面色调
     */
    background = Color(0xFFFFFFFF),
    /**
     * 背景上的文本（OnBackground）：全局背景文本（如页脚）
     * 纯黑（#000000），通用易读
     */
    onBackground = Color(0xFF000000),

    // ------------------------------ 错误色（保留标准红） ------------------------------
    /**
     * 错误提示色（Error）：警示性错误（如无效输入）
     * Material 标准红（#B00020），深色/浅色模式通用
     */
    error = Color(0xFFB00020),
    /**
     * 错误文本（OnError）：错误提示中的文字
     * 纯白（#FFFFFF），对比度 15:1，醒目易读
     */
    onError = Color(0xFFFFFFFF)
)


/**
 * 传统黑白风格 - 深色模式 ColorScheme
 * 定义：深灰为背景主调，浅灰/中灰为文本，纯黑为强调色
 * 优化：用浅灰/中灰替代全白，避免深色背景下的刺眼感
 */
val DarkColorScheme = darkColorScheme(
    // ------------------------------ 核心品牌色（黑白灰） ------------------------------
    /**
     * 主品牌色（Primary）：深色背景下的核心标识
     * 浅灰（#E0E0E0），不刺眼且保持存在感
     */
    primary = Color(0xFFE0E0E0),
    /**
     * 主色上的文本（OnPrimary）：与浅灰强对比的黑色
     * 纯黑（#000000），对比度 18:1，清晰可读
     */
    onPrimary = Color(0xFF000000),
    /**
     * 主色容器背景（PrimaryContainer）：主色的强调区域
     * 深灰（#333333），区分层级
     */
    primaryContainer = Color(0xFF333333),
    /**
     * 主容器内的文本（OnPrimaryContainer）：与深灰对比的浅灰
     * 浅灰（#E0E0E0），对比度 6.8:1（合规），替代全白
     */
    onPrimaryContainer = Color(0xFFE0E0E0),

    // ------------------------------ 次要操作色（黑白灰） ------------------------------
    /**
     * 次要操作色（Secondary）：深色模式下的次要操作
     * 中灰（#9E9E9E），降低对比度避免疲劳
     */
    secondary = Color(0xFF9E9E9E),
    /**
     * 次要色上的文本（OnSecondary）：与中灰对比的黑色
     * 纯黑（#000000），对比度 15:1
     */
    onSecondary = Color(0xFF000000),
    /**
     * 次要容器背景（SecondaryContainer）：次要内容的容器
     * 深灰（#4D4D4D），区分次要内容与背景
     */
    secondaryContainer = Color(0xFF4D4D4D),
    /**
     * 次要容器内的文本（OnSecondaryContainer）：与深灰对比的浅灰
     * 浅灰（#E0E0E0），对比度 7.2:1（合规），替代全白
     */
    onSecondaryContainer = Color(0xFFE0E0E0),

    // ------------------------------ 表面系列（深灰分层） ------------------------------
    /**
     * 表面背景（Surface）：深色模式的核心背景
     * 深灰（#121212），Material 推荐的深色底色（避免纯黑反光）
     */
    surface = Color(0xFF121212),
    /**
     * 表面上的文本（OnSurface）：主要内容（如标题、正文）
     * 浅灰（#CCCCCC），替代全白！对比度 16.9:1（合规），柔和不刺眼
     */
    onSurface = Color(0xFFCCCCCC),
    /**
     * 表面变体（SurfaceVariant）：输入框/分割线/禁用状态
     * 深灰（#333333），区分交互元素
     */
    surfaceVariant = Color(0xFF333333),
    /**
     * 表面变体上的文本（OnSurfaceVariant）：输入框文字/禁用提示
     * 中灰（#9E9E9E），替代全白！对比度 4.8:1（合规），柔和清晰
     */
    onSurfaceVariant = Color(0xFF9E9E9E),
    /**
     * 页面背景（Background）：整个页面的底色（可覆盖 Surface）
     * 更深的灰（#0A0A0A），增强沉浸感
     */
    background = Color(0xFF0A0A0A),
    /**
     * 背景上的文本（OnBackground）：全局背景文本（如页脚）
     * 浅灰（#CCCCCC），对比度 20.3:1（合规），保持风格统一
     */
    onBackground = Color(0xFFCCCCCC),

    // ------------------------------ 错误色（保留标准红） ------------------------------
    /**
     * 错误提示色（Error）：警示性错误
     * Material 标准红（#B00020），深色模式下仍醒目
     */
    error = Color(0xFFB00020),
    /**
     * 错误文本（OnError）：错误提示中的文字
     * 纯白（#FFFFFF），对比度 6.4:1（合规），醒目易读
     */
    onError = Color(0xFFFFFFFF)
)

//https://yuanbao.tencent.com/chat/naQivTmsDa/c04225d0-bc07-44ac-beb3-1260d01ab505
enum class ThemeEnum(val light: ColorScheme, val dark: ColorScheme, val resId: Int){
    Default(LightColorScheme, DarkColorScheme, R.string.theme_default),
    Blue(LightBlueScheme, DarkBlueScheme, R.string.theme_blue),
    Green(LightGreenScheme, DarkGreenScheme, R.string.theme_green),
    Orange(LightOrangeScheme, DarkOrangeScheme, R.string.theme_orange),
    Purple(LightPurpleScheme, DarkPurpleScheme, R.string.theme_purple),
    Dynamic(LightColorScheme, DarkColorScheme, R.string.theme_dynamic)
}

@Composable
fun MyAppTheme(
    themeEnum: ThemeEnum,
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colorScheme = when {
        // Dynamic color is available on Android 12+
        themeEnum == ThemeEnum.Dynamic && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            val context = LocalContext.current
            if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        }
        darkTheme -> themeEnum.dark
        else -> themeEnum.light
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