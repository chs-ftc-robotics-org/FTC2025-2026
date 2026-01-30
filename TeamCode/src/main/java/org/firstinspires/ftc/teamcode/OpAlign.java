package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;

import org.firstinspires.ftc.robotcore.external.navigation.DistanceUnit;

@TeleOp(name = "Self align")
public class OpAlign extends LinearOpMode {
    @Override
    public void runOpMode() {
        Robot r = new Robot(this, 0.0);
        Task t = Task.of(() -> {
            double bias = r.intake.getHorizontalBias();
            telemetry.addData("Bias", bias);

            double abs = Math.abs(bias);

            if (abs > Intake.ALIGN_TOLERANCE) {
                double sign = bias / abs;
                r.drivetrain.move(-sign * 0.3, 0, 0);
            } else if (abs > Intake.ALIGN_TOLERANCE / 4) {
                double k = bias / Intake.ALIGN_TOLERANCE;
                double min = 0.2;
                double power = -k * 0.3;
                if (power > 0 && power < min) power = min;
                else if (power < 0 && power > -min) power = -min;
                r.drivetrain.move(power, 0, 0);
            } else {
                r.drivetrain.stop();
            }

            return Task.CONTINUE;
        });

        r.runTask(t);
    }
}
