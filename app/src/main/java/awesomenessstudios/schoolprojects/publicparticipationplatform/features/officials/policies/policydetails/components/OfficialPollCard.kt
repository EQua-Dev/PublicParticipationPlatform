package awesomenessstudios.schoolprojects.publicparticipationplatform.features.officials.policies.policydetails.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Loop
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import awesomenessstudios.schoolprojects.publicparticipationplatform.data.models.Poll

@Composable
fun PollCard(
    poll: Poll,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val isExpired =
        poll.pollExpiry.toLong() > System.currentTimeMillis() /* Logic to check if poll.pollExpiry < currentDate */


    Card(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp)
            .clickable { onClick() },
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            // Poll Question
            Text(
                text = poll.pollQuestion,
                style = MaterialTheme.typography.titleMedium,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis
            )

            // Poll Status (Ongoing/Expired)
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Icon(
                    imageVector = if (isExpired) Icons.Default.Info else Icons.Default.Loop,
                    contentDescription = "Poll status",
                    tint = if (isExpired) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.primary
                )
                Text(
                    text = if (isExpired) "Closed" else "Ongoing",
                    color = if (isExpired) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.primary
                )
            }

            // Expiry Date
            Text(
                text = "Closes on ${poll.pollExpiry}",
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
            )
        }
    }
}