package org.firstinspires.ftc.teamcode;

import com.qualcomm.hardware.limelightvision.LLResult;
import com.qualcomm.hardware.limelightvision.LLResultTypes;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;

import java.util.function.Supplier;

public class Robot {
    public final Drivetrain drivetrain;
    public final Odometry odometry;
    public final Intake intake;
    public final Launcher launcher;
    public final Camera camera;
    public final MotifLedStrip motifLeds;
    public final SpindexerBallTracker ballTracker;

    public final Box.Int motif = Box.Int.of(0);

    private final LinearOpMode opMode;

    public final Task.Pool pool = new Task.Pool();

    public Robot(LinearOpMode opMode, double startingAngle) {
        this.opMode = opMode;

        // Odometry can be started early
        odometry = new Odometry(opMode);
        odometry.reset(0, 0, startingAngle);

        opMode.waitForStart();

        // Init these when start is pressed
        drivetrain = new Drivetrain(opMode);
        intake = new Intake(opMode);
        launcher = new Launcher(opMode, this);
        camera = new Camera(opMode);
        motifLeds = new MotifLedStrip(opMode);
        ballTracker = new SpindexerBallTracker();
    }

    private final static Task.ControlFlow CONTINUE = Task.CONTINUE;
    private final static Task.ControlFlow BREAK = Task.BREAK;

