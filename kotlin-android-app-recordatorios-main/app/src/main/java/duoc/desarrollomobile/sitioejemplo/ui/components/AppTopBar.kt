package duoc.desarrollomobile.sitioejemplo.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.statusBars
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector

/**
 * TopAppBar principal de la aplicación
 * Componente reutilizable con colores consistentes del tema
 *
 * @param title Título a mostrar en el AppBar
 * @param navigationIcon Icono de navegación opcional (usualmente flecha atrás)
 * @param onNavigationClick Acción al hacer clic en el icono de navegación
 * @param actions Composable para las acciones del AppBar (iconos a la derecha)
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AppTopBar(
    title: String,
    navigationIcon: ImageVector? = null,
    navigationIconDescription: String? = null,
    onNavigationClick: (() -> Unit)? = null,
    actions: @Composable () -> Unit = {}
) {
    // Gradiente moderno para el TopAppBar
    val gradientColors = listOf(
        MaterialTheme.colorScheme.primary,
        MaterialTheme.colorScheme.primaryContainer
    )

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .background(
                brush = Brush.horizontalGradient(
                    colors = gradientColors
                )
            )
    ) {
        TopAppBar(
            title = {
                Text(
                    text = title,
                    style = MaterialTheme.typography.headlineSmall,
                    color = Color.White
                )
            },
            navigationIcon = {
                if (navigationIcon != null && onNavigationClick != null) {
                    IconButton(onClick = onNavigationClick) {
                        Icon(
                            imageVector = navigationIcon,
                            contentDescription = navigationIconDescription,
                            tint = Color.White
                        )
                    }
                }
            },
            actions = { actions() },
            colors = TopAppBarDefaults.topAppBarColors(
                containerColor = Color.Transparent,
                titleContentColor = Color.White,
                navigationIconContentColor = Color.White,
                actionIconContentColor = Color.White
            ),
            windowInsets = WindowInsets.statusBars
        )
    }
}

/**
 * TopAppBar con botón de volver atrás
 * Variante conveniente para pantallas secundarias
 *
 * @param title Título a mostrar
 * @param onBackClick Acción al hacer clic en volver
 * @param actions Composable para acciones adicionales
 */
@Composable
fun AppTopBarWithBack(
    title: String,
    onBackClick: () -> Unit,
    actions: @Composable () -> Unit = {}
) {
    AppTopBar(
        title = title,
        navigationIcon = Icons.AutoMirrored.Filled.ArrowBack,
        navigationIconDescription = "Volver",
        onNavigationClick = onBackClick,
        actions = actions
    )
}
