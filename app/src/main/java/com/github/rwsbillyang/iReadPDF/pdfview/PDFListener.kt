package com.github.rwsbillyang.iReadPDF.pdfview

import androidx.compose.ui.geometry.Offset


interface StatusCallBack {
    fun onPageChanged(currentPage: Int) {}

    fun onTransformStateChanged(zoomChange: Float, offsetChange: Offset, rotationChange: Float){}
}
