package com.aquaspoof.unified.toolkit.mcpe

import android.content.Context
import android.net.Uri
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.security.MessageDigest
import java.util.zip.CRC32

data class FileHashes(
    val md5: String = "...",
    val sha1: String = "...",
    val sha256: String = "...",
    val crc32: String = "..."
)

object HashCalculator {
    suspend fun calculateHashes(context: Context, uri: Uri): FileHashes = withContext(Dispatchers.IO) {
        val md5Digest = MessageDigest.getInstance("MD5")
        val sha1Digest = MessageDigest.getInstance("SHA-1")
        val sha256Digest = MessageDigest.getInstance("SHA-256")
        val crc32 = CRC32()

        try {
            context.contentResolver.openInputStream(uri)?.use { inputStream ->
                val buffer = ByteArray(8192)
                var bytesRead: Int
                while (inputStream.read(buffer).also { bytesRead = it } != -1) {
                    md5Digest.update(buffer, 0, bytesRead)
                    sha1Digest.update(buffer, 0, bytesRead)
                    sha256Digest.update(buffer, 0, bytesRead)
                    crc32.update(buffer, 0, bytesRead)
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
            return@withContext FileHashes("Ошибка", "Ошибка", "Ошибка", "Ошибка")
        }

        return@withContext FileHashes(
            md5 = md5Digest.digest().toHexString(),
            sha1 = sha1Digest.digest().toHexString(),
            sha256 = sha256Digest.digest().toHexString(),
            crc32 = crc32.value.toHexString()
        )
    }

    private fun ByteArray.toHexString() = joinToString("") { "%02x".format(it) }
    private fun Long.toHexString() = "%08x".format(this)
}