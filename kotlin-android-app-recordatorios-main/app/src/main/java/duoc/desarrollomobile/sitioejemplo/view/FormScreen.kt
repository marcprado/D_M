package duoc.desarrollomobile.sitioejemplo.view

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import duoc.desarrollomobile.sitioejemplo.model.Categoria
import duoc.desarrollomobile.sitioejemplo.model.Prioridad
import duoc.desarrollomobile.sitioejemplo.ui.components.AppTopBarWithBack
import duoc.desarrollomobile.sitioejemplo.viewmodel.RecordatorioViewModel
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*
import java.util.Calendar
import java.util.TimeZone

/**
 * Pantalla de formulario para crear un nuevo recordatorio
 * Incluye validaciones visuales en tiempo real
 *
 * @param viewModel ViewModel que maneja el estado y la lógica
 * @param onNavigateBack Callback para volver atrás
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FormScreen(
    viewModel: RecordatorioViewModel,
    onNavigateBack: () -> Unit
) {
    // Observar el estado del formulario
    val uiState by viewModel.uiState.collectAsState()

    // Estados para los DatePicker y TimePicker
    var showDatePicker by remember { mutableStateOf(false) }
    var showTimePicker by remember { mutableStateOf(false) }

    // Estados para los dropdowns
    var expandedCategoria by remember { mutableStateOf(false) }
    var expandedPrioridad by remember { mutableStateOf(false) }

    // Snackbar para feedback
    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()

    // Efecto para manejar el guardado exitoso
    LaunchedEffect(uiState.guardadoExitoso) {
        if (uiState.guardadoExitoso) {
            snackbarHostState.showSnackbar(
                message = "Recordatorio creado exitosamente ✓",
                duration = SnackbarDuration.Short
            )
            kotlinx.coroutines.delay(500)  // Pequeño delay para que se vea el snackbar
            viewModel.resetForm()
            onNavigateBack()
        }
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
            AppTopBarWithBack(
                title = "Nuevo Recordatorio",
                onBackClick = onNavigateBack
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Campo de título
            OutlinedTextField(
                value = uiState.titulo,
                onValueChange = { viewModel.onTituloChange(it) },
                label = { Text("Título *") },
                placeholder = { Text("Ej: Prueba de Matemáticas") },
                leadingIcon = {
                    Icon(imageVector = Icons.Default.Title, contentDescription = null)
                },
                isError = uiState.tituloError != null,
                supportingText = {
                    uiState.tituloError?.let { Text(it) }
                },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )

            // Campo de descripción con contador de caracteres
            OutlinedTextField(
                value = uiState.descripcion,
                onValueChange = { viewModel.onDescripcionChange(it) },
                label = { Text("Descripción *") },
                placeholder = { Text("Describe el recordatorio") },
                leadingIcon = {
                    Icon(imageVector = Icons.Default.Description, contentDescription = null)
                },
                isError = uiState.descripcionError != null,
                supportingText = {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(uiState.descripcionError ?: "")
                        Text("${uiState.descripcion.length}/200")
                    }
                },
                modifier = Modifier.fillMaxWidth(),
                minLines = 3,
                maxLines = 5
            )

            // Dropdown de categoría
            ExposedDropdownMenuBox(
                expanded = expandedCategoria,
                onExpandedChange = { expandedCategoria = !expandedCategoria }
            ) {
                OutlinedTextField(
                    value = uiState.categoria,
                    onValueChange = {},
                    readOnly = true,
                    label = { Text("Categoría *") },
                    leadingIcon = {
                        Icon(imageVector = Icons.Default.Folder, contentDescription = null)
                    },
                    trailingIcon = {
                        ExposedDropdownMenuDefaults.TrailingIcon(expanded = expandedCategoria)
                    },
                    isError = uiState.categoriaError != null,
                    supportingText = {
                        uiState.categoriaError?.let { Text(it) }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .menuAnchor(),
                    colors = ExposedDropdownMenuDefaults.outlinedTextFieldColors()
                )

                ExposedDropdownMenu(
                    expanded = expandedCategoria,
                    onDismissRequest = { expandedCategoria = false }
                ) {
                    Categoria.entries.forEach { categoria ->
                        DropdownMenuItem(
                            text = { Text(categoria.displayName) },
                            onClick = {
                                viewModel.onCategoriaChange(categoria.name)
                                expandedCategoria = false
                            }
                        )
                    }
                }
            }

            // Selector de fecha límite
            OutlinedTextField(
                value = if (uiState.fechaLimite != null) {
                    formatFecha(uiState.fechaLimite!!)
                } else {
                    ""
                },
                onValueChange = {},
                readOnly = true,
                label = { Text("Fecha Límite *") },
                placeholder = { Text("Selecciona una fecha") },
                leadingIcon = {
                    Icon(imageVector = Icons.Default.CalendarToday, contentDescription = null)
                },
                trailingIcon = {
                    IconButton(onClick = { showDatePicker = true }) {
                        Icon(imageVector = Icons.Default.EditCalendar, contentDescription = "Seleccionar fecha")
                    }
                },
                isError = uiState.fechaLimiteError != null,
                supportingText = {
                    uiState.fechaLimiteError?.let { Text(it) }
                },
                modifier = Modifier.fillMaxWidth()
            )

            // Campo de hora
            OutlinedTextField(
                value = uiState.horaRecordatorio,
                onValueChange = { viewModel.onHoraChange(it) },
                label = { Text("Hora (HH:mm) *") },
                placeholder = { Text("Ej: 14:30") },
                leadingIcon = {
                    Icon(imageVector = Icons.Default.Schedule, contentDescription = null)
                },
                trailingIcon = {
                    IconButton(onClick = { showTimePicker = true }) {
                        Icon(imageVector = Icons.Default.AccessTime, contentDescription = "Seleccionar hora")
                    }
                },
                isError = uiState.horaError != null,
                supportingText = {
                    uiState.horaError?.let { Text(it) }
                },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )

            // Dropdown de prioridad
            ExposedDropdownMenuBox(
                expanded = expandedPrioridad,
                onExpandedChange = { expandedPrioridad = !expandedPrioridad }
            ) {
                OutlinedTextField(
                    value = uiState.prioridad,
                    onValueChange = {},
                    readOnly = true,
                    label = { Text("Prioridad *") },
                    leadingIcon = {
                        Icon(imageVector = Icons.Default.Flag, contentDescription = null)
                    },
                    trailingIcon = {
                        ExposedDropdownMenuDefaults.TrailingIcon(expanded = expandedPrioridad)
                    },
                    isError = uiState.prioridadError != null,
                    supportingText = {
                        uiState.prioridadError?.let { Text(it) }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .menuAnchor(),
                    colors = ExposedDropdownMenuDefaults.outlinedTextFieldColors()
                )

                ExposedDropdownMenu(
                    expanded = expandedPrioridad,
                    onDismissRequest = { expandedPrioridad = false }
                ) {
                    Prioridad.entries.forEach { prioridad ->
                        DropdownMenuItem(
                            text = { Text(prioridad.displayName) },
                            onClick = {
                                viewModel.onPrioridadChange(prioridad.name)
                                expandedPrioridad = false
                            }
                        )
                    }
                }
            }

            // Switch de notificaciones
            Card(
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Notifications,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.primary
                        )
                        Column {
                            Text(
                                text = "Notificación activa",
                                style = MaterialTheme.typography.titleMedium
                            )
                            Text(
                                text = "Recibir recordatorio a la hora indicada",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                    Switch(
                        checked = uiState.notificacionActiva,
                        onCheckedChange = { viewModel.toggleNotificacion() }
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Botón de guardar
            Button(
                onClick = { viewModel.onGuardarClick() },
                modifier = Modifier.fillMaxWidth(),
                enabled = !uiState.isLoading
            ) {
                if (uiState.isLoading) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(24.dp),
                        color = MaterialTheme.colorScheme.onPrimary
                    )
                } else {
                    Icon(imageVector = Icons.Default.Save, contentDescription = null)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Guardar Recordatorio")
                }
            }
        }
    }

    // DatePicker Dialog
    if (showDatePicker) {
        val datePickerState = rememberDatePickerState(
            initialSelectedDateMillis = uiState.fechaLimite ?: System.currentTimeMillis()
        )

        DatePickerDialog(
            onDismissRequest = { showDatePicker = false },
            confirmButton = {
                TextButton(
                    onClick = {
                        datePickerState.selectedDateMillis?.let { utcMillis ->
                            // El DatePicker devuelve UTC 00:00:00
                            // Necesitamos ajustar al inicio del día en zona horaria local
                            val calendar = Calendar.getInstance()
                            val timeZone = TimeZone.getDefault()

                            // Obtener el offset de zona horaria en este momento
                            val offset = timeZone.getOffset(utcMillis)

                            // Ajustar sumando el offset para obtener la fecha local correcta
                            val localMillis = utcMillis + offset

                            // Establecer la fecha al mediodía local para máxima compatibilidad
                            calendar.timeInMillis = localMillis
                            calendar.set(Calendar.HOUR_OF_DAY, 12)
                            calendar.set(Calendar.MINUTE, 0)
                            calendar.set(Calendar.SECOND, 0)
                            calendar.set(Calendar.MILLISECOND, 0)

                            viewModel.onFechaLimiteChange(calendar.timeInMillis)
                        }
                        showDatePicker = false
                    }
                ) {
                    Text("Aceptar")
                }
            },
            dismissButton = {
                TextButton(onClick = { showDatePicker = false }) {
                    Text("Cancelar")
                }
            }
        ) {
            DatePicker(state = datePickerState)
        }
    }

    // TimePicker Dialog con selector visual
    if (showTimePicker) {
        // Parse hora actual o usar hora por defecto
        val currentTime = try {
            if (uiState.horaRecordatorio.isNotEmpty()) {
                val parts = uiState.horaRecordatorio.split(":")
                Pair(parts[0].toInt(), parts[1].toInt())
            } else {
                val calendar = Calendar.getInstance()
                Pair(calendar.get(Calendar.HOUR_OF_DAY), calendar.get(Calendar.MINUTE))
            }
        } catch (e: Exception) {
            val calendar = Calendar.getInstance()
            Pair(calendar.get(Calendar.HOUR_OF_DAY), calendar.get(Calendar.MINUTE))
        }

        val timePickerState = rememberTimePickerState(
            initialHour = currentTime.first,
            initialMinute = currentTime.second,
            is24Hour = true
        )

        AlertDialog(
            onDismissRequest = { showTimePicker = false },
            confirmButton = {
                TextButton(
                    onClick = {
                        val hora = String.format("%02d:%02d", timePickerState.hour, timePickerState.minute)
                        viewModel.onHoraChange(hora)
                        showTimePicker = false
                    }
                ) {
                    Text("Aceptar")
                }
            },
            dismissButton = {
                TextButton(onClick = { showTimePicker = false }) {
                    Text("Cancelar")
                }
            },
            text = {
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "Seleccionar Hora",
                        style = MaterialTheme.typography.titleLarge,
                        modifier = Modifier.padding(bottom = 16.dp)
                    )
                    TimePicker(
                        state = timePickerState,
                        modifier = Modifier.padding(16.dp)
                    )
                }
            }
        )
    }
}
