package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.hardware.DistanceSensor;

import org.firstinspires.ftc.robotcore.external.navigation.DistanceUnit;

@Autonomous
public class RipAutonomous extends LinearOpMode {
    @Override
    public void runOpMode() {
        Drivetrain drivetrain = new Drivetrain(this);
        DistanceSensor ds = hardwareMap.get(DistanceSensor.class, "sensors/distance");

        waitForStart();
        while (opModeIsActive()) {
            // if (ds.getDistance(DistanceUnit.CM) > 10) drivetrain.move(0, 0.2, 0);
            telemetry.addData("Distance Detected", "%.2f", ds.getDistance(DistanceUnit.CM));
            telemetry.update();
        }

    }
}
