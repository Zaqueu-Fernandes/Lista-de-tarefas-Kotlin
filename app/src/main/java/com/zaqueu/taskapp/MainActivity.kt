package com.zaqueu.taskapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.compose.rememberNavController
import com.zaqueu.taskapp.navigation.TaskNavGraph
import com.zaqueu.taskapp.ui.theme.TaskAppTheme
import com.zaqueu.taskapp.viewmodel.TaskViewModel

/**
 * Ponto de entrada da aplicação.
 *
 * Responsabilidades desta classe:
 * - Habilitar o modo edge-to-edge (tela inteira).
 * - Instanciar o [TaskViewModel] via [ViewModelProvider] com a Factory customizada.
 * - Configurar o tema [TaskAppTheme] e o [TaskNavGraph].
 *
 * A Activity não conhece os detalhes das telas — toda a navegação é gerenciada
 * pelo [TaskNavGraph], e o estado pelo [TaskViewModel].
 */
class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        // Instancia o ViewModel com a Factory (necessário por usar Application)
        val viewModel = ViewModelProvider(
            this,
            TaskViewModel.Factory(application)
        )[TaskViewModel::class.java]

        setContent {
            TaskAppTheme {
                val navController = rememberNavController()
                TaskNavGraph(
                    navController = navController,
                    viewModel = viewModel
                )
            }
        }
    }
}
