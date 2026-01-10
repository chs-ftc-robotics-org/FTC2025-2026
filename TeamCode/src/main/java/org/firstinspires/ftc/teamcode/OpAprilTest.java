package org.firstinspires.ftc.teamcode;

import com.qualcomm.hardware.limelightvision.LLResult;
import com.qualcomm.hardware.limelightvision.LLResultTypes;
import com.qualcomm.hardware.limelightvision.Limelight3A;
import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;

import org.firstinspires.ftc.robotcore.external.hardware.camera.CameraName;
import org.firstinspires.ftc.robotcore.external.navigation.Pose3D;
import org.firstinspires.ftc.robotcore.external.navigation.Position;
import org.firstinspires.ftc.robotcore.external.navigation.YawPitchRollAngles;
import org.firstinspires.ftc.vision.VisionPortal;
import org.firstinspires.ftc.vision.apriltag.AprilTagDetection;
import org.firstinspires.ftc.vision.apriltag.AprilTagProcessor;

import java.util.List;
import java.util.Locale;

@SuppressWarnings("unused")
@Autonomous(name = "April Tag Test")
public class OpAprilTest extends LinearOpMode {
    private Limelight3A webcam;

    @Override
    public void runOpMode() {
        webcam = hardwareMap.get(Limelight3A.class, "camera");
        
        // AprilTagProcessor aprilProcessor = AprilTagProcessor.easyCreateWithDefaults();
        // @SuppressWarnings("unused")
        // VisionPortal visionPortal = VisionPortal.easyCreateWithDefaults(camera, aprilProcessor);

        telemetry.setMsTransmissionInterval(11);

        webcam.pipelineSwitch(0);

        webcam.start();

        waitForStart();
        while (opModeIsActive()) {
            LLResult result = webcam.getLatestResult();

            if (result != null) {
                if (result.isValid()) {
                    for (LLResultTypes.FiducialResult r : result.getFiducialResults()) {
                        telemetry.addLine(displayTagInfo(r));
                    }
                    telemetry.update();
                }
            }
        }



//        waitForStart();
//        while (opModeIsActive()) {
//            List<AprilTagDetection> detectedTags = aprilProcessor.getDetections();
//            if (!detectedTags.isEmpty()) {
//                telemetry.clearAll();
//                for (AprilTagDetection tag : detectedTags) {
//                    telemetry.addData("Detected april tag:", displayTagInfo(tag));
//                }
//                telemetry.update();
//            }
//        }

    }

    private static String displayTagInfo(AprilTagDetection tag) {
        return String.format(
                Locale.ENGLISH,
                "{ id: %d, pos: (%f, %f) }",
                tag.id, tag.center.x, tag.center.y);
    }

    private static String displayTagInfo(LLResultTypes.FiducialResult result) {
        // Position p = pose.getPosition();
        // YawPitchRollAngles orientation = pose.getOrientation();
        return String.format(
                Locale.ENGLISH,
                "{ id: %d, tx: %f, ty: %f }",
                result.getFiducialId(), result.getTargetXDegrees(), result.getTargetYDegrees()
        );
    }
}
