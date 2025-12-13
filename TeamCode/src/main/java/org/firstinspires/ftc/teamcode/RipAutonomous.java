package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.hardware.DistanceSensor;

//@Autonomous(name = "RIP Autonomous")
//public class RipAutonomous extends LinearOpMode {
//    private Odometry odometry;
//    private Drivetrain drivetrain;
//    private Launcher launcher;
//    private Intake intake;
//
//    private void initSubsystems() {
//        drivetrain = new Drivetrain(this);
//        drivetrain.setFactor(0.75);
//        // DistanceSensor _ds = hardwareMap.get(DistanceSensor.class, "sensors/distance");
//        launcher = new Launcher(this);
//        odometry = new Odometry(this);
//        odometry.reset(0, 0, 0);
//        // odometry.notifyCurrentPos(24, 0);
//        intake = new Intake(this);
//    }
//
//    final static Task.Running.Result CONTINUE = Task.CONTINUE;
//    final static Task.Running.Result FINISH = Task.FINISH;
//
//    @Override
//    public void runOpMode() {
//        initSubsystems();
//
//        waitForStart();
//
////        final Task WARMUP_LAUNCHER = Task.until(() -> launcher.getRpm() >= Launcher.IDEAL_RPM);
////
////        final Task LAUNCH = Task.sequence(
////                Task.once(launcher::lockFeed),
////                Task.pause(500),
////                Task.once(() -> launcher.start(Launcher.BASELINE_POWER)),
////                WARMUP_LAUNCHER,
////                Task.once(launcher::pushFeed),
////                Task.pause(1000),
////                Task.of(() -> {
////                    launcher.stop();
////                    launcher.resetFeed();
////                    return Task.FINISH;
////                }),
////                Task.pause(1000)
////        );
////
////        final Task LAUNCH_TWO = Task.sequence(
////                Task.once(launcher::resetFeed),
////                Task.once(() -> launcher.start(Launcher.BASELINE_POWER)),
////                prepLauncher(Launcher.IDEAL_RPM + 3),
////                Task.once(launcher::lockFeed),
////                Task.pause(1500),
////                WARMUP_LAUNCHER,
////                Task.once(launcher::pushFeed),
////                Task.until(() -> launcher.isAtPosition(Launcher.PUSH_LOCATION)),
////                Task.pause(750),
////                Task.once(() -> {
////                    launcher.stop();
////                    launcher.resetFeed();
////                })
////        );
////
////        final Task LAUNCH_THREE = Task.sequence(
////                Task.once(launcher::resetFeed),
////                Task.once(() -> launcher.start(Launcher.BASELINE_POWER + 0.1)),
////                prepLauncher(Launcher.IDEAL_RPM + 3),
////                Task.once(intake::start),
////                Task.pause(250),
////                Task.once(intake::stop),
////                Task.once(() -> launcher.stop()),
////                Task.pause(500),
////                Task.once(intake::start),
////                Task.pause(500),
////                Task.once(intake::stop),
////                LAUNCH_TWO
////        );
//
////        runTask(Task.sequence(
////                Task.once(() -> drivetrain.move(0.8, 0, 0)),
////                Task.until(() -> odometry.posX() >= 14),
////
////                Task.once(() -> drivetrain.move(0, -0.8, 0)),
////                Task.until(() -> odometry.posY() >= 32),
////
////                Task.once(() -> drivetrain.move(0, 0, 0.4)),
////                Task.until(() -> odometry.normalizedAngle() <= -37),
////                Task.once(() -> drivetrain.stop()),
////
////                LAUNCH_THREE,
////                Task.once(launcher::resetFeed),
////                Task.until(() -> launcher.isAtPosition(Launcher.PREP_LOCATION)),
////                Task.pause(1000)
////        ));
//
////        runTask(() -> {
////            Box<Task.Running> active = Box.of(null);
////
////            return () -> {
////                if (active.get() == null) {
////                    if (gamepad1.dpad_down) active.set(faceDir(180, 0.5).spawn());
////                    else if (gamepad1.dpad_left) active.set(faceDir(90, 0.5).spawn());
////                    else if (gamepad1.dpad_right) active.set(faceDir(-90, 0.5).spawn());
////                    else if (gamepad1.dpad_up) active.set(faceDir(0, 0.5).spawn());
////                }
////
////                if (active.get() != null) {
////                    Task.Running.Result result = active.get().run();
////                    if (result == FINISH) active.set(null);
////                }
////
////                return CONTINUE;
////            };
////        });
//
//        // runTask(LAUNCH_TWO);
//        /*
//        runTask(Task.sequence(
//                moveBy(8.0, 8.0),
//                moveBy(-8.0, 8.0),
//                faceDir(180),
//                Task.pause(1000),
//                faceDir(-90)
//
//                Task.once(() -> drivetrain.move(0.5, 0, 0)),
//                Task.until(() -> odometry.posX() >= 24),
//
//                Task.once(() -> drivetrain.move(0, -0.5, 0)),
//                Task.until(() -> odometry.posY() >= 48),
//
//                Task.once(() -> drivetrain.move(0, 0, -0.4)),
//                Task.until(() -> odometry.normalizedAngle() >= 90),
//
//                Task.once(() -> {
//                    drivetrain.move(0, -0.5, 0);
//                    intake.start();
//                }),
//
//                Task.until(() -> odometry.posX() <= -6),
//                Task.once(() -> {
//                    intake.stop();
//                    drivetrain.move(0, 0, 0);
//                })
//
//
//                // LAUNCH
//        ));
//
//        */
//
//        /* runTask(Task.sequence(
//                moveBy(12, 0, 0.3),
//                Task.pause(1000),
//                moveBy(0, 12, 0.3),
//                Task.pause(1000),
//                moveBy(-12, -12, 0.3)
//        )); */
//
//        /* runTask(Task.sequence(
//                moveBy(0, -(3.5 * 24), 0.3),
//                faceDir(-55.0, 0.3),
//                LAUNCH_THREE,
//                faceDir(0, 0.3),
//                moveBy(16, 16, 0.3)
//        )); */
//
//        runTask(Task.sequence(
//                moveBy(-25, 35, 0.3),
//                faceDir(-55.0, 0.3),
//                LAUNCH_THREE,
//                faceDir(0, 0.3),
//                moveBy(22, 22, 0.3)
//        ));
//    }
//
//
//}

