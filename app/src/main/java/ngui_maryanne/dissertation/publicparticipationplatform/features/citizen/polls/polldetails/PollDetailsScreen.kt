package ngui_maryanne.dissertation.publicparticipationplatform.features.citizen.polls.polldetails

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.ErrorOutline
import androidx.compose.material.icons.filled.Policy
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.fragment.app.FragmentActivity
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import ngui_maryanne.dissertation.publicparticipationplatform.components.AnimatedProgressIndicator
import ngui_maryanne.dissertation.publicparticipationplatform.components.getTimeLeft
import ngui_maryanne.dissertation.publicparticipationplatform.data.enums.UserRole
import ngui_maryanne.dissertation.publicparticipationplatform.data.models.Policy
import ngui_maryanne.dissertation.publicparticipationplatform.data.models.Poll
import ngui_maryanne.dissertation.publicparticipationplatform.data.models.PollOption
import ngui_maryanne.dissertation.publicparticipationplatform.data.models.PollResponses
import ngui_maryanne.dissertation.publicparticipationplatform.features.citizen.polls.presentation.PollStatus
import ngui_maryanne.dissertation.publicparticipationplatform.navigation.Screen
import ngui_maryanne.dissertation.publicparticipationplatform.utils.findActivity


@RequiresApi(Build.VERSION_CODES.P)
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PollDetailsScreen(
    pollId: String,
    navController: NavHostController,
    viewModel: PollDetailsViewModel = hiltViewModel()
) {
    val state by viewModel.uiState.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }
    val context = LocalContext.current
    val activity = context.findActivity() as? FragmentActivity

    LaunchedEffect(pollId) {
        viewModel.onEvent(PollDetailsEvent.LoadPollDetails(pollId))
    }

    // Track snackbar messages
    var snackbarMessage by remember { mutableStateOf<String?>(null) }

    // Show snackbar when message changes
    LaunchedEffect(snackbarMessage) {
        snackbarMessage?.let { message ->
            snackbarHostState.showSnackbar(message)
            snackbarMessage = null // Clear after showing
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Poll Details") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        },
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            when {
                state.isLoading -> FullScreenLoading()
                state.error != null -> ErrorState(
                    error = state.error!!,
                    onRetry = { viewModel.onEvent(PollDetailsEvent.Retry) }
                )

                state.poll != null && state.policy != null -> {
                    PollDetailsContent(
                        poll = state.poll!!,
                        policy = state.policy!!,
                        currentUserRole = state.currentUserRole,
                        votedOptionId = state.votedOptionId,
                        onVote = { option ->
                            activity?.let {
                                viewModel.verifyAndVoteOption(
                                    activity = it,
                                    optionId = option.optionId,
                                    optionName = option.optionText,
                                    hashType = "SHA-256",
                                    isAnonymous = false,
                                    onSuccess = {
                                        viewModel.onEvent(
                                            PollDetailsEvent.LoadPollDetails(pollId)
                                        )
                                        snackbarMessage = "Vote recorded successfully"
                                    },
                                    onFailure = { error ->
                                        snackbarMessage = error
                                    }
                                )
                            } ?: run { snackbarMessage = "Could not authenticate vote" }
                        },
                        onViewPolicy = {
                            navController.navigate(
                                Screen.CitizenPolicyDetailsScreen.route.replace(
                                    "{policyId}",
                                    state.policy!!.id
                                )
                            )
                        },
                        modifier = Modifier.padding(16.dp)
                    )
                }
            }
        }
    }
}

@RequiresApi(Build.VERSION_CODES.O)
@Composable
private fun PollDetailsContent(
    poll: Poll,
    policy: Policy,
    currentUserRole: String,
    votedOptionId: String?,
    onVote: (PollOption) -> Unit,
    onViewPolicy: () -> Unit,
    modifier: Modifier = Modifier
) {
    val timeLeft = remember { getTimeLeft(poll.pollExpiry.toLong()) }
    val totalVotes = poll.responses.size
    val pollStatus =
        if (poll.pollExpiry.toLong() > System.currentTimeMillis()) PollStatus.ACTIVE else PollStatus.CLOSED

    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Poll Status and Time
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            PollStatusChip(status = pollStatus)
            Text(
                text = if (pollStatus == PollStatus.ACTIVE) "Expires in: $timeLeft"
                else "Closed",
                color = if (pollStatus == PollStatus.ACTIVE) MaterialTheme.colorScheme.error
                else MaterialTheme.colorScheme.outline,
                style = MaterialTheme.typography.labelLarge
            )
        }

        // Related Policy Card
        PolicyCard(
            policy = policy,
            onClick = onViewPolicy,
            modifier = Modifier.fillMaxWidth()
        )

        // Poll Question
        Text(
            text = poll.pollQuestion,
            style = MaterialTheme.typography.titleSmall,
            fontWeight = FontWeight.Bold
        )

        // Poll Options
        Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
            poll.pollOptions.forEach { option ->
                PollOptionCard(
                    option = option,
                    totalVotes = totalVotes,
                    isSelected = votedOptionId == option.optionId,
                    canVote = currentUserRole.equals(UserRole.CITIZEN.name, ignoreCase = true) &&
                            pollStatus == PollStatus.ACTIVE &&
                            votedOptionId == null,
                    onVote = { onVote(option) },
                    modifier = Modifier.fillMaxWidth(),
                    allThisVotes = poll.responses.filter { it.optionId == option.optionId }
                )
            }
        }

        // Poll Statistics
        if (totalVotes > 0) {
            PollStatistics(
                totalVotes = totalVotes,
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}

@Composable
private fun PolicyCard(
    policy: Policy,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        onClick = onClick,
        modifier = modifier,
        elevation = CardDefaults.cardElevation(2.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface,
            contentColor = MaterialTheme.colorScheme.onSurface
        ),
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = "Related Policy",
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.primary
            )
            Spacer(Modifier.height(8.dp))
            Text(
                text = policy.policyName,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold
            )
            Spacer(Modifier.height(4.dp))
            Text(
                text = policy.policyDescription,
                style = MaterialTheme.typography.bodySmall,
                maxLines = 3,
                overflow = TextOverflow.Ellipsis
            )
        }
    }
}

