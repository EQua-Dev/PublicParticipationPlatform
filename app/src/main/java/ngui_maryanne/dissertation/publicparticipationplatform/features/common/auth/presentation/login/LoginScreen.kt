package ngui_maryanne.dissertation.publicparticipationplatform.features.common.auth.presentation.login

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.scaleIn
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Login
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import ngui_maryanne.dissertation.publicparticipationplatform.R
import ngui_maryanne.dissertation.publicparticipationplatform.components.CustomButton
import ngui_maryanne.dissertation.publicparticipationplatform.components.CustomTextField
import ngui_maryanne.dissertation.publicparticipationplatform.components.PasswordTextField
import ngui_maryanne.dissertation.publicparticipationplatform.data.enums.ButtonIconPosition
import ngui_maryanne.dissertation.publicparticipationplatform.data.enums.UserRole
import ngui_maryanne.dissertation.publicparticipationplatform.ui.components.BackgroundAnimations
import ngui_maryanne.dissertation.publicparticipationplatform.ui.components.LoadingDialog

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Login
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Snackbar
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import ngui_maryanne.dissertation.publicparticipationplatform.ui.theme.KenyaBlack
import ngui_maryanne.dissertation.publicparticipationplatform.ui.theme.KenyaGold
import ngui_maryanne.dissertation.publicparticipationplatform.ui.theme.KenyaGreen
import ngui_maryanne.dissertation.publicparticipationplatform.ui.theme.KenyaRed
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.min
import kotlin.math.sin

@Composable
fun LoginScreen(
    viewModel: LoginViewModel = hiltViewModel(),
    onLoginSuccess: (role: String) -> Unit,
    onForgotPasswordClicked: () -> Unit,
    onRegisterClicked: (role: String) -> Unit
) {
    val state = viewModel.state.value
    val snackbarHostState = remember { SnackbarHostState() }
    val scrollState = rememberScrollState()
    var logoVisible by remember { mutableStateOf(false) }
    LaunchedEffect(Unit) {
        logoVisible = true // Trigger the animation when the screen appears
    }
    Box(
        modifier = Modifier
            .fillMaxSize()

    ) {
        // Background with Kenyan-inspired patterns
//        KenyanBackgroundPattern()

        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(scrollState)
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(48.dp))

            // Logo with animation
            AnimatedVisibility(
                visible = logoVisible,
                enter = fadeIn(animationSpec = tween(1000)) + scaleIn(
                    initialScale = 0.5f,
                    animationSpec = tween(1000)
                )
            ) {
                /*Card(
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    elevation = CardDefaults.cardElevation(4.dp),
                    shape = RoundedCornerShape(16.dp),
                    modifier = Modifier
                        .padding(16.dp)
                        .wrapContentSize()
                ) {*/
                Image(
                    painter = painterResource(id = R.drawable.app_logo), // replace with your drawable
                    contentDescription = "App Logo",
                    modifier = Modifier
                        .size(150.dp)
                        .padding(16.dp) // inner padding inside the card
                        .clip(shape = RoundedCornerShape(12.dp))
                )
//                    }

            }

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = stringResource(id = R.string.login_title),
                style = MaterialTheme.typography.headlineMedium.copy(
                    fontWeight = FontWeight.Bold
                ),
                color = MaterialTheme.colorScheme.onBackground
            )

            Text(
                text = stringResource(id = R.string.login_subtitle),
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.7f),
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(48.dp))

            // Login card
            Card(
                modifier = Modifier
                    .fillMaxWidth(),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surface
                ),
                elevation = CardDefaults.cardElevation(
                    defaultElevation = 4.dp
                ),
                shape = RoundedCornerShape(16.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 8.dp, vertical = 32.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    // Email Field
                    OutlinedTextField(
                        value = state.email,
                        onValueChange = { viewModel.onEvent(LoginEvent.EmailChanged(it)) },
                        label = { Text(stringResource(id = R.string.email_label)) },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp),
                        keyboardOptions = KeyboardOptions(
                            keyboardType = KeyboardType.Email,
                            imeAction = ImeAction.Next
                        ),
                        leadingIcon = {
                            Icon(
                                imageVector = Icons.Default.Email,
                                contentDescription = null
                            )
                        },
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = MaterialTheme.colorScheme.primary,
                            unfocusedBorderColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.12f),
                            focusedLabelColor = MaterialTheme.colorScheme.primary
                        ),
                        singleLine = true
                    )

                    // Password Field
                    var passwordVisible by remember { mutableStateOf(false) }
                    OutlinedTextField(
                        value = state.password,
                        onValueChange = { viewModel.onEvent(LoginEvent.PasswordChanged(it)) },
                        label = { Text(stringResource(id = R.string.password_label)) },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp),
                        keyboardOptions = KeyboardOptions(
                            keyboardType = KeyboardType.Password,
                            imeAction = ImeAction.Done
                        ),
                        visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                        leadingIcon = {
                            Icon(
                                imageVector = Icons.Default.Lock,
                                contentDescription = null
                            )
                        },
                        trailingIcon = {
                            IconButton(onClick = { passwordVisible = !passwordVisible }) {
                                Icon(
                                    imageVector = if (passwordVisible) Icons.Default.Visibility else Icons.Default.VisibilityOff,
                                    contentDescription = if (passwordVisible) "Hide password" else "Show password"
                                )
                            }
                        },
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = MaterialTheme.colorScheme.primary,
                            unfocusedBorderColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.12f),
                            focusedLabelColor = MaterialTheme.colorScheme.primary
                        ),
                        singleLine = true
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    // Forgot Password Link
                    if (state.userRole != UserRole.SUPERADMIN.name) {
                        Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.CenterEnd) {
                            TextButton(
                                onClick = onForgotPasswordClicked,
                                colors = ButtonDefaults.textButtonColors(
                                    contentColor = MaterialTheme.colorScheme.primary
                                )
                            ) {
                                Text(stringResource(id = R.string.forgot_password))
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    // Login Button
                    Button(
                        onClick = { viewModel.onEvent(LoginEvent.Login) },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(56.dp),
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = MaterialTheme.colorScheme.primary
                        ),
                        elevation = ButtonDefaults.buttonElevation(
                            defaultElevation = 2.dp
                        )
                    ) {
                        Row(
                            horizontalArrangement = Arrangement.Center,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = Icons.Default.Login,
                                contentDescription = null
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = stringResource(id = R.string.login),
                                style = MaterialTheme.typography.titleMedium.copy(
                                    fontWeight = FontWeight.Bold
                                )
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Create Account Link
            if (state.userRole == UserRole.CITIZEN.name) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center
                ) {
                    Text(
                        stringResource(id = R.string.dont_have_account),
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.7f)
                    )
                    TextButton(
                        onClick = { onRegisterClicked(state.userRole!!) },
                        colors = ButtonDefaults.textButtonColors(
                            contentColor = MaterialTheme.colorScheme.primary
                        )
                    ) {
                        Text(stringResource(id = R.string.create_account))
                    }
                }
            }
        }

        // Loading dialog
        if (state.isLoading) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.Black.copy(alpha = 0.5f)),
                contentAlignment = Alignment.Center
            ) {
                Card(
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.surface
                    ),
                    elevation = CardDefaults.cardElevation(
                        defaultElevation = 8.dp
                    ),
                    shape = RoundedCornerShape(16.dp)
                ) {
                    Column(
                        modifier = Modifier.padding(24.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        CircularProgressIndicator(
                            color = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(48.dp)
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        Text(
                            text = stringResource(id = R.string.logging_in),
                            style = MaterialTheme.typography.bodyLarge
                        )
                    }
                }
            }
        }

        // Snackbar for errors
        SnackbarHost(
            hostState = snackbarHostState,
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(16.dp)
        ) { data ->
            Snackbar(
                containerColor = MaterialTheme.colorScheme.errorContainer,
                contentColor = MaterialTheme.colorScheme.onErrorContainer,
                snackbarData = data
            )
        }
    }

    // Handle errors and navigation
    LaunchedEffect(state.errorMessage) {
        state.errorMessage?.let { message ->
            snackbarHostState.showSnackbar(message)
        }
    }

    LaunchedEffect(state.isLoginSuccessful) {
        if (state.isLoginSuccessful) {
            onLoginSuccess(state.userRole!!)
        }
    }
}

