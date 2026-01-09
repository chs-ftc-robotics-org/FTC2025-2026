package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.Servo;
import com.qualcomm.robotcore.util.ElapsedTime;

@TeleOp(name = "Servo Test", group = "tests")
public class OpServoTest extends LinearOpMode {
    public void runOpMode() {
        Servo servo = hardwareMap.servo.get("ctl/servo1");
        waitForStart();
        while (opModeIsActive()) {
            if (gamepad1.x) {
                servo.setPosition(0.15);
            }
            if (gamepad1.y) {
                servo.setPosition(1.0);
            }
        }

    }
}
