package duoc.desarrollomobile.sitioejemplo.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import duoc.desarrollomobile.sitioejemplo.view.DetailScreen
import duoc.desarrollomobile.sitioejemplo.view.FormScreen
import duoc.desarrollomobile.sitioejemplo.view.HomeScreen
import duoc.desarrollomobile.sitioejemplo.viewmodel.RecordatorioViewModel

/**
 * Rutas de navegación de la aplicación
 * Sealed class para type-safety
 */
sealed class Screen(val route: String) {
    object Home : Screen("home")
    object Form : Screen("form")
    object Detail : Screen("detail/{recordatorioId}") {
        fun createRoute(recordatorioId: Int) = "detail/$recordatorioId"
    }
}

/**
 * Configuración de navegación de la aplicación
 * Define las rutas y transiciones entre pantallas
 *
 * @param navController Controlador de navegación
 * @param viewModel ViewModel compartido entre pantallas
 */
@Composable
fun AppNavigation(
    navController: NavHostController,
    viewModel: RecordatorioViewModel
) {
    NavHost(
        navController = navController,
        startDestination = Screen.Home.route
    ) {
        // Pantalla principal (Home)
        composable(route = Screen.Home.route) {
            HomeScreen(
                viewModel = viewModel,
                onNavigateToForm = {
                    navController.navigate(Screen.Form.route)
                },
                onNavigateToDetail = { recordatorioId ->
                    navController.navigate(Screen.Detail.createRoute(recordatorioId))
                }
            )
        }

        // Pantalla de formulario
        composable(route = Screen.Form.route) {
            FormScreen(
                viewModel = viewModel,
                onNavigateBack = {
                    navController.popBackStack()
                }
            )
        }

        // Pantalla de detalle
        composable(
            route = Screen.Detail.route,
            arguments = listOf(
                navArgument("recordatorioId") {
                    type = NavType.IntType
                }
            )
        ) { backStackEntry ->
            val recordatorioId = backStackEntry.arguments?.getInt("recordatorioId") ?: 0

            DetailScreen(
                recordatorioId = recordatorioId,
                viewModel = viewModel,
                onNavigateBack = {
                    navController.popBackStack()
                }
            )
        }
    }
}
