package com.github.rwsbillyang.iReadPDF


import com.github.rwsbillyang.composerouter.ScaffoldScreen3
import com.github.rwsbillyang.composerouter.route


object AppRoutes {
    const val BookShelf = "BookShelf"
    const val PDFViewer = "PDFViewer"
    const val Settings = "Settings"
}

fun getAppRoutes() = listOf(
    route("Home", R.string.app_name,"/",  true, true){ Waiting() },

    route(AppRoutes.BookShelf, R.string.bookshelf, screen = ScaffoldScreen3(
        { ScreenBookShelf(it) },
        {
            BookShelfToolIcons()
        })
    ),

    route(AppRoutes.PDFViewer, R.string.app_name, screen = ScaffoldScreen3(
        { ScreenPdfViewer(it) }, {PdfViewerToolIcons()})),


    route(AppRoutes.Settings, R.string.settings){ SettingsScreen(it.scaffoldPadding) },
)