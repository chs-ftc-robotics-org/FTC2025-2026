//package org.firstinspires.ftc.teamcode;
//
//import com.qualcomm.robotcore.eventloop.opmode.OpMode;
//import com.qualcomm.robotcore.hardware.Gamepad;
//
//public class GamepadTracker {
//    public static enum Button {
//        X, Y, A, B
//    }
//
//    public static class Proxy {
//        private final Gamepad inner;
//        private boolean a;
//        private boolean b;
//        private boolean x;
//        private boolean y;
//
//        public Proxy(Gamepad inner) {
//            this.inner = inner;
//        }
//
//        public boolean pressed(Button button) {
//            switch (button) {
//                case A:
//                    return inner.a && !this.a;
//                case B:
//                    return inner.b && !this.b;
//                case X:
//                    return inner.x && !this.x;
//                case Y:
//                    return inner.y && !this.y;
//            }
//
//            return false;
//        }
//
//        public void sync() {
//            this.a = inner.a;
//            this.b = inner.b;
//            this.x = inner.x;
//            this.y = inner.y;
//        }
//    }
//
//    public final Proxy gamepad1;
//    public final Proxy gamepad2;
//
//    public GamepadTracker(OpMode opMode) {
//        gamepad1 = new Proxy(opMode.gamepad1);
//        gamepad2 = new Proxy(opMode.gamepad2);
//    }
//
//    public void update() {
//        gamepad1.sync();
//        gamepad2.sync();
//    }
//}
