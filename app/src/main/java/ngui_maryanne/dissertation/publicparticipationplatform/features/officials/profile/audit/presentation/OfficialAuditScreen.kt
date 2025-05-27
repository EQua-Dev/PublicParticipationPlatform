package ngui_maryanne.dissertation.publicparticipationplatform.features.officials.profile.audit.presentation

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import ngui_maryanne.dissertation.publicparticipationplatform.R
import ngui_maryanne.dissertation.publicparticipationplatform.components.CustomButton
import ngui_maryanne.dissertation.publicparticipationplatform.utils.HelpMe

@Composable
fun OfficialAuditScreen(
    modifier: Modifier = Modifier,
    navController: NavHostController,
    viewModel: OfficialAuditLogViewModel = hiltViewModel()
) {
    val state by viewModel.state

    Scaffold(/*floatingActionButton = {
        FloatingActionButton(onClick = { viewModel.onEvent(OfficialAuditLogEvent.RunDiscrepancyCheck) }) {
            Text(text = "Run Discrepancy Check")
        }
    }*/) { padding ->
        Column(modifier = modifier.padding(16.dp)) {
            Text(stringResource(id = R.string.audit_logs), style = MaterialTheme.typography.titleLarge)

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

                            Text(
                                text = "Location: ${logUI.log.location}",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )

                        }
                    }

                }
            }

        }
    }


    if (state.showDiscrepancyDialog) {
        AlertDialog(
            onDismissRequest = {
                viewModel.onEvent(OfficialAuditLogEvent.DismissDiscrepancyDialog)
            },
            confirmButton = {
                TextButton(onClick = {
                    viewModel.onEvent(OfficialAuditLogEvent.DismissDiscrepancyDialog)
                }) {
                    Text("OK")
                }
            },
            title = {
                Text(if (state.discrepancyFound) "Discrepancy Found" else "Logs Valid")
            },
            text = {
                Text(
                    if (state.discrepancyFound)
                        "There are inconsistencies in the audit logs."
                    else
                        "100% valid. No discrepancies found."
                )
            }
        )
    }
}
