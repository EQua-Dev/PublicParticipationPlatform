package ngui_maryanne.dissertation.publicparticipationplatform.features.citizen.petitions.petitiondetails

import android.net.Uri
import android.os.Build
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.EditNote
import androidx.compose.material.icons.filled.ErrorOutline
import androidx.compose.material.icons.filled.Verified
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.ProgressIndicatorDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.core.net.toUri
import androidx.fragment.app.FragmentActivity
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import coil.compose.AsyncImage
import ngui_maryanne.dissertation.publicparticipationplatform.R
import ngui_maryanne.dissertation.publicparticipationplatform.components.AnimatedProgressIndicator
import ngui_maryanne.dissertation.publicparticipationplatform.data.enums.UserRole
import ngui_maryanne.dissertation.publicparticipationplatform.data.models.Petition
import ngui_maryanne.dissertation.publicparticipationplatform.data.models.daysToExpiry
import ngui_maryanne.dissertation.publicparticipationplatform.data.models.signaturesProgress
import ngui_maryanne.dissertation.publicparticipationplatform.features.citizen.petitions.newpetition.NewPetitionEvent
import ngui_maryanne.dissertation.publicparticipationplatform.features.officials.policies.createpolicy.PolicyCoverImageSection
import ngui_maryanne.dissertation.publicparticipationplatform.utils.findActivity

@OptIn(ExperimentalMaterial3Api::class)
@RequiresApi(Build.VERSION_CODES.P)
@Composable
fun PetitionDetailsScreen(
    petitionId: String,
    navController: NavHostController,
    viewModel: PetitionDetailsViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }
    val context = LocalContext.current
    val activity = context.findActivity() as? FragmentActivity
    val showEditSheet = remember { mutableStateOf(false) }

    LaunchedEffect(petitionId) {
        viewModel.onEvent(PetitionDetailsEvent.LoadPetition(petitionId))
    }

    /* LaunchedEffect(Unit) {
         viewModel.events.collect { event ->
             when (event) {
                 is PetitionDetailsEvent.ShowMessage -> {
                     snackbarHostState.showSnackbar(event.message)
                 }
                 PetitionDetailsEvent.PetitionDeleted -> {
                     navController.popBackStack()
                 }
             }
         }
     }*/

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(stringResource(R.string.petition_details)) },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                },
                actions = {
                    if (state.petition?.createdBy == state.currentUserId) {
                        IconButton(
                            onClick = { showEditSheet.value = true },
                            modifier = Modifier.padding(end = 8.dp)
                        ) {
                            Icon(Icons.Default.Edit, contentDescription = "Edit Petition")
                        }
                    }
                }
            )
        },
        floatingActionButton = {
            if (state.petition?.createdBy != state.currentUserId &&
                !state.hasSigned &&
                state.currentUserRole == UserRole.CITIZEN
            ) {
                ExtendedFloatingActionButton(
                    onClick = {
                        activity?.let {
                            viewModel.onEvent(PetitionDetailsEvent.SignPetition(activity, false))

                        }
                            ?: run { /*snackbarHostState.showSnackbar("Could not authenticate signature")*/ }
                    },
                    icon = { Icon(Icons.Default.EditNote, contentDescription = "Sign Petition") },
                    text = { Text(stringResource(R.string.sign_petition)) },
                    containerColor = MaterialTheme.colorScheme.primary,
                    contentColor = MaterialTheme.colorScheme.onPrimary
                )
            }
        },
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { paddingValues ->
        when {
            state.isLoading -> FullScreenLoading()
            state.error != null -> ErrorState(
                error = state.error!!,
                onRetry = { viewModel.onEvent(PetitionDetailsEvent.LoadPetition(petitionId)) }
            )

            state.petition != null -> {
                PetitionDetailsContent(
                    petition = state.petition!!,
                    hasSigned = state.hasSigned,
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues)
                        .padding(16.dp)
                )
            }
        }
    }

    // Edit Petition Bottom Sheet
    if (showEditSheet.value && state.petition != null) {
        EditPetitionBottomSheet(
            petition = state.petition!!,
            onDismiss = { showEditSheet.value = false },
            onSave = { title, description, coverImage, goals ->
                viewModel.onEvent(
                    PetitionDetailsEvent.UpdatePetition(
                        title = title,
                        description = description,
                        coverImage = coverImage,
                        requestGoals = goals
                    )
                )
                showEditSheet.value = false
            },
            onDelete = {
                viewModel.onEvent(PetitionDetailsEvent.DeletePetition)
                showEditSheet.value = false
            }
        )
    }
}

