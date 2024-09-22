package com.randos.doctorsapp.presentation.feature.splash

import android.util.Log
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel

data class SplashScreenState(
    val isVerifiedUser: Boolean? = null
)

private const val TAG = "SplashScreen"

@Composable
fun SplashScreen(
    moveToLoginScreen: () -> Unit,
    moveToHomeScreen: () -> Unit,
    viewModel: SplashScreenViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.observeAsState(SplashScreenState())
    SplashScreen()

    LaunchedEffect(uiState.isVerifiedUser) {
        when (uiState.isVerifiedUser) {
            true -> {
                moveToHomeScreen()
            }

            false -> {
                moveToLoginScreen()
            }

            null -> {
                viewModel.checkIsVerifiedUser()
            }
        }
    }
}

@Composable
private fun SplashScreen() {
    Log.d(TAG, "SplashScreen: ")
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Icon(
            modifier = Modifier.size(100.dp),
            imageVector = Icons.Default.Add,
            contentDescription = ""
        )
    }
}

@Preview
@Composable
private fun PreviewSplashScreen() {
    SplashScreen()
}