package ngui_maryanne.dissertation.publicparticipationplatform.ui.theme

import android.app.Activity
import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

// Kenyan Flag-Inspired Theme Colors
val KenyaGreen = Color(0xFF006600)  // Green from the flag
val KenyaRed = Color(0xFFBF0000)    // Red from the flag
val KenyaBlack = Color(0xFF000000)  // Black from the flag
val KenyaWhite = Color(0xFFF5F5F5)  // White/off-white
val KenyaGold = Color(0xFFFFD700)   // Gold accent inspired by African jewelry

// Light Theme Colors - Kenya Inspired
private val KenyaLightColorScheme = lightColorScheme(
    primary = KenyaGreen,
    onPrimary = Color.White,
    secondary = KenyaRed,
    onSecondary = Color.White,
    tertiary = KenyaGold,
    background = KenyaWhite,
    surface = Color(0xFFF9F9F9),
    surfaceVariant = Color(0xFFF9F9F9),
    onBackground = KenyaBlack,
    onSurface = KenyaBlack,
)

// Dark Theme Colors - Kenya Inspired
private val KenyaDarkColorScheme = darkColorScheme(
    primary = KenyaGreen,
    onPrimary = Color.White,
    secondary = KenyaRed,
    onSecondary = Color.White,
    tertiary = KenyaGold,
    background = KenyaBlack,
    surface = Color(0xFF1A1A1A),
    surfaceVariant = Color(0xFF1A1A1A),
    onBackground = KenyaWhite,
    onSurface = KenyaWhite,
)

@Composable
fun PublicParticipationPlatformTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    // Dynamic color is available on Android 12+
    dynamicColor: Boolean = false, // Set to false by default to preserve the Kenyan colors
    content: @Composable () -> Unit
) {
    val colorScheme = when {
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            val context = LocalContext.current
            if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        }
        darkTheme -> KenyaDarkColorScheme
        else -> KenyaLightColorScheme
    }

    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window
            window.statusBarColor = colorScheme.primary.toArgb()
            WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = !darkTheme
        }
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}
