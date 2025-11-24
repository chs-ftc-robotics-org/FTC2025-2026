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
        
        boolean prevA = false;

        int leftCurrPos = 0;
        int leftGoal = 0;

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
                launcher.start(gamepad2.a ? 1.0 : 0.9);
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
            
            if (gamepad1.a && !prevA) {
                drivetrain.rotateControls();
            }
            prevA = gamepad1.a;

            if (gamepad1.x) {
                lift.up();
            } else if (gamepad1.y) {
                lift.down();
            } else {
                lift.stop();
            }

//            leftCurrPos = (int) lift.getEncoderStatus();
//            if (gamepad1.dpad_down) {
//                // lift.goToBottom();
//                if (leftGoal == 0) {
//                    leftGoal = -36;
//                    lift.down();
//                }
//            } else if (gamepad1.dpad_up) {
//                lift.goToTop();
//            }

            double launcherRPM = launcher.calculateLED();
            double liftEnc = lift.getEncoderStatus();

//            if (leftGoal != 0) {
//                // if ()
//            }

            telemetry.addData("Motor RPM", "%.2f", launcherRPM);
            telemetry.addData("Lift Position", "%.2f", liftEnc);
            telemetry.update();
        }
    }

}
