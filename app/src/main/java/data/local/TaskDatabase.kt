package com.zaqueu.taskapp.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.zaqueu.taskapp.data.local.dao.TaskDao
import com.zaqueu.taskapp.data.local.entity.TaskEntity

/**
 * Classe principal do banco de dados Room.
 *
 * Segue o padrão Singleton para garantir uma única instância em toda a aplicação.
 *
 * @property version Incrementar ao fazer alterações de esquema (migrations necessárias).
 */
@Database(
    entities = [TaskEntity::class],
    version = 1,
    exportSchema = false
)
abstract class TaskDatabase : RoomDatabase() {

    abstract fun taskDao(): TaskDao

    companion object {
        private const val DATABASE_NAME = "task_database"

        @Volatile
        private var INSTANCE: TaskDatabase? = null

        /**
         * Retorna a instância única do banco, criando-a caso ainda não exista.
         * O bloco `synchronized` garante segurança em acesso concorrente.
         */
        fun getInstance(context: Context): TaskDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    TaskDatabase::class.java,
                    DATABASE_NAME
                )
                    .fallbackToDestructiveMigration()
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }
}
