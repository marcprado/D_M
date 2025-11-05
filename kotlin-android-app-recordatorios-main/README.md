# StudyReminder - Aplicacion de Recordatorios

Proyecto desarrollado para el ramo de Desarrollo Mobile en DuocUC

## Descripcion del Proyecto

Esta es una aplicacion Android nativa desarrollada en Kotlin con Jetpack Compose que permite a los estudiantes gestionar recordatorios de tareas academicas. La app incluye funcionalidades de creacion, edicion, eliminacion y marcado de recordatorios como completados.

## Tecnologias Utilizadas

- Kotlin como lenguaje principal
- Jetpack Compose para la interfaz de usuario
- Material Design 3 para componentes visuales
- Room Database para persistencia de datos
- MVVM como patron de arquitectura
- Coroutines para operaciones asincronas
- Navigation Compose para navegacion entre pantallas

## Estructura del Proyecto

La aplicacion esta organizada siguiendo una arquitectura MVVM limpia:

```
app/src/main/java/duoc/desarrollomobile/sitioejemplo/
├── data/                    Base de datos y entidades
│   ├── AppDatabase.kt      Configuracion de Room
│   ├── Recordatorio.kt     Entidad principal
│   └── RecordatorioDao.kt  Operaciones de base de datos
├── model/                   Logica de negocio
│   ├── Categoria.kt        Enum de categorias
│   ├── Prioridad.kt        Enum de prioridades
│   └── RecordatorioRepository.kt
├── view/                    Pantallas de la UI
│   ├── HomeScreen.kt       Pantalla principal con lista
│   ├── FormScreen.kt       Formulario de creacion
│   └── DetailScreen.kt     Detalle de recordatorio
├── viewmodel/              Logica de presentacion
│   └── RecordatorioViewModel.kt
├── navigation/             Sistema de navegacion
│   └── AppNavigation.kt
├── ui/                     Tema y componentes
│   ├── theme/              Colores y estilos
│   └── components/         Componentes reutilizables
└── utils/                  Utilidades
    ├── NotificationHelper.kt
    ├── ShareHelper.kt
    └── ValidationUtils.kt
```

## Funcionalidades Implementadas

### 1. Sistema de Navegacion

Implemente navegacion entre tres pantallas principales usando Navigation Compose:
- Pantalla Home que muestra la lista de recordatorios
- Pantalla Form para crear nuevos recordatorios
- Pantalla Detail para ver informacion completa

La navegacion incluye paso de parametros entre pantallas y manejo del back stack correctamente.

### 2. Persistencia con Room

Configure una base de datos local usando Room que persiste todos los recordatorios. Los datos sobreviven al cierre de la aplicacion y se cargan automaticamente al inicio.

La base de datos usa:
- Entidad Recordatorio con todos los campos necesarios
- DAO con operaciones CRUD completas
- Flows reactivos para actualizacion automatica de la UI

### 3. Gestion de Estado con ViewModel

Implemente un ViewModel robusto que maneja:
- Estado del formulario con validaciones en tiempo real
- Operaciones asincronas con coroutines
- Flows para observar cambios en la base de datos
- Logica de negocio separada de la UI

El estado del formulario es inmutable y se actualiza usando copy() para garantizar una UI predecible.

### 4. Interfaz de Usuario Moderna

Diseñe una UI completamente en Jetpack Compose con Material 3 que incluye:

**Paleta de Colores Moderna 2025:**
- Cyan Blue como color primario para confianza
- Deep Purple como secundario para creatividad
- Teal como terciario para balance
- Colores de prioridad vibrantes (Rojo, Ambar, Verde neon)

**Componentes Visuales:**
- TopAppBar con gradiente horizontal personalizado
- FilterChips con colores diferenciados por categoria
- Cards con elevacion y barra lateral de color de prioridad
- FloatingActionButton con elevacion aumentada
- Animaciones suaves en transiciones

### 5. Validaciones del Formulario

Implemente validaciones robustas en el formulario:
- Titulo: minimo 3 caracteres, no vacio
- Descripcion: entre 10 y 200 caracteres
- Categoria: seleccion obligatoria
- Fecha limite: debe ser fecha futura
- Hora: formato HH:mm validado
- Prioridad: seleccion obligatoria

Las validaciones se muestran en tiempo real bajo cada campo con mensajes claros.

### 6. Sistema de Filtros

La pantalla principal incluye tres filtros funcionales:
- Pendientes: muestra solo los no completados
- Completados: muestra los marcados como completados
- Todos: muestra la lista completa

Los filtros actualizan la UI reactivamente usando Flows de Room.

### 7. Dialogos de Confirmacion

Antes de cualquier accion destructiva, muestro un dialogo de confirmacion:
- Eliminar recordatorio individual
- Eliminar todos los completados
- Cambiar estado de completado/pendiente en DetailScreen

