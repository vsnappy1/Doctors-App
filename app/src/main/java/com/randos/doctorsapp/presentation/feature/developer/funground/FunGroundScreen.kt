package com.randos.doctorsapp.presentation.feature.developer.funground

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.randos.doctorsapp.R
import com.randos.domain.model.Accelerometer

data class FunGroundScreenState(
    val accelerometer: Accelerometer? = null
)

@Composable
fun FunGroundScreen(
    onMoveToFileDownload: () -> Unit,
    viewModel: FunGroundViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.observeAsState(FunGroundScreenState())

    FunGroundScreen(
        state = uiState,
        onMoveToFileDownload = onMoveToFileDownload
    )

    LaunchedEffect(Unit) {
        viewModel.startObservingAccelerometer()
    }

    DisposableEffect(Unit) {
        onDispose {
            viewModel.stopObservingAccelerometer()
        }
    }
}

@Composable
private fun FunGroundScreen(
    state: FunGroundScreenState,
    onMoveToFileDownload: () -> Unit
) {
    val accelerometer = state.accelerometer
    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {

        Column(modifier = Modifier.fillMaxWidth()) {
            Text(
                modifier = Modifier.align(Alignment.CenterHorizontally),
                text = "Fun Ground",
                style = MaterialTheme.typography.titleLarge
            )
            Text(text = stringResource(R.string.fun_ground_message))
            Text(text = "x: ${accelerometer?.x}")
            Text(text = "y: ${accelerometer?.y}")
            Text(text = "z: ${accelerometer?.z}")
        }

        val color = MaterialTheme.colorScheme.onBackground
        accelerometer?.apply {
            val multiplier = 50
            val x = (x * multiplier)
            val y = (y * multiplier)
            val z = (z + 15) / 25
            Box(
                modifier = Modifier
                    .align(Alignment.Center)
                    .size(20.dp)
                    .scale(z)
                    .graphicsLayer {
                        translationX = -x
                        translationY = y
                    }
                    .drawWithContent {
                        drawContent()
                        drawCircle(color, size.width / 2)
                    }
            )
        }

        Button(
            modifier = Modifier.align(Alignment.BottomCenter),
            onClick = onMoveToFileDownload
        ) {
            Text("Move to file download")
        }
    }
}

@Preview
@Composable
private fun PreviewFunGround() {
    FunGroundScreen(FunGroundScreenState(), {})
}