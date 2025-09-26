package com.github.rwsbillyang.pdfview

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
    private val context: Context,
    private val currentOpenedFileName: String,
    private val cacheStrategy: CacheStrategy = CacheStrategy.MAXIMIZE_PERFORMANCE
) {
    private val memoryCache: LruCache<Int, Bitmap> = createMemoryCache()
    private var cacheDir = File(context.cacheDir, "${CACHE_PATH}/$currentOpenedFileName")

    suspend fun initialize() = withContext(Dispatchers.IO) {
        if (cacheStrategy == CacheStrategy.DISABLE_CACHE) return@withContext

        cacheDir = File(context.cacheDir, "$CACHE_PATH/$currentOpenedFileName")
        if (!cacheDir.exists()) {
            cacheDir.mkdirs()
        }

        CacheHelper.handleCacheStrategy(
            "CacheManager",
            cacheDir,
            cacheStrategy,
            currentOpenedFileName,
            CommonUtils.Companion.MAX_CACHED_PDFS
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
        val file = File(cacheDir, FileUtils.cachedFileNameWithFormat(pageNo))
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
            cacheDir.mkdirs()
            val savePath = File(cacheDir, FileUtils.cachedFileNameWithFormat(pageNo))
            savePath.parentFile?.mkdirs()
            FileOutputStream(savePath).use { fos ->
                bitmap.compress(Bitmap.CompressFormat.PNG, 100, fos)
            }
        }.onFailure {
            Log.e("CacheManager", "Error writing bitmap to cache (Page $pageNo)", it)
        }
    }

    suspend fun pageExistsInCache(pageNo: Int): Boolean = withContext(Dispatchers.IO) {
        if (cacheStrategy == CacheStrategy.DISABLE_CACHE) return@withContext false
        File(cacheDir, FileUtils.cachedFileNameWithFormat(pageNo)).exists()
    }

    companion object {
        const val CACHE_PATH = "___pdf___cache___"

        suspend fun clearCacheDir(context: Context) {
            withContext(Dispatchers.IO) {
                val cacheDir = File(context.cacheDir, CACHE_PATH)
                if (cacheDir.exists()) {
                    cacheDir.deleteRecursively()
                }
            }
        }
    }
}