package duoc.desarrollomobile.sitioejemplo.model

import duoc.desarrollomobile.sitioejemplo.data.Recordatorio
import duoc.desarrollomobile.sitioejemplo.data.RecordatorioDao
import kotlinx.coroutines.flow.Flow

/**
 * Repository que maneja la lógica de negocio de recordatorios
 *
 * En MVVM, el Repository es el intermediario entre el ViewModel y las fuentes de datos
 * Puede combinar datos de múltiples fuentes: Room, API REST, archivos locales, etc.
 *
 * @param recordatorioDao DAO para acceder a la base de datos de recordatorios
 */
class RecordatorioRepository(
    // Pasamos por parámetro recordatorioDao para operaciones CRUD en SQLite
    private val recordatorioDao: RecordatorioDao
) {

    /**
     * Flow es un stream reactivo que emite valores automáticamente cuando cambian
     * Este Flow emite la lista de recordatorios cada vez que se inserta, actualiza o elimina
     * un recordatorio en la base de datos. El ViewModel puede observar estos cambios y
     * actualizar la UI automáticamente sin necesidad de refrescar manualmente.
     */
    val allRecordatorios: Flow<List<Recordatorio>> = recordatorioDao.getAllRecordatorios()

    /**
     * Flow de recordatorios pendientes (no completados)
     */
    val recordatoriosPendientes: Flow<List<Recordatorio>> = recordatorioDao.getRecordatoriosPendientes()

    /**
     * Flow de recordatorios completados
     */
    val recordatoriosCompletados: Flow<List<Recordatorio>> = recordatorioDao.getRecordatoriosCompletados()

    /**
     * Inserta un nuevo recordatorio en la base de datos
     * suspend function porque es una operación asíncrona de base de datos
     *
     * @param recordatorio Recordatorio a insertar
     */
    suspend fun insertRecordatorio(recordatorio: Recordatorio) {
        recordatorioDao.insertRecordatorio(recordatorio)
    }

    /**
     * Actualiza un recordatorio existente en la base de datos
     *
     * @param recordatorio Recordatorio a actualizar
     */
    suspend fun updateRecordatorio(recordatorio: Recordatorio) {
        recordatorioDao.updateRecordatorio(recordatorio)
    }

    /**
     * Elimina un recordatorio de la base de datos
     *
     * @param recordatorio Recordatorio a eliminar
     */
    suspend fun deleteRecordatorio(recordatorio: Recordatorio) {
        recordatorioDao.deleteRecordatorio(recordatorio)
    }

    /**
     * Obtiene un recordatorio específico por su ID
     * También devuelve null si no existe (nullable)
     *
     * @param id ID del recordatorio
     * @return Recordatorio encontrado o null si no existe
     */
    suspend fun getRecordatorioById(id: Int): Recordatorio? {
        return recordatorioDao.getRecordatorioById(id)
    }

    /**
     * Obtiene recordatorios filtrados por categoría
     *
     * @param categoria Categoría a filtrar
     * @return Flow de lista de recordatorios de esa categoría
     */
    fun getRecordatoriosByCategoria(categoria: String): Flow<List<Recordatorio>> {
        return recordatorioDao.getRecordatoriosByCategoria(categoria)
    }

    /**
     * Obtiene recordatorios filtrados por prioridad
     *
     * @param prioridad Prioridad a filtrar
     * @return Flow de lista de recordatorios de esa prioridad
     */
    fun getRecordatoriosByPrioridad(prioridad: String): Flow<List<Recordatorio>> {
        return recordatorioDao.getRecordatoriosByPrioridad(prioridad)
    }

    /**
     * Marca un recordatorio como completado o pendiente
     * Útil para cambiar solo el estado sin modificar otros campos
     *
     * @param id ID del recordatorio
     * @param completado true para marcar como completado, false para pendiente
     */
    suspend fun updateCompletado(id: Int, completado: Boolean) {
        recordatorioDao.updateCompletado(id, completado)
    }

    /**
     * Elimina todos los recordatorios completados
     * Útil para limpiar recordatorios antiguos
     */
    suspend fun deleteAllCompletados() {
        recordatorioDao.deleteAllCompletados()
    }

    /**
     * Obtiene el número total de recordatorios pendientes
     * Útil para mostrar estadísticas
     *
     * @return Cantidad de recordatorios pendientes
     */
    suspend fun getCountPendientes(): Int {
        return recordatorioDao.getCountPendientes()
    }

    /**
     * Obtiene el número total de recordatorios completados
     *
     * @return Cantidad de recordatorios completados
     */
    suspend fun getCountCompletados(): Int {
        return recordatorioDao.getCountCompletados()
    }
}
