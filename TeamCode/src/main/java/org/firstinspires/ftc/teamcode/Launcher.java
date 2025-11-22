package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.HardwareMap;
import com.qualcomm.robotcore.hardware.Servo;

public class Launcher {
    private final DcMotor upper;
    private final DcMotor lower;

    /*
     * 0 => high (prep)
     * 1 => low (push)
     */
    private final Servo inputFeed;

    public Launcher(OpMode opMode) {
        HardwareMap map = opMode.hardwareMap;
        upper = map.dcMotor.get("launcher/upper");
        lower = map.dcMotor.get("launcher/lower");
        inputFeed = map.servo.get("launcher/feed");

        inputFeed.setPosition(0.15);

        opMode.telemetry.addLine("Launcher initialized");
        opMode.telemetry.update();
    }

    public void startSpinning() {
        // Lower is slightly more powerful to give it a bit of backspin
        upper.setPower(-0.9);
        lower.setPower(-0.8);
    }

    public void stopSpinning() {
        upper.setPower(0.0);
        lower.setPower(0.0);
    }

    public void pushFeed() {
        // Slightly above 0 so that it's not in contact with the launch wheels
        inputFeed.setPosition(0.2);
    }

    public void prepareFeed() {
        inputFeed.setPosition(0.85);
    }

    @TeleOp(name = "Launcher Test", group = "tests")
    public static class Test extends LinearOpMode {
        @Override
        public void runOpMode() {
            Launcher launcher = new Launcher(this);

            waitForStart();
            while (opModeIsActive()) {
                if (gamepad1.right_bumper) {
                    launcher.startSpinning();
                }
                else {
                    launcher.stopSpinning();
                }

                if (gamepad1.x) {
                    launcher.pushFeed();
                }
                if (gamepad1.y) {
                    launcher.prepareFeed();
                }
            }

            launcher.stopSpinning();
        }
    }
}
