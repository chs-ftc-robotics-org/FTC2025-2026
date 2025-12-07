package org.firstinspires.ftc.teamcode;

public class Box<T> {
    private T t;

    private Box(T t) {
        this.t = t;
    }

    public T get() {
        return t;
    }

    public void set(T t) {
        this.t = t;
    }

    public static <T> Box<T> of(T t) {
        return new Box<>(t);
    }
}
