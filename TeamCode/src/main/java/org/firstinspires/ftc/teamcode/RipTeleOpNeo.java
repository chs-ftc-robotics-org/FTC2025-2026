package org.firstinspires.ftc.teamcode;

import com.qualcomm.hardware.rev.RevColorSensorV3;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.Servo;

import org.firstinspires.ftc.robotcore.external.navigation.DistanceUnit;

@TeleOp(name = "RIP TeleOp [NEW]")
public class RipTeleOpNeo extends LinearOpMode {
    @Override
    public void runOpMode() {
        Robot r = new Robot(this);

        // Task.Pool pool = new Task.Pool();

        Task mainControl = Task.of(() -> {
            double axial = gamepad1.left_stick_y;
            double strafe = gamepad1.left_stick_x;
            double turn = gamepad1.right_stick_x;
            r.drivetrain.move(strafe, axial, 0.7 * turn);

//            if (gamepad2.yWasPressed()) {
//                if (pool.has("launch")) {
//                    pool.remove("launch");
//                }
//                else {
//                    pool.tryAdd("launch", launch(r));
//                }
//            }

            if (gamepad1.right_bumper || gamepad2.right_trigger > 0.1) {
                r.intake.start();
            }
            else if (gamepad1.left_bumper || gamepad2.left_trigger > 0.1) {
                r.intake.reverse();
            }
            else {
                r.intake.stop();
            }

            if (gamepad2.y) {
                r.launcher.liftUp();
            }
            else {
                r.launcher.liftDown();
            }
            
            if (gamepad2.dpadLeftWasPressed()) {
                r.launcher.spinAddIndex(1);
            }
            else if (gamepad2.dpadRightWasPressed()) {
                r.launcher.spinAddIndex(-1);
            }
            
            if (gamepad2.dpadUpWasPressed()) {
                r.launcher.spinRotate(0.001);
            }
            else if (gamepad2.dpadDownWasPressed()) {
                r.launcher.spinRotate(-0.001);
            }

            if (gamepad2.right_bumper) {
                r.launcher.startFlywheel(gamepad2.a ? Launcher.FLYWHEEL_POWER_FAR : Launcher.FLYWHEEL_POWER_NEAR);
            }
            else if (gamepad2.left_bumper) {
                r.launcher.reverseFlywheel();
            }
            else {
                r.launcher.stopFlywheel();
            }

            telemetry.addData("Spindexer position", r.launcher.spinGetPosition());

            RevColorSensorV3 cs = r.launcher.getColorSensor();
            telemetry.addData("Detected color", "rgb(%d, %d, %d)", cs.red(), cs.green(), cs.blue());
            telemetry.addData("Proximity", cs.getDistance(DistanceUnit.MM));

            return Task.CONTINUE;
        });

        // r.runTask(Task.all(Task.pool(pool), mainControl));
        r.runTask(mainControl);
    }

//    private Task launch(Robot r) {
//        return Task.sequence(
//                r.launcher.feedToPosition(Launcher.FEED_POSITION_HALF),
//                r.launcher.raiseToPosition(Launcher.RAISE_POSITION_ACTIVE),
//                r.launcher.raiseToPosition(Launcher.RAISE_POSITION_IDLE)
//        );
//    }
}
