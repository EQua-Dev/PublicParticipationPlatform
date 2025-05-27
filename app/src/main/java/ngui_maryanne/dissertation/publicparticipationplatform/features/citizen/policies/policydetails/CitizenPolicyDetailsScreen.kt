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
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.Divider
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.ErrorOutline
import androidx.compose.material.icons.filled.ExpandLess
import androidx.compose.material.icons.filled.ExpandMore
import androidx.compose.material.icons.filled.Policy
import androidx.compose.material3.AssistChip
import androidx.compose.material3.AssistChipDefaults
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.MaterialTheme.colorScheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Snackbar
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import coil.compose.AsyncImage
import ngui_maryanne.dissertation.publicparticipationplatform.R
import ngui_maryanne.dissertation.publicparticipationplatform.components.PolicyTimelineStepper
import ngui_maryanne.dissertation.publicparticipationplatform.data.enums.PolicyStatus
import ngui_maryanne.dissertation.publicparticipationplatform.data.models.Comment
import ngui_maryanne.dissertation.publicparticipationplatform.data.models.Poll
import ngui_maryanne.dissertation.publicparticipationplatform.data.models.StatusChange
import ngui_maryanne.dissertation.publicparticipationplatform.features.common.auth.presentation.login.KenyanBackgroundPattern
import ngui_maryanne.dissertation.publicparticipationplatform.navigation.Screen
import ngui_maryanne.dissertation.publicparticipationplatform.utils.HelpMe
import ngui_maryanne.dissertation.publicparticipationplatform.utils.HelpMe.getDate

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CitizenPolicyDetailsScreen(
    modifier: Modifier = Modifier,
    navController: NavHostController,
    policyId: String,
    viewModel: PolicyDetailsViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }
    val scrollState = rememberScrollState()

    LaunchedEffect(policyId) {
        viewModel.handleAction(PolicyDetailsAction.LoadPolicy(policyId))
    }

    LaunchedEffect(Unit) {
        viewModel.events.collect { event ->
            when (event) {
                PolicyDetailsEvent.SetupCommentsListener -> Unit // Handled by ViewModel
                PolicyDetailsEvent.NavigateBack -> navController.popBackStack()
                is PolicyDetailsEvent.NavigateToPollDetails -> {
                    navController.navigate(
                        Screen.PollDetailsScreen.route.replace(
                            "{pollId}",
                            event.pollId
                        )
                    )
                }

                is PolicyDetailsEvent.ShowSnackbar -> {
                    snackbarHostState.showSnackbar(event.message)
                }

                PolicyDetailsEvent.DescriptionExpanded -> TODO()
                is PolicyDetailsEvent.PolicyStatusUpdateError -> TODO()
                PolicyDetailsEvent.PolicyStatusUpdated -> TODO()
                PolicyDetailsEvent.SetupPollsListener -> TODO()
                is PolicyDetailsEvent.SubmitCommentError -> TODO()
                PolicyDetailsEvent.SubmitCommentSuccess -> TODO()
                PolicyDetailsEvent.TimelineExpanded -> TODO()
            }
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
    ) {

        Scaffold(
            modifier = modifier,
            topBar = {
                CenterAlignedTopAppBar(
                    modifier = Modifier.background(MaterialTheme.colorScheme.surface),
                    title = {
                        Text(
                            uiState.policy?.policyTitle ?: stringResource(R.string.policy_details),
                            style = MaterialTheme.typography.headlineSmall.copy(
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onBackground
                            ),
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    },
                    navigationIcon = {
                        IconButton(
                            onClick = { viewModel.handleAction(PolicyDetailsAction.OnBackClicked) }
                        ) {
                            Icon(
                                Icons.Default.ArrowBack,
                                contentDescription = "Back",
                                tint = MaterialTheme.colorScheme.onBackground
                            )
                        }
                    },
                    colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                        containerColor = Color.Transparent,
                        scrolledContainerColor = MaterialTheme.colorScheme.surface.copy(alpha = 0.8f)
                    ),
                    actions = {
                        uiState.policy?.let { policy ->
                            PolicyStatusBadge(status = policy.policyStatus)
                        }
                    }
                )
            },
            containerColor = Color.Transparent,
            snackbarHost = {
                SnackbarHost(
                    hostState = snackbarHostState,
                    modifier = Modifier.padding(16.dp)
                ) { data ->
                    Snackbar(
                        containerColor = MaterialTheme.colorScheme.errorContainer,
                        contentColor = MaterialTheme.colorScheme.onErrorContainer,
                        snackbarData = data
                    )
                }
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
                        CircularProgressIndicator(
                            color = MaterialTheme.colorScheme.primary
                        )
                    }
                }

                uiState.error != null -> {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(paddingValues),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.spacedBy(16.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.ErrorOutline,
                                contentDescription = null,
                                modifier = Modifier.size(48.dp),
                                tint = MaterialTheme.colorScheme.error
                            )
                            Text(
                                text = stringResource(R.string.failed_to_load_policy_details),
                                style = MaterialTheme.typography.bodyLarge
                            )
                            Text(
                                text = uiState.error ?: "Unknown error",
                                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                            )
                            Button(
                                onClick = {
                                    viewModel.handleAction(
                                        PolicyDetailsAction.LoadPolicy(
                                            policyId
                                        )
                                    )
                                }
                            ) {
                                Text(stringResource(id = R.string.retry))
                            }
                        }
                    }
                }

                uiState.policy == null -> {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(paddingValues),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.spacedBy(16.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Policy,
                                contentDescription = null,
                                modifier = Modifier.size(48.dp),
                                tint = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f)
                            )
                            Text(stringResource(R.string.policy_not_found))
                        }
                    }
                }

                else -> {
                    val policy = uiState.policy!!

                    Column(
                        modifier = Modifier
                            .verticalScroll(scrollState)
                            .padding(paddingValues)
                    ) {
                        // Policy cover image
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .aspectRatio(16f / 9f)
                                .background(MaterialTheme.colorScheme.surface)
                        ) {
                            if (policy.policyCoverImage.isNotEmpty()) {
                                AsyncImage(
                                    model = policy.policyCoverImage,
                                    contentDescription = "Policy cover image",
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
                                    tint = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }

                            Box(
                                modifier = Modifier
                                    .align(Alignment.BottomStart)
                                    .padding(16.dp)
                            ) {
                                PolicyStatusBadge(status = policy.policyStatus)
                            }
                        }

                        // Policy content
                        Column(
                            modifier = Modifier.padding(16.dp),
                            verticalArrangement = Arrangement.spacedBy(24.dp)
                        ) {
                            // Basic info row
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = policy.policySector,
                                    style = MaterialTheme.typography.labelLarge.copy(
                                        color = MaterialTheme.colorScheme.primary
                                    )
                                )

                                Text(
                                    text = stringResource(
                                        R.string.published, getDate(
                                            policy.dateCreated.toLong(),
                                            "dd MMM yyyy"
                                        )
                                    ),
                                    style = MaterialTheme.typography.labelSmall,
                                    color = MaterialTheme.colorScheme.outline
                                )
                            }

                            // Description section
                            ExpandableSection(
                                title = stringResource(id = R.string.description),
                                content = policy.policyDescription,
                                isExpanded = uiState.isDescriptionExpanded,
                                onToggleExpand = { viewModel.handleAction(PolicyDetailsAction.ToggleDescriptionExpanded) }
                            )

                            // Timeline section
                            ExpandableSection(
                                title = stringResource(R.string.policy_timeline),
                                isExpanded = uiState.isTimelineExpanded,
                                onToggleExpand = { viewModel.handleAction(PolicyDetailsAction.ToggleTimelineExpanded) }
                            ) {
                                PolicyTimeline(
                                    statusHistory = policy.statusHistory,
                                    currentStatus = policy.policyStatus
                                )
                            }

                            // Public participation section
                            if (uiState.polls.isNotEmpty()) {
                                SectionTitle(stringResource(R.string.public_participation))
                                LazyColumn(
                                    verticalArrangement = Arrangement.spacedBy(8.dp),
                                    modifier = Modifier.heightIn(max = 300.dp)
                                ) {
                                    items(uiState.polls) { poll ->
                                        PollCard(
                                            poll = poll,
                                            onClick = {
                                                viewModel.handleAction(
                                                    PolicyDetailsAction.OnPollClicked(poll.id)
                                                )
                                            }
                                        )
                                    }
                                }
                            }

                            // Comments section
                            CommentsSection(
                                comments = uiState.comments,
                                newCommentText = uiState.newCommentText,
                                isAnonymous = uiState.isAnonymous,
                                canComment = policy.policyStatus == PolicyStatus.PUBLIC_CONSULTATION,
                                onCommentTextChanged = { text ->
                                    viewModel.handleAction(
                                        PolicyDetailsAction.OnCommentTextChanged(text)
                                    )
                                },
                                onAnonymousToggled = { isAnonymous ->
                                    viewModel.handleAction(
                                        PolicyDetailsAction.OnAnonymousToggled(isAnonymous)
                                    )
                                },
                                onSubmitComment = {
                                    viewModel.handleAction(PolicyDetailsAction.SubmitComment)
                                },
                                viewModel = viewModel
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun PolicyStatusBadge(status: PolicyStatus) {
    Box(
        modifier = Modifier
            .clip(MaterialTheme.shapes.small)
            .background(
                when (status) {
                    PolicyStatus.PUBLIC_CONSULTATION -> Color(0xFF2196F3).copy(alpha = 0.2f)
                    PolicyStatus.APPROVED -> Color(0xFF4CAF50).copy(alpha = 0.2f)
                    PolicyStatus.REJECTED -> Color(0xFFF44336).copy(alpha = 0.2f)
                    else -> MaterialTheme.colorScheme.surfaceVariant
                }
            )
            .padding(horizontal = 12.dp, vertical = 6.dp)
    ) {
        Text(
            text = status.displayName,
            style = MaterialTheme.typography.labelSmall.copy(
                color = when (status) {
                    PolicyStatus.PUBLIC_CONSULTATION -> Color(0xFF2196F3)
                    PolicyStatus.APPROVED -> Color(0xFF4CAF50)
                    PolicyStatus.REJECTED -> Color(0xFFF44336)
                    else -> MaterialTheme.colorScheme.onSurface
                }
            )
        )
    }
}

@Composable
private fun ExpandableSection(
    title: String,
    isExpanded: Boolean,
    onToggleExpand: () -> Unit,
    content: @Composable () -> Unit
) {
    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = title,
                style = MaterialTheme.typography.titleMedium
            )

            IconButton(
                onClick = onToggleExpand,
                modifier = Modifier.size(24.dp)
            ) {
                Icon(
                    imageVector = if (isExpanded) Icons.Default.ExpandLess else Icons.Default.ExpandMore,
                    contentDescription = if (isExpanded) "Collapse" else "Expand"
                )
            }
        }

        if (isExpanded) {
            content()
        }
    }
}

