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


private val LightColor = lightColorScheme(
    primary = Color(0xFF333333),//主字体色  顶部状态栏色调、settings中checkbox框框的groupName字体色
    onPrimary = Color(0xFFFFFFFF),//主背景色

    secondary =  Color(0xFF464646),//次字体色
    onSecondary = Color(0xFFD5D5D5),//次背景色

    background = Color(0xFFF0EAE2),//正文区的背景使用此色
    onBackground = Color(0xFF655454),//正文区文字色，如settings中的选项文字颜色

    surface = Color.White.copy(alpha = .85f),//TopAppBar中的背景将使用此色
    onSurface = Color(0xFF333333).copy(0.8f)//TopAppBar 中的title和navIcon将使用此颜色
)

private val DarkColor = darkColorScheme(
    primary = Color(0xFFFFFFFF), //主字体色 顶部状态栏色调、settings中checkbox框框的groupName字体色
    onPrimary = Color(0xFF140a2d),//主背景色

    secondary = Color(0xFFB4B4B4),//次字体色
    onSecondary = Color(0xFF5A5A5A),//次背景色

    background = Color(0xFF0F0F0F),//正文区的背景使用此色
    onBackground = Color(0xFFF0F0F0),//正文区文字色，如settings中的选项文字颜色

    surface = Color(0xFF070707), //TopAppBar中的背景将使用此色
    onSurface = Color(0xFFF9F9F9)//TopAppBar 中的title和navIcon将使用此颜色
)

//https://juejin.cn/post/6939506468475371557
private val LightColorPalette = lightColorScheme(
    primary = pink100,
    //primaryVariant = purple700,
    secondary = pink900,
    background = white,
    surface = whit850,
    onPrimary = gray,
    onSecondary = white,
    onBackground = gray,
    onSurface = gray,
)
private val DarkColorPalette = darkColorScheme(
    primary = green900,
    //primaryVariant = purple700,
    secondary = green300,
    background = gray,
    surface = whit150,
    onPrimary = white,
    onSecondary = gray,
    onBackground = white,
    onSurface = whit850,
)



val GreenDarkColor = darkColorScheme(
    primary = Color(0xFF1EB980),
    surface = Color(0xFF26282F),
    onSurface = Color.White,
    background = Color(0xFF26282F),
    onBackground = Color.White
)


private val PurpleDarkColor = darkColorScheme(
    primary = Color(0xFFD0BCFF),
    secondary = Color(0xFFCCC2DC),
    tertiary = Color(0xFFEFB8C8)
)

private val PurpleLightColor = lightColorScheme(
    primary = Color(0xFF6650a4),
    secondary = Color(0xFF625b71),
    tertiary = Color(0xFF7D5260),

    ///* Other default colors to override
    background = Color(0xFFFFFBFE),
    surface = Color(0xFFFFFBFE),
    onPrimary = Color.White,
    onSecondary = Color.White,
    onTertiary = Color.White,
    onBackground = Color(0xFF1C1B1F),
    onSurface = Color(0xFF1C1B1F),

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
        darkTheme -> DarkColor
        else -> LightColor
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