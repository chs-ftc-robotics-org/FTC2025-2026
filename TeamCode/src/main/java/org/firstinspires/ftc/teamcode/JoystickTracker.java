package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.hardware.Gamepad;

public class JoystickTracker {
    private static final double THRESHOLD = 0.2;

    private final Gamepad gamepad;

    public JoystickTracker(Gamepad g) {
        gamepad = g;
    }

    private State leftX = State.ZERO;
    private State leftY = State.ZERO;
    private State rightX = State.ZERO;
    private State rightY = State.ZERO;

    public boolean leftXUpdated() {
        return leftX() != leftX;
    }

    public boolean leftYUpdated() {
        return leftY() != leftY;
    }

    public boolean rightXUpdated() {
        return rightX() != rightX;
    }

    public boolean rightYUpdated() {
        return rightY() != rightY;
    }

    public State leftX() {
        return State.fromValue(gamepad.left_stick_x);
    }

    public State leftY() {
        return State.fromValue(gamepad.left_stick_y);
    }

    public State rightX() {
        return State.fromValue(gamepad.right_stick_x);
    }

    public State rightY() {
        return State.fromValue(gamepad.right_stick_y);
    }

    public void update() {
        leftX = leftX();
        leftY = leftY();
        rightX = rightX();
        rightY = rightY();
    }

    public enum State {
        NEGATIVE,
        ZERO,
        POSITIVE;

        public static State fromValue(double x) {
            if (x > THRESHOLD) return POSITIVE;
            if (x < -THRESHOLD) return NEGATIVE;
            return ZERO;
        }
    }
}
