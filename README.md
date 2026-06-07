# TaskApp — Lista de Tarefas

## 📲 Download do Aplicativo

> Instale o app diretamente no seu dispositivo Android!

| | |
|---|---|
| **Download APK** | [⬇️ Clique aqui para baixar](https://github.com/Zaqueu-Fernandes/Lista-de-tarefas-Kotlin/releases/tag/v1.0) |
| **Versão** | v1.0 |
| **Plataforma** | Android (API 26+) |

### Como instalar
1. Acesse o link acima no seu celular Android
2. Toque em **`app-debug.apk`** para baixar
3. Abra o arquivo baixado
4. Se solicitado, vá em **Configurações → Permitir desta fonte**
5. Toque em **Instalar**

---

## Informações do Aluno

| Campo       | Dados                     |
|-------------|---------------------------|
| **Nome**    | Zaqueu Fernandes Alves    |
| **Matrícula** | 333333333               |
| **Entrega** | 07/06/2026                |

---

## Justificativa da Escolha do Tema

O tema **Lista de Tarefas** foi escolhido por ser um domínio familiar e objetivo, o que permite focar inteiramente nos requisitos técnicos da atividade (Jetpack Compose, Room, MVVM e Navegação) sem que a complexidade do domínio desvie a atenção. Aplicativos de tarefas possuem operações CRUD bem definidas, tornando-se um campo ideal para demonstrar persistência local, fluxo de dados reativo com `StateFlow` e separação clara de responsabilidades na arquitetura.

---

## Descrição do Funcionamento

O aplicativo permite ao usuário **gerenciar tarefas do cotidiano** de forma simples e persistente. Todos os dados são salvos localmente no dispositivo via **Room Database**, sem necessidade de internet.

### Fluxo de Uso

1. **Tela de Lista (`TaskListScreen`)**
   - Exibe todas as tarefas cadastradas em cards
   - Cada card mostra o título, a descrição resumida e um checkbox de conclusão
   - O usuário pode marcar/desmarcar uma tarefa como concluída diretamente na lista
   - Botão `+` (FAB) no canto inferior direito abre a tela de cadastro
   - Toque em um card abre a tela de detalhes/edição daquela tarefa
   - Botão de lixeira em cada card permite excluir a tarefa

2. **Tela de Cadastro/Edição (`TaskFormScreen`)**
   - Campos de texto para **Título** e **Descrição**
   - Ao abrir para **nova tarefa**: campos em branco
   - Ao abrir para **edição**: campos pré-preenchidos com os dados existentes
   - Botão **Salvar** persiste a tarefa no banco Room e retorna à lista
   - Botão de voltar (AppBar) descarta alterações e retorna à lista

### Operações Suportadas

| Operação | Descrição |
|----------|-----------|
| Criar    | Adiciona nova tarefa ao banco |
| Listar   | Exibe todas as tarefas em tempo real (Flow) |
| Editar   | Atualiza título, descrição ou status |
| Concluir | Marca/desmarca tarefa via checkbox |
| Excluir  | Remove tarefa permanentemente |

---

## Arquitetura — MVVM

```
UI Layer (Compose Screens)
        ↕ observa StateFlow / chama funções
ViewModel Layer (TaskViewModel)
        ↕ chama suspend functions
Repository Layer (TaskRepository)
        ↕ acessa DAO
Data Layer (Room DAO + Entity)
```

### Estrutura de Pacotes

```
com.zaqueu.taskapp/
├── data/
│   └── local/
│       ├── entity/
│       │   └── TaskEntity.kt        # Entidade Room
│       ├── dao/
│       │   └── TaskDao.kt           # Interface DAO
│       └── TaskDatabase.kt          # Classe Database Room
├── repository/
│   └── TaskRepository.kt            # Camada de acesso a dados
├── viewmodel/
│   └── TaskViewModel.kt             # Lógica de negócio + estado
├── ui/
│   ├── screens/
│   │   ├── TaskListScreen.kt        # Tela 1 — Lista
│   │   └── TaskFormScreen.kt        # Tela 2 — Cadastro/Edição
│   ├── components/
│   │   └── TaskCard.kt              # Componente reutilizável
│   └── theme/
│       ├── Color.kt
│       ├── Theme.kt
│       └── Type.kt
├── navigation/
│   └── NavGraph.kt                  # Rotas de navegação
└── MainActivity.kt
```

---

## Tecnologias Utilizadas

| Tecnologia | Versão | Uso |
|------------|--------|-----|
| Kotlin | 2.0.21 | Linguagem principal |
| Jetpack Compose | BOM 2024.05 | Interface declarativa |
| Navigation Compose | 2.7.7 | Navegação entre telas |
| Room | 2.7.1 | Persistência local (SQLite) |
| ViewModel + StateFlow | 2.8.1 | Gerenciamento de estado MVVM |
| Coroutines | 1.8.1 | Operações assíncronas |
| Material 3 | — | Design System |

---

## Como Executar

1. Clone o repositório:
```bash
git clone https://github.com/Zaqueu-Fernandes/Lista-de-tarefas-Kotlin.git
```
2. Abra no **Android Studio Hedgehog** (ou superior)
3. Aguarde o Gradle sincronizar as dependências
4. Execute em um emulador ou dispositivo físico com **API 26+**
5. Nenhuma configuração adicional é necessária

---

## Observações

- O banco de dados Room é criado automaticamente na primeira execução
- As mudanças na lista são reativas: qualquer inserção/edição/exclusão reflete imediatamente na UI via `Flow` → `StateFlow`
- O projeto não usa bibliotecas de injeção de dependência (como Hilt) para manter a simplicidade, instanciando o repositório diretamente no `ViewModelFactory`
- O código segue as convenções oficiais do [Android Kotlin Style Guide](https://developer.android.com/kotlin/style-guide)
