package duoc.desarrollomobile.sitioejemplo.view

import androidx.compose.animation.*
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import duoc.desarrollomobile.sitioejemplo.data.Recordatorio
import duoc.desarrollomobile.sitioejemplo.model.Prioridad
import duoc.desarrollomobile.sitioejemplo.ui.components.AppTopBar
import duoc.desarrollomobile.sitioejemplo.viewmodel.RecordatorioViewModel
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*
import java.util.TimeZone

/**
 * Pantalla principal de la aplicación
 * Muestra la lista de recordatorios con opciones de filtrado
 *
 * @param viewModel ViewModel que maneja el estado y la lógica
 * @param onNavigateToForm Callback para navegar al formulario
 * @param onNavigateToDetail Callback para navegar al detalle de un recordatorio
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    viewModel: RecordatorioViewModel,
    onNavigateToForm: () -> Unit,
    onNavigateToDetail: (Int) -> Unit
) {
    // Observar los recordatorios pendientes desde el ViewModel
    val recordatoriosPendientes by viewModel.recordatoriosPendientes.collectAsState(initial = emptyList())
    val recordatoriosCompletados by viewModel.recordatoriosCompletados.collectAsState(initial = emptyList())

    // Estado para controlar qué filtro está activo
    var filtroActual by remember { mutableStateOf(FiltroRecordatorio.PENDIENTES) }

    // Lista de recordatorios según el filtro activo
    val recordatoriosMostrados = when (filtroActual) {
        FiltroRecordatorio.PENDIENTES -> recordatoriosPendientes
        FiltroRecordatorio.COMPLETADOS -> recordatoriosCompletados
        FiltroRecordatorio.TODOS -> recordatoriosPendientes + recordatoriosCompletados
    }

    // Snackbar para feedback
    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()

    // Estado para diálogos de confirmación
    var showDeleteAllDialog by remember { mutableStateOf(false) }
    var recordatorioToDelete by remember { mutableStateOf<Recordatorio?>(null) }

    // Diálogo de confirmación para eliminar todos los completados
    if (showDeleteAllDialog) {
        AlertDialog(
            onDismissRequest = { showDeleteAllDialog = false },
            icon = {
                Icon(
                    imageVector = Icons.Default.Warning,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.error
                )
            },
            title = {
                Text("Eliminar todos los completados")
            },
            text = {
                Text("¿Estás seguro de que deseas eliminar ${recordatoriosCompletados.size} recordatorios completados? Esta acción no se puede deshacer.")
            },
            confirmButton = {
                Button(
                    onClick = {
                        viewModel.deleteAllCompletados()
                        showDeleteAllDialog = false
                        scope.launch {
                            snackbarHostState.showSnackbar(
                                message = "Se eliminaron ${recordatoriosCompletados.size} recordatorios completados",
                                duration = SnackbarDuration.Short
                            )
                        }
                    },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.error
                    )
                ) {
                    Text("Eliminar")
                }
            },
            dismissButton = {
                TextButton(onClick = { showDeleteAllDialog = false }) {
                    Text("Cancelar")
                }
            }
        )
    }

    // Diálogo de confirmación para eliminar un recordatorio individual
    recordatorioToDelete?.let { recordatorio ->
        AlertDialog(
            onDismissRequest = { recordatorioToDelete = null },
            icon = {
                Icon(
                    imageVector = Icons.Default.Delete,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.error
                )
            },
            title = {
                Text("Eliminar recordatorio")
            },
            text = {
                Text("¿Deseas eliminar \"${recordatorio.titulo}\"? Podrás deshacerlo después.")
            },
            confirmButton = {
                Button(
                    onClick = {
                        val recordatorioTemp = recordatorio
                        viewModel.deleteRecordatorio(recordatorio)
                        recordatorioToDelete = null
                        scope.launch {
                            val result = snackbarHostState.showSnackbar(
                                message = "Recordatorio eliminado",
                                actionLabel = "Deshacer",
                                duration = SnackbarDuration.Long
                            )
                            if (result == SnackbarResult.ActionPerformed) {
                                viewModel.insertRecordatorio(recordatorioTemp)
                            }
                        }
                    },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.error
                    )
                ) {
                    Text("Eliminar")
                }
            },
            dismissButton = {
                TextButton(onClick = { recordatorioToDelete = null }) {
                    Text("Cancelar")
                }
            }
        )
    }

    Scaffold(
        snackbarHost = {
            SnackbarHost(hostState = snackbarHostState) { data ->
                Snackbar(
                    snackbarData = data,
                    containerColor = MaterialTheme.colorScheme.inverseSurface,
                    contentColor = MaterialTheme.colorScheme.inverseOnSurface,
                    actionColor = MaterialTheme.colorScheme.primary
                )
            }
        },
        topBar = {
            AppTopBar(
                title = "StudyReminder",
                actions = {
                    // Botón para eliminar todos los completados
                    if (recordatoriosCompletados.isNotEmpty()) {
                        IconButton(onClick = {
                            showDeleteAllDialog = true
                        }) {
                            Icon(
                                imageVector = Icons.Default.Delete,
                                contentDescription = "Eliminar completados",
                                tint = Color.White
                            )
                        }
                    }
                }
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = onNavigateToForm,
                containerColor = MaterialTheme.colorScheme.secondary,
                elevation = FloatingActionButtonDefaults.elevation(
                    defaultElevation = 8.dp,
                    pressedElevation = 12.dp,
                    hoveredElevation = 10.dp
                )
            ) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = "Agregar recordatorio",
                    tint = Color.White
                )
            }
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            // Chips de filtro
            FiltroChips(
                filtroActual = filtroActual,
                onFiltroChange = { filtroActual = it },
                cantidadPendientes = recordatoriosPendientes.size,
                cantidadCompletados = recordatoriosCompletados.size
            )

            // Lista de recordatorios
            AnimatedVisibility(
                visible = recordatoriosMostrados.isEmpty(),
                enter = fadeIn(animationSpec = tween(300)),
                exit = fadeOut(animationSpec = tween(300))
            ) {
                EmptyState(filtro = filtroActual)
            }

            AnimatedVisibility(
                visible = recordatoriosMostrados.isNotEmpty(),
                enter = fadeIn(animationSpec = tween(300)),
                exit = fadeOut(animationSpec = tween(300))
            ) {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(
                        items = recordatoriosMostrados,
                        key = { it.id }
                    ) { recordatorio ->
                        AnimatedVisibility(
                            visible = true,
                            enter = fadeIn(animationSpec = tween(300)) +
                                    slideInVertically(
                                        animationSpec = spring(
                                            dampingRatio = Spring.DampingRatioMediumBouncy,
                                            stiffness = Spring.StiffnessLow
                                        )
                                    ),
                            exit = fadeOut(animationSpec = tween(300)) +
                                   slideOutVertically(animationSpec = tween(300))
                        ) {
                            RecordatorioItem(
                                recordatorio = recordatorio,
                                onToggleCompletado = {
                                    viewModel.toggleCompletado(recordatorio.id, !recordatorio.completado)
                                    scope.launch {
                                        val mensaje = if (!recordatorio.completado) {
                                            "Recordatorio completado ✓"
                                        } else {
                                            "Recordatorio marcado como pendiente"
                                        }
                                        snackbarHostState.showSnackbar(
                                            message = mensaje,
                                            duration = SnackbarDuration.Short
                                        )
                                    }
                                },
                                onDelete = {
                                    recordatorioToDelete = recordatorio
                                },
                                onClick = { onNavigateToDetail(recordatorio.id) }
                            )
                        }
                    }
                }
            }
        }
    }
}

/**
 * Enum para los filtros disponibles
 */
