package com.github.rwsbillyang.iReadPDF


import androidx.compose.foundation.layout.RowScope
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Settings
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import com.github.rwsbillyang.composerouter.ScaffoldScreen3
import com.github.rwsbillyang.composerouter.route
import com.github.rwsbillyang.composerouter.useRouter


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
//    route(AppRoutes.BookShelf, R.string.bookshelf){  ScreenBookShelf(it) },

    route(AppRoutes.PDFViewer, R.string.app_name, screen = ScaffoldScreen3(
        { ScreenPdfViewer(it) }, {PdfViewerToolIcons()})),

//    route(AppRoutes.PDFViewer, R.string.app_name){ ScreenPdfViewer(it) },

    route(AppRoutes.Settings, R.string.settings){ SettingsScreen(it.scaffoldPadding) },
)