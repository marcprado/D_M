package duoc.desarrollomobile.sitioejemplo.utils

/**
 * Object que contiene funciones de validación para el formulario de recordatorios
 *
 * object crea un Singleton automáticamente
 * Esto permite usar las funciones sin crear instancias:
 * ValidationUtils.isValidTitulo("Mi recordatorio")
 */
object ValidationUtils {

    /**
     * Valida que el título sea válido
     * Requisitos: no vacío, mínimo 3 caracteres, máximo 50 caracteres
     *
     * @param titulo Título a validar
     * @return true si el título es válido, false si no
     */
    fun isValidTitulo(titulo: String): Boolean {
        // Verificar que no esté vacío
        if (titulo.isBlank()) return false

        // Verificar longitud
        if (titulo.length < 3 || titulo.length > 50) return false

        return true
    }

    /**
     * Valida que la descripción sea válida
     * Requisitos: no vacía, mínimo 10 caracteres, máximo 200 caracteres
     *
     * @param descripcion Descripción a validar
     * @return true si la descripción es válida, false si no
     */
    fun isValidDescripcion(descripcion: String): Boolean {
        // Verificar que no esté vacía
        if (descripcion.isBlank()) return false

        // Verificar longitud
        if (descripcion.length < 10 || descripcion.length > 200) return false

        return true
    }

    /**
     * Valida que la hora tenga formato válido (HH:mm)
     * Ejemplos válidos: "14:30", "09:00", "23:59"
     * Ejemplos inválidos: "25:00", "14:70", "14", "14:3"
     *
     * @param hora Hora a validar
     * @return true si la hora es válida, false si no
     */
    fun isValidHora(hora: String): Boolean {
        if (hora.isBlank()) return false

        /**
         * Regex para validar hora en formato HH:mm:
         * ^              = Inicio
         * ([01][0-9]|2[0-3])  = Horas: 00-19 o 20-23
         * :              = Dos puntos literal
         * [0-5][0-9]     = Minutos: 00-59
         * $              = Fin
         *
         * Válidos: "00:00", "14:30", "23:59"
         * Inválidos: "24:00", "14:60", "5:30"
         */
        val horaRegex = "^([01][0-9]|2[0-3]):[0-5][0-9]$".toRegex()
        return horaRegex.matches(hora)
    }

    /**
     * Valida que la fecha límite sea futura (no puede ser en el pasado)
     *
     * @param fechaLimiteMillis Fecha límite en milisegundos
     * @return true si la fecha es futura, false si es pasada
     */
    fun isValidFechaLimite(fechaLimiteMillis: Long): Boolean {
        // La fecha debe ser mayor que la fecha actual
        return fechaLimiteMillis > System.currentTimeMillis()
    }

    /**
     * Valida que la categoría no esté vacía
     *
     * @param categoria Categoría a validar
     * @return true si la categoría es válida, false si no
     */
    fun isValidCategoria(categoria: String): Boolean {
        return categoria.isNotBlank()
    }

    /**
     * Valida que la prioridad no esté vacía
     *
     * @param prioridad Prioridad a validar
     * @return true si la prioridad es válida, false si no
     */
    fun isValidPrioridad(prioridad: String): Boolean {
        return prioridad.isNotBlank()
    }

    /**
     * Obtiene el mensaje de error para el campo título
     * @param titulo Título a validar
     * @return Mensaje de error o null si es válido
     */
    fun getTituloErrorMessage(titulo: String): String? {
        return when {
            titulo.isBlank() -> "El título es requerido"
            titulo.length < 3 -> "El título debe tener al menos 3 caracteres"
            titulo.length > 50 -> "El título no puede exceder 50 caracteres"
            else -> null
        }
    }

    /**
     * Obtiene el mensaje de error para el campo descripción
     * @param descripcion Descripción a validar
     * @return Mensaje de error o null si es válida
     */
    fun getDescripcionErrorMessage(descripcion: String): String? {
        return when {
            descripcion.isBlank() -> "La descripción es requerida"
            descripcion.length < 10 -> "La descripción debe tener al menos 10 caracteres"
            descripcion.length > 200 -> "La descripción no puede exceder 200 caracteres"
            else -> null
        }
    }

    /**
     * Obtiene el mensaje de error para el campo hora
     * @param hora Hora a validar
     * @return Mensaje de error o null si es válida
     */
    fun getHoraErrorMessage(hora: String): String? {
        return when {
            hora.isBlank() -> "La hora es requerida"
            !isValidHora(hora) -> "Formato de hora inválido. Use HH:mm (ej: 14:30)"
            else -> null
        }
    }

    /**
     * Obtiene el mensaje de error para el campo fecha límite
     * @param fechaLimiteMillis Fecha límite en milisegundos
     * @return Mensaje de error o null si es válida
     */
    fun getFechaLimiteErrorMessage(fechaLimiteMillis: Long?): String? {
        return when {
            fechaLimiteMillis == null -> "La fecha límite es requerida"
            !isValidFechaLimite(fechaLimiteMillis) -> "La fecha límite debe ser futura"
            else -> null
        }
    }

    /**
     * Obtiene el mensaje de error para el campo categoría
     * @param categoria Categoría a validar
     * @return Mensaje de error o null si es válida
     */
    fun getCategoriaErrorMessage(categoria: String): String? {
        return when {
            categoria.isBlank() -> "Debe seleccionar una categoría"
            else -> null
        }
    }

    /**
     * Obtiene el mensaje de error para el campo prioridad
     * @param prioridad Prioridad a validar
     * @return Mensaje de error o null si es válida
     */
    fun getPrioridadErrorMessage(prioridad: String): String? {
        return when {
            prioridad.isBlank() -> "Debe seleccionar una prioridad"
            else -> null
        }
    }
}
