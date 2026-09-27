package com.flowcus.mobile.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.flowcus.mobile.R
import com.flowcus.mobile.data.Task
import com.flowcus.mobile.data.TaskFilter
import com.flowcus.mobile.data.TaskStatus
import com.flowcus.mobile.data.TasksViewModel
import com.flowcus.mobile.ui.theme.FlowcusColors
import com.flowcus.mobile.ui.theme.oswald
import com.flowcus.mobile.ui.theme.serif
import kotlinx.coroutines.launch

@Composable
fun TasksScreen(
    viewModel: TasksViewModel,
    onOpenTask: (Int) -> Unit,
    onCreateTask: () -> Unit,
    onNavigate: (NavTab) -> Unit,
) {
    var filter by viewModel::filter
    var query by rememberSaveable { mutableStateOf("") }
    val listState = rememberLazyListState()
    val scope = rememberCoroutineScope()

    val tasks = viewModel.tasks
    val counts = mapOf(
        TaskFilter.ALL to tasks.size,
        TaskFilter.ACTIVE to tasks.count { it.status == TaskStatus.ACTIVE },
        TaskFilter.DONE to tasks.count { it.status == TaskStatus.DONE },
    )
    val visible = tasks.filter { task ->
        val matchesFilter = when (filter) {
            TaskFilter.ALL -> true
            TaskFilter.ACTIVE -> task.status == TaskStatus.ACTIVE
            TaskFilter.DONE -> task.status == TaskStatus.DONE
        }
        matchesFilter && task.title.contains(query.trim(), ignoreCase = true)
    }

    Scaffold(
        containerColor = Color(0xFFFDFDFD),
        bottomBar = {
            FlowcusBottomBar(
                current = if (filter == TaskFilter.DONE) NavTab.HISTORY else NavTab.TASKS,
                onSelect = { tab ->
                    when (tab) {
                        NavTab.TASKS -> { filter = TaskFilter.ALL; scope.launch { listState.animateScrollToItem(0) } }
                        NavTab.HISTORY -> { filter = TaskFilter.DONE; scope.launch { listState.animateScrollToItem(0) } }
                        else -> onNavigate(tab)
                    }
                },
            ) {
                Button(
                    onClick = { onOpenTask(tasks.first().id) },
                    shape = RoundedCornerShape(25.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = FlowcusColors.Blue, contentColor = Color.White),
                    modifier = Modifier.fillMaxWidth().padding(horizontal = 20.dp, vertical = 12.dp).height(45.dp),
                ) {
                    Text("▷", style = serif(16.sp))
                    Spacer(Modifier.width(9.dp))
                    Text("INICIAR FLOWCUS", style = oswald(14.sp, .02f))
                }
            }
        },
    ) { padding ->
        LazyColumn(
            state = listState,
            contentPadding = PaddingValues(start = 8.dp, end = 8.dp, top = 20.dp, bottom = 24.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
            modifier = Modifier.fillMaxSize().statusBarsPadding().padding(bottom = padding.calculateBottomPadding()),
        ) {
            item {
                Column {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.fillMaxWidth().padding(start = 24.dp, end = 4.dp, bottom = 7.dp),
                    ) {
                        Column(Modifier.weight(1f)) {
                            Text("TAREAS", style = oswald(10.sp, .04f, FlowcusColors.Muted))
                            Text("BIENVENIDO DE NUEVO", style = oswald(20.sp, .035f, FlowcusColors.Ink), modifier = Modifier.padding(top = 2.dp))
                        }
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier
                                .clip(RoundedCornerShape(20.dp))
                                .border(1.dp, FlowcusColors.Blue, RoundedCornerShape(20.dp))
                                .clickable(onClickLabel = "Crear tarea", onClick = onCreateTask)
                                .padding(horizontal = 12.dp, vertical = 8.dp),
                        ) {
                            Icon(painterResource(R.drawable.ic_plus), contentDescription = null, tint = FlowcusColors.Blue, modifier = Modifier.size(14.dp))
                            Spacer(Modifier.width(4.dp))
                            Text("NUEVA", style = oswald(12.sp, .04f, FlowcusColors.Blue))
                        }
                    }
                    SearchField(query, onQueryChange = { query = it })
                    Row(
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.fillMaxWidth().padding(start = 6.dp, end = 6.dp, top = 12.dp, bottom = 6.dp),
                    ) {
                        TaskFilter.entries.forEach { option ->
                            FilterTab("${option.label} (${counts[option]})", selected = option == filter) { filter = option }
                        }
                    }
                }
            }
            if (visible.isEmpty()) {
                item {
                    Text(
                        "No hay tareas que coincidan.",
                        style = serif(13.sp, FlowcusColors.Muted),
                        modifier = Modifier.padding(horizontal = 24.dp, vertical = 16.dp),
                    )
                }
            }
            items(visible, key = { it.id }) { task ->
                TaskCard(
                    task = task,
                    onCheckedChange = { viewModel.setDone(task.id, it) },
                    onOpen = { onOpenTask(task.id) },
                    modifier = Modifier.padding(horizontal = 12.dp),
                )
            }
        }
    }
}

