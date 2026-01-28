package org.firstinspires.ftc.teamcode;

import com.qualcomm.hardware.limelightvision.LLResult;
import com.qualcomm.hardware.limelightvision.LLResultTypes;
import com.qualcomm.hardware.limelightvision.Limelight3A;
import com.qualcomm.robotcore.eventloop.opmode.OpMode;

import org.firstinspires.ftc.robotcore.external.navigation.Pose3D;

public class Camera {
    private final Limelight3A limelight;
    private final OpMode opMode;

    public Camera(OpMode opMode) {
        limelight = opMode.hardwareMap.get(Limelight3A.class, "camera");
        limelight.setPollRateHz(100);
        limelight.pipelineSwitch(0);

        this.opMode = opMode;
    }

    public void start() {
        limelight.start();
    }

    public void stop() {
        limelight.stop();
    }

    public void report() {
        LLResult result = limelight.getLatestResult();
        if (result == null || !result.isValid()) return;
        for (LLResultTypes.FiducialResult fiducial : result.getFiducialResults()) {
            opMode.telemetry.addLine(fiducialSummary(fiducial));
        }
    }

    private String fiducialSummary(LLResultTypes.FiducialResult result) {
        Pose3D pose = result.getRobotPoseTargetSpace();

        StringBuilder s = new StringBuilder();
        s.append("AprilTag {\n");
        putProp(s, "Id", result.getFiducialId());
        putProp(s, "TargetX", result.getTargetXDegrees());
        putProp(s, "TargetY", result.getTargetYDegrees());
        putProp(s, "PoseX", pose.getPosition().x);
        putProp(s, "PoseY", pose.getPosition().y);
        s.append('}');

        return s.toString();
    }

    public Coordinate2D fiducialGetTarget() {
        LLResult r = limelight.getLatestResult();
        if (r == null || !r.isValid()) return null;
        for (LLResultTypes.FiducialResult f : r.getFiducialResults()) {
            if (f.getFiducialId() != 20 && f.getFiducialId() != 24) continue;

            return new Coordinate2D(f.getTargetXDegrees(), f.getTargetYDegrees());
            // return new double[] { f.getTargetXDegrees(), f.getTargetYDegrees() };
        }

        return null;
    }

    public Coordinate2D botposeGetEstimate() {
        LLResult result = getLatestResult();
        if (result != null && result.isValid()) {
            Pose3D botpose = result.getBotpose();
            if (botpose != null) {
                return new Coordinate2D(botpose.getPosition().x, botpose.getPosition().y);
                // telemetry.addData("MT1 Location", "(" + x + ", " + y + ")");
            }
        }

        return null;
    }

    public LLResult getLatestResult() {
        return limelight.getLatestResult();
    }

    private static void putProp(StringBuilder s, String prop, Object val) {
        s.append('\t');
        s.append(prop);
        s.append(": ");
        s.append(val);
        s.append('\n');
    }
}
