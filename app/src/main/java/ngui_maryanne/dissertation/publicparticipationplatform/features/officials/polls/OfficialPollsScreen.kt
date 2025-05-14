package ngui_maryanne.dissertation.publicparticipationplatform.features.officials.polls

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Policy
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.FloatingActionButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import ngui_maryanne.dissertation.publicparticipationplatform.components.EmptyState
import ngui_maryanne.dissertation.publicparticipationplatform.components.ErrorState
import ngui_maryanne.dissertation.publicparticipationplatform.data.models.Poll
import ngui_maryanne.dissertation.publicparticipationplatform.navigation.Screen
import ngui_maryanne.dissertation.publicparticipationplatform.utils.HelpMe.getDate

@Composable
fun OfficialPollsScreen(
    modifier: Modifier = Modifier,
    navController: NavHostController,
    viewModel: PollViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsState()
    val context = LocalContext.current
    val scrollState = rememberLazyListState()
    val showFab by remember { derivedStateOf { scrollState.firstVisibleItemIndex == 0 } }


    Scaffold(
        floatingActionButton = {
            if (state.canCreatePoll) {
                AnimatedVisibility(
                    visible = showFab,
                    enter = fadeIn() + slideInVertically { it },
                    exit = fadeOut() + slideOutVertically { it }
                ) {
                    FloatingActionButton(
                        onClick = { navController.navigate(Screen.CreatePollScreen.route) },
                        modifier = Modifier.padding(16.dp),
                        containerColor = MaterialTheme.colorScheme.primary,
                        contentColor = MaterialTheme.colorScheme.onSurface,
                        elevation = FloatingActionButtonDefaults.elevation(defaultElevation = 2.dp)
                    ) {
                        Icon(Icons.Default.Add, contentDescription = "Create Poll")
                    }
                }
            }
        }
    ) { paddingValues ->
        Box(
            modifier = modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            when {
                state.isLoading -> CircularProgressIndicator(Modifier.align(Alignment.Center))
                state.error != null -> ErrorState(
                    message = state.error!!,
                    onRetry = { viewModel.onEvent(PollEvent.LoadData) })

                state.polls.isEmpty() -> EmptyState(
                    icon = Icons.Default.Policy,
                    title = "No polls available"
                )

                else -> PollList(state.polls) {
                    navController.navigate(
                        Screen.PollDetailsScreen.route.replace(
                            "{pollId}",
                            it
                        )
                    )
                }
            }
        }
    }
}

@Composable
private fun PollList(polls: List<Poll>, onPollClick: (pollId: String) -> Unit) {
    LazyColumn(modifier = Modifier.padding(16.dp)) {
        items(polls) { poll ->
            PollCard(poll = poll) {
                onPollClick(it)
            }
        }
    }
}

@Composable
private fun PollCard(poll: Poll, onClick: (pollId: String) -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp)
            .clickable { onClick(poll.id) },
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface,
            contentColor = MaterialTheme.colorScheme.onSurface
        ),
        elevation = CardDefaults.cardElevation(4.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(poll.pollQuestion, style = MaterialTheme.typography.titleMedium)
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                "Expires on: ${getDate(poll.pollExpiry.toLong(), "EEE dd MMM yyyy")}",
                style = MaterialTheme.typography.bodySmall
            )
        }
    }
}