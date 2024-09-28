package com.randos.doctorsapp.data.manager

import android.app.DownloadManager
import android.database.Cursor
import android.net.Uri
import android.os.Environment
import android.util.Log
import com.randos.doctorsapp.di.Dispatcher
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.cancel
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.FlowCollector
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.launch
import javax.inject.Inject

private const val TAG = "DownloadManagerImpl"

class DownloadManagerImpl @Inject constructor(
    private val downloadManager: DownloadManager,
    @Dispatcher.Io private val dispatcher: CoroutineDispatcher
) : com.randos.domain.manager.DownloadManager {

    private val scope = CoroutineScope(dispatcher)

    //https://files.testfile.org/PDF/10MB-TESTFILE.ORG.pdf
    override fun download(
        url: String,
        fileName: String,
        extension: String,
        onDownloadSuccess: (String) -> Unit,
        onDownloadFailed: (Exception) -> Unit
    ): Flow<Int> = callbackFlow {
        val uri = Uri.parse(url)
        val request = getDownloadRequest(uri, fileName, extension)
        val result = downloadManager.enqueue(request)

        scope.launch {
            observeDownloadProgress(result, url, onDownloadSuccess, onDownloadFailed).collect {
                trySend(it)
            }
        }
        awaitClose {
            scope.cancel()
        }
    }

    private fun getDownloadRequest(
        uri: Uri?,
        fileName: String,
        extension: String
    ): DownloadManager.Request {
        return DownloadManager.Request(uri).apply {
            setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED)
            setDestinationInExternalPublicDir(
                Environment.DIRECTORY_DOWNLOADS,
                "$fileName.$extension"
            )
        }
    }

    private fun observeDownloadProgress(
        result: Long,
        url: String,
        onDownloadSuccess: (String) -> Unit,
        onDownloadFailed: (Exception) -> Unit
    ) = flow {
        try {
            var isDownloadFinished = false
            var isFailed = false
            while (!isDownloadFinished && !isFailed) {
                delay(100)
                val cursor = getDownloadManagerCursor(result)
                if (cursor.moveToFirst()) {
                    this.handleDownloadStatus(
                        cursor = cursor,
                        url = url,
                        onDownloadSuccess = {
                            onDownloadSuccess(it)
                            isDownloadFinished = true
                        },
                        onDownloadFailed = {
                            onDownloadFailed(it)
                            isFailed = true
                        }
                    )
                }
            }
        } catch (e: Exception) {
            Log.e(TAG, "Failed to observe download progress", e)
        }
    }

    private suspend fun FlowCollector<Int>.handleDownloadStatus(
        cursor: Cursor,
        url: String,
        onDownloadSuccess: (String) -> Unit,
        onDownloadFailed: (Exception) -> Unit
    ) {
        val status = cursor.getStatus()
        when (status) {
            DownloadManager.STATUS_SUCCESSFUL -> {
                emit(100)
                val path = cursor.getFilePath()
                onDownloadSuccess(path)
                Log.i(TAG, "File downloaded from $url, and stored at $path")
            }

            DownloadManager.STATUS_FAILED -> {
                onDownloadFailed(Exception("Failed to download file from $url"))
                Log.e(TAG, "Failed to download file from $url")
            }

            DownloadManager.STATUS_RUNNING -> {
                val totalBytes = cursor.getTotalBytes()
                if (totalBytes > 0) {
                    val downloadedBytes = cursor.getDownloadedBytesSoFar()
                    val percentageDownloaded =
                        ((downloadedBytes * 100) / totalBytes).toInt()
                    emit(percentageDownloaded)
                    Log.i(
                        TAG,
                        "Downloaded: $downloadedBytes  $totalBytes"
                    )
                }
            }
        }
    }

    private fun getDownloadManagerCursor(result: Long): Cursor {
        val query = DownloadManager.Query().setFilterById(result)
        val cursor = downloadManager.query(query)
        return cursor
    }

    private fun Cursor.getStatus(): Int =
        getInt(getColumnIndexOrThrow(DownloadManager.COLUMN_STATUS))

    private fun Cursor.getDownloadedBytesSoFar(): Long =
        getLong(getColumnIndexOrThrow(DownloadManager.COLUMN_BYTES_DOWNLOADED_SO_FAR))

    private fun Cursor.getTotalBytes(): Long =
        getLong(getColumnIndexOrThrow(DownloadManager.COLUMN_TOTAL_SIZE_BYTES))

    private fun Cursor.getFilePath(): String =
        getString(getColumnIndexOrThrow(DownloadManager.COLUMN_LOCAL_URI))
}

