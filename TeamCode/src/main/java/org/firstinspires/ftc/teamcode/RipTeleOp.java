package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.util.ElapsedTime;

import java.util.Optional;
import java.util.concurrent.TimeUnit;

@TeleOp(name = "RIP TeleOp")
public class RipTeleOp extends LinearOpMode {
    @Override
    public void runOpMode() {
        Drivetrain drivetrain = new Drivetrain(this);
        Intake intake = new Intake(this);
        Launcher launcher = new Launcher(this);
        Lift lift = new Lift(this);
        Odometry odometry = new Odometry(this);
        odometry.reset(24, 0, 0);

        waitForStart();

        boolean prevGamepad1A = false;

        int liftCurrPos = 0;
        Optional<Integer> liftGoal = Optional.empty();

        ElapsedTime gamepad2BTimer = new ElapsedTime();
        boolean gamepad2BActivated = false;

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
                launcher.start(gamepad2.a ? 1.0 : Launcher.BASELINE_POWER);
            } else if (gamepad2.left_bumper) {
                launcher.reverse();
            } else {
                launcher.stop();
            }

            if (gamepad2.y) {
                launcher.pushFeed();
            }
            else if (!gamepad2BActivated) {
                launcher.resetFeed();
            }

            if (gamepad2.b && !gamepad2BActivated) {
                gamepad2BActivated = true;
                gamepad2BTimer.reset();
                launcher.lockFeed();
            }

            if (gamepad2BActivated) {
                telemetry.addLine("2B Activated");
                if (gamepad2BTimer.time(TimeUnit.MILLISECONDS) >= 2250 && launcher.isAtPosition(Launcher.PUSH_LOCATION)) {
                    launcher.resetFeed();
                    gamepad2BActivated = false;
                } else if (gamepad2BTimer.time(TimeUnit.MILLISECONDS) >= 1500) {
                    launcher.pushFeed();
                }
            }

            if (gamepad1.a && !prevGamepad1A) {
                drivetrain.rotateControls();
            }
            prevGamepad1A = gamepad1.a;

            boolean liftManual = false;
            if (gamepad1.y) {
                lift.up();
                liftManual = true;
            } else if (gamepad1.x) {
                lift.down();
                liftManual = true;
            } else {
                // Automatic lift movement will jitter without the if-statement below
                if (!liftGoal.isPresent()) {
                    lift.stop();
                }
            }

            liftCurrPos = (int) lift.getEncoderStatus();
            if (gamepad1.dpad_down) {
                liftGoal = Optional.of(Lift.BOTTOM_POSITION);
                lift.down();
            } else if (gamepad1.dpad_up) {
                liftGoal = Optional.of(Lift.TOP_POSITION);
                lift.up();
            }

            if (liftManual) liftGoal = Optional.empty();

            if (liftGoal.isPresent()) {
                int goal = liftGoal.get();
                // Allow for 6000 tick stopping detection range for testing purposes
                // if (goal - 3000 <= liftCurrPos && goal + 3000 >= liftCurrPos) {
                if (Math.abs(goal - liftCurrPos) <= 3000) {
                    lift.stop();
                    liftGoal = Optional.empty();
                } else if (liftCurrPos < goal) {
                    lift.up();
                } else if (liftCurrPos > goal) {
                    lift.down();
                }
            }

            launcher.displayStatus();

            telemetry.addData("Launcher RPM", launcher.getRpm());
            telemetry.addData("Lift Position", liftCurrPos);
            odometry.update();
            odometry.log();
            telemetry.update();
        }
    }
}
