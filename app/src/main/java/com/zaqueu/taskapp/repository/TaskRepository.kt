package com.zaqueu.taskapp.repository

import com.zaqueu.taskapp.data.local.dao.TaskDao
import com.zaqueu.taskapp.data.local.entity.TaskEntity
import kotlinx.coroutines.flow.Flow

/**
 * Repositório que abstrai o acesso à camada de dados.
 *
 * Responsabilidade única: expor os dados ao ViewModel de forma limpa,
 * desacoplando a fonte de dados (Room) da lógica de negócio.
 *
 * Em projetos maiores, esta camada poderia unificar dados locais (Room)
 * com dados remotos (Retrofit/API), mas aqui opera apenas localmente.
 */
class TaskRepository(private val taskDao: TaskDao) {

    /**
     * Fluxo reativo com todas as tarefas.
     * O ViewModel observa este Flow e atualiza a UI automaticamente.
     */
    val allTasks: Flow<List<TaskEntity>> = taskDao.getAllTasks()

    /**
     * Busca uma tarefa pelo ID. Retorna null se não encontrada.
     */
    suspend fun getTaskById(id: Long): TaskEntity? = taskDao.getTaskById(id)

    /**
     * Insere uma nova tarefa no banco. Retorna o ID gerado.
     */
    suspend fun insertTask(task: TaskEntity): Long = taskDao.insertTask(task)

    /**
     * Atualiza uma tarefa existente.
     */
    suspend fun updateTask(task: TaskEntity) = taskDao.updateTask(task)

    /**
     * Remove uma tarefa do banco.
     */
    suspend fun deleteTask(task: TaskEntity) = taskDao.deleteTask(task)

    /**
     * Alterna o status de conclusão (concluída / pendente).
     */
    suspend fun toggleTaskCompletion(id: Long, isCompleted: Boolean) =
        taskDao.updateCompletionStatus(id, isCompleted)
}
