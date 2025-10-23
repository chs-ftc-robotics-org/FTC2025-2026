package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.util.ElapsedTime;

@SuppressWarnings("unused")
@Autonomous(name = "Launcher Test")
public class OpLauncherTest extends LinearOpMode {
    @Override
    public void runOpMode() {
        DcMotor motorA = hardwareMap.get(DcMotor.class, "ctl/motor0");
        DcMotor motorB = hardwareMap.get(DcMotor.class, "ctl/motor1");
        ElapsedTime timer = new ElapsedTime();
        waitForStart();
        timer.reset();
        motorA.setPower(1.0);
        motorB.setPower(-1.0);
        while (opModeIsActive()) {
            if (timer.milliseconds() > 1000) {
                break;
            }
        }
        motorA.setPower(0.0);
        motorB.setPower(0.0);
    }
}