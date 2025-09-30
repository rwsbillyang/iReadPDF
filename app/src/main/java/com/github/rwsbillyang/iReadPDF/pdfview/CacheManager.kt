package com.github.rwsbillyang.iReadPDF.pdfview

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.util.Log
import android.util.LruCache


import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileOutputStream

//https://github.com/afreakyelf/Pdf-Viewer/blob/master/pdfViewer/src/main/java/com/rajat/pdfviewer/util/CacheManager.kt
class CacheManager(
    private val appCacheDir: String, // context.cacheDir
    private val fileId: String, // md5 of pdf
    private val cacheStrategy: CacheStrategy = CacheStrategy.MAXIMIZE_PERFORMANCE
) {
    companion object {
        const val CACHE_PATH_PDF = "_pdf_cache_"
        const val MAX_CACHED_PDFS = 5
        const val PreFetchDistance = 2
        suspend fun clearCacheDir(context: Context) {
            withContext(Dispatchers.IO) {
                val cacheDir = File(context.cacheDir, CACHE_PATH_PDF)
                if (cacheDir.exists()) {
                    cacheDir.deleteRecursively()
                }
            }
        }
    }

    private val memoryCache: LruCache<Int, Bitmap> = createMemoryCache()
    private var fileCacheDir = "$appCacheDir/$CACHE_PATH_PDF/$fileId"

    suspend fun initialize() = withContext(Dispatchers.IO) {
        if (cacheStrategy == CacheStrategy.DISABLE_CACHE) return@withContext

        val cacheDirFile = File(fileCacheDir)
        if (!cacheDirFile.exists()) {
            cacheDirFile.mkdirs()
        }

        CacheHelper.handleCacheStrategy(
            "$appCacheDir/$CACHE_PATH_PDF",
            cacheStrategy,
            fileId,
            MAX_CACHED_PDFS
        )
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
        val file = File(fileCacheDir, pageNo.toString())
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
            val savePath = File(fileCacheDir, pageNo.toString())
            //savePath.parentFile?.mkdirs()
            FileOutputStream(savePath).use { fos ->
                bitmap.compress(Bitmap.CompressFormat.PNG, 100, fos)
            }
        }.onFailure {
            Log.e("CacheManager", "Error writing bitmap to cache (Page $pageNo)", it)
        }
    }

    suspend fun pageExistsInCache(pageNo: Int): Boolean = withContext(Dispatchers.IO) {
        if (cacheStrategy == CacheStrategy.DISABLE_CACHE) return@withContext false
        File(fileCacheDir, pageNo.toString()).exists()
    }


}