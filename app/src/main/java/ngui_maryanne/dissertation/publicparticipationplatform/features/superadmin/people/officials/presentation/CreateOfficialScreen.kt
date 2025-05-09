package ngui_maryanne.dissertation.publicparticipationplatform.features.superadmin.people.officials.presentation

import androidx.compose.foundation.layout.Arrangement
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
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import ngui_maryanne.dissertation.publicparticipationplatform.components.CustomButton
import ngui_maryanne.dissertation.publicparticipationplatform.components.CustomTextField
import ngui_maryanne.dissertation.publicparticipationplatform.utils.Constants.permissions

@Composable
fun CreateOfficialScreen(
    modifier: Modifier = Modifier,
    viewModel: CreateOfficialViewModel = hiltViewModel()
) {
    val uiState = viewModel.uiState.value


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

        CustomTextField(
            value = uiState.lastName,
            onValueChange = { viewModel.onEvent(CreateOfficialUiEvent.UpdateLastName(it)) },
            label = "Last Name",
//            modifier = Modifier.fillMaxWidth()
        )

        CustomTextField(
            value = uiState.email,
            onValueChange = { viewModel.onEvent(CreateOfficialUiEvent.UpdateEmail(it)) },
            label = "Email",
            keyboardType = KeyboardType.Email,
            modifier = Modifier.fillMaxWidth()
        )

        CustomTextField(
            value = uiState.phoneNumber,
            onValueChange = { viewModel.onEvent(CreateOfficialUiEvent.UpdatePhoneNumber(it)) },
            label = "Phone Number",
            placeholder = "eg: +234123456789",
            keyboardType = KeyboardType.Phone,
            modifier = Modifier.fillMaxWidth()
        )

        Text("Permissions", style = MaterialTheme.typography.titleSmall)
        permissions.forEach { permission ->
            Row(
                modifier = Modifier
                    .fillMaxWidth() // Make Row fill the full width
                    .padding(vertical = 4.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Start // Align contents to start (left)
            ) {
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

        CustomButton(
            text = "Save",
            onClick = { viewModel.onEvent(CreateOfficialUiEvent.CreateOfficial) },
            enabled = !uiState.isLoading,
            modifier = Modifier.fillMaxWidth(),
            isLoading = uiState.isLoading
        )

        uiState.successMessage?.let {
            Text(text = it, color = Color.Green)
        }

        uiState.errorMessage?.let {
            Text(text = it, color = Color.Red)
        }
    }
}
