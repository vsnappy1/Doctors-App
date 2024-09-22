package com.randos.doctorsapp.presentation.feature.developer.locationstream

import android.Manifest
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.randos.doctorsapp.R
import com.randos.domain.type.Location

data class LocationStreamState(
    val location: Location.LatLng? = Location.LatLng(0.0, 0.0),
    val isLocationTrackingActive: Boolean = false
)

@Composable
fun LocationStreamScreen(
    viewModel: LocationStreamViewModel = hiltViewModel(),
    onMoveToContentProvider: () -> Unit,
) {
    val uiState by viewModel.uiState.observeAsState(LocationStreamState())
    val launcher = rememberLauncherForActivityResult(ActivityResultContracts.RequestPermission()) {
        if (it) {
            viewModel.startLocationTracking()
        }
    }
    LocationStreamScreen(
        state = uiState,
        onMoveToContentProvider = onMoveToContentProvider,
        onStartLocationUpdates = viewModel::startLocationTracking,
        onStopLocationUpdates = viewModel::stopLocationTracking
    )

    LaunchedEffect(Unit) {
        viewModel.observeLocationTrackingStatus()
        if (!viewModel.isLocationPermissionGranted()) {
            launcher.launch(Manifest.permission.ACCESS_FINE_LOCATION)
        }
    }
}

@Composable
private fun LocationStreamScreen(
    state: LocationStreamState,
    onMoveToContentProvider: () -> Unit,
    onStartLocationUpdates: () -> Unit,
    onStopLocationUpdates: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {

        Column(modifier = Modifier.fillMaxWidth()) {
            Text(
                modifier = Modifier.align(Alignment.CenterHorizontally),
                text = "Location Stream",
                style = MaterialTheme.typography.titleLarge
            )
            Text(text = stringResource(R.string.location_stream_message))

            Text(
                modifier = Modifier
                    .align(Alignment.CenterHorizontally)
                    .padding(top = 64.dp),
                text = "Lat: ${state.location?.lat} | Lng: ${state.location?.lng}"
            )

            Button(
                modifier = Modifier.align(Alignment.CenterHorizontally),
                colors = ButtonDefaults.buttonColors().copy(
                    containerColor = if (state.isLocationTrackingActive) Color.Red else Color.Green
                ),
                onClick = {
                    if (state.isLocationTrackingActive) {
                        onStopLocationUpdates()
                    } else {
                        onStartLocationUpdates()
                    }
                }
            ) {
                val text = if (state.isLocationTrackingActive) {
                    "Stop location updates"
                } else {
                    "Start location updates"
                }
                Text(text)
            }
        }

        Button(
            modifier = Modifier.align(Alignment.BottomCenter),
            onClick = onMoveToContentProvider
        ) {
            Text("Move to content provider")
        }
    }
}

@Preview
@Composable
private fun PreviewLocationStreamScreen() {
    LocationStreamScreen(LocationStreamState(), {}, {}, {})
}