package com.flowcus.mobile.ui

import androidx.annotation.DrawableRes
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.flowcus.mobile.R
import com.flowcus.mobile.data.Priority
import com.flowcus.mobile.ui.theme.FlowcusColors
import com.flowcus.mobile.ui.theme.oswald
import com.flowcus.mobile.ui.theme.serif

fun Priority.colors(): Pair<Color, Color> = when (this) {
    Priority.HIGH -> FlowcusColors.HighText to FlowcusColors.HighBg
    Priority.MEDIUM -> FlowcusColors.Ink to FlowcusColors.MediumBg
    Priority.LOW -> FlowcusColors.LowText to FlowcusColors.LowBg
}

@Composable
fun Tag(text: String, color: Color, background: Color, modifier: Modifier = Modifier) {
    Text(
        text,
        style = oswald(10.sp, color = color),
        maxLines = 1,
        modifier = modifier
            .background(background, RoundedCornerShape(2.dp))
            .padding(horizontal = 7.dp, vertical = 3.dp),
    )
}

@Composable
fun PriorityTag(priority: Priority) {
    val (text, bg) = priority.colors()
    Tag(priority.label, text, bg)
}

enum class TagKind(val text: Color, val background: Color) {
    INFO(FlowcusColors.InfoText, FlowcusColors.InfoBg),
    WARNING(Color(0xFF333333), Color(0xFFFFF3C4)),
    DANGER(FlowcusColors.DangerText, FlowcusColors.DangerBg),
    NEUTRAL(FlowcusColors.NeutralText, FlowcusColors.NeutralBg),
    SOLID(Color.White, FlowcusColors.InfoText),
}

/** Etiqueta en mayúsculas con los estilos `.tag-*` de la versión web. */
@Composable
fun Pill(text: String, kind: TagKind, modifier: Modifier = Modifier) {
    Text(
        text.uppercase(),
        style = oswald(10.sp, .03f, kind.text),
        maxLines = 1,
        modifier = modifier
            .background(kind.background, RoundedCornerShape(4.dp))
            .padding(horizontal = 8.dp, vertical = 3.dp),
    )
}

val CardShape = RoundedCornerShape(15.dp)

@Composable
fun FlowcusCard(
    modifier: Modifier = Modifier,
    background: Color = FlowcusColors.Surface,
    border: Color? = FlowcusColors.Line,
    padding: Dp = 16.dp,
    shape: Shape = CardShape,
    content: @Composable ColumnScope.() -> Unit,
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .background(background, shape)
            .then(if (border != null) Modifier.border(1.dp, border, shape) else Modifier)
            .padding(padding),
        content = content,
    )
}

@Composable
fun ScreenHeader(eyebrow: String, title: String, subtitle: String? = null, modifier: Modifier = Modifier) {
    Column(modifier) {
        Text(eyebrow.uppercase(), style = oswald(10.sp, .04f, FlowcusColors.Muted))
        Text(title.uppercase(), style = oswald(20.sp, .035f, FlowcusColors.Ink).copy(lineHeight = 24.sp), modifier = Modifier.padding(top = 2.dp))
        if (subtitle != null) {
            Text(subtitle, style = serif(13.sp, FlowcusColors.Muted).copy(lineHeight = 18.sp), modifier = Modifier.padding(top = 4.dp))
        }
    }
}

@Composable
fun SectionTitle(text: String, modifier: Modifier = Modifier) {
    Text(text.uppercase(), style = oswald(12.sp, .035f, FlowcusColors.Ink), modifier = modifier)
}

@Composable
fun PrimaryButton(text: String, onClick: () -> Unit, modifier: Modifier = Modifier, enabled: Boolean = true, height: Dp = 44.dp) {
    Button(
        onClick = onClick,
        enabled = enabled,
        shape = RoundedCornerShape(25.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = FlowcusColors.Blue,
            contentColor = Color.White,
            disabledContainerColor = FlowcusColors.NeutralBg,
            disabledContentColor = FlowcusColors.DoneText,
        ),
        modifier = modifier.fillMaxWidth().height(height),
    ) { Text(text, style = oswald(13.sp, .035f)) }
}

@Composable
fun SecondaryButton(text: String, onClick: () -> Unit, modifier: Modifier = Modifier, color: Color = Color(0xFF505357), height: Dp = 40.dp) {
    OutlinedButton(
        onClick = onClick,
        shape = RoundedCornerShape(25.dp),
        border = BorderStroke(1.dp, if (color == Color(0xFF505357)) FlowcusColors.Line else color),
        colors = ButtonDefaults.outlinedButtonColors(containerColor = Color.White, contentColor = color),
        modifier = modifier.fillMaxWidth().height(height),
    ) { Text(text, style = oswald(13.sp, .035f)) }
}

enum class NavTab(val label: String, @DrawableRes val icon: Int) {
    DASHBOARD("Inicio", R.drawable.ic_home),
    TASKS("Tareas", R.drawable.ic_tasks),
    PLANNING("Planificación", R.drawable.ic_calendar),
    HISTORY("Historial", R.drawable.ic_history),
}

/** Barra inferior compartida por las pantallas principales. */
@Composable
fun FlowcusBottomBar(current: NavTab, onSelect: (NavTab) -> Unit, top: (@Composable ColumnScope.() -> Unit)? = null) {
    Column(Modifier.fillMaxWidth().background(Color.White).navigationBarsPadding()) {
        top?.invoke(this)
        HorizontalDivider(color = Color(0xFFE3E5E7))
        Row(Modifier.fillMaxWidth().height(56.dp)) {
            NavTab.entries.forEach { tab -> NavItem(tab, tab == current) { onSelect(tab) } }
        }
    }
}

@Composable
private fun RowScope.NavItem(tab: NavTab, selected: Boolean, onClick: () -> Unit) {
    val color = if (selected) FlowcusColors.Blue else Color(0xFF7A7D81)
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
        modifier = Modifier
            .weight(1f)
            .fillMaxSize()
            .drawBehind { if (selected) drawLine(color, Offset(0f, 0f), Offset(size.width, 0f), 2.dp.toPx()) }
            .selectable(selected = selected, role = Role.Tab, onClick = onClick),
    ) {
        Icon(painterResource(tab.icon), contentDescription = null, tint = color, modifier = Modifier.size(18.dp))
        Spacer(Modifier.height(3.dp))
        Text(tab.label, style = if (selected) oswald(11.sp, color = color) else serif(11.sp, color), maxLines = 1)
    }
}
