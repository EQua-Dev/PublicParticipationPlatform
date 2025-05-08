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
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.withTransform
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.unit.dp
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.sin

/*@Composable
fun BackgroundAnimations() {
    val infiniteTransition = rememberInfiniteTransition(label = "kenyan_background_animation")

    // Create animated values for different shapes
    val wave1 by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(8000, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "wave1"
    )

    val wave2 by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(12000, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "wave2"
    )

    val rotation by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(
            animation = tween(30000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "rotation"
    )

    val scale by infiniteTransition.animateFloat(
        initialValue = 0.8f,
        targetValue = 1.2f,
        animationSpec = infiniteRepeatable(
            animation = tween(10000, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "scale"
    )

    Box(modifier = Modifier.fillMaxSize()) {
        // Define Kenyan theme colors
        val kenyaGreen = Color(0xFF006600)
        val kenyaRed = Color(0xFFBF0000)
        val kenyaBlack = Color(0xFF000000)
        val kenyaGold = Color(0xFFFFD700)

        // Savannah wave pattern - bottom
        Canvas(modifier = Modifier.fillMaxSize()) {
            val canvasWidth = size.width
            val canvasHeight = size.height

            val path = Path().apply {
                moveTo(0f, canvasHeight)

                // Create a wavy pattern for the bottom
                val segments = 5
                val waveHeight = canvasHeight * 0.15f
                val segmentWidth = canvasWidth / segments

                for (i in 0..segments) {
                    val x = i * segmentWidth
                    val phase = (wave1 + i * 0.2f) % 1f
                    val y = canvasHeight - waveHeight * sin(phase * PI.toFloat())

                    lineTo(x, y)
                }

                lineTo(canvasWidth, canvasHeight)
                close()
            }

            drawPath(
                path = path,
                brush = Brush.verticalGradient(
                    colors = listOf(
                        kenyaGold.copy(alpha = 0.8f),
                        kenyaGold.copy(alpha = 0.2f)
                    ),
                    startY = canvasHeight * 0.7f,
                    endY = canvasHeight
                )
            )
        }

        // Red accent streaks
        Canvas(modifier = Modifier.fillMaxSize()) {
            val canvasWidth = size.width
            val canvasHeight = size.height

            val path = Path().apply {
                moveTo(-canvasWidth * 0.2f, canvasHeight * 0.65f)

                cubicTo(
                    canvasWidth * 0.2f, canvasHeight * (0.6f + wave2 * 0.1f),
                    canvasWidth * 0.6f, canvasHeight * (0.75f - wave1 * 0.1f),
                    canvasWidth * 1.2f, canvasHeight * 0.7f
                )

                lineTo(canvasWidth * 1.2f, canvasHeight * 0.8f)

                cubicTo(
                    canvasWidth * 0.6f, canvasHeight * (0.85f - wave1 * 0.1f),
                    canvasWidth * 0.2f, canvasHeight * (0.7f + wave2 * 0.1f),
                    -canvasWidth * 0.2f, canvasHeight * 0.75f
                )

                close()
            }

            drawPath(
                path = path,
                brush = Brush.horizontalGradient(
                    colors = listOf(
                        kenyaRed.copy(alpha = 0.7f),
                        kenyaRed.copy(alpha = 0.9f),
                        kenyaRed.copy(alpha = 0.7f)
                    )
                )
            )
        }

        // Green forest elements - left side
        Canvas(modifier = Modifier
            .fillMaxSize()
            .graphicsLayer {
                this.scaleX = scale
                this.scaleY = scale
            }
        ) {
            val canvasWidth = size.width
            val canvasHeight = size.height

            val pathLeft = Path().apply {
                moveTo(0f, canvasHeight * 0.6f)

                // Create triangle-like shapes to represent acacia trees
                for (i in 0 until 6) {
                    val x = canvasWidth * 0.15f
                    val baseY = canvasHeight * (0.6f - i * 0.1f)
                    val width = canvasWidth * (0.25f - i * 0.03f)
                    val height = canvasHeight * (0.12f - i * 0.015f)

                    moveTo(x - width/2, baseY)
                    lineTo(x, baseY - height)
                    lineTo(x + width/2, baseY)
                    close()
                }
            }

            drawPath(
                path = pathLeft,
                brush = Brush.verticalGradient(
                    colors = listOf(
                        kenyaGreen.copy(alpha = 0.9f),
                        kenyaGreen.copy(alpha = 0.6f)
                    )
                )
            )
        }

        // Sun/shield element
        Canvas(modifier = Modifier.fillMaxSize()) {
            val canvasWidth = size.width
            val canvasHeight = size.height

            // Position of the sun/shield
            val centerX = canvasWidth * 0.7f
            val centerY = canvasHeight * 0.3f
            val radius = canvasWidth * 0.15f

            withTransform({
                translate(centerX, centerY)
                rotate(rotation)
            }) {
                // Shield/sun shape
                drawCircle(
                    brush = Brush.radialGradient(
                        colors = listOf(
                            kenyaGold,
                            kenyaGold.copy(alpha = 0.6f),
                            kenyaGold.copy(alpha = 0f)
                        ),
                        radius = radius * 1.5f
                    ),
                    radius = radius
                )

                // Spear-like rays
                val rayCount = 8
                val rayLength = radius * 0.6f

                for (i in 0 until rayCount) {
                    val angle = (i * 360f / rayCount) * (PI / 180f).toFloat()
                    val startX = (radius * 0.8f) * cos(angle)
                    val startY = (radius * 0.8f) * sin(angle)
                    val endX = (radius + rayLength) * cos(angle)
                    val endY = (radius + rayLength) * sin(angle)

                    drawLine(
                        color = kenyaBlack.copy(alpha = 0.7f),
                        start = Offset(startX, startY),
                        end = Offset(endX, endY),
                        strokeWidth = 12f,
                        cap = StrokeCap.Round
                    )
                }
            }
        }

        // Black accent line - representing the black stripe in the flag
        Canvas(modifier = Modifier.fillMaxSize()) {
            val canvasWidth = size.width
            val canvasHeight = size.height

            val path = Path().apply {
                moveTo(0f, canvasHeight * 0.45f)

                cubicTo(
                    canvasWidth * 0.3f, canvasHeight * (0.4f + wave1 * 0.05f),
                    canvasWidth * 0.7f, canvasHeight * (0.5f - wave2 * 0.05f),
                    canvasWidth, canvasHeight * 0.47f
                )

                lineTo(canvasWidth, canvasHeight * 0.5f)

                cubicTo(
                    canvasWidth * 0.7f, canvasHeight * (0.53f - wave2 * 0.05f),
                    canvasWidth * 0.3f, canvasHeight * (0.43f + wave1 * 0.05f),
                    0f, canvasHeight * 0.48f
                )

                close()
            }

            drawPath(
                path = path,
                color = kenyaBlack.copy(alpha = 0.6f)
            )
        }
    }
}*/