@Composable
private fun PetitionDetailsContent(
    petition: Petition,
    hasSigned: Boolean,
    modifier: Modifier = Modifier
) {
    val progress by remember { mutableStateOf(petition.signaturesProgress()) }
    val animatedProgress by animateFloatAsState(
        targetValue = progress,
        animationSpec = ProgressIndicatorDefaults.ProgressAnimationSpec
    )

    Column(
        modifier = modifier.verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Cover Image
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .aspectRatio(16f / 9f)
                .clip(MaterialTheme.shapes.large)
        ) {
            if (petition.coverImage.isNotEmpty()) {
                AsyncImage(
                    model = petition.coverImage,
                    contentDescription = "Petition cover",
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.fillMaxSize()
                )
            } else {
                Icon(
                    imageVector = Icons.Default.Description,
                    contentDescription = "Petition placeholder",
                    modifier = Modifier
                        .size(64.dp)
                        .align(Alignment.Center),
                    tint = MaterialTheme.colorScheme.surface
                )
            }
        }

        // Basic Info
        Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
            Text(
                text = petition.title,
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = stringResource(R.string.county, petition.county),
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
            )
        }

        // Description
        Text(
            text = petition.description,
            style = MaterialTheme.typography.bodyMedium
        )

        // Request Goals
        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Text(
                text = stringResource(R.string.request_goals),
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold
            )
            Column {
                petition.requestGoals.forEach { goal ->
                    Row(
                        modifier = Modifier.padding(vertical = 4.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.CheckCircle,
                            contentDescription = null,
                            modifier = Modifier.size(16.dp),
                            tint = MaterialTheme.colorScheme.primary
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = goal,
                            style = MaterialTheme.typography.bodyMedium
                        )
                    }
                }
            }
        }

        // Signature Progress
        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = stringResource(R.string.signatures),
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold
                )
                Text(
                    text = stringResource(R.string.days_left, petition.daysToExpiry()),
                    style = MaterialTheme.typography.bodyMedium,
                    color = if (petition.daysToExpiry() < 3) MaterialTheme.colorScheme.error
                    else MaterialTheme.colorScheme.onSurface
                )
            }

            AnimatedProgressIndicator(percentage = progress)

            /*LinearProgressIndicator(
                progress = animatedProgress,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(8.dp),
                color = MaterialTheme.colorScheme.primary,
                trackColor = MaterialTheme.colorScheme.surfaceVariant
            )*/

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = stringResource(R.string.signatures_signed, petition.signatures.size),
                    style = MaterialTheme.typography.bodyMedium
                )
                Text(
                    text = stringResource(R.string.goal, petition.signatureGoal),
                    style = MaterialTheme.typography.bodyMedium
                )
            }

            if (hasSigned) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.Verified,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = stringResource(R.string.you_ve_signed_this_petition),
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.primary
                    )
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun EditPetitionBottomSheet(
    petition: Petition,
    onDismiss: () -> Unit,
    onSave: (String, String, String, List<String>) -> Unit,
    onDelete: () -> Unit
) {
    var title by remember { mutableStateOf(petition.title) }
    var description by remember { mutableStateOf(petition.description) }
    var goals by remember { mutableStateOf(petition.requestGoals.joinToString("\n")) }
    var coverImage by remember {mutableStateOf(petition.coverImage)}

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
                text = stringResource(R.string.edit_petition),
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold
            )
            val imagePicker = rememberLauncherForActivityResult(
                contract = ActivityResultContracts.GetContent()
            ) { uri: Uri? ->
                uri?.let { coverImage = it.toString() }
            }

            PolicyCoverImageSection(
                imageUri = coverImage.toUri(),
                onImageClick = { imagePicker.launch("image/*") }
            )
            OutlinedTextField(
                value = title,
                onValueChange = { title = it },
                modifier = Modifier.fillMaxWidth(),
                label = { Text(stringResource(R.string.title)) },
                singleLine = true
            )

            OutlinedTextField(
                value = description,
                onValueChange = { description = it },
                modifier = Modifier
                    .fillMaxWidth()
                    .heightIn(min = 120.dp),
                label = { Text(stringResource(R.string.description)) }
            )

            OutlinedTextField(
                value = goals,
                onValueChange = { goals = it },
                modifier = Modifier
                    .fillMaxWidth()
                    .heightIn(min = 100.dp),
                label = { Text(stringResource(R.string.request_goals_one_per_line)) }
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Button(
                    onClick = {
                        onSave(
                            title,
                            description,
                            coverImage,
                            goals.split("\n").map { it.trim() }.filter { it.isNotEmpty() },

                        )
                    },
                    modifier = Modifier.weight(1f),
                    enabled = title.isNotEmpty() && description.isNotEmpty()
                ) {
                    Text(stringResource(R.string.save_changes))
                }

                Button(
                    onClick = onDelete,
                    modifier = Modifier.weight(1f),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.errorContainer,
                        contentColor = MaterialTheme.colorScheme.onErrorContainer
                    )
                ) {
                    Text(stringResource(R.string.delete_petition))
                }
            }
        }
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
            Text(stringResource(R.string.retry))
        }
    }
}