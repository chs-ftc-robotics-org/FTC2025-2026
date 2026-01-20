package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;

import java.util.function.Function;

public class RipAutonomous {
    private static void basicGoalSide(LinearOpMode opMode, double side) {
        Robot r = new Robot(opMode, 0);
        Task t = Task.sequence(
                r.moveBy(side * 25, -35, 0.6),
                r.faceDir(side * 10, 0.6),

                r.fetchMotif(),
                r.faceDir(side * -45, 0.6),
                r.faceClosestGoal(),
                Task.once(() -> r.launcher.setLaunchProfile(Launcher.LaunchProfile.AUTONOMOUS)),
                r.launchMotif(0),
                r.moveBy(side * -3, -10, 0.5),
                r.faceDir(side * -90, 0.4),
                // r.moveBy(0, -13, 0.6),
                doIntake(r),
                rawMove(r, 0.0),
                r.moveBy(36, 10, 0.5),
                r.faceDir(-55, 0.45),
                r.launchMotif(2),
                r.moveBy(-24, -24, 0.5)
//                 rawMove(r, -0.3, x -> x < 0),
//                 r.launcher.addSpinIndexAndWait(2),
//                 rawMove(r, -0.3, x -> x < -4),
//                 r.moveBy(30, 13, 0.6),
//                 Task.once(r.intake::stop),
//                r.moveBy(-24, -24, 0.5)
        );
        r.runTask(t);
    }

    private static void basicParkingSide(LinearOpMode opMode, double side) {
        Robot r = new Robot(opMode, 0);
        Task t = Task.sequence(
                r.fetchMotif(),
                Task.once(() -> r.launcher.setLaunchProfile(Launcher.LaunchProfile.AUTONOMOUS_FAR)),
                r.moveBy(0, 1.5, 0.3),
                r.faceDir(side * -23.5, 0.3),
                // r.faceClosestGoal(),
                r.launchMotif(0),
                r.faceDir(side * -90, 0.4),
                r.moveBy(-10, 24, 0.6),
                doIntake(r)
//                r.moveBy()
//                r.moveTo(-10, 10, 0.5),
//                r.moveTo(0, 0, 0.2),
//                r.faceDir(side * -23.5, 0.3),
                // r.faceClosestGoal(),
                //r.launchMotif(0),
                // r.moveBy(side * -20, 20, 0.4)
        );
        r.runTask(t);
    }

    private static Task doIntake(Robot r) {
        return Task.sequence(
                Task.once(() -> r.launcher.spindexerSetIndex(0)),
                Task.once(r.intake::start),
                rawMove(r, -0.2),
                Task.until(() -> !r.intake.finIsNotPressed()),
                Task.until(r.intake::finIsNotPressed),
                r.launcher.addSpinIndexAndWait(2),
                Task.any(
                        Task.until(() -> !r.intake.finIsNotPressed()),
                        Task.pause(1000)
                ),
                Task.until(r.intake::finIsNotPressed),
                r.launcher.addSpinIndexAndWait(2),
                Task.any(
                        Task.until(() -> !r.intake.finIsNotPressed()),
                        Task.pause(1000)
                ),
                Task.until(r.intake::finIsNotPressed),
                r.launcher.addSpinIndexAndWait(2),
                Task.once(r.intake::stop)
        );
    }

    private static final double RED_SIDE = 1.0;
    private static final double BLUE_SIDE = -1.0;

    @Autonomous(name = "Red Goal")
    public static class RedNear extends LinearOpMode {
        @Override
        public void runOpMode() {
            basicGoalSide(this, 1.0);
        }
    }

    @Autonomous(name = "Red Parking")
    public static class RedFar extends LinearOpMode {
        @Override
        public void runOpMode() {
            basicParkingSide(this, 1.0);
        }
    }

    @Autonomous(name = "Blue Goal")
    public static class BlueNear extends LinearOpMode {
        @Override
        public void runOpMode() {
            basicGoalSide(this, -1.0);
        }
    }

    @Autonomous(name = "Blue Parking")
    public static class BlueFar extends LinearOpMode {
        @Override
        public void runOpMode() {
            basicParkingSide(this, -1.0);
        }
    }

    private static Task rawMove(Robot r, double power) {
        return Task.once(() -> {
            r.drivetrain.setFactor(1.0);
            r.drivetrain.move(0, power, 0);
        });
    }

