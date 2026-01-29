package org.firstinspires.ftc.teamcode;

import com.acmerobotics.dashboard.config.Config;
import com.qualcomm.hardware.rev.RevColorSensorV3;
import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorEx;
import com.qualcomm.robotcore.hardware.DcMotorSimple;
import com.qualcomm.robotcore.hardware.PIDFCoefficients;
import com.qualcomm.robotcore.hardware.Servo;

import org.firstinspires.ftc.robotcore.external.navigation.DistanceUnit;

@Config
public class Launcher {
    public static double CALI_GARAGE_DOOR_POS = 0.7294;
    public static double CALI_VELOCITY = 1800;
    public static double DYNAMIC_GARAGE_DOOR_POS = 0.7294;
    public static double DYNAMIC_VELOCITY = 1700;
    private final Robot robot;
    private final OpMode opMode;

    private boolean initialized = false;

    /* MOTORS & SERVOS */
    private final DcMotorEx flywheel;
    private final Servo spindexer;
    private final Servo feed;
    private final Servo garageDoor;

    /* LED INDICATORS */
    private final Servo rpmIndicator;
    private final Servo launchBallIndicator;
    private final RevColorSensorV3 colorSensor;

    /* PHYSICAL COMPONENT SETTINGS */
    private final static double LAUNCHER_TARGET_VELOCITY = 1630;
    public static final double FEED_POSITION_UP = 0.4, FEED_POSITION_DOWN = 0.7;

    public Launcher(OpMode opMode, Robot r) {
        robot = r;
        this.opMode = opMode;
        flywheel = opMode.hardwareMap.get(DcMotorEx.class, "launcher/motor");
        spindexer = opMode.hardwareMap.get(Servo.class, "launcher/spin");
        feed = opMode.hardwareMap.get(Servo.class, "launcher/lift");
        garageDoor = opMode.hardwareMap.get(Servo.class, "launcher/garageDoor");
        rpmIndicator = opMode.hardwareMap.get(Servo.class, "launcher/rpmIndicator");
        launchBallIndicator = opMode.hardwareMap.get(Servo.class, "launcher/ballColor");
        colorSensor = opMode.hardwareMap.get(RevColorSensorV3.class, "launcher/color");

        flywheel.setDirection(DcMotorSimple.Direction.REVERSE);
        // motor.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
        flywheel.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        PIDFCoefficients baseline = new PIDFCoefficients(42, 0, 0, 12.247);
        flywheel.setPIDFCoefficients(DcMotor.RunMode.RUN_USING_ENCODER, baseline);

        feedDown();
        spindexerSetIndex(0);
        setLaunchProfile(LaunchProfile.DEFAULT);
    }

    /* LOCK */
    private Lock currentLock = Lock.EMPTY;

    private enum Lock {
        EMPTY,
        SPINDEXER,
        FEED
    }

    public void lockReport() {
        opMode.telemetry.addData("Launcher lock status", currentLock);
    }

    private boolean lockAcquire(Lock which) {
        if (1 + 1 == 2) return true;

        if (currentLock == Lock.EMPTY || currentLock == which) {
            currentLock = which;
            return true;
        }

        return false;
    }

    private void lockRelease() {
        currentLock = Lock.EMPTY;
    }

    private Task lockWaitAndRelease(int millis) {
        return Task.sequence(
                Task.pause(millis),
                Task.once(this::lockRelease),
                Task.once(() -> robot.pool.debugPrintln("[#] lock released"))
        );
    }

    /* FLYWHEEL */
    public boolean flywheelReady() {
        return Math.abs(flywheelGetError()) < 50;
    }

    public Task flywheelReadyTimeout(int millis) {
        return Task.any(
                Task.until(this::flywheelReady),
                Task.pause(millis)
        );
    }

    public void flywheelRunWithPower(double power) {
        spinSetMode(SpinMode.LAUNCH);
        // motor.setVelocity(TARGET_VELOCITY);
        flywheel.setPower(power);
    }

