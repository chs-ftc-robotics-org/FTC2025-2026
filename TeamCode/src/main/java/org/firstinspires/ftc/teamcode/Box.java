package org.firstinspires.ftc.teamcode;

public class Box<T> {
    private T t;

    public Box(T t) {
        this.t = t;
    }

    public T get() {
        return t;
    }

    public void set(T t) {
        this.t = t;
    }
}
