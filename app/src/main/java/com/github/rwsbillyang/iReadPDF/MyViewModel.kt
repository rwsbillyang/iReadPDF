package com.github.rwsbillyang.iReadPDF



import android.util.Log
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.geometry.Offset
import androidx.lifecycle.ViewModel
import com.github.rwsbillyang.iReadPDF.AppConstants.TAG
import com.github.rwsbillyang.iReadPDF.db.Book


class MyViewModel: ViewModel(){

    val isFullScreen = mutableStateOf(false)

    val currentBook = mutableStateOf<Book?>(null)
    val shelfList = mutableStateListOf<Book>()
    var shelfListLoaded = false

    //val pdfPageLoader = mutableStateOf<PdfPageLoader?>(null)
    //val lazyItemWidth = mutableStateOf(10)
    //val lazyItemHeight = mutableStateOf(10)

    //val scale = mutableFloatStateOf(1f)
    //val offset = mutableStateOf(Offset.Zero)
    //val rotation = mutableFloatStateOf(0f)





    fun updateLandScape(landscape: Int) {
        currentBook.value?.let { it.landscape = landscape }?:Log.w(TAG, "currentBook is null")
    }
    fun updateTransformState(zoom: Float, x: Float, y: Float) {
        //scale.value *= zoom
        //offset.value += Offset(x,y)

        currentBook.value?.let {
            it.zoom *= zoom
            it.offsetX += x
            it.offsetY += y
        }?:Log.w(TAG, "currentBook is null")
    }

}