package ngui_maryanne.dissertation.publicparticipationplatform.features.citizen.petitions.newpetition

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
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
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import ngui_maryanne.dissertation.publicparticipationplatform.R
import ngui_maryanne.dissertation.publicparticipationplatform.components.AssimOutlinedDropdown
import ngui_maryanne.dissertation.publicparticipationplatform.features.common.auth.presentation.signup.CitizenRegistrationEvent
import ngui_maryanne.dissertation.publicparticipationplatform.utils.Constants.countiesMap
import ngui_maryanne.dissertation.publicparticipationplatform.utils.Constants.sectors

@Composable
fun CreatePetitionBottomSheet(
    state: NewPetitionState,
    onEvent: (NewPetitionEvent) -> Unit,
    onSubmit: () -> Unit,
    onDismiss: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
            .verticalScroll(rememberScrollState())
    ) {
        Text("Create Petition", style = MaterialTheme.typography.titleLarge)

        Spacer(Modifier.height(8.dp))
        BannerDisclaimer()

        Spacer(Modifier.height(16.dp))
        // County of Residence Dropdown
        AssimOutlinedDropdown(
            label = stringResource(id = R.string.sector_label),
            hint = stringResource(id = R.string.sector_hint),
            options = sectors,
            selectedValue = state.sector,
            onValueSelected = { onEvent(NewPetitionEvent.OnSectorChanged(it.toString())) },
            isCompulsory = true,
//            error = state.genderError?.let { stringResource(id = it) },
            isSearchable = true // Enable search functionality
        )


        Spacer(Modifier.height(8.dp))
        OutlinedTextField(
            value = state.title,
            onValueChange = { onEvent(NewPetitionEvent.OnTitleChanged(it)) },
            label = { Text("Petition Title") },
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
            options = countiesMap,
            selectedValue = state.county,
            onValueSelected = { onEvent(NewPetitionEvent.OnCountyChanged(it.toString())) },
            isCompulsory = true,
//            error = state.genderError?.let { stringResource(id = it) },
            isSearchable = true // Enable search functionality
        )
       /* DropdownField("Select County", value = state.county, options = COUNTIES) {
            onEvent(NewPetitionEvent.OnCountyChanged(it))
        }*/

        Spacer(Modifier.height(16.dp))
        Text("Request Goal (what the petition is to achieve)")
        state.requestGoals.forEachIndexed { index, goal ->
            OutlinedTextField(
                value = goal,
                onValueChange = { onEvent(NewPetitionEvent.OnRequestGoalChanged(index, it)) },
                modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp)
            )
        }
        TextButton(onClick = { onEvent(NewPetitionEvent.OnAddRequestGoal) }) {
            Text("Add Goal")
        }

        Spacer(Modifier.height(16.dp))
        Text("Target Signatures")
        Row(verticalAlignment = Alignment.CenterVertically) {
            IconButton(onClick = { onEvent(NewPetitionEvent.OnTargetSignatureChanged(-1)) }) {
                Icon(Icons.Default.Remove, contentDescription = "Decrease")
            }
            Text("${state.targetSignatures}", modifier = Modifier.padding(horizontal = 16.dp))
            IconButton(onClick = { onEvent(NewPetitionEvent.OnTargetSignatureChanged(1)) }) {
                Icon(Icons.Default.Add, contentDescription = "Increase")
            }
        }

        Spacer(Modifier.height(16.dp))
        Text("Supporting Reason (backing your petition)")
        state.supportingReasons.forEachIndexed { index, reason ->
            OutlinedTextField(
                value = reason,
                onValueChange = { onEvent(NewPetitionEvent.OnSupportingReasonChanged(index, it)) },
                modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp)
            )
        }
        TextButton(onClick = { onEvent(NewPetitionEvent.OnAddSupportingReason) }) {
            Text("Add Reason")
        }

        Spacer(Modifier.height(16.dp))
        Button(
            onClick = onSubmit,
            enabled = !state.isLoading,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Submit Petition")
        }

        Spacer(Modifier.height(8.dp))
        TextButton(onClick = onDismiss, modifier = Modifier.align(Alignment.End)) {
            Text("Cancel")
        }
    }
}
