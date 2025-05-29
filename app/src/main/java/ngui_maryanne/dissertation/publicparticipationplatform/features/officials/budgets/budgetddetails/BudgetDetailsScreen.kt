package ngui_maryanne.dissertation.publicparticipationplatform.features.officials.budgets.budgetddetails

import android.os.Build
import android.widget.Toast
import androidx.annotation.RequiresApi
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
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ErrorOutline
import androidx.compose.material.icons.filled.Verified
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.fragment.app.FragmentActivity
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import coil.compose.AsyncImage
import ngui_maryanne.dissertation.publicparticipationplatform.R
import ngui_maryanne.dissertation.publicparticipationplatform.data.enums.UserRole
import ngui_maryanne.dissertation.publicparticipationplatform.data.models.Budget
import ngui_maryanne.dissertation.publicparticipationplatform.data.models.BudgetOption
import ngui_maryanne.dissertation.publicparticipationplatform.features.officials.budgets.OfficialBudgetViewModel
import ngui_maryanne.dissertation.publicparticipationplatform.utils.findActivity
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale


@RequiresApi(Build.VERSION_CODES.P)
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BudgetDetailsScreen(
    viewModel: OfficialBudgetViewModel = hiltViewModel(),
    detailsViewModel: OfficialBudgetDetailsViewModel = hiltViewModel(),
    navController: NavHostController,
    budgetId: String,
) {
    val state by viewModel.uiState.collectAsState()
    val detailsState by detailsViewModel.uiState.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }
    val showBottomSheet = remember { mutableStateOf(false) }
    val context = LocalContext.current
    val activity = remember(context) { context.findActivity() as? FragmentActivity }

    LaunchedEffect(Unit) {
        detailsViewModel.onEvent(BudgetDetailsEvent.LoadBudget(budgetId))
    }

    // Handle errors and messages
    LaunchedEffect(detailsState.error) {
        detailsState.error?.let { error ->
            snackbarHostState.showSnackbar(error)
            detailsViewModel.onEvent(BudgetDetailsEvent.OnErrorShown)
        }
    }

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        topBar = {
            TopAppBar(
                title = { Text(stringResource(R.string.budget_details)) },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface,
                    scrolledContainerColor = MaterialTheme.colorScheme.surface
                )
            )
        }
    ) { padding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            when {
                detailsState.isLoading -> FullScreenLoading()
                detailsState.budget == null -> ErrorState(
                    error = stringResource(R.string.budget_not_found),
                    onRetry = { detailsViewModel.onEvent(BudgetDetailsEvent.LoadBudget(budgetId)) }
                )
                else -> {
                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        contentPadding = PaddingValues(16.dp),
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        // Budget Header
                        item {
                            BudgetHeaderCard(detailsState.budget!!)
                        }

                        // Budget Options Title
                        item {
                            Text(
                                text = stringResource(id = R.string.budget_options, detailsState.budgetOptions.size),
                                style = MaterialTheme.typography.titleSmall
                            )
                        }

                        // Budget Options List
                        if (detailsState.budgetOptions.isEmpty()) {
                            item {
                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(vertical = 32.dp),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        text = stringResource(R.string.no_options_available_for_this_budget),
                                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                                    )
                                }
                            }
                        } else {
                            items(detailsState.budgetOptions) { option ->
                                BudgetOptionCard(
                                    option = option,
                                    isCitizen = state.currentUserRole.lowercase() == UserRole.CITIZEN.name.lowercase(),
                                    votedOptionId = detailsState.votedOptionId,
                                    onVote = {
                                        activity?.let { fragmentActivity ->
                                            detailsViewModel.verifyAndVoteOption(
                                                activity = fragmentActivity,
                                                optionId = option.optionId,
                                                optionName = option.optionProjectName,
                                                hashType = "SHA-256",
                                                isAnonymous = false,
                                                onSuccess = {
                                                    detailsViewModel.onEvent(
                                                        BudgetDetailsEvent.LoadBudget(budgetId)
                                                    )
                                                },
                                                onFailure = { error ->
                                                    Toast.makeText(context, error ?: "Voting failed", Toast.LENGTH_LONG).show()
                                                }
                                            )
                                        }
                                    }
                                )
                            }
                        }

                        // Official Actions
                        if (state.currentUserRole.lowercase() == UserRole.OFFICIAL.name.lowercase()) {
                            item {
                                Column(
                                    modifier = Modifier.fillMaxWidth(),
                                    verticalArrangement = Arrangement.spacedBy(12.dp)
                                ) {
                                    Spacer(modifier = Modifier.height(16.dp))
                                    FilledTonalButton(
                                        onClick = { detailsViewModel.onEvent(BudgetDetailsEvent.ToggleActivation) },
                                        modifier = Modifier.fillMaxWidth(),
                                        colors = ButtonDefaults.filledTonalButtonColors(
                                            containerColor = if (detailsState.budget!!.isActive ?: true) {
                                                MaterialTheme.colorScheme.errorContainer
                                            } else {
                                                MaterialTheme.colorScheme.tertiaryContainer
                                            },
                                            contentColor = if (detailsState.budget!!.isActive ?: true) {
                                                MaterialTheme.colorScheme.onErrorContainer
                                            } else {
                                                MaterialTheme.colorScheme.onTertiaryContainer
                                            }
                                        )
                                    ) {
                                        Text(
                                            text = if (detailsState.budget!!.isActive != false) stringResource(
                                                R.string.deactivate_budget
                                            ) else stringResource(R.string.activate_budget),
                                            style = MaterialTheme.typography.labelLarge
                                        )
                                    }

                                    FilledTonalButton(
                                        onClick = { showBottomSheet.value = true },
                                        modifier = Modifier.fillMaxWidth()
                                    ) {
                                        Text(stringResource(R.string.edit_budget_details))
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
    // Edit Budget Bottom Sheet
    if (showBottomSheet.value && detailsState.budget != null) {
            EditBudgetBottomSheet(
                budget = detailsState.budget!!,
                onSave = { amount, note, impact, options ->
                    detailsViewModel.submitBudgetEdit(
                        detailsState.budget!!.id,
                        amount,
                        note,
                        impact,
                        options
                    )
                    showBottomSheet.value = false
                },
                onClose = { showBottomSheet.value = false },

            )

    }
}

@Composable
private fun BudgetHeaderCard(budget: Budget) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface,
            contentColor = MaterialTheme.colorScheme.onSurface
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = stringResource(id = R.string.budget, budget.budgetNo),
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )

                val budgetActive = /*budget.budgetExpiry.toLong()> System.currentTimeMillis() || */budget.isActive
                val budgetStatus = if (budgetActive) "ACTIVE" else "INACTIVE"
                val statusColor = if (budgetActive)
                    MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.error


                Box(
                    modifier = Modifier
                        .clip(MaterialTheme.shapes.small)
                        .background(
                            statusColor
                        )
                        .padding(horizontal = 8.dp, vertical = 4.dp)
                ) {
                    Text(
                        text = budgetStatus,
                        style = MaterialTheme.typography.labelMedium,
                        color = Color.White
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                LabeledText(label = stringResource(R.string.total_amount), text = "KSH ${budget.amount}")
                LabeledText(label = stringResource(id = R.string.impact), text = budget.impact)
                LabeledText(label = stringResource(id = R.string.description), text = budget.budgetNote)
                LabeledText(
                    label = stringResource(R.string.created_on),
                    text = budget.dateCreated?.let { formatDate(it) } ?: "Unknown"
                )
                LabeledText(
                    label = stringResource(R.string.status),
                    text = if (budget.isActive) stringResource(id = R.string.active) else stringResource(
                        R.string.inactive
                    )
                )
            }
        }
    }
}

@Composable
private fun BudgetOptionCard(
    option: BudgetOption,
    isCitizen: Boolean,
    votedOptionId: String?,
    onVote: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(2.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = option.optionProjectName,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = option.optionDescription,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.8f)
            )

            Spacer(modifier = Modifier.height(8.dp))

            option.imageUrl?.let { imageUrl ->
                AsyncImage(
                    model = imageUrl,
                    contentDescription = null,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(180.dp)
                        .clip(RoundedCornerShape(8.dp)),
                    contentScale = ContentScale.Crop
                )
                Spacer(modifier = Modifier.height(8.dp))
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "KSH ${option.optionAmount}",
                    style = MaterialTheme.typography.bodyLarge,
                    fontWeight = FontWeight.Bold
                )

                if (isCitizen) {
                    if (votedOptionId == option.optionId) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier
                                .clip(MaterialTheme.shapes.small)
                                .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.1f))
                                .padding(horizontal = 8.dp, vertical = 4.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Verified,
                                contentDescription = "Voted",
                                tint = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = stringResource(id = R.string.your_vote),
                                style = MaterialTheme.typography.labelMedium,
                                color = MaterialTheme.colorScheme.primary
                            )
                        }
                    } else if (votedOptionId == null) {
                        FilledTonalButton(
                            onClick = onVote,
                            modifier = Modifier.height(36.dp)
                        ) {
                            Text(stringResource(id = R.string.vote))
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun LabeledText(label: String, text: String) {
    Column {
        Text(
            text = label,
            style = MaterialTheme.typography.labelMedium,
            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
        )
        Text(
            text = text,
            style = MaterialTheme.typography.bodyMedium
        )
    }
}

// Reused from previous implementations
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
private fun ErrorState(error: String, onRetry: () -> Unit) {
    Column(
        modifier = Modifier.fillMaxSize(),
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
            Text(stringResource(id = R.string.retry))
        }
    }
}

private fun formatDate(timestamp: String): String {
    return try {
        val date = Date(timestamp.toLong())
        val formatter = SimpleDateFormat("dd MMM yyyy 'at' HH:mm", Locale.getDefault())
        formatter.format(date)
    } catch (e: Exception) {
        "Invalid date"
    }
}