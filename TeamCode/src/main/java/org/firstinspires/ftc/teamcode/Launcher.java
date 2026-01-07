package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorEx;
import com.qualcomm.robotcore.hardware.DcMotorSimple;
import com.qualcomm.robotcore.hardware.Servo;

//public class Launcher {
//    public static final double BASELINE_POWER = 0.7;
//    public static final double IDEAL_RPM = 33.0;
//
//    private final DcMotorEx launcher;
//
//    /*
//     * 0 => high (prep)
//     * 1 => low (push)
//     */
//    private final Servo inputFeed;
//    private final Servo ledIndicator;
//
//    private final double RED = 0.277;
//    private final double ORANGE = 0.333;
//    private final double GREEN = 0.500;
//
//    public Launcher(OpMode opMode) {
//        HardwareMap map = opMode.hardwareMap;
//        launcher = map.get(DcMotorEx.class, "launcher/motor");
//        // launcherEnc = map.get(DcMotorEx.class, "launcher/motor");
//        // launcher = map.get(Servo.class, "test/pwm");
//        inputFeed = map.servo.get("launcher/feed");
//        ledIndicator = map.servo.get("launcher/indicator");
//
//        // inputFeed.setPosition(0.15);
//        // launcherEnc.setMode(DcMotorEx.RunMode.RUN_USING_ENCODER);
//        ledIndicator.setPosition(0.000);
//
//        opMode.telemetry.addLine("Launcher initialized");
//        opMode.telemetry.update();
//    }
//
//    public void start(double power) {
//        launcher.setPower(-power);
//        // launcher.setPosition(0.5 + power);
//    }
//
//    public void reverse() {
//        launcher.setPower(1.0);
//        // start(-1.0);
//    }
//
//    public void stop() {
//        launcher.setPower(0.0);
//        // start(0.0);
//    }
//
//    public double getRpm() {
//        double ticksPerRev = launcher.getMotorType().getTicksPerRev();
//        double ticksPerSecond = launcher.getVelocity();
//        return Math.abs((ticksPerSecond / ticksPerRev) * 60.0);
//    }
//
//    public void displayStatus() {
//        double rpm = getRpm();
//
//        if (rpm == 0.0) {
//            ledIndicator.setPosition(0.000);
//        } else if (rpm < IDEAL_RPM) {
//            ledIndicator.setPosition(ORANGE);
//        } else {
//            ledIndicator.setPosition(GREEN);
//        }
//    }
//
//    public static final double PUSH_LOCATION = 0.315;
//    public static final double PREP_LOCATION = 0.2;
//
//    public void pushFeed() {
//        // Slightly above 0 so that it's not in contact with the launch wheels
//        // inputFeed.setPosition(0.2);
//        inputFeed.setPosition(PUSH_LOCATION);
//    }
//
//    public void lockFeed() {
//        double t = 0.7;
//        inputFeed.setPosition(PUSH_LOCATION * t + PREP_LOCATION * (1 - t));
//    }
//
//    public void resetFeed() {
//        // inputFeed.setPosition(0.315);
//        inputFeed.setPosition(PREP_LOCATION);
//    }
//
//    public boolean isAtPosition(double pos) {
//        return inputFeed.getPosition() == pos;
//    }
//
//    @TeleOp(name = "Launcher Test", group = "tests")
//    public static class Test extends LinearOpMode {
//        @Override
//        public void runOpMode() {
//            Launcher launcher = new Launcher(this);
//
//            waitForStart();
//            while (opModeIsActive()) {
//                if (gamepad1.right_bumper) {
//                    launcher.start(0.9);
//                }
//                else {
//                    launcher.stop();
//                }
//
//                if (gamepad1.x) {
//                    launcher.pushFeed();
//                }
//                if (gamepad1.y) {
//                    launcher.resetFeed();
//                }
//            }
//
//            launcher.stop();
//        }
//    }
//}

public class Launcher {
    private final DcMotorEx motor;
    private final Servo feed;
    private final Servo raise;
    private final Servo indicator;

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

    public Launcher(OpMode opMode) {
        this.opMode = opMode;
        motor = opMode.hardwareMap.get(DcMotorEx.class, "launcher/motor");
        raise = opMode.hardwareMap.get(Servo.class, "launcher/push");
        feed = opMode.hardwareMap.get(Servo.class, "launcher/feed");
        indicator = opMode.hardwareMap.get(Servo.class, "launcher/indicator");

        motor.setDirection(DcMotorSimple.Direction.REVERSE);
        motor.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
//        motor.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
//        PIDFCoefficients baseline = new PIDFCoefficients(42, 0, 0, 12.247);
//        motor.setPIDFCoefficients(DcMotor.RunMode.RUN_USING_ENCODER, baseline);

        raiseIdle();
        setFeedPosition(FeedPosition.IDLE);
    }

    public void stopFlywheel() {
        // motor.setVelocity(0);
        motor.setPower(0);
    }

    public void reverseFlywheel() {
        // motor.setVelocity(-1000);
        motor.setPower(-1.0);
    }

    public void startFlywheel() {
        // motor.setVelocity(TARGET_VELOCITY);
        motor.setPower(1.0);
    }

    public boolean flywheelReady() {
        opMode.telemetry.addData("Velocity", motor.getVelocity());
        opMode.telemetry.addData("Error", TARGET_VELOCITY - motor.getVelocity());
        return Math.abs(motor.getVelocity() - TARGET_VELOCITY) < 60;
    }

    public void setFeedPosition(FeedPosition pos) {
        feed.setPosition(pos.raw);
    }

    public void feedIdle() {
        setFeedPosition(FeedPosition.IDLE);
    }

    public void feedPushFull() {
        setFeedPosition(FeedPosition.FULL);
    }

    public void feedPushHalf() {
        setFeedPosition(FeedPosition.HALF);}

    public boolean feedIsAtPosition(FeedPosition pos) {
        return Math.abs(feed.getPosition() - pos.raw) < 0.01;
    }

    public boolean feedIsIdle() {
        return feedIsAtPosition(FeedPosition.IDLE);
    }

    public boolean feedIsFullPush() {
        return feedIsAtPosition(FeedPosition.FULL);
    }

    public boolean feedIsHalfPush() {
        return feedIsAtPosition(FeedPosition.HALF);
    }

    public void raiseIdle() {
        raise.setPosition(0.7);
    }

    public void raiseActivate() {
        raise.setPosition(0.4);
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
}
