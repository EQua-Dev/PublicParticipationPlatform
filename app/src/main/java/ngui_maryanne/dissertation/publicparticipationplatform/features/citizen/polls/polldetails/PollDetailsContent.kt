package ngui_maryanne.dissertation.publicparticipationplatform.features.citizen.polls.polldetails

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.BorderStroke
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
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import ngui_maryanne.dissertation.publicparticipationplatform.R
import ngui_maryanne.dissertation.publicparticipationplatform.components.getTimeLeft
import ngui_maryanne.dissertation.publicparticipationplatform.data.enums.UserRole
import ngui_maryanne.dissertation.publicparticipationplatform.data.models.PollOption
import ngui_maryanne.dissertation.publicparticipationplatform.data.models.PollResponses
import ngui_maryanne.dissertation.publicparticipationplatform.data.models.PollWithPolicyNameAndDescription
import ngui_maryanne.dissertation.publicparticipationplatform.features.citizen.polls.presentation.PollStatus


@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun PollDetailsContent(
    pollData: PollWithPolicyNameAndDescription,
    currentUserRole: String,
    votedOptionId: String?,
    onOptionSelected: (PollOption) -> Unit,
    onViewPolicyClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val poll = pollData.poll
    val pollStatus = pollData.pollStatus
    val timeLeft = remember { getTimeLeft(poll.pollExpiry.toLong()) }
    val totalResponses = poll.responses.size

    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(24.dp)
    ) {
        // Poll Status and Time Left
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            PolicyStatusBadge(status = pollStatus)
            Text(
                text = "Expires in: $timeLeft",
                color = if (pollStatus == PollStatus.ACTIVE) MaterialTheme.colorScheme.error
                else MaterialTheme.colorScheme.outline,
                style = MaterialTheme.typography.labelLarge
            )
        }

        // Policy Info
        Card(
            onClick = onViewPolicyClick,
            modifier = Modifier.fillMaxWidth(),
            elevation = CardDefaults.cardElevation(2.dp)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    text = stringResource(R.string.related_policy),
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.primary
                )
                Spacer(Modifier.height(8.dp))
                Text(
                    text = pollData.policyName,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold
                )
                Spacer(Modifier.height(4.dp))
                Text(
                    text = pollData.policyDescription,
                    style = MaterialTheme.typography.bodySmall,
                    maxLines = 3,
                    overflow = TextOverflow.Ellipsis
                )
            }
        }

        // Poll Question
        Text(
            text = poll.pollQuestion,
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold
        )

        // Poll Options
        Column(
            modifier = Modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            poll.pollOptions.forEach { option ->
                PollOptionItem(
                    option = option,
                    totalVotes = totalResponses,
                    isSelected = votedOptionId == option.optionId,
                    canVote = currentUserRole.equals(UserRole.CITIZEN.name, ignoreCase = true) &&
                            pollStatus == PollStatus.ACTIVE &&
                            votedOptionId == null,
                    onVote = { onOptionSelected(option) },
                    allThisVotes = poll.responses.filter { it.optionId == option.optionId }

                )
            }
        }

        // Poll Stats
        if (totalResponses > 0) {
            Card(
                modifier = Modifier.fillMaxWidth(),
                elevation = CardDefaults.cardElevation(2.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "Poll Statistics",
                        style = MaterialTheme.typography.labelMedium,
                        color = MaterialTheme.colorScheme.primary
                    )
                    Spacer(Modifier.height(8.dp))
                    Text("Total votes: $totalResponses")
                }
            }
        }
    }
}

@Composable
fun PollOptionItem(
    option: PollOption,
    allThisVotes: List<PollResponses>,
    totalVotes: Int,
    isSelected: Boolean,
    canVote: Boolean,
    onVote: () -> Unit
) {
    val votes = allThisVotes.size
    val percentage = if (totalVotes > 0) votes * 100f / totalVotes else 0f

    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(1.dp),
        border = if (isSelected) BorderStroke(
            2.dp,
            MaterialTheme.colorScheme.primary
        ) else null
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            // Option Text
            Text(
                text = option.optionText,
                style = MaterialTheme.typography.bodyLarge,
                fontWeight = FontWeight.SemiBold
            )

            // Option Explanation
            if (option.optionExplanation.isNotBlank()) {
                Spacer(Modifier.height(4.dp))
                Text(
                    text = option.optionExplanation,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                )
            }

            // Progress Bar
            Spacer(Modifier.height(8.dp))

            LinearProgressIndicator(
                progress = percentage / 100f,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(4.dp),
                color = if (isSelected) MaterialTheme.colorScheme.primary
                else MaterialTheme.colorScheme.primary.copy(alpha = 0.5f),
                trackColor = MaterialTheme.colorScheme.surfaceVariant
            )

            // Vote Info and Button
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 8.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = if (totalVotes > 0) "${percentage.toInt()}% ($votes votes)"
                    else stringResource(R.string.no_votes_yet),
                    style = MaterialTheme.typography.labelSmall
                )

                if (canVote) {
                    Button(
                        onClick = onVote,
                        modifier = Modifier.height(36.dp),
                        contentPadding = PaddingValues(horizontal = 16.dp)
                    ) {
                        Text(stringResource(R.string.vote))
                    }
                } else if (isSelected) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.CheckCircle,
                            contentDescription = stringResource(R.string.voted),
                            tint = MaterialTheme.colorScheme.primary
                        )
                        Text(
                            text = stringResource(R.string.your_vote),
                            style = MaterialTheme.typography.labelMedium,
                            color = MaterialTheme.colorScheme.primary
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun PolicyStatusBadge(status: PollStatus) {
    val context = LocalContext.current
    Box(
        modifier = Modifier
            .clip(MaterialTheme.shapes.small)
            .background(
                when (status) {
                    PollStatus.ACTIVE -> MaterialTheme.colorScheme.primary.copy(alpha = 0.2f)
                    PollStatus.CLOSED -> MaterialTheme.colorScheme.error.copy(alpha = 0.2f)
                    PollStatus.DRAFT -> MaterialTheme.colorScheme.outline.copy(alpha = 0.2f)
                }
            )
            .padding(horizontal = 12.dp, vertical = 6.dp)
    ) {
        Text(
            text = status.getDisplayName(context),
            style = MaterialTheme.typography.labelSmall.copy(
                color = when (status) {
                    PollStatus.ACTIVE -> MaterialTheme.colorScheme.primary
                    PollStatus.CLOSED -> MaterialTheme.colorScheme.error
                    PollStatus.DRAFT -> MaterialTheme.colorScheme.outline
                }
            )
        )
    }
}