@Composable
private fun PollOptionCard(
    option: PollOption,
    totalVotes: Int,
    allThisVotes: List<PollResponses>,
    isSelected: Boolean,
    canVote: Boolean,
    onVote: () -> Unit,
    modifier: Modifier = Modifier
) {
    val votes = allThisVotes.size
    val percentage = if (totalVotes > 0) votes * 100f / totalVotes else 0f

    Card(
        modifier = modifier,
        elevation = CardDefaults.cardElevation(1.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface,
            contentColor = MaterialTheme.colorScheme.onSurface
        ),
        border = if (isSelected) BorderStroke(
            2.dp,
            MaterialTheme.colorScheme.primary
        ) else null
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            // Option Text
            Text(
                text = option.optionText,
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.SemiBold
            )

            // Option Explanation
            if (option.optionExplanation.isNotBlank()) {
                Spacer(Modifier.height(4.dp))
                Text(
                    text = option.optionExplanation,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                )
            }

            // Progress Bar
            Spacer(Modifier.height(8.dp))
            AnimatedProgressIndicator(percentage = percentage, isSelected)
           /* androidx.compose.material3.LinearProgressIndicator(
                progress = percentage / 100f,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(4.dp),
                color = if (isSelected) MaterialTheme.colorScheme.primary
                else MaterialTheme.colorScheme.primary.copy(alpha = 0.5f),
                trackColor = MaterialTheme.colorScheme.surfaceVariant
            )*/

            // Vote Info and Button
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 8.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = if (totalVotes > 0) "${percentage.toInt()}% ($votes votes)"
                    else "No votes yet",
                    style = MaterialTheme.typography.labelSmall
                )

                if (canVote) {
                    Button(
                        onClick = onVote,
                        modifier = Modifier.height(36.dp),
                        contentPadding = PaddingValues(horizontal = 16.dp)
                    ) {
                        Text("Vote")
                    }
                } else if (isSelected) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.CheckCircle,
                            contentDescription = "Voted",
                            tint = MaterialTheme.colorScheme.primary
                        )
                        Text(
                            text = "Your vote",
                            style = MaterialTheme.typography.labelMedium,
                            color = MaterialTheme.colorScheme.primary
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun PollStatistics(
    totalVotes: Int,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier,
        elevation = CardDefaults.cardElevation(2.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = "Poll Statistics",
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.primary
            )
            Spacer(Modifier.height(8.dp))
            Text("Total votes: $totalVotes")
        }
    }
}

@Composable
private fun PollStatusChip(status: PollStatus) {
    Box(
        modifier = Modifier
            .clip(MaterialTheme.shapes.small)
            .background(
                when (status) {
                    PollStatus.ACTIVE -> MaterialTheme.colorScheme.primary.copy(alpha = 0.2f)
                    PollStatus.CLOSED -> MaterialTheme.colorScheme.error.copy(alpha = 0.2f)
                    PollStatus.DRAFT -> MaterialTheme.colorScheme.outline.copy(alpha = 0.2f)
                }
            )
            .padding(horizontal = 12.dp, vertical = 6.dp)
    ) {
        Text(
            text = status.displayName,
            style = MaterialTheme.typography.labelSmall.copy(
                color = when (status) {
                    PollStatus.ACTIVE -> MaterialTheme.colorScheme.primary
                    PollStatus.CLOSED -> MaterialTheme.colorScheme.error
                    PollStatus.DRAFT -> MaterialTheme.colorScheme.outline
                }
            )
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
            contentDescription = "Error",
            modifier = Modifier.size(48.dp),
            tint = MaterialTheme.colorScheme.error
        )
        Spacer(Modifier.height(16.dp))
        Text(
            text = error,
            style = MaterialTheme.typography.bodyLarge,
            textAlign = TextAlign.Center
        )
        Spacer(Modifier.height(16.dp))
        Button(onClick = onRetry) {
            Text("Retry")
        }
    }
}