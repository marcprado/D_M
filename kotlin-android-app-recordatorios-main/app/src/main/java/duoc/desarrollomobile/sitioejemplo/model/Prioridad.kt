package duoc.desarrollomobile.sitioejemplo.model

/**
 * Enum que representa la prioridad de un recordatorio
 * Los recordatorios se pueden ordenar por prioridad
 * Colores modernos 2025 para mejor visibilidad y diseño contemporáneo
 */
enum class Prioridad(val displayName: String, val colorValue: Long) {
    ALTA("Alta", 0xFFFF5252),     // Rojo vibrante moderno - Urgencia
    MEDIA("Media", 0xFFFFB300),   // Ámbar brillante - Atención
    BAJA("Baja", 0xFF00E676);     // Verde neón - Calma

    companion object {
        /**
         * Convierte un String a Prioridad
         * @param value Nombre de la prioridad como String
         * @return Prioridad correspondiente o BAJA si no se encuentra
         */
        fun fromString(value: String): Prioridad {
            return entries.find { it.name == value } ?: BAJA
        }

        /**
         * Obtiene todas las prioridades disponibles
         * @return Lista de nombres de prioridades para mostrar en UI
         */
        fun getAllDisplayNames(): List<String> {
            return entries.map { it.displayName }
        }
    }
}
