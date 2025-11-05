package duoc.desarrollomobile.sitioejemplo.ui.theme

import android.app.Activity
import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext

// Tema oscuro moderno con paleta 2025
private val DarkColorScheme = darkColorScheme(
    primary = CyanBlue80,
    onPrimary = MidnightCharcoal,
    primaryContainer = CyanBlue40,
    onPrimaryContainer = Color.White,

    secondary = DeepPurple80,
    onSecondary = MidnightCharcoal,
    secondaryContainer = DeepPurple40,
    onSecondaryContainer = Color.White,

    tertiary = TealAccent80,
    onTertiary = MidnightCharcoal,
    tertiaryContainer = TealAccent40,
    onTertiaryContainer = Color.White,

    background = Color(0xFF121212),
    onBackground = Color(0xFFE8E8E8),

    surface = Color(0xFF1E1E1E),
    onSurface = Color(0xFFE8E8E8),
    surfaceVariant = Color(0xFF2A2A2A),
    onSurfaceVariant = SoftSilver,

    outline = Color(0xFF484848),
    outlineVariant = Color(0xFF353535)
)

// Tema claro moderno con paleta 2025
private val LightColorScheme = lightColorScheme(
    primary = CyanBlue40,
    onPrimary = Color.White,
    primaryContainer = CyanBlue80.copy(alpha = 0.3f),
    onPrimaryContainer = Color(0xFF001F3D),

    secondary = DeepPurple40,
    onSecondary = Color.White,
    secondaryContainer = DeepPurple80.copy(alpha = 0.3f),
    onSecondaryContainer = Color(0xFF1E0054),

    tertiary = TealAccent40,
    onTertiary = Color.White,
    tertiaryContainer = TealAccent80.copy(alpha = 0.3f),
    onTertiaryContainer = Color(0xFF002019),

    background = Color(0xFFFAFAFA),
    onBackground = Color(0xFF1A1A1A),

    surface = Color.White,
    onSurface = Color(0xFF1A1A1A),
    surfaceVariant = Color(0xFFF4F4F4),
    onSurfaceVariant = Color(0xFF4A4A4A),

    outline = Color(0xFFBDBDBD),
    outlineVariant = Color(0xFFE0E0E0)
)

@Composable
fun SitioEjemploTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    // Dynamic color desactivado para usar nuestra paleta personalizada 2025
    dynamicColor: Boolean = false,
    content: @Composable () -> Unit
) {
    val colorScheme = when {
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            val context = LocalContext.current
            if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        }

        darkTheme -> DarkColorScheme
        else -> LightColorScheme
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}