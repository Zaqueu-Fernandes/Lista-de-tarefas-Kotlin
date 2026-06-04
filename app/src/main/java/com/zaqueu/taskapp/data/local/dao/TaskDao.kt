package com.zaqueu.taskapp.data.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.zaqueu.taskapp.data.local.entity.TaskEntity
import kotlinx.coroutines.flow.Flow

/**
 * Data Access Object (DAO) para operações CRUD na tabela de tarefas.
 *
 * Todas as operações de escrita são suspensas (coroutines).
 * A consulta de listagem retorna um [Flow] para atualização reativa da UI.
 */
@Dao
interface TaskDao {

    /**
     * Observa todas as tarefas, ordenadas da mais recente para a mais antiga.
     * O [Flow] emite um novo valor sempre que a tabela sofre alguma alteração.
     */
    @Query("SELECT * FROM tasks ORDER BY createdAt DESC")
    fun getAllTasks(): Flow<List<TaskEntity>>

    /**
     * Busca uma tarefa específica pelo seu [id].
     * Retorna null se não encontrada.
     */
    @Query("SELECT * FROM tasks WHERE id = :id")
    suspend fun getTaskById(id: Long): TaskEntity?

    /**
     * Insere uma nova tarefa. Em caso de conflito de ID, substitui o registro.
     * Retorna o ID gerado automaticamente pelo Room.
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTask(task: TaskEntity): Long

    /**
     * Atualiza todos os campos de uma tarefa existente (por ID).
     */
    @Update
    suspend fun updateTask(task: TaskEntity)

    /**
     * Remove permanentemente a tarefa informada.
     */
    @Delete
    suspend fun deleteTask(task: TaskEntity)

    /**
     * Alterna o status de conclusão de uma tarefa diretamente no banco.
     */
    @Query("UPDATE tasks SET isCompleted = :isCompleted WHERE id = :id")
    suspend fun updateCompletionStatus(id: Long, isCompleted: Boolean)
}
