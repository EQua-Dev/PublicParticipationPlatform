package ngui_maryanne.dissertation.publicparticipationplatform.features.citizen.policies.policydetails

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.ExpandLess
import androidx.compose.material.icons.filled.ExpandMore
import androidx.compose.material.icons.filled.Policy
import androidx.compose.material3.AssistChip
import androidx.compose.material3.AssistChipDefaults
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.MaterialTheme.colorScheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import coil.compose.AsyncImage
import ngui_maryanne.dissertation.publicparticipationplatform.components.PolicyTimelineStepper
import ngui_maryanne.dissertation.publicparticipationplatform.data.enums.PolicyStatus
import ngui_maryanne.dissertation.publicparticipationplatform.data.models.Comment
import ngui_maryanne.dissertation.publicparticipationplatform.data.models.Poll
import ngui_maryanne.dissertation.publicparticipationplatform.data.models.StatusChange
import ngui_maryanne.dissertation.publicparticipationplatform.navigation.Screen
import ngui_maryanne.dissertation.publicparticipationplatform.utils.HelpMe

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CitizenPolicyDetailsScreen(
    modifier: Modifier = Modifier,
    navController: NavHostController,
    policyId: String,
    viewModel: PolicyDetailsViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val context = LocalContext.current
    val scrollState = rememberScrollState()

    LaunchedEffect(policyId) {
        viewModel.handleAction(PolicyDetailsAction.LoadPolicy(policyId))
    }

    LaunchedEffect(Unit) {
        viewModel.events.collect { event ->
            when (event) {
                PolicyDetailsEvent.SetupCommentsListener -> {
                    // The ViewModel will handle the actual setup
                }

                PolicyDetailsEvent.NavigateBack -> navController.popBackStack()
                is PolicyDetailsEvent.NavigateToPollDetails -> {
                    navController.navigate("poll_details/${event.pollId}")
                }
            }
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        uiState.policy?.policyTitle ?: "Loading...",
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                },
                navigationIcon = {
                    IconButton(onClick = { viewModel.handleAction(PolicyDetailsAction.OnBackClicked) }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                },
                actions = {
                    uiState.policy?.let { policy ->
                        AssistChip(
                            colors = AssistChipDefaults.assistChipColors(
                                containerColor = when (policy.policyStatus) {
                                    PolicyStatus.PUBLIC_CONSULTATION -> Color(0xFF2196F3)
                                    PolicyStatus.APPROVED -> Color(0xFF4CAF50)
                                    PolicyStatus.REJECTED -> Color(0xFFF44336)
                                    else -> MaterialTheme.colorScheme.surfaceVariant
                                }
                            ),
                            border = null,
                            shape = MaterialTheme.shapes.small,
                            onClick = {},
                            label = { Text(policy.policyStatus.displayName) },
                        )
                    }
                }
            )
        }
    ) { paddingValues ->
        when {
            uiState.isLoading -> {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator()
                }
            }

            uiState.error != null -> {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues),
                    contentAlignment = Alignment.Center
                ) {
                    Text("Error: ${uiState.error}", color = MaterialTheme.colorScheme.error)
                }
            }

            uiState.policy == null -> {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues),
                    contentAlignment = Alignment.Center
                ) {
                    Text("Policy not found")
                }
            }

            else -> {
                LazyColumn(
                    modifier = modifier
                        .fillMaxSize()
                        .padding(paddingValues)
                        .padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(24.dp)
                ) {
                    item {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .aspectRatio(16f / 9f)
                                .background(colorScheme.surfaceVariant)
                                .clip(MaterialTheme.shapes.large)
                        ) {

                            if (uiState.policy!!.policyCoverImage.isNotEmpty()) {
                                AsyncImage(
                                    model = uiState.policy!!.policyCoverImage,
                                    contentDescription = "Policy cover",
                                    contentScale = ContentScale.Crop,
                                    modifier = Modifier.fillMaxSize()
                                )
                            } else {
                                Icon(
                                    imageVector = Icons.Default.Policy,
                                    contentDescription = "Policy placeholder",
                                    modifier = Modifier
                                        .size(64.dp)
                                        .align(Alignment.Center),
                                    tint = colorScheme.onSurfaceVariant
                                )
                            }

                        }
                    }
                    // Policy description section
                    item {
                        PolicyDescriptionSection(
                            description = uiState.policy!!.policyDescription,
                            isExpanded = uiState.isDescriptionExpanded,
                            onToggleExpand = { viewModel.handleAction(PolicyDetailsAction.ToggleDescriptionExpanded) }
                        )
                    }

                    // Policy timeline section
                    uiState.policy?.let { policy ->
                        item {
                            PolicyTimelineSection(
                                statusHistory = policy.statusHistory,
                                currentStatus = policy.policyStatus,
                                isExpanded = uiState.isTimelineExpanded,
                                onToggleExpand = { viewModel.handleAction(PolicyDetailsAction.ToggleTimelineExpanded) }
                            )
                        }
                    }

                    // Public participation (polls) section
                    if (uiState.polls.isNotEmpty()) {
                        item {
                            PublicParticipationSection(
                                polls = uiState.polls,
                                onPollClicked = { pollId ->
                                    navController.navigate(
                                        Screen.PollDetailsScreen.route.replace(
                                            "{pollId}",
                                            pollId
                                        )
                                    )
                                    /* viewModel.handleAction(
                                         PolicyDetailsAction.OnPollClicked(
                                             pollId
                                         )
                                     )*/
                                }
                            )
                        }
                    }
                    // Comments section
                    item {
                        CommentsSection(
                            comments = uiState.comments,
                            newCommentText = uiState.newCommentText,
                            isAnonymous = uiState.isAnonymous,
                            canComment = uiState.policy!!.policyStatus == PolicyStatus.PUBLIC_CONSULTATION,
                            onCommentTextChanged = { text ->
                                viewModel.handleAction(
                                    PolicyDetailsAction.OnCommentTextChanged(
                                        text
                                    )
                                )
                            },
                            onAnonymousToggled = { isAnonymous ->
                                viewModel.handleAction(
                                    PolicyDetailsAction.OnAnonymousToggled(
                                        isAnonymous
                                    )
                                )
                            },
                            onSubmitComment = { viewModel.handleAction(PolicyDetailsAction.SubmitComment) }
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun PolicyDescriptionSection(
    description: String,
    isExpanded: Boolean,
    onToggleExpand: () -> Unit
) {
    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        Text(
            text = "Description",
            style = MaterialTheme.typography.titleMedium
        )

        Text(
            text = if (isExpanded) description else description.take(200) + if (description.length > 200) "..." else "",
            style = MaterialTheme.typography.bodyMedium
        )

        if (description.length > 200) {
            TextButton(
                onClick = onToggleExpand,
                modifier = Modifier.padding(top = 4.dp)
            ) {
                Text(if (isExpanded) "See less" else "See more")
            }
        }
    }
}

@Composable
private fun PolicyTimelineSection(
    statusHistory: List<StatusChange>,
    currentStatus: PolicyStatus,
    isExpanded: Boolean,
    onToggleExpand: () -> Unit
) {
    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Policy Timeline",
                style = MaterialTheme.typography.titleMedium
            )

            IconButton(onClick = onToggleExpand) {
                Icon(
                    imageVector = if (isExpanded) Icons.Default.ExpandLess else Icons.Default.ExpandMore,
                    contentDescription = if (isExpanded) "Collapse" else "Expand"
                )
            }
        }

        if (isExpanded) {
            val allStatuses = PolicyStatus.entries
            val completedStatuses = statusHistory.map { it.status }

            PolicyTimelineStepper(
                steps = allStatuses.size,
                currentStep = allStatuses.indexOf(currentStatus),
                content = { step, isCurrent ->
                    val status = allStatuses[step]
                    val isCompleted = completedStatuses.contains(status)
                    val statusChange = statusHistory.find { it.status == status }

                    Column(
                        modifier = Modifier.padding(start = 16.dp, bottom = 24.dp),
                        verticalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            if (isCompleted) {
                                Icon(
                                    imageVector = Icons.Default.CheckCircle,
                                    contentDescription = "Completed",
                                    tint = MaterialTheme.colorScheme.primary
                                )
                            } else {
                                Box(
                                    modifier = Modifier
                                        .size(24.dp)
                                        .border(
                                            width = 2.dp,
                                            color = if (isCurrent) MaterialTheme.colorScheme.primary
                                            else MaterialTheme.colorScheme.outline,
                                            shape = CircleShape
                                        )
                                )
                            }

                            Text(
                                text = status.displayName,
                                style = MaterialTheme.typography.bodyMedium,
                                fontWeight = if (isCurrent) FontWeight.Bold else FontWeight.Normal
                            )
                        }

                        statusChange?.let {
                            Text(
                                text = "Completed on ${
                                    HelpMe.getDate(
                                        it.changedAt.toLong(),
                                        "EEE dd MMM yyyy | hh:mm a"
                                    )
                                }",
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.outline
                            )
                        }

                        Text(
                            text = status.description,
                            style = MaterialTheme.typography.bodySmall,
                            modifier = Modifier.padding(start = 32.dp)
                        )
                    }
                }
            )
        }
    }
}

