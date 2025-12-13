package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotorEx;
import com.qualcomm.robotcore.hardware.HardwareMap;
import com.qualcomm.robotcore.hardware.Servo;

public class Launcher {
    public static final double BASELINE_POWER = 0.73;
    public static final double IDEAL_RPM = 33.0;

    private final DcMotorEx launcher;

    /*
     * 0 => high (prep)
     * 1 => low (push)
     */
    private final Servo inputFeed;
    private final Servo ledIndicator;

    private final double RED = 0.277;
    private final double ORANGE = 0.333;
    private final double GREEN = 0.500;

    public Launcher(OpMode opMode) {
        HardwareMap map = opMode.hardwareMap;
        launcher = map.get(DcMotorEx.class, "launcher/motor");
        // launcherEnc = map.get(DcMotorEx.class, "launcher/motor");
        // launcher = map.get(Servo.class, "test/pwm");
        inputFeed = map.servo.get("launcher/feed");
        ledIndicator = map.servo.get("launcher/indicator");

        // inputFeed.setPosition(0.15);
        // launcherEnc.setMode(DcMotorEx.RunMode.RUN_USING_ENCODER);
        ledIndicator.setPosition(0.000);

        opMode.telemetry.addLine("Launcher initialized");
        opMode.telemetry.update();
    }

    public void start(double power) {
        launcher.setPower(-power);
        // launcher.setPosition(0.5 + power);
    }

    public void reverse() {
        launcher.setPower(1.0);
        // start(-1.0);
    }

    public void stop() {
        launcher.setPower(0.0);
        // start(0.0);
    }

    public double getRpm() {
        double ticksPerRev = launcher.getMotorType().getTicksPerRev();
        double ticksPerSecond = launcher.getVelocity();
        return Math.abs((ticksPerSecond / ticksPerRev) * 60.0);
    }

    public void displayStatus() {
        double rpm = getRpm();

        if (rpm == 0.0) {
            ledIndicator.setPosition(0.000);
        } else if (rpm < IDEAL_RPM) {
            ledIndicator.setPosition(ORANGE);
        } else {
            ledIndicator.setPosition(GREEN);
        }
    }

    public static final double PUSH_LOCATION = 0.315;
    public static final double PREP_LOCATION = 0.2;

    public void pushFeed() {
        // Slightly above 0 so that it's not in contact with the launch wheels
        // inputFeed.setPosition(0.2);
        inputFeed.setPosition(PUSH_LOCATION);
    }

    public void lockFeed() {
        double t = 0.7;
        inputFeed.setPosition(PUSH_LOCATION * t + PREP_LOCATION * (1 - t));
    }

    public void resetFeed() {
        // inputFeed.setPosition(0.315);
        inputFeed.setPosition(PREP_LOCATION);
    }

    public boolean isAtPosition(double pos) {
        return inputFeed.getPosition() == pos;
    }

    @TeleOp(name = "Launcher Test", group = "tests")
    public static class Test extends LinearOpMode {
        @Override
        public void runOpMode() {
            Launcher launcher = new Launcher(this);

            waitForStart();
            while (opModeIsActive()) {
                if (gamepad1.right_bumper) {
                    launcher.start(0.9);
                }
                else {
                    launcher.stop();
                }

                if (gamepad1.x) {
                    launcher.pushFeed();
                }
                if (gamepad1.y) {
                    launcher.resetFeed();
                }
            }

            launcher.stop();
        }
    }
}
