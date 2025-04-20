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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun BannerDisclaimer() {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color(0xFFFDE9E9), shape = RoundedCornerShape(12.dp))
            .padding(16.dp)
    ) {
        Text("Please read before submitting:", style = MaterialTheme.typography.titleMedium, color = Color(0xFFD32F2F))
        Spacer(modifier = Modifier.height(8.dp))

        val disclaimers = listOf(
            "Ensure your petition is respectful and factual.",
            "Avoid hateful or discriminatory language.",
            "Petitions with false claims may be removed.",
            "Once submitted, edits are limited — review carefully."
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
