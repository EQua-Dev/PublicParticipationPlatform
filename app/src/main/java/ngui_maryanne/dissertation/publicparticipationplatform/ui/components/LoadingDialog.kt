package ngui_maryanne.dissertation.publicparticipationplatform.ui.components

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.lerp
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.sin

@Composable
fun LoadingDialog(
    loadingText: String = "Loading...",
    onDismissRequest: () -> Unit = { }
) {
    // Define Kenyan theme colors
    val kenyaGreen = Color(0xFF006600)
    val kenyaRed = Color(0xFFBF0000)
    val kenyaBlack = Color(0xFF000000)
    val kenyaWhite = Color(0xFFF5F5F5)
    val kenyaGold = Color(0xFFFFD700)

    Dialog(
        onDismissRequest = onDismissRequest,
        properties = DialogProperties(
            dismissOnBackPress = false,
            dismissOnClickOutside = false
        )
    ) {
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier
                .size(250.dp)
                .clip(RoundedCornerShape(20.dp))
                .background(
                    brush = Brush.linearGradient(
                        colors = listOf(
                            kenyaBlack.copy(alpha = 0.9f),
                            kenyaBlack.copy(alpha = 0.8f)
                        )
                    )
                )
                .border(
                    width = 2.dp,
                    brush = Brush.linearGradient(
                        colors = listOf(kenyaRed, kenyaGreen)
                    ),
                    shape = RoundedCornerShape(20.dp)
                )
                .padding(16.dp)
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                // Shield animation
                KenyanShieldLoader()

                Spacer(modifier = Modifier.height(24.dp))

                Text(
                    text = loadingText,
                    color = kenyaWhite,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Medium,
                    textAlign = TextAlign.Center,
                    letterSpacing = 0.5.sp
                )
            }
        }
    }
}

@Composable
fun KenyanShieldLoader() {
    // Define Kenyan theme colors
    val kenyaGreen = Color(0xFF006600)
    val kenyaRed = Color(0xFFBF0000)
    val kenyaBlack = Color(0xFF000000)
    val kenyaWhite = Color(0xFFF5F5F5)
    val kenyaGold = Color(0xFFFFD700)

    val infiniteTransition = rememberInfiniteTransition(label = "shield_animation")

    // Rotation animation for spears
    val rotation by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(
            animation = tween(3000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "rotation"
    )

    // Pulse animation for shield
    val scale by infiniteTransition.animateFloat(
        initialValue = 0.85f,
        targetValue = 1.15f,
        animationSpec = infiniteRepeatable(
            animation = tween(1200, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "scale"
    )

    // Color animation
    val colorTransition by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(2500),
            repeatMode = RepeatMode.Reverse
        ),
        label = "color_transition"
    )

    // Calculate current color based on animation
    val currentBorderColor = lerp(kenyaRed, kenyaGreen, colorTransition)

    Box(
        contentAlignment = Alignment.Center,
        modifier = Modifier.size(120.dp)
    ) {
        // Rotating spears layer
        Canvas(
            modifier = Modifier
                .size(120.dp)
                .graphicsLayer {
                    rotationZ = rotation
                }
        ) {
            val center = Offset(size.width / 2, size.height / 2)
            val outerRadius = size.width / 2

            // Draw spears (traditional Maasai design)
            val spearCount = 8
            for (i in 0 until spearCount) {
                val angle = (i * 360f / spearCount) * (PI / 180f).toFloat()
                val startX = center.x + outerRadius * 0.5f * cos(angle)
                val startY = center.y + outerRadius * 0.5f * sin(angle)
                val endX = center.x + outerRadius * 0.9f * cos(angle)
                val endY = center.y + outerRadius * 0.9f * sin(angle)

                // Spear shaft
                drawLine(
                    color = if (i % 2 == 0) kenyaRed else kenyaGreen,
                    start = Offset(startX, startY),
                    end = Offset(endX, endY),
                    strokeWidth = 6f,
                    cap = StrokeCap.Round
                )

                // Spear tip
                val tipLength = outerRadius * 0.15f
                val tipX = center.x + outerRadius * cos(angle)
                val tipY = center.y + outerRadius * sin(angle)

                drawCircle(
                    color = kenyaGold,
                    radius = 5f,
                    center = Offset(endX, endY)
                )
            }
        }

        // Shield
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier
                .size(80.dp)
                .graphicsLayer {
                    scaleX = scale
                    scaleY = scale
                }
                .clip(CircleShape)
                .background(kenyaBlack)
                .border(
                    width = 3.dp,
                    brush = Brush.sweepGradient(
                        listOf(
                            currentBorderColor,
                            kenyaGold,
                            currentBorderColor
                        )
                    ),
                    shape = CircleShape
                )
        ) {
            // Traditional Maasai shield pattern
            Canvas(modifier = Modifier.size(60.dp)) {
                val width = size.width
                val height = size.height

                // Draw central vertical stripe (black already from background)

                // Draw horizontal stripes
                val stripeHeight = height / 5

                // Top red stripe
                drawRect(
                    color = kenyaRed,
                    topLeft = Offset(0f, stripeHeight),
                    size = Size(width, stripeHeight)
                )

                // Middle green stripe
                drawRect(
                    color = kenyaGreen,
                    topLeft = Offset(0f, stripeHeight * 2),
                    size = Size(width, stripeHeight)
                )

                // Middle white accent stripe
                drawRect(
                    color = kenyaWhite,
                    topLeft = Offset(width * 0.35f, height * 0.4f),
                    size = Size(width * 0.3f, height * 0.2f)
                )

                // Bottom red stripe
                drawRect(
                    color = kenyaRed,
                    topLeft = Offset(0f, stripeHeight * 3),
                    size = Size(width, stripeHeight)
                )
            }
        }

        // Progress indicator ring
        CircularProgressIndicator(
            modifier = Modifier.size(100.dp),
            color = kenyaGold,
            trackColor = kenyaGold.copy(alpha = 0.2f),
            strokeWidth = 3.dp
        )
    }
}