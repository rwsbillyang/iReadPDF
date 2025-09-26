package com.github.rwsbillyang.iReadPDF



import android.util.Log
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel

import com.github.rwsbillyang.iReadPDF.AppConstants.TAG
import com.github.rwsbillyang.iReadPDF.db.Book


class MyViewModel: ViewModel(){
    //val pdfView = mutableStateOf<PDFView?>(null)
    val isLoadingFile = mutableStateOf(true)
    val isFullScreen = mutableStateOf(true)

    val currentBook = mutableStateOf<Book?>(null)
    val shelfList = mutableStateListOf<Book>()

    fun updateTotalPages(pages: Int) {
        currentBook.value?.let { it.total = pages }?:Log.w(TAG, "currentBook is null")
    }
    fun updatePage(page: Int) {
        currentBook.value?.let { it.page = page }?:Log.w(TAG, "currentBook is null")
    }
    fun updateZoom(zoom: Float) {
        currentBook.value?.let { it.zoom = zoom }?:Log.w(TAG, "currentBook is null")
    }
    fun updateLandScape(landscape: Int) {
        currentBook.value?.let { it.landscape = landscape }?:Log.w(TAG, "currentBook is null")
    }
    fun updateScroll(scrollX: Int?, scrollY: Int?) {
        currentBook.value?.let {
            if(scrollX != null)it.scrollX = scrollX
            if(scrollY != null)it.scrollY = scrollY
        }?:Log.w(TAG, "currentBook is null")
    }

//    private val _currentPage = mutableStateOf(0)
//    val currentPage: Int get() = _currentPage.value
//
//    private val _totalPages = mutableStateOf(0)
//    val totalPages: Int get() = _totalPages.value
//    fun updateTotalPages(pages: Int) {
//        _totalPages.value = pages
//    }
//    private val _zoomLevel = mutableStateOf(1f)
//    val zoomLevel: Float get() = _zoomLevel.value
//
//    private val _scrollPosition = mutableStateOf(Pair(0, 0))
//    val scrollPosition: Pair<Int, Int> get() = _scrollPosition.value
//
//    private val _currentFilePath = mutableStateOf("")
//    val currentFilePath: String get() = _currentFilePath.value
//
//    private val _isLandscape = mutableStateOf(false)
//    val isLandscape: Boolean get() = _isLandscape.value

//    fun setCurrentFile(filePath: String) {
//        _currentFilePath.value = filePath
//        restoreProgress()
//    }

}