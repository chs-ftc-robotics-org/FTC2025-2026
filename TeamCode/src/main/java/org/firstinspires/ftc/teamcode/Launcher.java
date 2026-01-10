package org.firstinspires.ftc.teamcode;

import com.qualcomm.hardware.rev.RevColorSensorV3;
import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorEx;
import com.qualcomm.robotcore.hardware.DcMotorSimple;
import com.qualcomm.robotcore.hardware.PIDFCoefficients;
import com.qualcomm.robotcore.hardware.Servo;

import org.firstinspires.ftc.robotcore.external.navigation.DistanceUnit;

public class Launcher {
    private final DcMotorEx motor;
    private final Servo spin;
    private final Servo lift;
    private final Servo garageDoor;
    private final Servo rpmIndicator;
    private final Servo launchBallIndicator;
    private final RevColorSensorV3 colorSensor;

    private final static double TARGET_VELOCITY = 1630;
    private final OpMode opMode;
    public static final double LIFT_POSITION_UP = 0.4, LIFT_POSITION_DOWN = 0.7;
    public static final double FLYWHEEL_POWER_NEAR = 0.78;
    public static final double FLYWHEEL_POWER_FAR = 1.0;

    public static final double SPIN_OFFSET = 0.05;

    public Launcher(OpMode opMode) {
        this.opMode = opMode;
        motor = opMode.hardwareMap.get(DcMotorEx.class, "launcher/motor");
        spin = opMode.hardwareMap.get(Servo.class, "launcher/spin");
        lift = opMode.hardwareMap.get(Servo.class, "launcher/lift");
        garageDoor = opMode.hardwareMap.get(Servo.class, "launcher/garageDoor");
        rpmIndicator = opMode.hardwareMap.get(Servo.class, "launcher/rpmIndicator");
        launchBallIndicator = opMode.hardwareMap.get(Servo.class, "launcher/ballColor");
        colorSensor = opMode.hardwareMap.get(RevColorSensorV3.class, "color");

        motor.setDirection(DcMotorSimple.Direction.REVERSE);
        // motor.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
        motor.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        PIDFCoefficients baseline = new PIDFCoefficients(42, 0, 0, 12.247);
        motor.setPIDFCoefficients(DcMotor.RunMode.RUN_USING_ENCODER, baseline);

        liftDown();
        spinSetIndex(0);
        setLaunchProfile(LaunchProfile.DEFAULT);

        // raiseIdle();
        // setFeedPosition(FeedPosition.IDLE);
    }

    public void stopFlywheel() {
        // motor.setVelocity(0);
        motor.setPower(0);
    }

    public void reverseFlywheel() {
        // motor.setVelocity(-1000);
        motor.setPower(-0.8);
    }

    public void startFlywheel(double power) {
        // motor.setVelocity(TARGET_VELOCITY);
        motor.setPower(power);
    }

    public void flywheelStart() {
        motor.setPower(launchProfile.power);
    }

    public boolean flywheelReadyOld() {
        opMode.telemetry.addData("Velocity", motor.getVelocity());
        opMode.telemetry.addData("Error", TARGET_VELOCITY - motor.getVelocity());
        return Math.abs(motor.getVelocity() - TARGET_VELOCITY) < 60;
    }

    public boolean flywheelReady() {
        double rpm = getRpm();
        return Math.abs(rpm - launchProfile.rpm) < 2;
    }

    private boolean servoIsAtPos(Servo servo, double pos) {
        return Math.abs(servo.getPosition() - pos) < 0.01;
    }

    private Task servoToPosition(Servo servo, double position) {
        Task check = Task.until(() -> servoIsAtPos(servo, position));
        return Task.of(() -> servo.setPosition(position), check::run);
    }

    private boolean liftIsUp;

    public void liftUp() {
        if (!readyToLift() && !spinIsMoving()) return;
        liftIsUp = true;
        lift.setPosition(LIFT_POSITION_UP);
    }

    public void liftDown() {
        lift.setPosition(LIFT_POSITION_DOWN);
        liftIsUp = false;
    }

    public boolean getLiftUp() {
        return liftIsUp;
    }

    private double spinPosition;
    public void spinSetPosition(double position) {
        if (liftIsUp) return;

        position = Util.clamp(position, 0, 1);
        spin.setPosition(position);
        spinPosition = position;
    }

    public void spinRotate(double x) {
        spinSetPosition(spinPosition + x);
    }

    public double spinGetPosition() {
        return spin.getPosition();
    }

    private final static double[] spinPositions = {
         0.0450, // intake
         0.0767, // launch
         0.1134,
         0.1528,
         0.1850,
         0.2250,
    };

    private int spinIndex;
    public int spinSetIndex(int n) {
        n = (n + 6) % 6;
        int oldIndex = spinIndex;
        spinIndex = n;

        spinSetPosition(spinPositions[n]);
        return Math.abs(spinIndex - oldIndex);
    }

    public int spinGetIndex() {
        return spinIndex;
    }

    public int spinAddIndex(int i) {
        i = i % 6;
        int n = (spinIndex + i + 6) % 6;
        return spinSetIndex(n);
    }