@Composable
fun KenyanBackgroundPattern() {
    Canvas(modifier = Modifier.fillMaxSize()) {
        val canvasWidth = size.width
        val canvasHeight = size.height

        // Background gradient
        drawRect(
            brush = Brush.verticalGradient(
                colors = listOf(
                    Color(0xFF1A1A2E),
                    Color(0xFF16213E)
                )
            )
        )

        // Kenya flag-inspired decorative elements

        // Green horizontal stripe (bottom)
        drawRect(
            color = KenyaGreen.copy(alpha = 0.2f),
            topLeft = Offset(0f, canvasHeight * 0.85f),
            size = Size(canvasWidth, canvasHeight * 0.15f)
        )

        // Red horizontal stripe (middle-bottom)
        drawRect(
            color = KenyaRed.copy(alpha = 0.2f),
            topLeft = Offset(0f, canvasHeight * 0.7f),
            size = Size(canvasWidth, canvasHeight * 0.15f)
        )

        // Traditional pattern (stylized)
        val patternWidth = canvasWidth / 20
        val patternCount = (canvasWidth / patternWidth).toInt()

        for (i in 0 until patternCount) {
            val x = i * patternWidth

            // Diamond patterns inspired by traditional Kenyan textiles
            val diamondPath = Path().apply {
                moveTo(x, 0f)
                lineTo(x + patternWidth / 2, patternWidth / 2)
                lineTo(x, patternWidth)
                lineTo(x - patternWidth / 2, patternWidth / 2)
                close()
            }

            drawPath(
                path = diamondPath,
                color = when (i % 4) {
                    0 -> KenyaGreen.copy(alpha = 0.1f)
                    1 -> KenyaRed.copy(alpha = 0.1f)
                    2 -> KenyaBlack.copy(alpha = 0.1f)
                    else -> KenyaGold.copy(alpha = 0.1f)
                }
            )
        }
    }
}

