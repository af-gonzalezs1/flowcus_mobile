package com.flowcus.mobile.ui

import android.util.Patterns
import android.widget.Toast
import androidx.annotation.DrawableRes
import androidx.compose.foundation.border
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawingPadding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.flowcus.mobile.R
import com.flowcus.mobile.ui.theme.FlowcusColors
import com.flowcus.mobile.ui.theme.oswald
import com.flowcus.mobile.ui.theme.serif

@Composable
fun SignupScreen(onSignedUp: () -> Unit) {
    val context = LocalContext.current
    var name by rememberSaveable { mutableStateOf("Juan Pérez") }
    var email by rememberSaveable { mutableStateOf("usuario@ejemplo.com") }
    var password by rememberSaveable { mutableStateOf("Clave123!") }
    var showPassword by rememberSaveable { mutableStateOf(false) }
    var terms by rememberSaveable { mutableStateOf(true) }
    var message by rememberSaveable { mutableStateOf("") }

    fun submit() {
        message = when {
            name.isBlank() -> "Ingresa tu nombre completo."
            !Patterns.EMAIL_ADDRESS.matcher(email.trim()).matches() -> "Ingresa un correo electrónico válido."
            password.length < 8 -> "La contraseña debe tener al menos 8 caracteres."
            !password.any { it.isDigit() } || password.all { it.isLetterOrDigit() } ->
                "La contraseña debe incluir al menos un número y un símbolo."
            !terms -> "Debes aceptar los Términos de Servicio para continuar."
            else -> ""
        }
        if (message.isEmpty()) onSignedUp()
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(FlowcusColors.Paper)
            .safeDrawingPadding()
            .imePadding()
            .verticalScroll(rememberScrollState())
            .padding(start = 24.dp, end = 24.dp, top = 34.dp, bottom = 50.dp),
    ) {
        Text("CREAR CUENTA", style = oswald(24.sp, .04f, FlowcusColors.Ink))
        Text(
            "Ingresa tus datos para comenzar tu gestión de flowcus y tareas.",
            style = serif(14.sp, FlowcusColors.MutedLight).copy(lineHeight = 19.sp),
            modifier = Modifier.padding(top = 5.dp),
        )

        Column(Modifier.padding(top = 28.dp), verticalArrangement = Arrangement.spacedBy(24.dp)) {
            FormField(
                label = "Nombre completo", value = name, icon = R.drawable.ic_user,
                onValueChange = { name = it; message = "" },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Text, imeAction = ImeAction.Next),
            )
            FormField(
                label = "Correo electrónico", value = email, icon = R.drawable.ic_mail,
                onValueChange = { email = it; message = "" },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email, imeAction = ImeAction.Next),
            )
            Column {
                FormField(
                    label = "Contraseña", value = password, icon = R.drawable.ic_lock,
                    onValueChange = { password = it; message = "" },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password, imeAction = ImeAction.Done),
                    visualTransformation = if (showPassword) VisualTransformation.None else PasswordVisualTransformation(),
                    trailing = {
                        IconButton(onClick = { showPassword = !showPassword }, modifier = Modifier.size(36.dp)) {
                            Icon(
                                painterResource(if (showPassword) R.drawable.ic_eye_off else R.drawable.ic_eye),
                                contentDescription = if (showPassword) "Ocultar contraseña" else "Mostrar contraseña",
                                tint = FlowcusColors.MutedLight,
                                modifier = Modifier.size(16.dp),
                            )
                        }
                    },
                )
                Text(
                    "Usa al menos 8 caracteres con números y símbolos.",
                    style = serif(11.sp, FlowcusColors.MutedLight),
                    modifier = Modifier.padding(top = 4.dp),
                )
            }
        }

        Row(
            verticalAlignment = Alignment.Top,
            modifier = Modifier.padding(top = 12.dp).clickable { terms = !terms; message = "" },
        ) {
            Checkbox(
                checked = terms,
                onCheckedChange = null,
                colors = CheckboxDefaults.colors(checkedColor = FlowcusColors.Ink),
                modifier = Modifier.size(20.dp).padding(top = 2.dp),
            )
            Spacer(Modifier.width(6.dp))
            Text(
                buildAnnotatedString {
                    append("Acepto los ")
                    withStyle(SpanStyle(color = FlowcusColors.Ink, textDecoration = TextDecoration.Underline)) {
                        append("Términos de Servicio")
                    }
                    append(" y la ")
                    withStyle(SpanStyle(textDecoration = TextDecoration.Underline)) { append("Política de Privacidad") }
                },
                style = serif(11.sp, FlowcusColors.MutedLight).copy(lineHeight = 15.sp),
            )
        }

        if (message.isNotEmpty()) {
            Text(message, style = serif(11.sp, FlowcusColors.Error), modifier = Modifier.padding(top = 12.dp))
        }

        Button(
            onClick = ::submit,
            shape = RoundedCornerShape(26.dp),
            colors = ButtonDefaults.buttonColors(containerColor = FlowcusColors.BlueButton, contentColor = FlowcusColors.Paper),
            modifier = Modifier.fillMaxWidth().padding(top = 16.dp).height(50.dp),
        ) {
            Text("CREAR CUENTA  →", style = oswald(14.sp, .05f))
        }

        Row(
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.fillMaxWidth().padding(top = 8.dp),
        ) {
            Text("¿Ya tienes una cuenta?", style = serif(12.sp, FlowcusColors.MutedLight), textAlign = TextAlign.Center)
            Text(
                "INICIAR SESIÓN",
                style = oswald(12.sp, color = FlowcusColors.Ink).copy(textDecoration = TextDecoration.Underline),
                modifier = Modifier
                    .clickable { Toast.makeText(context, "Inicio de sesión disponible próximamente", Toast.LENGTH_SHORT).show() }
                    .padding(6.dp),
            )
        }
    }
}

@Composable
private fun FormField(
    label: String,
    value: String,
    @DrawableRes icon: Int,
    onValueChange: (String) -> Unit,
    keyboardOptions: KeyboardOptions,
    visualTransformation: VisualTransformation = VisualTransformation.None,
    trailing: (@Composable () -> Unit)? = null,
) {
    var focused by remember { mutableStateOf(false) }
    BasicTextField(
        value = value,
        onValueChange = onValueChange,
        singleLine = true,
        textStyle = serif(14.sp, FlowcusColors.InkStrong),
        cursorBrush = SolidColor(FlowcusColors.Blue),
        keyboardOptions = keyboardOptions,
        visualTransformation = visualTransformation,
        modifier = Modifier
            .fillMaxWidth()
            .onFocusChanged { focused = it.isFocused }
            .semantics { contentDescription = label },
        decorationBox = { inner ->
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp)
                    .background(FlowcusColors.Surface, RoundedCornerShape(8.dp))
                    .border(
                        width = if (focused) 1.5.dp else 1.dp,
                        color = if (focused) FlowcusColors.BlueButton else FlowcusColors.FieldBorder,
                        shape = RoundedCornerShape(8.dp),
                    )
                    .padding(start = 14.dp, end = if (trailing != null) 4.dp else 16.dp),
            ) {
                Icon(painterResource(icon), contentDescription = null, tint = FlowcusColors.MutedLight, modifier = Modifier.size(14.dp))
                Spacer(Modifier.width(12.dp))
                Box(Modifier.weight(1f)) {
                    if (value.isEmpty()) Text(label, style = serif(14.sp, FlowcusColors.MutedLight))
                    inner()
                }
                trailing?.invoke()
            }
        },
    )
}
