package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.hardware.DistanceSensor;
import com.qualcomm.robotcore.util.ElapsedTime;
import com.sun.tools.javac.jvm.Gen;

import java.util.concurrent.TimeUnit;

@Autonomous(name = "RIP Autonomous")
public class RipAutonomous extends LinearOpMode {
    @FunctionalInterface
    interface Task {
        enum Result {
            CONTINUE,
            FINISH
        }

        Result run();

        static Task sleep(int millis) {
            ElapsedTime time = new ElapsedTime();
            time.reset();

            return () -> {
                if (time.time(TimeUnit.MILLISECONDS) >= millis) {
                    return FINISH;
                }
                return CONTINUE;
            };
        }

        static Task once(Runnable f) {
            return () -> {
                f.run();
                return FINISH;
            };
        }

        static Task sequence(Task... tasks) {
            Box<Integer> current = new Box<>(0);
            return () -> {
                Task t = tasks[current.get()];
                Task.Result result = t.run();
                if (result == FINISH) {
                    current.set(current.get() + 1);
                    if (current.get() == tasks.length) return Task.Result.FINISH;
                }
                return Task.Result.CONTINUE;
            };
        }

        static Task parallel(Task... tasks) {
            Box<Integer> remaining = new Box<>(tasks.length);
            boolean[] done = new boolean[tasks.length];
            return () -> {
                for (int i = 0; i < tasks.length; i++) {
                    if (done[i]) continue;
                    Task.Result result = tasks[i].run();
                    if (result == FINISH) {
                        remaining.set(remaining.get() - 1);
                        done[i] = true;
                    }
                }
                return remaining.get() == 0 ? FINISH : CONTINUE;
            };
        }
    }

    static final Task.Result CONTINUE = Task.Result.CONTINUE;
    static final Task.Result FINISH = Task.Result.FINISH;

    enum State {
        INIT,
        MOVE,
        MOVE2,
        SPIN,
        STOP,
    }

    private State state = State.INIT;

    /*
    private State state() {
        return state;
    }

    private void stateTo(State new_) {
        state = new_;
    }
    */

    private Odometry odometry;
    private Drivetrain drivetrain;
    private Launcher launcher;

    @Override
    public void runOpMode() {
        drivetrain = new Drivetrain(this);
        drivetrain.setFactor(0.9);
        DistanceSensor ds = hardwareMap.get(DistanceSensor.class, "sensors/distance");

        launcher = new Launcher(this);

        odometry = new Odometry(this);
        odometry.reset();

        waitForStart();

        /*
        runTask(() -> {
            // if (ds.getDistance(DistanceUnit.CM) > 10) drivetrain.move(0, 0.2, 0);
            // telemetry.addData("Distance Detected", "%.2f", ds.getDistance(DistanceUnit.CM));

//            switch (state) {
//                case INIT:
//                    drivetrain.move(0, -0.5, 0);
//                    state = State.MOVE;
//                    break;
//                case MOVE:
//                    if (odometry.posX() >= 36) {
//                        drivetrain.move(0.5, 0, 0);
//                        state = State.MOVE2;
//                    }
//                    break;
//                case MOVE2:
//                    if (odometry.posY() >= 24) {
//                        drivetrain.move(0, 0, 0.4);
//                        state = State.SPIN;
//                    }
//                case SPIN:
//                    if (odometry.angle() <= -45) {
//                        drivetrain.move(0, 0, 0);
//                        state = State.STOP;
//                    }
//                    break;
//            }
        });
        */



        // @formatter:off
        /*
        runSequence(
                // Init
                () -> {
                    drivetrain.move(0.5, 0, 0);
                    return FINISH;
                },

                // Move to prelaunch and face goal
                () -> {
                    if (odometry.posX() >= 24) {
                        drivetrain.move(0, -0.5, 0);
                        return FINISH;
                    }
                    return CONTINUE;
                },
                () -> {
                    if (odometry.posY() >= 36) {
                        drivetrain.move(0, 0, 0.4);
                        return FINISH;
                    }
                    return CONTINUE;
                },
                () -> {
                    if (odometry.angle() <= -55) {
                        drivetrain.move(0, 0, 0);
                        return FINISH;
                    }
                    return CONTINUE;
                },
                () -> {
                    runSequence(launch);
                    return FINISH;
                }
        );
        */

        // formatter:on

        runTask(launch);
    }

//    private void moveToPrelaunch() {
//        runTask(() -> {
//            switch (state) {
//
//            }
//        });
//    }

    private void runTask(Task task) {
        while (opModeIsActive()) {
            odometry.update();
            Task.Result result = task.run();
            odometry.log();
            telemetry.addData("State", state);
            telemetry.update();

            if (result == Task.Result.FINISH) break;
        }
    }

    private final Task launch = Task.sequence(
            Task.once(() -> launcher.lockFeed()),
            Task.sleep(200),
            Task.once(() -> launcher.start(0.9)),
            () -> {
                if (launcher.getRpm() >= 45) {
                    return FINISH;
                }
                launcher.displayStatus();
                telemetry.addData("RPM", launcher.getRpm());
                return CONTINUE;
            },
            Task.once(() -> launcher.pushFeed()),
//            () -> {
//                launcher.pushFeed();
//
//                return CONTINUE;
//            },
            Task.sleep(500),
            () -> {
                launcher.stop();
                launcher.prepareFeed();
                return FINISH;
            }
    );
}
