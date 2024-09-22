package com.randos.doctorsapp.presentation.feature.login

import android.widget.Toast
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
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
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.randos.doctorsapp.presentation.component.NetworkActionButton
import com.randos.domain.type.NetworkResult

data class LoginScreenState(
    val email: String = "",
    val password: String = "",
    val buttonEnabled: Boolean = true,
    val networkResult: NetworkResult<Unit> = NetworkResult.NotStarted
)

@Composable
fun LoginScreen(
    onSuccessfulLogin: () -> Unit,
    viewModel: LoginViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.observeAsState(LoginScreenState())
    LoginScreen(
        state = uiState,
        onEmailChange = viewModel::onEmailChange,
        onPasswordChange = viewModel::onPasswordChange,
        onLogin = viewModel::login
    )

    val context = LocalContext.current
    LaunchedEffect(key1 = uiState.networkResult) {
        when (val networkResult = uiState.networkResult) {
            is NetworkResult.Failure -> {
                Toast.makeText(context, networkResult.message, Toast.LENGTH_SHORT).show()
            }

            is NetworkResult.Success -> {
                Toast.makeText(context, "Success", Toast.LENGTH_SHORT).show()
                onSuccessfulLogin()
            }

            else -> {
                // Do Nothing
            }
        }
    }
}

@Composable
private fun LoginScreen(
    state: LoginScreenState,
    onEmailChange: (String) -> Unit,
    onPasswordChange: (String) -> Unit,
    onLogin: () -> Unit,
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(
            verticalArrangement = Arrangement.spacedBy(4.dp),
        ) {
            Text(text = "Login", style = MaterialTheme.typography.displayMedium)
            Text(text = "Please sign in to continue")
            Spacer(modifier = Modifier.height(24.dp))
            OutlinedTextField(
                modifier = Modifier.align(Alignment.CenterHorizontally),
                value = state.email,
                onValueChange = onEmailChange,
                label = { Text(text = "Email") })
            OutlinedTextField(
                modifier = Modifier.align(Alignment.CenterHorizontally),
                value = state.password,
                onValueChange = onPasswordChange,
                label = { Text(text = "Password") })

            NetworkActionButton(
                modifier = Modifier.align(Alignment.End),
                isLoading = state.networkResult is NetworkResult.Loading,
                enabled = state.buttonEnabled,
                onClick = onLogin
            ) {
                Text(text = "Login")
            }
        }
    }
}

@Preview
@Composable
fun PreviewLoginScreen() {
    LoginScreen(
        state = LoginScreenState(),
        onEmailChange = {},
        onPasswordChange = {},
        onLogin = {})
}