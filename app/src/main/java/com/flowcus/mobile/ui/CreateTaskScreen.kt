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
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.flowcus.mobile.R
import com.flowcus.mobile.data.Categories
import com.flowcus.mobile.data.Priority
import com.flowcus.mobile.data.Subtask
import com.flowcus.mobile.data.TasksViewModel
import com.flowcus.mobile.ui.theme.FlowcusColors
import com.flowcus.mobile.ui.theme.oswald
import com.flowcus.mobile.ui.theme.serif

private const val MaxTitle = 120

@Composable
fun CreateTaskScreen(viewModel: TasksViewModel, onDone: () -> Unit) {
    var title by rememberSaveable { mutableStateOf("") }
    var category by rememberSaveable { mutableStateOf(Categories.first()) }
    var priority by rememberSaveable { mutableStateOf(Priority.HIGH) }
    var cycles by rememberSaveable { mutableStateOf(3) }
    var description by rememberSaveable { mutableStateOf("") }
    var showError by rememberSaveable { mutableStateOf(false) }
    val subtasks = remember { mutableStateListOf(Subtask(""), Subtask(""), Subtask("")) }

    fun save() {
        if (title.isBlank()) {
            showError = true
            return
        }
        viewModel.addTask(title, category, priority, cycles, description, subtasks.toList())
        onDone()
    }

    Scaffold(
        containerColor = Color(0xFFFDFDFD),
        bottomBar = {
            Column(Modifier.fillMaxWidth().background(Color.White).navigationBarsPadding().imePadding()) {
                HorizontalDivider(color = Color(0xFFE5E5E5))
                Column(
                    verticalArrangement = Arrangement.spacedBy(10.dp),
                    modifier = Modifier.padding(start = 18.dp, end = 18.dp, top = 12.dp, bottom = 10.dp),
                ) {
                    PrimaryButton("GUARDAR TAREA", onClick = ::save)
                    SecondaryButton("CANCELAR", onClick = onDone)
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
                .padding(start = 21.dp, end = 21.dp, top = 8.dp, bottom = 24.dp),
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                IconButton(onClick = onDone, modifier = Modifier.size(40.dp)) {
                    Icon(painterResource(R.drawable.ic_back), contentDescription = "Volver a tareas", tint = FlowcusColors.Ink, modifier = Modifier.size(20.dp))
                }
                Text("TAREAS  /  ", style = serif(12.sp, FlowcusColors.Muted))
                Text("NUEVA TAREA EN CURSO", style = oswald(11.sp, .03f, FlowcusColors.Ink))
            }

            Column {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.background(FlowcusColors.InfoBg, RoundedCornerShape(12.dp)).padding(horizontal = 10.dp, vertical = 4.dp),
                ) {
                    Box(Modifier.size(6.dp).background(FlowcusColors.InfoText, CircleShape))
                    Spacer(Modifier.width(6.dp))
                    Text("FLUJO DE SESIÓN ACTIVO", style = oswald(10.sp, .04f, FlowcusColors.InfoText))
                }
                Text("CREAR TAREA PARA LA SESIÓN", style = oswald(20.sp, .035f, FlowcusColors.Ink), modifier = Modifier.padding(top = 10.dp))
                Text(
                    "Esta tarea se asociará automáticamente a tu Flow de sesión.",
                    style = serif(13.sp, FlowcusColors.Muted),
                    modifier = Modifier.padding(top = 4.dp),
                )
            }

            FlowcusCard {
                Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.fillMaxWidth()) {
                    FormLabel("TÍTULO DE LA TAREA *", Modifier.weight(1f))
                    Text(
                        if (title.isEmpty()) "Máx. $MaxTitle caracteres" else "${title.length} / $MaxTitle caracteres",
                        style = serif(10.sp, FlowcusColors.Muted),
                    )
                }
                InputBox(
                    value = title,
                    onValueChange = { if (it.length <= MaxTitle) { title = it; showError = false } },
                    placeholder = "Título de tarea",
                    label = "Título de la tarea",
                    isError = showError,
                    modifier = Modifier.padding(top = 8.dp),
                )
                if (showError) {
                    Text("El título es obligatorio.", style = serif(11.sp, FlowcusColors.Error), modifier = Modifier.padding(top = 6.dp))
                }

                FormLabel("PROYECTO / CATEGORÍA", Modifier.padding(top = 18.dp))
                CategoryPicker(category, onSelect = { category = it }, modifier = Modifier.padding(top = 8.dp))

                FormLabel("NIVEL DE PRIORIDAD", Modifier.padding(top = 18.dp))
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.fillMaxWidth().padding(top = 8.dp)) {
                    Priority.entries.forEach { option ->
                        val (text, bg) = option.colors()
                        val selected = option == priority
                        Text(
                            option.label,
                            textAlign = TextAlign.Center,
                            style = oswald(12.sp, .04f, if (selected) text else FlowcusColors.Muted),
                            modifier = Modifier
                                .weight(1f)
                                .background(if (selected) bg else Color.White, RoundedCornerShape(8.dp))
                                .border(1.dp, if (selected) text else FlowcusColors.Line, RoundedCornerShape(8.dp))
                                .selectable(selected = selected, role = Role.RadioButton) { priority = option }
                                .padding(vertical = 11.dp),
                        )
                    }
                }
            }

            FlowcusCard {
                FormLabel("ESTIMACIÓN DE CICLOS POMODORO (25 MIN C/U)")
                Text(
                    "${if (cycles == 6) "X" else cycles} CICLOS = ${cycles * 25} MINUTOS DE FOCO",
                    style = oswald(12.sp, .03f, FlowcusColors.Blue),
                    modifier = Modifier.padding(top = 6.dp),
                )
                listOf(1..3, 4..6).forEach { range ->
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.fillMaxWidth().padding(top = 8.dp)) {
                        range.forEach { value ->
                            val selected = value == cycles
                            Column(
                                horizontalAlignment = Alignment.CenterHorizontally,
                                modifier = Modifier
                                    .weight(1f)
                                    .background(if (selected) FlowcusColors.Blue else Color.White, RoundedCornerShape(10.dp))
                                    .border(1.dp, if (selected) FlowcusColors.Blue else FlowcusColors.Line, RoundedCornerShape(10.dp))
                                    .selectable(selected = selected, role = Role.RadioButton) { cycles = value }
                                    .padding(vertical = 10.dp),
                            ) {
                                Text(if (value == 6) "X" else "$value", style = oswald(18.sp, color = if (selected) Color.White else FlowcusColors.Ink))
                                Text("${value * 25}M", style = oswald(10.sp, color = if (selected) Color.White else FlowcusColors.Muted))
                            }
                        }
                    }
                }
            }

            FlowcusCard {
                FormLabel("CHECKLIST DE SUBTAREAS INICIALES")
                subtasks.forEachIndexed { index, subtask ->
                    Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.padding(top = 6.dp)) {
                        Checkbox(
                            checked = subtask.done,
                            onCheckedChange = { subtasks[index] = subtask.copy(done = it) },
                            colors = CheckboxDefaults.colors(checkedColor = FlowcusColors.Blue),
                            modifier = Modifier.semantics { contentDescription = "Marcar subtarea ${index + 1}" },
                        )
                        InputBox(
                            value = subtask.title,
                            onValueChange = { subtasks[index] = subtask.copy(title = it) },
                            placeholder = "Subtarea ${index + 1}",
                            label = "Subtarea ${index + 1}",
                            modifier = Modifier.weight(1f),
                        )
                    }
                }
                Text(
                    "＋  AGREGAR SUBTAREA",
                    style = oswald(12.sp, .04f, FlowcusColors.Blue),
                    modifier = Modifier
                        .padding(top = 10.dp)
                        .clickable { subtasks += Subtask("") }
                        .padding(vertical = 8.dp, horizontal = 4.dp),
                )
            }

            FlowcusCard {
                FormLabel("DESCRIPCIÓN / NOTAS BREVES DE CONTEXTO")
                InputBox(
                    value = description,
                    onValueChange = { description = it },
                    placeholder = "Descripción de la tarea",
                    label = "Descripción",
                    singleLine = false,
                    modifier = Modifier.padding(top = 8.dp),
                )
            }

            FlowcusCard(background = FlowcusColors.SurfaceMuted, border = null) {
                SectionTitle("REGLA DE FOCO PROFUNDO")
                Text(
                    "Dividir las tareas en subtareas atómicas incrementa la tasa de finalización de ciclos en un 38% sin interrupciones.",
                    style = serif(12.sp, FlowcusColors.Body).copy(lineHeight = 18.sp),
                    modifier = Modifier.padding(top = 8.dp),
                )
                LinearProgressIndicator(
                    progress = { .38f },
                    color = FlowcusColors.Blue,
                    trackColor = Color.White,
                    drawStopIndicator = {},
                    modifier = Modifier.fillMaxWidth().padding(top = 12.dp).height(6.dp),
                )
            }
        }
    }
}

