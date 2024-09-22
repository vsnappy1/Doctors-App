package com.randos.doctorsapp.presentation.feature.doctorlist

import android.Manifest
import android.util.Log
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.randos.doctorsapp.presentation.utils.stringFormat
import com.randos.domain.model.Address
import com.randos.domain.model.Doctor
import com.randos.domain.type.Location
import com.randos.domain.type.NetworkResult

data class DoctorListScreenState(
    val networkResult: NetworkResult<Pair<List<Doctor>, Location>> = NetworkResult.NotStarted,
)

private const val TAG = "DoctorListScreen"

@Composable
fun DoctorListScreen(
    onItemClick: (Doctor) -> Unit,
    viewModel: DoctorListViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.observeAsState(DoctorListScreenState())
    val launcher = rememberLauncherForActivityResult(ActivityResultContracts.RequestPermission()) {
        if (it) {
            viewModel.fetchDoctors()
        }
    }

    DoctorListScreen(
        state = uiState,
        getDoctors = viewModel::fetchDoctors,
        onItemClick = onItemClick
    )

    LaunchedEffect(Unit) {
        if (!viewModel.isLocationPermissionGranted()) {
            launcher.launch(Manifest.permission.ACCESS_FINE_LOCATION)
        }
    }
}

@Composable
private fun DoctorListScreen(
    state: DoctorListScreenState,
    getDoctors: () -> Unit,
    onItemClick: (Doctor) -> Unit
) {
    Log.d(TAG, "DoctorListScreen: ")
    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        val context = LocalContext.current
        when (val result = state.networkResult) {
            is NetworkResult.Failure -> {
                Toast.makeText(context, result.message, Toast.LENGTH_SHORT).show()
            }

            is NetworkResult.Loading -> {
                CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
            }

            is NetworkResult.NotStarted -> {
                getDoctors()
            }

            is NetworkResult.Success -> {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    val (doctors, location) = result.data
                    Text(text = "Doctors", style = MaterialTheme.typography.titleLarge)
                    when (location) {
                        is Location.Address -> {
                            Text(text = "Based on zip: ${location.address.postalCode}")
                        }

                        is Location.LatLng -> {
                            Text(text = "Based on (${location.lat}, ${location.lng})")
                        }
                    }
                    LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        items(doctors) { doctor ->
                            DoctorItem(doctor = doctor, onItemClick = { onItemClick(doctor) })
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun DoctorItem(
    modifier: Modifier = Modifier,
    doctor: Doctor,
    onItemClick: () -> Unit
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        onClick = onItemClick
    ) {
        Column(modifier = Modifier.padding(8.dp)) {
            Text(text = doctor.name, style = MaterialTheme.typography.titleLarge)
            Text(text = doctor.address.stringFormat())
        }
    }
}

@Preview
@Composable
private fun PreviewDoctorItem() {
    DoctorItem(
        doctor = Doctor(
            1,
            "Kumar",
            Address("995", "Sunnyvale", "CA", "95560", "USA")
        ),
        onItemClick = {})
}

@Preview
@Composable
private fun PreviewDoctorListScreen() {
    DoctorListScreen(DoctorListScreenState(), {}, {})
}