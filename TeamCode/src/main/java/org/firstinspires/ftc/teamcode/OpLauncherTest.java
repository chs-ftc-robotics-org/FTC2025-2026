package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.Servo;

@SuppressWarnings("unused")
// @TeleOp(name = "Launcher Test")
public class OpLauncherTest extends LinearOpMode {
    DcMotor lower;
    DcMotor upper;
    private Servo inputFeed;

    @Override
    public void runOpMode() {
        inputFeed = hardwareMap.servo.get("ctl/servo1");

        lower = hardwareMap.get(DcMotor.class, "ctl/motor0");
        upper = hardwareMap.get(DcMotor.class, "ctl/motor1");

        waitForStart();
        while (opModeIsActive()) {

            if (gamepad1.left_bumper) {
                launch();
            } else {
                stopEverything();
            }

            if (gamepad1.x) {
                inputFeed.setPosition(0.15);
            }
            if (gamepad1.y) {
                inputFeed.setPosition(1.0);
            }
        }
        lower.setPower(0.0);
        upper.setPower(0.0);
    }

    private void launch() {
        lower.setPower(-0.8);
        upper.setPower(-0.6);
    }

    private void stopEverything() {
        lower.setPower(0.0);
        upper.setPower(0.0);
    }
}