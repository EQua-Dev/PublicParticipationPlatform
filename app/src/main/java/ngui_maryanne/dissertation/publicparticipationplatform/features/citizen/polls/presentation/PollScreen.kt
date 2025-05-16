package ngui_maryanne.dissertation.publicparticipationplatform.features.citizen.polls.presentation

import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.BorderStroke
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
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Poll
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.FloatingActionButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
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
import ngui_maryanne.dissertation.publicparticipationplatform.components.AnimatedProgressIndicator
import ngui_maryanne.dissertation.publicparticipationplatform.data.models.Poll
import ngui_maryanne.dissertation.publicparticipationplatform.features.common.auth.presentation.login.KenyanBackgroundPattern
import ngui_maryanne.dissertation.publicparticipationplatform.features.officials.polls.PollViewModel
import ngui_maryanne.dissertation.publicparticipationplatform.navigation.Screen
import ngui_maryanne.dissertation.publicparticipationplatform.utils.HelpMe.getDate


@RequiresApi(Build.VERSION_CODES.O)
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CitizenPollsScreen(
    modifier: Modifier = Modifier,
    navController: NavHostController,
    viewModel: CitizenPollsViewModel = hiltViewModel(),
    officialViewModel: PollViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val officialUiState by officialViewModel.state.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }
    val scrollState = rememberScrollState()
    val fabScrollState = rememberLazyListState()
    val showFab by remember { derivedStateOf { fabScrollState.firstVisibleItemIndex == 0 } }


    LaunchedEffect(Unit) {
        viewModel.events.collect { event ->
            when (event) {
                is CitizenPollsEvent.OnPollClicked -> {
                    navController.navigate(
                        Screen.PollDetailsScreen.route.replace(
                            "{pollId}",
                            event.poll.poll.id
                        )
                    )
                }

                else -> Unit
            }
        }
    }

    // Handle initial load and errors
//    LaunchedEffect(Unit) {
//        if (uiState.polls.isEmpty() && !uiState.isLoading) {
//            viewModel.onEvent(CitizenPollsEvent.RefreshPolls)
//        }
//    }


    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {

        Scaffold(
            modifier = modifier,
            floatingActionButton = {
                if (officialUiState.canCreatePoll) {
                    AnimatedVisibility(
                        visible = showFab,
                        enter = fadeIn() + slideInVertically { it },
                        exit = fadeOut() + slideOutVertically { it }
                    ) {

                        ExtendedFloatingActionButton(
                            onClick = { navController.navigate(Screen.CreatePollScreen.route) },
                            modifier = Modifier.padding(16.dp),
                            containerColor = MaterialTheme.colorScheme.primary,
                            contentColor = MaterialTheme.colorScheme.onPrimary,
                            elevation = FloatingActionButtonDefaults.elevation(defaultElevation = 2.dp),
                            icon = { Icon(Icons.Default.Add, contentDescription = "Create Poll") },
                            text = { Text("New Poll") }
                        )
                    }
                }
            },
            snackbarHost = {
                SnackbarHost(
                    hostState = snackbarHostState,
                    modifier = Modifier.padding(16.dp)
                )
            }
        ) { paddingValues ->

            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .padding(horizontal = 16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                item {
                    Spacer(modifier = Modifier.height(8.dp))
                }

                item {
                    // Header section
                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        Text(
                            text = "Public Polls",
                            style = MaterialTheme.typography.headlineMedium.copy(
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onBackground
                            )
                        )
                        Text(
                            text = "Participate in ongoing public consultations",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.7f)
                        )
                    }
                }

                item {          // Search and filter section
                    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                        SearchBar(
                            query = uiState.searchQuery,
                            onQueryChange = {
                                viewModel.onEvent(
                                    CitizenPollsEvent.OnSearchQueryChanged(
                                        it
                                    )
                                )
                            },
                            placeholder = "Search polls by question or policy...",
                            modifier = Modifier.fillMaxWidth()
                        )

                        // Status filter chips
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .horizontalScroll(rememberScrollState()),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            PollStatus.values().forEach { status ->
                                FilterChip(
                                    selected = uiState.selectedStatus == status,
                                    onClick = {
                                        viewModel.onEvent(
                                            CitizenPollsEvent.OnStatusFilterChanged(status)
                                        )
                                    },
                                    label = { Text(status.displayName) },
                                    colors = FilterChipDefaults.filterChipColors(
                                        selectedContainerColor = MaterialTheme.colorScheme.primary.copy(
                                            alpha = 0.2f
                                        ),
                                        selectedLabelColor = MaterialTheme.colorScheme.primary
                                    )
                                )
                            }
                        }
                    }
                }

                when {
                    uiState.isLoading -> {
                        item {
                            Box(
                                modifier = Modifier.fillMaxSize(),
                                contentAlignment = Alignment.Center
                            ) {
                                CircularProgressIndicator(
                                    color = MaterialTheme.colorScheme.primary
                                )
                            }
                        }

                    }

                    uiState.polls.isEmpty() -> {
                        item {
                            Box(
                                modifier = Modifier.fillMaxSize(),
                                contentAlignment = Alignment.Center
                            ) {
                                Column(
                                    horizontalAlignment = Alignment.CenterHorizontally,
                                    verticalArrangement = Arrangement.spacedBy(16.dp)
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Poll,
                                        contentDescription = null,
                                        modifier = Modifier.size(48.dp),
                                        tint = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f)
                                    )
                                    Text(
                                        text = if (uiState.searchQuery.isNotEmpty() || uiState.selectedStatus != null) {
                                            "No matching polls found"
                                        } else {
                                            "No active polls at this time"
                                        },
                                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                                    )
                                }
                            }
                        }

                    }

                    else -> {
                        // Polls list
                        /*LazyColumn(
                            verticalArrangement = Arrangement.spacedBy(16.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {*/
                        items(uiState.polls) { poll ->
                            PollCard(
                                poll = poll,
                                onClick = {
                                    navController.navigate(
                                        Screen.PollDetailsScreen.route.replace(
                                            "{pollId}",
                                            poll.poll.id
                                        )
                                    )
                                }
                            )
                        }

                    }
                }
                item {
                    Spacer(modifier = Modifier.height(32.dp))
                }

            }
        }
    }
}

