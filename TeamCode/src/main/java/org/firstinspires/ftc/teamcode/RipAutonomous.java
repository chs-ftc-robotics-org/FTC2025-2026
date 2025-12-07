package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.hardware.DistanceSensor;
import com.qualcomm.robotcore.util.ElapsedTime;
import com.sun.tools.javac.jvm.Gen;

import java.util.Arrays;
import java.util.concurrent.TimeUnit;

@Autonomous(name = "RIP Autonomous")
public class RipAutonomous extends LinearOpMode {
    @FunctionalInterface
    interface RunningTask {
        enum Result {
            CONTINUE,
            FINISH
        }

        Result run();
    }

    interface Task {
        RunningTask spawn();

        static Task of(RunningTask r) {
            return () -> r;
        }

        static Task pause(int millis) {
            return () -> {
                ElapsedTime time = new ElapsedTime();
                Box<Boolean> waiting = Box.of(true);

                return () -> {
                    if (waiting.get()) {
                        time.reset();
                        waiting.set(false);
                    }
                    if (time.time(TimeUnit.MILLISECONDS) >= millis) {
                        return FINISH;
                    }
                    return CONTINUE;
                };
            };
        }

        static Task once(Runnable f) {
            return () -> () -> {
                f.run();
                return FINISH;
            };
        }

        static Task sequence(final Task... tasks) {
            return () -> {
                Box<Integer> current = Box.of(0);
                Box<RunningTask> spawned = Box.of(tasks[0].spawn());
                return () -> {
                    RunningTask t = spawned.get();
                    RunningTask.Result result = t.run();
                    if (result == FINISH) {
                        current.set(current.get() + 1);
                        if (current.get() == tasks.length) return FINISH;
                        spawned.set(tasks[current.get()].spawn());
                    }
                    return CONTINUE;
                };
            };
        }

        static Task all(Task... tasks) {
            return () -> {
                Box<Integer> remaining = Box.of(tasks.length);
                RunningTask[] runningTasks = Arrays.stream(tasks).map(Task::spawn).toArray(RunningTask[]::new);
                boolean[] done = new boolean[tasks.length];
                return () -> {
                    for (int i = 0; i < tasks.length; i++) {
                        if (done[i]) continue;
                        RunningTask.Result result = runningTasks[i].run();
                        if (result == FINISH) {
                            remaining.set(remaining.get() - 1);
                            done[i] = true;
                        }
                    }
                    return remaining.get() == 0 ? FINISH : CONTINUE;
                };
            };
        }

        static Task any(Task... tasks) {
            return () -> {
                Box<Boolean> done = Box.of(false);
                RunningTask[] runningTasks = Arrays.stream(tasks).map(Task::spawn).toArray(RunningTask[]::new);
                return () -> {
                    for (RunningTask r : runningTasks) {
                        if (FINISH == r.run()) {
                            done.set(true);
                        }
                    }
                    return done.get() ? FINISH : CONTINUE;
                };
            };
        }
    }

    static final RunningTask.Result CONTINUE = RunningTask.Result.CONTINUE;
    static final RunningTask.Result FINISH = RunningTask.Result.FINISH;

    private Odometry odometry;
    private Drivetrain drivetrain;
    private Launcher launcher;

    private void initSubsystems() {
        drivetrain = new Drivetrain(this);
        drivetrain.setFactor(0.9);
        DistanceSensor _ds = hardwareMap.get(DistanceSensor.class, "sensors/distance");
        launcher = new Launcher(this);
        odometry = new Odometry(this);
        odometry.reset();
    }

    @Override
    public void runOpMode() {
        initSubsystems();

        waitForStart();

        final Task LAUNCH = Task.sequence(
                Task.once(launcher::lockFeed),
                Task.pause(2000),
                Task.once(() -> launcher.start(0.9)),
                Task.pause(2000),
                Task.of(() -> {
                    if (launcher.getRpm() >= 45) {
                        return FINISH;
                    }
                    launcher.displayStatus();
                    telemetry.addData("RPM", launcher.getRpm());
                    return CONTINUE;
                }),
                Task.pause(3000),
                Task.once(launcher::pushFeed),
                Task.pause(1000),
                Task.of(() -> {
                    launcher.stop();
                    launcher.prepareFeed();
                    return FINISH;
                })
        );

        runTask(LAUNCH);

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
        runTask(Task.sequence(
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
                }
                // Task.sequence(launch)
        ));
        */


        // formatter:on
    }

    private void runTask(Task task) {
        RunningTask r = task.spawn();
        while (opModeIsActive()) {
            odometry.update();
            RunningTask.Result result = r.run();
            odometry.log();
            telemetry.update();

            if (result == FINISH) break;
        }
    }
}
