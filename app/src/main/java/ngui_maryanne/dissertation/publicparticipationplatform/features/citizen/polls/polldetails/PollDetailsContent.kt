package ngui_maryanne.dissertation.publicparticipationplatform.features.citizen.polls.polldetails

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import ngui_maryanne.dissertation.publicparticipationplatform.components.getTimeLeft
import ngui_maryanne.dissertation.publicparticipationplatform.data.models.PollOption
import ngui_maryanne.dissertation.publicparticipationplatform.data.models.PollWithPolicyNameAndDescription

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun PollDetailsContent(
    pollData: PollWithPolicyNameAndDescription,
    onOptionSelected: (PollOption) -> Unit,
    onViewPolicyClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val poll = pollData.poll
    val policyName = pollData.policyName
    val policyDescription = pollData.policyDescription

    val totalResponses = poll.responses.size
    val timeLeft = remember { getTimeLeft(poll.pollExpiry.toLong()) }

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {

        // Top Bar
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = "Poll #${poll.pollNo}",
                style = MaterialTheme.typography.titleMedium
            )

            Text(
                text = "Expires in: $timeLeft",
                color = MaterialTheme.colorScheme.error,
                style = MaterialTheme.typography.bodyMedium
            )
        }

        // Policy Name & Description
        Column {
            Text(
                text = policyName,
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = policyDescription,
                style = MaterialTheme.typography.bodySmall
            )
        }

        // Poll Question
        Column {
            Text(
                text = poll.pollQuestion,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Medium
            )
        }

        // Options Card
        Card(
            modifier = Modifier.fillMaxWidth(),
            elevation = CardDefaults.cardElevation(4.dp)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                poll.pollOptions.forEach { option ->
                    val votes = poll.responses.count { it.optionId == option.optionId }
                    val percentage = if (totalResponses == 0) 0 else (votes * 100 / totalResponses)

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 8.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(text = option.optionText, fontWeight = FontWeight.SemiBold)
                            Spacer(modifier = Modifier.height(2.dp))
                            Text(text = option.optionExplanation, style = MaterialTheme.typography.bodySmall)
                        }

                        Column(horizontalAlignment = Alignment.End) {
                            Button(onClick = { onOptionSelected(option) }) {
                                Text("Choose")
                            }
                            Spacer(modifier = Modifier.height(4.dp))
                            Text("$percentage%", style = MaterialTheme.typography.labelSmall)
                        }
                    }
                }
            }
        }

        // Policy Button
        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Text(
                text = "Policy: $policyName",
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.SemiBold
            )
            Text(
                text = policyDescription,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.outline
            )
            Button(
                onClick = onViewPolicyClick,
                modifier = Modifier.align(Alignment.CenterHorizontally)
            ) {
                Text("View Policy Details")
            }
        }
    }
}
