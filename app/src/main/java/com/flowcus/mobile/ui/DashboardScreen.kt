package com.flowcus.mobile.ui

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.flowcus.mobile.R
import com.flowcus.mobile.ui.theme.FlowcusColors
import com.flowcus.mobile.ui.theme.oswald
import com.flowcus.mobile.ui.theme.serif

private data class PriorityItem(val title: String, val meta: String, val high: Boolean, val inProgress: Boolean = false)

private val PriorityItems = listOf(
    PriorityItem("Diseño del sistema de componentes", "2 ciclos est. · Backend", high = true, inProgress = true),
    PriorityItem("Documentar endpoints de autenticación", "3 ciclos est. · Backend", high = false),
    PriorityItem("Revisar pull request de componentes web", "1 ciclo est. · Frontend", high = true),
)

@Composable
fun DashboardScreen(
    onNavigate: (NavTab) -> Unit,
    onStartBlock: () -> Unit,
    onLogout: () -> Unit,
) {
    var showPerformance by rememberSaveable { mutableStateOf(true) }
    var onlyHigh by rememberSaveable { mutableStateOf(false) }

    Scaffold(
        containerColor = Color(0xFFFDFDFD),
        bottomBar = { FlowcusBottomBar(current = NavTab.DASHBOARD, onSelect = onNavigate) },
    ) { padding ->
        Column(
            verticalArrangement = Arrangement.spacedBy(16.dp),
            modifier = Modifier
                .fillMaxSize()
                .statusBarsPadding()
                .padding(bottom = padding.calculateBottomPadding())
                .verticalScroll(rememberScrollState())
                .padding(start = 20.dp, end = 20.dp, top = 16.dp, bottom = 24.dp),
        ) {
            Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.fillMaxWidth()) {
                Box(Modifier.size(34.dp).background(FlowcusColors.InfoBg, CircleShape), contentAlignment = Alignment.Center) {
                    Text("J", style = oswald(15.sp, color = FlowcusColors.InfoText))
                }
                Column(Modifier.padding(start = 8.dp).weight(1f)) {
                    Text("Perfil", style = serif(10.sp, FlowcusColors.Muted))
                    Text("ACTIVO", style = oswald(11.sp, color = FlowcusColors.Ink))
                }
                IconButton(onClick = onLogout) {
                    Icon(painterResource(R.drawable.ic_logout), contentDescription = "Cerrar sesión", tint = FlowcusColors.Muted, modifier = Modifier.size(20.dp))
                }
            }

            ScreenHeader(
                eyebrow = "Panel principal · Estación Deep Work",
                title = "Bienvenido de nuevo, Juan",
                subtitle = "Mantén el ritmo mental. Tienes 4 bloques estratégicos proyectados para esta jornada.",
            )
            Pill("Jueves, 24 de octubre · Semana 43", TagKind.NEUTRAL)

            FlowcusCard {
                Row(Modifier.fillMaxWidth()) {
                    Text("OBJETIVO DIARIO", style = oswald(10.sp, .04f, FlowcusColors.Muted), modifier = Modifier.weight(1f))
                    Text("3/5 H", style = oswald(10.sp, .04f, FlowcusColors.Ink))
                }
                ProgressBar(.6f, Modifier.padding(top = 8.dp))
            }

            StatCard("TIEMPO ENFOCADO HOY", "2h 15m", null) {
                Text(
                    buildAnnotatedString {
                        withStyle(SpanStyle(color = FlowcusColors.LowText)) { append("↑ +18% ") }
                        append("vs. ayer")
                    },
                    style = serif(11.sp, FlowcusColors.Muted),
                )
                ProgressBar(.6f, Modifier.padding(top = 8.dp))
            }
            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                StatCard("CICLOS POMODORO", "3", "/5", Modifier.weight(1f)) {
                    Text("60% de la meta", style = serif(11.sp, FlowcusColors.Muted))
                    Row(horizontalArrangement = Arrangement.spacedBy(4.dp), modifier = Modifier.padding(top = 8.dp)) {
                        repeat(5) { i ->
                            Box(
                                Modifier
                                    .weight(1f)
                                    .height(5.dp)
                                    .background(if (i < 3) FlowcusColors.Blue else FlowcusColors.NeutralBg, RoundedCornerShape(3.dp)),
                            )
                        }
                    }
                }
                StatCard("TAREAS PENDIENTES", "4", " activas", Modifier.weight(1f)) {
                    Text("2 urgentes · 3 completadas", style = serif(11.sp, FlowcusColors.Muted))
                }
            }

            FlowcusCard(background = FlowcusColors.Charcoal, border = null) {
                FlowRow(horizontalArrangement = Arrangement.spacedBy(8.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                    Pill("Sugerencia inteligente", TagKind.SOLID)
                    Text("Momento óptimo de atención", style = serif(11.sp, Color(0xFFCFD2D6)))
                }
                Text(
                    "DISEÑO DEL SISTEMA DE COMPONENTES",
                    style = oswald(17.sp, .03f, Color.White),
                    modifier = Modifier.padding(top = 12.dp),
                )
                Text(
                    "Asignada a: Sesión de Enfoque Profundo · Estimado: 3 ciclos (75m)",
                    style = serif(12.sp, Color(0xFFCFD2D6)).copy(lineHeight = 17.sp),
                    modifier = Modifier.padding(top = 4.dp),
                )
                FlowRow(
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                    verticalArrangement = Arrangement.spacedBy(6.dp),
                    modifier = Modifier.padding(top = 12.dp),
                ) {
                    listOf("25 min trabajo", "5 min descanso", "Silenciar notificaciones").forEach {
                        Text(
                            it,
                            style = serif(11.sp, Color.White),
                            modifier = Modifier.background(Color(0x33FFFFFF), RoundedCornerShape(12.dp)).padding(horizontal = 9.dp, vertical = 4.dp),
                        )
                    }
                }
                PrimaryButton("▷  INICIAR BLOQUE DE 75 MIN", onClick = onStartBlock, modifier = Modifier.padding(top = 16.dp))
                Text("Ciclo 3 de la jornada", style = serif(11.sp, Color(0xFFCFD2D6)), modifier = Modifier.align(Alignment.CenterHorizontally).padding(top = 8.dp))
            }

            FlowcusCard {
                Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.fillMaxWidth()) {
                    SectionTitle("Rendimiento hoy", Modifier.weight(1f))
                    Switch(
                        checked = showPerformance,
                        onCheckedChange = { showPerformance = it },
                        colors = SwitchDefaults.colors(checkedTrackColor = FlowcusColors.Blue),
                        modifier = Modifier.semantics { contentDescription = "Mostrar rendimiento" },
                    )
                }
                if (showPerformance) {
                    Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.padding(top = 8.dp)) {
                        Box(contentAlignment = Alignment.Center, modifier = Modifier.size(96.dp)) {
                            Canvas(Modifier.size(96.dp)) {
                                val stroke = Stroke(width = 10.dp.toPx(), cap = StrokeCap.Round)
                                drawArc(FlowcusColors.NeutralBg, 0f, 360f, false, style = stroke)
                                drawArc(FlowcusColors.BlueButton, -90f, 360f * .75f, false, style = stroke)
                            }
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Text("3/4", style = oswald(20.sp, color = FlowcusColors.Ink))
                                Text("BLOQUES", style = oswald(8.sp, .05f, FlowcusColors.Muted))
                            }
                        }
                        Spacer(Modifier.width(16.dp))
                        Text(
                            "Estás a sólo 1 ciclo (25 min) de completar tu objetivo de concentración diario.",
                            style = serif(12.sp, FlowcusColors.Body).copy(lineHeight = 18.sp),
                            modifier = Modifier.weight(1f),
                        )
                    }
                } else {
                    Text("Resumen oculto.", style = serif(12.sp, FlowcusColors.Muted), modifier = Modifier.padding(top = 4.dp))
                }
            }

            Column {
                Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.fillMaxWidth()) {
                    SectionTitle("Tareas prioritarias de hoy", Modifier.weight(1f))
                    Pill("4 pendientes", TagKind.INFO)
                    IconButton(onClick = { onlyHigh = !onlyHigh }) {
                        Icon(
                            painterResource(R.drawable.ic_filter),
                            contentDescription = if (onlyHigh) "Mostrar todas las prioridades" else "Mostrar solo prioridad alta",
                            tint = if (onlyHigh) FlowcusColors.Blue else FlowcusColors.Muted,
                            modifier = Modifier.size(18.dp),
                        )
                    }
                }
                if (onlyHigh) {
                    Text("Filtrando: solo prioridad alta", style = serif(11.sp, FlowcusColors.Blue))
                }
                Column(verticalArrangement = Arrangement.spacedBy(10.dp), modifier = Modifier.padding(top = 8.dp)) {
                    PriorityItems.filter { !onlyHigh || it.high }.forEach { item ->
                        PriorityRow(item) { onNavigate(NavTab.TASKS) }
                    }
                }
                Text(
                    "VER TODAS →",
                    style = oswald(12.sp, .04f, FlowcusColors.Blue),
                    modifier = Modifier
                        .align(Alignment.End)
                        .padding(top = 6.dp)
                        .clickable { onNavigate(NavTab.TASKS) }
                        .padding(8.dp),
                )
            }
        }
    }
}

