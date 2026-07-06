# Desafío de Reacción y Atención

Este proyecto es una aplicación Android diseñada para evaluar y mejorar los reflejos y la atención del usuario. El juego desafía a los jugadores a reaccionar ante diversos estímulos visuales dentro de un tiempo límite, registrando estadísticas y mejores puntuaciones.

## Características Principales

- **Estímulos Visuales:** El juego presenta estímulos aleatorios (palabras, números o colores) que el usuario debe responder correctamente.
- **Modos de Dificultad:**
  - **Entrenamiento:** Modo libre para práctica (no suma puntos).
  - **Fácil:** Tiempo máximo de reacción de 20 segundos (por defecto).
  - **Medio:** Tiempo máximo de reacción de 15 segundos (por defecto).
  - **Difícil:** Tiempo máximo de reacción de 10 segundos (por defecto).
- **Configuración Personalizable:**
  - Ajuste de la cantidad de iteraciones para pasar de nivel (por defecto 20).
  - Configuración del tiempo máximo de reacción (mínimo configurable, máximo 30 segundos).
- **Estadísticas y Récords:** Almacenamiento local de los mejores resultados obtenidos por cada jugador.
- **Flujo de Juego:** Posibilidad de reiniciar al perder o al completar todos los niveles.

## Restricciones

- **Desarrollo:** Individual.
- **Conectividad:** La aplicación funciona totalmente offline, sin acceso a internet.
- **Persistencia:** Almacenamiento de datos de forma local en el dispositivo.

## Agregados Opcionales (Bonus)

- Incremento dinámico de dificultad.
- Incorporación de sonidos.
- **Modo Reacción Inversa:** Variante donde ciertos estímulos no deben generar una acción (ej. reaccionar a todos los colores excepto el rojo).

## Detalles Técnicos

- **Lenguaje:** Kotlin
- **SDK Mínimo:** 26 (Android 8.0 Oreo)
- **SDK Objetivo:** 36 (Android 15)
- **UI:** Basada en Material Design Components.

## Requisitos de Instalación

1. Clonar este repositorio.
2. Abrir el proyecto en **Android Studio**.
3. Sincronizar el proyecto con los archivos de Gradle.
4. Ejecutar en un dispositivo físico o emulador con Android 8.0 o superior.

---
*Este proyecto fue desarrollado siguiendo las especificaciones del enunciado proporcionado en la materia Diseño y Desarrollo de Aplicaciones I.*
