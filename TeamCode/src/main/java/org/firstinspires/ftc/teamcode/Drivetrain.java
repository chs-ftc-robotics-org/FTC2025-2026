package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorSimple;
import com.qualcomm.robotcore.hardware.HardwareMap;

import java.util.List;

public class Drivetrain {
    private final DcMotor frontLeft;
    private final DcMotor frontRight;
    private final DcMotor backLeft;
    private final DcMotor backRight;
    private final List<DcMotor> motors;

    private double factor = 1.0;

    private boolean rotateControls = false;

    public Drivetrain(OpMode opMode) {
        HardwareMap map = opMode.hardwareMap;
        frontLeft = map.dcMotor.get("drive/fl");
        frontRight = map.dcMotor.get("drive/fr");
        backLeft = map.dcMotor.get("drive/bl");
        backRight = map.dcMotor.get("drive/br");
        motors = List.of(frontLeft, frontRight, backLeft, backRight);

        frontLeft.setDirection(DcMotorSimple.Direction.FORWARD);
        frontRight.setDirection(DcMotorSimple.Direction.REVERSE);
        backLeft.setDirection(DcMotorSimple.Direction.FORWARD);
        backRight.setDirection(DcMotorSimple.Direction.REVERSE);

        //motors.forEach(m -> m.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE));

        opMode.telemetry.addLine("Drivetrain initialized");
        opMode.telemetry.update();
    }


    /**
     * @param x Left (-) and right (+) strafe
     * @param y Forwards (+) and backwards (-) movement
     * @param theta Rotate (+ for ccw)
     */
    public void move(double x, double y, double theta) {
        double r = rotateControls ? -1 : 1;
        double k = factor;

        double a = (-x + y) * r - theta;
        double b =  (x + y) * r + theta;
        double c =  (x + y) * r - theta;
        double d = (-x + y) * r + theta;

        setPowerSafe(frontLeft, a * k);
        setPowerSafe(frontRight, b * k);
        setPowerSafe(backLeft, c * k);
        setPowerSafe(backRight, d * k);
    }

    public void setFactor(double k) {
        factor = k;
    }

    public void stop() {
        move(0.0, 0.0, 0.0);
    }

    public void rotateControls() {
        rotateControls = !rotateControls;
    }

    private static void setPowerSafe(DcMotor m, double power) {
        m.setPower(power);

//        final double SLEW_RATE = 0.2;
//        double current = m.getPower();
//        double diff = power - current;
//        double limited = Util.clamp(diff, -SLEW_RATE, SLEW_RATE);
//        m.setPower(power + limited);
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
