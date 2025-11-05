package duoc.desarrollomobile.sitioejemplo.data

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

/**
 * DAO (Data Access Object) define las operaciones de acceso a la base de datos
 * @Dao indica que esta interface contiene métodos de acceso a datos
 * Room implementará automáticamente esta interface en tiempo de compilación
 */
@Dao
interface RecordatorioDao {

    /**
     * Inserta un nuevo recordatorio en la base de datos
     * @Insert genera automáticamente el query INSERT
     *
     * onConflict = OnConflictStrategy.REPLACE significa que si ya existe
     * un recordatorio con el mismo ID, lo reemplazará
     *
     * suspend indica que esta función es asíncrona
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertRecordatorio(recordatorio: Recordatorio)

    /**
     * Actualiza un recordatorio existente en la base de datos
     * @Update genera automáticamente el query UPDATE
     * Room identifica el registro por su Primary Key (id)
     */
    @Update
    suspend fun updateRecordatorio(recordatorio: Recordatorio)

    /**
     * Elimina un recordatorio de la base de datos
     * @Delete genera automáticamente el query DELETE
     * Room identifica el registro por su Primary Key (id)
     */
    @Delete
    suspend fun deleteRecordatorio(recordatorio: Recordatorio)

    /**
     * Obtiene todos los recordatorios ordenados por fecha límite ascendente
     * Flow emite automáticamente la lista actualizada cada vez que la tabla cambia
     *
     * No necesita suspend porque Flow ya es asíncrono por naturaleza
     */
    @Query("SELECT * FROM recordatorios ORDER BY fechaLimite ASC")
    fun getAllRecordatorios(): Flow<List<Recordatorio>>

    /**
     * Obtiene solo los recordatorios pendientes (no completados)
     * Ordenados por fecha límite ascendente
     */
    @Query("SELECT * FROM recordatorios WHERE completado = 0 ORDER BY fechaLimite ASC")
    fun getRecordatoriosPendientes(): Flow<List<Recordatorio>>

    /**
     * Obtiene solo los recordatorios completados
     * Ordenados por fecha de creación descendente (más recientes primero)
     */
    @Query("SELECT * FROM recordatorios WHERE completado = 1 ORDER BY fechaCreacion DESC")
    fun getRecordatoriosCompletados(): Flow<List<Recordatorio>>

    /**
     * Obtiene un recordatorio específico por su ID
     * :id es un parámetro que se reemplaza con el valor que pasemos
     *
     * @return Recordatorio o null si no existe
     */
    @Query("SELECT * FROM recordatorios WHERE id = :id")
    suspend fun getRecordatorioById(id: Int): Recordatorio?

    /**
     * Obtiene recordatorios filtrados por categoría
     * Útil para mostrar solo recordatorios de un tipo específico
     */
    @Query("SELECT * FROM recordatorios WHERE categoria = :categoria ORDER BY fechaLimite ASC")
    fun getRecordatoriosByCategoria(categoria: String): Flow<List<Recordatorio>>

    /**
     * Obtiene recordatorios filtrados por prioridad
     */
    @Query("SELECT * FROM recordatorios WHERE prioridad = :prioridad ORDER BY fechaLimite ASC")
    fun getRecordatoriosByPrioridad(prioridad: String): Flow<List<Recordatorio>>

    /**
     * Marca un recordatorio como completado
     * Útil para actualizar solo el estado sin modificar otros campos
     */
    @Query("UPDATE recordatorios SET completado = :completado WHERE id = :id")
    suspend fun updateCompletado(id: Int, completado: Boolean)

    /**
     * Elimina todos los recordatorios completados
     * Útil para limpiar recordatorios antiguos
     */
    @Query("DELETE FROM recordatorios WHERE completado = 1")
    suspend fun deleteAllCompletados()

    /**
     * Obtiene el número total de recordatorios pendientes
     * Útil para mostrar estadísticas
     */
    @Query("SELECT COUNT(*) FROM recordatorios WHERE completado = 0")
    suspend fun getCountPendientes(): Int

    /**
     * Obtiene el número total de recordatorios completados
     */
    @Query("SELECT COUNT(*) FROM recordatorios WHERE completado = 1")
    suspend fun getCountCompletados(): Int
}
