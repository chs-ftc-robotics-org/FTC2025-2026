package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorEx;
import com.qualcomm.robotcore.hardware.DcMotorSimple;
import com.qualcomm.robotcore.hardware.HardwareMap;

import java.util.List;

public class Lift {
    // Bottom of lift: 32537;
    // Top of lift: 82024;
    
    public static final int TOP_POSITION = 49521;
    public static final int BOTTOM_POSITION = 0;
    
    private final DcMotorEx left;
    private final DcMotorEx right;

    private final List<DcMotor> motors;
    
    // private final DcMotor frontRight;

    public Lift(OpMode opMode) {
        HardwareMap map = opMode.hardwareMap;
        left = map.get(DcMotorEx.class, "lift/left");
        right = map.get(DcMotorEx.class, "lift/right");
        motors = List.of(left, right);
        // frontRight = map.dcMotor.get("drive/fr");

        left.setDirection(DcMotorSimple.Direction.FORWARD);
        right.setDirection(DcMotorSimple.Direction.FORWARD);

        left.setMode(DcMotorEx.RunMode.STOP_AND_RESET_ENCODER);
        right.setMode(DcMotorEx.RunMode.STOP_AND_RESET_ENCODER);

        left.setMode(DcMotorEx.RunMode.RUN_USING_ENCODER);
        right.setMode(DcMotorEx.RunMode.RUN_USING_ENCODER);
        // frontRight.setDirection(DcMotorSimple.Direction.REVERSE);

        opMode.telemetry.addLine("Lift initialized");
        opMode.telemetry.update();
    }

    public void up() {
        motors.forEach(m -> {
            m.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
            m.setPower(1.0);
        });
    }

    public void goToTop() {
        motors.forEach(m -> {
            m.setTargetPosition(TOP_POSITION);
            m.setMode(DcMotor.RunMode.RUN_TO_POSITION);
            m.setPower(1.0);
        });
    }

    public void down() {
        motors.forEach(m -> {
            m.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
            m.setPower(-1.0);
        });
    }

    public void goToBottom() {
        motors.forEach(m -> {
            m.setTargetPosition(BOTTOM_POSITION);
            m.setMode(DcMotor.RunMode.RUN_TO_POSITION);
            m.setPower(-1.0);
        });
    }

    public void stop() {
        left.setPower(0.0);
        right.setPower(0.0);
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
