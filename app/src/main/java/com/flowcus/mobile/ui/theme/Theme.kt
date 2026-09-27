package com.flowcus.mobile.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.em
import com.flowcus.mobile.R

// Paleta tomada de styles.css de la versión HTML (tema claro únicamente, como el Figma).
object FlowcusColors {
    val Ink = Color(0xFF292B2E)
    val InkStrong = Color(0xFF1C1C1C)
    val Muted = Color(0xFF777A7D)
    val MutedLight = Color(0xFF818181)
    val Line = Color(0xFFDFE1E4)
    val FieldBorder = Color(0xFFDBDBDB)
    val Blue = Color(0xFF59A5DF)
    val BlueButton = Color(0xFF55A8E3)
    val BlueDark = Color(0xFF347CAE)
    val BlueSoft = Color(0xFFE1F0FF)
    val Paper = Color(0xFFFCFCFC)
    val Surface = Color(0xFFFFFFFF)
    val SurfaceMuted = Color(0xFFE6E7E9)
    val DoneCard = Color(0xFFE8E9EA)
    val DoneText = Color(0xFF9B9D9F)
    val Body = Color(0xFF515459)
    val Error = Color(0xFFA43D39)
    val HighText = Color(0xFF9E2424)
    val HighBg = Color(0xFFFFDDDD)
    val MediumBg = Color(0xFFFFF4CB)
    val LowText = Color(0xFF36784A)
    val LowBg = Color(0xFFD9F2DF)

    // Etiquetas y superficies de las pantallas de Dashboard, Planificación y Sesión (web).
    val InfoText = Color(0xFF2F7EB3)
    val InfoBg = Color(0xFFDCEBF9)
    val DangerText = Color(0xFFAD2929)
    val DangerBg = Color(0xFFF8D3D3)
    val NeutralText = Color(0xFF4C4F52)
    val NeutralBg = Color(0xFFE5E5E5)
    val Charcoal = Color(0xFF303438)
    val Gray = Color(0xFFB7B7B7)
    val Soft = Color(0xFFF0F0F0)
}

val Oswald = FontFamily(
    Font(R.font.oswald_regular, FontWeight.Normal),
    Font(R.font.oswald_bold, FontWeight.Bold),
)

val SourceSerif = FontFamily(
    Font(R.font.source_serif_regular, FontWeight.Normal),
    Font(R.font.source_serif_bold, FontWeight.Bold),
    Font(R.font.source_serif_italic, FontWeight.Normal, FontStyle.Italic),
)

fun oswald(size: TextUnit, spacing: Float = 0f, color: Color = Color.Unspecified) = TextStyle(
    fontFamily = Oswald,
    fontWeight = FontWeight.Bold,
    fontSize = size,
    letterSpacing = spacing.em,
    color = color,
)

fun serif(size: TextUnit, color: Color = Color.Unspecified) = TextStyle(
    fontFamily = SourceSerif,
    fontSize = size,
    color = color,
)

@Composable
fun FlowcusTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = lightColorScheme(
            primary = FlowcusColors.Blue,
            onPrimary = Color.White,
            background = FlowcusColors.Paper,
            surface = FlowcusColors.Surface,
            onSurface = FlowcusColors.Ink,
            onBackground = FlowcusColors.Ink,
            error = FlowcusColors.Error,
        ),
        content = content,
    )
}
