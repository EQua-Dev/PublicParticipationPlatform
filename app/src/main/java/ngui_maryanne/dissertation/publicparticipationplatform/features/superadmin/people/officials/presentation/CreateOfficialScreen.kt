package ngui_maryanne.dissertation.publicparticipationplatform.features.superadmin.people.officials.presentation

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import ngui_maryanne.dissertation.publicparticipationplatform.components.CustomTextField

@Composable
fun CreateOfficialScreen(
    modifier: Modifier = Modifier,
    viewModel: CreateOfficialViewModel = hiltViewModel()
) {
    val uiState = viewModel.uiState.value
    val permissions = listOf(
        "create_policy",
        "create_poll",
        "create_projects",
        "update_policy_stage",
        "add_citizens",
        "approve_citizens"
    )

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp)
            .verticalScroll(rememberScrollState()),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text("Create Official", style = MaterialTheme.typography.headlineSmall)

        CustomTextField(
            value = uiState.firstName,
            onValueChange = { viewModel.onEvent(CreateOfficialUiEvent.UpdateFirstName(it)) },
            label = "Official First Name"
        )
        /*  OutlinedTextField(
              value = uiState.firstName,
              onValueChange = { viewModel.onEvent(CreateOfficialUiEvent.UpdateFirstName(it)) },
              label = { Text("First Name") },
              modifier = Modifier.fillMaxWidth()
          )*/

        OutlinedTextField(
            value = uiState.lastName,
            onValueChange = { viewModel.onEvent(CreateOfficialUiEvent.UpdateLastName(it)) },
            label = { Text("Last Name") },
            modifier = Modifier.fillMaxWidth()
        )

        OutlinedTextField(
            value = uiState.email,
            onValueChange = { viewModel.onEvent(CreateOfficialUiEvent.UpdateEmail(it)) },
            label = { Text("Email") },
            modifier = Modifier.fillMaxWidth()
        )

        OutlinedTextField(
            value = uiState.phoneNumber,
            onValueChange = { viewModel.onEvent(CreateOfficialUiEvent.UpdatePhoneNumber(it)) },
            label = { Text("Phone Number") },
            modifier = Modifier.fillMaxWidth()
        )

        Text("Permissions", style = MaterialTheme.typography.titleSmall)
        permissions.forEach { permission ->
            Row(verticalAlignment = Alignment.CenterVertically) {
                Checkbox(
                    checked = uiState.permissions.contains(permission),
                    onCheckedChange = {
                        viewModel.onEvent(
                            CreateOfficialUiEvent.TogglePermission(
                                permission
                            )
                        )
                    }
                )
                Text(permission)
            }
        }

        Button(
            onClick = { viewModel.onEvent(CreateOfficialUiEvent.CreateOfficial) },
            enabled = !uiState.isLoading,
            modifier = Modifier.fillMaxWidth()
        ) {
            if (uiState.isLoading) {
                CircularProgressIndicator(modifier = Modifier.size(24.dp))
            } else {
                Text("Save")
            }
        }

        uiState.successMessage?.let {
            Text(text = it, color = Color.Green)
        }

        uiState.errorMessage?.let {
            Text(text = it, color = Color.Red)
        }
    }
}
