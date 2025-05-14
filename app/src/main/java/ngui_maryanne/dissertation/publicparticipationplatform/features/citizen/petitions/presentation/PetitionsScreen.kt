package ngui_maryanne.dissertation.publicparticipationplatform.features.citizen.petitions.presentation


import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.background
import androidx.compose.foundation.horizontalScroll
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.ErrorOutline
import androidx.compose.material.icons.filled.PendingActions
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import com.google.firebase.auth.FirebaseAuth
import ngui_maryanne.dissertation.publicparticipationplatform.components.AnimatedProgressIndicator
import ngui_maryanne.dissertation.publicparticipationplatform.data.enums.UserRole
import ngui_maryanne.dissertation.publicparticipationplatform.data.models.Petition
import ngui_maryanne.dissertation.publicparticipationplatform.data.models.signaturesProgress
import ngui_maryanne.dissertation.publicparticipationplatform.features.citizen.petitions.newpetition.CreatePetitionBottomSheet
import ngui_maryanne.dissertation.publicparticipationplatform.features.citizen.petitions.newpetition.PetitionViewModel
import ngui_maryanne.dissertation.publicparticipationplatform.features.citizen.polls.presentation.SearchBar
import ngui_maryanne.dissertation.publicparticipationplatform.navigation.Screen
import ngui_maryanne.dissertation.publicparticipationplatform.utils.Constants.sectors
import java.time.Duration
import java.time.Instant