public class RipAutonomous {
    @Autonomous(name = "Red Goal")
    public static class RedNear extends LinearOpMode {
        @Override
        public void runOpMode() {
            Robot r = new Robot(this);
            Task t = Task.sequence(
                    r.moveBy(-25, 35, 0.3),
                    r.faceDir(-55, 0.3),
                    r.launchAll(),
                    r.faceDir(0, 0.3),
                    r.moveBy(22, 22, 0.3)
            );
            r.runTask(t);
        }
    }

    @Autonomous(name = "Blue Goal")
    public static class BlueNear extends LinearOpMode {
        @Override
        public void runOpMode() {
            Robot r = new Robot(this);
            Task t = Task.sequence(
                    r.moveBy(25, 35, 0.3),
                    r.faceDir(55, 0.3),
                    r.launchAll(),
                    r.faceDir(0, 0.3),
                    r.moveBy(-22, 22, 0.3)
            );
            r.runTask(t);
        }
    }

    @Autonomous(name = "Red Parking")
    public static class RedFar extends LinearOpMode {
        @Override
        public void runOpMode() {
            Robot r = new Robot(this);
            Task t = Task.sequence(
                    r.moveBy(0, -(3.5 * 24), 0.3),
                    r.faceDir(-55.0, 0.3),
                    r.launchAll(),
                    r.faceDir(0, 0.3),
                    r.moveBy(16, 16, 0.3)
            );
            r.runTask(t);
        }
    }

    @Autonomous(name = "Blue Parking")
    public static class BlueFar extends LinearOpMode {
        @Override
        public void runOpMode() {
            Robot r = new Robot(this);
            Task t = Task.sequence(
                    r.moveBy(0, -(3.5 * 24), 0.3),
                    r.faceDir(55.0, 0.3),
                    r.launchAll(),
                    r.faceDir(0, 0.3),
                    r.moveBy(-16, 16, 0.3)
            );
            r.runTask(t);
        }
    }
}