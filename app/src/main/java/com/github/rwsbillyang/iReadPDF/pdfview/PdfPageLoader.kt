package com.github.rwsbillyang.iReadPDF.pdfview

import android.content.Context
import android.graphics.Bitmap
import android.graphics.pdf.PdfRenderer
import android.os.ParcelFileDescriptor
import android.util.Log
import android.util.Size
import com.github.rwsbillyang.iReadPDF.AppConstants
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.async
import kotlinx.coroutines.cancelChildren
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlinx.coroutines.withContext
import java.util.concurrent.ConcurrentHashMap
import kotlin.math.abs

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

    companion object{
        suspend fun create(fd: ParcelFileDescriptor, fileId: String, ctx: Context, cacheStrategy: CacheStrategy) =
            PdfPageLoader(PdfRenderer(fd), fd, CacheManager(ctx.cacheDir.absolutePath, fileId, cacheStrategy).apply {
                initialize()
                Log.d(AppConstants.TAG, "create pdfPageLoader done")
            })
    }

    init {
        pageCount = pdfRenderer.pageCount
        preloadPageDimension()
    }

    private fun preloadPageDimension() {
        val page = pdfRenderer.openPage(0)
        pageSize = Size(page.width, page.height)
        Log.d(TAG, "preloadPageDimension: page.width=${page.width}, page.height=${page.height}")
        page.close()
    }

    suspend fun loadPage( pageNo: Int,  quality: Float) = withContext(Dispatchers.IO) {
        if (pageNo < 0 || pageNo >= pageCount) {
            Log.w(TAG, "Skipped invalid render for page $pageNo")
            null
        }else{
            val cachedBitmap = cacheManager.getBitmapFromCache(pageNo)
            if (cachedBitmap != null) {
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
                //val renderedBitmap = BitmapPool.getBitmap(lazyItemWidth, maxOf(10, height))
                val pdfPage = pdfRenderer.openPage(page)
                Log.d(TAG, "pdfRenderer.openPage $page: pdfPage.width=${pdfPage.width}, pdfPage.height=${pdfPage.height}")
                val renderedBitmap = BitmapPool.getBitmap((pdfPage.width * quality).toInt(), (pdfPage.height * quality).toInt())
                pdfPage.render(
                    renderedBitmap,
                    null,
                    null,
                    PdfRenderer.Page.RENDER_MODE_FOR_DISPLAY
                )
                pdfPage.close()
                cacheManager.addBitmapToCache(page, renderedBitmap)
                renderedBitmap
            }
        }
    }

    fun closePdfRender() {
        Log.d(TAG, "Closing PdfRenderer and releasing resources.")

        scope.coroutineContext.cancelChildren()
        //closeAllOpenPages()

        runCatching { pdfRenderer.close() }
            .onFailure { Log.e(TAG, "Error closing PdfRenderer: ${it.message}", it) }

        runCatching { fd.close() }
            .onFailure { Log.e(TAG, "Error closing file descriptor: ${it.message}", it) }

    }


    private var prefetchJob: Job? = null

    //lazyColumn已支持pre render从而提前加载
    fun prefetch(currentPage: Int, quality: Float,  direction: Int) {
        prefetchJob?.cancel()
        prefetchJob = scope.launch {
            delay(100)
            prefetchPagesAround(currentPage, quality,  direction)
        }
    }
    private suspend fun prefetchPagesAround(currentPage: Int, quality: Float,  direction: Int) {
        val prefetchDistance = CacheManager.PreFetchDistance
        val range = when (direction) {
            1 -> (currentPage + 1)..(currentPage + prefetchDistance)
            -1 -> (currentPage - prefetchDistance)..<currentPage
            else -> (currentPage - prefetchDistance)..(currentPage + prefetchDistance)
        }
        val sortedPages = range
            .filter { it in 0 until pageCount }
            .filter { !cacheManager.pageExistsInCache(it) }
            .sortedBy { abs(it - currentPage) } // prefer pages close to current page

        sortedPages.forEach { pageNo ->
            if (renderJobs[pageNo]?.isActive != true) {
                renderJobs[pageNo]?.cancel()
                renderJobs[pageNo] = doLoadAndRender(pageNo, quality)
            }
        }
    }
}