package com.zaqueu.taskapp.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.zaqueu.taskapp.data.local.TaskDatabase
import com.zaqueu.taskapp.data.local.entity.TaskEntity
import com.zaqueu.taskapp.repository.TaskRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

/**
 * ViewModel da camada de apresentação.
 *
 * Responsabilidades:
 * - Expor o estado da UI via [StateFlow] (imutável para a View)
 * - Receber eventos da UI (inserir, atualizar, deletar, toggle)
 * - Delegar operações de dados ao [TaskRepository]
 * - Sobreviver a recomposições e rotações de tela
 *
 * Usa [AndroidViewModel] para acessar o [Application] context e construir o banco.
 */
class TaskViewModel(application: Application) : AndroidViewModel(application) {

    private val repository: TaskRepository

    // ─── Lista de tarefas (observado pela TaskListScreen) ─────────────────────
    val tasks: StateFlow<List<TaskEntity>>

    // ─── Tarefa carregada para edição (observado pela TaskFormScreen) ──────────
    private val _selectedTask = MutableStateFlow<TaskEntity?>(null)
    val selectedTask: StateFlow<TaskEntity?> = _selectedTask.asStateFlow()

    // ─── Feedback de erro/validação para a UI ─────────────────────────────────
    private val _uiMessage = MutableStateFlow<String?>(null)
    val uiMessage: StateFlow<String?> = _uiMessage.asStateFlow()

    init {
        val dao = TaskDatabase.getInstance(application).taskDao()
        repository = TaskRepository(dao)

        // Converte o Flow do repositório em StateFlow com valor inicial de lista vazia.
        // SharingStarted.WhileSubscribed(5_000) mantém o Flow ativo por 5s após a
        // última assinatura, evitando recriações desnecessárias ao girar a tela.
        tasks = repository.allTasks.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = emptyList()
        )
    }

    // ─── Carrega uma tarefa específica para edição ────────────────────────────
    fun loadTask(id: Long) {
        viewModelScope.launch {
            _selectedTask.value = repository.getTaskById(id)
        }
    }

    /** Limpa a tarefa selecionada ao sair da tela de formulário. */
    fun clearSelectedTask() {
        _selectedTask.value = null
    }

    // ─── Salva (cria ou atualiza) uma tarefa ─────────────────────────────────
    /**
     * Se [id] == 0L, é uma nova tarefa → Insert.
     * Se [id] > 0L, é edição → Update mantendo os outros campos.
     *
     * @return true se salvou com sucesso, false se o título estava vazio.
     */
    fun saveTask(id: Long, title: String, description: String): Boolean {
        if (title.isBlank()) {
            _uiMessage.value = "O título não pode estar vazio."
            return false
        }

        viewModelScope.launch {
            if (id == 0L) {
                // Nova tarefa
                repository.insertTask(
                    TaskEntity(
                        title = title.trim(),
                        description = description.trim()
                    )
                )
            } else {
                // Edição: recupera o registro atual para preservar os outros campos
                val existing = repository.getTaskById(id)
                if (existing != null) {
                    repository.updateTask(
                        existing.copy(
                            title = title.trim(),
                            description = description.trim()
                        )
                    )
                }
            }
        }
        return true
    }

    // ─── Alterna o status de conclusão ───────────────────────────────────────
    fun toggleCompletion(task: TaskEntity) {
        viewModelScope.launch {
            repository.toggleTaskCompletion(task.id, !task.isCompleted)
        }
    }

    // ─── Remove uma tarefa ────────────────────────────────────────────────────
    fun deleteTask(task: TaskEntity) {
        viewModelScope.launch {
            repository.deleteTask(task)
        }
    }

    /** Consome a mensagem de UI após ela ser exibida. */
    fun consumeMessage() {
        _uiMessage.value = null
    }

    // ─── Factory ─────────────────────────────────────────────────────────────
    /**
     * Factory necessária porque o ViewModel recebe [Application] como parâmetro.
     * Registrada na Activity/Composable via `viewModel(factory = ...)`.
     */
    class Factory(private val application: Application) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            if (modelClass.isAssignableFrom(TaskViewModel::class.java)) {
                return TaskViewModel(application) as T
            }
            throw IllegalArgumentException("ViewModel desconhecido: ${modelClass.name}")
        }
    }
}
