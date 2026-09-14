# Mini Película: Audio + Imágenes + Threads

**Integrante:** Julio
**Curso:** Algoritmos y Estructuras de Datos — UTP

## Descripción del proyecto

Videoclip generativo en Java que combina audio (`short[]`) e imágenes (`int[][]`)
para producir una animación cuadro por cuadro (frame por frame), donde la
selección de imagen y los efectos visuales aplicados dependen del nivel de
energía del audio en cada instante. El resultado se genera tanto en modo
serial como en paralelo (con `ExecutorService`), comparando tiempos de
ejecución entre ambos.

## Material utilizado

- **Audio:** `edicion_pelea.wav` (12 s de duración)
- **Imágenes:** 10 imágenes extraídas como fotogramas clave de un video
  propio, colocadas en `input/images/`
- **Configuración:** `fps=12`, `durationSeconds=12` → 144 frames totales

## Qué se completó en `StudentWork.java`

### TODO 1 — `calculateAudioLevel`
Se calcula el nivel RMS (Root Mean Square) del segmento de audio
correspondiente a cada frame, normalizado entre 0 y 1, con una ganancia
de 3.0 para que las diferencias de volumen se noten más en el resultado
visual.

### TODO 2 — `chooseImageIndex`
La imagen se elige repartiendo los frames entre todas las imágenes según
el momento de la película (tiempo), pero si el audio está muy fuerte
(nivel ≥ 0.80), el programa "adelanta" un salto extra a la siguiente
imagen, reforzando la sensación de cambio en los golpes de audio.

### TODO 3 — `applyEffects`
Se aplican 4 estados visuales según el nivel de audio, además de una
rotación geométrica constante que varía con el tiempo y el nivel:

| Nivel de audio | Efecto |
|---|---|
| Bajo (< 0.25) | Brillo suave |
| Medio-bajo (0.25–0.50) | Blur (convolución) |
| Medio-alto (0.50–0.75) | Sharpen (convolución) |
| Alto (≥ 0.75) | Sobel + inversión de colores |

## Resultados: Serial vs Paralelo

| Modo | Threads | Tiempo | Speedup |
|---|---|---|---|
| SERIAL | 1 | 6.014 s | — |
| PARALLEL | 2 | 3.895 s | 1.554x |
| PARALLEL | 4 | 2.108 s | 2.853x |

**Observación:** más threads sí reduce el tiempo, pero no de forma
perfectamente proporcional — de 2 a 4 threads el tiempo no se redujo
exactamente a la mitad, debido al overhead de gestionar más hilos y a
que la cantidad de núcleos reales de la CPU limita la ganancia.

## Cómo se relacionó un bloque de audio con un frame

El arreglo completo de muestras de audio (`short[]`) se dividió en
segmentos `[start, end)`, uno por cada frame de video, según el momento
que le corresponde en el tiempo total de la película. Ese segmento se
pasa a `calculateAudioLevel` para obtener el nivel de energía de ese
instante específico.

## Entregables

- `proyecto/` — código fuente completo
- `videoclip_serial.mp4` — video generado en modo serial
- `videoclip_parallel.mp4` — video generado en modo paralelo (4 threads)
- `output/metrics.csv` — tiempos registrados
- `capturas/` — evidencia de ejecución
