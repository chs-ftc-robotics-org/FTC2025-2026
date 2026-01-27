package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorSimple;
import com.qualcomm.robotcore.hardware.TouchSensor;

public class Intake {
    private final DcMotor motor;

    private final TouchSensor fin;

    public Intake(OpMode opMode) {
        motor = opMode.hardwareMap.dcMotor.get("intake");
        motor.setDirection(DcMotorSimple.Direction.REVERSE);
        motor.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);

        fin = opMode.hardwareMap.get(TouchSensor.class, "touch");

        opMode.telemetry.addLine("Intake initialized");
    }

    public boolean finIsNotPressed() {
        return fin.isPressed();
    }

    public void start() {
        motor.setPower(-1.0);
    }

    public void reverse() {
        motor.setPower(1.0);
    }

    public void stop() {
        motor.setPower(0.0);
    }

    public double powerGet() {
        return motor.getPower();
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
