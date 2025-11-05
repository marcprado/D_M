package duoc.desarrollomobile.sitioejemplo.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import duoc.desarrollomobile.sitioejemplo.data.Recordatorio
import duoc.desarrollomobile.sitioejemplo.model.Categoria
import duoc.desarrollomobile.sitioejemplo.model.Prioridad
import duoc.desarrollomobile.sitioejemplo.model.RecordatorioRepository
import duoc.desarrollomobile.sitioejemplo.utils.ValidationUtils
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

/**
 * Data class que representa el estado completo de la UI del formulario
 *
 * ¿Por qué una data class?
 * - Nos permite agrupar TODOS los datos de la UI en un solo objeto
 * - Facilita pasar el estado completo a la View
 * - El compilador genera automáticamente copy(), equals(), toString()
 */
data class RecordatorioUiState(
    val titulo: String = "",
    val descripcion: String = "",
    val categoria: String = "",
    val fechaLimite: Long? = null,
    val horaRecordatorio: String = "",
    val prioridad: String = "",
    val notificacionActiva: Boolean = true,

    // Errores de validación
    val tituloError: String? = null,
    val descripcionError: String? = null,
    val categoriaError: String? = null,
    val fechaLimiteError: String? = null,
    val horaError: String? = null,
    val prioridadError: String? = null,

    // Estado de carga
    val isLoading: Boolean = false,
    val guardadoExitoso: Boolean = false
)

/**
 * ViewModel que maneja toda la lógica de presentación de recordatorios
 *
 * Extiende de ViewModel para:
 * - Sobrevivir cambios de configuración (rotación del teléfono)
 * - Tener un ciclo de vida propio
 * - Cancelar operaciones automáticamente cuando se destruye
 */
