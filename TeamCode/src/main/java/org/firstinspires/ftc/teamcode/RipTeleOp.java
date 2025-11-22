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
            drivetrain.move(gamepad1.left_stick_x, gamepad1.left_stick_y, gamepad1.right_stick_x);

            if (gamepad1.right_bumper || gamepad2.right_trigger > 0.1) {
                intake.start();
            }
            else if (gamepad1.left_bumper || gamepad2.left_trigger > 0.1) {
                intake.reverse();
            }
            else {
                intake.stop();
            }

            if (gamepad2.right_bumper) {
                launcher.start();
            }
            else if (gamepad2.left_bumper) {
                launcher.reverse();
            }
            else {
                launcher.stop();
            }

            if (gamepad2.x) {
                launcher.pushFeed();
            }
            if (gamepad2.y) {
                launcher.prepareFeed();
            }

            double launcherRPM = launcher.calculateLED();
            telemetry.addData("Motor RPM", "%.2f", launcherRPM);
            telemetry.update();
        }
    }

}
