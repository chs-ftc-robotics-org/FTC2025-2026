package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;

@TeleOp(name = "RIP TeleOp")
public class RipTeleOp extends LinearOpMode {
    @Override
    public void runOpMode() {
        Drivetrain drivetrain = new Drivetrain(this);
        Intake intake = new Intake(this);
        Launcher launcher = new Launcher(this);

        waitForStart();
        while (opModeIsActive()) {
            drivetrain.move(gamepad1.right_stick_x, gamepad1.right_stick_y, gamepad1.left_stick_x);

            if (gamepad1.a) {
                intake.start();
            }
            else if (gamepad1.b) {
                intake.reverse();
            }
            else {
                intake.stop();
            }

            if (gamepad1.right_bumper) {
                launcher.startSpinning();
            }
            else {
                launcher.stopSpinning();
            }

            if (gamepad1.x) {
                launcher.pushFeed();
            }
            if (gamepad1.y) {
                launcher.prepareFeed();
            }
        }
    }

}
