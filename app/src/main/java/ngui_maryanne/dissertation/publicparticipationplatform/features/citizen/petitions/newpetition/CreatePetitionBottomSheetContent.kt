package ngui_maryanne.dissertation.publicparticipationplatform.features.citizen.petitions.newpetition

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import ngui_maryanne.dissertation.publicparticipationplatform.R
import ngui_maryanne.dissertation.publicparticipationplatform.components.AssimOutlinedDropdown
import ngui_maryanne.dissertation.publicparticipationplatform.features.officials.policies.createpolicy.PolicyCoverImageSection
import ngui_maryanne.dissertation.publicparticipationplatform.utils.Constants.countiesMap
import ngui_maryanne.dissertation.publicparticipationplatform.utils.Constants.getSectors

@Composable
fun CreatePetitionBottomSheet(
    state: NewPetitionState,
    onEvent: (NewPetitionEvent) -> Unit,
    onSubmit: () -> Unit,
    onDismiss: () -> Unit
) {

    val context = LocalContext.current
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
            .verticalScroll(rememberScrollState())
    ) {
        Text(stringResource(R.string.create_petition), style = MaterialTheme.typography.titleLarge)

        Spacer(Modifier.height(8.dp))
        BannerDisclaimer()

        Spacer(Modifier.height(16.dp))
        val imagePicker = rememberLauncherForActivityResult(
            contract = ActivityResultContracts.GetContent()
        ) { uri: Uri? ->
            uri?.let { onEvent(NewPetitionEvent.CoverImageSelected(it)) }
        }

        PolicyCoverImageSection(
            imageUri = state.coverImageUri,
            onImageClick = { imagePicker.launch("image/*") }
        )

        Spacer(Modifier.height(8.dp))
        AssimOutlinedDropdown(
            label = stringResource(id = R.string.sector_label),
            hint = stringResource(id = R.string.sector_hint),
            options = getSectors(context),
            selectedValue = state.sector,
            onValueSelected = { onEvent(NewPetitionEvent.OnSectorChanged(it.toString())) },
            isCompulsory = true,
            isSearchable = true // Enable search functionality
        )

        Spacer(Modifier.height(8.dp))
        OutlinedTextField(
            value = state.title,
            onValueChange = { onEvent(NewPetitionEvent.OnTitleChanged(it)) },
            label = { Text(stringResource(R.string.petition_title)) },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(Modifier.height(8.dp))
        OutlinedTextField(
            value = state.description,
            onValueChange = { onEvent(NewPetitionEvent.OnDescriptionChanged(it)) },
            label = { Text("Petition Description") },
            modifier = Modifier.fillMaxWidth(),
            maxLines = 5
        )

        Spacer(Modifier.height(8.dp))
        AssimOutlinedDropdown(
            label = stringResource(id = R.string.county_label),
            hint = stringResource(id = R.string.county_hint),
            options = countiesMap.sortedBy { it.first },
            selectedValue = state.county,
            onValueSelected = { onEvent(NewPetitionEvent.OnCountyChanged(it.toString())) },
            isCompulsory = true,
            isSearchable = true // Enable search functionality
        )

        Spacer(Modifier.height(16.dp))
        Column(horizontalAlignment = Alignment.Start) {
            Text(stringResource(R.string.request_goal_what_the_petition_is_to_achieve))
            state.requestGoals.forEachIndexed { index, goal ->
                OutlinedTextField(
                    value = goal,
                    onValueChange = { onEvent(NewPetitionEvent.OnRequestGoalChanged(index, it)) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 4.dp)
                )
            }
            TextButton(onClick = { onEvent(NewPetitionEvent.OnAddRequestGoal) }) {
                Text(stringResource(R.string.add_goal))
            }
        }

        Spacer(Modifier.height(16.dp))
        Column(
            modifier = Modifier.fillMaxWidth(),
        ) {
            Text("Target Signatures")
            Row(verticalAlignment = Alignment.CenterVertically) {
                IconButton(onClick = { onEvent(NewPetitionEvent.OnTargetSignatureChanged(-1)) }) {
                    Icon(Icons.Default.Remove, contentDescription = "Decrease")
                }
                OutlinedTextField(
                    value = state.targetSignatures.toString(),
                    keyboardOptions = KeyboardOptions.Default.copy(imeAction = ImeAction.Done)
                    ,
                    onValueChange = {
                        val newValue = it.filter { char -> char.isDigit() }
                        if (newValue.isNotEmpty()) {
                            onEvent(NewPetitionEvent.OnTargetSignatureManuallyChanged(newValue.toInt()))
                        } else {
                            onEvent(NewPetitionEvent.OnTargetSignatureManuallyChanged(0))
                        }
                    },
                    modifier = Modifier
                        .width(120.dp)
                        .padding(horizontal = 8.dp),
                    label = { Text("") } // Remove label to save space
                    , singleLine = true
                )
                IconButton(onClick = { onEvent(NewPetitionEvent.OnTargetSignatureChanged(1)) }) {
                    Icon(Icons.Default.Add, contentDescription = "Increase")
                }
            }
        }

        Spacer(Modifier.height(16.dp))
        Column(horizontalAlignment = Alignment.Start) {
            Text(stringResource(R.string.supporting_reason_backing_your_petition))
            state.supportingReasons.forEachIndexed { index, reason ->
                OutlinedTextField(
                    value = reason,
                    onValueChange = { onEvent(NewPetitionEvent.OnSupportingReasonChanged(index, it)) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 4.dp)
                )
            }
            TextButton(onClick = { onEvent(NewPetitionEvent.OnAddSupportingReason) }) {
                Text(stringResource(R.string.add_reason))
            }
        }

        Spacer(Modifier.height(16.dp))
        Button(
            onClick = onSubmit,
            enabled = !state.isLoading,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(stringResource(R.string.submit_petition))
        }

        Spacer(Modifier.height(8.dp))
        TextButton(onClick = onDismiss, modifier = Modifier.align(Alignment.End)) {
            Text(stringResource(R.string.cancel))
        }
    }
}