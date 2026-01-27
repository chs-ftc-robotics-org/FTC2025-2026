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

    public static int min(int... nums) {
        return Arrays.stream(nums).reduce(Integer.MAX_VALUE, (a, b) -> Math.min(a, b));
    }

    public static double min(double... nums) {
        return Arrays.stream(nums).reduce(Double.POSITIVE_INFINITY, (a, b) -> Math.min(a, b));
    }

    public static int max(int... nums) {
        return Arrays.stream(nums).reduce(Integer.MIN_VALUE, (a, b) -> Math.max(a, b));
    }

    public static double max(double... nums) {
        return Arrays.stream(nums).reduce(Double.NEGATIVE_INFINITY, (a, b) -> Math.max(a, b));
    }

    public static double rem(double a, double b) {
        double result = a - Math.floor(a / b) * b;
        return result == b ? 0 : result;
    }

    public static double sq(double x) {
        return x * x;
    }
}

class Rgb {
    public short r;
    public short g;
    public short b;

    public Rgb(short red, short green, short blue) {
        r = red;
        g = green;
        b = blue;
    }

    public Hsv toHsv() {
        double r = this.r / 255.0;
        double g = this.g / 255.0;
        double b = this.b / 255.0;

        double max = Util.max(r, g, b);
        double min = Util.min(r, g, b);
        double diff = max - min;

        double h = 0.0;
        if (r == max)
            h = (60 * (g - b) / diff) % 360;
        else if (g == max)
            h = 60 * (b - r) / diff + 120;
        else if (b == max)
            h = 60 * (r - g) / diff + 240;

        double s = diff / max;
        double v = max;

        return new Hsv(h, s, v);
    }
}

class Hsv {
    public double h;
    public double s;
    public double v;

    public Hsv(double hue, double saturation, double value) {
        h = hue;
        s = saturation;
        v = value;
    }

    public Rgb toRgb() {
        double r = Util.clamp(Math.abs((h - 180) / 60) - 1, 0.0, 1.0);
        double g = Util.clamp(2 - Math.abs((h - 120) / 60), 0.0, 1.0);
        double b = Util.clamp(2 - Math.abs((h - 240) / 60), 0.0, 1.0);

        return new Rgb((short) (r * 255), (short) (g * 255), (short) (b * 255));
    }
}