@OptIn(ExperimentalMaterial3Api::class)
@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun CitizenPetitionsScreen(
    modifier: Modifier = Modifier,
    navController: NavHostController,
    viewModel: CitizenPetitionsViewModel = hiltViewModel(),
    newViewModel: PetitionViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val newPetitionState by newViewModel.newPetitionState.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }
    val scrollState = rememberScrollState()

    /*LaunchedEffect(Unit) {
        viewModel.events.collect { event ->
            when (event) {
                is PetitionEvent.ShowMessage -> {
                    snackbarHostState.showSnackbar(event.message)
                }
            }
        }
    }*/

    Scaffold(
        modifier = modifier,
        topBar = {
            TopAppBar(
                title = { Text("Public Petitions") },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface,
                    scrolledContainerColor = MaterialTheme.colorScheme.surface
                )
            )
        },
        floatingActionButton = {
            if (uiState.currentUserRole.name == UserRole.CITIZEN.name) {
                ExtendedFloatingActionButton(
                    onClick = { viewModel.onEvent(PetitionEvent.OnToggleCreatePetition) },
                    icon = { Icon(Icons.Default.Add, contentDescription = "New Petition") },
                    text = { Text("New Petition") },
                    containerColor = MaterialTheme.colorScheme.primary,
                    contentColor = MaterialTheme.colorScheme.onPrimary
                )
            }
        },
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            when {
                uiState.isLoading -> FullScreenLoading()
                uiState.error != null -> ErrorState(
                    error = uiState.error!!,
                    onRetry = { viewModel.observePetitions()
                    }
                )
                uiState.isCreatingNewPetition -> {
                    CreatePetitionBottomSheet(
                        state = newPetitionState,
                        onEvent = newViewModel::onNewPetitionEvent,
                        onSubmit = {
                            newViewModel.submitNewPetition(
                                userId = FirebaseAuth.getInstance().currentUser?.uid ?: "",
                                /*onSuccess = {
                                    viewModel.onEvent(PetitionEvent.OnToggleCreatePetition)
                                    viewModel.onEvent(PetitionEvent.RefreshPetitions)
                                },
                                onFailure = { error ->
                                    snackbarHostState.showSnackbar(error)
                                }*/
                            )
                            viewModel.onEvent(PetitionEvent.OnToggleCreatePetition)
                        },
                        onDismiss = { viewModel.onEvent(PetitionEvent.OnToggleCreatePetition) }
                    )
                }
                else -> {
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(horizontal = 16.dp)
                    ) {
                        // Search Bar
                        SearchBar(
                            query = uiState.searchQuery,
                            placeholder = "Search petitions by title or description...",
                            onQueryChange = { viewModel.onEvent(PetitionEvent.OnSearchQueryChanged(it)) },
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 8.dp)
                        )

                        // Sector Filter Chips
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .horizontalScroll(rememberScrollState()),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            FilterChip(
                                selected = uiState.selectedSector == null,
                                onClick = { viewModel.onEvent(PetitionEvent.OnSectorFilterChanged(null)) },
                                label = { Text("All Sectors") },
                                colors = FilterChipDefaults.filterChipColors(
                                    selectedContainerColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.2f),
                                    containerColor = MaterialTheme.colorScheme.surface,
                                    selectedLabelColor = MaterialTheme.colorScheme.primary,
                                    labelColor = MaterialTheme.colorScheme.onSurface
                                )
                            )
                            sectors.forEach { sector ->
                                FilterChip(
                                    selected = uiState.selectedSector == sector.first,
                                    onClick = { viewModel.onEvent(PetitionEvent.OnSectorFilterChanged(sector.first)) },
                                    label = { Text(sector.first) },
                                    colors = FilterChipDefaults.filterChipColors(
                                        selectedContainerColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.2f),
                                        containerColor = MaterialTheme.colorScheme.surface,
                                        selectedLabelColor = MaterialTheme.colorScheme.primary,
                                        labelColor = MaterialTheme.colorScheme.onSurface
                                    )
                                )
                            }
                        }

                        // Petitions List
                        LazyColumn(
                            modifier = Modifier.fillMaxSize(),
                            verticalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            if (uiState.allPetitions.isEmpty()) {
                                item {
                                    EmptyState(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(vertical = 48.dp)
                                    )
                                }
                            } else {
                                uiState.petitionsBySector.forEach { (sector, petitions) ->
                                    item {
                                        Text(
                                            text = sector,
                                            style = MaterialTheme.typography.titleMedium,
                                            modifier = Modifier.padding(vertical = 8.dp)
                                        )
                                    }

                                    items(petitions) { petition ->
                                        PetitionCard(
                                            petition = petition,
                                            onClick = {
                                                navController.navigate(
                                                    Screen.CitizenPetitionDetailsScreen.route.replace(
                                                        "{petitionId}",
                                                        petition.id
                                                    )
                                                )
                                            }
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

/*
@Composable
private fun SearchBar(
    query: String,
    onQueryChange: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    TextField(
        value = query,
        onValueChange = onQueryChange,
        modifier = modifier,
        placeholder = { Text("Search petitions by title or description...") },
        leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) },
        trailingIcon = {
            if (query.isNotEmpty()) {
                IconButton(onClick = { onQueryChange("") }) {
                    Icon(Icons.Default.Close, contentDescription = "Clear search")
                }
            }
        },
        shape = MaterialTheme.shapes.medium,
        colors = TextFieldDefaults.colors(
            focusedContainerColor = MaterialTheme.colorScheme.surfaceVariant,
            unfocusedContainerColor = MaterialTheme.colorScheme.surfaceVariant,
            disabledContainerColor = MaterialTheme.colorScheme.surfaceVariant,
            focusedIndicatorColor = Color.Transparent,
            unfocusedIndicatorColor = Color.Transparent,
            disabledIndicatorColor = Color.Transparent,
            focusedTrailingIconColor = MaterialTheme.colorScheme.onSurfaceVariant,
            unfocusedTrailingIconColor = MaterialTheme.colorScheme.onSurfaceVariant
        ),
        singleLine = true
    )
}
*/

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun PetitionCard(
    petition: Petition,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val daysLeft = remember(petition.expiryDate) {
        try {
            val expiry = Instant.ofEpochMilli(petition.expiryDate.toLong())
            val now = Instant.now()
            Duration.between(now, expiry).toDays()
        } catch (e: Exception) {
            -1
        }
    }

    val statusColor = when {
        daysLeft < 0 -> MaterialTheme.colorScheme.outline
        daysLeft < 2 -> MaterialTheme.colorScheme.error
        daysLeft < 5 -> MaterialTheme.colorScheme.tertiary
        else -> MaterialTheme.colorScheme.primary
    }

    Card(
        onClick = onClick,
        modifier = modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(2.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        )
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = petition.title,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )

                Box(
                    modifier = Modifier
                        .clip(MaterialTheme.shapes.small)
                        .background(statusColor.copy(alpha = 0.2f))
                        .padding(horizontal = 8.dp, vertical = 4.dp)
                ) {
                    Text(
                        text = if (daysLeft < 0) "Closed" else "$daysLeft days left",
                        style = MaterialTheme.typography.labelSmall,
                        color = statusColor
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = petition.description.take(150) + if (petition.description.length > 150) "..." else "",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.8f),
                maxLines = 3,
                overflow = TextOverflow.Ellipsis
            )

            Spacer(modifier = Modifier.height(12.dp))

//            LinearProgressIndicator()
            AnimatedProgressIndicator(percentage = petition.signaturesProgress() / petition.signatureGoal.toFloat())
            /*LinearProgressIndicator(
                progress = petition.signaturesProgress() / petition.signatureGoal.toFloat(),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(6.dp),
                color = MaterialTheme.colorScheme.primary,
                trackColor = MaterialTheme.colorScheme.surfaceVariant
            )*/

            Spacer(modifier = Modifier.height(8.dp))

            Row(
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = "${petition.signatures.size} of ${petition.signatureGoal} signatures",
                    style = MaterialTheme.typography.labelMedium
                )

                Text(
                    text = "${((petition.signaturesProgress() / petition.signatureGoal.toFloat()) * 100).toInt()}%",
                    style = MaterialTheme.typography.labelMedium,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}

/*
@Composable
private fun CreatePetitionBottomSheet(
    state: NewPetitionState,
    onEvent: (NewPetitionEvent) -> Unit,
    onSubmit: () -> Unit,
    onDismiss: () -> Unit
) {
    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = rememberModalBottomSheetState()
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
                .navigationBarsPadding(),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Text(
                text = "Create New Petition",
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold
            )

            OutlinedTextField(
                value = state.title,
                onValueChange = { onEvent(NewPetitionEvent.TitleChanged(it)) },
                modifier = Modifier.fillMaxWidth(),
                label = { Text("Title") },
                singleLine = true
            )

            OutlinedTextField(
                value = state.description,
                onValueChange = { onEvent(NewPetitionEvent.DescriptionChanged(it)) },
                modifier = Modifier
                    .fillMaxWidth()
                    .heightIn(min = 120.dp),
                label = { Text("Description") }
            )

            // Sector selection dropdown would go here

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                OutlinedTextField(
                    value = state.signatureGoal.toString(),
                    onValueChange = { onEvent(NewPetitionEvent.SignatureGoalChanged(it)) },
                    modifier = Modifier.weight(1f),
                    label = { Text("Signature Goal") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
                )

                // Date picker for expiry date would go here
            }

            Button(
                onClick = onSubmit,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp),
                enabled = state.isValid
            ) {
                Text("Submit Petition")
            }
        }
    }
}
*/

@Composable
private fun EmptyState(modifier: Modifier = Modifier) {
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Icon(
            imageVector = Icons.Default.PendingActions,
            contentDescription = null,
            modifier = Modifier.size(48.dp),
            tint = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f)
        )
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            text = "No petitions found",
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
        )
    }
}

@Composable
private fun FullScreenLoading() {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        CircularProgressIndicator()
    }
}

@Composable
private fun ErrorState(
    error: String,
    onRetry: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Icon(
            imageVector = Icons.Default.ErrorOutline,
            contentDescription = null,
            modifier = Modifier.size(48.dp),
            tint = MaterialTheme.colorScheme.error
        )
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            text = error,
            style = MaterialTheme.typography.bodyLarge,
            textAlign = TextAlign.Center
        )
        Spacer(modifier = Modifier.height(16.dp))
        Button(onClick = onRetry) {
            Text("Retry")
        }
    }
}