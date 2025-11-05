package duoc.desarrollomobile.sitioejemplo

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import androidx.core.content.ContextCompat
import androidx.navigation.compose.rememberNavController
import duoc.desarrollomobile.sitioejemplo.data.AppDatabase
import duoc.desarrollomobile.sitioejemplo.model.RecordatorioRepository
import duoc.desarrollomobile.sitioejemplo.navigation.AppNavigation
import duoc.desarrollomobile.sitioejemplo.ui.theme.SitioEjemploTheme
import duoc.desarrollomobile.sitioejemplo.utils.NotificationHelper
import duoc.desarrollomobile.sitioejemplo.viewmodel.RecordatorioViewModel

/**
 * Activity principal de la aplicación StudyReminder
 * Configura la navegación, base de datos, ViewModel y permisos
 */
class MainActivity : ComponentActivity() {

    // Launcher para solicitar permisos de notificación
    private val requestPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted: Boolean ->
        if (isGranted) {
            // Permiso concedido, ahora se pueden enviar notificaciones
        } else {
            // Permiso denegado, la app funcionará sin notificaciones
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Habilitar edge-to-edge para UI moderna
        enableEdgeToEdge()

        // Crear el canal de notificaciones
        NotificationHelper.createNotificationChannel(this)

        // Solicitar permisos de notificación en Android 13+
        requestNotificationPermission()

        // Inicializar la base de datos
        val database = AppDatabase.getDatabase(applicationContext)
        val recordatorioDao = database.recordatorioDao()

        // Crear el Repository
        val repository = RecordatorioRepository(recordatorioDao)

        // Crear el ViewModel
        val viewModel = RecordatorioViewModel(repository)

        setContent {
            SitioEjemploTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    // Controlador de navegación
                    val navController = rememberNavController()

                    // Configurar navegación con el ViewModel
                    AppNavigation(
                        navController = navController,
                        viewModel = viewModel
                    )
                }
            }
        }
    }

    /**
     * Solicita permisos de notificación en Android 13+
     * En versiones anteriores, los permisos se conceden automáticamente
     */
    private fun requestNotificationPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            when {
                ContextCompat.checkSelfPermission(
                    this,
                    Manifest.permission.POST_NOTIFICATIONS
                ) == PackageManager.PERMISSION_GRANTED -> {
                    // Ya tiene permisos, no hacer nada
                }
                else -> {
                    // Solicitar permiso
                    requestPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
                }
            }
        }
    }
}
