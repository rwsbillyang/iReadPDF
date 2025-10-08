package com.github.rwsbillyang.iReadPDF


import com.github.rwsbillyang.composerouter.ScaffoldScreen3
import com.github.rwsbillyang.composerouter.route




fun getAppRoutes() = listOf(
    route("Home", R.string.app_name,"/",  true, true){ Waiting() },

    route(
        AppConstants.AppRoutes.BookShelf, R.string.bookshelf, screen = ScaffoldScreen3(
        { ScreenBookShelf(it) },
        {
            BookShelfToolIcons()
        })
    ),

    route(AppConstants.AppRoutes.PDFViewer, R.string.app_name, useNavScaffold = false){ScreenPdfViewer(it)},

    route(AppConstants.AppRoutes.Settings, R.string.settings){ SettingsScreen(it.scaffoldPadding) },
    route(AppConstants.AppRoutes.BookSettings, R.string.book_settings){ ScreenBookSettings(it) },
)