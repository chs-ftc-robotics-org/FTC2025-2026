package org.firstinspires.ftc.teamcode;

import java.util.Arrays;
import java.util.function.Function;

public class Util {
    public static <T, U> U[] arrayMap(T[] arr, Function<T, U> f) {
        @SuppressWarnings("unchecked")
        U[] result = (U[]) Arrays.stream(arr).map(f).toArray(Object[]::new);
        return result;
    }

    public static double distance(double x1, double y1, double x2, double y2) {
        double x = x2 - x1;
        double y = y2 - y1;
        return magnitude(x, y);
    }

    public static double magnitude(double x, double y) {
        return Math.sqrt(x * x + y * y);
    }

    public static double clamp(double x, double min, double max) {
        return Math.max(min, Math.min(x, max));
    }

    public static double rem(double a, double b) {
        double result = a - Math.floor(a / b) * b;
        return result == b ? 0 : result;
    }
}
