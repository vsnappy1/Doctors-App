package com.randos.doctorsapp.presentation.feature.developer.filedownload

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.randos.doctorsapp.R
import com.randos.doctorsapp.presentation.component.NetworkActionButton

data class FileDownloadScreenState(
    val downloadedFilePath: String? = null,
    val percentageDownloaded: Int = 0,
    val isDownloadStarted: Boolean = false
)

@Composable
fun FileDownloadScreen(
    onMoveToLocationStream: () -> Unit,
    viewModel: FileDownloadViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.observeAsState(FileDownloadScreenState())
    FileDownloadScreen(
        state = uiState,
        onMoveToLocationStream = onMoveToLocationStream,
        onDownloadStart = viewModel::download
    )
}

@Composable
private fun FileDownloadScreen(
    state: FileDownloadScreenState,
    onMoveToLocationStream: () -> Unit,
    onDownloadStart: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {

        Column(modifier = Modifier.fillMaxWidth()) {
            Text(
                modifier = Modifier.align(Alignment.CenterHorizontally),
                text = "File Download",
                style = MaterialTheme.typography.titleLarge
            )
            Text(text = stringResource(R.string.file_download_message))

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 24.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text("File")
                Text(text = "${state.percentageDownloaded}%")
                NetworkActionButton(
                    isLoading = state.isDownloadStarted,
                    onClick = onDownloadStart
                ) {
                    Text("Download")
                }
            }

            state.downloadedFilePath?.let {
                Text(it)
            }
        }

        Button(
            modifier = Modifier.align(Alignment.BottomCenter),
            onClick = onMoveToLocationStream
        ) {
            Text("Move to location stream")
        }
    }
}

@Preview
@Composable
private fun PreviewFileDownload() {
    FileDownloadScreen(FileDownloadScreenState(), {}, {})
}