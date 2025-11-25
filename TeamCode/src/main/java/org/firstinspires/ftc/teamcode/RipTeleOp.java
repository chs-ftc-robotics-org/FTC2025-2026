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
        Lift lift = new Lift(this);

        waitForStart();

        boolean prevGamepad1A = false;

        int liftCurrPos = 0;
        int liftGoal = 0; // 0 to indicate no goal instead of Integer.MIN_VALUE or MAX_VALUE to avoid testing catastrophes

        while (opModeIsActive()) {
            drivetrain.move(gamepad1.left_stick_x, gamepad1.left_stick_y, gamepad1.right_stick_x);

            if (gamepad1.right_bumper || gamepad2.right_trigger > 0.1) {
                intake.start();
            } else if (gamepad1.left_bumper || gamepad2.left_trigger > 0.1) {
                intake.reverse();
            } else {
                intake.stop();
            }

            if (gamepad2.right_bumper) {
                launcher.start(gamepad2.a ? 1.0 : 0.9);
            } else if (gamepad2.left_bumper) {
                launcher.reverse();
            } else {
                launcher.stop();
            }

            if (gamepad2.x) {
                launcher.pushFeed();
            }
            if (gamepad2.y) {
                launcher.prepareFeed();
            }

            if (gamepad1.a && !prevGamepad1A) {
                drivetrain.rotateControls();
            }
            prevGamepad1A = gamepad1.a;

            if (gamepad1.y) {
                lift.up();
            } else if (gamepad1.x) {
                lift.down();
            } else {
                // Automatic lift movement will jitter without the if-statement below
                if (liftGoal == 0) {
                    lift.stop();
                }
            }

            liftCurrPos = (int) lift.getEncoderStatus();
            if (gamepad1.dpad_down) {
                liftGoal = -36;
                lift.down();
            } else if (gamepad1.dpad_up) {
                liftGoal = 131100;
                lift.up();
            }

            if (liftGoal != 0) {
                // Allow for 6000 tick stopping detection range for testing purposes
                if (liftGoal - 3000 <= liftCurrPos && liftGoal + 3000 >= liftCurrPos) {
                    lift.stop();
                    liftGoal = 0;
                } else if (liftCurrPos < liftGoal) {
                    lift.up();
                } else if (liftCurrPos > liftGoal) {
                    lift.down();
                }
            }

            telemetry.addData("Lift Position", liftCurrPos);
            telemetry.update();
        }
    }
}