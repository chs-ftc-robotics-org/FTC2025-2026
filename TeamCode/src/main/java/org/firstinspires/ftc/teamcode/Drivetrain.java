package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorSimple;
import com.qualcomm.robotcore.hardware.HardwareMap;

public class Drivetrain {
    private final DcMotor frontLeft;
    private final DcMotor frontRight;
    private final DcMotor backLeft;
    private final DcMotor backRight;

    private double factor = 1.0;


    public Drivetrain(OpMode opMode) {
        HardwareMap map = opMode.hardwareMap;
        frontLeft = map.dcMotor.get("drive/fl");
        frontRight = map.dcMotor.get("drive/fr");
        backLeft = map.dcMotor.get("drive/bl");
        backRight = map.dcMotor.get("drive/br");

        frontLeft.setDirection(DcMotorSimple.Direction.FORWARD);
        frontRight.setDirection(DcMotorSimple.Direction.REVERSE);
        backLeft.setDirection(DcMotorSimple.Direction.FORWARD);
        backRight.setDirection(DcMotorSimple.Direction.REVERSE);

        opMode.telemetry.addLine("Drivetrain initialized");
        opMode.telemetry.update();
    }


    /**
     * @param x Left (-) and right (+) strafe
     * @param y Forwards (+) and backwards (-) movement
     * @param theta Rotate (+ for ccw)
     */
    public void move(double x, double y, double theta) {
        double a = -x + y - theta;
        double b =  x + y + theta;
        double c =  x + y - theta;
        double d = -x + y + theta;
        double k = factor;

        frontLeft.setPower(a * k);
        frontRight.setPower(b * k);
        backLeft.setPower(c * k);
        backRight.setPower(d * k);
    }

    public void setFactor(double k) {
        factor = k;
    }

    public void stop() {
        move(0.0, 0.0, 0.0);
    }

    @TeleOp(name = "Drivetrain Test", group = "tests")
    public static class Test extends LinearOpMode {
        @Override
        public void runOpMode() {
            Drivetrain drivetrain = new Drivetrain(this);
            drivetrain.setFactor(0.5);

            waitForStart();
            while (opModeIsActive()) {
                drivetrain.move(gamepad1.right_stick_x, gamepad1.right_stick_y, gamepad1.left_stick_x);
            }

            drivetrain.stop();
        }
    }
}
