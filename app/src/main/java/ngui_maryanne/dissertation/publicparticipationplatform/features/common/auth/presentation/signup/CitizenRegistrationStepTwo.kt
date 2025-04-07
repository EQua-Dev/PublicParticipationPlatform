package ngui_maryanne.dissertation.publicparticipationplatform.features.common.auth.presentation.signup

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import ngui_maryanne.dissertation.publicparticipationplatform.components.AssimOutlinedDropdown
import ngui_maryanne.dissertation.publicparticipationplatform.R
import ngui_maryanne.dissertation.publicparticipationplatform.utils.Constants.countiesMap
import coil.compose.AsyncImage

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CitizenRegistrationStepTwo(
    state: CitizenRegistrationState,
    onEvent: (CitizenRegistrationEvent) -> Unit,
    onRegistrationComplete: () -> Unit
) {
    val context = LocalContext.current
    var selectedImageUri by remember { mutableStateOf<Uri?>(null) }


    val imagePickerLauncher =
        rememberLauncherForActivityResult(contract = ActivityResultContracts.GetContent()) { uri: Uri? ->
            selectedImageUri = uri
        }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text(
            text = "Citizen Registration - Step 2",
            style = MaterialTheme.typography.headlineSmall,
            modifier = Modifier.fillMaxWidth()
        )

        // Profile Image Upload
        Card(
            modifier = Modifier
                .size(150.dp)
                .clip(RoundedCornerShape(12.dp))
                .clickable { imagePickerLauncher.launch("image/*") },
            elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
            colors = CardDefaults.cardColors(containerColor = Color.LightGray),
        ) {
            Box(contentAlignment = Alignment.Center, modifier = Modifier.fillMaxSize()) {
                selectedImageUri?.let {
                    onEvent(CitizenRegistrationEvent.ProfileImageChanged(it))
                    AsyncImage(
                        model = it,
                        contentDescription = "Selected Image",
                        modifier = Modifier.fillMaxSize(),
                        contentScale = ContentScale.Crop
                    )
                } ?: Icon(
                    imageVector = Icons.Default.CameraAlt,
                    contentDescription = "Add Image",
                    tint = Color.DarkGray,
                    modifier = Modifier.size(50.dp)
                )
            }
        }



        OutlinedTextField(
            value = state.occupation,
            onValueChange = { onEvent(CitizenRegistrationEvent.OccupationChanged(it)) },
            label = { Text("Occupation") },
            modifier = Modifier.fillMaxWidth()
        )

        // County of Residence Dropdown
        AssimOutlinedDropdown(
            label = stringResource(id = R.string.county_of_residence_label),
            hint = stringResource(id = R.string.county_of_residence_hint),
            options = countiesMap,
            selectedValue = state.countyOfResidence,
            onValueSelected = { onEvent(CitizenRegistrationEvent.CountyOfResidenceChanged(it.toString())) },
            isCompulsory = true,
//            error = state.genderError?.let { stringResource(id = it) },
            isSearchable = true // Enable search functionality
        )
        /*  var expandedResidence by remember { mutableStateOf(false) }
          ExposedDropdownMenuBox(
              expanded = expandedResidence,
              onExpandedChange = { expandedResidence = !expandedResidence }
          ) {
              OutlinedTextField(
                  value = state.countyOfResidence,
                  onValueChange = {},
                  label = { Text("County of Residence") },
                  modifier = Modifier.fillMaxWidth(),
                  readOnly = true,
                  trailingIcon = {
                      ExposedDropdownMenuDefaults.TrailingIcon(expanded = expandedResidence)
                  }
              )

              ExposedDropdownMenu(
                  expanded = expandedResidence,
                  onDismissRequest = { expandedResidence = false }
              ) {
                  counties.forEach { county ->
                      DropdownMenuItem(
                          text = { Text(text = county) },
                          onClick = {
                              onEvent(CitizenRegistrationEvent.CountyOfResidenceChanged(county))
                              expandedResidence = false
                          }
                      )
                  }
              }
          }*/

        // County of Birth Dropdown
        AssimOutlinedDropdown(
            label = stringResource(id = R.string.county_of_birth_label),
            hint = stringResource(id = R.string.county_of_birth_hint),
            options = countiesMap,
            selectedValue = state.countyOfBirth,
            onValueSelected = { onEvent(CitizenRegistrationEvent.CountyOfBirthChanged(it.toString())) },
            isCompulsory = true,
//            error = state.genderError?.let { stringResource(id = it) },
            isSearchable = true // Enable search functionality
        )
        /*var expandedBirth by remember { mutableStateOf(false) }
        ExposedDropdownMenuBox(
            expanded = expandedBirth,
            onExpandedChange = { expandedBirth = !expandedBirth }
        ) {
            OutlinedTextField(
                value = state.countyOfBirth,
                onValueChange = {},
                label = { Text("County of Birth") },
                modifier = Modifier.fillMaxWidth(),
                readOnly = true,
                trailingIcon = {
                    ExposedDropdownMenuDefaults.TrailingIcon(expanded = expandedBirth)
                }
            )

            ExposedDropdownMenu(
                expanded = expandedBirth,
                onDismissRequest = { expandedBirth = false }
            ) {
                counties.forEach { county ->
                    DropdownMenuItem(
                        text = { Text(text = county) },
                        onClick = {
                            onEvent(CitizenRegistrationEvent.CountyOfBirthChanged(county))
                            expandedBirth = false
                        }
                    )
                }
            }
        }*/

        Button(
            onClick = { onEvent(CitizenRegistrationEvent.CompleteRegistration) },
            modifier = Modifier.fillMaxWidth(),
            enabled = state.occupation.isNotBlank() &&
                    state.countyOfResidence.isNotBlank() &&
                    state.countyOfBirth.isNotBlank()
        ) {
            Text("Complete Registration")
        }

        if (state.isLoading) {
            CircularProgressIndicator(modifier = Modifier.align(Alignment.CenterHorizontally))
        }

        state.errorMessage?.let { error ->
            Text(
                text = error,
                color = MaterialTheme.colorScheme.error,
                modifier = Modifier.fillMaxWidth()
            )
        }

        // Navigate on successful registration
        LaunchedEffect(state.isRegistrationComplete) {
            if (state.isRegistrationComplete) {
                onRegistrationComplete()
            }
        }
    }
}