    private double flywheelTargetVelocity() {
        if (launchProfile == LaunchProfile.CALIBRATION) {
            return CALI_VELOCITY;
        } else if (launchProfile == LaunchProfile.DYNAMIC) {
            return DYNAMIC_VELOCITY;
        }

        return launchProfile.velocity;
    }

    private double flywheelGetError() {
        return flywheel.getVelocity() - flywheelTargetVelocity();
    }

    public void flywheelStart() {
        final double P = 180, K_V = 0.0068;
        PIDFCoefficients coefs = new PIDFCoefficients(P, 0, 0, K_V * launchProfile.velocity);
        flywheel.setPIDFCoefficients(DcMotor.RunMode.RUN_USING_ENCODER, coefs);
        if (launchProfile == LaunchProfile.CALIBRATION) {
            flywheel.setVelocity(CALI_VELOCITY);
        }
        else if (launchProfile == LaunchProfile.DYNAMIC) {
            flywheel.setVelocity(DYNAMIC_VELOCITY);
        }
        else {
            flywheel.setVelocity(launchProfile.velocity);
        }
    }

    public void flywheelReverse() {
        flywheelRunWithPower(-0.8);
    }

    public void flywheelStop() {
        flywheel.setVelocity(0.0);
        flywheel.setPower(0.0);
    }

    public double flywheelGetRpm() {
        double ticksPerRev = flywheel.getMotorType().getTicksPerRev();
        double ticksPerSecond = flywheel.getVelocity();
        return Math.abs((ticksPerSecond / ticksPerRev) * 60.0);
    }

    public double flywheelGetVelocity() {
        return flywheel.getVelocity();
    }

    public void flywheelDisplayRpm() {
        double v = flywheel.getVelocity();
        if (v <= 0) {
            rpmIndicator.setPosition(0); // Off
        }
        else if (!flywheelReady()) {
            rpmIndicator.setPosition(0.333); // Orange
        }
        else {
            double blue = 0.611;
            double red = 0.3;
            rpmIndicator.setPosition(launchProfile == LaunchProfile.NEAR ? red : blue);
        }
    }

    /* FEED SERVO */
    public void feedUp() {
        if (!spindexerReadyToLaunch()) return;

        if (!lockAcquire(Lock.FEED)) return;
        feed.setPosition(FEED_POSITION_UP);
    }

    public void feedDown() {
        if (servoIsAtPos(feed, FEED_POSITION_DOWN)) return;

        feed.setPosition(FEED_POSITION_DOWN);
        robot.pool.forceAdd("FeedLockRelease", lockWaitAndRelease(250));
    }

    /* SPINDEXER */
    public double spindexerGetPosition() {
        return spindexer.getPosition();
    }

    private void spindexerSetPosition(double position) {
        position = Util.clamp(position, 0, 1);
        spindexer.setPosition(position);
    }

    private int spindexerIndex = 0;
    private final static double[] spindexerPositions = {
            0.0450, // intake
            0.0767, // launch
            0.1134,
            0.1528,
            0.1850,
            0.2250,
    };

    public int spindexerGetIndex() {
        return spindexerIndex;
    }

    public int spindexerSetIndex(int n) {
        n = (n + 6) % 6;
        int oldIndex = spindexerIndex;
        spindexerIndex = n;

        int diff = Math.abs(spindexerIndex - oldIndex);
        if (initialized && (diff == 0 || !lockAcquire(Lock.SPINDEXER))) return 0;
        initialized = true;

        spindexerSetPosition(spindexerPositions[n]);

        robot.pool.forceAdd("SpindexerLockRelease", lockWaitAndRelease(350 * diff));

        return diff;
    }

    public int spindexerAddIndex(int i) {
        i = i % 6;
        int n = (spindexerIndex + i + 6) % 6;
        return spindexerSetIndex(n);
    }

    public boolean spindexerReadyToLaunch() {
        return spindexerIndex % 2 == 1;
    }

    public boolean spindexerReadyToIntake() {
        return spindexerIndex % 2 == 0;
    }

    public enum SpinMode {
        INTAKE,
        LAUNCH
    }

