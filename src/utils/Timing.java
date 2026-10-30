package utils;

// Durata del frame corrente, espressa come fattore rispetto a 1/60 s
// (1 a 60 Hz, ~0.42 a 144 Hz, 2 se un frame a 60 Hz è stato saltato).
// Tutte le costanti di gioco (velocità, gravità, contatori) sono tarate "per 1/60 s":
// moltiplicandole per scale() valgono a qualsiasi frequenza dello schermo.
public final class Timing {
    private static double scale = 1;

    private Timing() {
    }

    public static double scale() {
        return scale;
    }

    public static void setFrameTime(double seconds) {
        scale = seconds * 60;
    }
}
