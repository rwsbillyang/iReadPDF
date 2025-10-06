package com.github.rwsbillyang.iReadPDF.pdfview

import android.content.Context
import android.net.Uri
import android.os.Build
import android.os.ParcelFileDescriptor
import java.io.File
import java.nio.file.Files
import java.nio.file.Paths

abstract class PdfSource(
    val fd: ParcelFileDescriptor?,
    val fileId: String?, //md5 of file content
    val displayName: String?
)

//data class Remote(val url: String) : PdfSource

@Deprecated("Not need")
class LocalFile(fd: ParcelFileDescriptor?, fileId: String?, displayName: String?) : PdfSource(fd, fileId, displayName) {
    companion object{
        suspend fun create(file: File): LocalFile {
            val fd = if(!file.exists())  null
            else{
                val safeFile = File(sanitizeFilePath(file.path))
                ParcelFileDescriptor.open(safeFile, ParcelFileDescriptor.MODE_READ_ONLY)
            }

            val fileId = FileUtil.calculateMd5ByInputStream(
            file.inputStream()
            ) ?:file.name

            return LocalFile(fd, fileId, file.name)
        }
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
}


class LocalUri(fd: ParcelFileDescriptor?, fileId: String?, displayName: String?) : PdfSource(fd, fileId, displayName) {
    companion object{
        suspend fun create(uri: Uri, ctx: Context): LocalUri {
            return LocalUri(ctx.contentResolver.openFileDescriptor(uri, "r"),
                FileUtil.calculateMd5(ctx, uri),
                FileUtil.getFileNameFromUri(ctx, uri))
        }
    }

}

@Deprecated("Not need")
class PdfSourceFromAsset(fd: ParcelFileDescriptor?, fileId: String?, displayName: String?) : PdfSource(fd, fileId, displayName) {
    companion object{
        //没加入book shelf 最终无法删除cache，除非全部清除缓存
        suspend fun create(assetFileName: String, ctx: Context): PdfSourceFromAsset {
            val fileId = FileUtil.calculateMd5ByInputStream(ctx.assets.open(assetFileName)) ?:assetFileName

            val dest = CacheManager.defaultPdfFile(ctx, fileId)
            FileUtil.copyFileFromAsset(ctx, assetFileName, dest)
            val fd = ParcelFileDescriptor.open(dest, ParcelFileDescriptor.MODE_READ_ONLY)

            return PdfSourceFromAsset(fd, fileId,  assetFileName)
        }
    }

}