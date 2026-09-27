package com.flowcus.mobile.ui

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.flowcus.mobile.data.Session
import com.flowcus.mobile.data.SessionFilter
import com.flowcus.mobile.data.SessionStatus
import com.flowcus.mobile.data.Sessions
import com.flowcus.mobile.ui.theme.FlowcusColors
import com.flowcus.mobile.ui.theme.oswald
import com.flowcus.mobile.ui.theme.serif

@Composable
fun PlanningScreen(onNavigate: (NavTab) -> Unit, onOpenSession: (start: Boolean) -> Unit) {
    var weekly by rememberSaveable { mutableStateOf(false) }
    var filter by rememberSaveable { mutableStateOf(SessionFilter.ALL) }
    var query by rememberSaveable { mutableStateOf("") }

    val visible = Sessions.filter { session ->
        val matchesFilter = when (filter) {
            SessionFilter.ALL -> true
            SessionFilter.TODAY -> session.today
            SessionFilter.PENDING -> session.status == SessionStatus.PENDING
            SessionFilter.PREPARING -> session.status == SessionStatus.PREPARING
        }
        val q = query.trim()
        matchesFilter && (q.isEmpty() || listOf(session.title, session.task, session.category).any { it.contains(q, ignoreCase = true) })
    }

    Scaffold(
        containerColor = Color(0xFFFDFDFD),
        bottomBar = { FlowcusBottomBar(current = NavTab.PLANNING, onSelect = onNavigate) },
    ) { padding ->
        Column(
            verticalArrangement = Arrangement.spacedBy(16.dp),
            modifier = Modifier
                .fillMaxSize()
                .statusBarsPadding()
                .padding(bottom = padding.calculateBottomPadding())
                .verticalScroll(rememberScrollState())
                .padding(start = 20.dp, end = 20.dp, top = 20.dp, bottom = 24.dp),
        ) {
            ScreenHeader(
                eyebrow = "Cronograma de alto rendimiento",
                title = "Planificación de sesiones",
                subtitle = "Organiza y agenda tus bloques de trabajo profundo y concentración.",
            )

            Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.fillMaxWidth()) {
                Row(
                    modifier = Modifier
                        .weight(1f)
                        .background(FlowcusColors.SurfaceMuted, RoundedCornerShape(20.dp))
                        .padding(3.dp),
                ) {
                    listOf(false to "Lista", true to "Agenda semanal").forEach { (value, label) ->
                        val selected = weekly == value
                        Text(
                            label,
                            textAlign = TextAlign.Center,
                            style = serif(12.sp, if (selected) FlowcusColors.Ink else FlowcusColors.Muted),
                            modifier = Modifier
                                .weight(1f)
                                .background(if (selected) Color.White else Color.Transparent, RoundedCornerShape(18.dp))
                                .selectable(selected = selected, role = Role.Tab) { weekly = value }
                                .padding(vertical = 8.dp),
                        )
                    }
                }
                Spacer(Modifier.width(10.dp))
                Text(
                    "＋ CREAR",
                    style = oswald(12.sp, .04f, Color.White),
                    modifier = Modifier
                        .background(FlowcusColors.Blue, RoundedCornerShape(20.dp))
                        .clickable(onClickLabel = "Crear sesión") { onOpenSession(false) }
                        .padding(horizontal = 14.dp, vertical = 9.dp),
                )
            }

            if (weekly) {
                WeeklyAgenda(onOpenSession = { onOpenSession(false) })
            } else {
                SearchBox(query) { query = it }
                Row(horizontalArrangement = Arrangement.spacedBy(6.dp), modifier = Modifier.horizontalScroll(rememberScrollState())) {
                    SessionFilter.entries.forEach { option ->
                        val count = when (option) {
                            SessionFilter.ALL -> " (${Sessions.size})"
                            SessionFilter.TODAY -> " (${Sessions.count { it.today }})"
                            else -> ""
                        }
                        val selected = option == filter
                        Text(
                            option.label + count,
                            style = serif(12.sp, if (selected) Color.White else FlowcusColors.Muted),
                            modifier = Modifier
                                .background(if (selected) FlowcusColors.Blue else Color.Transparent, RoundedCornerShape(20.dp))
                                .border(1.dp, if (selected) FlowcusColors.Blue else FlowcusColors.Line, RoundedCornerShape(20.dp))
                                .selectable(selected = selected, role = Role.Tab) { filter = option }
                                .padding(horizontal = 12.dp, vertical = 7.dp),
                        )
                    }
                }

                if (visible.isEmpty()) {
                    Text("No hay sesiones que coincidan.", style = serif(13.sp, FlowcusColors.Muted))
                }
                listOf(true to "Hoy, 24 de octubre", false to "Mañana, 25 de octubre").forEach { (today, label) ->
                    val day = visible.filter { it.today == today }
                    if (day.isNotEmpty()) {
                        FlowRow(
                            verticalArrangement = Arrangement.spacedBy(6.dp),
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            itemVerticalAlignment = Alignment.CenterVertically,
                        ) {
                            Text(label.uppercase(), style = oswald(13.sp, .03f, FlowcusColors.Ink))
                            Pill("${day.size} bloques programados", TagKind.NEUTRAL)
                            if (today) Text("3.5 hrs estimadas", style = serif(11.sp, FlowcusColors.Muted))
                        }
                        day.forEach { SessionCard(it, onOpen = { onOpenSession(false) }, onStart = { onOpenSession(true) }) }
                    }
                }
            }

            DaySummary()
            CategoryDonut()
            FlowcusCard(background = FlowcusColors.SurfaceMuted, border = null) {
                SectionTitle("Regla de los 90 minutos")
                Text(
                    "El cerebro humano mantiene su pico de concentración ultradiana durante bloques no mayores a 90 minutos. Procura no enlazar más de 3 ciclos consecutivos sin pausas largas.",
                    style = serif(12.sp, FlowcusColors.Body).copy(lineHeight = 18.sp),
                    modifier = Modifier.padding(top = 8.dp),
                )
            }
        }
    }
}

