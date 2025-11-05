package duoc.desarrollomobile.sitioejemplo.data

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * Entity de Room que representa la tabla de recordatorios en SQLite
 * Cada instancia de esta clase es una fila en la tabla
 *
 * @Entity indica que esta clase es una tabla de Room
 * tableName define el nombre de la tabla en la base de datos
 */
@Entity(tableName = "recordatorios")
data class Recordatorio(
    /**
     * @PrimaryKey indica que este campo es la clave primaria
     * autoGenerate = true hace que Room genere automáticamente un ID único
     * Inicializamos con 0 porque Room lo reemplazará con el siguiente ID disponible
     */
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,

    /**
     * Título del recordatorio (ej: "Prueba de Matemáticas")
     * Campo obligatorio, validado en ViewModel
     */
    val titulo: String,

    /**
     * Descripción detallada del recordatorio
     * Campo obligatorio, mínimo 10 caracteres
     */
    val descripcion: String,

    /**
     * Categoría del recordatorio (PRUEBA, TAREA, ESTUDIO, PROYECTO, OTRO)
     * Se guarda como String en la base de datos
     */
    val categoria: String,

    /**
     * Fecha límite del recordatorio en milisegundos (timestamp)
     * Se puede convertir a LocalDate para mostrar en UI
     */
    val fechaLimite: Long,

    /**
     * Hora del recordatorio en formato "HH:mm" (ej: "14:30")
     * Se usa para programar notificaciones
     */
    val horaRecordatorio: String,

    /**
     * Prioridad del recordatorio (ALTA, MEDIA, BAJA)
     * Se guarda como String en la base de datos
     */
    val prioridad: String,

    /**
     * Indica si el recordatorio está completado
     * false = pendiente, true = completado
     */
    val completado: Boolean = false,

    /**
     * Indica si la notificación está activa para este recordatorio
     * true = notificar, false = no notificar
     */
    val notificacionActiva: Boolean = true,

    /**
     * Fecha de creación del recordatorio en milisegundos (timestamp)
     * Se asigna automáticamente al crear el recordatorio
     */
    val fechaCreacion: Long = System.currentTimeMillis()
)