    private SpinMode spinMode = SpinMode.INTAKE;
    public void spinSetMode(SpinMode mode) {
        spinMode = mode;
        if (!spinIsReadyFor(mode)) {
            robot.pool.debugPrintln("[!] adjust spindexer");
            spindexerAddIndex(1);
        }
    }

    public boolean spinIsReadyFor(SpinMode mode) {
        switch (mode) {
            case INTAKE:
                return spindexerReadyToIntake();
            case LAUNCH:
                return spindexerReadyToLaunch();
            default:
                return false;
        }
    }

    public void spinNext() {
        spindexerAddIndex(spinIsReadyFor(spinMode) ? 2 : 1);
    }

    public void spinPrev() {
        spindexerAddIndex(spinIsReadyFor(spinMode) ? -2 : -1);
    }

    public Task setSpinIndexAndWait(int n) {
        Box.Int diff = Box.Int.of(0);
        return Task.sequence(
                // Task.until(() -> !spindexerLocked),
                Task.once(() -> diff.set(Launcher.this.spindexerSetIndex(n))),
                Task.lazy(() -> Task.pause(300 * diff.get()))
        );
    }

    public Task addSpinIndexAndWait(int i) {
        Box.Int diff = Box.Int.of(0);
        return Task.sequence(
                // Task.until(() -> !spindexerLocked),
                Task.once(() -> diff.set(Launcher.this.spindexerAddIndex(i))),
                Task.lazy(() -> Task.pause(300 * diff.get()))
        );
    }

    /* GARAGE DOOR */
    private static final double GARAGE_POSITION_MAX = 0.7294;
    private static final double GARAGE_POSITION_MIN = 0.2950;
    public static final double GARAGE_POSITION_NEAR = 0.7294;
    public static final double GARAGE_POSITION_FAR = 0.3089;

    public enum LaunchProfile {
        DEFAULT(GARAGE_POSITION_NEAR, 1860),
        NEAR(GARAGE_POSITION_NEAR, 1760),
        FAR(GARAGE_POSITION_FAR, 2300),
        AUTONOMOUS(GARAGE_POSITION_NEAR, 1700),
        AUTONOMOUS_FAR(0.3209, 2240),
        DYNAMIC(DYNAMIC_GARAGE_DOOR_POS, DYNAMIC_VELOCITY),
        CALIBRATION(CALI_GARAGE_DOOR_POS, CALI_VELOCITY);

        private final double garagePos;
        private final double velocity;

        LaunchProfile(double garagePos, double velocity) {
            this.garagePos = garagePos;
            this.velocity = velocity;
        }
    }

    private LaunchProfile launchProfile = LaunchProfile.DEFAULT;

//    public void handleGarageDoorSpecialCases() {
//        if (this.launchProfile == LaunchProfile.CALIBRATION) {
//            garageDoorSetPosition(Util.clamp(CALI_GARAGE_DOOR_POS, GARAGE_POSITION_MIN, GARAGE_POSITION_MAX));
//        } else if (this.launchProfile == LaunchProfile.DYNAMIC) {
//            garageDoorSetPosition(Util.clamp(DYNAMIC_GARAGE_DOOR_POS, GARAGE_POSITION_MIN, GARAGE_POSITION_MAX));
//        }
//    }

    public void setLaunchProfile(LaunchProfile profile) {
        if (this.launchProfile == LaunchProfile.DYNAMIC) {
            garageDoorSetPosition(Util.clamp(DYNAMIC_GARAGE_DOOR_POS, GARAGE_POSITION_MIN, GARAGE_POSITION_MAX));
        } else {
            garageDoorSetPosition(profile.garagePos);
        }
        this.launchProfile = profile;
    }

    public double garageDoorGetPosition() {
        return garageDoor.getPosition();
    }

    public void garageDoorSetPosition(double pos) {
        garageDoor.setPosition(Util.clamp(pos, GARAGE_POSITION_MIN, GARAGE_POSITION_MAX));
    }

    public void garageDoorRotate(double dx) {
        double newPos = garageDoorGetPosition() + dx;
        garageDoorSetPosition(newPos);
    }

