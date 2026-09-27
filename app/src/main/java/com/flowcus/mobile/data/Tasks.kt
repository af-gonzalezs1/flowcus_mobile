package com.flowcus.mobile.data

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel

enum class Priority(val label: String) { LOW("BAJA"), MEDIUM("MEDIA"), HIGH("ALTA") }

enum class TaskStatus { READY, ACTIVE, DONE }

enum class TaskFilter(val label: String) { ALL("Todas"), ACTIVE("En curso"), DONE("Completadas") }

val Categories = listOf("Backend", "Frontend", "Estudio", "Personal")

data class Subtask(val title: String, val done: Boolean = false)

data class Task(
    val id: Int,
    val title: String,
    val status: TaskStatus,
    val priority: Priority,
    val cyclesLabel: String,
    val estimate: String,
    val minutes: String,
    val description: String,
    val category: String = "BACKEND",
    val subtasks: List<Subtask> = listOf(Subtask("Revisar endpoints pendientes"), Subtask("Documentar casos de error")),
)

/**
 * Estado de la maqueta en memoria: no hay backend, pero las interacciones
 * (crear, completar, editar, usar en Flowcus) se reflejan entre pantallas.
 */
class TasksViewModel : ViewModel() {
    var filter by mutableStateOf(TaskFilter.ALL)

    val tasks = mutableStateListOf(
        Task(
            id = 1, title = "TAREA 1", status = TaskStatus.READY, priority = Priority.HIGH,
            cyclesLabel = "X CICLO", estimate = "X CICLOS (XX MIN ENFOQUE + XX MIN DESCANSO)", minutes = "X×XXM",
            description = "Revisar y documentar los endpoints pendientes del módulo de autenticación antes de la entrega del sprint. Confirmar que los casos de error estén cubiertos.",
        ),
        Task(
            id = 2, title = "TAREA 2", status = TaskStatus.ACTIVE, priority = Priority.MEDIUM,
            cyclesLabel = "X CICLO", estimate = "X CICLOS (XX MIN ENFOQUE + XX MIN DESCANSO)", minutes = "X×XXM",
            description = "Detalles de la tarea 2. Revisa sus subtareas antes de iniciar el bloque de concentración.",
        ),
        Task(
            id = 3, title = "TAREA 3", status = TaskStatus.READY, priority = Priority.HIGH,
            cyclesLabel = "1 CICLO", estimate = "1 CICLO (25 MIN ENFOQUE)", minutes = "1×25M",
            description = "Detalles de la tarea 3. Revisa sus subtareas antes de iniciar el bloque de concentración.",
        ),
        Task(
            id = 4, title = "TAREA 4", status = TaskStatus.DONE, priority = Priority.MEDIUM,
            cyclesLabel = "1 CICLO", estimate = "1 CICLO (25 MIN ENFOQUE)", minutes = "1×25M",
            description = "Detalles de la tarea 4. Revisa sus subtareas antes de iniciar el bloque de concentración.",
        ),
    )

    fun task(id: Int): Task? = tasks.firstOrNull { it.id == id }

    fun addTask(
        title: String,
        category: String,
        priority: Priority,
        cycles: Int,
        description: String,
        subtasks: List<Subtask>,
    ) {
        val cyclesText = "$cycles CICLO${if (cycles == 1) "" else "S"}"
        tasks += Task(
            id = (tasks.maxOfOrNull { it.id } ?: 0) + 1,
            title = title.trim(),
            status = TaskStatus.READY,
            priority = priority,
            cyclesLabel = cyclesText,
            estimate = "$cyclesText (${cycles * 25} MIN ENFOQUE)",
            minutes = "$cycles×25M",
            description = description.trim().ifEmpty { "Sin descripción por ahora." },
            category = category.uppercase(),
            subtasks = subtasks.filter { it.title.isNotBlank() },
        )
    }

    fun setDone(id: Int, done: Boolean) = update(id) {
        it.copy(status = if (done) TaskStatus.DONE else TaskStatus.READY)
    }

    fun toggleSubtask(id: Int, index: Int) = update(id) { task ->
        task.copy(subtasks = task.subtasks.mapIndexed { i, s -> if (i == index) s.copy(done = !s.done) else s })
    }

    fun edit(id: Int, title: String, description: String) = update(id) {
        it.copy(
            title = title.trim().ifEmpty { it.title },
            description = description.trim().ifEmpty { "Sin descripción por ahora." },
        )
    }

    private fun update(id: Int, change: (Task) -> Task) {
        val index = tasks.indexOfFirst { it.id == id }
        if (index >= 0) tasks[index] = change(tasks[index])
    }
}
