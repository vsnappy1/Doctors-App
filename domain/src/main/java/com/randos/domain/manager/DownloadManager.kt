package com.randos.domain.manager

import kotlinx.coroutines.flow.Flow

interface DownloadManager {
    fun download(
        url: String,
        fileName: String,
        extension: String,
        onDownloadSuccess: (String) -> Unit,
        onDownloadFailed: (Exception) -> Unit
    ): Flow<Int>
}