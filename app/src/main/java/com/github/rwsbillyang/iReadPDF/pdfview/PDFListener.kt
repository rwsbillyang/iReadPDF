package com.github.rwsbillyang.iReadPDF.pdfview


interface StatusCallBack {
    fun onPdfLoadStart(displayName: String? ,fileId: String?) {}
    fun onPdfLoadSuccess(displayName: String? ,fileId: String?) {}
    fun onError(error: String) {}
    //fun onPdfLoadProgress(progress: Int, downloadedBytes: Long, totalBytes: Long?) {}

//    fun onPdfRenderStart() {}
//    fun onPdfRenderSuccess() {}


    fun onPageChanged(currentPage: Int, totalPage: Int) {}
}

interface ZoomListener {
    fun onZoomChanged(isZoomedIn: Boolean, scale: Float)
}