@Composable
private fun PublicParticipationSection(
    polls: List<Poll>,
    onPollClicked: (String) -> Unit
) {
    Column(
        verticalArrangement = Arrangement.spacedBy(8.dp),
        modifier = Modifier.heightIn(max = 400.dp)
    ) {
        Text(
            text = "Public Participation",
            style = MaterialTheme.typography.titleMedium
        )

        LazyColumn(
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(polls) { poll ->
                Card(
                    onClick = { onPollClicked(poll.id) },
                    modifier = Modifier.fillMaxWidth(),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Text(
                            text = poll.pollQuestion,
                            style = MaterialTheme.typography.bodyMedium,
                            fontWeight = FontWeight.Bold
                        )

                        Text(
                            text = "Poll #${poll.pollNo} • Expires: ${poll.pollExpiry}",
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.outline
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun CommentsSection(
    comments: List<Comment>,
    newCommentText: String,
    isAnonymous: Boolean,
    canComment: Boolean,
    onCommentTextChanged: (String) -> Unit,
    onAnonymousToggled: (Boolean) -> Unit,
    onSubmitComment: () -> Unit
) {
    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        Text(
            text = "Comments (${comments.size})",
            style = MaterialTheme.typography.titleMedium
        )

        if (canComment) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 8.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                OutlinedTextField(
                    value = newCommentText,
                    onValueChange = onCommentTextChanged,
                    modifier = Modifier.fillMaxWidth(),
                    label = { Text("Add your comment") },
                    maxLines = 3
                )

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Checkbox(
                            checked = isAnonymous,
                            onCheckedChange = onAnonymousToggled
                        )
                        Text("Post anonymously")
                    }

                    Button(
                        onClick = onSubmitComment,
                        enabled = newCommentText.isNotBlank()
                    ) {
                        Text("Submit")
                    }
                }
            }
        }
        Column(
            modifier = Modifier.heightIn(max = 400.dp) // or some other appropriate max height
        ) {
            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(comments) { comment ->
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
                    ) {
                        Column(
                            modifier = Modifier.padding(12.dp),
                            verticalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            Row(
                                horizontalArrangement = Arrangement.SpaceBetween,
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Text(
                                    text = if (comment.isAnonymous) "Anonymous" else "User ${
                                        comment.userId.take(
                                            6
                                        )
                                    }",
                                    style = MaterialTheme.typography.labelMedium,
                                    fontWeight = FontWeight.Bold
                                )

                                Text(
                                    text = comment.dateCreated,
                                    style = MaterialTheme.typography.labelSmall,
                                    color = MaterialTheme.colorScheme.outline
                                )
                            }

                            Text(
                                text = comment.comment,
                                style = MaterialTheme.typography.bodyMedium
                            )
                        }
                    }
                }
            }
        }
    }
}