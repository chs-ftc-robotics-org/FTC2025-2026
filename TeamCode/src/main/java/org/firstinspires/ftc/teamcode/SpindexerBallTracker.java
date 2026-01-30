package org.firstinspires.ftc.teamcode;

import java.util.Arrays;

public class SpindexerBallTracker {
    public final ColorSensorDetection[] balls = new ColorSensorDetection[3];

    public SpindexerBallTracker() {
        clear();
    }

    public void clear() {
        Arrays.fill(balls, ColorSensorDetection.EMPTY);
    }

    public void addBall(int spindexerIdx, ColorSensorDetection c) {
        // if (spindexerIdx % 2 != 0) throw new RuntimeException("AddBall Index Incorrect. Received " + spindexerIdx);

        balls[convertIndexToSlot(spindexerIdx)] = c;
    }

    public void removeBall(int spindexerIdx) {
        // if (spindexerIdx % 2 == 0) throw new RuntimeException("RemoveBall Index Incorrect. Received " + spindexerIdx);

        balls[convertIndexToSlot(spindexerIdx)] = ColorSensorDetection.EMPTY;
    }

    private int convertIndexToSlot(int spindexerIdx) {
        if (spindexerIdx == 0 || spindexerIdx == 3) return 0;
        if (spindexerIdx == 1 || spindexerIdx == 4) return 1;
        if (spindexerIdx == 2 || spindexerIdx == 5) return 2;

        return 0;
    }

    public String getCurrentBalls() {
        return balls[0].name() + ", " + balls[1].name() + ", " + balls[2].name();
    }
}