@Composable
private fun ExpandableSection(
    title: String,
    content: String,
    isExpanded: Boolean,
    onToggleExpand: () -> Unit
) {
    ExpandableSection(
        title = title,
        isExpanded = isExpanded,
        onToggleExpand = onToggleExpand
    ) {
        Text(
            text = content,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.8f)
        )
    }
}

@Composable
private fun SectionTitle(text: String) {
    Text(
        text = text,
        style = MaterialTheme.typography.titleMedium,
        modifier = Modifier.padding(bottom = 8.dp)
    )
}

@Composable
private fun PolicyTimeline(
    statusHistory: List<StatusChange>,
    currentStatus: PolicyStatus
) {
    val allStatuses = PolicyStatus.entries
    val completedStatuses = statusHistory.map { it.status }
    val currentStep = allStatuses.indexOf(currentStatus)

    Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
        allStatuses.forEachIndexed { index, status ->
            val isCompleted = completedStatuses.contains(status)
            val isCurrent = index == currentStep
            val statusChange = statusHistory.find { it.status == status }

            Row(
                verticalAlignment = Alignment.Top,
                modifier = Modifier.fillMaxWidth()
            ) {
                // Timeline indicator
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.width(24.dp)
                ) {
                    if (isCompleted) {
                        Icon(
                            imageVector = Icons.Default.CheckCircle,
                            contentDescription = "Completed",
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(24.dp)
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

                    if (index < allStatuses.lastIndex) {
                        Divider(
                            modifier = Modifier
                                .height(24.dp)
                                .width(2.dp),
                            color = if (isCompleted) MaterialTheme.colorScheme.primary
                            else MaterialTheme.colorScheme.outline.copy(alpha = 0.2f)
                        )
                    }
                }

                // Timeline content
                Column(
                    modifier = Modifier.padding(start = 12.dp),
                    verticalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Text(
                        text = status.displayName,
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = if (isCurrent) FontWeight.Bold else FontWeight.Normal
                    )

                    statusChange?.let {
                        Text(
                            text = stringResource(
                                R.string.completed_on,
                                getDate(it.changedAt.toLong(), "dd MMM yyyy")
                            ),
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.outline
                        )
                    }

                    Text(
                        text = status.description,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                    )
                }
            }
        }
    }
}

