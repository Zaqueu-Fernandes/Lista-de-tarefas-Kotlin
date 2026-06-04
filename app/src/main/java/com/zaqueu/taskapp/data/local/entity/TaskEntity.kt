package com.zaqueu.taskapp.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * Entidade Room que representa uma tarefa no banco de dados local.
 *
 * @property id         Chave primária gerada automaticamente pelo Room.
 * @property title      Título obrigatório da tarefa.
 * @property description Descrição opcional com mais detalhes.
 * @property isCompleted Indica se a tarefa foi concluída (default: false).
 * @property createdAt  Timestamp em milissegundos da criação da tarefa.
 */
@Entity(tableName = "tasks")
data class TaskEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0L,
    val title: String,
    val description: String = "",
    val isCompleted: Boolean = false,
    val createdAt: Long = System.currentTimeMillis()
)
