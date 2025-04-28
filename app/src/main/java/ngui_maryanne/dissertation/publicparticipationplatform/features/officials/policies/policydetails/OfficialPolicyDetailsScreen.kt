package ngui_maryanne.dissertation.publicparticipationplatform.features.officials.policies.policydetails

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import ngui_maryanne.dissertation.publicparticipationplatform.components.CommentItem
import ngui_maryanne.dissertation.publicparticipationplatform.components.ErrorState
import ngui_maryanne.dissertation.publicparticipationplatform.components.PolicyStageTracker
import ngui_maryanne.dissertation.publicparticipationplatform.components.StageUpdateDialog
import ngui_maryanne.dissertation.publicparticipationplatform.data.enums.PolicyStatus
import ngui_maryanne.dissertation.publicparticipationplatform.features.officials.policies.policydetails.components.EditPolicyBottomSheet
import ngui_maryanne.dissertation.publicparticipationplatform.features.officials.policies.policydetails.components.PolicyDetailsSection
import ngui_maryanne.dissertation.publicparticipationplatform.features.officials.policies.policydetails.components.PollCard

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun OfficialPolicyDetailsScreen(
    policyId: String,
    modifier: Modifier = Modifier,
    navController: NavHostController,
    viewModel: OfficialPolicyDetailsViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsState()
    val context = LocalContext.current
    var showEditBottomSheet by remember { mutableStateOf(false) }


    LaunchedEffect(policyId) {
        viewModel.onEvent(OfficialPolicyDetailsEvent.LoadData(policyId))
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(state.policy?.policyTitle ?: "Policy Details") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = { showEditBottomSheet = true }
            ) {
                Icon(Icons.Default.Edit, contentDescription = "Edit Policy")
            }
        }
    ) { paddingValues ->
        Box(modifier = modifier.fillMaxSize()) {
            when {
                state.isLoading -> {
                    CircularProgressIndicator(
                        modifier = Modifier.align(Alignment.Center),
                        color = MaterialTheme.colorScheme.primary
                    )
                }

                state.error != null -> ErrorState(
                    message = state.error!!,
                    onRetry = { viewModel.onEvent(OfficialPolicyDetailsEvent.LoadData(policyId)) },
//                    onDismiss = { viewModel.onEvent(OfficialPolicyDetailsEvent.DismissError) }
                )

                else -> PolicyDetailsContent(
                    state = state,
                    onEvent = viewModel::onEvent,
                    modifier = Modifier.padding(paddingValues)
                )
            }
        }
    }

    if (showEditBottomSheet) {
        EditPolicyBottomSheet(
            policy = state.policy,
            onDismiss = { showEditBottomSheet = false },
            onSave = { name, imageUrl, otherDetails ->
                viewModel.onEvent(OfficialPolicyDetailsEvent.UpdatePolicy(name, imageUrl, otherDetails))
                showEditBottomSheet = false
            },
            onDelete = {
                viewModel.onEvent(OfficialPolicyDetailsEvent.DeletePolicy)
                showEditBottomSheet = false
            }
        )
    }
}

@Composable
private fun PolicyDetailsContent(
    state: OfficialPolicyDetailsState,
    onEvent: (OfficialPolicyDetailsEvent) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
    ) {
        // Policy Details Section
        state.policy?.let { policy ->
            PolicyDetailsSection(policy = policy)
        }

        // Policy Stage Tracker
        PolicyStageTracker(
            currentStage = state.currentStage,
            canUpdateStage = state.canUpdateStage,
            onStageChange = { onEvent(OfficialPolicyDetailsEvent.ShowStageDialog) }
        )


        // Polls Section
        if (state.polls.isNotEmpty()) {
            Text(
                "Public Polls",
                style = MaterialTheme.typography.titleSmall,
                modifier = Modifier.padding(16.dp)
            )
            state.polls.forEach { poll ->
                PollCard(
                    poll = poll,
                    onClick = { /* Navigate to poll details */ }
                )
            }
        }

        // Comments Section
        Text(
            "Public Comments",
            style = MaterialTheme.typography.titleSmall,
            modifier = Modifier.padding(16.dp)
        )
        if (state.comments.isNotEmpty()) {
            state.comments.forEach { comment ->
                CommentItem(comment = comment)
            }
        } else {
            Text(
                "No comments yet",
                style = MaterialTheme.typography.bodyMedium,
                modifier = Modifier.padding(16.dp)
            )
        }
    }

    // Stage Update Dialog
    if (state.showStageUpdateDialog) {
        StageUpdateDialog(
            currentStage = state.currentStage,
            onDismiss = { onEvent(OfficialPolicyDetailsEvent.DismissStageDialog) },
            onConfirm = { newStage ->
                onEvent(OfficialPolicyDetailsEvent.UpdateStage(newStage))
            }
        )
    }
}

