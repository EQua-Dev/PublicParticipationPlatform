package ngui_maryanne.dissertation.publicparticipationplatform.features.superadmin.people.citizens.presentation

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.AsyncImage
import ngui_maryanne.dissertation.publicparticipationplatform.data.models.Citizen
import ngui_maryanne.dissertation.publicparticipationplatform.data.models.NationalCitizen
import ngui_maryanne.dissertation.publicparticipationplatform.data.models.Official
import ngui_maryanne.dissertation.publicparticipationplatform.features.officials.citizens.CitizenEvent
import ngui_maryanne.dissertation.publicparticipationplatform.features.officials.citizens.OfficialCitizenViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CitizenListScreen(
    modifier: Modifier = Modifier,
    viewModel: CreateCitizenViewModel = hiltViewModel(),
) {
    val state = viewModel.state.value
    val scope = rememberCoroutineScope()
    val bottomSheetState = rememberModalBottomSheetState()
//    val showFab = state.official?.permissions?.contains("add_citizens") ?: false


    if (state.showBottomSheet) {
        ModalBottomSheet(
            onDismissRequest = { viewModel.onEvent(AddCitizenEvent.DismissBottomSheet) },
            sheetState = bottomSheetState
        ) {
            CitizenVerificationBottomSheet(
                citizen = state.selectedCitizen,
            )
        }
    }
    Scaffold(
    ) { paddingValues ->
        Box(
            modifier = modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            when {
                state.isLoading -> {
                    CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
                }

                state.errorMessage != null -> {
                    ErrorMessage(state.errorMessage) {
                        viewModel.onEvent(AddCitizenEvent.LoadData)
                    }
                }

                else -> {
                    CitizenList(
                        citizens = state.citizens,
                        onCitizenSelected = { citizen ->
                            viewModel.onEvent(AddCitizenEvent.SelectCitizen(citizen))
                        }
                    )
                }
            }
        }
    }
}


@Composable
private fun CitizenList(
    citizens: List<Citizen>,
    onCitizenSelected: (Citizen) -> Unit
) {
    LazyColumn(modifier = Modifier.fillMaxSize()) {
        items(citizens) { citizen ->
            CitizenCard(
                citizen = citizen,
                onVerifyClick = { onCitizenSelected(citizen) }
            )
        }
    }
}

@Composable
private fun CitizenCard(
    citizen: Citizen,
    onVerifyClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp)
            .clickable { onVerifyClick() },
        elevation = CardDefaults.cardElevation(4.dp)
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            AsyncImage(
                model = citizen.profileImage,
                contentDescription = "Profile image",
                modifier = Modifier
                    .size(64.dp)
                    .clip(CircleShape),
                contentScale = ContentScale.Crop
            )

            Spacer(modifier = Modifier.width(16.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = "${citizen.firstName} ${citizen.lastName}",
                    style = MaterialTheme.typography.titleMedium
                )
                Text(
                    text = "ID: ${citizen.nationalID}",
                    style = MaterialTheme.typography.bodySmall
                )
                Text(
                    text = citizen.occupation,
                    style = MaterialTheme.typography.bodyMedium
                )

                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(8.dp)
                            .background(
                                color = if (citizen.approved == "true") Color.Green else Color.Red,
                                shape = CircleShape
                            )
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = if (citizen.approved == "true") "Approved" else "Pending",
                        style = MaterialTheme.typography.labelSmall
                    )
                }
            }

//            if (canApprove && citizen.approved !== "true") {
//                Button(onClick = onVerifyClick) {
//                    Text("Verify")
//                }
//            }
        }
    }
}

@Composable
private fun CitizenVerificationBottomSheet(
    citizen: Citizen?,
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        citizen?.let { citizen ->
            // Citizen from app database
            Text("App Citizen", style = MaterialTheme.typography.titleLarge)
            AsyncImage(
                model = citizen.profileImage,
                contentDescription = "Profile image",
                modifier = Modifier
                    .size(120.dp)
                    .clip(CircleShape),
                contentScale = ContentScale.Crop
            )
            Text("${citizen.firstName} ${citizen.lastName}")
            Text("ID: ${citizen.nationalID}")

            Spacer(modifier = Modifier.height(24.dp))


            Spacer(modifier = Modifier.height(24.dp))


                Button(
                    onClick = { AddCitizenEvent.DismissBottomSheet },
                    colors = ButtonDefaults.buttonColors(containerColor = Color.Green)
                ) {
                    Text("Okay")
                }

        } ?: run {
            Text("No citizen selected", color = Color.Red)
        }
    }
}

@Composable
private fun ErrorMessage(error: String, onRetry: () -> Unit) {
    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(error, color = Color.Red)
        Spacer(modifier = Modifier.height(16.dp))
        Button(onClick = onRetry) {
            Text("Retry")
        }
    }
}
