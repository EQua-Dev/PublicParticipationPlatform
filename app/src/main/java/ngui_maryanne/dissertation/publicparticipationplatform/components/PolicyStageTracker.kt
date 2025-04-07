package ngui_maryanne.dissertation.publicparticipationplatform.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import ngui_maryanne.dissertation.publicparticipationplatform.data.enums.PolicyStatus

@Composable
fun PolicyStageTracker(
    currentStage: PolicyStatus,
    onStageChange: (PolicyStatus) -> Unit,
    canUpdateStage: Boolean
) {
    val allStages = PolicyStatus.entries
    val currentIndex = allStages.indexOf(currentStage)

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text("Policy Progress", style = MaterialTheme.typography.titleLarge)
            Spacer(modifier = Modifier.height(8.dp))

            allStages.forEachIndexed { index, stage ->
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.padding(vertical = 8.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(24.dp)
                            .background(
                                color = when {
                                    index < currentIndex -> Color.Green
                                    index == currentIndex -> MaterialTheme.colorScheme.primary
                                    else -> MaterialTheme.colorScheme.surfaceVariant
                                },
                                shape = CircleShape
                            )
                            .border(
                                width = 1.dp,
                                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.12f),
                                shape = CircleShape
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        if (index < currentIndex) {
                            Icon(Icons.Default.Check, null, Modifier.size(12.dp))
                        }
                    }

                    Text(
                        text = stage.displayName,
                        modifier = Modifier.padding(start = 16.dp),
                        fontWeight = if (index == currentIndex) FontWeight.Bold else FontWeight.Normal
                    )

                    if (index == currentIndex && canUpdateStage && index < allStages.size - 1) {
                        Button(
                            onClick = { onStageChange(allStages[index + 1]) },
                            modifier = Modifier.padding(start = 16.dp)
                        ) {
                            Text("Advance to ${allStages[index + 1].displayName}")
                        }
                    }
                }
            }
        }
    }
}