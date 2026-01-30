package org.firstinspires.ftc.teamcode;

import com.acmerobotics.dashboard.config.Config;
import com.qualcomm.hardware.rev.RevColorSensorV3;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorSimple;
import com.qualcomm.robotcore.hardware.DistanceSensor;
import com.qualcomm.robotcore.hardware.TouchSensor;

import org.firstinspires.ftc.robotcore.external.navigation.DistanceUnit;

@Config
public class Intake {
    public static double ALIGN_BIAS = 90;
    public static double ALIGN_TOLERANCE = 40;

    private final DcMotor motor;

    private final TouchSensor fin;

    private final DistanceSensor leftDist;
    private final DistanceSensor rightDist;

    private final RevColorSensorV3 colorSensor;

    ColorDetectionVector[] config = new ColorDetectionVector[] {
            new ColorDetectionVector(ColorSensorDetection.GREEN, 159, 0.61, 0.57, 43),
            new ColorDetectionVector(ColorSensorDetection.PURPLE, 210.46, 0.433, 0.59, 45),
            new ColorDetectionVector(ColorSensorDetection.EMPTY, 150.77, 0.386, 0.40, 60),
    };

    public Intake(OpMode opMode) {
        motor = opMode.hardwareMap.dcMotor.get("intake");
        motor.setDirection(DcMotorSimple.Direction.REVERSE);
        motor.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);

        fin = opMode.hardwareMap.get(TouchSensor.class, "touch");

        leftDist = opMode.hardwareMap.get(DistanceSensor.class, "intake/distance/left");
        rightDist = opMode.hardwareMap.get(DistanceSensor.class, "intake/distance/right");

        colorSensor = opMode.hardwareMap.get(RevColorSensorV3.class, "intake/color");

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

    public ColorSensorDetection colorSensorGetDetection() {
        Hsv color = colorSensorGetColors();
        double proximity = colorSensorGetProximity();
        return ColorDetectionVector.identify(config, color.h, color.s, color.v, proximity);
    }

    public Hsv colorSensorGetColors() {
        return new Rgb((short) colorSensor.red(), (short) colorSensor.green(), (short) colorSensor.blue()).toHsv();
    }

    public double colorSensorGetProximity() {
        return colorSensor.getDistance(DistanceUnit.MM);
    }

    public double getHorizontalBias() {
        double rawDistance = rightDist.getDistance(DistanceUnit.MM);
        if (rawDistance > 220) return 0; // Nothing in intake
        return rawDistance - ALIGN_BIAS;
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