@Composable
private fun SearchBox(query: String, onQueryChange: (String) -> Unit) {
    BasicTextField(
        value = query,
        onValueChange = onQueryChange,
        singleLine = true,
        textStyle = serif(13.sp, FlowcusColors.InkStrong),
        cursorBrush = SolidColor(FlowcusColors.Blue),
        modifier = Modifier.fillMaxWidth().semantics { contentDescription = "Buscar sesión" },
        decorationBox = { inner ->
            Box(
                contentAlignment = Alignment.CenterStart,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(40.dp)
                    .background(Color.White, RoundedCornerShape(8.dp))
                    .border(1.dp, FlowcusColors.Line, RoundedCornerShape(8.dp))
                    .padding(horizontal = 14.dp),
            ) {
                if (query.isEmpty()) Text("Buscar sesión, tarea o proyecto...", style = serif(13.sp, FlowcusColors.Muted))
                inner()
            }
        },
    )
}

@Composable
private fun SessionCard(session: Session, onOpen: () -> Unit, onStart: () -> Unit) {
    val canStart = session.status == SessionStatus.PREPARING
    FlowcusCard(modifier = Modifier.clickable(onClickLabel = "Ver detalle", onClick = onOpen)) {
        Row(Modifier.fillMaxWidth()) {
            Column(Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                Pill(session.status.label, TagKind.INFO)
                Text(session.time, style = serif(12.sp, FlowcusColors.Muted))
            }
            Column(horizontalAlignment = Alignment.End) {
                Text(session.cycles, style = oswald(12.sp, color = FlowcusColors.Ink))
                Text(session.rest, style = serif(11.sp, FlowcusColors.Muted))
            }
        }
        Text(session.title.uppercase(), style = oswald(16.sp, .03f, FlowcusColors.Ink), modifier = Modifier.padding(top = 10.dp))
        Text("Tarea: ${session.task}", style = serif(12.sp, FlowcusColors.Body), modifier = Modifier.padding(top = 2.dp))
        Row(horizontalArrangement = Arrangement.spacedBy(6.dp), modifier = Modifier.padding(top = 10.dp)) {
            Pill(session.category, TagKind.INFO)
            if (session.highPriority) Pill("Alta prioridad", TagKind.DANGER)
        }
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.padding(top = 14.dp)) {
            SecondaryButton("VER DETALLE", onClick = onOpen, modifier = Modifier.weight(1f), height = 38.dp)
            PrimaryButton(
                if (canStart) "INICIAR AHORA" else "AÚN NO",
                onClick = onStart,
                enabled = canStart,
                modifier = Modifier.weight(1f),
                height = 38.dp,
            )
        }
    }
}

