package com.github.rwsbillyang.iReadPDF

import android.content.Context
import android.database.Cursor
import android.net.Uri
import android.provider.OpenableColumns
import android.widget.Toast
import com.github.rwsbillyang.iReadPDF.db.db
import java.io.File
import java.io.InputStream
import java.security.MessageDigest

object UriFileUtil {
    //需要注意的是，ContentResolver打开的输入流可能需要正确关闭，所以在复制完成后，要确保输入流和输出流都被关闭。
    // 可以使用try-with-resources（在Kotlin中使用use函数）来自动管理资源。
    fun copyPdfFromUri(ctx: Context, uri: Uri) {
        try {
            ctx.contentResolver.openInputStream(uri)?.use { inputStream ->
                val md5Hash = calculateMd5(inputStream)// 计算文件 MD5
                val tmp = "${md5Hash}.pdf"


                val tempFile = File(ctx.cacheDir, tmp)// 创建以 MD5 命名的临时文件（保留 .pdf 后缀）
                tempFile.outputStream().use { output ->
                    inputStream.copyTo(output)
                }

                // 验证复制后的文件 MD5（可选，确保复制完整性）
//                val copiedFileMd5 = calculateMd5(tempFile.inputStream())
//                if (copiedFileMd5 != md5Hash) {
//                    tempFile.delete()
//                    throw IOException("文件复制后 MD5 不一致，可能已损坏")
//                }

            } ?: run {
                Toast.makeText(ctx, "无法打开文件", Toast.LENGTH_SHORT).show()
            }
        } catch (e: Exception) {
            e.printStackTrace()
            Toast.makeText(ctx, "打开文件失败: ${e.message}", Toast.LENGTH_SHORT).show()
        }
    }

    fun calculateMd5(ctx: Context, uri: Uri): String? {
        try {
            ctx.contentResolver.openInputStream(uri)?.use { inputStream ->
                return calculateMd5(inputStream)// 计算文件 MD5
        } ?: run {
                Toast.makeText(ctx, "无法打开文件", Toast.LENGTH_SHORT).show()
            }
        } catch (e: Exception) {
            e.printStackTrace()
            Toast.makeText(ctx, "打开文件失败: ${e.message}", Toast.LENGTH_SHORT).show()
        }
        return null
    }
    //MD5 计算的时间复杂度为 O(n)（n 为文件大小），但对于 PDF 文件（通常几 MB 到几十 MB），
    // 现代设备的计算速度足够快（约 10-100ms）。文件名生成仅为字符串拼接，无性能问题。
    // readBytes()方法会将整个文件加载到内存，对于超大 PDF（如超过 100MB）可能导致内存溢出。
    // 改用分块读取方式：
    fun calculateMd5(inputStream: InputStream): String {
        val md5 = MessageDigest.getInstance("MD5")
        val buffer = ByteArray(8192) // 8KB 缓冲区
        var bytesRead: Int

        while (inputStream.read(buffer).also { bytesRead = it } != -1) {
            md5.update(buffer, 0, bytesRead)
        }
        return md5.digest().foldIndexed(StringBuilder()) { index, sb, byte ->
            if (index == 0) sb.append(String.format("%02x", byte))
            else sb.append(String.format("%02x", byte))
        }.toString()
    }


    /**
     * 从 Content Uri 中获取原始文件名（如 "example.pdf"）
     * @param uri 文件的 Content Uri（如 content://com.android.providers.media.documents/document/pdf:123）
     * @return 原始文件名（可能为空，需判空处理）
     */
    fun getFileNameFromUri(ctx:Context, uri: Uri): String? {
        // 优先通过 DISPLAY_NAME 列获取（标准元数据）
        var displayName: String? = queryDisplayName(ctx, uri)

        // 如果 DISPLAY_NAME 为空（罕见情况），尝试从 Uri 路径中提取
        if (displayName.isNullOrEmpty()) {
            displayName = extractFileNameFromPath(uri)
        }

        return displayName
    }

    /**
     * 查询 Content Provider 的 DISPLAY_NAME 元数据
     */
    private fun queryDisplayName(context: Context, uri: Uri): String? {
        var cursor: Cursor? = null
        return try {
            // 查询的列：仅需 DISPLAY_NAME
            val projection = arrayOf(OpenableColumns.DISPLAY_NAME)
            cursor = context.contentResolver.query(uri, projection, null, null, null)

            if (cursor != null && cursor.moveToFirst()) {
                val displayNameIndex = cursor.getColumnIndexOrThrow(OpenableColumns.DISPLAY_NAME)
                cursor.getString(displayNameIndex)
            } else {
                null
            }
        } catch (e: Exception) {
            e.printStackTrace()
            null
        } finally {
            cursor?.close() // 确保关闭 Cursor 避免内存泄漏
        }
    }

    /**
     * 备用方案：从 Uri 路径中提取文件名（当 DISPLAY_NAME 不可用时）
     * 适用于自定义 Content Provider 或特殊格式的 Uri
     */
    private fun extractFileNameFromPath(uri: Uri): String? {
        val path = uri.path ?: return null
        val lastSlashIndex = path.lastIndexOf('/')
        return if (lastSlashIndex != -1 && lastSlashIndex < path.length - 1) {
            path.substring(lastSlashIndex + 1)
        } else {
            null
        }
    }

}