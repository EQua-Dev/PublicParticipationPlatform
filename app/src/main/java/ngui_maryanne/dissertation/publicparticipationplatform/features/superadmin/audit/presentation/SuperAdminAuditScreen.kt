package ngui_maryanne.dissertation.publicparticipationplatform.features.superadmin.audit.presentation

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import coil.compose.AsyncImage
import ngui_maryanne.dissertation.publicparticipationplatform.components.CustomButton
import ngui_maryanne.dissertation.publicparticipationplatform.utils.HelpMe

@Composable
fun SuperAdminAuditScreen(
    modifier: Modifier = Modifier,
    navController: NavHostController,
    viewModel: SuperAdminAuditLogViewModel = hiltViewModel()
) {
    val state by viewModel.state

    Scaffold(floatingActionButton = {
        FloatingActionButton(onClick = { viewModel.onEvent(SuperAdminAuditLogEvent.RunDiscrepancyCheck) }) {
            Text(text = "Run Discrepancy Test")
        }

    }) { padding ->
        Column(modifier = modifier.padding(16.dp)) {
            Text("Audit Logs", style = MaterialTheme.typography.titleLarge)

            LazyColumn {
                itemsIndexed(state.logs) { index, logUI ->
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 8.dp, vertical = 4.dp),
                        shape = RoundedCornerShape(16.dp),
                        elevation = CardDefaults.cardElevation(defaultElevation = 6.dp)
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(MaterialTheme.colorScheme.surface)
                                .padding(4.dp)
                        ) {

                            Row(
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                // Profile image if revealed
                                if (logUI.revealedName != null) {
                                    AsyncImage(
                                        model = logUI.profileImage,
                                        contentDescription = "User Profile Image",
                                        modifier = Modifier
                                            .size(64.dp)
                                            .clip(CircleShape)
                                            .border(
                                                2.dp,
                                                MaterialTheme.colorScheme.primary,
                                                CircleShape
                                            ),
                                        contentScale = ContentScale.Crop
                                    )

                                    Spacer(modifier = Modifier.width(12.dp))
                                }

                                Column(
                                    modifier = Modifier.weight(1f)
                                ) {
                                    Text(
                                        text = logUI.userType ?: logUI.log.createdBy,
                                        style = MaterialTheme.typography.titleMedium,
                                        color = MaterialTheme.colorScheme.onSurface
                                    )

                                    if (logUI.userType != null) {
                                        Text(
                                            text = "Type: ${logUI.revealedName}",
                                            style = MaterialTheme.typography.labelLarge,
                                            color = MaterialTheme.colorScheme.tertiary
                                        )
                                    }
                                }
                            }

                            Spacer(modifier = Modifier.height(12.dp))

                            Divider()

                            Spacer(modifier = Modifier.height(12.dp))

                            Text(
                                text = "Transaction: ${logUI.log.transactionType}",
                                style = MaterialTheme.typography.bodyLarge,
                                color = MaterialTheme.colorScheme.primary
                            )

                            Text(
                                text = "Hash: ${logUI.log.hash}",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )

                            Text(
                                text = "Time: ${
                                    HelpMe.getDate(
                                        logUI.log.timestamp.toLong(),
                                        "EEE, dd MMM yyyy | hh:mm a"
                                    )
                                }",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )

                            Spacer(modifier = Modifier.height(8.dp))

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.End
                            ) {
                                TextButton(
                                    onClick = {
                                        viewModel.onEvent(
                                            SuperAdminAuditLogEvent.RevealUser(logUI.log.createdBy, index)
                                        )
                                    }
                                ) {
                                    Icon(
                                        Icons.Default.Visibility,
                                        contentDescription = "Reveal User"
                                    )
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text("Reveal", style = MaterialTheme.typography.labelLarge)
                                }
                            }
                        }
                    }

                }
            }

        }
    }


    if (state.showDiscrepancyDialog) {
        AlertDialog(
            onDismissRequest = {
                viewModel.onEvent(SuperAdminAuditLogEvent.DismissDiscrepancyDialog)
            },
            confirmButton = {
                TextButton(onClick = {
                    viewModel.onEvent(SuperAdminAuditLogEvent.DismissDiscrepancyDialog)
                }) {
                    Text("OK")
                }
            },
            title = {
                Text(if (state.discrepancyFound) "Discrepancy Found!" else "Logs Valid")
            },
            text = {
                Text(
                    if (state.discrepancyFound)
                        "There are inconsistencies in the audit logs.\n${state.discrepancies[0]}"
                    else
                        "100% valid. No discrepancies found."
                )
            }
        )
    }
}
