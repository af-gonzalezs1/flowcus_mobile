package com.flowcus.mobile.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.flowcus.mobile.R
import com.flowcus.mobile.data.Task
import com.flowcus.mobile.data.TaskStatus
import com.flowcus.mobile.data.TasksViewModel
import com.flowcus.mobile.ui.theme.FlowcusColors
import com.flowcus.mobile.ui.theme.oswald
import com.flowcus.mobile.ui.theme.serif

@Composable
fun TaskDetailScreen(viewModel: TasksViewModel, taskId: Int) {
    val task = viewModel.task(taskId) ?: return
    var inFlowcus by rememberSaveable { mutableStateOf(false) }
    var editing by rememberSaveable { mutableStateOf(false) }

    Scaffold(
        containerColor = Color(0xFFFDFDFD),
        bottomBar = {
            Column(Modifier.fillMaxWidth().background(Color.White).navigationBarsPadding()) {
                HorizontalDivider(color = Color(0xFFE5E5E5))
                Column(
                    verticalArrangement = Arrangement.spacedBy(10.dp),
                    modifier = Modifier.padding(start = 18.dp, end = 18.dp, top = 15.dp, bottom = 10.dp),
                ) {
                    Button(
                        onClick = { inFlowcus = true },
                        shape = RoundedCornerShape(25.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = FlowcusColors.Blue, contentColor = Color.White),
                        modifier = Modifier.fillMaxWidth().height(44.dp),
                    ) {
                        Text(if (inFlowcus) "✓  EN FLOWCUS" else "▷  USAR EN FLOWCUS", style = oswald(13.sp, .035f))
                    }
                    OutlinedButton(
                        onClick = { editing = true },
                        shape = RoundedCornerShape(25.dp),
                        border = androidx.compose.foundation.BorderStroke(1.dp, FlowcusColors.Line),
                        colors = ButtonDefaults.outlinedButtonColors(containerColor = Color.White, contentColor = Color(0xFF505357)),
                        modifier = Modifier.fillMaxWidth().height(40.dp),
                    ) {
                        Text("EDITAR TAREA", style = oswald(13.sp, .035f))
                    }
                }
            }
        },
    ) { padding ->
        Column(
            verticalArrangement = Arrangement.spacedBy(16.dp),
            modifier = Modifier
                .fillMaxSize()
                .statusBarsPadding()
                .padding(bottom = padding.calculateBottomPadding())
                .verticalScroll(rememberScrollState())
                .padding(start = 21.dp, end = 21.dp, top = 17.dp, bottom = 24.dp),
        ) {
            SummaryCard(task)
            EstimateCard(task)
            NotesCard(task)
            SubtasksCard(task) { viewModel.toggleSubtask(task.id, it) }
        }
    }

    if (editing) {
        EditTaskDialog(
            task = task,
            onDismiss = { editing = false },
            onSave = { title, description ->
                viewModel.edit(task.id, title, description)
                editing = false
            },
        )
    }
}

@Composable
private fun SummaryCard(task: Task) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .heightIn(min = 142.dp)
            .background(FlowcusColors.Surface, CardShape)
            .border(1.dp, FlowcusColors.Line, CardShape)
            .padding(15.dp),
    ) {
        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.SpaceBetween, modifier = Modifier.fillMaxWidth()) {
            Text("ESTADO", style = oswald(10.sp, color = FlowcusColors.Muted))
            Text(
                when {
                    task.status == TaskStatus.DONE -> "●  COMPLETADA"
                    task.status == TaskStatus.ACTIVE -> "●  EN CURSO"
                    else -> "●  LISTO PARA FLOWCUS"
                },
                style = oswald(10.sp, color = FlowcusColors.Ink),
                maxLines = 1,
                modifier = Modifier
                    .border(1.dp, FlowcusColors.Line, RoundedCornerShape(6.dp))
                    .padding(horizontal = 7.dp, vertical = 4.dp),
            )
        }
        Text(task.title, style = oswald(18.sp, color = FlowcusColors.Ink), modifier = Modifier.padding(top = 15.dp, bottom = 3.dp))
        Text("fecha/hora creación", style = serif(12.sp, FlowcusColors.Muted))
        Row(horizontalArrangement = Arrangement.spacedBy(6.dp), modifier = Modifier.padding(top = 15.dp)) {
            Tag("PRIORIDAD", Color.White, FlowcusColors.BlueDark)
            PriorityTag(task.priority)
            Tag(task.category, FlowcusColors.BlueDark, FlowcusColors.BlueSoft)
        }
    }
}

