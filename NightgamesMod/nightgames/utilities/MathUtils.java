package nightgames.utilities;

/**
 * MathUtils helpers.
 */
public class MathUtils {
    public static int clamp(int number, int min, int max) {
        return Math.min(Math.max(number, min), max);
    }

    public static double clamp(double number, double min, double max) {
        return Math.min(Math.max(number, min), max);
    }

    public static double clamp(double number) {
        return clamp(number, 0., 1.);
    }

    public static float clamp(float number, float min, float max) {
        return Math.min(Math.max(min, number), max);
    }

    public static float clamp(float number) {
        return clamp(number, 0.f, 1.f);
    }
}
