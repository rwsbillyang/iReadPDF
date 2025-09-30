package com.github.rwsbillyang.iReadPDF.pdfview

import android.content.Context
import android.net.Uri
import android.os.Build
import android.os.ParcelFileDescriptor
import java.io.File
import java.nio.file.Files
import java.nio.file.Paths

sealed interface PdfSource {
    suspend fun getFileDescriptor(context: Context): ParcelFileDescriptor?

    /**
     * md5 of file content
     * */
    suspend fun getFileId(context: Context): String?

    fun getDisplayName(context: Context): String?
}

//data class Remote(val url: String) : PdfSource

class LocalFile(val file: File) : PdfSource {
    override suspend fun getFileDescriptor(context: Context): ParcelFileDescriptor? {
        if(!file.exists()) return null
        val safeFile = File(sanitizeFilePath(file.path))
        return ParcelFileDescriptor.open(safeFile, ParcelFileDescriptor.MODE_READ_ONLY)
    }

    override suspend fun getFileId(context: Context): String = FileUtil.calculateMd5ByInputStream(
        file.inputStream()
    ) ?:file.name
    override fun getDisplayName(context: Context): String? = file.name
    private fun sanitizeFilePath(filePath: String): String {
        return try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                val path = Paths.get(filePath)
                if (Files.exists(path)) filePath else ""
            } else filePath
        } catch (e: Exception) {
            ""
        }
    }
}

class LocalUri(val uri: Uri) : PdfSource {
    override suspend fun getFileDescriptor(context: Context): ParcelFileDescriptor? {
        return context.contentResolver.openFileDescriptor(uri, "r")
    }

    override suspend fun getFileId(context: Context) = FileUtil.calculateMd5(context, uri)

    override fun getDisplayName(context: Context): String? =
        FileUtil.getFileNameFromUri(context, uri)
}

class PdfSourceFromAsset(val assetFileName: String) : PdfSource {
    override suspend fun getFileDescriptor(context: Context): ParcelFileDescriptor?{
        val resolvedFile = FileUtil.copyFileFromAsset(context, assetFileName)
        return ParcelFileDescriptor.open(resolvedFile, ParcelFileDescriptor.MODE_READ_ONLY)
    }

    override suspend fun getFileId(context: Context) = FileUtil.calculateMd5ByInputStream(
        context.assets.open(
            assetFileName
        )
    ) ?:assetFileName
    override fun getDisplayName(context: Context): String? = assetFileName
}