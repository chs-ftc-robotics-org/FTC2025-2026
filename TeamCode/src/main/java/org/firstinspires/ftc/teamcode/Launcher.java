package org.firstinspires.ftc.teamcode;

import com.qualcomm.hardware.rev.RevColorSensorV3;
import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.hardware.ColorSensor;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorEx;
import com.qualcomm.robotcore.hardware.DcMotorSimple;
import com.qualcomm.robotcore.hardware.PIDFCoefficients;
import com.qualcomm.robotcore.hardware.Servo;

public class Launcher {
    private final DcMotorEx motor;
    private final Servo spin;
    private final Servo lift;
    private final Servo indicator;
    private final RevColorSensorV3 colorSensor;

    private final static double TARGET_VELOCITY = 1630;

    private final OpMode opMode;

    public enum FeedPosition {
        FULL(0.315),
        IDLE(0.2),
        HALF(0.298);

        private final double raw;

        FeedPosition(double raw) {
            this.raw = raw;
        }
    }

    public static final double FEED_POSITION_FULL = 0.315, FEED_POSITION_IDLE = 0.2, FEED_POSITION_HALF = 0.298;
    public static final double LIFT_POSITION_UP = 0.4, LIFT_POSITION_DOWN = 0.7;
    public static final double FLYWHEEL_POWER_NEAR = 0.78;
    public static final double FLYWHEEL_POWER_FAR = 1.0;

    public static final double SPIN_OFFSET = 0.05;

    public Launcher(OpMode opMode) {
        this.opMode = opMode;
        motor = opMode.hardwareMap.get(DcMotorEx.class, "launcher/motor");
        spin = opMode.hardwareMap.get(Servo.class, "launcher/spin");
        lift = opMode.hardwareMap.get(Servo.class, "launcher/lift");
        indicator = opMode.hardwareMap.get(Servo.class, "launcher/indicator");
        colorSensor = opMode.hardwareMap.get(RevColorSensorV3.class, "color");

        motor.setDirection(DcMotorSimple.Direction.REVERSE);
        //motor.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
        motor.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        PIDFCoefficients baseline = new PIDFCoefficients(42, 0, 0, 12.247);
        motor.setPIDFCoefficients(DcMotor.RunMode.RUN_USING_ENCODER, baseline);

        liftDown();
        spinSetIndex(0);

        // raiseIdle();
        // setFeedPosition(FeedPosition.IDLE);
    }

    public void stopFlywheel() {
        // motor.setVelocity(0);
        motor.setPower(0);
    }

    public void reverseFlywheel() {
        // motor.setVelocity(-1000);
        motor.setPower(-0.8);
    }

    public void startFlywheel(double power) {
        // motor.setVelocity(TARGET_VELOCITY);
        motor.setPower(power);
    }

    public boolean flywheelReady() {
        opMode.telemetry.addData("Velocity", motor.getVelocity());
        opMode.telemetry.addData("Error", TARGET_VELOCITY - motor.getVelocity());
        return Math.abs(motor.getVelocity() - TARGET_VELOCITY) < 60;
    }

//    public void setFeedPosition(FeedPosition pos) {
//        feed.setPosition(pos.raw);
//    }
//
//    public void feedIdle() {
//        setFeedPosition(FeedPosition.IDLE);
//    }
//
//    public void feedPushFull() {
//        setFeedPosition(FeedPosition.FULL);
//    }
//
//    public void feedPushHalf() {
//        setFeedPosition(FeedPosition.HALF);}
//
//    public boolean feedIsAtPosition(FeedPosition pos) {
//        return Math.abs(feed.getPosition() - pos.raw) < 0.01;
//    }

    private boolean servoIsAtPos(Servo servo, double pos) {
        return Math.abs(servo.getPosition() - pos) < 0.01;
    }

//    public boolean feedIsIdle() {
//        return servoIsAtPos(feed, FEED_POSITION_IDLE);
//    }
//
//    public boolean feedIsFullPush() {
//        return servoIsAtPos(feed, FEED_POSITION_FULL);
//    }
//
//    public boolean feedIsHalfPush() {
//        return servoIsAtPos(feed, FEED_POSITION_HALF);
//    }
//
//    public void raiseIdle() {
//        raise.setPosition(RAISE_POSITION_IDLE);
//    }
//
//    public void raiseActivate() {
//        raise.setPosition(RAISE_POSITION_ACTIVE);
//    }
//
//    public boolean raiseIsActive() {
//        return servoIsAtPos(feed, RAISE_POSITION_ACTIVE);
//    }
//
//    public boolean raiseIsIdle() {
//        return servoIsAtPos(feed, RAISE_POSITION_IDLE);
//    }

    private Task servoToPosition(Servo servo, double position) {
        Task check = Task.until(() -> servoIsAtPos(servo, position));
        return Task.of(() -> servo.setPosition(position), check::run);
    }

//    public Task feedToPosition(double position) {
//        return servoToPosition(this.feed, position);
//    }
//
//    public Task raiseToPosition(double position) {
//        return servoToPosition(this.raise, position);
//    }

    public void liftUp() {
        lift.setPosition(LIFT_POSITION_UP);
    }

    public void liftDown() {
        lift.setPosition(LIFT_POSITION_DOWN);
    }

    private double spinPosition;
    public void spinSetPosition(double position) {
        position = Util.clamp(position, 0, 1);
        spin.setPosition(position);
        spinPosition = position;
    }

    public void spinRotate(double x) {
        spinSetPosition(spinPosition + x);
    }

    public double spinGetPosition() {
        return spin.getPosition();
    }


    private final static double[] spinPositions = {
         0.0450,
         0.0789,
         0.1134,
         0.1500,
         0.1850,
         0.2239,
    };

    private int spinIndex;
    private void spinSetIndex(int n) {
        spinIndex = n;

        spinSetPosition(spinPositions[n]);
    }

    public void spinAddIndex(int i) {
        i = i % 6;
        int n = (spinIndex + i + 6) % 6;
        spinSetIndex(n);
    }

    public void displayStatus() {
        double v = motor.getVelocity();
        if (v <= 0) {
            indicator.setPosition(0); // Off
        }
        else if (v < TARGET_VELOCITY - 30) {
            indicator.setPosition(0.333); // Orange
        }
        else {
            indicator.setPosition(0.5); // Green
        }
    }

    public RevColorSensorV3 getColorSensor() {
        return colorSensor;
    }

    enum Detection {
        PURPLE,
        GREEN,
        EMPTY,
    }
}

/*
Launcher LED:
Yellow: Revving
Blue: Ready

Spindexer LED:
Red: No ball
Green: Green ball ready to launch
Purple: Green but Purple ball
 */