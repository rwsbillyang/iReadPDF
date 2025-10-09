package com.github.rwsbillyang.iReadPDF.pdfview

import androidx.compose.ui.geometry.Offset


interface StatusCallBack {
    fun onPageChanged(currentPage: Int, pageOffset: Int) {}
    fun onTotalPages(total: Int) {}
    fun onTransformStateChanged(zoomChange: Float,  moveX: Float, moveY: Float, rotationChange: Float){}
}
