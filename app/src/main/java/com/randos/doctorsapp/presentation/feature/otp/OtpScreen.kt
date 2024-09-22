package com.randos.doctorsapp.presentation.feature.otp

import android.Manifest
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.randos.doctorsapp.presentation.component.NetworkActionButton
import com.randos.domain.type.NetworkResult

data class OtpScreenState(
    val otp: String? = null,
    val buttonEnabled: Boolean = false,
    val otpVerification: NetworkResult<Unit> = NetworkResult.NotStarted
)

@Composable
fun OtpScreen(
    onConfirmation: () -> Unit = {},
    viewModel: OtpViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.observeAsState(OtpScreenState())

    OtpScreen(
        state = uiState,
        onOtpChange = viewModel::onOtpChange,
        onVerify = viewModel::verify
    )

    val context = LocalContext.current

    LaunchedEffect(key1 = uiState.otpVerification) {
        when (val result = uiState.otpVerification) {
            is NetworkResult.Failure -> {
                Toast.makeText(context, result.message, Toast.LENGTH_SHORT).show()
            }

            is NetworkResult.Success -> {
                Toast.makeText(context, "Otp Verified.", Toast.LENGTH_SHORT).show()
                onConfirmation()
            }

            else -> {
                // Do Nothing
            }
        }
    }

    // Request RECEIVE_SMS permission if necessary
    val launcher =
        rememberLauncherForActivityResult(contract = ActivityResultContracts.RequestPermission()) { granted ->
            if (granted) {
                viewModel.observeReceivedSms()
            }
        }

    LaunchedEffect(Unit) {
        viewModel.requestOtp()
        if (viewModel.isReceiveSmsPermissionGranted()) {
            viewModel.observeReceivedSms()
        } else {
            launcher.launch(Manifest.permission.RECEIVE_SMS)
        }
    }
}

@Composable
private fun OtpScreen(
    state: OtpScreenState,
    onOtpChange: (String) -> Unit,
    onVerify: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
            Text(text = "Verification", style = MaterialTheme.typography.displayMedium)
            Text(text = "We've sent you the verification \ncode on registered email")
            Spacer(modifier = Modifier.height(24.dp))

            OutlinedTextField(
                modifier = Modifier.align(Alignment.CenterHorizontally),
                value = state.otp.orEmpty(),
                onValueChange = onOtpChange,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
            )
            NetworkActionButton(
                modifier = Modifier.align(Alignment.End),
                isLoading = state.otpVerification is NetworkResult.Loading,
                enabled = state.buttonEnabled,
                onClick = onVerify
            ) {
                Text(text = "Verify")
            }
        }
    }
}

@Preview
@Composable
private fun PreviewOtpScreen() {
    OtpScreen(state = OtpScreenState(),
        onOtpChange = {},
        onVerify = {})
}