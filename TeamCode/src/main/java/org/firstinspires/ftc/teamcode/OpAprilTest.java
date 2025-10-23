package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;

import org.firstinspires.ftc.robotcore.external.hardware.camera.CameraName;
import org.firstinspires.ftc.vision.VisionPortal;
import org.firstinspires.ftc.vision.apriltag.AprilTagDetection;
import org.firstinspires.ftc.vision.apriltag.AprilTagProcessor;

import java.util.List;
import java.util.Locale;

@SuppressWarnings("unused")
@Autonomous(name = "April Tag Test")
public class OpAprilTest extends LinearOpMode {
    @Override
    public void runOpMode() {
        CameraName camera = hardwareMap.get(CameraName.class, "ctl/misc0");
        AprilTagProcessor aprilProcessor = AprilTagProcessor.easyCreateWithDefaults();
        @SuppressWarnings("unused")
        VisionPortal visionPortal = VisionPortal.easyCreateWithDefaults(camera, aprilProcessor);

        waitForStart();
        while (opModeIsActive()) {
            List<AprilTagDetection> detectedTags = aprilProcessor.getDetections();
            if (!detectedTags.isEmpty()) {
                telemetry.clearAll();
                for (AprilTagDetection tag : detectedTags) {
                    telemetry.addData("Detected april tag:", displayTagInfo(tag));
                }
                telemetry.update();
            }
        }

    }

    private static String displayTagInfo(AprilTagDetection tag) {
        return String.format(
                Locale.ENGLISH,
                "{ id: %d, pos: (%f, %f) }",
                tag.id, tag.center.x, tag.center.y);
    }
}
