package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;

public class Robot {
    public final Drivetrain drivetrain;
    public final Odometry odometry;
    public final Intake intake;
    public final Launcher launcher;

    private LinearOpMode opMode;

    public Robot(LinearOpMode opMode) {
        this.opMode = opMode;

        drivetrain = new Drivetrain(opMode);
        odometry = new Odometry(opMode);
        intake = new Intake(opMode);
        launcher = new Launcher(opMode);

        opMode.waitForStart();
    }

    private final static Task.Running.Result CONTINUE = Task.CONTINUE;
    private final static Task.Running.Result FINISH = Task.FINISH;

    public void runTask(Task task) {
        Task.Running r = task.spawn();
        while (opMode.opModeIsActive()) {
            odometry.update();
            Task.Running.Result result = r.run();
            odometry.log();
            launcher.displayStatus();
            opMode.telemetry.update();

            if (result == FINISH) break;
        }
    }

    private static final double DEG_TO_RAD = Math.PI / 180;
    private static final double MIN_POWER = 0.2;

    public Task moveTo(double x, double y, double power) {
        final double DAMP_THRESHOLD = 5 * power;
        return Task.of(() -> {
            opMode.telemetry.addData("Moving to", "(%f, %f)", x, y);

            double cx = odometry.posX();
            double cy = odometry.posY();

            double dx = x - cx;
            double dy = y - cy;

            double currentAngle = odometry.directionRaw();
            double targetAngle = Math.atan2(dy, dx);
            double dt = targetAngle - (currentAngle * DEG_TO_RAD) + Math.PI;

            double cos = Math.cos(dt);
            double sin = Math.sin(dt);

            double distance = Util.magnitude(dx, dy);

            drivetrain.setFactor(power);

            if (distance < DAMP_THRESHOLD / 4) {
                drivetrain.move(0, 0, 0);
                return FINISH;
            }

            if (distance < DAMP_THRESHOLD) {
                double factor = distance / DAMP_THRESHOLD;
                double mainPower = Math.max(Math.abs(cos), Math.abs(sin));
                if (mainPower * factor < MIN_POWER) {
                    factor = MIN_POWER / mainPower;
                }
                drivetrain.move(cos * factor , sin * factor, 0);
                return CONTINUE;
            }

            drivetrain.move(cos, sin, 0);

            return CONTINUE;
        });
    }

    public Task moveBy(double dx, double dy, double power) {
        return () -> {
            Box<Task.Running> running = Box.of(null);

            return () -> {
                if (running.get() == null) {
                    double x = odometry.posX() + dx;
                    double y = odometry.posY() + dy;
                    running.set(moveTo(x, y, power).spawn());
                    return CONTINUE;
                }
                return running.get().run();
            };

        };
    }

    public Task faceDir(double angle, double power) {
        final double DAMP_THRESHOLD = 30 * power;
        return Task.of(() -> {
            double current = odometry.directionRaw();
            double diff = angle - current;
            long normalized = Math.floorMod((long) diff, 360);
            if (normalized > 180) normalized -= 360;
            normalized *= -1;

            opMode.telemetry.addData("Normalized angle:", normalized);

            drivetrain.setFactor(power);
            if (Math.abs(normalized) < DAMP_THRESHOLD / 4) {
                drivetrain.move(0, 0, 0);
                return FINISH;
            }
            if (Math.abs(normalized) < DAMP_THRESHOLD) {
                double factor = normalized / DAMP_THRESHOLD;
                if (Math.abs(factor) < MIN_POWER) factor *= MIN_POWER / Math.abs(factor);
                drivetrain.move(0, 0, factor);
                return CONTINUE;
            }
            if (normalized > 0) drivetrain.move(0, 0, 1);
            else if (normalized < 0) drivetrain.move(0, 0, -1);
            return CONTINUE;
        });
    }

    private Task prepLauncher(double rpm) {
        return Task.until(() -> launcher.getRpm() >= rpm);
    }

    public Task launchSingle() {
        return Task.sequence(
                Task.once(launcher::resetFeed),
                Task.once(() -> launcher.start(Launcher.BASELINE_POWER)),
                prepLauncher(Launcher.IDEAL_RPM + 3),
                Task.once(launcher::lockFeed),
                Task.pause(1500),
                prepLauncher(Launcher.IDEAL_RPM),
                Task.once(launcher::pushFeed),
                Task.until(() -> launcher.isAtPosition(Launcher.PUSH_LOCATION)),
                Task.pause(750),
                Task.once(() -> {
                    launcher.stop();
                    launcher.resetFeed();
                })
        );
    }

    private Task launchLastTwo() {
        return Task.sequence(
                Task.once(launcher::resetFeed),
                Task.once(() -> launcher.start(Launcher.BASELINE_POWER)),
                prepLauncher(Launcher.IDEAL_RPM + 3),
                Task.once(launcher::lockFeed),
                Task.pause(1500),
                prepLauncher(Launcher.IDEAL_RPM + 1.5),
                Task.once(launcher::pushFeed),
                Task.until(() -> launcher.isAtPosition(Launcher.PUSH_LOCATION)),
                Task.pause(750),
                Task.once(() -> {
                    launcher.stop();
                    launcher.resetFeed();
                })
        );
    }

    public Task launchAll() {
        return Task.sequence(
                Task.once(launcher::resetFeed),
                Task.once(() -> launcher.start(Launcher.BASELINE_POWER + 0.1)),
                prepLauncher(Launcher.IDEAL_RPM + 3),
                Task.once(intake::start),
                Task.pause(250),
                Task.once(intake::stop),
                Task.once(launcher::stop),
                Task.pause(500),
                Task.once(intake::start),
                Task.pause(500),
                Task.once(intake::stop),
                launchLastTwo()
        );
    }
}
