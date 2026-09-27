package com.flowcus.mobile.ui

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.listSaver
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.role
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.flowcus.mobile.R
import com.flowcus.mobile.ui.theme.FlowcusColors
import com.flowcus.mobile.ui.theme.oswald
import com.flowcus.mobile.ui.theme.serif
import kotlinx.coroutines.delay

private const val FocusMinutes = 25
private const val RestMinutes = 5
private const val StartMinuteOfDay = 11 * 60

@Composable
fun SessionDetailScreen(startImmediately: Boolean, onBack: () -> Unit) {
    val context = LocalContext.current
    var cycles by rememberSaveable { mutableIntStateOf(4) }
    var started by rememberSaveable { mutableStateOf(startImmediately) }
    var paused by rememberSaveable { mutableStateOf(false) }
    // Antes de iniciar: cuenta regresiva hasta el inicio. Después: tiempo restante del bloque de foco actual.
    var seconds by rememberSaveable { mutableIntStateOf(if (startImmediately) FocusMinutes * 60 else 34 * 60 + 12) }
    var editing by rememberSaveable { mutableStateOf(false) }
    var confirmCancel by rememberSaveable { mutableStateOf(false) }
    val checklist = rememberSaveable(saver = listSaver(save = { it.toList() }, restore = { mutableStateListOf(*it.toTypedArray()) })) {
        mutableStateListOf(true, true, false, false)
    }

    LaunchedEffect(started, paused) {
        while (!paused && seconds > 0) {
            delay(1000)
            seconds--
        }
    }

    val focusTotal = cycles * FocusMinutes
    val restTotal = (cycles - 1) * RestMinutes
    val total = focusTotal + restTotal
    val done = checklist.count { it }

    Column(
        verticalArrangement = Arrangement.spacedBy(16.dp),
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFFDFDFD))
            .statusBarsPadding()
            .verticalScroll(rememberScrollState())
            .padding(start = 20.dp, end = 20.dp, top = 8.dp, bottom = 32.dp),
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            IconButton(onClick = onBack, modifier = Modifier.size(40.dp)) {
                Icon(painterResource(R.drawable.ic_back), contentDescription = "Volver a planificación", tint = FlowcusColors.Ink, modifier = Modifier.size(20.dp))
            }
            Text("Planificación  ›  ", style = serif(12.sp, FlowcusColors.Muted))
            Text("Detalle de sesión #TSK-8821", style = serif(12.sp, FlowcusColors.Ink))
        }

        FlowRow(horizontalArrangement = Arrangement.spacedBy(8.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
            Pill("● Programada para hoy 11:00 AM", TagKind.INFO)
            Pill("Duración estimada: ${formatDuration(total)}", TagKind.NEUTRAL)
        }
        ScreenHeader(
            eyebrow = "Deep work block · Enfoque crítico",
            title = "Sesión de Enfoque Profundo",
            subtitle = "Bloque dedicado a diseño de interfaz y revisión de componentes.",
        )

        FlowcusCard(background = FlowcusColors.Charcoal, border = null) {
            Text(
                when {
                    !started -> "CUENTA REGRESIVA PARA INICIAR"
                    paused -> "SESIÓN EN PAUSA · BLOQUE B1"
                    seconds == 0 -> "BLOQUE B1 COMPLETADO"
                    else -> "BLOQUE B1 EN CURSO · FOCO"
                },
                style = oswald(10.sp, .05f, Color(0xFFCFD2D6)),
            )
            Text(formatClock(seconds), style = oswald(40.sp, .04f, Color.White), modifier = Modifier.padding(top = 4.dp))
        }

        FlowcusCard {
            Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.fillMaxWidth()) {
                SectionTitle("Estructura de intervalos", Modifier.weight(1f))
                Pill("$total min totales", TagKind.NEUTRAL)
            }
            FlowRow(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.padding(top = 12.dp),
            ) {
                for (i in 1..cycles) {
                    IntervalBlock("B$i", "${FocusMinutes}m Foco", current = started && i == 1, rest = false)
                    if (i < cycles) IntervalBlock("D$i", "${RestMinutes}m Pausa", current = false, rest = true)
                }
            }
            Column(verticalArrangement = Arrangement.spacedBy(10.dp), modifier = Modifier.padding(top = 16.dp)) {
                Metric("TIEMPO DE FOCO NETO", "$focusTotal min")
                Metric("PAUSAS TÁCTICAS", "${cycles - 1} descansos (${restTotal}m)")
                Metric("RENDIMIENTO PROYECTADO", "+94% flujo")
            }
        }

        FlowcusCard {
            SectionTitle("Tarea primaria vinculada")
            Text("ID: #TSK-8821", style = serif(11.sp, FlowcusColors.Muted), modifier = Modifier.padding(top = 10.dp))
            Text("DISEÑO DEL SISTEMA DE COMPONENTES", style = oswald(15.sp, .03f, FlowcusColors.Ink), modifier = Modifier.padding(top = 2.dp))
            Row(Modifier.fillMaxWidth().padding(top = 12.dp)) {
                Text("PROGRESO DE CHECKLIST", style = oswald(10.sp, .04f, FlowcusColors.Muted), modifier = Modifier.weight(1f))
                Text("$done de ${checklist.size} completadas (${done * 100 / checklist.size}%)", style = serif(11.sp, FlowcusColors.Body))
            }
            ProgressBar(done / checklist.size.toFloat(), Modifier.padding(top = 6.dp))
            checklist.forEachIndexed { index, checked ->
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 10.dp)
                        .clickable { checklist[index] = !checked }
                        .semantics { role = Role.Checkbox }
                        .padding(vertical = 4.dp),
                ) {
                    Box(
                        contentAlignment = Alignment.Center,
                        modifier = Modifier
                            .size(20.dp)
                            .background(if (checked) FlowcusColors.Blue else Color.White, CircleShape)
                            .border(1.dp, if (checked) FlowcusColors.Blue else FlowcusColors.FieldBorder, CircleShape),
                    ) {
                        if (checked) Text("✓", style = oswald(11.sp, color = Color.White))
                    }
                    Text(
                        "Tarea ${index + 1} · " + if (checked) "Completado en preparación" else "Pendiente de inicio",
                        style = serif(12.sp, if (checked) FlowcusColors.Muted else FlowcusColors.Body),
                        modifier = Modifier.padding(start = 10.dp),
                    )
                }
            }
        }

        FlowcusCard {
            Text("PANEL DE CONTROL", style = oswald(10.sp, .05f, FlowcusColors.Muted))
            SectionTitle("Acciones de sesión", Modifier.padding(top = 2.dp))
            Text(
                "Inicia inmediatamente la cuenta regresiva o ajusta los parámetros antes de arrancar.",
                style = serif(12.sp, FlowcusColors.Body).copy(lineHeight = 18.sp),
                modifier = Modifier.padding(top = 6.dp),
            )
            PrimaryButton(
                when {
                    !started -> "▷  INICIAR SESIÓN AHORA"
                    paused -> "▷  REANUDAR SESIÓN"
                    else -> "❚❚  PAUSAR SESIÓN"
                },
                onClick = {
                    if (!started) {
                        started = true
                        paused = false
                        seconds = FocusMinutes * 60
                    } else {
                        paused = !paused
                    }
                },
                modifier = Modifier.padding(top = 14.dp),
            )
            SecondaryButton("EDITAR SESIÓN", onClick = { editing = true }, modifier = Modifier.padding(top = 10.dp))
            Column(verticalArrangement = Arrangement.spacedBy(10.dp), modifier = Modifier.padding(top = 16.dp)) {
                MetaRow("Hora de inicio prevista", "11:00 AM")
                MetaRow("Hora de culminación", formatTime(StartMinuteOfDay + total))
                MetaRow("Dispositivo ancla", "MacBook Pro M3")
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text("Modo de bloqueo web", style = serif(12.sp, FlowcusColors.Muted), modifier = Modifier.weight(1f))
                    Pill("Hard Shield", TagKind.SOLID)
                }
            }
        }

        FlowcusCard(background = Color(0xFFFFF6F6), border = FlowcusColors.DangerBg) {
            Text("ZONA DE RIESGO", style = oswald(12.sp, .035f, FlowcusColors.DangerText))
            Text(
                "Si cancelas, la ranura horaria quedará libre y la tarea volverá a pendientes.",
                style = serif(12.sp, FlowcusColors.Body).copy(lineHeight = 18.sp),
                modifier = Modifier.padding(top = 6.dp),
            )
            SecondaryButton("CANCELAR SESIÓN", onClick = { confirmCancel = true }, color = FlowcusColors.DangerText, modifier = Modifier.padding(top = 12.dp))
        }

        FlowcusCard(background = FlowcusColors.SurfaceMuted, border = null) {
            SectionTitle("Consejo de optimización")
            Text(
                "Los primeros 12 minutos de cada intervalo presentan la mayor fricción cognitiva. Mantén tu editor abierto antes de hacer click en Iniciar sesión.",
                style = serif(12.sp, FlowcusColors.Body).copy(lineHeight = 18.sp),
                modifier = Modifier.padding(top = 8.dp),
            )
        }
    }

    if (editing) {
        EditSessionDialog(
            cycles = cycles,
            onDismiss = { editing = false },
            onSave = {
                cycles = it
                editing = false
                Toast.makeText(context, "Sesión actualizada: $it ciclos", Toast.LENGTH_SHORT).show()
            },
        )
    }

    if (confirmCancel) {
        AlertDialog(
            onDismissRequest = { confirmCancel = false },
            containerColor = Color.White,
            shape = CardShape,
            title = { Text("¿CANCELAR SESIÓN?", style = oswald(18.sp, color = FlowcusColors.Ink)) },
            text = { Text("La ranura de las 11:00 AM quedará libre y la tarea volverá a pendientes.", style = serif(13.sp, FlowcusColors.Body)) },
            dismissButton = {
                TextButton(onClick = { confirmCancel = false }) { Text("Mantener", style = oswald(12.sp, color = FlowcusColors.Ink)) }
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        confirmCancel = false
                        Toast.makeText(context, "Sesión cancelada", Toast.LENGTH_SHORT).show()
                        onBack()
                    },
                    colors = ButtonDefaults.textButtonColors(containerColor = FlowcusColors.DangerText, contentColor = Color.White),
                ) { Text("Cancelar sesión", style = oswald(12.sp)) }
            },
        )
    }
}

