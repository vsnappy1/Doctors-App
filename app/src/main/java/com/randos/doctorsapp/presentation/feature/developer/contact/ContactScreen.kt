package com.randos.doctorsapp.presentation.feature.developer.contact

import android.Manifest
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
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.randos.doctorsapp.R
import com.randos.domain.model.Contact

data class ContactScreenState(
    val contacts: List<Contact> = emptyList(),
    val fetchingContacts: Boolean = false
)

@Composable
fun ContactScreen(viewModel: ContactViewModel = hiltViewModel()) {
    val uiState by viewModel.uiState.observeAsState(ContactScreenState())
    var isReadContactPermissionGranted by remember { mutableStateOf(viewModel.isReadContactPermissionGranted()) }

    if (isReadContactPermissionGranted) {
        ContactScreen(state = uiState)
    } else {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Text(text = "Contact read permission not granted.")
        }
    }

    val launcher =
        rememberLauncherForActivityResult(ActivityResultContracts.RequestPermission()) {
            isReadContactPermissionGranted = it
            viewModel.getContacts()
        }
    LaunchedEffect(Unit) {
        launcher.launch(Manifest.permission.READ_CONTACTS)
    }
}

@Composable
private fun ContactScreen(
    state: ContactScreenState
) {

    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Column(modifier = Modifier.fillMaxWidth()) {
            Text(
                modifier = Modifier.align(Alignment.CenterHorizontally),
                text = "Contact",
                style = MaterialTheme.typography.titleLarge
            )
            Text(text = stringResource(R.string.contact_message))

            state.contacts.let { contacts ->
                LazyColumn(
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(contacts) {
                        ContactItem(it)
                    }
                }
            }
        }
        if (state.fetchingContacts) {
            CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
        }
    }
}

@Composable
fun ContactItem(contact: Contact) {
    Card {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp)
        ) {
            Text(text = contact.name, style = MaterialTheme.typography.titleMedium)
            if (contact.number.isNotEmpty()) {
                Text(text = contact.number[0])
            }
        }
    }
}

@Preview
@Composable
private fun PreviewContactItem() {
    ContactItem(contact = Contact(1, "kumar", listOf("641***")))
}

@Preview
@Composable
private fun PreviewContactScreen() {
    ContactScreen(ContactScreenState())
}