    /* COLOR SENSOR */
    public enum ColorSensorDetection {
        PURPLE,
        GREEN,
        EMPTY,
    }

    public ColorSensorDetection colorSensorGetDetection() {
//        if (colorSensorGetProximity() > 60) {
//            return ColorSensorDetection.EMPTY;
//        }
//        else {
//            if (colorSensorGetColors().v < 0.4) {
//                return ColorSensorDetection.EMPTY;
//            }
//
//            if (colorSensorGetColors().h < 185) {
//                return ColorSensorDetection.GREEN;
//            }
//            else {
//                return ColorSensorDetection.PURPLE;
//            }
//        }

//        if (colorSensorGetProximity() > 45) {
//            return ColorSensorDetection.EMPTY;
//        }
//        else {
//            if (colorSensorGetColors().h > 180) {
//                return ColorSensorDetection.PURPLE;
//            }
//
//            if
//        }
        Hsv color = colorSensorGetColors();
        double proximity = colorSensorGetProximity();
        return ColorSensorDetectionData.identify(color.h, color.s, color.v, proximity);
    }

    private enum ColorSensorDetectionData {
        GREEN(159, 0.61, 0.57, 43),
        PURPLE(210.46, 0.433, 0.59, 45),
        EMPTY(150.77, 0.386, 0.40, 60);

        private double h;
        private double s;
        private double v;
        private double p;

        ColorSensorDetectionData(double h, double s, double v, double p) {
            this.h = h;
            this.s = s;
            this.v = v;
            this.p = p;
        }

        private ColorSensorDetection toDetection() {
            switch (this) {
                case PURPLE:
                    return ColorSensorDetection.PURPLE;
                case GREEN:
                    return ColorSensorDetection.GREEN;
                case EMPTY:
                default:
                    return ColorSensorDetection.EMPTY;
            }
        }

        private static ColorSensorDetection identify(double h, double s, double v, double p) {
            double closest = Double.POSITIVE_INFINITY;
            ColorSensorDetection result = ColorSensorDetection.EMPTY;
            for (ColorSensorDetectionData c : values()) {
                double distanceSq = Util.sq(c.h - h) + Util.sq(c.s - s) + Util.sq(c.v - v) + Util.sq(c.p - p);
                if (distanceSq < closest) {
                    closest = distanceSq;
                    result = c.toDetection();
                }
            }
            return result;
        }
    }

    public void colorSensorDisplayDetection() {
        ColorSensorDetection detected = colorSensorGetDetection();

        switch (detected) {
            case GREEN:
                launchBallIndicator.setPosition(0.5);
                break;
            case PURPLE:
                launchBallIndicator.setPosition(0.720);
                break;
            case EMPTY:
            default:
                launchBallIndicator.setPosition(0);
        }
    }

    public Hsv colorSensorGetColors() {
        return new Rgb((short) colorSensor.red(), (short) colorSensor.green(), (short) colorSensor.blue()).toHsv();
    }

    public double colorSensorGetProximity() {
        return colorSensor.getDistance(DistanceUnit.MM);
    }

    private boolean servoIsAtPos(Servo servo, double position) {
        return Math.abs(servo.getPosition() - position) < 0.01;
    }

    public LaunchProfile getLaunchProfile() {
        return launchProfile;
    }
}

/*
Launcher LED:
Yellow: Revving
Blue: Ready

Spindexer LED:
Red: No ball
Green: Green ball ready to launch
Purple: Green but Purple ball

Color Sensor Data:
GREEN: 160, 0.63, 0.60; 44 mm
PURPLE: 212, 0.44, 0.56; 44 mm
BLANK: 170, 0.43, 0.28; 64 mm

Color Sensor Data NEWNEWNEWNEW:
GREEN: 160, 0.59, 0.56; 35 ~ 40 mm
PURPLE: 205.26, 0.43, 0.69; 35 ~ 40 mm
BLANK: 166 +- 2, 0.44, 0.45; 49 mm

 */

/* Christopher was here */