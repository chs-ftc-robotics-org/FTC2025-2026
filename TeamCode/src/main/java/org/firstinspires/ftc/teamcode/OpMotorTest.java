package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotor;

@TeleOp(name = "Motor Test")
public class OpMotorTest extends LinearOpMode {
    @Override
    public void runOpMode() {
        DcMotor motor = hardwareMap.get(DcMotor.class, "ctl/motor0");
        waitForStart();
        telemetry.addData("Note", "Test motor power with left stick X of gamepad 1");
        telemetry.update();
        while (opModeIsActive()) {
            motor.setPower(gamepad1.left_stick_x);
        }
    }
}
