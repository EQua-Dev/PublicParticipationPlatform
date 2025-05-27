package ngui_maryanne.dissertation.publicparticipationplatform.features.citizen.petitions.newpetition

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import ngui_maryanne.dissertation.publicparticipationplatform.R

@Composable
fun BannerDisclaimer() {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color(0xFFFDE9E9), shape = RoundedCornerShape(12.dp))
            .padding(16.dp)
    ) {
        Text(stringResource(R.string.please_read_before_submitting), style = MaterialTheme.typography.titleMedium, color = Color(0xFFD32F2F))
        Spacer(modifier = Modifier.height(8.dp))

        val disclaimers = listOf(
            stringResource(R.string.ensure_your_petition_is_respectful_and_factual),
            stringResource(R.string.avoid_hateful_or_discriminatory_language),
            stringResource(R.string.petitions_with_false_claims_may_be_removed),
            stringResource(R.string.once_submitted_edits_are_limited_review_carefully)
        )

        disclaimers.forEach {
            Row(verticalAlignment = Alignment.Top, modifier = Modifier.padding(vertical = 2.dp)) {
                Icon(
                    imageVector = Icons.Default.Warning,
                    contentDescription = null,
                    tint = Color(0xFFD32F2F),
                    modifier = Modifier.size(16.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = it,
                    style = MaterialTheme.typography.bodySmall.copy(fontSize = 13.sp),
                    color = Color(0xFF444444),
                    lineHeight = 18.sp
                )
            }
        }
    }
}
