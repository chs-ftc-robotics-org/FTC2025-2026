package org.firstinspires.ftc.teamcode;

public class ColorDetectionVector {
    private final double h;
    private final double s;
    private final double v;
    private final double p;
    private final ColorSensorDetection tag;

    public ColorDetectionVector(ColorSensorDetection tag, double hue, double saturation, double value, double proximity) {
        h = hue;
        s = saturation;
        v = value;
        p = proximity;
        this.tag = tag;
    }

    public static ColorSensorDetection identify(ColorDetectionVector[] config, double h, double s, double v, double p) {
        double closest = Double.POSITIVE_INFINITY;
        ColorSensorDetection result = ColorSensorDetection.EMPTY;
        for (ColorDetectionVector c : config) {
            double distanceSq = Util.sq(c.h - h) + Util.sq(c.s - s) + Util.sq(c.v - v) + Util.sq(c.p - p);
            if (distanceSq < closest) {
                closest = distanceSq;
                result = c.tag;
            }
        }
        return result;
    }
}

enum ColorSensorDetection {
    PURPLE,
    GREEN,
    EMPTY,
}