    public double getRpm() {
        double ticksPerRev = motor.getMotorType().getTicksPerRev();
        double ticksPerSecond = motor.getVelocity();
        return Math.abs((ticksPerSecond / ticksPerRev) * 60.0);
    }

    public void displayRpmStatus() {
        double v = motor.getVelocity();
        if (v <= 0) {
            rpmIndicator.setPosition(0); // Off
        }
        else if (v < TARGET_VELOCITY - 30) {
            rpmIndicator.setPosition(0.333); // Orange
        }
        else {
            double blue = 0.611;
            double red = 0.3;
            rpmIndicator.setPosition(launchProfile == LaunchProfile.NEAR ? red : blue); // Blue
        }
    }

    public void displayBallStatus() {
        BallDetection detected = getDetectedBall();
        setBallStatusDisplay(readyToLift() ? detected : BallDetection.EMPTY);
    }

    public void setBallStatusDisplay(BallDetection b) {
        if (b == BallDetection.EMPTY) {
            launchBallIndicator.setPosition(0);
        }
        else if (b == BallDetection.GREEN) {
            launchBallIndicator.setPosition(0.5);
        }
        else if (b == BallDetection.PURPLE) {
            launchBallIndicator.setPosition(0.720);
        }
    }

    public boolean readyToLift() {
        return spinIndex % 2 == 1;
    }

    public boolean readyToIntake() {
        return spinIndex % 2 == 0;
    }

    private static final double GARAGE_POSITION_MAX = 0.7294;
    private static final double GARAGE_POSITION_MIN = 0.2950;

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

    public Hsv getDetectedColorValues() {
        return new Rgb((short) colorSensor.red(), (short) colorSensor.green(), (short) colorSensor.blue()).toHsv();
    }

    public double getProximity() {
        return colorSensor.getDistance(DistanceUnit.MM);
    }

    public enum BallDetection {
        PURPLE,
        GREEN,
        EMPTY,
    }

    public BallDetection getDetectedBall() {
        if (getProximity() > 60) {
            return BallDetection.EMPTY;
        }
        else {
            if (getDetectedColorValues().v < 0.4) {
                return BallDetection.EMPTY;
            }

            if (getDetectedColorValues().h < 185) {
                return BallDetection.GREEN;
            }
            else {
                return BallDetection.PURPLE;
            }
        }
    }

    public enum SpinMode {
        INTAKE,
        LAUNCH
    }

    private SpinMode spinMode = SpinMode.INTAKE;
    public void spinSetMode(SpinMode mode) {
        spinMode = mode;
        if (!spinIsReadyFor(mode)) spinAddIndex(1);
    }

    public boolean spinIsReadyFor(SpinMode mode) {
        switch (mode) {
            case INTAKE:
                return readyToIntake();
            case LAUNCH:
                return readyToLift();
            default:
                return false;
        }
    }

    public void spinNext() {
        spinAddIndex(spinIsReadyFor(spinMode) ? 2 : 1);
    }

    public void spinPrev() {
        spinAddIndex(spinIsReadyFor(spinMode) ? -2 : -1);
    }

    public boolean spinIsMoving() {
        return Math.abs(spin.getPosition() - spinPositions[spinIndex]) > 0.001;
    }

    // GREEN: 160, 0.63, 0.60; 44 mm
    // PURPLE: 212, 0.44, 0.56; 44 mm
    // BLANK: 170, 0.43, 0.28; 64 mm

    private static final double GARAGE_POSITION_NEAR = 0.7294;
    private static final double GARAGE_POSITION_FAR = 0.3289;

    public enum LaunchProfile {
        DEFAULT(GARAGE_POSITION_NEAR, 0.80, 40),
        NEAR(GARAGE_POSITION_NEAR, 0.75, 37),
        FAR(GARAGE_POSITION_FAR, 1.0, 50),
        AUTONOMOUS(GARAGE_POSITION_NEAR, 0.70, 34),
        AUTONOMOUS_FAR(0.3189, 0.95, 47);

        private final double garagePos;
        private final double power;
        private final double rpm;

        LaunchProfile(double garagePos, double power, double rpm) {
            this.garagePos = garagePos;
            this.power = power;
            this.rpm = rpm;
        }
    }

    private LaunchProfile launchProfile = LaunchProfile.DEFAULT;
    public void setLaunchProfile(LaunchProfile profile) {
        garageDoorSetPosition(profile.garagePos);
        this.launchProfile = profile;
    }

    public Task setSpinIndexAndWait(int n) {
        Box.Int diff = Box.Int.of(0);
        return Task.sequence(
                Task.once(() -> diff.set(Launcher.this.spinSetIndex(n))),
                Task.lazy(() -> Task.pause(300 * diff.get()))
        );
    }

    public Task addSpinIndexAndWait(int i) {
        Box.Int diff = Box.Int.of(0);
        return Task.sequence(
                Task.once(() -> diff.set(Launcher.this.spinAddIndex(i))),
                Task.lazy(() -> Task.pause(300 * diff.get()))
        );
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
 */
