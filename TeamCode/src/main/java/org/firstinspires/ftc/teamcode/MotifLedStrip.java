package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.hardware.DigitalChannel;

public class MotifLedStrip {
    private final MotifLed one;
    private final MotifLed two;
    private final MotifLed three;

    public MotifLedStrip(LinearOpMode opMode) {
        one = new MotifLed(opMode, "motif/ledOne/red", "motif/ledOne/green");
        two = new MotifLed(opMode, "motif/ledTwo/red", "motif/ledTwo/green");
        three = new MotifLed(opMode, "motif/ledThree/red", "motif/ledThree/green");

        // reset();
    }

    public void reset() {
        one.setColor(MotifLed.Color.YELLOW);
        two.setColor(MotifLed.Color.YELLOW);
        three.setColor(MotifLed.Color.YELLOW);
    }

    public void displayMotif(int id) {
        one.setColor(id == 0 ? MotifLed.Color.GREEN : MotifLed.Color.RED);
        two.setColor(id == 1 ? MotifLed.Color.GREEN : MotifLed.Color.RED);
        three.setColor(id == 2 ? MotifLed.Color.GREEN : MotifLed.Color.RED);
    }
}

class MotifLed {
    private final DigitalChannel red;
    private final DigitalChannel green;

    public MotifLed(LinearOpMode opMode, String redHardwareMapId, String greenHardwareMapId) {
        red = opMode.hardwareMap.get(DigitalChannel.class, redHardwareMapId);
        green = opMode.hardwareMap.get(DigitalChannel.class, greenHardwareMapId);

        red.setMode(DigitalChannel.Mode.OUTPUT);
        green.setMode(DigitalChannel.Mode.OUTPUT);

        red.setState(true);
        green.setState(true);
    }

    public void setColor(Color c) {
        // if (red == null || green == null) return;
        red.setState(c.red);
        green.setState(c.green);
    }

    enum Color {
        RED(true, false),
        GREEN(false, true),
        YELLOW(false, false);

        private final boolean red;
        private final boolean green;

        Color(boolean r, boolean g) {
            red = r;
            green = g;
        }
    }
}