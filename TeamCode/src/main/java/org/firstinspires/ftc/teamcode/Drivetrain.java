package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorSimple;
import com.qualcomm.robotcore.hardware.HardwareMap;

public class Drivetrain {
    private final DcMotor frontLeft;
    private final DcMotor frontRight;
    private final DcMotor backLeft;
    private final DcMotor backRight;


    public Drivetrain(OpMode opMode) {
        HardwareMap map = opMode.hardwareMap;
        frontLeft = map.get(DcMotor.class, "ctl/motor0");
        frontRight = map.get(DcMotor.class, "ctl/motor1");
        backLeft = map.get(DcMotor.class, "ctl/motor2");
        backRight = map.get(DcMotor.class, "ctl/motor3");

        frontLeft.setDirection(DcMotorSimple.Direction.REVERSE);
        frontRight.setDirection(DcMotorSimple.Direction.FORWARD);
        backLeft.setDirection(DcMotorSimple.Direction.REVERSE);
        backRight.setDirection(DcMotorSimple.Direction.FORWARD);

        opMode.telemetry.addLine("Drivetrain initialized");
        opMode.telemetry.update();
    }


    /**
     * @param x Left (-) and right (+) strafe
     * @param y Forwards (+) and backwards (-) movement
     * @param theta Rotate (+ for ccw)
     */
    public void move(double x, double y, double theta) {
        double a =  x + y - theta;
        double b = -x + y + theta;
        double c = -x + y - theta;
        double d =  x + y + theta;

        frontLeft.setPower(a);
        frontRight.setPower(b);
        backLeft.setPower(c);
        backRight.setPower(d);
    }
}