@Composable
private fun SearchField(query: String, onQueryChange: (String) -> Unit) {
    BasicTextField(
        value = query,
        onValueChange = onQueryChange,
        singleLine = true,
        textStyle = serif(13.sp, Color(0xFF3B3D40)),
        cursorBrush = SolidColor(FlowcusColors.Blue),
        modifier = Modifier.fillMaxWidth().semantics { contentDescription = "Buscar tarea" },
        decorationBox = { inner ->
            Box(
                contentAlignment = Alignment.CenterStart,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(40.dp)
                    .background(FlowcusColors.Surface, RoundedCornerShape(8.dp))
                    .border(1.dp, FlowcusColors.Line, RoundedCornerShape(8.dp))
                    .padding(horizontal = 24.dp),
            ) {
                if (query.isEmpty()) Text("Buscar tarea...", style = serif(13.sp, FlowcusColors.Muted))
                inner()
            }
        },
    )
}

@Composable
private fun FilterTab(text: String, selected: Boolean, onClick: () -> Unit) {
    Text(
        text,
        style = serif(12.sp, if (selected) Color.White else FlowcusColors.Muted),
        maxLines = 1,
        modifier = Modifier
            .background(if (selected) FlowcusColors.Blue else Color.Transparent, RoundedCornerShape(20.dp))
            .selectable(selected = selected, role = Role.Tab, onClick = onClick)
            .padding(horizontal = if (selected) 12.dp else 5.dp, vertical = 7.dp),
    )
}

@Composable
private fun TaskCard(task: Task, onCheckedChange: (Boolean) -> Unit, onOpen: () -> Unit, modifier: Modifier = Modifier) {
    val done = task.status == TaskStatus.DONE
    Box(
        modifier = modifier
            .fillMaxWidth()
            .heightIn(min = 91.dp)
            .background(if (done) FlowcusColors.DoneCard else FlowcusColors.Surface, RoundedCornerShape(16.dp))
            .border(1.dp, FlowcusColors.Line, RoundedCornerShape(16.dp))
            .clickable(onClick = onOpen),
    ) {
        Row(Modifier.padding(start = 2.dp, end = 12.dp, top = 2.dp, bottom = 15.dp)) {
            Checkbox(
                checked = done,
                onCheckedChange = onCheckedChange,
                colors = CheckboxDefaults.colors(checkedColor = Color(0xFF979A9C), uncheckedColor = Color(0xFF979A9C)),
                modifier = Modifier.semantics { contentDescription = "Completar ${task.title}" },
            )
            Column(Modifier.padding(top = 13.dp)) {
                Text(
                    task.title,
                    style = oswald(13.sp, color = if (done) FlowcusColors.DoneText else FlowcusColors.Ink)
                        .copy(textDecoration = if (done) TextDecoration.LineThrough else null),
                )
                Text("Estimado:", style = serif(12.sp, Color(0xFF85878A)), modifier = Modifier.padding(top = 7.dp))
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    modifier = Modifier.padding(start = 5.dp, top = 8.dp),
                ) {
                    Text(task.cyclesLabel, style = oswald(10.sp, color = FlowcusColors.Muted))
                    PriorityTag(task.priority)
                }
            }
        }
        if (done) {
            Text(
                "•••",
                style = oswald(14.sp, .15f, FlowcusColors.DoneText),
                modifier = Modifier.align(Alignment.TopEnd).padding(top = 14.dp, end = 14.dp),
            )
        }
    }
}
