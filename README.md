# Semanales - Gestor de Tareas y Citas

**Semanales** es una aplicación de Android diseñada para la gestión organizada de tareas y citas semanales. Ofrece una interfaz intuitiva con vistas de calendario mensual y listados detallados por semana, permitiendo un control total sobre tus responsabilidades diarias.

## 🚀 Características principales

*   **Vista de Calendario Mensual:** Navega fácilmente entre meses para ver una visión general de tus tareas. Los días con tareas pendientes o completadas están resaltados visualmente.
*   **Gestión Semanal Detallada:** Visualiza las tareas de la semana actual con detalles como título, descripción, fecha y hora.
*   **Añadir Citas/Tareas:** Diálogo completo para añadir nuevas tareas especificando:
    *   Título (obligatorio).
    *   Descripción.
    *   Fecha (con selector de fecha).
    *   Hora opcional (con selector de hora).
*   **Control de Estado:** Marca tareas como completadas directamente desde la lista principal.
*   **Eliminación de Tareas:** Opción para borrar tareas con confirmación de seguridad.
*   **Interfaz Adaptada:** Diseño limpio basado en Material Design 3, optimizado para el modo claro para una mejor legibilidad.
*   **Persistencia de Datos:** Utiliza SQLite (`DBHelper`) para almacenar tus tareas de forma local y segura.

## 🛠️ Tecnologías utilizadas

*   **Lenguaje:** Kotlin
*   **Arquitectura:** Basada en actividades y adaptadores personalizados.
*   **UI:** Jetpack (ConstraintLayout, CoordinatorLayout, RecyclerView, Material Components).
*   **Base de Datos:** SQLite para el almacenamiento local.
*   **Herramientas:** Android Studio, Gradle (KTS).

## 📱 Funcionamiento de la App

1.  **Pantalla de Inicio (Splash):** Al abrir la app, se muestra un logo de bienvenida durante 2 segundos.
2.  **Pantalla Principal:**
    *   En la parte superior, encontrarás un **calendario mensual**. Puedes cambiar de mes con las flechas laterales. Al pulsar un día, la lista inferior se actualizará a la semana correspondiente.
    *   En la parte central, se muestra el **rango de la semana** consultada y los botones para navegar semana a semana.
    *   La **lista de tareas** muestra todas las citas de esa semana. El color de la tarjeta indica si la tarea está pendiente o completada.
3.  **Añadir Tarea:** Pulsa el botón flotante (FAB) "+" para abrir el formulario. Selecciona la fecha y, si lo deseas, la hora específica.

## 👤 Desarrollador

*   **Iván Almagro**

---
*Nota: Esta aplicación está configurada para mostrarse siempre en modo claro para mantener la consistencia visual y de marca.*
