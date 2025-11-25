package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorEx;
import com.qualcomm.robotcore.hardware.DcMotorSimple;
import com.qualcomm.robotcore.hardware.HardwareMap;

public class Lift {
    private final DcMotorEx left;
    // private final DcMotor frontRight;

    public Lift(OpMode opMode) {
        HardwareMap map = opMode.hardwareMap;
        left = map.get(DcMotorEx.class, "lift/left");
        // frontRight = map.dcMotor.get("drive/fr");

        left.setDirection(DcMotorSimple.Direction.FORWARD);
        left.setMode(DcMotorEx.RunMode.RUN_USING_ENCODER);
        // frontRight.setDirection(DcMotorSimple.Direction.REVERSE);

        opMode.telemetry.addLine("Lift initialized");
        opMode.telemetry.update();
    }

    public void up() {
        left.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        left.setPower(1.0);
    }

    public void goToTop() {
        left.setTargetPosition(131100);
        left.setMode(DcMotor.RunMode.RUN_TO_POSITION);
        left.setPower(1.0);
    }

    public void down() {
        left.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        left.setPower(-1.0);
    }

    public void goToBottom() {
        left.setTargetPosition(-36);
        left.setMode(DcMotor.RunMode.RUN_TO_POSITION);
        left.setPower(-1.0);
    }

    public void stop() {
        left.setPower(0.0);
    }

    public double getEncoderStatus() {
        return left.getCurrentPosition();
    }

//    @TeleOp(name = "Lift Test", group = "tests")
//    public static class Test extends LinearOpMode {
//        @Override
//        public void runOpMode() {
//            // make a Lift class test
//            Drivetrain drivetrain = new Drivetrain(this);
//            drivetrain.setFactor(0.5);
//
//            waitForStart();
//            while (opModeIsActive()) {
//                drivetrain.move(gamepad1.right_stick_x, gamepad1.right_stick_y, gamepad1.left_stick_x);
//            }
//
//            drivetrain.stop();
//        }
//    }
}