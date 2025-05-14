package ngui_maryanne.dissertation.publicparticipationplatform.components

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

@Composable
fun AnimatedProgressIndicator(
    percentage: Float,
    isSelected: Boolean = false
) {
    // Animate the progress from 0 to the target value (percentage)
    val animatedProgress = animateFloatAsState(
        targetValue = percentage / 100f,  // Target value as a float between 0 and 1
        animationSpec = androidx.compose.animation.core.tween(
            durationMillis = 1000,  // Adjust duration for the animation speed
            easing = androidx.compose.animation.core.EaseInOut
        )
    ).value

    LinearProgressIndicator(
        progress = animatedProgress,
        modifier = Modifier
            .fillMaxWidth()
            .height(4.dp),
        color = if (isSelected) MaterialTheme.colorScheme.primary
        else MaterialTheme.colorScheme.primary.copy(alpha = 0.5f),
        trackColor = MaterialTheme.colorScheme.surfaceVariant
    )
}
