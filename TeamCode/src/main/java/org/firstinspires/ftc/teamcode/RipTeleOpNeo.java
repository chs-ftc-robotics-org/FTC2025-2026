package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;

/*
1. LED for Color Sensor (ball)
2. LED for launcher rev
3. Garage Door functions (gamepad1.dpad)
 */
@TeleOp(name = "RIP TeleOp [NEW]")
public class RipTeleOpNeo extends LinearOpMode {
    @Override
    public void runOpMode() {
        // Camera camera = new Camera(this);
        Robot r = new Robot(this, 0);

        r.camera.start();

        Task.Pool pool = r.pool;

        Box<Boolean> intakeFinPrevState = Box.of(true);
        Box<Boolean> intakeEnabled = Box.of(true);
        Box<Integer> numBalls = Box.of(0);
        Box<Boolean> liveFaceGoalEnabled = Box.of(false);

        Task mainControl = Task.of(() -> {
            // ========= Movement ==========
            double axial = gamepad1.left_stick_y;
            double strafe = gamepad1.left_stick_x;
            double turn = gamepad1.right_stick_x;
            r.drivetrain.setFactor(1.0);
            if (Math.abs(axial) + Math.abs(strafe) + Math.abs(turn) > 0.05) {
                liveFaceGoalEnabled.set(false);
                r.pool.remove("LiveFaceGoal");
                r.pool.remove("AutoPower");
                r.drivetrain.move(strafe, axial, 0.7 * turn);
            }
            else if (!liveFaceGoalEnabled.get()) {
                r.drivetrain.stop();
            }

            // ========== Intake ===========
            if ((gamepad1.right_bumper || gamepad2.right_trigger > 0.1) && intakeEnabled.get()) {
                r.launcher.spinSetMode(Launcher.SpinMode.INTAKE);
                // r.launcher.spindexerSetIndex(0);
                if (r.launcher.spindexerReadyToIntake()) {
                    r.intake.start();
                }
            } else if (gamepad1.left_bumper || gamepad2.left_trigger > 0.1) {
                r.intake.reverse();
            } else {
                r.intake.stop();
            }

            // ========= Launcher ==========
            if (gamepad2.y) {
                r.launcher.feedUp();
                r.ballTracker.removeBall(r.launcher.spindexerGetIndex());
            }
            else {
                r.launcher.feedDown();
            }

//            if (gamepad2.yWasReleased() && r.launcher.spindexerReadyToLaunch()) {
//                r.ballTracker.addBall(r.launcher.spindexerGetIndex(), r.launcher.colorSensorGetDetection());
//            }

            if (gamepad1.dpadUpWasPressed()) {
                r.launcher.garageDoorRotate(-0.01);
                // r.launcher.spindexerRotate(0.001);
            }
            else if (gamepad1.dpadDownWasPressed()) {
                r.launcher.garageDoorRotate(0.01);
                // r.launcher.spindexerRotate(-0.001);
            }

            if (gamepad1.yWasPressed()) {
                r.launcher.setLaunchProfile(Launcher.LaunchProfile.FAR);
            } else if (gamepad1.bWasPressed()) {
                r.launcher.setLaunchProfile(Launcher.LaunchProfile.DEFAULT);
            } else if (gamepad1.aWasPressed()) {
                  r.launcher.setLaunchProfile(Launcher.LaunchProfile.NEAR);
            }
            
            if (r.intake.finIsNotPressed()) {
                if (gamepad2.dpadLeftWasPressed()) {
                    if (r.launcher.spindexerReadyToIntake()) r.ballTracker.addBall(r.launcher.spindexerGetIndex(), r.intake.colorSensorGetDetection());
                    r.launcher.spindexerAddIndex(-2);
//                    if (r.launcher.spindexerReadyToIntake()) {
//                        r.pool.forceAdd("RotateSpindexerLeft", Task.sequence(
//                            Task.once(() -> ),
//                            Task.pause(25),
//                            Task.once(() -> r.launcher.spindexerAddIndex(-2))
//                        ));
//                    } else {
//                        r.launcher.spindexerAddIndex(-2);
//                    }
                }
                else if (gamepad2.dpadRightWasPressed()) {
                    if (r.launcher.spindexerReadyToIntake()) r.ballTracker.addBall(r.launcher.spindexerGetIndex(), r.intake.colorSensorGetDetection());
                    r.launcher.spindexerAddIndex(2);
//                    if (r.launcher.spindexerReadyToIntake()) {
//                        r.pool.forceAdd("RotateSpindexerRight", Task.sequence(
//                            Task.once(() -> ),
//                            Task.pause(25),
//                            Task.once(() -> r.launcher.spindexerAddIndex(2))
//                        ));
//                    } else {
//                        r.launcher.spindexerAddIndex(2);
//                    }
                }
            }

            if (gamepad2.dpad_up) {
                r.launcher.spindexerSetIndex(1);
                // r.launcher.spinSetMode(Launcher.SpinMode.LAUNCH);
            }
            else if (gamepad2.dpad_down) {
                r.launcher.spindexerSetIndex(0);
                // r.launcher.spinSetMode(Launcher.SpinMode.INTAKE);
            }

            if (gamepad1.bWasPressed()) {
                if (pool.has("LaunchAll")) {
                    pool.remove("LaunchAll");
                }
                else {
                    pool.tryAdd("LaunchAll", launchAll(r));
                }
            }

            if (gamepad2.a) {
                r.launcher.flywheelRunWithPower(-0.45);
            }
            else if (gamepad2.right_bumper) {
                if (gamepad2.b) r.launcher.flywheelRunWithPower(1.0);
                else r.launcher.flywheelStart();
            }
            else if (gamepad2.left_bumper) {
                r.launcher.flywheelReverse();
            }
            else {
                r.launcher.flywheelStop();
            }

            r.launcher.colorSensorDisplayDetection();

            if (intakeFinPrevState.get() && !r.intake.finIsNotPressed()) {
                pool.tryAdd("DisableIntake", Task.sequence(
                        Task.pause(200),
                        Task.once(() -> intakeEnabled.set(false))
                ));

                pool.tryAdd("EmergencyEnableIntake", Task.sequence(
                        Task.pause(750),
                        Task.once(() -> intakeEnabled.set(true))
                ));
            }
            else if (!intakeFinPrevState.get() && r.intake.finIsNotPressed()) {
                pool.remove("EmergencyEnableIntake");

                r.ballTracker.addBall(r.launcher.spindexerGetIndex(), r.intake.colorSensorGetDetection());
                r.launcher.spindexerAddIndex(2);
                pool.forceAdd("EnableIntake", Task.sequence(
                        Task.pause(700),
                        Task.once(() -> intakeEnabled.set(true))
                ));
            }

            if (gamepad1.xWasPressed()) {
                liveFaceGoalEnabled.set(!liveFaceGoalEnabled.get());

                if (liveFaceGoalEnabled.get()) {
                    r.pool.forceAdd("LiveFaceGoal", r.liveFaceGoal());
                    r.pool.forceAdd("AutoPower", r.autoPower());
                } else {
                    r.pool.remove("LiveFaceGoal");
                    r.pool.remove("AutoPower");
                }
                // r.launcher.setLaunchProfile(Launcher.LaunchProfile.CALIBRATION);
            }

            if (gamepad2.xWasPressed()) {
                r.pool.forceAdd("DisplayMotif", Task.sequence(
                        r.fetchMotif(),
                        Task.once(() -> r.motifLeds.displayMotif(r.motif.get()))
                ));
            }

            intakeFinPrevState.set(r.intake.finIsNotPressed());

            Coordinate2D botpose = r.camera.botposeGetEstimate();
            if (botpose != null) telemetry.addData("Est Bot Pose", "(%f, %f)", botpose.x(), botpose.y());

            telemetry.addData("Balls", "[%s]", r.ballTracker.getCurrentBalls());

            // telemetry.addData("Detected Motif ID", r.motif.get() + 21);
            telemetry.addData("Live Correction Enabled", liveFaceGoalEnabled.get());
            telemetry.addData("Garage position", r.launcher.garageDoorGetPosition());
            telemetry.addData("Spindexer position", r.launcher.spindexerGetPosition());
            telemetry.addData("Launcher RPM", r.launcher.flywheelGetRpm());
            telemetry.addData("Launcher velocity", r.launcher.flywheelGetVelocity());

            // V_NEAR = 1760
            // V_NORMAL = 1860
            // V_FAR = 2320

            Hsv intakeColors = r.intake.colorSensorGetColors();
            telemetry.addData("[INTAKE] Detected color", "hsv(%f, %f, %f)", intakeColors.h, intakeColors.s, intakeColors.v);
            telemetry.addData("[INTAKE] Proximity", r.intake.colorSensorGetProximity());
            telemetry.addData("[INTAKE] Detected color", r.intake.colorSensorGetDetection().name());

            Hsv launcherColors = r.launcher.colorSensorGetColors();
            telemetry.addData("[LAUNCHER] Detected color", "hsv(%f, %f, %f)", launcherColors.h, launcherColors.s, launcherColors.v);
            telemetry.addData("[LAUNCHER] Proximity", r.launcher.colorSensorGetProximity());
            telemetry.addData("[LAUNCHER] Detected Ball Color", r.launcher.colorSensorGetDetection().name());

            telemetry.addData("Intake Fin Pressed", r.intake.finIsNotPressed());

            telemetry.addData("Number of Balls", numBalls.get());

            r.camera.report();
            r.launcher.handleGarageDoorSpecialCases();

            return Task.CONTINUE;
        });

        r.runTask(Task.all(Task.pool(pool), mainControl));
        // r.runTask(mainControl);
    }

//    private Task launch(Robot r) {
//        return Task.sequence(
//                r.launcher.feedToPosition(Launcher.FEED_POSITION_HALF),
//                r.launcher.raiseToPosition(Launcher.RAISE_POSITION_ACTIVE),
//                r.launcher.raiseToPosition(Launcher.RAISE_POSITION_IDLE)
//        );
//    }

    private Task launchOne(Robot r) {
        return Task.sequence(
                Task.until(r.launcher::flywheelReady),
                Task.once(r.launcher::feedUp),
                Task.pause(750),
                Task.once(r.launcher::feedDown),
                Task.pause(500),
                Task.once(r.launcher::spinNext),
                Task.pause(500)
        );
    }

    private Task launchAll(Robot r) {
        return Task.sequence(
                Task.once(() -> r.launcher.spinSetMode(Launcher.SpinMode.LAUNCH)),
                Task.once(() -> {
                    if (!r.launcher.spindexerReadyToLaunch()) r.launcher.spindexerAddIndex(1);
                }),
                Task.once(r.launcher::flywheelStart),
                launchOne(r),
                launchOne(r),
                launchOne(r),
                Task.once(r.launcher::flywheelStop)
        );
    }
}
