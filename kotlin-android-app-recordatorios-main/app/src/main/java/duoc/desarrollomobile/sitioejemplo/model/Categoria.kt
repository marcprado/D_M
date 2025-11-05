package duoc.desarrollomobile.sitioejemplo.model

/**
 * Enum que representa las categorías de recordatorios
 * Cada recordatorio debe tener una categoría asignada
 */
enum class Categoria(val displayName: String) {
    PRUEBA("Prueba/Examen"),
    TAREA("Tarea/Trabajo"),
    ESTUDIO("Estudio"),
    PROYECTO("Proyecto"),
    OTRO("Otro");

    companion object {
        /**
         * Convierte un String a Categoria
         * @param value Nombre de la categoría como String
         * @return Categoria correspondiente o OTRO si no se encuentra
         */
        fun fromString(value: String): Categoria {
            return entries.find { it.name == value } ?: OTRO
        }

        /**
         * Obtiene todas las categorías disponibles
         * @return Lista de nombres de categorías para mostrar en UI
         */
        fun getAllDisplayNames(): List<String> {
            return entries.map { it.displayName }
        }
    }
}