enum class FiltroRecordatorio {
    PENDIENTES,
    COMPLETADOS,
    TODOS
}

/**
 * Chips de filtro para cambiar entre pendientes, completados y todos
 */
@Composable
fun FiltroChips(
    filtroActual: FiltroRecordatorio,
    onFiltroChange: (FiltroRecordatorio) -> Unit,
    cantidadPendientes: Int,
    cantidadCompletados: Int
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .background(
                brush = Brush.verticalGradient(
                    colors = listOf(
                        MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f),
                        MaterialTheme.colorScheme.surface
                    )
                )
            )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 16.dp),
            horizontalArrangement = Arrangement.spacedBy(10.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            FilterChip(
                selected = filtroActual == FiltroRecordatorio.PENDIENTES,
                onClick = { onFiltroChange(FiltroRecordatorio.PENDIENTES) },
                label = {
                    Text(
                        text = "Pendientes ($cantidadPendientes)",
                        style = MaterialTheme.typography.labelLarge,
                        fontWeight = if (filtroActual == FiltroRecordatorio.PENDIENTES) FontWeight.Bold else FontWeight.Normal
                    )
                },
                leadingIcon = {
                    Icon(
                        imageVector = Icons.Default.Schedule,
                        contentDescription = null,
                        modifier = Modifier.size(18.dp)
                    )
                },
                colors = FilterChipDefaults.filterChipColors(
                    containerColor = MaterialTheme.colorScheme.surface,
                    labelColor = MaterialTheme.colorScheme.onSurface,
                    iconColor = MaterialTheme.colorScheme.primary,
                    selectedContainerColor = MaterialTheme.colorScheme.primaryContainer,
                    selectedLabelColor = MaterialTheme.colorScheme.onPrimaryContainer,
                    selectedLeadingIconColor = MaterialTheme.colorScheme.primary
                ),
                border = FilterChipDefaults.filterChipBorder(
                    enabled = true,
                    selected = filtroActual == FiltroRecordatorio.PENDIENTES,
                    borderColor = MaterialTheme.colorScheme.outline.copy(alpha = 0.3f),
                    selectedBorderColor = MaterialTheme.colorScheme.primary,
                    borderWidth = 1.dp,
                    selectedBorderWidth = 2.dp
                ),
                modifier = Modifier.height(42.dp)
            )

            FilterChip(
                selected = filtroActual == FiltroRecordatorio.COMPLETADOS,
                onClick = { onFiltroChange(FiltroRecordatorio.COMPLETADOS) },
                label = {
                    Text(
                        text = "Completados ($cantidadCompletados)",
                        style = MaterialTheme.typography.labelLarge,
                        fontWeight = if (filtroActual == FiltroRecordatorio.COMPLETADOS) FontWeight.Bold else FontWeight.Normal
                    )
                },
                leadingIcon = {
                    Icon(
                        imageVector = Icons.Default.CheckCircle,
                        contentDescription = null,
                        modifier = Modifier.size(18.dp)
                    )
                },
                colors = FilterChipDefaults.filterChipColors(
                    containerColor = MaterialTheme.colorScheme.surface,
                    labelColor = MaterialTheme.colorScheme.onSurface,
                    iconColor = MaterialTheme.colorScheme.secondary,
                    selectedContainerColor = MaterialTheme.colorScheme.secondaryContainer,
                    selectedLabelColor = MaterialTheme.colorScheme.onSecondaryContainer,
                    selectedLeadingIconColor = MaterialTheme.colorScheme.secondary
                ),
                border = FilterChipDefaults.filterChipBorder(
                    enabled = true,
                    selected = filtroActual == FiltroRecordatorio.COMPLETADOS,
                    borderColor = MaterialTheme.colorScheme.outline.copy(alpha = 0.3f),
                    selectedBorderColor = MaterialTheme.colorScheme.secondary,
                    borderWidth = 1.dp,
                    selectedBorderWidth = 2.dp
                ),
                modifier = Modifier.height(42.dp)
            )

            FilterChip(
                selected = filtroActual == FiltroRecordatorio.TODOS,
                onClick = { onFiltroChange(FiltroRecordatorio.TODOS) },
                label = {
                    Text(
                        text = "Todos",
                        style = MaterialTheme.typography.labelLarge,
                        fontWeight = if (filtroActual == FiltroRecordatorio.TODOS) FontWeight.Bold else FontWeight.Normal
                    )
                },
                leadingIcon = {
                    Icon(
                        imageVector = Icons.Default.List,
                        contentDescription = null,
                        modifier = Modifier.size(18.dp)
                    )
                },
                colors = FilterChipDefaults.filterChipColors(
                    containerColor = MaterialTheme.colorScheme.surface,
                    labelColor = MaterialTheme.colorScheme.onSurface,
                    iconColor = MaterialTheme.colorScheme.tertiary,
                    selectedContainerColor = MaterialTheme.colorScheme.tertiaryContainer,
                    selectedLabelColor = MaterialTheme.colorScheme.onTertiaryContainer,
                    selectedLeadingIconColor = MaterialTheme.colorScheme.tertiary
                ),
                border = FilterChipDefaults.filterChipBorder(
                    enabled = true,
                    selected = filtroActual == FiltroRecordatorio.TODOS,
                    borderColor = MaterialTheme.colorScheme.outline.copy(alpha = 0.3f),
                    selectedBorderColor = MaterialTheme.colorScheme.tertiary,
                    borderWidth = 1.dp,
                    selectedBorderWidth = 2.dp
                ),
                modifier = Modifier.height(42.dp)
            )
        }
    }
}

