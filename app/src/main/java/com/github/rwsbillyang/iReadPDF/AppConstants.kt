package com.github.rwsbillyang.iReadPDF


import android.util.Log
import java.time.format.DateTimeFormatter

fun log(str: String) {
    if (AppConstants.DebugRender) Log.d(AppConstants.TAG, str)
}

object AppConstants {
    const val DebugRender = true
    const val TAG = "MyAPP"

    const val KEY_CURRENT = "current"

    object AppRoutes {
        const val BookShelf = "BookShelf"
        const val PDFViewer = "PDFViewer"
        const val Settings = "Settings"
        const val BookSettings = "BookSettings"
    }

    object SettingsKey {
        const val EnterBookDirectly = "EnterBookDirectly"
        const val DisableMovePdf = "DisableMovePdf"
    }
    object BookSettingsKey {
        const val Cover = "Cover"
        const val Quality = "Quality"
        const val DarkMode = "DarkMode"
        const val Name = "Name"
        const val Author = "Author"
        const val Publisher = "Publisher"

    }

    val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")
}
