package ngui_maryanne.dissertation.publicparticipationplatform.features.citizen.policies.presentation

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Policy
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Snackbar
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import ngui_maryanne.dissertation.publicparticipationplatform.data.enums.PolicyStatus
import ngui_maryanne.dissertation.publicparticipationplatform.data.models.Policy
import ngui_maryanne.dissertation.publicparticipationplatform.features.common.auth.presentation.login.KenyanBackgroundPattern
import ngui_maryanne.dissertation.publicparticipationplatform.navigation.Screen
import ngui_maryanne.dissertation.publicparticipationplatform.utils.HelpMe

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CitizenPoliciesScreen(
    modifier: Modifier = Modifier,
    navController: NavHostController,
    viewModel: CitizenPoliciesViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }
    val scrollState = rememberScrollState()

    LaunchedEffect(Unit) {
        viewModel.events.collect { event ->
            when (event) {
                CitizenPoliciesEvent.NavigateBack -> navController.popBackStack()
                is CitizenPoliciesEvent.NavigateToPolicyDetails -> {
                    navController.navigate(
                        Screen.CitizenPolicyDetailsScreen.route.replace(
                            "{policyId}",
                            event.policyId
                        )
                    )
                }
                is CitizenPoliciesEvent.ShowError -> {
                    snackbarHostState.showSnackbar(event.message)
                }

                CitizenPoliciesEvent.ClearError -> TODO()
            }
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        // Kenyan-themed background
//        KenyanBackgroundPattern()

        Scaffold(
            modifier = modifier,
            topBar = {
                CenterAlignedTopAppBar(
                    title = {
                        Text(
                            "Public Policies",
                            style = MaterialTheme.typography.headlineSmall.copy(
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onBackground
                            )
                        )
                    },
                    navigationIcon = {
                        IconButton(
                            onClick = { viewModel.handleAction(CitizenPoliciesAction.OnBackClicked) }
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
                    )
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
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .padding(horizontal = 16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Explanation text
                Text(
                    text = "Explore government policies and proposed legislation. " +
                            "Participate in consultations and stay informed about new initiatives.",
                    style = MaterialTheme.typography.bodyMedium.copy(
                        color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.8f)
                    ),
                    modifier = Modifier.fillMaxWidth()
                )

                // Search bar
                SearchBar(
                    query = uiState.searchQuery,
                    onQueryChange = {
                        viewModel.handleAction(
                            CitizenPoliciesAction.OnSearchQueryChanged(it)
                        )
                    },
                    modifier = Modifier.fillMaxWidth()
                )

                // Status filter chips
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .horizontalScroll(rememberScrollState()),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    PolicyStatus.values().forEach { status ->
                        FilterChip(
                            selected = uiState.selectedStatus == status,
                            enabled = true,
                            onClick = {
                                viewModel.handleAction(
                                    CitizenPoliciesAction.OnStatusFilterChanged(status)
                                )
                            },
                            label = { Text(status.displayName) },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = when (status) {
                                    PolicyStatus.PUBLIC_CONSULTATION -> Color(0xFF2196F3).copy(alpha = 0.2f)
                                    PolicyStatus.APPROVED -> Color(0xFF4CAF50).copy(alpha = 0.2f)
                                    PolicyStatus.REJECTED -> Color(0xFFF44336).copy(alpha = 0.2f)
                                    else -> MaterialTheme.colorScheme.surfaceVariant
                                },
                                selectedLabelColor = when (status) {
                                    PolicyStatus.PUBLIC_CONSULTATION -> Color(0xFF2196F3)
                                    PolicyStatus.APPROVED -> Color(0xFF4CAF50)
                                    PolicyStatus.REJECTED -> Color(0xFFF44336)
                                    else -> MaterialTheme.colorScheme.onSurface
                                }
                            ),
                            border = FilterChipDefaults.filterChipBorder(
                                borderColor = when (status) {
                                    PolicyStatus.PUBLIC_CONSULTATION -> Color(0xFF2196F3)
                                    PolicyStatus.APPROVED -> Color(0xFF4CAF50)
                                    PolicyStatus.REJECTED -> Color(0xFFF44336)
                                    else -> MaterialTheme.colorScheme.outline
                                },
                                selectedBorderColor = Color.Transparent,
                                selected = uiState.selectedStatus == status,
                                enabled = true
                            )
                        )
                    }
                }

                // Loading/error states
                when {
                    uiState.isLoading -> {
                        Box(
                            modifier = Modifier.fillMaxSize(),
                            contentAlignment = Alignment.Center
                        ) {
                            CircularProgressIndicator(
                                color = MaterialTheme.colorScheme.primary
                            )
                        }
                    }

                    uiState.policies.isEmpty() -> {
                        Box(
                            modifier = Modifier.fillMaxSize(),
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
                                Text(
                                    text = if (uiState.searchQuery.isNotEmpty() || uiState.selectedStatus != null) {
                                        "No matching policies found"
                                    } else {
                                        "No policies available at this time"
                                    },
                                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                                )
                            }
                        }
                    }

                    else -> {
                        // Policy list
                        LazyColumn(
                            modifier = Modifier.fillMaxSize(),
                            verticalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            items(uiState.policies) { policy ->
                                PolicyCard(
                                    policy = policy,
                                    onClick = {
                                        viewModel.handleAction(
                                            CitizenPoliciesAction.OnPolicyClicked(policy.id)
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

@Composable
private fun PolicyCard(policy: Policy, onClick: () -> Unit) {
    Card(
        onClick = onClick,
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface.copy(alpha = 0.9f)
        ),
        shape = MaterialTheme.shapes.medium,
        border = BorderStroke(
            1.dp,
            MaterialTheme.colorScheme.outline.copy(alpha = 0.1f)
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
                    text = policy.policyTitle,
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontWeight = FontWeight.SemiBold
                    ),
                    modifier = Modifier.weight(1f)
                )

                Text(
                    text = HelpMe.getDate(policy.dateCreated.toLong(), "dd MMM yyyy"),
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.outline
                )
            }

            Text(
                text = policy.policySector,
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.primary
            )

            Text(
                text = policy.policyDescription.take(150) + if (policy.policyDescription.length > 150) "..." else "",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.8f),
                maxLines = 3,
                overflow = TextOverflow.Ellipsis
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    contentAlignment = Alignment.Center,
                    modifier = Modifier
                        .clip(MaterialTheme.shapes.small)
                        .background(
                            when (policy.policyStatus) {
                                PolicyStatus.PUBLIC_CONSULTATION -> Color(0xFF2196F3).copy(alpha = 0.2f)
                                PolicyStatus.APPROVED -> Color(0xFF4CAF50).copy(alpha = 0.2f)
                                PolicyStatus.REJECTED -> Color(0xFFF44336).copy(alpha = 0.2f)
                                else -> MaterialTheme.colorScheme.surfaceVariant
                            }
                        )
                        .padding(horizontal = 12.dp, vertical = 6.dp)
                ) {
                    Text(
                        text = policy.policyStatus.displayName,
                        style = MaterialTheme.typography.labelSmall.copy(
                            color = when (policy.policyStatus) {
                                PolicyStatus.PUBLIC_CONSULTATION -> Color(0xFF2196F3)
                                PolicyStatus.APPROVED -> Color(0xFF4CAF50)
                                PolicyStatus.REJECTED -> Color(0xFFF44336)
                                else -> MaterialTheme.colorScheme.onSurface
                            }
                        )
                    )
                }

                Icon(
                    imageVector = Icons.Default.ChevronRight,
                    contentDescription = "View details",
                    tint = MaterialTheme.colorScheme.outline
                )
            }
        }
    }
}

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
        placeholder = { Text("Search policies by title or sector...") },
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