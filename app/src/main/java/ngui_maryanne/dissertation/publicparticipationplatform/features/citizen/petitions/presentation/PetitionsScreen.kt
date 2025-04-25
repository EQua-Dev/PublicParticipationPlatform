package ngui_maryanne.dissertation.publicparticipationplatform.features.citizen.petitions.presentation

import android.os.Build
import androidx.annotation.RequiresApi
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import com.google.firebase.auth.FirebaseAuth
import ngui_maryanne.dissertation.publicparticipationplatform.components.CustomTextField
import ngui_maryanne.dissertation.publicparticipationplatform.data.enums.UserRole
import ngui_maryanne.dissertation.publicparticipationplatform.data.models.Petition
import ngui_maryanne.dissertation.publicparticipationplatform.features.citizen.petitions.newpetition.CreatePetitionBottomSheet
import ngui_maryanne.dissertation.publicparticipationplatform.features.citizen.petitions.newpetition.PetitionViewModel
import ngui_maryanne.dissertation.publicparticipationplatform.navigation.Screen
import java.time.Duration
import java.time.Instant

@RequiresApi(Build.VERSION_CODES.O)
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CitizenPetitionsScreen(
    modifier: Modifier = Modifier,
    navController: NavHostController,
    viewModel: CitizenPetitionsViewModel = hiltViewModel(),
    newViewModel: PetitionViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState
    val newPetitionUiState by newViewModel.newPetitionState
    val currentUid = FirebaseAuth.getInstance()

    Scaffold(
        topBar = {
            TopAppBar(title = { Text("Petitions") })
        },
        floatingActionButton = {
            if (uiState.currentUserRole == UserRole.CITIZEN.name) {
                FloatingActionButton(onClick = { viewModel.onEvent(PetitionEvent.OnToggleCreatePetition) }) {
                    Icon(Icons.Default.Add, contentDescription = "New Petition")
                }
            }
        }
    ) { padding ->
        Column(
            modifier = modifier
                .padding(padding)
                .fillMaxSize()
        ) {
            CustomTextField(
                value = uiState.searchQuery,
                onValueChange = { viewModel.onEvent(PetitionEvent.OnSearchQueryChanged(it)) },
                label = "",
                placeholder = "Search Petitions..."
            )
           /* OutlinedTextField(
                value = uiState.searchQuery,
                onValueChange = { viewModel.onEvent(PetitionEvent.OnSearchQueryChanged(it)) },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                placeholder = { Text("Search Petitions...") },
                singleLine = true
            )*/

            if (uiState.isLoading) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator()
                }
            } else if (uiState.isCreatingNewPetition) {
                ModalBottomSheet(
                    onDismissRequest = { viewModel.onEvent(PetitionEvent.OnToggleCreatePetition) }
                ) {
                    CreatePetitionBottomSheet(
                        state = newViewModel.newPetitionState.value,
                        onEvent = newViewModel::onNewPetitionEvent,
                        onSubmit = {
                            newViewModel.submitNewPetition(userId = currentUid.currentUser!!.uid)
                            viewModel.onEvent(PetitionEvent.OnToggleCreatePetition)
                        },
                        onDismiss = { viewModel.onEvent(PetitionEvent.OnToggleCreatePetition) }
                    )
                }
            } else {
                LazyColumn(modifier = Modifier.fillMaxSize()) {
                    uiState.petitionsBySector.forEach { (sector, petitions) ->
                        item {
                            Text(
                                text = sector,
                                style = MaterialTheme.typography.titleMedium,
                                modifier = Modifier.padding(8.dp)
                            )
                        }
                        items(petitions) { petition ->
                            PetitionCard(petition) {
                                navController.navigate(
                                    Screen.CitizenPetitionDetailsScreen.route.replace(
                                        "{petitionId}",
                                        it
                                    )
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun PetitionCard(petition: Petition, onPetitionClicked: (petitionId: String) -> Unit) {
    val daysLeft = remember(petition.expiryDate) {
        try {
            val expiry = Instant.ofEpochMilli(petition.expiryDate.toLong())
            val now = Instant.now()
            val days = Duration.between(now, expiry).toDays()
            days
        } catch (e: Exception) {
            -1
        }
    }

    val color = when {
        daysLeft < 0 -> Color.Gray
        daysLeft < 2 -> Color.Red
        daysLeft < 5 -> Color.Yellow
        else -> Color.Green
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp)
            .clickable { onPetitionClicked(petition.id) },
        elevation = CardDefaults.cardElevation(4.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(text = petition.title, style = MaterialTheme.typography.titleLarge)
                Text(text = "${daysLeft}d left", color = color)
            }
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = petition.description.take(100) + if (petition.description.length > 100) "..." else "",
                style = MaterialTheme.typography.bodyMedium
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "Target: ${petition.signatureGoal} signatures",
                style = MaterialTheme.typography.labelMedium,
                fontWeight = FontWeight.SemiBold
            )
        }
    }
}