@Composable
private fun PollCard(
    poll: Poll,
    onClick: () -> Unit
) {
    Card(
        onClick = onClick,
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        )
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Text(
                text = poll.pollQuestion,
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.SemiBold
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = stringResource(R.string.poll, poll.pollNo),
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.outline
                )

                Text(
                    text = stringResource(id = R.string.expires, getDate(poll.pollExpiry.toLong(), "EEE, dd MMM yyyy")),
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.outline
                )
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
    onSubmitComment: () -> Unit,
    viewModel: PolicyDetailsViewModel
) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        SectionTitle(stringResource(R.string.comments, comments.size))

        if (canComment) {
            Card(
                modifier = Modifier.fillMaxWidth(),
                elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    OutlinedTextField(
                        value = newCommentText,
                        onValueChange = onCommentTextChanged,
                        modifier = Modifier.fillMaxWidth(),
                        label = { Text(stringResource(R.string.add_your_comment)) },
                        shape = MaterialTheme.shapes.small,
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = MaterialTheme.colorScheme.primary,
                            unfocusedBorderColor = MaterialTheme.colorScheme.outline.copy(alpha = 0.2f)
                        ),
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
                                onCheckedChange = onAnonymousToggled,
                                colors = CheckboxDefaults.colors(
                                    checkedColor = MaterialTheme.colorScheme.primary
                                )
                            )
                            Text(
                                stringResource(R.string.post_anonymously),
                                style = MaterialTheme.typography.bodySmall
                            )
                        }

                        Button(
                            onClick = onSubmitComment,
                            enabled = newCommentText.isNotBlank(),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = MaterialTheme.colorScheme.primary
                            ),
                            shape = MaterialTheme.shapes.small
                        ) {
                            Text(stringResource(R.string.submit))
                        }
                    }
                }
            }
        }

        if (comments.isNotEmpty()) {
            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(12.dp),
                modifier = Modifier.heightIn(max = 400.dp)
            ) {
                items(comments) { comment ->
                    CommentCard(comment = comment, viewModel = viewModel)
                }
            }
        } else {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 24.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = if (canComment) stringResource(R.string.no_comments_yet_be_the_first_to_comment)
                    else stringResource(R.string.no_comments_available),
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                )
            }
        }
    }
}

@Composable
private fun CommentCard(
    comment: Comment,
    viewModel: PolicyDetailsViewModel
) {
    var displayName by remember { mutableStateOf("User ${comment.userId.take(6)}") }

    DisposableEffect(comment.userId) {
        val listener = if (!comment.anonymous) {
            viewModel.getCitizenNameRealtime(comment.userId) {
                displayName = it
            }
        } else null

        onDispose {
            listener?.remove()
        }
    }

    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        )
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = if (comment.anonymous) stringResource(R.string.anonymous) else displayName,
                    style = MaterialTheme.typography.labelMedium,
                    fontWeight = FontWeight.Bold
                )

                Text(
                    text = getDate(comment.dateCreated.toLong(), "dd MMM yyyy | hh:mm a"),
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.outline
                )
            }

            Text(
                text = comment.comment,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.8f)
            )
        }
    }
}