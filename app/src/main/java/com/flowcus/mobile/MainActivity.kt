package com.flowcus.mobile

import android.graphics.Color
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.SystemBarStyle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.Composable
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.flowcus.mobile.data.TaskFilter
import com.flowcus.mobile.data.TasksViewModel
import com.flowcus.mobile.ui.CreateTaskScreen
import com.flowcus.mobile.ui.DashboardScreen
import com.flowcus.mobile.ui.NavTab
import com.flowcus.mobile.ui.PlanningScreen
import com.flowcus.mobile.ui.SessionDetailScreen
import com.flowcus.mobile.ui.SignupScreen
import com.flowcus.mobile.ui.TaskDetailScreen
import com.flowcus.mobile.ui.TasksScreen
import com.flowcus.mobile.ui.theme.FlowcusTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // La app es de tema claro: barras del sistema claras con íconos oscuros.
        enableEdgeToEdge(
            statusBarStyle = SystemBarStyle.light(Color.TRANSPARENT, Color.TRANSPARENT),
            navigationBarStyle = SystemBarStyle.light(Color.WHITE, Color.WHITE),
        )
        setContent { FlowcusTheme { FlowcusApp() } }
    }
}

private object Routes {
    const val SIGNUP = "signup"
    const val DASHBOARD = "dashboard"
    const val TASKS = "tasks"
    const val PLANNING = "planning"
    const val CREATE_TASK = "create-task"
    const val TASK = "task/{id}"
    const val SESSION = "session?start={start}"

    fun task(id: Int) = "task/$id"
    fun session(start: Boolean) = "session?start=$start"
}

@Composable
fun FlowcusApp() {
    val navController = rememberNavController()
    val tasksViewModel: TasksViewModel = viewModel()

    // Las pestañas principales comparten una sola entrada en la pila: Atrás desde cualquiera sale de la app.
    val onNavigate: (NavTab) -> Unit = { tab ->
        when (tab) {
            NavTab.DASHBOARD -> navController.switchTab(Routes.DASHBOARD)
            NavTab.PLANNING -> navController.switchTab(Routes.PLANNING)
            NavTab.TASKS -> {
                tasksViewModel.filter = TaskFilter.ALL
                navController.switchTab(Routes.TASKS)
            }
            NavTab.HISTORY -> {
                tasksViewModel.filter = TaskFilter.DONE
                navController.switchTab(Routes.TASKS)
            }
        }
    }

    NavHost(navController = navController, startDestination = Routes.SIGNUP) {
        composable(Routes.SIGNUP) {
            SignupScreen(onSignedUp = {
                navController.navigate(Routes.TASKS) { popUpTo(Routes.SIGNUP) { inclusive = true } }
            })
        }
        composable(Routes.DASHBOARD) {
            DashboardScreen(
                onNavigate = onNavigate,
                onStartBlock = { navController.navigate(Routes.session(start = true)) },
                onLogout = {
                    navController.navigate(Routes.SIGNUP) { popUpTo(0) { inclusive = true } }
                },
            )
        }
        composable(Routes.TASKS) {
            TasksScreen(
                viewModel = tasksViewModel,
                onOpenTask = { id -> navController.navigate(Routes.task(id)) },
                onCreateTask = { navController.navigate(Routes.CREATE_TASK) },
                onNavigate = onNavigate,
            )
        }
        composable(Routes.PLANNING) {
            PlanningScreen(
                onNavigate = onNavigate,
                onOpenSession = { start -> navController.navigate(Routes.session(start)) },
            )
        }
        composable(Routes.CREATE_TASK) {
            CreateTaskScreen(viewModel = tasksViewModel, onDone = { navController.popBackStack() })
        }
        composable(Routes.TASK, arguments = listOf(navArgument("id") { type = NavType.IntType })) { entry ->
            TaskDetailScreen(
                viewModel = tasksViewModel,
                taskId = entry.arguments?.getInt("id") ?: 1,
            )
        }
        composable(
            Routes.SESSION,
            arguments = listOf(navArgument("start") { type = NavType.BoolType; defaultValue = false }),
        ) { entry ->
            SessionDetailScreen(
                startImmediately = entry.arguments?.getBoolean("start") ?: false,
                onBack = { navController.popBackStack() },
            )
        }
    }
}

private fun NavHostController.switchTab(route: String) {
    if (currentDestination?.route == route) return
    navigate(route) {
        popUpTo(graph.id) { inclusive = true }
        launchSingleTop = true
    }
}
