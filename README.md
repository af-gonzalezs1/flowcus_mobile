# Flowcus — Frontend Mobile

Maquetación nativa en Android (Kotlin + Jetpack Compose) del frontend móvil de
Flowcus, la aplicación de gestión de tareas y sesiones de enfoque (técnica
Pomodoro). Las interfaces son navegables entre sí; los componentes con algún
nivel de interacción (pestañas, filtros, checkboxes, campos de formulario,
diálogos) responden visualmente al usuario, pero no ejecutan lógica de negocio
real ni se conectan a un backend: el estado vive en memoria mientras la app
está abierta.

## Estructura del repositorio

```
app/src/main/java/com/flowcus/mobile/
  MainActivity.kt              → Actividad única y navegación entre pantallas
  data/
    Tasks.kt                   → Tareas de ejemplo y estado en memoria
    Sessions.kt                → Sesiones de ejemplo de Planificación
  ui/
    SignupScreen.kt            → Pantalla de entrada (Crear cuenta)
    TasksScreen.kt             → Gestión de tareas
    CreateTaskScreen.kt        → Crear tarea
    DashboardScreen.kt         → Dashboard
    PlanningScreen.kt          → Planificación de sesiones
    SessionDetailScreen.kt     → Detalle de sesión
    TaskDetailScreen.kt        → Detalle de tarea (vista complementaria)
    Components.kt              → Componentes compartidos (etiquetas, tarjetas, botones, barra inferior)
    theme/Theme.kt             → Colores y tipografías (Oswald, Source Serif 4)
app/src/main/res/
  drawable/                    → Íconos vectoriales
  font/                        → Fuentes Oswald y Source Serif 4
*.html, styles.css, script.js  → Prototipo HTML móvil original (referencia)
```

## Pantallas maquetadas

1. **Crear cuenta** (`SignupScreen.kt`)
   Formulario de registro: nombre, correo, contraseña con toggle de
   visibilidad, aceptación de términos y validación básica de campos.

2. **Gestión de tareas** (`TasksScreen.kt`)
   Listado de tareas con filtros (Todas / En curso / Completadas), buscador,
   checkboxes para completar tareas y acceso a la creación de una nueva tarea
   (botón **＋ Nueva**) y al detalle de cada tarea.

3. **Crear tarea** (`CreateTaskScreen.kt`)
   Formulario para vincular una tarea a una sesión activa: título, categoría,
   nivel de prioridad, estimación en ciclos Pomodoro y checklist de subtareas.
   Al guardar, la tarea aparece en la lista de tareas.

4. **Dashboard** (`DashboardScreen.kt`)
   Resumen del día: tiempo enfocado, ciclos Pomodoro completados, tareas
   pendientes, sugerencia de próxima sesión y lista de tareas prioritarias.

5. **Planificación de sesiones** (`PlanningScreen.kt`)
   Agenda de sesiones programadas para el día, con estado de cada una (en
   preparación / pendiente), vista de lista o agenda semanal, resumen de carga
   diaria y distribución por categoría.

6. **Detalle de sesión** (`SessionDetailScreen.kt`)
   Vista de control de una sesión: estructura de intervalos de foco/pausa,
   cuenta regresiva, tarea vinculada con su checklist de progreso, panel de
   acciones y zona de cancelación.

Además, **Detalle de tarea** (`TaskDetailScreen.kt`) muestra el estado,
prioridad, estimación, notas y subtareas de una tarea, con las acciones
*Usar en Flowcus* y *Editar tarea*.

### Navegación

- Crear cuenta → Gestión de tareas.
- La barra inferior conecta **Inicio** (Dashboard), **Tareas**,
  **Planificación** e **Historial** (tareas completadas).
- Tareas → Crear tarea / Detalle de tarea.
- Dashboard → *Iniciar bloque de 75 min* → Detalle de sesión.
- Planificación → *Ver detalle*, *Iniciar ahora* o *Crear* → Detalle de sesión.
- El ícono de salida del Dashboard vuelve a Crear cuenta.

## Cómo verlo localmente

Requiere Android Studio (incluye el JDK y el SDK de Android).

1. Abrir la carpeta del repositorio en Android Studio y esperar a que termine
   la sincronización de Gradle.
2. Elegir un emulador o un teléfono Android (8.0 o superior) conectado por USB.
3. Pulsar **Run ▶**.

También se puede compilar desde la terminal:

```bash
./gradlew assembleDebug     # app/build/outputs/apk/debug/app-debug.apk
./gradlew assembleRelease   # app/build/outputs/apk/release/app-release.apk
```

El APK de release se firma con la clave de depuración, de modo que puede
instalarse directamente en cualquier dispositivo (activando "Instalar apps de
origen desconocido") o con:

```bash
adb install app/build/outputs/apk/release/app-release.apk
```