@Composable
private fun WeeklyAgenda(onOpenSession: () -> Unit) {
    val days = listOf("LUN" to 21, "MAR" to 22, "MIÉ" to 23, "JUE" to 24, "VIE" to 25, "SÁB" to 26, "DOM" to 27)
    var selectedDay by rememberSaveable { mutableStateOf(24) }
    FlowcusCard {
        SectionTitle("Semana 43 · Octubre")
        Row(horizontalArrangement = Arrangement.spacedBy(4.dp), modifier = Modifier.fillMaxWidth().padding(top = 12.dp)) {
            days.forEach { (name, number) ->
                val count = when (number) { 24 -> 3; 25 -> 3; else -> 0 }
                val selected = number == selectedDay
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier
                        .weight(1f)
                        .background(if (selected) FlowcusColors.Blue else Color.Transparent, RoundedCornerShape(10.dp))
                        .selectable(selected = selected, role = Role.Tab) { selectedDay = number }
                        .padding(vertical = 8.dp),
                ) {
                    Text(name, style = oswald(9.sp, color = if (selected) Color.White else FlowcusColors.Muted))
                    Text("$number", style = oswald(15.sp, color = if (selected) Color.White else FlowcusColors.Ink))
                    Box(
                        Modifier
                            .padding(top = 4.dp)
                            .size(5.dp)
                            .background(
                                when {
                                    count == 0 -> Color.Transparent
                                    selected -> Color.White
                                    else -> FlowcusColors.Blue
                                },
                                CircleShape,
                            ),
                    )
                }
            }
        }
        val sessions = Sessions.filter { (selectedDay == 24 && it.today) || (selectedDay == 25 && !it.today) }
        if (sessions.isEmpty()) {
            Text("Sin sesiones programadas este día.", style = serif(12.sp, FlowcusColors.Muted), modifier = Modifier.padding(top = 14.dp))
        }
        sessions.forEach { session ->
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 10.dp)
                    .background(FlowcusColors.Soft, RoundedCornerShape(10.dp))
                    .clickable(onClick = onOpenSession)
                    .padding(12.dp),
            ) {
                Text(session.time.substringBefore(" ("), style = oswald(12.sp, color = FlowcusColors.Blue), modifier = Modifier.width(70.dp))
                Column(Modifier.weight(1f)) {
                    Text(session.title, style = oswald(12.sp, color = FlowcusColors.Ink))
                    Text(session.task, style = serif(11.sp, FlowcusColors.Muted), maxLines = 1)
                }
            }
        }
    }
}

@Composable
private fun DaySummary() {
    FlowcusCard {
        SectionTitle("Resumen del día")
        Row(horizontalArrangement = Arrangement.spacedBy(12.dp), modifier = Modifier.padding(top = 12.dp)) {
            listOf("SESIONES" to "3", "HORAS ENFOQUE" to "3.5").forEach { (label, value) ->
                Column(
                    Modifier
                        .weight(1f)
                        .background(FlowcusColors.Soft, RoundedCornerShape(10.dp))
                        .padding(12.dp),
                ) {
                    Text(label, style = oswald(10.sp, .04f, FlowcusColors.Muted))
                    Text(value, style = oswald(22.sp, color = FlowcusColors.Ink))
                }
            }
        }
        Row(Modifier.fillMaxWidth().padding(top = 14.dp)) {
            Text("Capacidad de carga diaria", style = serif(12.sp, FlowcusColors.Body), modifier = Modifier.weight(1f))
            Text("70%", style = oswald(12.sp, color = FlowcusColors.Ink))
        }
        ProgressBar(.7f, Modifier.padding(top = 6.dp))
        Text("3.5h planificadas · Meta recomendada: 5.0h", style = serif(11.sp, FlowcusColors.Muted), modifier = Modifier.padding(top = 6.dp))
    }
}

@Composable
private fun CategoryDonut() {
    val segments = listOf(
        Triple("Categoría (4)", "1.5h", FlowcusColors.BlueButton) to .45f,
        Triple("Diseño (2)", "1h", FlowcusColors.Charcoal) to .30f,
        Triple("Docs (3)", "1h", FlowcusColors.Gray) to .25f,
    )
    FlowcusCard {
        SectionTitle("Distribución por categoría")
        Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.padding(top = 12.dp)) {
            Box(contentAlignment = Alignment.Center, modifier = Modifier.size(84.dp)) {
                Canvas(Modifier.size(84.dp)) {
                    var start = -90f
                    val stroke = Stroke(width = 14.dp.toPx())
                    segments.forEach { (info, share) ->
                        drawArc(info.third, start, 360f * share, false, style = stroke)
                        start += 360f * share
                    }
                }
                Text("9", style = oswald(18.sp, color = FlowcusColors.Ink))
            }
            Column(verticalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.padding(start = 20.dp).weight(1f)) {
                segments.forEach { (info, _) ->
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(Modifier.size(9.dp).background(info.third, CircleShape))
                        Text(info.first, style = serif(12.sp, FlowcusColors.Body), modifier = Modifier.padding(start = 8.dp).weight(1f))
                        Text(info.second, style = oswald(11.sp, color = FlowcusColors.Muted))
                    }
                }
            }
        }
    }
}
