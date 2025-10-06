package com.github.rwsbillyang.iReadPDF.pdfview


import android.util.Log
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File
import java.security.MessageDigest
import kotlin.math.max

//https://github.com/afreakyelf/Pdf-Viewer/
object CacheHelper {
    // **Apply Cache Strategy**
    fun handleCacheStrategy(
        cachedPagesDir: File, //context.cacheDir/_pdf_/$md5
        cacheStrategy: CacheStrategy,
        keepFileId: String,
        maxCachedPdfs: Int
    ) {
        when (cacheStrategy) {
            CacheStrategy.MINIMIZE_CACHE -> {//only cache pages of current book
                clearAllPreviousCache(cachedPagesDir, keepFileId)
            }
            CacheStrategy.MAXIMIZE_PERFORMANCE -> {//only cache pages of latest 5 books
                updateCacheAccessTime(cachedPagesDir)
                enforceCacheLimit(cachedPagesDir, maxCachedPdfs)
            }
            CacheStrategy.DISABLE_CACHE -> {//not cache
                // no-op
            }
        }
    }

    // **Clear all old files, keeping only the latest (for MINIMIZE_CACHE)**
    private fun clearAllPreviousCache(cacheDir: File, keepFileName: String) {
        val cachedFiles = cacheDir.parentFile?.listFiles() ?: return

        // If the file is not in cache, remove all previous files
        cachedFiles.forEach { file ->
            Log.d("CacheHelper", "Deleting old cached file: ${file.absolutePath}")
            if (file.name != keepFileName) {
                file.deleteRecursively()
            }
        }
    }


    // **Enforce LRU-based limit but only remove the oldest file if needed**
    private fun enforceCacheLimit(
        cacheDir: File,
        maxCachedPdfs: Int
    ) {
        val cachedFolders =
            cacheDir.parentFile?.listFiles()?.filter { it.isDirectory } ?: return

        if (cachedFolders.size >= max(maxCachedPdfs,1)) {
            cachedFolders.minByOrNull { it.lastModified() }?.let {
                Log.d("CacheHelper", "Evicting old cached folder: ${it.absolutePath}")
                it.deleteRecursively()
            }
        }
    }

    // **Update access time**
    private fun updateCacheAccessTime(cacheDir: File) {
        cacheDir.setLastModified(System.currentTimeMillis())
    }

    fun getCacheKey(source: String): String {
        val prefix = if (source.startsWith("http")) "url_" else "file_"
        val hash = sha256(source)
        return prefix + hash
    }

    private fun sha256(input: String): String {
        val bytes = MessageDigest.getInstance("SHA-256").digest(input.toByteArray())
        return bytes.joinToString("") { "%02x".format(it) }
    }
}