package com.zaqueu.taskapp.ui.screens

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.zaqueu.taskapp.viewmodel.TaskViewModel

/**
 * Tela 2 — Formulário de Cadastro / Edição de Tarefa.
 *
 * Comportamento:
 * - Se [taskId] == 0L → Nova tarefa (campos em branco).
 * - Se [taskId] > 0L  → Edição (campos pré-preenchidos com dados do banco).
 *
 * O FAB com ícone de check salva os dados e navega de volta à lista.
 *
 * @param taskId          ID da tarefa a editar, ou 0 para criação.
 * @param viewModel       ViewModel compartilhado.
 * @param onNavigateBack  Callback para retornar à tela anterior.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TaskFormScreen(
    taskId: Long,
    viewModel: TaskViewModel,
    onNavigateBack: () -> Unit
) {
    val isEditing = taskId != 0L
    val selectedTask by viewModel.selectedTask.collectAsState()
    val uiMessage by viewModel.uiMessage.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }

    // ── Estados locais dos campos (rememberSaveable sobrevive a rotações) ─────
    var title by rememberSaveable { mutableStateOf("") }
    var description by rememberSaveable { mutableStateOf("") }
    var fieldsLoaded by rememberSaveable { mutableStateOf(false) }

    // Carrega a tarefa no ViewModel quando a tela entra em composição
    LaunchedEffect(taskId) {
        if (isEditing) {
            viewModel.loadTask(taskId)
        }
    }

    // Preenche os campos quando os dados chegam do ViewModel (apenas uma vez)
    LaunchedEffect(selectedTask) {
        if (isEditing && !fieldsLoaded && selectedTask != null) {
            title = selectedTask!!.title
            description = selectedTask!!.description
            fieldsLoaded = true
        }
    }

    // Exibe erros de validação via Snackbar
    LaunchedEffect(uiMessage) {
        uiMessage?.let {
            snackbarHostState.showSnackbar(it)
            viewModel.consumeMessage()
        }
    }

    // Limpa o estado ao sair da tela
    LaunchedEffect(Unit) {
        // onDispose não existe em LaunchedEffect — limpeza feita no navBack
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = if (isEditing) "Editar Tarefa" else "Nova Tarefa",
                        style = MaterialTheme.typography.headlineMedium
                    )
                },
                navigationIcon = {
                    IconButton(onClick = {
                        viewModel.clearSelectedTask()
                        onNavigateBack()
                    }) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Voltar"
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    titleContentColor = MaterialTheme.colorScheme.onPrimaryContainer,
                    navigationIconContentColor = MaterialTheme.colorScheme.onPrimaryContainer
                )
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = {
                    val saved = viewModel.saveTask(
                        id = taskId,
                        title = title,
                        description = description
                    )
                    if (saved) {
                        viewModel.clearSelectedTask()
                        onNavigateBack()
                    }
                },
                containerColor = MaterialTheme.colorScheme.primary
            ) {
                Icon(
                    imageVector = Icons.Default.Check,
                    contentDescription = "Salvar tarefa",
                    tint = MaterialTheme.colorScheme.onPrimary
                )
            }
        },
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { innerPadding ->

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(horizontal = 20.dp, vertical = 16.dp)
                .verticalScroll(rememberScrollState())
                .imePadding()   // Ajusta o layout ao teclado virtual
        ) {
            // ── Campo Título ──────────────────────────────────────────────────
            Text(
                text = "Título *",
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.primary
            )
            Spacer(modifier = Modifier.height(4.dp))
            OutlinedTextField(
                value = title,
                onValueChange = { title = it },
                modifier = Modifier.fillMaxWidth(),
                placeholder = { Text("Ex: Comprar mantimentos") },
                singleLine = true,
                isError = title.isBlank() && fieldsLoaded,
                supportingText = {
                    if (title.isBlank() && fieldsLoaded) {
                        Text(
                            text = "O título é obrigatório.",
                            color = MaterialTheme.colorScheme.error
                        )
                    }
                }
            )

            Spacer(modifier = Modifier.height(20.dp))

            // ── Campo Descrição ───────────────────────────────────────────────
            Text(
                text = "Descrição",
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.primary
            )
            Spacer(modifier = Modifier.height(4.dp))
            OutlinedTextField(
                value = description,
                onValueChange = { description = it },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(160.dp),
                placeholder = { Text("Detalhes opcionais da tarefa…") },
                maxLines = 6
            )

            Spacer(modifier = Modifier.height(80.dp)) // Espaço para o FAB
        }
    }
}
