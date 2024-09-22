package com.randos.domain.repository

import kotlinx.coroutines.flow.Flow

interface FileDownloadRepository {
    fun download(
        url: String,
        fileName: String,
        extension: String,
        onDownloadSuccess: (String) -> Unit,
        onDownloadFailed: (Exception) -> Unit
    ): Flow<Int>
}