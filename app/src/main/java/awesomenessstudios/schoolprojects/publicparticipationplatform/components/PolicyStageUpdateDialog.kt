package awesomenessstudios.schoolprojects.publicparticipationplatform.components

import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import awesomenessstudios.schoolprojects.publicparticipationplatform.data.enums.PolicyStatus

@Composable
fun StageUpdateDialog(
    currentStage: PolicyStatus,
    onDismiss: () -> Unit,
    onConfirm: (PolicyStatus) -> Unit
) {
    val nextStage = PolicyStatus.entries
        .getOrNull(PolicyStatus.entries.indexOf(currentStage) + 1)

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Advance Policy Stage") },
        text = {
            if (nextStage != null) {
                Text("Are you sure you want to advance this policy to ${nextStage.displayName}?")
            } else {
                Text("This policy has reached its final stage")
            }
        },
        confirmButton = {
            if (nextStage != null) {
                Button(onClick = { onConfirm(nextStage) }) {
                    Text("Advance to ${nextStage.displayName}")
                }
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}