package com.github.rwsbillyang.pdfview

import android.net.Uri
import java.io.File

sealed interface PdfSource {
    //data class Remote(val url: String) : PdfSource
    data class LocalFile(val file: File) : PdfSource
    data class LocalUri(val uri: Uri) : PdfSource
    data class PdfSourceFromAsset(val assetFileName: String) : PdfSource
}