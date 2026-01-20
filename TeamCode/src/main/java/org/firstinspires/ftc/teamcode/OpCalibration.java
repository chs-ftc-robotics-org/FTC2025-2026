package org.firstinspires.ftc.teamcode;

import com.acmerobotics.dashboard.FtcDashboard;
import com.acmerobotics.dashboard.config.Config;

import com.acmerobotics.dashboard.telemetry.MultipleTelemetry;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorEx;
import com.qualcomm.robotcore.hardware.DcMotorSimple;
import com.qualcomm.robotcore.hardware.PIDFCoefficients;
import com.qualcomm.robotcore.util.ElapsedTime;

import java.util.concurrent.TimeUnit;

@Config
@TeleOp(name = "Calibration")
public class OpCalibration extends LinearOpMode {
    public static double P = 180, K_V = 0.0068, TARGET = 800;

    @Override
    public void runOpMode() {
        this.telemetry = new MultipleTelemetry(telemetry, FtcDashboard.getInstance().getTelemetry());

        DcMotorEx flywheel = hardwareMap.get(DcMotorEx.class, "launcher/motor");
        flywheel.setDirection(DcMotorSimple.Direction.REVERSE);
        flywheel.setMode(DcMotor.RunMode.RUN_USING_ENCODER);

        waitForStart();

        PIDFCoefficients coefs = new PIDFCoefficients(P, 0, 0, TARGET * K_V);
        flywheel.setPIDFCoefficients(DcMotor.RunMode.RUN_USING_ENCODER, coefs);
        flywheel.setVelocity(TARGET);

        // 20, 1.25, 15, 0

        while (opModeIsActive()) {
            double currentVelocity = flywheel.getVelocity();
            double error = TARGET - currentVelocity;

            double F = TARGET * K_V;

            coefs = new PIDFCoefficients(P, 0, 0, F);
            flywheel.setPIDFCoefficients(DcMotor.RunMode.RUN_USING_ENCODER, coefs);
            flywheel.setVelocity(TARGET);

            telemetry.addData("Target", TARGET);
            telemetry.addData("Current", currentVelocity);
            telemetry.addData("Error", error);

            telemetry.update();
        }
    }
}
