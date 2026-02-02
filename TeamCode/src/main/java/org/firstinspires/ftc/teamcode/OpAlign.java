package org.firstinspires.ftc.teamcode;

import com.acmerobotics.dashboard.FtcDashboard;
import com.acmerobotics.dashboard.config.Config;
import com.acmerobotics.dashboard.telemetry.MultipleTelemetry;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.util.ElapsedTime;

import org.firstinspires.ftc.robotcore.external.navigation.DistanceUnit;

import java.util.concurrent.TimeUnit;

@Config
@TeleOp(name = "Self Align")
public class OpAlign extends LinearOpMode {
    public static double P = 0.005, I, D;
    public static int COOLDOWN_TIME = 500;

    private final ElapsedTime timer = new ElapsedTime();
    private final ElapsedTime cooldownTimer = new ElapsedTime(COOLDOWN_TIME);
    private double prevError = Double.NaN;
    private double accumError = 0.0;
    private double calculatePidTerms(double error) {
        double dt = timer.time(TimeUnit.MILLISECONDS) / 1000.0;
        timer.reset();

        if (Double.isNaN(prevError)) prevError = error;
        double derivative = (error - prevError) / dt;
        prevError = error;
        accumError += error * dt;

        telemetry.addData("dE/dt", derivative);

        if (Math.abs(derivative) > 2000) {
            cooldownTimer.reset();
            return 0;
        }

        if (cooldownTimer.time(TimeUnit.MILLISECONDS) < COOLDOWN_TIME) {
            return 0;
        }

        return P * error + I * accumError + D * derivative;
    }

    @Override
    public void runOpMode() {
        this.telemetry = new MultipleTelemetry(telemetry, FtcDashboard.getInstance().getTelemetry());
        Robot r = new Robot(this, 0.0);
        Task t = Task.of(() -> {
            double bias = r.intake.getHorizontalBias();
            telemetry.addData("Bias", bias);

            double power = calculatePidTerms(-bias);
            telemetry.addData("Power", power);

            r.drivetrain.move(power, 0, 0);

//            double abs = Math.abs(bias);

//            if (abs > Intake.ALIGN_TOLERANCE) {
//                double sign = bias / abs;
//                r.drivetrain.move(-sign * 0.3, 0, 0);
//            } else if (abs > Intake.ALIGN_TOLERANCE / 4) {
//                double k = bias / Intake.ALIGN_TOLERANCE;
//                double min = 0.2;
//                double power = -k * 0.3;
//                if (power > 0 && power < min) power = min;
//                else if (power < 0 && power > -min) power = -min;
//                r.drivetrain.move(power, 0, 0);
//            } else {
//                r.drivetrain.stop();
//            }

            return Task.CONTINUE;
        });

        r.runTask(t);
    }
}