@Composable
private fun FormLabel(text: String, modifier: Modifier = Modifier) {
    Text(text, style = oswald(10.sp, .05f, FlowcusColors.Muted), modifier = modifier)
}

@Composable
private fun InputBox(
    value: String,
    onValueChange: (String) -> Unit,
    placeholder: String,
    label: String,
    modifier: Modifier = Modifier,
    singleLine: Boolean = true,
    isError: Boolean = false,
) {
    BasicTextField(
        value = value,
        onValueChange = onValueChange,
        singleLine = singleLine,
        textStyle = serif(13.sp, FlowcusColors.InkStrong).copy(lineHeight = 19.sp),
        cursorBrush = SolidColor(FlowcusColors.Blue),
        keyboardOptions = KeyboardOptions(capitalization = KeyboardCapitalization.Sentences),
        modifier = modifier.fillMaxWidth().semantics { contentDescription = label },
        decorationBox = { inner ->
            Box(
                contentAlignment = if (singleLine) Alignment.CenterStart else Alignment.TopStart,
                modifier = Modifier
                    .fillMaxWidth()
                    .heightIn(min = if (singleLine) 44.dp else 110.dp)
                    .background(Color.White, RoundedCornerShape(8.dp))
                    .border(1.dp, if (isError) FlowcusColors.Error else FlowcusColors.FieldBorder, RoundedCornerShape(8.dp))
                    .padding(horizontal = 12.dp, vertical = 11.dp),
            ) {
                if (value.isEmpty()) Text(placeholder, style = serif(13.sp, FlowcusColors.MutedLight))
                inner()
            }
        },
    )
}

@Composable
private fun CategoryPicker(selected: String, onSelect: (String) -> Unit, modifier: Modifier = Modifier) {
    var expanded by remember { mutableStateOf(false) }
    Box(modifier) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .fillMaxWidth()
                .height(44.dp)
                .background(Color.White, RoundedCornerShape(8.dp))
                .border(1.dp, FlowcusColors.FieldBorder, RoundedCornerShape(8.dp))
                .clickable(onClickLabel = "Elegir categoría") { expanded = true }
                .padding(horizontal = 12.dp),
        ) {
            Text(selected, style = serif(13.sp, FlowcusColors.InkStrong), modifier = Modifier.weight(1f))
            Text("▾", style = serif(14.sp, FlowcusColors.Muted))
        }
        DropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }, containerColor = Color.White) {
            Categories.forEach { option ->
                DropdownMenuItem(
                    text = { Text(option, style = serif(13.sp, FlowcusColors.Ink)) },
                    onClick = { onSelect(option); expanded = false },
                )
            }
        }
    }
}
