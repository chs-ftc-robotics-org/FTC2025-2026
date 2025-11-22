package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotor;

@SuppressWarnings("unused")
@TeleOp(name = "Motor Test")
public class OpMotorTest extends LinearOpMode {
    @Override
    public void runOpMode() {
        DcMotor motor = hardwareMap.get(DcMotor.class, "ctl/motor0");
        waitForStart();
        telemetry.addData("Note", "Test motor power with left stick X of gamepad 1");
        telemetry.update();



        boolean pressed = false;
        int target = 0;

        while (opModeIsActive()) {
            // motor.setPower(gamepad1.left_stick_x);

            motor.setPower(0.7);

            if (!pressed && gamepad1.a) {
                motor.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
                motor.setTargetPosition(2000);
                motor.setMode(DcMotor.RunMode.RUN_TO_POSITION);
            }
            pressed = gamepad1.a;
        }
    }
}
