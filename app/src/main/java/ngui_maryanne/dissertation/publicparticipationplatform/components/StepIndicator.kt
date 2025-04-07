package ngui_maryanne.dissertation.publicparticipationplatform.components

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun StepIndicator(
    currentStep: Int,
    totalSteps: Int,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(16.dp),
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically
    ) {
        for (step in 1..totalSteps) {
            // Step Circle
            Surface(
                modifier = Modifier
                    .size(32.dp)
                    .padding(4.dp),
                shape = CircleShape,
                color = if (step <= currentStep) MaterialTheme.colorScheme.primary else Color.LightGray
            ) {
                Box(
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = step.toString(),
                        color = if (step <= currentStep) Color.White else Color.Black,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            // Step Connector (line between circles)
            if (step < totalSteps) {
                Surface(
                    modifier = Modifier
                        .width(32.dp)
                        .height(2.dp),
                    color = if (step < currentStep) MaterialTheme.colorScheme.primary else Color.LightGray
                ) {}
            }
        }
    }
}