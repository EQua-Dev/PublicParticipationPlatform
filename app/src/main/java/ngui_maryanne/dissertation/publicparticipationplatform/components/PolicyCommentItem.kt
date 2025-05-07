package ngui_maryanne.dissertation.publicparticipationplatform.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material3.Card
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import ngui_maryanne.dissertation.publicparticipationplatform.data.models.Comment
import ngui_maryanne.dissertation.publicparticipationplatform.features.citizen.policies.policydetails.PolicyDetailsViewModel
import ngui_maryanne.dissertation.publicparticipationplatform.utils.HelpMe

@Composable
fun CommentItem(comment: Comment, viewModel: PolicyDetailsViewModel = hiltViewModel()) {
    var displayName by remember { mutableStateOf("User ${comment.userId.take(6)}") }

    DisposableEffect(comment.userId) {
        val listener = if (!comment.anonymous) {
            viewModel.getCitizenNameRealtime(comment.userId) {
                displayName = it
            }
        } else null

        onDispose {
            listener?.remove()
        }
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.Default.AccountCircle,
                    contentDescription = "User",
                    modifier = Modifier.size(24.dp)
                )
                Text(
                    text = displayName,
                    modifier = Modifier.padding(start = 8.dp),
                    fontWeight = FontWeight.Bold,
                    style = MaterialTheme.typography.bodyMedium
                )
                Spacer(modifier = Modifier.weight(1f))
                Text(
                    text = HelpMe.getDate(
                        comment.dateCreated.toLong(),
                        "EEE dd MMM yyyy | hh:mm a"
                    ),
                    style = MaterialTheme.typography.bodySmall
                )
            }
            Spacer(modifier = Modifier.height(8.dp))
            Text(text = comment.comment,
                style = MaterialTheme.typography.bodyMedium)
        }
    }
}