Los dialogos tienen:
- Iconos contextuales con colores semanticos
- Mensaje claro mostrando el nombre del item
- Botones bien diferenciados (Confirmar/Cancelar)
- Advertencias apropiadas segun el tipo de accion

### 8. Sistema de Feedback con Snackbars

Implemente Snackbars para dar feedback inmediato al usuario:
- Crear recordatorio: "Recordatorio creado exitosamente"
- Eliminar: "Recordatorio eliminado" con boton Deshacer
- Toggle completado: "Recordatorio completado" o "Marcado como pendiente"
- Eliminar todos: "Se eliminaron X recordatorios completados"

Los Snackbars incluyen:
- Duracion apropiada (Short o Long segun el caso)
- Colores del tema Material 3
- Accion de deshacer en eliminaciones individuales
- Iconos check para acciones exitosas

### 9. Selectores de Fecha y Hora

Corregi problemas de zona horaria e implemente selectores visuales:

**DatePicker:**
- Ajuste correcto de zona horaria UTC a local
- Calculo de offset para evitar cambio de dia
- Formato dd/MM/yyyy consistente

**TimePicker:**
- Selector visual tipo reloj circular Material 3
- Formato 24 horas
- Estado inicial inteligente (hora actual o previamente seleccionada)
- Validacion automatica de horas y minutos validos

### 10. Compartir Funcionalidad

Implemente la capacidad de compartir recordatorios usando ShareHelper:
- Formatea la informacion del recordatorio en texto legible
- Usa Intent de Android para compartir
- Funciona con cualquier app que acepte texto (WhatsApp, Email, etc)

### 11. Sistema de Notificaciones

Configure NotificationHelper con:
- Canal de notificaciones para Android 8+
- Solicitud de permisos en Android 13+
- Toggle para activar/desactivar notificaciones por recordatorio

### 12. Animaciones y Transiciones

Agregue animaciones para mejorar la experiencia:
- Fade in/out al cambiar entre estados vacios y con contenido
- Slide in vertical con efecto bounce al aparecer items
- Transiciones de 300ms para cambios de estado
- AnimatedVisibility en listas de recordatorios

### 13. Componentes Reutilizables

Cree componentes encapsulados para reutilizacion:

**AppTopBar:**
- Gradiente horizontal personalizado
- Soporte para icono de navegacion
- Slot para acciones (IconButtons)
- Manejo correcto de window insets

**AppTopBarWithBack:**
- Variante con boton de volver incluido
- Reutilizado en FormScreen y DetailScreen

### 14. Cards con Diseño Visual Rico

Las cards de recordatorios incluyen:
- Barra lateral gradiente con color de prioridad (6dp de ancho)
- Elevacion con estados (4dp default, 8dp pressed)
- Checkbox para marcar como completado
- Chips de categoria y fecha
- Boton de eliminar con icono
- Efecto de completado con fondo gris y texto tachado

### 15. Empty States

Diseñe estados vacios informativos para cada filtro:
- Icono contextual grande
- Mensaje principal claro
- Texto secundario con accion sugerida
- Diferente para cada filtro (Pendientes/Completados/Todos)

## Aspectos Tecnicos Destacados

### Manejo de Zona Horaria

Resolvi el problema comun de DatePicker y zona horaria:
```kotlin
val offset = timeZone.getOffset(utcMillis)
val localMillis = utcMillis + offset
```
Esto asegura que la fecha seleccionada sea exactamente la que el usuario ve.

### Arquitectura MVVM Limpia

Separe claramente las responsabilidades:
- View: solo renderiza UI y captura eventos
- ViewModel: maneja estado y logica de presentacion
- Repository: abstrae el acceso a datos
- DAO: operaciones de base de datos

### Flujos Reactivos

Use Flows de Kotlin para actualizaciones automaticas:
```kotlin
val recordatoriosPendientes = repository.recordatoriosPendientes
  .collectAsState(initial = emptyList())
```
Cuando cambia la base de datos, la UI se actualiza sola.

### Validaciones Robustas

Cree ValidationUtils con funciones puras para validar:
- Retornan null si es valido
- Retornan mensaje de error si es invalido
- Facil de testear y mantener

## Decisiones de Diseño

### Colores Basados en Tendencias 2025

Investigue tendencias actuales de diseño mobile y aplique:
- Neon Blue para innovacion
- Deep Purple para exclusividad y creatividad
- Gradientes sutiles en lugar de colores planos
- Prioridades con colores vibrantes pero legibles

### Material Design 3

Segui las guias de Material Design 3:
- Uso correcto de elevation y shadows
- Color scheme semantico (primary, secondary, tertiary)
- Componentes standard (Card, Chip, Button, etc)
- Spacing consistente (8dp, 12dp, 16dp)

### UX Centrada en el Usuario

Priorice la experiencia del usuario:
- Confirmaciones antes de acciones destructivas
- Feedback inmediato con Snackbars
- Opcion de deshacer en eliminaciones
- Validaciones en tiempo real (no esperar al submit)
- Mensajes de error claros y accionables
