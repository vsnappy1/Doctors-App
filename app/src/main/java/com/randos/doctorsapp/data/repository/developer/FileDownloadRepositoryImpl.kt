package com.randos.doctorsapp.data.repository.developer

import com.randos.domain.manager.DownloadManager
import com.randos.domain.repository.FileDownloadRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class FileDownloadRepositoryImpl @Inject constructor(
    private val downloadManager: DownloadManager
) : FileDownloadRepository {

    override fun download(
        url: String,
        fileName: String,
        extension: String,
        onDownloadSuccess: (String) -> Unit,
        onDownloadFailed: (Exception) -> Unit
    ): Flow<Int> =
        downloadManager.download(url, fileName, extension, onDownloadSuccess, onDownloadFailed)
}