@Composable
private fun StatCard(
    title: String,
    value: String,
    suffix: String?,
    modifier: Modifier = Modifier,
    footer: @Composable () -> Unit,
) {
    FlowcusCard(modifier = modifier, padding = 14.dp) {
        Text(title, style = oswald(10.sp, .04f, FlowcusColors.Muted), maxLines = 1)
        Text(
            buildAnnotatedString {
                append(value)
                if (suffix != null) withStyle(SpanStyle(fontSize = 12.sp, color = FlowcusColors.Muted)) { append(suffix) }
            },
            style = oswald(26.sp, color = FlowcusColors.Ink),
            modifier = Modifier.padding(vertical = 4.dp),
        )
        footer()
    }
}

@Composable
private fun PriorityRow(item: PriorityItem, onClick: () -> Unit) {
    FlowcusCard(padding = 14.dp, modifier = Modifier.clickable(onClick = onClick)) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Box(Modifier.size(8.dp).background(if (item.high) FlowcusColors.DangerText else Color(0xFFE0B400), CircleShape))
            Column(Modifier.weight(1f).padding(horizontal = 12.dp)) {
                Text(item.title, style = serif(13.sp, FlowcusColors.Ink).copy(lineHeight = 17.sp))
                Text(item.meta, style = serif(11.sp, FlowcusColors.Muted), modifier = Modifier.padding(top = 2.dp))
            }
            Column(horizontalAlignment = Alignment.End, verticalArrangement = Arrangement.spacedBy(4.dp)) {
                Pill(if (item.high) "Alta" else "Media", if (item.high) TagKind.DANGER else TagKind.WARNING)
                if (item.inProgress) Pill("En curso", TagKind.INFO)
            }
        }
    }
}

@Composable
fun ProgressBar(progress: Float, modifier: Modifier = Modifier) {
    LinearProgressIndicator(
        progress = { progress },
        color = FlowcusColors.BlueButton,
        trackColor = FlowcusColors.NeutralBg,
        drawStopIndicator = {},
        gapSize = 0.dp,
        modifier = modifier.fillMaxWidth().height(6.dp),
    )
}
