package awesomenessstudios.schoolprojects.publicparticipationplatform.features.officials.policies.policydetails.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import awesomenessstudios.schoolprojects.publicparticipationplatform.data.models.Policy
import awesomenessstudios.schoolprojects.publicparticipationplatform.utils.HelpMe
import coil.compose.AsyncImage

@Composable
fun PolicyDetailsSection(
    policy: Policy,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        // Cover Image (if available)
        if (policy.policyCoverImage.isNotEmpty()) {
            AsyncImage(
                model = policy.policyCoverImage,
                contentDescription = "Policy cover image",
                modifier = Modifier
                    .fillMaxWidth()
                    .height(200.dp)
                    .clip(RoundedCornerShape(8.dp)),
                contentScale = ContentScale.Crop
            )
        }

        // Policy Title & Sector
        Text(
            text = policy.policyTitle,
            style = MaterialTheme.typography.headlineMedium
        )
        Text(
            text = "Sector: ${policy.policySector}",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
        )

        // Policy Description
        Text(
            text = policy.policyDescription,
            style = MaterialTheme.typography.bodyLarge,
            modifier = Modifier.padding(top = 8.dp)
        )

        // Metadata (e.g., creation date)
        Text(
            text = "Created on ${
                HelpMe.getDate(
                    policy.dateCreated.toLong(),
                    "EEE dd MMM yyyy | hh:mm a"
                )
            }",
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
        )
    }
}