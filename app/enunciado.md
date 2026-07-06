# Desafío de Reacción y Atención
---
Desarrollar una aplicación Android que implemente un juego de reflejos y atención,
evaluando la capacidad del usuario para reaccionar ante estímulos visuales dentro de
un tiempo determinado. El sistema registra resultados, tiempos de reacción y
estadísticas del jugador.
La aplicación presenta estímulos visuales (pueden ser aleatoriamente palabras,
números o colores o por niveles de un mismo estilo) que el usuario debe responder
correctamente en un tiempo determinado.
En el inicio puede configurar el modo de dificultad, siendo posible elegir
“entrenamiento” (este modo no suma puntos), **“fácil”**, **“medio”** y **“difícil”**.
También podrá configurar cantidad de iteraciones para pasar de nivel (por defecto
20).
También podrá definir el tiempo máximo de reacción (por defecto 20 segundos en
modo fácil, 15 segundos en medio y 10 segundos en difícil). Los tiempos de reacción
nunca pueden superar los 30 segundos.
Debe mostrar estadísticas finales y almacenar los mejores resultados obtenidos por
jugador.
Además, debe brindar la posibilidad de volver a iniciar cuando pierda o cuando
supere todos los niveles del juego

## Restricciones
* El desarrollo individual.
* La aplicación no se conectará a internet.
* La persistencia será local.

## Agregados Opcionales
* Incremento dinámico de dificultad
* Incorporación de sonidos.
* Modo reacción inversa.

**Nota**: El modo reacción inversa es una variante del juego en la que el estímulo NO siempre debe generar una acción, y en algunos casos la respuesta correcta es NO responder; por ejemplo, al aparecer cierto estímulo **NO** debería generar una reacción.

Algunos ejemplos:

| Caso | Regla                                          | Estímulo    | Acción           | Respuesta  |
|------|------------------------------------------------|-------------|------------------|------------|
| 1    | Reaccionar ante todos los colores excepto ROJO | Color Verde | Genera evento    | Correcto   |
| 2    | No reaccionar ante ROJO                        | Color Rojo  | Genera evento    | Incorrecto |
| 3    | No reaccionar ante ROJO                        | Color Rojo  | No genera evento | Correcto   |
| 4    | Reaccionar ante todos los colores excepto ROJO | Color Azul  | No genera evento | Incorrecto |
| 5    | No reaccionar ante **Número Primo**            | 127         | No genera evento | Correcto   |

