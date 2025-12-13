package org.firstinspires.ftc.teamcode;

import com.qualcomm.hardware.gobilda.GoBildaPinpointDriver;
import com.qualcomm.robotcore.eventloop.opmode.OpMode;

import org.firstinspires.ftc.robotcore.external.Telemetry;
import org.firstinspires.ftc.robotcore.external.navigation.AngleUnit;
import org.firstinspires.ftc.robotcore.external.navigation.DistanceUnit;
import org.firstinspires.ftc.robotcore.external.navigation.UnnormalizedAngleUnit;

public class Odometry {
    private final GoBildaPinpointDriver driver;
    private final Telemetry telemetry;

    private double startingDegrees;


    public static final DistanceUnit UNIT = DistanceUnit.INCH;

    public Odometry(OpMode opMode) {
        driver = opMode.hardwareMap.get(GoBildaPinpointDriver.class, "odometry");
        telemetry = opMode.telemetry;
        telemetry.addLine("Odometry initialized");
        telemetry.update();
    }

    public void resetPos(double x, double y) {
        driver.setPosX(y, DistanceUnit.INCH);
        driver.setPosY(x, DistanceUnit.INCH);
    }

    public void reset(double x, double y, double direction) {
        driver.resetPosAndIMU();
        startingDegrees = direction;
        resetPos(x, y);
    }

    public void update() {
        driver.update();
    }

    // These two are swapped because someone put it in the wrong direction
    public double posX() {
        return driver.getPosY(UNIT);
        // return driver.getEncoderY();
    }

    public double posY() {
        return driver.getPosX(UNIT);
        // return driver.getEncoderX();
    }

    public double directionRaw() {
        double rawAngle = driver.getHeading(AngleUnit.DEGREES);
        return rawAngle - startingDegrees;
    }

    public double directionNormalized() {
        double raw = directionRaw();
        double result = Util.rem(raw, 360);
        return result > 180 ? result - 360 : result;
    }

    public void log() {
        telemetry.addData("Position", "(%f, %f)", posX(), posY());
        telemetry.addData("Orientation", directionNormalized());
    }
}