class RecordatorioViewModel(
    private val repository: RecordatorioRepository
) : ViewModel() {

    /**
     * Estado mutable PRIVADO del formulario - Solo el ViewModel puede modificarlo
     */
    private val _uiState = MutableStateFlow(RecordatorioUiState())

    /**
     * Estado público INMUTABLE del formulario - La View solo puede observarlo
     */
    val uiState: StateFlow<RecordatorioUiState> = _uiState.asStateFlow()

    /**
     * Flow de todos los recordatorios desde el Repository
     * La View puede observar este Flow para mostrar la lista actualizada automáticamente
     */
    val allRecordatorios = repository.allRecordatorios

    /**
     * Flow de recordatorios pendientes
     */
    val recordatoriosPendientes = repository.recordatoriosPendientes

    /**
     * Flow de recordatorios completados
     */
    val recordatoriosCompletados = repository.recordatoriosCompletados

    /**
     * Se ejecuta cuando el usuario escribe en el campo de título
     * @param titulo Nuevo valor del título
     */
    fun onTituloChange(titulo: String) {
        _uiState.value = _uiState.value.copy(
            titulo = titulo,
            tituloError = null  // Limpiamos el error al escribir
        )
    }

    /**
     * Se ejecuta cuando el usuario escribe en el campo de descripción
     * @param descripcion Nuevo valor de la descripción
     */
    fun onDescripcionChange(descripcion: String) {
        _uiState.value = _uiState.value.copy(
            descripcion = descripcion,
            descripcionError = null
        )
    }

    /**
     * Se ejecuta cuando el usuario selecciona una categoría
     * @param categoria Categoría seleccionada
     */
    fun onCategoriaChange(categoria: String) {
        _uiState.value = _uiState.value.copy(
            categoria = categoria,
            categoriaError = null
        )
    }

    /**
     * Se ejecuta cuando el usuario selecciona una fecha límite
     * @param fechaLimite Fecha límite en milisegundos
     */
    fun onFechaLimiteChange(fechaLimite: Long?) {
        _uiState.value = _uiState.value.copy(
            fechaLimite = fechaLimite,
            fechaLimiteError = null
        )
    }

    /**
     * Se ejecuta cuando el usuario ingresa una hora
     * @param hora Hora en formato HH:mm
     */
    fun onHoraChange(hora: String) {
        _uiState.value = _uiState.value.copy(
            horaRecordatorio = hora,
            horaError = null
        )
    }

    /**
     * Se ejecuta cuando el usuario selecciona una prioridad
     * @param prioridad Prioridad seleccionada
     */
    fun onPrioridadChange(prioridad: String) {
        _uiState.value = _uiState.value.copy(
            prioridad = prioridad,
            prioridadError = null
        )
    }

    /**
     * Alterna el estado de la notificación
     */
    fun toggleNotificacion() {
        _uiState.value = _uiState.value.copy(
            notificacionActiva = !_uiState.value.notificacionActiva
        )
    }

    /**
     * Valida todos los campos del formulario
     * @return true si todos los campos son válidos, false si hay errores
     *
     * Función PRIVADA porque es lógica interna del ViewModel
     */
    private fun validateFields(): Boolean {
        val tituloError = ValidationUtils.getTituloErrorMessage(_uiState.value.titulo)
        val descripcionError = ValidationUtils.getDescripcionErrorMessage(_uiState.value.descripcion)
        val categoriaError = ValidationUtils.getCategoriaErrorMessage(_uiState.value.categoria)
        val fechaLimiteError = ValidationUtils.getFechaLimiteErrorMessage(_uiState.value.fechaLimite)
        val horaError = ValidationUtils.getHoraErrorMessage(_uiState.value.horaRecordatorio)
        val prioridadError = ValidationUtils.getPrioridadErrorMessage(_uiState.value.prioridad)

        // Actualizamos el estado con los errores
        _uiState.value = _uiState.value.copy(
            tituloError = tituloError,
            descripcionError = descripcionError,
            categoriaError = categoriaError,
            fechaLimiteError = fechaLimiteError,
            horaError = horaError,
            prioridadError = prioridadError
        )

        // Retornamos true SOLO si no hay errores
        return tituloError == null &&
                descripcionError == null &&
                categoriaError == null &&
                fechaLimiteError == null &&
                horaError == null &&
                prioridadError == null
    }

    /**
     * Se ejecuta cuando el usuario hace click en el botón de guardar
     *
     * Flujo completo:
     * 1. Valida los campos
     * 2. Si son válidos, activa el estado de loading
     * 3. Crea el recordatorio y lo guarda en la base de datos
     * 4. Actualiza el estado indicando éxito
     */
    fun onGuardarClick() {
        // PASO 1: Validar campos primero
        if (!validateFields()) {
            return  // Si hay errores, no continuamos
        }

        // PASO 2: Iniciar operación asíncrona
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(
                isLoading = true,
                guardadoExitoso = false
            )

            // PASO 3: Crear el recordatorio
            val recordatorio = Recordatorio(
                titulo = _uiState.value.titulo,
                descripcion = _uiState.value.descripcion,
                categoria = _uiState.value.categoria,
                fechaLimite = _uiState.value.fechaLimite!!,
                horaRecordatorio = _uiState.value.horaRecordatorio,
                prioridad = _uiState.value.prioridad,
                completado = false,
                notificacionActiva = _uiState.value.notificacionActiva
            )

            // Guardar en la base de datos
            repository.insertRecordatorio(recordatorio)

            // PASO 4: Actualizar estado indicando éxito
            _uiState.value = _uiState.value.copy(
                isLoading = false,
                guardadoExitoso = true
            )
        }
    }

    /**
     * Marca un recordatorio como completado o pendiente
     *
     * @param recordatorioId ID del recordatorio
     * @param completado true para marcar como completado, false para pendiente
     */
    fun toggleCompletado(recordatorioId: Int, completado: Boolean) {
        viewModelScope.launch {
            repository.updateCompletado(recordatorioId, completado)
        }
    }

    /**
     * Elimina un recordatorio
     *
     * @param recordatorio Recordatorio a eliminar
     */
    fun deleteRecordatorio(recordatorio: Recordatorio) {
        viewModelScope.launch {
            repository.deleteRecordatorio(recordatorio)
        }
    }

    /**
     * Inserta un recordatorio (útil para deshacer eliminación)
     *
     * @param recordatorio Recordatorio a insertar
     */
    fun insertRecordatorio(recordatorio: Recordatorio) {
        viewModelScope.launch {
            repository.insertRecordatorio(recordatorio)
        }
    }

    /**
     * Elimina todos los recordatorios completados
     */
    fun deleteAllCompletados() {
        viewModelScope.launch {
            repository.deleteAllCompletados()
        }
    }

    /**
     * Resetea el formulario al estado inicial
     * Útil después de guardar exitosamente
     */
    fun resetForm() {
        _uiState.value = RecordatorioUiState()
    }

    /**
     * Resetea el flag de guardado exitoso
     * Útil para cerrar mensajes de éxito
     */
    fun resetGuardadoExitoso() {
        _uiState.value = _uiState.value.copy(guardadoExitoso = false)
    }

    /**
     * Obtiene un recordatorio por su ID
     * Función pública para que la View pueda acceder
     *
     * @param id ID del recordatorio
     * @return Recordatorio o null si no existe
     */
    suspend fun getRecordatorioById(id: Int): Recordatorio? {
        return repository.getRecordatorioById(id)
    }
}
