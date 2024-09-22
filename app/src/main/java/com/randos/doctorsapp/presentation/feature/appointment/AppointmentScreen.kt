package com.randos.doctorsapp.presentation.feature.appointment

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.randos.doctorsapp.presentation.component.NetworkActionButton
import com.randos.domain.model.Appointment
import com.randos.domain.model.Doctor
import com.randos.domain.type.NetworkResult

data class AppointmentScreenState(
    val doctor: Doctor? = null,
    val appointments: NetworkResult<List<Appointment>> = NetworkResult.NotStarted,
    val appointmentConfirmation: NetworkResult<Unit> = NetworkResult.NotStarted,
    val selectedAppointment: Appointment? = null
)

@Composable
fun AppointmentScreen(
    onAppointmentConfirmation: () -> Unit,
    viewModel: AppointmentViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.observeAsState(AppointmentScreenState())

    AppointmentScreen(
        state = uiState,
        onAppointmentSelected = viewModel::onAppointmentSelected,
        fetchAppointment = viewModel::getAppointments,
        onBookAppointment = viewModel::bookAppointment
    )

    val context = LocalContext.current
    LaunchedEffect(uiState.appointmentConfirmation) {
        when (val result = uiState.appointmentConfirmation) {
            is NetworkResult.Failure -> {
                Toast.makeText(context, result.message, Toast.LENGTH_SHORT).show()
            }

            is NetworkResult.Success -> {
                Toast.makeText(context, "Appointment confirmed.", Toast.LENGTH_SHORT).show()
                onAppointmentConfirmation()
            }

            else -> {
                // Do Nothing
            }

        }
    }
}

@Composable
private fun AppointmentScreen(
    state: AppointmentScreenState,
    onAppointmentSelected: (Appointment) -> Unit,
    fetchAppointment: () -> Unit,
    onBookAppointment: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        contentAlignment = Alignment.Center
    ) {

        when (val result = state.appointments) {
            is NetworkResult.Failure -> {
                Text(result.message)
            }

            is NetworkResult.Loading -> {
                CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
            }

            is NetworkResult.NotStarted -> {
                fetchAppointment()
            }

            is NetworkResult.Success -> {
                AppointmentScreen(
                    state = state,
                    appointments = result.data,
                    onAppointmentSelected = onAppointmentSelected,
                    onBookAppointment = onBookAppointment
                )
            }
        }
    }
}

@Composable
private fun AppointmentScreen(
    state: AppointmentScreenState,
    appointments: List<Appointment>,
    onAppointmentSelected: (Appointment) -> Unit,
    onBookAppointment: () -> Unit
) {
    var isDropExpended by remember { mutableStateOf(false) }
    Column(
        verticalArrangement = Arrangement.spacedBy(8.dp),
        modifier = Modifier.fillMaxWidth(),
    ) {
        Text(text = "Appointment", style = MaterialTheme.typography.displayMedium)
        Text(text = "Please select time for appointment with Doctor1 tomorrow")
        Spacer(modifier = Modifier.height(24.dp))
        
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(Color.Gray.copy(alpha = 0.3f))
                .padding(2.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween,
        ) {
            Text(text = "Time")
            Box {
                Text(text = "${state.selectedAppointment?.time ?: "Select Time"}",
                    modifier = Modifier.clickable { isDropExpended = true })
                DropdownMenu(
                    expanded = isDropExpended,
                    onDismissRequest = { isDropExpended = false }) {
                    appointments.forEach {
                        DropdownMenuItem(
                            text = { Text(text = "${it.time.from} - ${it.time.to}") },
                            onClick = {
                                onAppointmentSelected(it)
                                isDropExpended = false
                            })
                    }
                }
            }
        }

        NetworkActionButton(
            modifier = Modifier.align(Alignment.End),
            isLoading = state.appointmentConfirmation is NetworkResult.Loading,
            enabled = state.selectedAppointment != null,
            onClick = onBookAppointment
        ) {
            Text(text = "Confirm")
        }
    }
}

@Preview
@Composable
private fun PreviewAppointmentScreen() {
    AppointmentScreen(
        state = AppointmentScreenState(
            appointments = NetworkResult.Success(emptyList())
        ),
        onAppointmentSelected = {},
        fetchAppointment = {},
        onBookAppointment = {}
    )
}