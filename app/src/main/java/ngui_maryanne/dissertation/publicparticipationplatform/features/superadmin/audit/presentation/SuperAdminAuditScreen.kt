package ngui_maryanne.dissertation.publicparticipationplatform.features.superadmin.audit.presentation

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.ExpandLess
import androidx.compose.material.icons.filled.ExpandMore
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBarDefaults
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import coil.compose.AsyncImage
import com.google.firebase.auth.FirebaseAuth
import ngui_maryanne.dissertation.publicparticipationplatform.utils.HelpMe


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SuperAdminAuditScreen(
    modifier: Modifier = Modifier,
    navController: NavHostController,
    viewModel: SuperAdminAuditLogViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }

  /*  LaunchedEffect(Unit) {
        viewModel.eventFlow.collect { event ->
            when (event) {
                is SuperAdminAuditLogEvent.ShowMessage -> {
                    snackbarHostState.showSnackbar(event.message)
                }
            }
        }
    }*/

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("Audit Logs") },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface,
                    scrolledContainerColor = MaterialTheme.colorScheme.surface
                )
            )
        },
        floatingActionButton = {
            ExtendedFloatingActionButton(
                onClick = { viewModel.onEvent(SuperAdminAuditLogEvent.RunDiscrepancyCheck) },
                icon = { Icon(Icons.Default.Search, contentDescription = "Run check") },
                text = { Text("Verify Integrity") },
                containerColor = MaterialTheme.colorScheme.primary,
                contentColor = MaterialTheme.colorScheme.surface
            )
        }
    ) { paddingValues ->
        Box(
            modifier = modifier
                .fillMaxSize()
                .padding(paddingValues)
                .background(MaterialTheme.colorScheme.background)
        ) {
            when {
//                state.isLoading -> FullScreenLoading()
                state.logs.isEmpty() -> EmptyAuditLogs()
                else -> AuditLogList(state, viewModel)
            }
        }
    }

    if (state.showDiscrepancyDialog) {
        AlertDialog(
            onDismissRequest = {
                viewModel.onEvent(SuperAdminAuditLogEvent.DismissDiscrepancyDialog)
            },
            icon = {
                Icon(
                    imageVector = if (state.discrepancyFound) Icons.Default.Warning else Icons.Default.CheckCircle,
                    contentDescription = null,
                    tint = if (state.discrepancyFound) MaterialTheme.colorScheme.error
                    else MaterialTheme.colorScheme.primary
                )
            },
            title = {
                Text(
                    text = if (state.discrepancyFound) "Discrepancy Detected!" else "Logs Verified",
                    color = if (state.discrepancyFound) MaterialTheme.colorScheme.error
                    else MaterialTheme.colorScheme.primary
                )
            },
            text = {
                Column {
                    Text(
                        text = if (state.discrepancyFound)
                            "Audit log integrity check failed:"
                        else
                            "All audit logs are valid and consistent",
                        style = MaterialTheme.typography.bodyMedium
                    )
                    if (state.discrepancyFound) {
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = state.discrepancies.joinToString("\n"),
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.error
                        )
                    }
                }
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        viewModel.onEvent(SuperAdminAuditLogEvent.DismissDiscrepancyDialog)
                    }
                ) {
                    Text("Dismiss")
                }
            }
        )
    }
}

@Composable
private fun AuditLogList(
    state: SuperAdminAuditLogState,
    viewModel: SuperAdminAuditLogViewModel
) {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp)
    ) {
        val sortedLogs = state.logs.sortedByDescending { it.log.timestamp }
        items(sortedLogs) { logUI ->
            AuditLogItem(
                logUI = logUI,
                onRevealClick = {
                    viewModel.onEvent(
                        SuperAdminAuditLogEvent.RevealUser(
                            userId = logUI.log.createdBy,
                            logId = logUI.log.transactionId // Or another unique field
                        )
                    )
                },
                modifier = Modifier.padding(vertical = 8.dp)
            )
        }

    }
}