/**
 * Badge circular de color para los chips
 */
@Composable
fun ColorBadge(color: Color, count: Int) {
    Box(
        modifier = Modifier
            .size(32.dp)
            .clip(CircleShape)
            .background(color),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = count.toString(),
            style = MaterialTheme.typography.labelMedium,
            color = Color.White,
            fontWeight = FontWeight.Bold
        )
    }
}

/**
 * Item individual de recordatorio en la lista
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RecordatorioItem(
    recordatorio: Recordatorio,
    onToggleCompletado: () -> Unit,
    onDelete: () -> Unit,
    onClick: () -> Unit
) {
    val prioridad = Prioridad.fromString(recordatorio.prioridad)
    val colorPrioridad = Color(prioridad.colorValue)

    Card(
        onClick = onClick,
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = if (recordatorio.completado) {
                MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.7f)
            } else {
                MaterialTheme.colorScheme.surface
            }
        ),
        elevation = CardDefaults.cardElevation(
            defaultElevation = 4.dp,
            pressedElevation = 8.dp,
            hoveredElevation = 6.dp
        )
    ) {
        Row(
            modifier = Modifier.fillMaxWidth()
        ) {
            // Barra lateral de color de prioridad
            Box(
                modifier = Modifier
                    .width(6.dp)
                    .fillMaxHeight()
                    .background(
                        brush = Brush.verticalGradient(
                            colors = listOf(
                                colorPrioridad,
                                colorPrioridad.copy(alpha = 0.6f)
                            )
                        )
                    )
            )

            Row(
                modifier = Modifier
                    .weight(1f)
                    .padding(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
            // Checkbox para marcar como completado
            Checkbox(
                checked = recordatorio.completado,
                onCheckedChange = { onToggleCompletado() }
            )

            Spacer(modifier = Modifier.width(12.dp))

            // Contenido del recordatorio
            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = recordatorio.titulo,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    textDecoration = if (recordatorio.completado) TextDecoration.LineThrough else null,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )

                Spacer(modifier = Modifier.height(4.dp))

                Text(
                    text = recordatorio.descripcion,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )

                Spacer(modifier = Modifier.height(8.dp))

                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Chip de categoría
                    AssistChip(
                        onClick = {},
                        label = {
                            Text(
                                text = recordatorio.categoria,
                                style = MaterialTheme.typography.labelSmall
                            )
                        },
                        leadingIcon = {
                            Icon(
                                imageVector = Icons.Default.Folder,
                                contentDescription = null,
                                modifier = Modifier.size(16.dp)
                            )
                        }
                    )

                    // Chip de fecha
                    AssistChip(
                        onClick = {},
                        label = {
                            Text(
                                text = formatFecha(recordatorio.fechaLimite),
                                style = MaterialTheme.typography.labelSmall
                            )
                        },
                        leadingIcon = {
                            Icon(
                                imageVector = Icons.Default.CalendarToday,
                                contentDescription = null,
                                modifier = Modifier.size(16.dp)
                            )
                        }
                    )
                }
            }

                // Botón de eliminar
                IconButton(onClick = onDelete) {
                    Icon(
                        imageVector = Icons.Default.Delete,
                        contentDescription = "Eliminar",
                        tint = MaterialTheme.colorScheme.error
                    )
                }
            }
        }
    }
}

/**
 * Estado vacío cuando no hay recordatorios
 */
