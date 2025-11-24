package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorEx;
import com.qualcomm.robotcore.hardware.DcMotorSimple;
import com.qualcomm.robotcore.hardware.HardwareMap;
import com.qualcomm.robotcore.hardware.Servo;

public class Launcher {
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
        // launcher = map.dcMotor.get("launcher/motor");
        launcher = map.get(DcMotorEx.class, "launcher/motor");
        inputFeed = map.servo.get("launcher/feed");
        ledIndicator = map.servo.get("launcher/indicator");

        // inputFeed.setPosition(0.15);
        launcher.setMode(DcMotorEx.RunMode.RUN_USING_ENCODER);
        ledIndicator.setPosition(0.000);

        opMode.telemetry.addLine("Launcher initialized");
        opMode.telemetry.update();
    }

    public void start(double power) {
        launcher.setPower(-power);
    }

    public void reverse() {
        launcher.setPower(1.0);
    }

    public void stop() {
        launcher.setPower(0.0);
    }

    public double calculateLED() {
        double ticksPerRev = launcher.getMotorType().getTicksPerRev();
        double ticksPerSecond = launcher.getVelocity();
        double motorRPM = Math.abs((ticksPerSecond / ticksPerRev) * 60.0);

        if (motorRPM == 0.0) {
            ledIndicator.setPosition(0.000);
        } else if (motorRPM < 42.5) {
            ledIndicator.setPosition(ORANGE);
        } else {
            ledIndicator.setPosition(GREEN);
        }

        return motorRPM;
    }

    public void pushFeed() {
        // Slightly above 0 so that it's not in contact with the launch wheels
        inputFeed.setPosition(0.2);
    }

    public void prepareFeed() {
        inputFeed.setPosition(0.315);
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
                    launcher.prepareFeed();
                }
            }

            launcher.stop();
        }
    }
}
