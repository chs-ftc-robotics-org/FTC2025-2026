package org.firstinspires.ftc.teamcode;

import com.qualcomm.hardware.rev.Rev9AxisImu;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;

import org.firstinspires.ftc.robotcore.external.navigation.YawPitchRollAngles;

@TeleOp(name = "Imu Test", group = "tests")
public class OpImuTest extends LinearOpMode {
    @Override
    public void runOpMode() {
        Rev9AxisImu imu = hardwareMap.get(Rev9AxisImu.class, "imu9");
        waitForStart();
        while (opModeIsActive()) {
            YawPitchRollAngles orientation = imu.getRobotYawPitchRollAngles();
            telemetry.addData("dbg", "%f, %f, %f", orientation.getYaw(), orientation.getPitch(), orientation.getRoll());
            telemetry.update();
        }
    }
}
