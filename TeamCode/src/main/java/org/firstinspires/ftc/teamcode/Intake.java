package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotor;

public class Intake {
    private final DcMotor motor;

    public Intake(OpMode opMode) {
        motor = opMode.hardwareMap.dcMotor.get("intake");
        motor.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);

        opMode.telemetry.addLine("Intake initialized");
    }

    public void start() {
        motor.setPower(0.9);
    }

    public void reverse() {
        motor.setPower(-0.9);
    }

    public void stop() {
        motor.setPower(0.0);
    }

    @TeleOp(name = "Intake Test", group = "tests")
    public static class Test extends LinearOpMode {
        @Override
        public void runOpMode() {
            Intake intake = new Intake(this);

            waitForStart();
            while (opModeIsActive()) {
                if (gamepad1.a) {
                    intake.start();
                }
                else if (gamepad1.b) {
                    intake.reverse();
                }
                else {
                    intake.stop();
                }
            }

            intake.stop();
        }
    }
}
