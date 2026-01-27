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
//                r.pool.remove("LiveFaceGoal");
                liveFaceGoalEnabled.set(false);
                r.drivetrain.move(strafe, axial, 0.7 * turn);
            }
            else if (!liveFaceGoalEnabled.get()) {
                r.drivetrain.stop();
            }

            // ========== Intake ===========
            if ((gamepad1.right_bumper || gamepad2.right_trigger > 0.1) && intakeEnabled.get()) {
                r.launcher.spinSetMode(Launcher.SpinMode.INTAKE);
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
            }
            else {
                r.launcher.feedDown();
            }

            if (gamepad2.yWasReleased() && r.launcher.flywheelGetRpm() > 0) {
//                if (numBalls.get() != 1) {
//                    pool.tryAdd("AutoRotateSpindexer", Task.sequence(
//                            Task.pause(500),
//                            Task.once(() -> r.launcher.spinAddIndex(2))
//                    ));
//                }
//                if (numBalls.get() == 1) {
//                    // will become 0
//                    pool.tryAdd("SetBallCountIntakeMode", Task.sequence(
//                        Task.pause(1000),
//                        Task.once(() -> r.launcher.spinSetMode(Launcher.SpinMode.INTAKE))
//                    ));
//                }
//                numBalls.set(Math.max(numBalls.get() - 1, 0));
            }

            if (gamepad1.dpadUpWasPressed()) {
                r.launcher.garageDoorRotate(-0.01);
            }
            else if (gamepad1.dpadDownWasPressed()) {
                r.launcher.garageDoorRotate(0.01);
            }

            if (gamepad1.yWasPressed()) {
                r.launcher.setLaunchProfile(Launcher.LaunchProfile.FAR);
            } else if (gamepad1.bWasPressed()) {
                r.launcher.setLaunchProfile(Launcher.LaunchProfile.DEFAULT);
            } else if (gamepad1.aWasPressed()) {
                  r.launcher.setLaunchProfile(Launcher.LaunchProfile.NEAR);
              }
            
            if (r.intake.finIsNotPressed()) {
                boolean modNumBalls = false;
                if (gamepad2.dpadLeftWasPressed()) {
                    r.launcher.spindexerAddIndex(-2);
                    if (gamepad2.a) modNumBalls = true;
                }
                else if (gamepad2.dpadRightWasPressed()) {
                    r.launcher.spindexerAddIndex(2);
                    if (gamepad2.a) modNumBalls = true;
                }

//                if (modNumBalls) {
//                    if (numBalls.get() == 2) {
//                        // will become 3
//                        r.launcher.spinSetMode(Launcher.SpinMode.LAUNCH);
//                    }
//                    numBalls.set(Math.min(numBalls.get() + 1, 3));
//                }
            }

//            if (r.joystick2.leftXUpdated()) {
//                switch (r.joystick2.leftX()) {
//                    case NEGATIVE:
//                        r.launcher.spinNext();
//                        break;
//                    case POSITIVE:
//                        r.launcher.spinPrev();
//                        break;
//                }
//            }

            if (gamepad2.dpad_up) {
                r.launcher.spindexerSetIndex(1);
//                r.launcher.spinSetMode(Launcher.SpinMode.LAUNCH);
            }
            else if (gamepad2.dpad_down) {
                r.launcher.spinSetMode(Launcher.SpinMode.INTAKE);
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
                        Task.pause(2000),
                        Task.once(() -> intakeEnabled.set(true))
                ));
            }
            else if (!intakeFinPrevState.get() && r.intake.finIsNotPressed()) {
                pool.remove("EmergencyEnableIntake");
//                if (r.intake.powerGet() >= 0) {
//                    if (numBalls.get() == 2) {
//                        // will become 3
//                        r.launcher.spinSetMode(Launcher.SpinMode.LAUNCH);
//                    }
//                    numBalls.set(Math.min(numBalls.get() + 1, 3));
//                }

                r.launcher.spindexerAddIndex(2);
                pool.forceAdd("EnableIntake", Task.sequence(
                        Task.pause(700),
                        Task.once(() -> intakeEnabled.set(true))
                ));
            }

            if (gamepad2.xWasPressed()) {
                if (r.pool.has("FaceGoal")) r.pool.remove("FaceGoal");
                else r.pool.forceAdd("FaceGoal", r.faceClosestGoal());
            }

            if (gamepad1.xWasPressed()) {
//                if (liveFaceGoalEnabled.get()) {
//                    r.pool.remove("LiveFaceGoal");
//                    liveFaceGoalEnabled.set(false);
//                }
//                else {
//                    r.pool.forceAdd("LiveFaceGoal", r.liveFaceGoal());
//                    liveFaceGoalEnabled.set(true);
//                }
//                if (r.pool.has("LiveFaceGoal")) r.pool.remove("LiveFaceGoal");
//                else r.pool.forceAdd("LiveFaceGoal", r.liveFaceGoal());
                liveFaceGoalEnabled.set(!liveFaceGoalEnabled.get());
            }

            if (liveFaceGoalEnabled.get()) {
                r.pool.forceAdd("FaceGoal", r.faceClosestGoal());
            }

            intakeFinPrevState.set(r.intake.finIsNotPressed());

            telemetry.addData("Live Correction Enabled", liveFaceGoalEnabled.get());
            telemetry.addData("Garage position", r.launcher.garageDoorGetPosition());
            telemetry.addData("Spindexer position", r.launcher.spindexerGetPosition());
            telemetry.addData("Launcher RPM", r.launcher.flywheelGetRpm());
            telemetry.addData("Launcher velocity", r.launcher.flywheelGetVelocity());

            // V_NEAR = 1760
            // V_NORMAL = 1860
            // V_FAR = 2320

            Hsv cs = r.launcher.colorSensorGetColors();
            telemetry.addData("Detected color", "hsv(%f, %f, %f)", cs.h, cs.s, cs.v);
            telemetry.addData("Proximity", r.launcher.colorSensorGetProximity());
            telemetry.addData("Detected Ball Color", r.launcher.colorSensorGetDetection().name());

            telemetry.addData("Intake Fin Pressed", r.intake.finIsNotPressed());

            telemetry.addData("Number of Balls", numBalls.get());

            r.camera.report();

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