@Composable
fun SearchBar(
    query: String,
    placeholder: String,
    onQueryChange: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        tonalElevation = 4.dp, // Adds subtle shadow/elevation
        shape = MaterialTheme.shapes.medium
    ) {
        TextField(
            value = query,
            onValueChange = onQueryChange,
            modifier = modifier,
            placeholder = {
                Text(
                    placeholder,
                    style = MaterialTheme.typography.labelMedium
                )
            },
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
                focusedContainerColor = MaterialTheme.colorScheme.surface,               // Keep card color when focused
                unfocusedContainerColor = MaterialTheme.colorScheme.surface,             // Same when not focused
                disabledContainerColor = MaterialTheme.colorScheme.surface,
                focusedIndicatorColor = MaterialTheme.colorScheme.primary,               // Show primary border when focused
                unfocusedIndicatorColor = MaterialTheme.colorScheme.outline.copy(alpha = 0.3f), // Subtle outline border
                disabledIndicatorColor = Color.Transparent,
                focusedTextColor = MaterialTheme.colorScheme.onSurface,
                unfocusedTextColor = MaterialTheme.colorScheme.onSurface,
                disabledTextColor = MaterialTheme.colorScheme.onSurfaceVariant,
                focusedTrailingIconColor = MaterialTheme.colorScheme.primary,
                unfocusedTrailingIconColor = MaterialTheme.colorScheme.onSurfaceVariant
            ),
            singleLine = true
        )
    }

}

@Composable
fun PollCard(
    poll: PollWithPolicyName,
    onClick: () -> Unit
) {
    val totalVotes = poll.poll.responses.size

    val pollStatus = if (poll.poll.pollExpiry.toLong() > System.currentTimeMillis()) {
        PollStatus.ACTIVE
    } else {
        PollStatus.CLOSED
    }
    val isActive = pollStatus == PollStatus.ACTIVE

    Log.d("Poll Screen", "PollCard: $poll")
    Card(
        onClick = onClick,
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface.copy(alpha = 0.9f)
        ),
        border = BorderStroke(
            1.dp,
            MaterialTheme.colorScheme.outline.copy(alpha = 0.1f)
        )
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = poll.poll.pollQuestion,
                    style = MaterialTheme.typography.titleSmall.copy(
                        fontWeight = FontWeight.SemiBold
                    ),
                    modifier = Modifier.weight(1f)
                )

                Box(
                    contentAlignment = Alignment.Center,
                    modifier = Modifier
                        .clip(MaterialTheme.shapes.small)
                        .background(
                            if (isActive) Color(0xFF2196F3).copy(alpha = 0.2f)
                            else Color(0xFF4CAF50).copy(alpha = 0.2f)
                        )
                        .padding(horizontal = 8.dp, vertical = 4.dp)
                ) {
                    Text(
                        text = pollStatus.displayName,
                        style = MaterialTheme.typography.labelSmall.copy(
                            color = if (isActive) Color(0xFF2196F3) else Color(0xFF4CAF50)
                        )
                    )
                }
            }

            Text(
                text = "Policy: ${poll.policyName}",
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.primary
            )

            // Poll options with progress bars
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                poll.poll.pollOptions.forEach { option ->
                    val votes = poll.poll.responses.count { it.optionId == option.optionId }
                    val percentage = if (totalVotes > 0) votes.toFloat() / totalVotes else 0f

                    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(
                                text = option.optionText,
                                style = MaterialTheme.typography.bodyMedium,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis,
                                modifier = Modifier
                                    .weight(0.6f)
                            )
                            Spacer(modifier = Modifier.weight(0.1f))
                            Text(
                                text = "$votes votes (${(percentage * 100).toInt()}%)",
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.outline,
                                textAlign = TextAlign.End,
                                modifier = Modifier
                                    .weight(0.3f)
                            )
                        }


                        AnimatedProgressIndicator(percentage = percentage)
                        /*LinearProgressIndicator(
                            progress = percentage,
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(4.dp),
                            color = MaterialTheme.colorScheme.primary,
                            trackColor = MaterialTheme.colorScheme.surfaceVariant
                        )*/
                    }
                }
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "${totalVotes} total votes",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.outline
                )

                Text(
                    text = "Expires: ${getDate(poll.poll.pollExpiry.toLong(), "EEE, dd MMM yyyy")}",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.outline
                )
            }
        }
    }
}

enum class PollStatus(val displayName: String) {
    ACTIVE("Active"),
    CLOSED("Closed"),
    DRAFT("Draft")
}