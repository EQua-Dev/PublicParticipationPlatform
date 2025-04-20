package ngui_maryanne.dissertation.publicparticipationplatform.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun PolicyTimelineStepper(
    steps: Int,
    currentStep: Int,
    content: @Composable (step: Int, isCurrent: Boolean) -> Unit
) {
    Column {
        repeat(steps) { step ->
            Row(verticalAlignment = Alignment.Top) {
                // Vertical line
                if (step < steps - 1) {
                    Box(
                        modifier = Modifier
                            .width(24.dp)
                            .padding(top = 24.dp, bottom = 4.dp)
                            .fillMaxHeight()
                            .width(2.dp)
                            .background(MaterialTheme.colorScheme.outline)
                    )
                } else {
                    Spacer(modifier = Modifier.width(24.dp))
                }

                // Step content
                Box(modifier = Modifier.weight(1f)) {
                    content(step, step == currentStep)
                }
            }
        }
    }
}