package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.util.ElapsedTime;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.function.Supplier;
import java.util.stream.Collectors;

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

    static Task lazy(Supplier<Task> f) {
        Box<Task> delegate = Box.of(null);
        return Task.of(() -> delegate.set(f.get()), () -> delegate.get().run());
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
            List<Pool.Diff> changes = pool.pendingChanges;
            pool.tasks.forEach((name, task) -> {
                if (task.run() == BREAK) {
                    changes.add(Pool.Diff.remove(name));
                }
            });
            for (Pool.Diff diff : changes) switch (diff.kind) {
                case REMOVE:
                    pool.unsafeRemove(diff.name);
                    break;
                case TRY_ADD:
                    pool.unsafeTryAdd(diff.name, diff.task);
                    break;
                case FORCE_ADD:
                    pool.unsafeForceAdd(diff.name, diff.task);
                    break;
            }
            changes.clear();
            return CONTINUE;
        });
    }

    public static class Pool {
        private enum DiffKind { TRY_ADD, FORCE_ADD, REMOVE }

        private static class Diff {
            public final DiffKind kind;
            public final String name;
            public final Task task;

            private Diff(DiffKind kind, String name, Task task) {
                this.kind = kind;
                this.name = name;
                this.task = task;
            }

            public static Diff tryAdd(String name, Task task) {
                return new Diff(DiffKind.TRY_ADD, name, task);
            }

            public static Diff forceAdd(String name, Task task) {
                return new Diff(DiffKind.FORCE_ADD, name, task);
            }

            public static Diff remove(String name) {
                return new Diff(DiffKind.REMOVE, name, null);
            }
        }

        private final HashMap<String, Task> tasks = new HashMap<>();
        private final List<Diff> pendingChanges = new ArrayList<>();
        private StringBuilder debugLog = new StringBuilder();

        public void tryAdd(String name, Task task) {
            pendingChanges.add(Diff.tryAdd(name, task));
        }

        public void forceAdd(String name, Task task) {
            pendingChanges.add(Diff.forceAdd(name, task));
        }

        public void remove(String name) {
            pendingChanges.add(Diff.remove(name));
        }

        public boolean has(String name) {
            return tasks.containsKey(name);
        }

        /// IMPORTANT: may not be called within a Task that's inside of this pool
        private void unsafeTryAdd(String name, Task task) {
            if (this.has(name)) return;
            tasks.put(name, task);
            debugPrintln("[!] try add " + name);
        }

        /// IMPORTANT: may not be called within a Task that's inside of this pool
        private void unsafeForceAdd(String name, Task task) {
            unsafeRemove(name);
            tasks.put(name, task);
            debugPrintln("[!] force add " + name);
        }

        /// IMPORTANT: may not be called within a Task that's inside of this pool
        private void unsafeRemove(String name) {
            Task removed = tasks.remove(name);
            if (removed != null) removed.cancel();
            debugPrintln("[!] remove " + name);
        }

        public void debugPrintln(String s) {
            debugLog.append(s);
            debugLog.append('\n');
        }

        public void clearDebugLog() {
            debugLog = new StringBuilder();
        }

        public String getDebugLog() {
            return debugLog.toString();
        }

        public List<String> activeTaskNames() {
            return new ArrayList<>(tasks.keySet());
        }
    }
}
