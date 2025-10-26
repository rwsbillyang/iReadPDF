package com.github.rwsbillyang.iReadPDF



import android.content.Context
import android.util.Log
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import com.github.rwsbillyang.iReadPDF.db.Book
import com.github.rwsbillyang.iReadPDF.db.MyDao
import com.github.rwsbillyang.iReadPDF.pdfview.CacheStrategy
import com.github.rwsbillyang.iReadPDF.pdfview.LocalUri
import com.github.rwsbillyang.iReadPDF.pdfview.PdfPageLoader
import com.github.rwsbillyang.iReadPDF.pdfview.TAG
import com.github.rwsbillyang.iReadPDF.ui.theme.ThemeEnum


class MyViewModel: ViewModel(){
    var isFromConfigurationsChanged = false
    val shelfList = mutableStateListOf<Book>()
    var isEditingShelf = mutableStateOf(false)
    var shelfListLoaded = false

    private val _currentBook = mutableStateOf<Book?>(null)
    val currentBook: Book?
        get() = _currentBook.value


    private val _pdfPageLoader = mutableStateOf<PdfPageLoader?>(null)
    val pdfPageLoader: PdfPageLoader?
        get() = _pdfPageLoader.value


    suspend fun updateCurrentBook(dao: MyDao, book: Book, ctx: Context){
        //下面的执行必须串行化，先释放旧的再创建新的
        if(book.id != _currentBook.value?.id){
            _currentBook.value?.let{dao.updateOne(it)}//切换了book，需要保存原有的
            _pdfPageLoader.value?.apply {
                Log.d(TAG, "reset pdfPageLoader")
                releasePdfLoader()
            }

            val cacheStrategy = if(book.cachePages) CacheStrategy.MAXIMIZE_PERFORMANCE else CacheStrategy.DISABLE_CACHE
            if(book.uri != null){//通常为空 //临时打开的pdf不缓存其页面
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
        }else
            Log.d(TAG, "not need to re-create pdfPageLoader")


        book.lastOpen = System.currentTimeMillis()
        _currentBook.value = book
    }

    fun updateTransformState(zoom: Float, x: Float, y: Float) {
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


    //settings
    var enterBookDirectly = false
    //var disableMovePdf = true
    var theme = mutableStateOf(ThemeEnum.Default)
    var screenOn = mutableStateOf(0) //minutes of keeping screen on
}