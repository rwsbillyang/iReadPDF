package com.github.rwsbillyang.iReadPDF



import android.content.Context
import android.util.Log
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import com.github.rwsbillyang.iReadPDF.db.Book
import com.github.rwsbillyang.iReadPDF.pdfview.CacheStrategy
import com.github.rwsbillyang.iReadPDF.pdfview.LocalUri
import com.github.rwsbillyang.iReadPDF.pdfview.PdfPageLoader
import com.github.rwsbillyang.iReadPDF.pdfview.TAG


class MyViewModel: ViewModel(){
    val isFullScreen = mutableStateOf(false)

    val shelfList = mutableStateListOf<Book>()
    var isEditingShelf = mutableStateOf(false)
    var shelfListLoaded = false

    private val _currentBook = mutableStateOf<Book?>(null)
    val currentBook: Book?
        get() = _currentBook.value


    private val _loadingPdf = mutableStateOf(true)
    val loadingPdf:Boolean
        get() = _loadingPdf.value

    private val _pdfPageLoader = mutableStateOf<PdfPageLoader?>(null)
    val pdfPageLoader: PdfPageLoader?
        get() = _pdfPageLoader.value


    private fun updateCurrentBook(b: Book){
        b.lastOpen = System.currentTimeMillis()
        _currentBook.value = b
    }

    suspend fun onBookMaybeChanged(book: Book, ctx: Context){
        if(book.id != _currentBook.value?.id){
            _pdfPageLoader.value?.apply {
                Log.d(TAG, "reset pdfPageLoader")
                releasePdfLoader()
            }
            //临时打开的pdf不缓存其页面
            val cacheStrategy = if(book.cachePages) CacheStrategy.MAXIMIZE_PERFORMANCE else CacheStrategy.DISABLE_CACHE
            if(book.uri != null){//通常为空
                val source = LocalUri.create(book.uri!!, ctx)
                if(source.fd != null ||  source.fileId != null){
                    _pdfPageLoader.value = PdfPageLoader.create(source.fd!!, source.fileId!!, ctx, cacheStrategy).apply { preload() }
                }
            }else{
                PdfPageLoader.create(book.pdfFile(ctx), book.id,  ctx, cacheStrategy)?.apply {
                    preload()
                    _pdfPageLoader.value = this
                }
            }
            _loadingPdf.value = false
        }else
            Log.d(TAG, "not need to re-create pdfPageLoader")
        updateCurrentBook(book)
    }
    fun updateLandScape(landscape: Int) {
        _currentBook.value?.let { it.landscape = landscape }?:Log.w(TAG, "currentBook is null")
    }
    fun updateTransformState(zoom: Float, x: Float, y: Float) {
        //scale.value *= zoom
        //offset.value += Offset(x,y)

        _currentBook.value?.let {
            it.zoom *= zoom
            it.offsetX += x
            it.offsetY += y
        }?:Log.w(TAG, "currentBook is null")
    }

    fun releasePdfLoader(){
        pdfPageLoader?.closePdfRender()
        _pdfPageLoader.value = null
    }

    fun releaseShelfList(){
        shelfList.clear()
        shelfListLoaded = false
    }

    //val lazyItemWidth = mutableStateOf(10)
    //val lazyItemHeight = mutableStateOf(10)

    //val scale = mutableFloatStateOf(1f)
    //val offset = mutableStateOf(Offset.Zero)
    //val rotation = mutableFloatStateOf(0f)


}