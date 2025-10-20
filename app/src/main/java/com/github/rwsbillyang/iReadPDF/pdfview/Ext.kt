package com.github.rwsbillyang.iReadPDF.pdfview

import android.app.Activity
import android.content.pm.ActivityInfo
import android.view.Window
import android.view.WindowManager
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat

fun Window.setFullScreen(enabled: Boolean){
    if(enabled){
        // 进入全屏：隐藏系统栏（状态栏+导航栏）
        WindowCompat.setDecorFitsSystemWindows(this, false) // 让内容延伸到系统栏区域
        WindowCompat.getInsetsController(this, this.decorView).apply {
            hide(WindowInsetsCompat.Type.systemBars()) // 隐藏系统栏
            //systemBarsBehavior = WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE // 滑动边缘唤出系统栏
        }
    }else{
        // 退出全屏：显示系统栏
        WindowCompat.setDecorFitsSystemWindows(this, true) // 内容不延伸到系统栏
        WindowCompat.getInsetsController(this, this.decorView).apply {
            show(WindowInsetsCompat.Type.systemBars()) // 显示系统栏
        }
    }
}
fun Window.setScreenOn(state: Boolean) = if(state) addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON) else clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)

fun Activity.setLandscape(landscape: Int){
    requestedOrientation = if(landscape == 1) ActivityInfo.SCREEN_ORIENTATION_USER_LANDSCAPE else ActivityInfo.SCREEN_ORIENTATION_USER_PORTRAIT
}