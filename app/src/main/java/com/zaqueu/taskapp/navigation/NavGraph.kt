package com.zaqueu.taskapp.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.zaqueu.taskapp.ui.screens.TaskFormScreen
import com.zaqueu.taskapp.ui.screens.TaskListScreen
import com.zaqueu.taskapp.viewmodel.TaskViewModel

/**
 * Define as rotas de navegação do aplicativo.
 *
 * Rotas disponíveis:
 * - [Screen.TaskList]  → Tela principal com a lista de tarefas.
 * - [Screen.TaskForm]  → Formulário de cadastro/edição.
 *   Aceita argumento opcional `taskId` (Long, default = 0 = nova tarefa).
 */
object Screen {
    const val TASK_LIST = "task_list"
    const val TASK_FORM = "task_form"
    const val TASK_FORM_WITH_ARG = "task_form/{taskId}"
    const val ARG_TASK_ID = "taskId"

    /** Constrói a rota para edição de tarefa existente. */
    fun taskFormRoute(taskId: Long): String = "task_form/$taskId"

    /** Rota para criação de nova tarefa (id = 0). */
    fun newTaskRoute(): String = "task_form/0"
}

/**
 * Gráfico de navegação principal.
 * Toda a lógica de rotas fica centralizada aqui, mantendo as Screens desacopladas.
 */
@Composable
fun TaskNavGraph(
    navController: NavHostController,
    viewModel: TaskViewModel
) {
    NavHost(
        navController = navController,
        startDestination = Screen.TASK_LIST
    ) {

        // ── Tela 1: Lista de Tarefas ─────────────────────────────────────────
        composable(route = Screen.TASK_LIST) {
            TaskListScreen(
                viewModel = viewModel,
                onAddTaskClick = {
                    navController.navigate(Screen.newTaskRoute())
                },
                onTaskClick = { taskId ->
                    navController.navigate(Screen.taskFormRoute(taskId))
                }
            )
        }

        // ── Tela 2: Formulário (Cadastro / Edição) ───────────────────────────
        composable(
            route = Screen.TASK_FORM_WITH_ARG,
            arguments = listOf(
                navArgument(Screen.ARG_TASK_ID) {
                    type = NavType.LongType
                    defaultValue = 0L
                }
            )
        ) { backStackEntry ->
            val taskId = backStackEntry.arguments?.getLong(Screen.ARG_TASK_ID) ?: 0L
            TaskFormScreen(
                taskId = taskId,
                viewModel = viewModel,
                onNavigateBack = {
                    navController.popBackStack()
                }
            )
        }
    }
}
