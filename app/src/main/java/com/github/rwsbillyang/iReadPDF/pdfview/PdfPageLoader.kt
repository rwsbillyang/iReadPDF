package com.github.rwsbillyang.iReadPDF.pdfview

import android.content.Context
import android.graphics.Bitmap
import android.graphics.pdf.PdfRenderer
import android.os.Build
import android.os.ParcelFileDescriptor
import android.util.Log
import android.util.Size
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.async
import kotlinx.coroutines.cancelChildren
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileOutputStream
import java.util.concurrent.ConcurrentHashMap

class PdfPageLoader(
    private val pdfRenderer: PdfRenderer,
    private val fd: ParcelFileDescriptor,
    private val cacheManager: CacheManager
) {
    private val scope = CoroutineScope(Dispatchers.IO + SupervisorJob())
    private val renderLock = Mutex()
    private val renderJobs = ConcurrentHashMap<Int, Deferred<Bitmap?>>()

    var pageCount = 0
    var pageSize = Size(1,1)
    private var currentPage: PdfRenderer.Page? = null

    companion object{
        fun create(fd: ParcelFileDescriptor, fileId: String, ctx: Context, cacheStrategy: CacheStrategy):PdfPageLoader{
            return PdfPageLoader(PdfRenderer(fd), fd, CacheManager(ctx, fileId, cacheStrategy))
        }


        /**
         * @param pdfFile: files/_pdf_/$md5.pdf
         * */
        fun create(pdfFile: File, fileId: String, ctx: Context, cacheStrategy: CacheStrategy):PdfPageLoader?{
            if(!pdfFile.exists()){
                return null
            }
            Log.d(TAG, "to open pdf: ${pdfFile.absolutePath}")
            val fd = ParcelFileDescriptor.open(pdfFile, ParcelFileDescriptor.MODE_READ_ONLY)
            return create(fd, fileId, ctx, cacheStrategy)
        }

        /**
         * @param fileId: book.id
         * @param height cover height
         * @return  absolute path of cover of pdfFile
         * TODO: not consider the conflict with PdfPageLoader instance
         * */
        suspend fun loadFirstPageAsCover(fileId: String, ctx: Context,  height: Int) = withContext(Dispatchers.IO){
            val pdfFile = CacheManager.defaultPdfFile(ctx, fileId)
            try{
                val fd = ParcelFileDescriptor.open(pdfFile, ParcelFileDescriptor.MODE_READ_ONLY)
                val pdfPage = PdfRenderer(fd).openPage(0)
                val width = height * pdfPage.width / pdfPage.height
                Log.d(TAG, "cover height=$height, width=$width")
                val renderedBitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
                pdfPage.render(
                    renderedBitmap,
                    null,
                    null,
                    PdfRenderer.Page.RENDER_MODE_FOR_DISPLAY
                )
                val cover = CacheManager.defaultCover(ctx, fileId)
                FileOutputStream(cover).use { fos ->
                    if(Build.VERSION.SDK_INT > 29)
                        renderedBitmap.compress(Bitmap.CompressFormat.WEBP_LOSSY, 50, fos)
                    else
                        renderedBitmap.compress(Bitmap.CompressFormat.PNG, 50, fos)
                }
                pdfPage.close()
                fd.close()
                cover
            }catch (e: Exception){
                e.printStackTrace()
                null
            }
        }
    }

    fun preload() {
        pageCount = pdfRenderer.pageCount
        preloadPageDimension()
    }

    private fun preloadPageDimension() {
        Log.d(TAG, "preloadPageDimension")
        val page = pdfRenderer.openPage(0)
        pageSize = Size(page.width, page.height)
        page.close()
    }

    suspend fun loadPage(pageNo: Int, quality: Float, darkThemeEnabled: Boolean) = withContext(Dispatchers.IO) {
        if (pageNo < 0 || pageNo >= pageCount) {
            Log.w(TAG, "Skipped invalid render for page $pageNo")
            null
        }else{
            val cachedBitmap = cacheManager.getBitmapFromCache(pageNo)

            val b = if (cachedBitmap != null) {
                Log.d(TAG, "Page $pageNo loaded from cache")
                cachedBitmap
            }else{
                if (renderJobs[pageNo]?.isActive == true){
                    renderJobs[pageNo]?.await()
                }else{
                    renderJobs[pageNo]?.cancel()
                    val d = doLoadAndRender(pageNo, quality)
                    renderJobs[pageNo] = d
                    d.await()
                }
            }

            if(darkThemeEnabled) invert(b) else b
        }
    }


    /**
     * quality: (0, 1.0] 渲染质量
     * */
    private fun doLoadAndRender(page: Int, quality: Float):Deferred<Bitmap?>  {
        return scope.async {
            //这里，直接t渲染pdfPage的width和heigh大小的renderedBitmap，在Compose的Image中，使用ContentScale.Fit进行缩放适配
//            val aspectRatio = pageSize.width.toFloat() / pageSize.height.toFloat()
//            val height = (lazyItemWidth / aspectRatio).toInt()

            renderLock.withLock {
                Log.d(TAG, "openPage $page")
                currentPage = pdfRenderer.openPage(page)
                currentPage?.let {pdfPage ->
                    Log.d(TAG, "pdfRenderer.openPage $page: pdfPage.width=${pdfPage.width}, pdfPage.height=${pdfPage.height}, quality=$quality")
                    val renderedBitmap = BitmapPool.getBitmap((pdfPage.width * quality).toInt(), (pdfPage.height * quality).toInt())
                    pdfPage.render(
                        renderedBitmap,
                        null,
                        null,
                        PdfRenderer.Page.RENDER_MODE_FOR_DISPLAY
                    )
                    pdfPage.close()
                    currentPage = null
                    cacheManager.addBitmapToCache(page, renderedBitmap)
                    renderedBitmap
                }

            }
        }
    }

    fun closePdfRender() {
        Log.d(TAG, "Closing PdfRenderer and releasing resources.")

        scope.coroutineContext.cancelChildren()
        currentPage?.close()
        currentPage = null

        runCatching { pdfRenderer.close() }
            .onFailure { Log.e(TAG, "Error closing PdfRenderer: ${it.message}", it) }

        runCatching { fd.close() }
            .onFailure { Log.e(TAG, "Error closing file descriptor: ${it.message}", it) }

    }

    private fun invert(original: Bitmap?): Bitmap? {
        if(original == null) return null
        val inversion = original.copy(Bitmap.Config.ARGB_8888, true)

        val width = inversion.width
        val height = inversion.height
        val pixels = width * height

        val pixel = IntArray(pixels)
        inversion.getPixels(pixel, 0, width, 0, 0, width, height)

        for (i in 0 until pixels) pixel[i] = pixel[i] xor 0x00FFFFFF
        inversion.setPixels(pixel, 0, width, 0, 0, width, height)

        return inversion
    }



    //lazyColumn已支持pre render从而提前加载
//    private var prefetchJob: Job? = null
//    fun prefetch(currentPage: Int, quality: Float,  direction: Int) {
//        prefetchJob?.cancel()
//        prefetchJob = scope.launch {
//            delay(100)
//            prefetchPagesAround(currentPage, quality,  direction)
//        }
//    }
//    private suspend fun prefetchPagesAround(currentPage: Int, quality: Float,  direction: Int) {
//        val prefetchDistance = CacheManager.PreFetchDistance
//        val range = when (direction) {
//            1 -> (currentPage + 1)..(currentPage + prefetchDistance)
//            -1 -> (currentPage - prefetchDistance)..<currentPage
//            else -> (currentPage - prefetchDistance)..(currentPage + prefetchDistance)
//        }
//        val sortedPages = range
//            .filter { it in 0 until pageCount }
//            .filter { !cacheManager.pageExistsInCache(it) }
//            .sortedBy { abs(it - currentPage) } // prefer pages close to current page
//
//        sortedPages.forEach { pageNo ->
//            if (renderJobs[pageNo]?.isActive != true) {
//                renderJobs[pageNo]?.cancel()
//                renderJobs[pageNo] = doLoadAndRender(pageNo, quality)
//            }
//        }
//    }
}