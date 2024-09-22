package com.randos.doctorsapp.presentation.feature.home

import android.util.Log
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.randos.domain.model.Address
import com.randos.domain.model.User
import com.randos.domain.type.NetworkResult

data class HomeScreenState(
    val user: NetworkResult<User> = NetworkResult.NotStarted
)

private const val TAG = "HomeScreen"

@Composable
fun HomeScreen(
    onDoctorListClick: () -> Unit,
    onMyAppointmentClick: () -> Unit,
    onFunGroundClick: () -> Unit,
    viewModel: HomeViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.observeAsState(HomeScreenState())
    HomeScreen(
        state = uiState,
        onDoctorListClick = onDoctorListClick,
        onMyAppointmentClick = onMyAppointmentClick,
        onFunGroundClick = onFunGroundClick,
        onGetUser = viewModel::getUserDetails
    )
}

@Composable
private fun HomeScreen(
    state: HomeScreenState,
    onDoctorListClick: () -> Unit,
    onMyAppointmentClick: () -> Unit,
    onFunGroundClick: () -> Unit,
    onGetUser: () -> Unit,
) {

    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        contentAlignment = Alignment.Center
    ) {

        when (val result = state.user) {
            is NetworkResult.Failure -> {
                Log.e(TAG, result.message, result.exception)
            }

            is NetworkResult.Loading -> {
                CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
            }

            is NetworkResult.NotStarted -> {
                Log.d(TAG, "HomeScreen: ")
                onGetUser()
            }

            is NetworkResult.Success -> {
                HomeScreen(
                    user = result.data,
                    onDoctorListClick = onDoctorListClick,
                    onMyAppointmentClick = onMyAppointmentClick,
                    onFunGroundClick = onFunGroundClick
                )
            }
        }
    }
}

@Composable
private fun HomeScreen(
    user: User,
    onDoctorListClick: () -> Unit,
    onMyAppointmentClick: () -> Unit,
    onFunGroundClick: () -> Unit,
) {
    Box(
        modifier = Modifier.fillMaxSize(),
    ) {
        Text(text = "Hi, ${user.name}", style = MaterialTheme.typography.titleLarge)
        Column(
            modifier = Modifier
                .align(Alignment.Center),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Button(
                onClick = onDoctorListClick
            ) {
                Text(text = "Doctor List")
            }
            Button(
                onClick = onMyAppointmentClick
            ) {
                Text(text = "My Appointments")
            }
            Button(
                onClick = onFunGroundClick
            ) {
                Text(text = "Fun ground")
            }
        }

    }
}


@Preview
@Composable
private fun PreviewHomeScreen() {
    HomeScreen(
        state = HomeScreenState(
            user = NetworkResult.Success(
                User(
                    1,
                    "kumar",
                    Address("", "", "", "", "")
                )
            ),
        ),
        onDoctorListClick = {},
        onMyAppointmentClick = {},
        onFunGroundClick = {},
        onGetUser = {}
    )
}