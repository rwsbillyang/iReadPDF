package com.github.rwsbillyang.iReadPDF.pdfview

import androidx.compose.ui.geometry.Offset


interface StatusCallBack {
    fun onPdfLoadStart(displayName: String? ,fileId: String?) {}
    fun onPdfLoadSuccess(displayName: String? ,fileId: String?, totalPage: Int) {}
    fun onError(error: String) {}
    //fun onPdfLoadProgress(progress: Int, downloadedBytes: Long, totalBytes: Long?) {}

//    fun onPdfRenderStart() {}
//    fun onPdfRenderSuccess() {}


    fun onPageChanged(currentPage: Int) {}

    fun onTransformStateChanged(zoomChange: Float, offsetChange: Offset, rotationChange: Float){}
}
