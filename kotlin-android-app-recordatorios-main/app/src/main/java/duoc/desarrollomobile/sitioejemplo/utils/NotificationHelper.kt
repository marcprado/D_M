package duoc.desarrollomobile.sitioejemplo.utils

import android.Manifest
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import duoc.desarrollomobile.sitioejemplo.MainActivity
import duoc.desarrollomobile.sitioejemplo.R
import duoc.desarrollomobile.sitioejemplo.data.Recordatorio

/**
 * Helper para gestionar notificaciones de recordatorios
 * Maneja la creación de canales de notificación y el envío de notificaciones
 */
object NotificationHelper {

    private const val CHANNEL_ID = "recordatorios_channel"
    private const val CHANNEL_NAME = "Recordatorios"
    private const val CHANNEL_DESCRIPTION = "Notificaciones de recordatorios de estudio"

    /**
     * Crea el canal de notificación (requerido en Android 8.0+)
     * Debe llamarse al iniciar la aplicación
     *
     * @param context Contexto de Android
     */
    fun createNotificationChannel(context: Context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val importance = NotificationManager.IMPORTANCE_HIGH
            val channel = NotificationChannel(CHANNEL_ID, CHANNEL_NAME, importance).apply {
                description = CHANNEL_DESCRIPTION
                enableLights(true)
                enableVibration(true)
            }

            val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }
    }

    /**
     * Muestra una notificación inmediata para un recordatorio
     * Útil para testing o recordatorios urgentes
     *
     * @param context Contexto de Android
     * @param recordatorio Recordatorio a notificar
     */
    fun showNotification(context: Context, recordatorio: Recordatorio) {
        // Verificar permisos en Android 13+
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ActivityCompat.checkSelfPermission(
                    context,
                    Manifest.permission.POST_NOTIFICATIONS
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                // No hay permiso, no podemos mostrar la notificación
                return
            }
        }

        // Intent para abrir la app cuando se toque la notificación
        val intent = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            putExtra("recordatorioId", recordatorio.id)
        }

        val pendingIntent = PendingIntent.getActivity(
            context,
            recordatorio.id,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        // Construir la notificación
        val notification = NotificationCompat.Builder(context, CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_launcher_foreground) // Asegúrate de tener un ícono
            .setContentTitle("📚 ${recordatorio.titulo}")
            .setContentText(recordatorio.descripcion)
            .setStyle(
                NotificationCompat.BigTextStyle()
                    .bigText("${recordatorio.descripcion}\n\n📅 ${recordatorio.horaRecordatorio}")
            )
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setCategory(NotificationCompat.CATEGORY_REMINDER)
            .setContentIntent(pendingIntent)
            .setAutoCancel(true) // Se elimina al tocarla
            .setVibrate(longArrayOf(0, 500, 250, 500)) // Patrón de vibración
            .build()

        // Mostrar la notificación
        with(NotificationManagerCompat.from(context)) {
            notify(recordatorio.id, notification)
        }
    }

    /**
     * Cancela una notificación específica
     *
     * @param context Contexto de Android
     * @param recordatorioId ID del recordatorio cuya notificación se cancelará
     */
    fun cancelNotification(context: Context, recordatorioId: Int) {
        with(NotificationManagerCompat.from(context)) {
            cancel(recordatorioId)
        }
    }

    /**
     * Verifica si la app tiene permisos de notificación
     * En Android 13+ se requiere permiso explícito
     *
     * @param context Contexto de Android
     * @return true si tiene permisos, false si no
     */
    fun hasNotificationPermission(context: Context): Boolean {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            ActivityCompat.checkSelfPermission(
                context,
                Manifest.permission.POST_NOTIFICATIONS
            ) == PackageManager.PERMISSION_GRANTED
        } else {
            // En versiones anteriores a Android 13, no se necesita permiso
            true
        }
    }

    /**
     * Programa una notificación para una fecha y hora específicas
     * NOTA: En producción, esto debería usar AlarmManager o WorkManager
     * Esta es una implementación simplificada para el ejemplo
     *
     * @param context Contexto de Android
     * @param recordatorio Recordatorio a programar
     */
    fun scheduleNotification(context: Context, recordatorio: Recordatorio) {
        // TODO: Implementar con AlarmManager o WorkManager para notificaciones programadas
        // Por ahora, esta función es un placeholder para futuras mejoras

        /*
        Ejemplo de implementación con AlarmManager:

        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val intent = Intent(context, NotificationReceiver::class.java).apply {
            putExtra("recordatorioId", recordatorio.id)
        }

        val pendingIntent = PendingIntent.getBroadcast(
            context,
            recordatorio.id,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        // Programar la alarma
        alarmManager.setExactAndAllowWhileIdle(
            AlarmManager.RTC_WAKEUP,
            recordatorio.fechaLimite,
            pendingIntent
        )
        */
    }

    /**
     * Cancela una notificación programada
     *
     * @param context Contexto de Android
     * @param recordatorioId ID del recordatorio
     */
    fun cancelScheduledNotification(context: Context, recordatorioId: Int) {
        // TODO: Implementar cancelación con AlarmManager
    }
}