@Composable
fun KenyanShieldLogo(modifier: Modifier = Modifier) {
    Box(
        modifier = modifier,
        contentAlignment = Alignment.Center
    ) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            val canvasWidth = size.width
            val canvasHeight = size.height
            val center = Offset(canvasWidth / 2, canvasHeight / 2)
            val radius = min(canvasWidth, canvasHeight) / 2

            // Shield base
            val shieldPath = Path().apply {
                moveTo(center.x, center.y - radius)
                quadraticBezierTo(
                    center.x + radius * 0.9f, center.y,
                    center.x, center.y + radius
                )
                quadraticBezierTo(
                    center.x - radius * 0.9f, center.y,
                    center.x, center.y - radius
                )
                close()
            }

            // Draw shield with gradient
            drawPath(
                path = shieldPath,
                brush = Brush.verticalGradient(
                    colors = listOf(
                        KenyaRed,
                        KenyaRed.copy(alpha = 0.7f)
                    )
                )
            )

            // Border
            drawPath(
                path = shieldPath,
                color = KenyaBlack,
                style = Stroke(width = radius * 0.05f)
            )

            // Middle black band (inspired by Kenyan flag)
            val bandPath = Path().apply {
                moveTo(center.x - radius * 0.7f, center.y - radius * 0.1f)
                lineTo(center.x + radius * 0.7f, center.y - radius * 0.1f)
                lineTo(center.x + radius * 0.7f, center.y + radius * 0.1f)
                lineTo(center.x - radius * 0.7f, center.y + radius * 0.1f)
                close()
            }

            drawPath(
                path = bandPath,
                color = KenyaBlack
            )

            // Spears
            val spearLength = radius * 1.4f
            val spearWidth = radius * 0.08f

            // Left spear
            val leftSpearPath = Path().apply {
                // Shaft
                moveTo(center.x - radius * 0.5f, center.y - spearLength * 0.7f)
                lineTo(center.x - radius * 0.5f + spearWidth, center.y - spearLength * 0.7f)
                lineTo(center.x - radius * 0.5f + spearWidth, center.y + spearLength * 0.7f)
                lineTo(center.x - radius * 0.5f, center.y + spearLength * 0.7f)
                close()

                // Spearhead
                moveTo(center.x - radius * 0.5f - spearWidth * 1.5f, center.y - spearLength * 0.7f - spearWidth * 4)
                lineTo(center.x - radius * 0.5f + spearWidth * 2.5f, center.y - spearLength * 0.7f - spearWidth * 4)
                lineTo(center.x - radius * 0.5f + spearWidth * 0.5f, center.y - spearLength * 0.7f)
                close()
            }

            // Right spear
            val rightSpearPath = Path().apply {
                // Shaft
                moveTo(center.x + radius * 0.5f, center.y - spearLength * 0.7f)
                lineTo(center.x + radius * 0.5f - spearWidth, center.y - spearLength * 0.7f)
                lineTo(center.x + radius * 0.5f - spearWidth, center.y + spearLength * 0.7f)
                lineTo(center.x + radius * 0.5f, center.y + spearLength * 0.7f)
                close()

                // Spearhead
                moveTo(center.x + radius * 0.5f + spearWidth * 1.5f, center.y - spearLength * 0.7f - spearWidth * 4)
                lineTo(center.x + radius * 0.5f - spearWidth * 2.5f, center.y - spearLength * 0.7f - spearWidth * 4)
                lineTo(center.x + radius * 0.5f - spearWidth * 0.5f, center.y - spearLength * 0.7f)
                close()
            }

            drawPath(
                path = leftSpearPath,
                color = KenyaBlack
            )

            drawPath(
                path = rightSpearPath,
                color = KenyaBlack
            )

            // Shield center emblem (stylized sun/star)
            drawCircle(
                color = KenyaGold,
                radius = radius * 0.25f,
                center = center
            )

            // Rays around the sun/star
            val rayCount = 8
            for (i in 0 until rayCount) {
                val angle = (i * 360f / rayCount) * (PI / 180f).toFloat()
                val innerRadius = radius * 0.25f
                val outerRadius = radius * 0.4f

                val startX = center.x + innerRadius * cos(angle)
                val startY = center.y + innerRadius * sin(angle)
                val endX = center.x + outerRadius * cos(angle)
                val endY = center.y + outerRadius * sin(angle)

                drawLine(
                    color = KenyaGold,
                    start = Offset(startX, startY),
                    end = Offset(endX, endY),
                    strokeWidth = radius * 0.06f,
                    cap = StrokeCap.Round
                )
            }
        }
    }
}