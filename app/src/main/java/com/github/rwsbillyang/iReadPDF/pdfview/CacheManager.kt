package com.github.rwsbillyang.iReadPDF.pdfview

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Build
import android.util.Log
import android.util.LruCache


import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileOutputStream

//https://github.com/afreakyelf/Pdf-Viewer/blob/master/pdfViewer/src/main/java/com/rajat/pdfviewer/util/CacheManager.kt
/**
 * directory structure
 * /data/user/0/com.github.rwsbillyang.iReadPDF/cache/_pdf/0aedc5d342adf2a63f475cfe746ca42f/
 * file: /files/_pdf_/$md5.pdf
 * cover: /files/_pdf_/$md5.cover
 * pages: /cache/_pdf_/$md5/0,1,2,...
 * 因为pages的缓存有数量限制，故pdf和cover不能放在/cache/_pdf_/$md5/下面
 * */
class CacheManager(
    ctx: Context,
    fileId: String, // md5 of pdf
    private val cacheStrategy: CacheStrategy = CacheStrategy.MAXIMIZE_PERFORMANCE
) {
    private val memoryCache: LruCache<Int, Bitmap> = createMemoryCache()
    private val cachedPagesDir = getCachedPagesDir(ctx, fileId)
    companion object {
        const val _PDF_ = "_pdf_"
        const val MAX_CACHED_PDFS = 5
        const val PreFetchDistance = 2

        /**
         * return directory File: ${ctx.cacheDir}/$CACHE_PATH_PDF/$fileId, if not exists, create it firstly
         *
         * eg: /.../cache/_pdf/0aedc5d342adf2a63f475cfe746ca42f
         * /data/user/0/com.github.rwsbillyang.iReadPDF/cache/_pdf/0aedc5d342adf2a63f475cfe746ca42f
         * */
        private fun getCachedPagesDir(ctx: Context, fileId: String): File{
            val d = "${ctx.cacheDir}/$_PDF_/$fileId"
            val f = File(d)
            if (!f.exists()) {
                f.mkdirs()
            }
            return f
        }
        /**
         * return directory File:  ${ctx.filesDir}/$CACHE_PATH_PDF, if not exists, create it firstly
         * eg: /.../files/_pdf_
         * /data/user/0/com.github.rwsbillyang.iReadPDF/files/_pdf_
         * */
        private fun getShelfDir(ctx: Context): File{
            val d = "${ctx.filesDir}/$_PDF_"
            val f = File(d)
            if (!f.exists()) {
                f.mkdirs()
            }
            return f
        }



        fun delBook(ctx: Context, fileId: String){
            val d = getCachedPagesDir(ctx, fileId)
            if (d.exists()) {
                d.deleteRecursively()
            }

            var f = defaultCover(ctx, fileId)
            if (f.exists()) {
                f.delete()
            }
            f = defaultPdfFile(ctx, fileId)
            if (f.exists()) {
                f.delete()
            }
        }
        fun delCover(ctx: Context, fileId: String){
            val f = defaultCover(ctx, fileId)
            if (f.exists()) {
                f.delete()
            }
        }
        fun defaultCover(ctx: Context, fileId: String) = File(getShelfDir(ctx), "$fileId.cover")


        fun defaultPdfFile(ctx: Context, fileId: String) = File(getShelfDir(ctx), "$fileId.pdf")
    }


    init {
        if (cacheStrategy != CacheStrategy.DISABLE_CACHE) {
            CacheHelper.handleCacheStrategy(
               getCachedPagesDir(ctx, fileId),
                cacheStrategy,
                fileId,
                MAX_CACHED_PDFS
            )
        }
    }

    private fun createMemoryCache(): LruCache<Int, Bitmap> {
        val maxMemory = (Runtime.getRuntime().maxMemory() / 1024).toInt()
        val cacheSize = maxMemory / 6
        return object : LruCache<Int, Bitmap>(cacheSize) {
            override fun sizeOf(key: Int, value: Bitmap): Int = value.byteCount / 1024
        }
    }

    suspend fun getBitmapFromCache(pageNo: Int): Bitmap? = withContext(Dispatchers.IO) {
        memoryCache.get(pageNo)?.let { return@withContext it }
        if (cacheStrategy == CacheStrategy.DISABLE_CACHE) return@withContext null

        decodeBitmapFromDiskCache(pageNo)?.also {
            memoryCache.put(pageNo, it)
        }
    }

    private fun decodeBitmapFromDiskCache(pageNo: Int): Bitmap? {
        val file = File(cachedPagesDir, pageNo.toString())
        return if (file.exists()) BitmapFactory.decodeFile(file.absolutePath) else null
    }

    suspend fun addBitmapToCache(pageNo: Int, bitmap: Bitmap) {
        memoryCache.put(pageNo, bitmap)
        if (cacheStrategy != CacheStrategy.DISABLE_CACHE) {
            writeBitmapToCache(pageNo, bitmap)
        }
    }

    private suspend fun writeBitmapToCache(pageNo: Int, bitmap: Bitmap) = withContext(Dispatchers.IO) {
        runCatching {
            //cacheDir.mkdirs() // done in initialize
            val savePath = File(cachedPagesDir, pageNo.toString())
            //savePath.parentFile?.mkdirs()
            FileOutputStream(savePath).use { fos ->
                if(Build.VERSION.SDK_INT > 29)
                    bitmap.compress(Bitmap.CompressFormat.WEBP_LOSSY, 50, fos)
                else
                    bitmap.compress(Bitmap.CompressFormat.PNG, 50, fos)
            }
        }.onFailure {
            Log.e("CacheManager", "Error writing bitmap to cache (Page $pageNo)", it)
        }
    }

    suspend fun pageExistsInCache(pageNo: Int): Boolean = withContext(Dispatchers.IO) {
        if (cacheStrategy == CacheStrategy.DISABLE_CACHE) return@withContext false
        File(cachedPagesDir, pageNo.toString()).exists()
    }


}