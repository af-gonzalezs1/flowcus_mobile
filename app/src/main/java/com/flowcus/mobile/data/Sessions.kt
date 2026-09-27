package com.flowcus.mobile.data

enum class SessionStatus(val label: String) { PREPARING("En preparación"), PENDING("Pendiente") }

enum class SessionFilter(val label: String) { ALL("Todos"), TODAY("Hoy"), PENDING("Pendientes"), PREPARING("En preparación") }

data class Session(
    val id: Int,
    val title: String,
    val status: SessionStatus,
    val time: String,
    val cycles: String,
    val rest: String,
    val task: String,
    val category: String,
    val highPriority: Boolean = false,
    val today: Boolean = true,
)

// Contenido de la pantalla web de Planificación; las sesiones de mañana completan el contador "Todos (6)".
val Sessions = listOf(
    Session(1, "Sesión 1", SessionStatus.PREPARING, "09:30 AM (en 45 min)", "4 ciclos × 25m", "Descanso: 5m", "Diseño del sistema de componentes", "Backend"),
    Session(2, "Sesión 2", SessionStatus.PENDING, "11:30 AM", "2 ciclos × 30m", "Descanso: 10m", "Documentar endpoints de autenticación", "Backend", highPriority = true),
    Session(3, "Sesión 3", SessionStatus.PENDING, "04:00 PM", "3 ciclos × 25m", "Descanso: 5m", "Revisar pull request de componentes web", "Frontend"),
    Session(4, "Sesión 4", SessionStatus.PENDING, "08:00 AM", "3 ciclos × 25m", "Descanso: 5m", "Preparar demo del sprint", "Frontend", today = false),
    Session(5, "Sesión 5", SessionStatus.PENDING, "10:30 AM", "2 ciclos × 25m", "Descanso: 5m", "Estudiar patrones de accesibilidad", "Estudio", today = false),
    Session(6, "Sesión 6", SessionStatus.PENDING, "03:00 PM", "2 ciclos × 30m", "Descanso: 10m", "Refactorizar servicio de sesiones", "Backend", highPriority = true, today = false),
)
