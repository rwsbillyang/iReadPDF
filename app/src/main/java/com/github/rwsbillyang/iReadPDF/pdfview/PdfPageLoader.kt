package com.github.rwsbillyang.iReadPDF.pdfview

import android.content.Context
import android.graphics.Bitmap
import android.graphics.pdf.PdfRenderer
import android.os.ParcelFileDescriptor
import android.util.Log
import android.util.Size
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
import java.util.concurrent.atomic.AtomicInteger
import kotlin.math.abs

class PdfPageLoader(
    val pdfRenderer: PdfRenderer,
    private val fd: ParcelFileDescriptor,
    private val cacheManager: CacheManager
) {
    private val scope = CoroutineScope(Dispatchers.IO + SupervisorJob())
    private val renderLock = Mutex()
    private val pageCount = AtomicInteger(pdfRenderer.pageCount)


    private val renderJobs = ConcurrentHashMap<Int, Deferred<Bitmap?>>()
    //private val pageDimensionCache = mutableMapOf<Int, Size>()
    private var pageSize = Size(1,1)

    private var prefetchJob: Job? = null


    companion object{
        suspend fun create(fd: ParcelFileDescriptor, fileId: String, ctx: Context, cacheStrategy: CacheStrategy) =
            PdfPageLoader(PdfRenderer(fd), fd, CacheManager(ctx.cacheDir.absolutePath, fileId, cacheStrategy).apply {
                initialize()
            })
    }

    init {
        preloadPageDimension()
    }

    private fun preloadPageDimension() {
        scope.launch {
            val page = pdfRenderer.openPage(0)
            pageSize = Size(page.width, page.height)
            page.close()
//            for (pageNo in 0 until getPageCount()) {
//                if (!pageDimensionCache.containsKey(pageNo)) {
//                    withPdfPage(pageNo) { page ->
//                        pageDimensionCache[pageNo] = Size(page.width, page.height)
//                    }
//                }
//            }
        }
    }

    suspend fun loadPage( pageNo: Int, lazyItemWidth: Int) = withContext(Dispatchers.IO) {
        if (pageNo < 0 || pageNo >= pageCount.get()) {
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
                    val d = doLoadAndRender(pageNo, lazyItemWidth)
                    renderJobs[pageNo] = d
                    d.await()
                }
            }
        }
    }

    fun prefetch(currentPage: Int, lazyItemWidth: Int,  direction: Int) {
        prefetchJob?.cancel()
        prefetchJob = scope.launch {
            delay(100)
            prefetchPagesAround(currentPage, lazyItemWidth,  direction)
        }
    }



    private suspend fun prefetchPagesAround(currentPage: Int, lazyItemWidth: Int,  direction: Int) {
        val prefetchDistance = CacheManager.PreFetchDistance
        val range = when (direction) {
            1 -> (currentPage + 1)..(currentPage + prefetchDistance)
            -1 -> (currentPage - prefetchDistance)..<currentPage
            else -> (currentPage - prefetchDistance)..(currentPage + prefetchDistance)
        }
        val sortedPages = range
            .filter { it in 0 until pageCount.get() }
            .filter { !cacheManager.pageExistsInCache(it) }
            .sortedBy { abs(it - currentPage) } // prefer pages close to current page

        sortedPages.forEach { pageNo ->
            if (renderJobs[pageNo]?.isActive != true) {
                renderJobs[pageNo]?.cancel()
                renderJobs[pageNo] = doLoadAndRender(pageNo, lazyItemWidth)
            }
        }
    }

    private fun doLoadAndRender(page: Int, lazyItemWidth: Int):Deferred<Bitmap?>  {
        return scope.async {
            val aspectRatio = pageSize.width.toFloat() / pageSize.height.toFloat()
            val height = (lazyItemWidth / aspectRatio).toInt()

            renderLock.withLock {
                val renderedBitmap = BitmapPool.getBitmap(lazyItemWidth, maxOf(10, height))
                val pdfPage = pdfRenderer.openPage(page)
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

    // private val openPages = ConcurrentHashMap<Int, PdfRenderer.Page>()
//    private fun openPageSafely(pageNo: Int): PdfRenderer.Page? {
//        openPages[pageNo]?.let { return it }
//        return try {
//            val page = pdfRenderer.openPage(pageNo)
//            openPages[pageNo] = page
//            if (openPages.size > 5) {
//                openPages.keys.minOrNull()?.let { openPages.remove(it)?.close() }
//            }
//            page
//        } catch (e: Exception) {
//            Log.e(TAG, "Error opening page $pageNo: ${e.message}", e)
//            null
//        }
//    }
//    private fun closeAllOpenPages() {
//        val iterator = openPages.iterator()
//        while (iterator.hasNext()) {
//            val entry = iterator.next()
//            try {
//                entry.value.close()
//            } catch (e: IllegalStateException) {
//                Log.e(TAG, "Page ${entry.key} was already closed", e)
//            } finally {
//                iterator.remove()
//            }
//        }
//    }
}