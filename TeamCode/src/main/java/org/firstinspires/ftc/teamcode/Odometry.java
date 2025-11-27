package org.firstinspires.ftc.teamcode;

import com.qualcomm.hardware.gobilda.GoBildaPinpointDriver;
import com.qualcomm.robotcore.eventloop.opmode.OpMode;

import org.firstinspires.ftc.robotcore.external.Telemetry;
import org.firstinspires.ftc.robotcore.external.navigation.AngleUnit;
import org.firstinspires.ftc.robotcore.external.navigation.DistanceUnit;

public class Odometry {
    private final GoBildaPinpointDriver driver;
    private final Telemetry telemetry;

    public Odometry(OpMode opMode) {
        driver = opMode.hardwareMap.get(GoBildaPinpointDriver.class, "odometry");
        telemetry = opMode.telemetry;
        telemetry.addLine("Odometry initialized");
        telemetry.update();
    }

    public void reset() {
        driver.resetPosAndIMU();
    }

    public void update() {
        driver.update();
    }

    // These two are swapped because someone put it in the wrong direction
    public double posX() {
        return driver.getPosY(DistanceUnit.INCH);
    }

    public double posY() {

        return driver.getPosX(DistanceUnit.INCH);
    }

    public double angle() {
        return driver.getHeading(AngleUnit.DEGREES);
    }

    public void log() {
        telemetry.addData("Position", "(%f, %f)", posX(), posY());
        telemetry.addData("Orientation", angle());
    }
}