@Composable
private fun EstimateCard(task: Task) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .fillMaxWidth()
            .heightIn(min = 71.dp)
            .background(FlowcusColors.SurfaceMuted, CardShape)
            .padding(horizontal = 13.dp, vertical = 10.dp),
    ) {
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier.size(28.dp).background(Color.White, RoundedCornerShape(8.dp)),
        ) {
            Icon(painterResource(R.drawable.ic_clock), contentDescription = null, tint = FlowcusColors.BlueButton, modifier = Modifier.size(16.dp))
        }
        Spacer(Modifier.width(12.dp))
        Column(Modifier.weight(1f)) {
            Text("ESTIMACIÓN POMODORO", style = oswald(10.sp, color = FlowcusColors.Muted))
            Text(task.estimate, style = oswald(11.sp, color = FlowcusColors.Ink).copy(lineHeight = 14.sp), modifier = Modifier.padding(top = 2.dp))
        }
        Spacer(Modifier.width(12.dp))
        Text(
            task.minutes,
            style = oswald(11.sp, color = FlowcusColors.Ink),
            modifier = Modifier.background(Color.White, RoundedCornerShape(4.dp)).padding(horizontal = 7.dp, vertical = 5.dp),
        )
    }
}

@Composable
private fun NotesCard(task: Task) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .heightIn(min = 236.dp)
            .background(FlowcusColors.Surface, CardShape)
            .border(1.dp, FlowcusColors.Line, CardShape)
            .padding(16.dp),
    ) {
        Text("DESCRIPCIÓN Y NOTAS", style = oswald(12.sp, .035f, FlowcusColors.Ink), modifier = Modifier.padding(bottom = 14.dp))
        Text(task.description, style = serif(12.sp, FlowcusColors.Body).copy(lineHeight = 18.sp))
        Text(
            "Nota: Priorizar este bloque en la mañana, cuando el nivel de concentración suele ser más alto.",
            style = serif(12.sp, FlowcusColors.Body).copy(lineHeight = 18.sp, fontStyle = FontStyle.Italic),
            modifier = Modifier.padding(top = 10.dp),
        )
    }
}

@Composable
private fun SubtasksCard(task: Task, onToggle: (Int) -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .heightIn(min = 150.dp)
            .background(FlowcusColors.Surface, CardShape)
            .border(1.dp, FlowcusColors.Line, CardShape)
            .padding(16.dp),
    ) {
        Text("SUBTAREAS", style = oswald(12.sp, .035f, FlowcusColors.Ink), modifier = Modifier.padding(bottom = 6.dp))
        if (task.subtasks.isEmpty()) {
            Text("Esta tarea no tiene subtareas.", style = serif(12.sp, FlowcusColors.Muted), modifier = Modifier.padding(top = 8.dp))
        }
        task.subtasks.forEachIndexed { index, subtask ->
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth().clickable { onToggle(index) },
            ) {
                Checkbox(
                    checked = subtask.done,
                    onCheckedChange = null,
                    colors = CheckboxDefaults.colors(checkedColor = FlowcusColors.Blue),
                    modifier = Modifier.padding(12.dp).size(18.dp),
                )
                Text(subtask.title, style = serif(12.sp, Color(0xFF55595D)))
            }
        }
    }
}

@Composable
private fun EditTaskDialog(task: Task, onDismiss: () -> Unit, onSave: (String, String) -> Unit) {
    var title by rememberSaveable { mutableStateOf(task.title) }
    var description by rememberSaveable { mutableStateOf(task.description) }
    val fieldColors = OutlinedTextFieldDefaults.colors(
        focusedBorderColor = FlowcusColors.Blue,
        unfocusedBorderColor = FlowcusColors.Line,
        focusedLabelColor = FlowcusColors.Blue,
        cursorColor = FlowcusColors.Blue,
    )
    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = Color.White,
        shape = CardShape,
        title = { Text("EDITAR TAREA", style = oswald(18.sp, color = FlowcusColors.Ink)) },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                OutlinedTextField(
                    value = title,
                    onValueChange = { if (it.length <= 120) title = it },
                    label = { Text("Título") },
                    singleLine = true,
                    textStyle = serif(13.sp, FlowcusColors.Ink),
                    colors = fieldColors,
                    modifier = Modifier.fillMaxWidth(),
                )
                OutlinedTextField(
                    value = description,
                    onValueChange = { description = it },
                    label = { Text("Descripción") },
                    minLines = 4,
                    textStyle = serif(13.sp, FlowcusColors.Ink),
                    colors = fieldColors,
                    modifier = Modifier.fillMaxWidth(),
                )
            }
        },
        dismissButton = {
            TextButton(
                onClick = onDismiss,
                colors = ButtonDefaults.textButtonColors(containerColor = FlowcusColors.SurfaceMuted, contentColor = Color(0xFF343434)),
            ) { Text("Cancelar", style = oswald(12.sp)) }
        },
        confirmButton = {
            TextButton(
                onClick = { onSave(title, description) },
                enabled = title.isNotBlank(),
                colors = ButtonDefaults.textButtonColors(containerColor = FlowcusColors.Blue, contentColor = Color.White),
            ) { Text("Guardar", style = oswald(12.sp)) }
        },
    )
}
