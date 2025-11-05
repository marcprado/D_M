package duoc.desarrollomobile.sitioejemplo.utils

import android.content.Context
import android.content.Intent
import duoc.desarrollomobile.sitioejemplo.data.Recordatorio
import duoc.desarrollomobile.sitioejemplo.model.Categoria
import duoc.desarrollomobile.sitioejemplo.model.Prioridad
import java.text.SimpleDateFormat
import java.util.*

/**
 * Helper para compartir recordatorios usando el Intent de Android
 * Permite compartir recordatorios a través de WhatsApp, Email, SMS, etc.
 */
object ShareHelper {

    /**
     * Comparte un recordatorio usando el Intent.ACTION_SEND de Android
     * El usuario podrá elegir qué aplicación usar para compartir
     *
     * @param context Contexto de Android
     * @param recordatorio Recordatorio a compartir
     */
    fun compartirRecordatorio(context: Context, recordatorio: Recordatorio) {
        val categoria = Categoria.fromString(recordatorio.categoria)
        val prioridad = Prioridad.fromString(recordatorio.prioridad)

        // Formatear la fecha
        val dateFormat = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
        val fechaFormateada = dateFormat.format(Date(recordatorio.fechaLimite))

        // Crear el texto a compartir
        val textoCompartir = buildString {
            appendLine("📚 RECORDATORIO DE ESTUDIO")
            appendLine("━━━━━━━━━━━━━━━━━━━━━━")
            appendLine()
            appendLine("📝 Título:")
            appendLine(recordatorio.titulo)
            appendLine()
            appendLine("📄 Descripción:")
            appendLine(recordatorio.descripcion)
            appendLine()
            appendLine("📂 Categoría: ${categoria.displayName}")
            appendLine("🚩 Prioridad: ${prioridad.displayName}")
            appendLine()
            appendLine("📅 Fecha límite: $fechaFormateada")
            appendLine("⏰ Hora: ${recordatorio.horaRecordatorio}")
            appendLine()
            if (recordatorio.notificacionActiva) {
                appendLine("🔔 Notificaciones activadas")
            } else {
                appendLine("🔕 Notificaciones desactivadas")
            }
            appendLine()
            appendLine("━━━━━━━━━━━━━━━━━━━━━━")
            appendLine("Compartido desde StudyReminder 📱")
        }

        // Crear el Intent de compartir
        val sendIntent = Intent().apply {
            action = Intent.ACTION_SEND
            putExtra(Intent.EXTRA_TEXT, textoCompartir)
            type = "text/plain"
        }

        // Crear el chooser para que el usuario elija la app
        val shareIntent = Intent.createChooser(sendIntent, "Compartir recordatorio")

        // Iniciar la actividad
        context.startActivity(shareIntent)
    }

    /**
     * Comparte múltiples recordatorios a la vez
     * Útil para compartir una lista de recordatorios pendientes
     *
     * @param context Contexto de Android
     * @param recordatorios Lista de recordatorios a compartir
     */
    fun compartirListaRecordatorios(context: Context, recordatorios: List<Recordatorio>) {
        val dateFormat = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())

        val textoCompartir = buildString {
            appendLine("📚 LISTA DE RECORDATORIOS")
            appendLine("━━━━━━━━━━━━━━━━━━━━━━")
            appendLine()

            recordatorios.forEachIndexed { index, recordatorio ->
                val categoria = Categoria.fromString(recordatorio.categoria)
                val fechaFormateada = dateFormat.format(Date(recordatorio.fechaLimite))

                appendLine("${index + 1}. ${recordatorio.titulo}")
                appendLine("   ${categoria.displayName} • $fechaFormateada • ${recordatorio.horaRecordatorio}")
                appendLine()
            }

            appendLine("━━━━━━━━━━━━━━━━━━━━━━")
            appendLine("Total: ${recordatorios.size} recordatorios")
            appendLine("Compartido desde StudyReminder 📱")
        }

        val sendIntent = Intent().apply {
            action = Intent.ACTION_SEND
            putExtra(Intent.EXTRA_TEXT, textoCompartir)
            type = "text/plain"
        }

        val shareIntent = Intent.createChooser(sendIntent, "Compartir recordatorios")
        context.startActivity(shareIntent)
    }
}