@Composable
private fun IntervalBlock(code: String, label: String, current: Boolean, rest: Boolean) {
    val (bg, fg) = when {
        current -> FlowcusColors.BlueButton to Color.White
        rest -> FlowcusColors.Soft to FlowcusColors.MutedLight
        else -> FlowcusColors.NeutralBg to FlowcusColors.Charcoal
    }
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .widthIn(min = 64.dp)
            .background(bg, RoundedCornerShape(10.dp))
            .padding(horizontal = 10.dp, vertical = 10.dp),
    ) {
        Text(code, style = oswald(14.sp, color = fg))
        Text(label, style = serif(10.sp, fg), modifier = Modifier.padding(top = 2.dp))
    }
}

@Composable
private fun Metric(label: String, value: String) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Text(label, style = oswald(10.sp, .04f, FlowcusColors.Muted), modifier = Modifier.weight(1f))
        Text(value, style = oswald(13.sp, color = FlowcusColors.Ink))
    }
}

@Composable
private fun MetaRow(label: String, value: String) {
    Row {
        Text(label, style = serif(12.sp, FlowcusColors.Muted), modifier = Modifier.weight(1f))
        Text(value, style = oswald(12.sp, color = FlowcusColors.Ink))
    }
}

@Composable
private fun EditSessionDialog(cycles: Int, onDismiss: () -> Unit, onSave: (Int) -> Unit) {
    var value by rememberSaveable { mutableIntStateOf(cycles) }
    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = Color.White,
        shape = CardShape,
        title = { Text("EDITAR SESIÓN", style = oswald(18.sp, color = FlowcusColors.Ink)) },
        text = {
            Column {
                Text("Ciclos de foco (25 min + 5 min de pausa)", style = serif(13.sp, FlowcusColors.Body))
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center,
                    modifier = Modifier.fillMaxWidth().padding(top = 16.dp),
                ) {
                    StepButton("−", enabled = value > 1, label = "Quitar ciclo") { value-- }
                    Text("$value", textAlign = TextAlign.Center, style = oswald(28.sp, color = FlowcusColors.Ink), modifier = Modifier.width(72.dp))
                    StepButton("+", enabled = value < 6, label = "Agregar ciclo") { value++ }
                }
                Spacer(Modifier.size(12.dp))
                Text(
                    "Duración total: ${formatDuration(value * FocusMinutes + (value - 1) * RestMinutes)}",
                    style = serif(12.sp, FlowcusColors.Muted),
                    textAlign = TextAlign.Center,
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
                onClick = { onSave(value) },
                colors = ButtonDefaults.textButtonColors(containerColor = FlowcusColors.Blue, contentColor = Color.White),
            ) { Text("Guardar", style = oswald(12.sp)) }
        },
    )
}

@Composable
private fun StepButton(text: String, enabled: Boolean, label: String, onClick: () -> Unit) {
    TextButton(
        onClick = onClick,
        enabled = enabled,
        shape = CircleShape,
        colors = ButtonDefaults.textButtonColors(containerColor = FlowcusColors.InfoBg, contentColor = FlowcusColors.InfoText),
        modifier = Modifier.size(48.dp).semantics { contentDescription = label },
    ) { Text(text, style = oswald(20.sp)) }
}

private fun formatClock(totalSeconds: Int): String {
    val h = totalSeconds / 3600
    val m = totalSeconds % 3600 / 60
    val s = totalSeconds % 60
    return "%02d:%02d:%02d".format(h, m, s)
}

private fun formatDuration(minutes: Int): String =
    if (minutes >= 60) "${minutes / 60}h ${minutes % 60}m" else "${minutes}m"

private fun formatTime(minuteOfDay: Int): String {
    val h24 = minuteOfDay / 60 % 24
    val h12 = if (h24 % 12 == 0) 12 else h24 % 12
    return "%02d:%02d %s".format(h12, minuteOfDay % 60, if (h24 < 12) "AM" else "PM")
}
