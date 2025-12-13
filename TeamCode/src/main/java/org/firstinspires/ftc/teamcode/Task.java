package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.util.ElapsedTime;

import java.util.Arrays;
import java.util.concurrent.TimeUnit;
import java.util.function.Supplier;

@FunctionalInterface
public interface Task {
    interface Running {
        enum Result {
            CONTINUE,
            FINISH
        }

        Result run();
    }

    Running.Result CONTINUE = Running.Result.CONTINUE;
    Running.Result FINISH = Running.Result.FINISH;

    Running spawn();

    static Task of(Running r) {
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
            Box<Running> spawned = Box.of(tasks[0].spawn());
            return () -> {
                Running t = spawned.get();
                Running.Result result = t.run();
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
            Running[] runningTasks = Util.arrayMap(tasks, Task::spawn);
            boolean[] done = new boolean[tasks.length];
            return () -> {
                for (int i = 0; i < tasks.length; i++) {
                    if (done[i]) continue;
                    Running.Result result = runningTasks[i].run();
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
            Running[] runningTasks = Util.arrayMap(tasks, Task::spawn);
            return () -> {
                for (Running r : runningTasks) {
                    if (FINISH == r.run()) {
                        done.set(true);
                    }
                }
                return done.get() ? FINISH : CONTINUE;
            };
        };
    }

    static Task until(Supplier<Boolean> f) {
        return () -> () -> f.get() ? FINISH : CONTINUE;
    }
}
