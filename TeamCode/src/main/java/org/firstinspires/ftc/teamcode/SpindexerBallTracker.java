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
        if (spindexerIdx % 2 != 0) throw new RuntimeException("AddBall Index Incorrect");

        int ballSlot = spindexerIdx;
        if (spindexerIdx == 4) ballSlot = 1;

        balls[ballSlot] = c;
    }

    public void removeBall(int spindexerIdx) {
        if (spindexerIdx % 2 == 0) throw new RuntimeException("RemoveBall Index Incorrect");

        int ballSlot = spindexerIdx;
        if (spindexerIdx == 3) ballSlot = 0;
        else if (spindexerIdx == 5) ballSlot = 2;

        balls[ballSlot] = ColorSensorDetection.EMPTY;
    }

    public String getCurrentBalls() {
        return balls[0].name() + ", " + balls[1].name() + ", " + balls[2].name();
    }
}