@Composable
private fun AuditLogItem(
    logUI: SuperAdminAuditLogUIModel,
    onRevealClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val borderColor = when (logUI.log.transactionType) {
        // CREATE-like actions
        "CREATE_CITIZEN_RECORD", "CREATE_OFFICIAL", "CREATE_POLICY", "CREATE_POLL",
        "CREATE_PETITION", "CREATE_BUDGET", "CREATE_ACCOUNT" -> MaterialTheme.colorScheme.primary

        // UPDATE-like actions
        "UPDATE_POLICY_STATUS", "UPDATE_PROFILE", "UPDATE_POLICY", "EDIT_BUDGET",
        "UPDATE_OFFICIAL" -> MaterialTheme.colorScheme.secondary

        // DELETE or deactivate-like actions
        "DELETE_POLICY", "DEACTIVATE_OFFICIAL" -> MaterialTheme.colorScheme.error

        else -> MaterialTheme.colorScheme.outline
    }

    Card(
        modifier = modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface,
            contentColor = MaterialTheme.colorScheme.onSurface
        ),
        border = BorderStroke(1.dp, borderColor.copy(alpha = 0.5f)),
        shape = MaterialTheme.shapes.medium
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            // User Info Section
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                if (logUI.revealedName != null) {
                    AsyncImage(
                        model = logUI.profileImage,
                        contentDescription = "User profile",
                        modifier = Modifier
                            .size(48.dp)
                            .clip(CircleShape)
                            .border(1.dp, borderColor, CircleShape),
                        contentScale = ContentScale.Crop
                    )
                    Spacer(modifier = Modifier.width(16.dp))
                }

                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = logUI.userType ?: "Anonymous User",
                        style = MaterialTheme.typography.titleMedium,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )

                    if (logUI.revealedName != null) {
                        Text(
                            text = logUI.revealedName,
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.8f)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Transaction Details
            LabeledText(
                label = "Action",
                text = logUI.log.transactionType,
                textColor = borderColor
            )

          /*  LabeledText(
                label = "Entity",
                text = logUI.log.,
                textColor = MaterialTheme.colorScheme.onSurface
            )*/

            LabeledText(
                label = "Timestamp",
                text = HelpMe.getDate(
                    logUI.log.timestamp.toLong(),
                    "MMM dd, yyyy 'at' hh:mm a"
                ),
                textColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
            )

            // Hash (collapsible)
            var expanded by remember { mutableStateOf(false) }
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { expanded = !expanded },
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Hash: ${if (expanded) logUI.log.hash else logUI.log.hash.take(12) + "..."}",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f),
                    modifier = Modifier.weight(1f)
                )
                Icon(
                    imageVector = if (expanded) Icons.Default.ExpandLess else Icons.Default.ExpandMore,
                    contentDescription = if (expanded) "Show less" else "Show more",
                    tint = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                )
            }

            val currentUser = FirebaseAuth.getInstance().currentUser
            // Reveal Button (if not already revealed)
            if (logUI.revealedName == null || logUI.log.createdBy != currentUser!!.uid) {
                Spacer(modifier = Modifier.height(8.dp))
                TextButton(
                    onClick = onRevealClick,
                    modifier = Modifier.align(Alignment.End)
                ) {
                    Icon(
                        imageVector = Icons.Default.Visibility,
                        contentDescription = "Reveal user",
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Reveal Identity")
                }
            }
        }
    }
}

@Composable
private fun LabeledText(
    label: String,
    text: String,
    textColor: Color = MaterialTheme.colorScheme.onSurface
) {
    Column(modifier = Modifier.padding(vertical = 4.dp)) {
        Text(
            text = label,
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
        )
        Text(
            text = text,
            style = MaterialTheme.typography.bodyMedium,
            color = textColor
        )
    }
}

@Composable
private fun EmptyAuditLogs() {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Icon(
            imageVector = Icons.Default.History,
            contentDescription = null,
            modifier = Modifier.size(64.dp),
            tint = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f)
        )
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            text = "No audit logs found",
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