package com.aquaspoof.unified.toolkit.mcpe

import android.content.Context
import android.net.Uri
import androidx.core.content.edit
import androidx.documentfile.provider.DocumentFile
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn

object ConfigDownloader {

    fun download(context: Context, item: ConfigItem): Flow<DownloadState> = flow {
        val notificationHelper = NotificationHelper(context)
        notificationHelper.createChannel()
        val notificationId = item.id.hashCode()
        val notificationManager = notificationHelper.getManager()

        try {
            val responseBody = ApiService.instance.downloadFile(item.downloadUrl)
            val prefs = context.getSharedPreferences(LocaleHelper.PREFS_NAME, Context.MODE_PRIVATE)
            val folderUriString = prefs.getString("configs_folder_uri", null)

            if (folderUriString == null) {
                emit(DownloadState.Error("Папка для конфигов не выбрана в настройках!"))
                return@flow
            }

            val folderUri = Uri.parse(folderUriString)
            val configsFolder = DocumentFile.fromTreeUri(context, folderUri)

            if (configsFolder == null || !configsFolder.canWrite()) {
                emit(DownloadState.Error(context.getString(R.string.setup_error_permission_failed)))
                return@flow
            }

            configsFolder.findFile(item.filename)?.delete()
            val newFile = configsFolder.createFile("application/octet-stream", item.filename)

            if (newFile == null) {
                emit(DownloadState.Error("Не удалось создать файл."))
                return@flow
            }

            val inputStream = responseBody.byteStream()
            val outputStream = context.contentResolver.openOutputStream(newFile.uri)

            if (outputStream == null) {
                emit(DownloadState.Error("Не удалось открыть поток для записи."))
                inputStream.close()
                return@flow
            }

            val totalBytes = responseBody.contentLength()
            var downloadedBytes = 0L
            val buffer = ByteArray(8192)
            var bytesRead: Int
            var lastTime = System.currentTimeMillis()
            var lastDownloaded = 0L

            notificationManager.notify(notificationId, notificationHelper.buildProgressNotification(item.filename, 0).build())

            while (inputStream.read(buffer).also { bytesRead = it } != -1) {
                outputStream.write(buffer, 0, bytesRead)
                downloadedBytes += bytesRead

                val currentTime = System.currentTimeMillis()
                val elapsedTime = currentTime - lastTime

                if (elapsedTime > 200) {
                    val bytesSinceLast = downloadedBytes - lastDownloaded
                    val speedBps = if (elapsedTime > 0) (bytesSinceLast * 1000 / elapsedTime) else 0L
                    val progress = if (totalBytes > 0) (downloadedBytes * 100 / totalBytes).toInt() else -1

                    emit(DownloadState.Progress(progress, downloadedBytes, totalBytes, speedBps))

                    if (progress != -1) {
                        notificationManager.notify(notificationId, notificationHelper.buildProgressNotification(item.filename, progress).build())
                    }

                    lastTime = currentTime
                    lastDownloaded = downloadedBytes
                    delay(50)
                }
            }

            outputStream.flush()
            outputStream.close()
            inputStream.close()
            prefs.edit {
                putString("config_version_${item.id}", item.version)
            }

            notificationHelper.showCompleteNotification(notificationId, item.filename)
            emit(DownloadState.Success(item.filename))

        } catch (e: Exception) {
            e.printStackTrace()
            notificationHelper.showFailedNotification(notificationId, item.filename)
            emit(DownloadState.Error(e.message ?: "Неизвестная ошибка"))
        }
    }.flowOn(Dispatchers.IO)
}