@Composable
fun EmptyState(filtro: FiltroRecordatorio) {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Icon(
                imageVector = when (filtro) {
                    FiltroRecordatorio.PENDIENTES -> Icons.Default.Schedule
                    FiltroRecordatorio.COMPLETADOS -> Icons.Default.CheckCircle
                    FiltroRecordatorio.TODOS -> Icons.Default.List
                },
                contentDescription = null,
                modifier = Modifier.size(64.dp),
                tint = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = when (filtro) {
                    FiltroRecordatorio.PENDIENTES -> "No hay recordatorios pendientes"
                    FiltroRecordatorio.COMPLETADOS -> "No hay recordatorios completados"
                    FiltroRecordatorio.TODOS -> "No hay recordatorios"
                },
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = when (filtro) {
                    FiltroRecordatorio.PENDIENTES -> "Crea tu primer recordatorio"
                    FiltroRecordatorio.COMPLETADOS -> "Completa algunos recordatorios"
                    FiltroRecordatorio.TODOS -> "Presiona + para crear uno"
                },
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

/**
 * Formatea una fecha en milisegundos a formato legible
 * Usa la zona horaria local del dispositivo
 */
fun formatFecha(millis: Long): String {
    val dateFormat = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
    dateFormat.timeZone = TimeZone.getDefault() // Asegurar zona horaria local
    return dateFormat.format(Date(millis))
}
