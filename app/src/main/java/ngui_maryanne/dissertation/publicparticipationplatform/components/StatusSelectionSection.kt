package ngui_maryanne.dissertation.publicparticipationplatform.components

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.Leaderboard
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import ngui_maryanne.dissertation.publicparticipationplatform.data.enums.PolicyStatus

@Composable
fun StatusSelectionSection(
    selectedStatus: PolicyStatus,
    onStatusSelected: (PolicyStatus) -> Unit,
    modifier: Modifier = Modifier
) {
    var expanded by remember { mutableStateOf(false) }

    Column(modifier = modifier.fillMaxWidth()) {
        Text(
            text = "Current Policy Stage *",
            style = MaterialTheme.typography.labelMedium,
            modifier = Modifier.padding(bottom = 4.dp)
        )

        Box(modifier = Modifier.fillMaxWidth()) {
            CustomTextField(
                value = selectedStatus.displayName,
                onValueChange = {},
                label = "Select Policy Stage",
                leadingIcon = Icons.Default.Leaderboard,
                trailingIcon = Icons.Default.ArrowDropDown,
                onTrailingIconClick = { expanded = true },
                readOnly = true,
                modifier = Modifier.fillMaxWidth()
            )

            DropdownMenu(
                expanded = expanded,
                onDismissRequest = { expanded = false },
                modifier = Modifier.fillMaxWidth(0.95f)
            ) {
                PolicyStatus.entries.forEach { status ->
                    DropdownMenuItem(
                        text = {
                            Column {
                                Text(
                                    text = status.displayName,
                                    fontWeight = FontWeight.SemiBold
                                )
                                Text(
                                    text = status.description,
                                    style = MaterialTheme.typography.bodySmall,
                                    maxLines = 2
                                )
                                if (status.isPublicVisible) {
                                    Text(
                                        text = "Publicly Visible",
                                        style = MaterialTheme.typography.labelSmall,
                                        color = MaterialTheme.colorScheme.primary
                                    )
                                }
                            }
                        },
                        onClick = {
                            onStatusSelected(status)
                            expanded = false
                        }
                    )
                }
            }
        }

        // Status description
        Text(
            text = selectedStatus.description,
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f),
            modifier = Modifier.padding(top = 4.dp)
        )
    }
}