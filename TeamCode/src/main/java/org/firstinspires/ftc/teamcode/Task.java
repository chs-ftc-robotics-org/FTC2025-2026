package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.util.ElapsedTime;

import java.util.HashMap;
import java.util.concurrent.TimeUnit;
import java.util.function.Supplier;

public class Task {
    public enum ControlFlow {
        CONTINUE,
        BREAK
    }


    static ControlFlow CONTINUE = ControlFlow.CONTINUE;
    static ControlFlow BREAK = ControlFlow.BREAK;

    private final Runnable init;
    private final Supplier<ControlFlow> loop;
    private final Runnable finish;

    private boolean started = false;
    private boolean done = false;

    private Task(Runnable init, Supplier<ControlFlow> loop, Runnable finish) {
        this.init = init;
        this.loop = loop;
        this.finish = finish;
    }

    public ControlFlow run() {
        if (done) return BREAK;
        if (!started) {
            init.run();
            started = true;
        }

        Task.ControlFlow result = loop.get();
        if (result == BREAK) {
            finish.run();
            done = true;
        }
        return result;
    }

    public void cancel() {
        if (done) return;
        if (!started) {
            init.run();
            started = true;
        }

        finish.run();
        done = true;
    }

    static Task of(Runnable init, Supplier<ControlFlow> loop, Runnable finish) {
        return new Task(init, loop, finish);
    }

    static Task of(Runnable init, Supplier<ControlFlow> loop) {
        return Task.of(init, loop, () -> {});
    }

    static Task of(Supplier<ControlFlow> r) {
        return Task.of(() -> {}, r);
    }

    static Task pause(int millis) {
        ElapsedTime time = new ElapsedTime();
        return Task.of(time::reset, () -> (time.time(TimeUnit.MILLISECONDS) >= millis) ? BREAK : CONTINUE);
    }

    static Task once(Runnable f) {
        return Task.of(() -> {
            f.run();
            return BREAK;
        });
    }

    static Task sequence(final Task... tasks) {
        Box<Integer> current = Box.of(0);
        return Task.of(() -> {
            Task t = tasks[current.get()];
            switch (t.run()) {
                case CONTINUE:
                    return CONTINUE;
                case BREAK:
                    current.set(current.get() + 1);
                    return (current.get() == tasks.length) ? BREAK : CONTINUE;
                default:
                    throw new NullPointerException();
            }
        });
    }

    static Task all(Task... tasks) {
        Box<Integer> remaining = Box.of(tasks.length);

        return Task.of(() -> {
            for (Task task : tasks) {
                if (task.done) continue;
                if (task.run() == BREAK) {
                    remaining.set(remaining.get() - 1);
                }
            }
            return remaining.get() <= 0 ? BREAK : CONTINUE;
        });
    }

    static Task any(Task... tasks) {
        Box<Boolean> done = Box.of(false);

        return Task.of(() -> {
            for (Task t : tasks) {
                if (t.run() == BREAK) done.set(true);
            }
            return done.get() ? BREAK : CONTINUE;
        });
    }

    static Task until(Supplier<Boolean> f) {
        return Task.of(() -> f.get() ? BREAK : CONTINUE);
    }

    static Task pool(Pool pool) {
        return Task.of(() -> {
            pool.tasks.forEach((name, task) -> {
                if (task.run() == BREAK) {
                    pool.tasks.remove(name);
                }
            });
            return CONTINUE;
        });
    }

    public static class Pool {
        private final HashMap<String, Task> tasks = new HashMap<>();

        public void tryAdd(String name, Task task) {
            if (has(name)) return;
            tasks.put(name, task);
        }

        public void forceAdd(String name, Task task) {
            Task old = tasks.get(name);
            if (old != null) {
                old.cancel();
            }
            tasks.put(name, task);
        }

        public void remove(String name) {
            Task removed = tasks.remove(name);
            if (removed != null) removed.cancel();
        }

        public boolean has(String name) {
            return tasks.containsKey(name);
        }
    }
}
