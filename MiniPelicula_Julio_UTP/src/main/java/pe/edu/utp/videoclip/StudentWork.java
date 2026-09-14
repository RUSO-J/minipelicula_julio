package pe.edu.utp.videoclip;

/**
 * Trabajo del estudiante.
 *
 * La idea es que el nivel del audio controle la selección de imágenes
 * y la intensidad de los efectos aplicados a cada frame.
 */
public class StudentWork {

    /**
     * Calcula el nivel RMS del bloque de audio y lo normaliza entre 0 y 1.
     */
    public static double calculateAudioLevel(short[] samples, int start, int end) {
        if (samples == null || samples.length == 0) {
            return 0.0;
        }

        start = Math.max(0, start);
        end = Math.min(samples.length, end);

        if (start >= end) {
            return 0.0;
        }

        double sumSquares = 0.0;

        for (int i = start; i < end; i++) {
            double normalized = samples[i] / 32768.0;
            sumSquares += normalized * normalized;
        }

        double rms = Math.sqrt(sumSquares / (end - start));

        // Ganancia para que las diferencias del audio sean más visibles.
        return Math.min(1.0, rms * 3.0);
    }

    /**
     * Las imágenes se muestran en secuencia durante toda la película.
     * Cuando el audio es fuerte, se adelanta un frame de imagen para
     * reforzar la sensación de cambio.
     */
    public static int chooseImageIndex(
            double level,
            int frameNumber,
            int totalFrames,
            int imageCount) {

        if (imageCount <= 1) {
            return 0;
        }

        if (totalFrames <= 0) {
            return 0;
        }

        // Reparte todos los frames entre todas las imágenes.
        int index = (int) ((long) frameNumber * imageCount / totalFrames);
        index = Math.min(imageCount - 1, index);

        // Con audio fuerte se produce un cambio ligeramente más rápido.
        if (level >= 0.80 && frameNumber % 3 == 0) {
            index = (index + 1) % imageCount;
        }

        return index;
    }

    /**
     * Efectos animados:
     * - rotación: efecto geométrico
     * - brillo
     * - blur: convolución
     * - sharpen: convolución
     * - sobel + invert: matriz/convolución más intensa
     */
    public static MatrixImage applyEffects(
            MatrixImage base,
            double level,
            int frameNumber,
            int totalFrames) {

        // Rotación suave y variable en el tiempo.
        double angle = Math.sin(frameNumber * 0.18) * (3.0 + level * 12.0);
        MatrixImage result = base.rotate(angle);

        if (level < 0.25) {
            // Audio bajo: movimiento suave + brillo.
            result = result.brighten(1.05 + level * 0.8);

        } else if (level < 0.50) {
            // Audio medio-bajo: desenfoque por convolución.
            result = result.blur();

        } else if (level < 0.75) {
            // Audio medio-alto: enfoque por convolución.
            result = result.sharpen();

        } else {
            // Audio alto: detección de bordes + inversión.
            result = result.sobel().invert();
        }

        return result;
    }
}