@Composable
fun BackgroundAnimations() {
    val infiniteTransition = rememberInfiniteTransition(label = "kenyan_background_animation")

    // Simplified animations
    val wave1 by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(10000, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "wave1"
    )

    val rotation by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(
            animation = tween(40000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "rotation"
    )

    // Define Kenyan theme colors with lighter opacities
    val kenyaGreen = Color(0xFF006600).copy(alpha = 0.6f)
    val kenyaRed = Color(0xFFBF0000).copy(alpha = 0.5f)
    val kenyaBlack = Color(0xFF000000).copy(alpha = 0.4f)
    val kenyaGold = Color(0xFFFFD700).copy(alpha = 0.5f)

    Box(modifier = Modifier.fillMaxSize()) {
        // Subtle Savannah wave pattern - bottom
        Canvas(modifier = Modifier.fillMaxSize()) {
            val canvasWidth = size.width
            val canvasHeight = size.height

            val path = Path().apply {
                moveTo(0f, canvasHeight)

                // Simplified wavy pattern
                val segments = 4
                val waveHeight = canvasHeight * 0.1f
                val segmentWidth = canvasWidth / segments

                for (i in 0..segments) {
                    val x = i * segmentWidth
                    val phase = (wave1 + i * 0.2f) % 1f
                    val y = canvasHeight - waveHeight * sin(phase * PI.toFloat())
                    lineTo(x, y)
                }

                lineTo(canvasWidth, canvasHeight)
                close()
            }

            drawPath(
                path = path,
                brush = Brush.verticalGradient(
                    colors = listOf(
                        kenyaGold.copy(alpha = 0.4f),
                        kenyaGold.copy(alpha = 0.1f)
                    ),
                    startY = canvasHeight * 0.8f,
                    endY = canvasHeight
                )
            )
        }

        // Single red accent streak (simplified from multiple)
        Canvas(modifier = Modifier.fillMaxSize()) {
            val canvasWidth = size.width
            val canvasHeight = size.height

            val path = Path().apply {
                moveTo(-canvasWidth * 0.2f, canvasHeight * 0.65f)
                cubicTo(
                    canvasWidth * 0.2f, canvasHeight * 0.6f,
                    canvasWidth * 0.6f, canvasHeight * 0.7f,
                    canvasWidth * 1.2f, canvasHeight * 0.65f
                )
                lineTo(canvasWidth * 1.2f, canvasHeight * 0.7f)
                cubicTo(
                    canvasWidth * 0.6f, canvasHeight * 0.75f,
                    canvasWidth * 0.2f, canvasHeight * 0.65f,
                    -canvasWidth * 0.2f, canvasHeight * 0.7f
                )
                close()
            }

            drawPath(
                path = path,
                brush = Brush.horizontalGradient(
                    colors = listOf(
                        kenyaRed.copy(alpha = 0.3f),
                        kenyaRed.copy(alpha = 0.5f),
                        kenyaRed.copy(alpha = 0.3f)
                    )
                )
            )
        }

        // Simplified sun/shield element
        Canvas(modifier = Modifier.fillMaxSize()) {
            val canvasWidth = size.width
            val canvasHeight = size.height

            val centerX = canvasWidth * 0.7f
            val centerY = canvasHeight * 0.3f
            val radius = canvasWidth * 0.1f

            withTransform({
                translate(centerX, centerY)
                rotate(rotation)
            }) {
                // Softer sun effect
                drawCircle(
                    brush = Brush.radialGradient(
                        colors = listOf(
                            kenyaGold.copy(alpha = 0.4f),
                            kenyaGold.copy(alpha = 0.2f),
                            kenyaGold.copy(alpha = 0f)
                        ),
                        radius = radius * 1.2f
                    ),
                    radius = radius
                )

                // Fewer rays with lighter color
                val rayCount = 6
                val rayLength = radius * 0.5f

                for (i in 0 until rayCount) {
                    val angle = (i * 360f / rayCount) * (PI / 180f).toFloat()
                    val startX = radius * cos(angle)
                    val startY = radius * sin(angle)
                    val endX = (radius + rayLength) * cos(angle)
                    val endY = (radius + rayLength) * sin(angle)

                    drawLine(
                        color = kenyaBlack.copy(alpha = 0.3f),
                        start = Offset(startX, startY),
                        end = Offset(endX, endY),
                        strokeWidth = 8f,
                        cap = StrokeCap.Round
                    )
                }
            }
        }

        // Simplified black accent line
        Canvas(modifier = Modifier.fillMaxSize()) {
            val canvasWidth = size.width
            val canvasHeight = size.height

            drawLine(
                brush = Brush.horizontalGradient(
                    colors = listOf(
                        Color.Transparent,
                        kenyaBlack,
                        Color.Transparent
                    )
                ),
                start = Offset(0f, canvasHeight * 0.45f),
                end = Offset(canvasWidth, canvasHeight * 0.47f),
                strokeWidth = 8f,
                alpha = 0.3f
            )
        }
    }
}