    private static Task rawMove(Robot r, double power, Function<Double, Boolean> f) {
        return Task.sequence(
                Task.once(() -> r.drivetrain.move(0, power, 0)),
                Task.until(() -> f.apply(r.odometry.posX())),
                Task.once(() -> r.drivetrain.move(0, 0, 0))
        );
    }

//    @Autonomous(name = "Red Goal [MODIFIED]")
//    public static class RedNear2 extends LinearOpMode {
//        @Override
//        public void runOpMode() {
//            Robot r = new Robot(this);
//            Task t = Task.sequence(
//                    r.moveBy(-25, 35, 0.3),
//                    r.faceDir(-55, 0.3),
//                    // r.launchAll(),
//                    r.faceDir(0, 0.3),
//                    r.moveBy(0, 13, 0.3),
//                    r.faceDir(90, 0.3),
//                    Task.once(r.intake::start),
//                    Task.once(() -> r.drivetrain.move(0, -0.4, 0)),
//                    Task.until(() -> r.odometry.posX() > 18),
//                    Task.once(r.intake::stop),
//                    Task.once(() -> r.drivetrain.move(0, 0.4, 0)),
//                    Task.until(() -> r.odometry.posX() < -24),
//                    Task.once(() -> r.drivetrain.move(0, 0, 0)),
//                    r.faceDir(0, 0.3),
//                    r.moveBy(0, -13, 0.3),
//                    r.faceDir(-55, 0.3),
//                    Task.once(() -> r.launcher.setLaunchProfile(Launcher.LaunchProfile.DEFAULT)),
//                    r.launchMotif(),
//                    r.faceDir(0, 0.3),
//                    r.moveBy(22, 22, 0.3)
//            );
//            r.runTask(t);
//        }
//    }
//
//    @Autonomous(name = "Blue Goal [MODIFIED]")
//    public static class BlueNear2 extends LinearOpMode {
//        @Override
//        public void runOpMode() {
//            Robot r = new Robot(this);
//            Task t = Task.sequence(
//                    r.moveBy(25, 35, 0.3),
//                    r.faceDir(55, 0.3),
//                    // r.launchAll(),
//                    r.faceDir(0, 0.3),
//                    r.moveBy(0, 13, 0.3),
//                    r.faceDir(-90, 0.3),
//                    Task.once(r.intake::start),
//                    Task.once(() -> r.drivetrain.move(0, -0.4, 0)),
//                    Task.until(() -> r.odometry.posX() < -18),
//                    Task.once(r.intake::stop),
//                    Task.once(() -> r.drivetrain.move(0, 0.4, 0)),
//                    Task.until(() -> r.odometry.posX() > 24),
//                    Task.once(() -> r.drivetrain.move(0, 0, 0)),
//                    r.faceDir(0, 0.3),
//                    r.moveBy(0, -13, 0.3),
//                    r.faceDir(55, 0.3),
//                    // r.launchAll(),
//                    r.faceDir(0, 0.3),
//                    r.moveBy(-22, 22, 0.3)
//            );
//            r.runTask(t);
//        }
//    }
//
//    @Autonomous(name = "Blue Goal")
//    public static class BlueNear extends LinearOpMode {
//        @Override
//        public void runOpMode() {
//            Robot r = new Robot(this);
//            Task t = Task.sequence(
//                    r.moveBy(25, 35, 0.3),
//                    r.faceDir(55, 0.3),
//                    // r.launchAll(),
//                    r.faceDir(0, 0.3),
//                    r.moveBy(-22, 22, 0.3)
//            );
//            r.runTask(t);
//        }
//    }
//
//    @Autonomous(name = "Red Parking")
//    public static class RedFar extends LinearOpMode {
//        @Override
//        public void runOpMode() {
//            Robot r = new Robot(this);
//            Task t = Task.sequence(
//                    r.moveBy(0, -(3.5 * 24), 0.3),
//                    r.faceDir(-55.0, 0.3),
//                    // r.launchAll(),
//                    r.faceDir(0, 0.3),
//                    r.moveBy(16, 16, 0.3)
//            );
//            r.runTask(t);
//        }
//    }
//
//    @Autonomous(name = "Blue Parking")
//    public static class BlueFar extends LinearOpMode {
//        @Override
//        public void runOpMode() {
//            Robot r = new Robot(this);
//            Task t = Task.sequence(
//                    r.moveBy(0, -(3.5 * 24), 0.3),
//                    r.faceDir(55.0, 0.3),
//                    // r.launchAll(),
//                    r.faceDir(0, 0.3),
//                    r.moveBy(-16, 16, 0.3)
//            );
//            r.runTask(t);
//        }
//    }
//
//    @Autonomous(name = "Angled strafing test", group = "tests")
//    public static class AngledStrafingTest extends LinearOpMode {
//        @Override
//        public void runOpMode() {
//            Robot r = new Robot(this);
//            Task t = Task.sequence(
//                    r.faceDir(120, 0.2),
//                    r.moveBy(12, 0, 0.2),
//                    r.faceDir(-43, 0.2),
//                    r.moveBy(-12, 0, 0.2),
//                    r.faceDir(0, 0.2)
//            );
//            r.runTask(t);
//        }
//    }
}