    public void runTask(Task task) {
        while (opMode.opModeIsActive()) {
            odometry.update();
            Task.ControlFlow result = task.run();
            odometry.log();
            launcher.flywheelDisplayRpm();
            launcher.lockReport();
            opMode.telemetry.addData("Motif", motif.get());
            opMode.telemetry.addData("Spin index", launcher.spindexerGetIndex());
            opMode.telemetry.addData("Active tasks", pool.activeTaskNames());
//            opMode.telemetry.addLine("===== Log =====");
//            opMode.telemetry.addLine(pool.getDebugLog());
            opMode.telemetry.update();

            if (result == BREAK) break;
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
            // double dt = targetAngle - (currentAngle * DEG_TO_RAD) + Math.PI;
            double dt = targetAngle + (currentAngle * DEG_TO_RAD) + Math.PI;

            double cos = Math.cos(dt);
            double sin = Math.sin(dt);

            double distance = Util.magnitude(dx, dy);

            drivetrain.setFactor(power);

            if (distance < DAMP_THRESHOLD / 4) {
                drivetrain.move(0, 0, 0);
                return BREAK;
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
        Box<Task> delegate = Box.of(null);

        return Task.of(() -> {
            double x = odometry.posX() + dx;
            double y = odometry.posY() + dy;
            delegate.set(this.moveTo(x, y, power));
        }, () -> delegate.get().run());
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
                return BREAK;
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

    public Task faceDynamicDir(Supplier<Double> f, double power) {
        final double DAMP_THRESHOLD = 30 * power;
        return Task.of(() -> {
            double angle = f.get();
            double current = odometry.directionRaw();
            double diff = angle - current;
            long normalized = Math.floorMod((long) diff, 360);
            if (normalized > 180) normalized -= 360;
            normalized *= -1;

            opMode.telemetry.addData("Normalized angle:", normalized);

            drivetrain.setFactor(power);
            if (Math.abs(normalized) < DAMP_THRESHOLD / 4) {
                drivetrain.move(0, 0, 0);
                // return BREAK;
                return CONTINUE;
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

    public Double goalDirection() {
        Coordinate2D target = camera.fiducialGetTarget();
        if (target == null) return null;

        double error = target.x();
        double adjust = this.launcher.getLaunchProfile() == Launcher.LaunchProfile.FAR ? -4 : 0;

        return this.odometry.directionNormalized() - error + adjust;
    }

    public Task faceClosestGoal() {
        // double range = 5;

        return Task.lazy(() -> {
            Double dir = goalDirection();
            if (dir == null) return Task.of(() -> Task.ControlFlow.BREAK);
            return faceDir(dir, 0.2);
        });
    }

    public Task liveFaceGoal() {
        // return Task.sequence(
        //         Task.any(
        //             faceClosestGoal(),
        //             Task.pause(250)
        //         ),
        //         Task.once(() -> pool.forceAdd("LiveFaceGoal", liveFaceGoal()))
        // );
        Task delegate = faceDynamicDir(() -> {
            Double dir = goalDirection();
            if (dir == null) return odometry.directionNormalized();
            else return dir;
        }, 0.2);

        return Task.of(() -> {}, () -> { delegate.run(); return CONTINUE; }, drivetrain::stop);
    }

    private static final double[] DISTANCES = new double[] { 0.34885527085, 0.648151216924, 0.992018144995, 1.66760307028 };
    private static final double[] POWERS = new double[] { 1700, 1700, 1860, 2300 };
    private final double[] GARAGE_DOOR_POSITIONS = new double[] { 0.7294, 0.7294, 0.7294, 0.3489 };

    public double[] findIdealLauncherSettings(Coordinate2D c) {
        double dist = Math.sqrt(Math.pow((-1.3 - c.x()), 2) + Math.pow(1.19 - c.y(), 2));

        int chosenIdx = DISTANCES.length - 1;
        for (int i = 0; i < DISTANCES.length; i++) {
            if (dist < DISTANCES[i]) {
                chosenIdx = i;
                break;
            }
        }

        return new double[] { POWERS[chosenIdx], GARAGE_DOOR_POSITIONS[chosenIdx] };
    }

    public Task autoPower() {
        return Task.of(() -> {
            Coordinate2D botpose = camera.botposeGetEstimate();
            if (botpose == null) return CONTINUE;

            double[] config = findIdealLauncherSettings(botpose);
            launcher.setLaunchProfile(Launcher.LaunchProfile.DYNAMIC);

            Launcher.DYNAMIC_VELOCITY = config[0];
            Launcher.DYNAMIC_GARAGE_DOOR_POS = config[1];

            return CONTINUE;
        });
    }

    public Task launchOne(int millis) {
        return Task.sequence(
                // Task.until(launcher::flywheelReady),
                launcher.flywheelReadyTimeout(millis),
                Task.once(launcher::feedUp),
                Task.pause(300),
                Task.once(launcher::feedDown),
                Task.pause(300),
                Task.once(launcher::feedUp),
                Task.pause(300),
                Task.once(launcher::feedDown),
                Task.pause(300),
                launcher.addSpinIndexAndWait(2)
        );
    }

    public Task launchMotif(int offset) {

        return Task.sequence(
                // Task.once(() -> launcher.spinSetMode(Launcher.SpinMode.LAUNCH)),
                // Green 0 --> 3
                // Green 1 --> 1
                // Green 2 --> 5
                // TODO: handle different green position

                Task.lazy(() -> launcher.setSpinIndexAndWait(3 - 2 * (this.motif.get() - offset))),
                Task.once(launcher::flywheelStart),
                launchOne(2500),
                launchOne(1500),
                launchOne(1500),
                Task.once(() -> launcher.flywheelRunWithPower(0.7)) // Less time to rev up
        );
    }

    public Task fetchMotif() {
        Task main = Task.sequence(
                Task.once(camera::start),
                Task.pause(250),
                Task.until(() -> {
                    LLResult result = camera.getLatestResult();
                    if (result == null || !result.isValid()) return false;
                    for (LLResultTypes.FiducialResult detection : result.getFiducialResults()) {
                        int id = detection.getFiducialId();
                        if (id >= 21 && id <= 23) {
                            motif.set(id % 20 - 1);
                            return true;
                        }
                    }
                    return false;
                }),
                Task.once(camera::stop)
        );
        return Task.any(main, Task.pause(1000));
    }
}
